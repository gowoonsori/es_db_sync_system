package kr.co.saraminhr.esassingment.DtoTests;

import kr.co.saraminhr.esassingment.Dtos.IndexingDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class IndexingDtoTest {
    @Test
    @DisplayName("IndexingDto toString 테스트")
    public void toStringTest(){
        //given
        String[] serviceIds = new String[]{"a1","a2"};
        String contentsIdValue = "a1";


        //when
        IndexingDto index = new IndexingDto(serviceIds,contentsIdValue);

        //then
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(String id : serviceIds){
            sb.append("\"").append(id).append("\"");
            if(!id.equals(serviceIds[serviceIds.length-1]))sb.append(",");
        }
        sb.append("]");

        Assertions.assertEquals("{\"service_ids\":" + sb.toString()
                        +",\"contents_id_value\":\"" + contentsIdValue
                        +"\"}"
                ,index.toString());
    }
}
