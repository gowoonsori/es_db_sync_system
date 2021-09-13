package kr.co.saraminhr.esassingment.DtoTests;

import kr.co.saraminhr.esassingment.Dtos.DocumentSource;
import kr.co.saraminhr.esassingment.Dtos.SearchResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SearchResponseDtoTest {
    @Test
    @DisplayName("SearchResponseDto toString 테스트")
    public void toStringTest(){
        //given
        Long total = 1000L;
        Integer pageSize = 10;
        Integer page = 0;
        DocumentSource[] result = new DocumentSource[]{};


        //when
        SearchResponseDto highlight = new SearchResponseDto(total,pageSize,page,result);

        //then
        Assertions.assertEquals("{\"result\":" + Arrays.toString(result)
                        +",\"total\":" + total
                        +",\"page_size\":" + pageSize
                        +",\"page\":" + page
                        +"}"
                ,highlight.toString());
    }
    @Test
    @DisplayName("SearchResponseDto toString 테스트 : page null")
    public void toStringPageNullTest(){
        //given
        Long total = 1000L;
        Integer pageSize = 10;
        Integer page = null;
        DocumentSource[] result = new DocumentSource[]{};


        //when
        SearchResponseDto highlight = new SearchResponseDto(total,pageSize,page,result);

        //then
        Assertions.assertEquals("{\"result\":" + Arrays.toString(result)
                        +",\"total\":" + total
                        +",\"page_size\":" + pageSize
                        +"}"
                ,highlight.toString());
    }
    @Test
    @DisplayName("SearchResponseDto toString 테스트 : page size null")
    public void toStringPageSizeNullTest(){
        //given
        Long total = 1000L;
        Integer pageSize = null;
        Integer page = 0;
        DocumentSource[] result = new DocumentSource[]{};


        //when
        SearchResponseDto highlight = new SearchResponseDto(total,pageSize,page,result);

        //then
        Assertions.assertEquals("{\"result\":" + Arrays.toString(result)
                        +",\"total\":" + total
                        +",\"page\":" + page
                        +"}"
                ,highlight.toString());
    }
}
