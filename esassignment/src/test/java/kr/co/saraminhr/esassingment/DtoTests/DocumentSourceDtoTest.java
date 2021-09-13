package kr.co.saraminhr.esassingment.DtoTests;

import kr.co.saraminhr.esassingment.Dtos.DocumentSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentSourceDtoTest {
    @Test
    @DisplayName("DocumentSource toString 테스트 ")
    public void toStringTest(){
        //given
        Map<String,String> data = Collections.singletonMap("id","1");
        Map<String,String> highlight = Collections.singletonMap("id","1");


        //when
        DocumentSource documentSource = new DocumentSource(data,highlight);

        //then
        StringBuilder dataSb = new StringBuilder();
        dataSb.append("{");
        for(Map.Entry<String,String> entry : data.entrySet()){
            dataSb.append("\"").append(entry.getKey()).append("\"").append(":")
                    .append("\"").append(entry.getValue()).append("\"");
        }
        dataSb.append("}");
        StringBuilder highlightSb = new StringBuilder();
        highlightSb.append("{");
        for(Map.Entry<String,String> entry : data.entrySet()){
            highlightSb.append("\"").append(entry.getKey()).append("\"").append(":")
                    .append("\"").append(entry.getValue()).append("\"");
        }
        highlightSb.append("}");
        assertEquals("{\"data\":" + dataSb
                        +",\"highlight\":" + highlightSb
                        +"}"
                ,documentSource.toString());
    }

    @Test
    @DisplayName("DocumentSource toString 테스트 : highlight null")
    public void toStringNullTest(){
        //given
        Map<String,String> data = Collections.singletonMap("id","1");
        Map<String,String> highlight = null;


        //when
        DocumentSource documentSource = new DocumentSource(data,highlight);

        //then
        StringBuilder dataSb = new StringBuilder();
        dataSb.append("{");
        for(Map.Entry<String,String> entry : data.entrySet()){
            dataSb.append("\"").append(entry.getKey()).append("\"").append(":")
                    .append("\"").append(entry.getValue()).append("\"");
        }
        dataSb.append("}");
        assertEquals("{\"data\":" + dataSb
                        +"}"
                ,documentSource.toString());
    }
}
