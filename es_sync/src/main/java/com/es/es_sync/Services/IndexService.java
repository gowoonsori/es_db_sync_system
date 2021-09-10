package com.es.es_sync.Services;

import com.es.es_sync.Daos.CoffeeDAO;
import com.es.es_sync.Daos.IndexDAO;
import com.es.es_sync.Domains.Worker;
import com.es.es_sync.Dto.IndexingRequestDto;
import com.es.es_sync.Utils.CustomStringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexService {
    private static final String INDEXING_URL = "http://localhost:8080/index/search/indexing";
    private final IndexDAO indexDAO;
    private final CoffeeDAO coffeeDAO;
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;

    /**
     * create indexing 메서드
     *
     * @param workerList tableName 이 같은 서비스들
     * @param docId 이벤트가 발생한 문서 id(table pk)
     * @throws IOException kafka / RestHighClient 가 발생
     * */
    public void createIndex(List<Worker> workerList, String docId) throws IOException {
        //index 가 존재하는 서비스들 찾기
        List<Worker> indices = getWorkersByExistIndex(workerList);
        List<String> serviceIds = new ArrayList<>();


        //sql문 실행해서 데이터 get
        for(Worker worker : indices){
            List<Map<String, Object>> data = coffeeDAO.findBySqlOrderByPkAsc(worker.getSqlString(),worker.getPkName());
            if(data.isEmpty()) continue;

            //sql 조건에 포함되는 데이터인지 확인
            if(isExistId(data,worker.getPkName(),docId)){
                serviceIds.add(worker.getUuid());
            }
        }

        //비어있다면 종료
        if(serviceIds.isEmpty())return;

        //create indexing 요청
        //service ids 와 docId request
        IndexingRequestDto requestDto = new IndexingRequestDto(serviceIds.toArray(new String[serviceIds.size()]),docId);
        Object response = restTemplate.exchange(INDEXING_URL, HttpMethod.POST,new HttpEntity<>(requestDto,headers),Object.class);
        log.info(response.toString());
    }

    /**
     * update indexing 메서드
     *
     * @param workerList tableName 이 같은 서비스들
     * @param docId 이벤트가 발생한 문서 id(table pk)
     * @throws IOException kafka / RestHighClient 가 발생
     * */
    public void updateIndex(List<Worker> workerList, String docId) throws IOException {
        //index 가 존재하는 서비스들 찾기
        Map<String,Worker> indices = getMapByExistIndex(workerList);

        //workerList 의 index들 에서 id가 존재하는지 확인
        //exist를 list size만큼 호출하는 것보다 multi-get으로 한번에 조회하고 판별하는 방법으로 구현
        MultiGetItemResponse[] existIndices = indexDAO.multiGet(indices,docId).getResponses();

        //존재하는 service id get
        List<String> serviceIds = getServiceIds(indices,existIndices);
        //해당되는 service 가 없다면 종료
        if(serviceIds.isEmpty()) return;

        //service ids 와 docId request
        IndexingRequestDto requestDto = new IndexingRequestDto(serviceIds.toArray(new String[serviceIds.size()]),docId);
        Object response = restTemplate.exchange(INDEXING_URL, HttpMethod.PUT,new HttpEntity<>(requestDto,headers),Object.class);
        log.info(response.toString());
    }


    /**
     * delete indexing 메서드
     *
     * @param workerList tableName 이 같은 서비스들
     * @param docId 이벤트가 발생한 문서 id(table pk)
     * @throws IOException kafka / RestHighClient 가 발생
     * */
    public void deleteIndex(List<Worker> workerList,String docId) throws IOException {
        //index 가 존재하는 서비스들 찾아 index name get
        Map<String,Worker> indices = getMapByExistIndex(workerList);

        //workerList 의 index들 에서 id가 존재하는지 확인
        //exist를 list size만큼 호출하는 것보다 multi-get으로 한번에 조회하고 판별하는 방법으로 구현
        MultiGetItemResponse[] existIndices = indexDAO.multiGet(indices,docId).getResponses();

        //존재하는 service id get
        List<String> serviceIds = getServiceIds(indices,existIndices);
        //해당되는 service 가 없다면 종료
        if(serviceIds.isEmpty()) return;

        //service ids 와 docId request
        IndexingRequestDto requestDto = new IndexingRequestDto(serviceIds.toArray(new String[serviceIds.size()]),docId);
        log.info("request => " + requestDto);
        Object response = restTemplate.exchange(INDEXING_URL, HttpMethod.DELETE,new HttpEntity<>(requestDto,headers),Object.class);
        log.info(response.toString());
    }

    /**
     *  table이름이 같은 서비스들에서 index를 가지고 있는 서비스들을 골라 indexName을 key로 갖는 Map의 형태로 서비스들 반환
     *
     * @param workerList tableName이 같은 서비스들
     * */
    private Map<String,Worker> getMapByExistIndex(List<Worker> workerList){
        Map<String,Worker> indices = new HashMap<>();
        for(Worker worker : workerList){
            //index name이 있는지 확인
            if(CustomStringUtil.isNotEmptyAndBlank(worker.getIndexName())){
                indices.put(worker.getIndexName(),worker);
            }
        }
        return indices;
    }

    /**
     *  table이름이 같은 서비스들에서 index를 가지고 있는 서비스들을 골라 indexName들을 List형태로 반환하는 메서드
     *
     * @param workerList tableName이 같은 서비스들
     * */
    private List<Worker> getWorkersByExistIndex(List<Worker> workerList){
        List<Worker> indices = new ArrayList<>();
        for(Worker worker : workerList){
            if(CustomStringUtil.isNotEmptyAndBlank(worker.getIndexName())){
                indices.add(worker);
            }
        }
        return indices;
    }

    /**
     *  ES의 Multi-Get의 응답에서 index Name을 key로 하고 해당 indexName을 가지고 있는 서비스를 value로 하는 Map을 반환
     *
     * @param indices index 들이 존재하는 서비스들
     * @param existIndices multi-get을 통해 검색결과가 존재하는 index목록
     * */
    private List<String> getServiceIds(Map<String,Worker> indices,MultiGetItemResponse[] existIndices){
        List<String> serviceIds = new ArrayList<>();
        for(MultiGetItemResponse item : existIndices){
            if(item.getResponse().isExists()) serviceIds.add(indices.get(item.getIndex()).getUuid());
        }
        return serviceIds;
    }

    /**
     *  서비스의 sql 문을 실행한 데이터 중 id가 같은 데이터가 있는지 판별하는 메서드
     *      - pk가 integer/long 타입이면 이분탐색
     *      - pk가 String 타입이면 순차탐색
     *
     * @param data sql 문 실행한 데이터
     * @param pkName table의 pk 이름
     * @param id 검색할 key(pk)
     * */
    private boolean isExistId(List<Map<String, Object>> data,String pkName, String id){
        if(data.get(0).get(pkName) instanceof Integer || data.get(0).get(pkName) instanceof Long){
            return binarySearchByPkTypeNumber(data,pkName,Long.parseLong(id));
        }else{
            return binarySearchByPkTypeString(data,pkName,id);
        }
    }

    /**
     *  검색할 pk가 숫자타입이면 이분탐색으로 존재하는지 찾는 메서드
     *
     * @param dataList sql 문 실행한 데이터
     * @param pkName table의 pk 이름
     * @param id 검색할 key(pk)
     * */
    private boolean binarySearchByPkTypeNumber(List<Map<String, Object>> dataList,String pkName, long id){
        int first=0, last = dataList.size()-1;
        int mid;
        long midValue;

        while(first <= last){
            mid = (first + last) / 2;
            if(dataList.get(mid).get(pkName) instanceof Integer) midValue = (Integer) dataList.get(mid).get(pkName);
            else midValue = (Long) dataList.get(mid).get(pkName);

            if(id == midValue){
                return true;
            }else if(id < midValue){
                last = mid -1;
            }else {
                first = mid + 1;
            }
        }
        return false;
    }

    /**
     *  검색할 pk가 문자열이라면 순차탐색으로 key가 존재하는지 검색
     *
     * @param dataList sql 문 실행한 데이터
     * @param pkName table의 pk 이름
     * @param id 검색할 key(pk)
     * */
    private boolean binarySearchByPkTypeString(List<Map<String, Object>> dataList,String pkName, String id){
        for(Map<String,Object> data : dataList){
            if(data.get(pkName).equals(id)) return true;
        }
        return false;
    }
}
