package kr.co.saraminhr.esassingment.DomainTests;

import kr.co.saraminhr.esassingment.Domains.Index;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class IndexTest {
    @Test
    @DisplayName("Index 모델 toString 테스트")
    public void indexToStringTest(){
        //given
        String uuid = "123";
        String status = "open";
        String indexName = "example";
        int count = 12345;
        String health = "yellow";
        int primary = 1;
        int replica = 1;
        int deleted = 1;
        String size = "123.kb";
        String primarySize = "123.kb";

        //when
        Index index = new Index(uuid,status,indexName,count,health,primary,replica,deleted,size,primarySize);

        //then
        Assertions.assertEquals("{\"uuid\":\"" +  uuid
                        +"\",\"status\":\"" + status
                        +"\",\"index\":\""+indexName
                        +"\",\"count\":"+ count
                        +",\"health\":\""+ health
                        +"\",\"primary\":"+ primary
                        +",\"replica\":"+ replica
                        +",\"deleted\":"+ deleted
                        +",\"size\":\""+ size
                        +"\",\"primary_size\":\""+ primarySize
                        +"\"}"
                ,index.toString());
    }

    @Test
    @DisplayName("Index 정적 팩토리메서드 테스트")
    public void indexStaticFactoryMethod(){
        //given
        String uuid = "123";
        String status = "open";
        String indexName = "example";
        int count = 12345;
        String health = "yellow";
        int primary = 1;
        int replica = 1;
        int deleted = 1;
        String size = "123.kb";
        String primarySize = "123.kb";
        Index compareIndex = new Index(uuid,status,indexName,count,health,primary,replica,deleted,size,primarySize);

        //when
        Map<String,String> indexMap = new HashMap<>();
        indexMap.put("uuid",uuid);
        indexMap.put("status",status);
        indexMap.put("index",indexName);
        indexMap.put("docs.count",String.valueOf(count));
        indexMap.put("health",health);
        indexMap.put("pri",String.valueOf(primary));
        indexMap.put("rep",String.valueOf(replica));
        indexMap.put("docs.deleted",String.valueOf(deleted));
        indexMap.put("store.size",size);
        indexMap.put("pri.store.size",primarySize);
        Index index = Index.newInstance(indexMap);

        //then
        Assertions.assertEquals(index,compareIndex);
    }
}
