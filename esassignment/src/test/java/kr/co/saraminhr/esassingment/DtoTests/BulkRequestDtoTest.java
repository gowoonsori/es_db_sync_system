package kr.co.saraminhr.esassingment.DtoTests;

import kr.co.saraminhr.esassingment.Dtos.BulkRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class BulkRequestDtoTest {
    @Test
    @DisplayName("BulkRequestDto toString 테스트")
    public void toStringTest() {
        //given
        String serviceId = "test";
        String indexName = "test";


        //when
        BulkRequestDto request = new BulkRequestDto(serviceId, indexName);

        //then
        Assertions.assertEquals("{\"service_id\":\"" + serviceId
                        + "\",\"index_name\":\"" + indexName
                        + "\"}"
                , request.toString());
    }
}
