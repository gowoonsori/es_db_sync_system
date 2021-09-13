package kr.co.saraminhr.esassingment.ExceptionsTests;

import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Exceptions.DuplicateServiceIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DuplicateServiceIdExceptionTest {
    String message = "DuplicateService id exception";
    Throwable cause = new Throwable(message);

    @Test
    @DisplayName("message construct test")
    void messageConstructTest(){
        //when
        DuplicateServiceIdException exception = new DuplicateServiceIdException(message);

        //then
        assertEquals(message,exception.getMessage());
    }

    @Test
    @DisplayName("message & throwable construct test")
    void messageAndThrowalbeConstructTest(){
        //when
        DuplicateServiceIdException exception = new DuplicateServiceIdException(message,cause);

        //then
        assertEquals(message,exception.getMessage());
        assertEquals(message,exception.getCause().getMessage());
    }
}
