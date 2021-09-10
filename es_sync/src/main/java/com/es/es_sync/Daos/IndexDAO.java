package com.es.es_sync.Daos;


import com.es.es_sync.Domains.Worker;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class IndexDAO {
    private final RestHighLevelClient client;


    /**
     *  ES에서 index 이름과 doc id를 가지고 조회할 multi-get 메서드
     *      한 doc_id가 존재하는 index를 찾기 위해 사용
     *
     * @param data index이름이 key, 검색할 doc id이 value로 갖는 map
     * @return MultiGetResponse
     * @throws IOException
     */
    public MultiGetResponse multiGet(Map<String, Worker> data, String docId) throws IOException {
        MultiGetRequest request = new MultiGetRequest();

        for(String indexName : data.keySet()){
            request.add(new MultiGetRequest.Item(
                    indexName, docId
            ));
        }

        return client.mget(request, RequestOptions.DEFAULT);
    }
}
