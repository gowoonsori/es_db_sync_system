package kr.co.saraminhr.esassingment.ExceptionsTests;

import kr.co.saraminhr.esassingment.Exceptions.ErrorLog;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorLogTest {
    @Test
    @DisplayName("no args construct test")
    void noArgsConstructTest(){
        //when
        ErrorLog errorLog = new ErrorLog();

        //then
        assertEquals(ErrorLog.class, errorLog.getClass());
    }

}
