package kr.co.saraminhr.esassingment.DomainTests;

import kr.co.saraminhr.esassingment.Domains.Worker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class WorkerTest {
    @Test
    @DisplayName("Worker 모델 toString 테스트")
    public void toStringTest(){
        //given
        Integer id = null;
        String uuid = "test-service";
        String sqlString = "select * from members";
        String tableName = "members";
        String pkName ="mem_idx";
        String indexName ="test";
        LocalDateTime createdAt =null;
        LocalDateTime updatedAt =null;

        //when
        Worker index = new Worker(id,uuid,sqlString,tableName,pkName,indexName,createdAt,updatedAt);

        //then
        Assertions.assertEquals("{\"id\":" +  id
                        +",\"uuid\":\"" + uuid
                        +"\",\"sql_string\":\"" + sqlString
                        +"\",\"table_name\":\""+tableName
                        +"\",\"pk_name\":\""+ pkName
                        +"\",\"index_name\":\""+ indexName
                        +"\",\"created_at\":"+ createdAt
                        +",\"updated_at\":"+ updatedAt
                        +"}"
                ,index.toString());
    }

}
