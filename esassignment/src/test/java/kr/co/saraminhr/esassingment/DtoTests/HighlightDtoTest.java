package kr.co.saraminhr.esassingment.DtoTests;

import kr.co.saraminhr.esassingment.Dtos.Highlight;
import kr.co.saraminhr.esassingment.Dtos.Highlight;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class HighlightDtoTest {
    @Test
    @DisplayName("Highlight toString 테스트")
    public void toStringTest(){
        //given
        String prefixTag = "<br>";
        String postfixTag = "<br>";
        Highlight.Column[] columns = new Highlight.Column[]{};

        //when
        Highlight highlight = new Highlight(columns,prefixTag,postfixTag);

        //then
        Assertions.assertEquals("{\"columns\":" + Arrays.toString(columns)
                        +",\"prefix_tag\":\"" + prefixTag
                        +"\",\"postfix_tag\":\"" + postfixTag
                        +"\"}"
                ,highlight.toString());
    }

    @Test
    @DisplayName("Column toString 테스트")
    public void columnToStringTest(){
        //when
        Highlight.Column column = new Highlight.Column();

        //then
        Assertions.assertEquals( "{\"column\":null}"
                ,column.toString());
    }

}
