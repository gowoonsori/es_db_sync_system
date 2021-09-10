package com.es.es_sync.Consumer;

import com.es.es_sync.Domains.Worker;
import com.es.es_sync.Services.IndexService;
import com.es.es_sync.Services.WorkerService;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MyKafkaConsumer {
    private Map<String,Object> data = new HashMap<>();
    private final Gson gson = new Gson();
    private final WorkerService workerSevice;
    private final IndexService indexService;

    public MyKafkaConsumer(WorkerService workerSevice,IndexService indexService) {
        this.workerSevice = workerSevice;
        this.indexService = indexService;
    }

    @KafkaListener(topics="es-sync.es_assignment.coffee", groupId = "es_assignment")
    public void consume(@Payload String message) throws IOException{
        log.info(String.format("message arrive : %s",message));

        //message parsing
        data = (Map<String,Object>) gson.fromJson(message,data.getClass());
        Map<String,Object> payload = (LinkedTreeMap)data.get("payload");
        Map<String,Object> source = (LinkedTreeMap)payload.get("source");
        Map<String,Object> before = (LinkedTreeMap)payload.get("before");
        Map<String,Object> after = (LinkedTreeMap)payload.get("after");

        //get op, table
        String operator = (String) payload.get("op");
        String tableName = (String) source.get("table");

        //get services
        List<Worker> workerList = workerSevice.getWorkers(tableName);
        if(workerList.isEmpty()) {
            log.info("message consume! => services is empty");
            return;
        }

        //get doc id
        String pkName = workerList.get(0).getPkName();
        String id;
        Map<String,Object> document;
        //update&create 는 after의 데이터, delete는 before데이터 이용
        if("u".equals(operator) || "c".equals(operator)){
            document = after;
        }else if("d".equals(operator)){
            document = before;
        }else{
            log.info("message consume! => binlog op is not u,d,c");
            return;
        }
        if(document.get(pkName) instanceof Double) id = String.valueOf(((Double) document.get(pkName)).longValue());
        else id = String.valueOf(document.get(pkName));

        // u / d / c 에 따라 다르게 처리
        switch (operator){
            case "u" :
                indexService.updateIndex(workerList,id);
                break;
            case "d" :
                indexService.deleteIndex(workerList,id);
                break;
            case "c" :
                indexService.createIndex(workerList,id);
                break;
            default:
                break;
        }
        log.info("message consume!");
    }
}
