package com.es.es_sync.Services;

import com.es.es_sync.Daos.WorkerDAO;
import com.es.es_sync.Domains.Worker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerService {
    private final WorkerDAO workerDAO;

    /**
     *  테이블 이름으로 서비스들 조회
     *
     * @param tableName 검색할 테이블 이름
     * */
    public List<Worker> getWorkers(String tableName){
        return workerDAO.findByTableName(tableName);
    }
}
