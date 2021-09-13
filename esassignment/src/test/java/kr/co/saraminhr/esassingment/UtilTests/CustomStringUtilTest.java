package kr.co.saraminhr.esassingment.UtilTests;

import kr.co.saraminhr.esassingment.Utils.CustomStringUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomStringUtilTest {
    @Test
    @DisplayName("no args construct test")
    public void constructTest(){
        //when
        CustomStringUtil customStringUtil = new CustomStringUtil();

        //then
        assertEquals(CustomStringUtil.class, customStringUtil.getClass());
    }

    @Test
    @DisplayName("is empty test")
    public void isEmptyTest(){
        //given
        String text1 = null;
        String text2 ="";

        //when & then
        assertTrue( CustomStringUtil.isEmptyAndBlank(text1));
        assertTrue( CustomStringUtil.isEmptyAndBlank(text2));
        assertFalse( CustomStringUtil.isNotEmptyAndBlank(text1));
        assertFalse( CustomStringUtil.isNotEmptyAndBlank(text2));
    }

    @Test
    @DisplayName("is blank test")
    public void isBlankTest(){
        //given
        String text = "    ";

        //when & then
        assertTrue( CustomStringUtil.isEmptyAndBlank(text));
        assertFalse( CustomStringUtil.isNotEmptyAndBlank(text));
    }

    @Test
    @DisplayName("is not blank & Emptytest")
    public void isNotEmptyBlankTest(){
        //given
        String text = "a ";

        //when & then
        assertTrue( CustomStringUtil.isNotEmptyAndBlank(text));
    }
}
