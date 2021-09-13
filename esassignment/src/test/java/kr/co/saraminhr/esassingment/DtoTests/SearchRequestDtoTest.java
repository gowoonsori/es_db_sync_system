package kr.co.saraminhr.esassingment.DtoTests;

import kr.co.saraminhr.esassingment.Dtos.Highlight;
import kr.co.saraminhr.esassingment.Dtos.SearchRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SearchRequestDtoTest {
    @Test
    @DisplayName("SearchRequestDto toString 테스트")
    public void toStringTest(){
        //given
        String serviceId = "test";
        String searchText = "test";
        String[] resultColumns = new String[]{};
        String prefixTag = "<br>";
        String postfixTag = "</br>";
        Highlight highlight = new Highlight(new Highlight.Column[]{},prefixTag,postfixTag);
        Integer pageSize = 10;
        Integer pageNo = 0;


        //when
        SearchRequestDto request = new SearchRequestDto(serviceId,searchText,resultColumns,highlight,pageSize,pageNo);

        //then
        Assertions.assertEquals("{\"service_id\":\"" + serviceId
                        +"\",\"search_text\":\"" + searchText
                        +"\",\"result_columns\":" + Arrays.toString(resultColumns)
                        +",\"highlight\":" + highlight
                        +",\"page_size\":" + pageSize
                        +",\"page_no\":" + pageNo
                        +"}"
                ,request.toString());
    }
}
