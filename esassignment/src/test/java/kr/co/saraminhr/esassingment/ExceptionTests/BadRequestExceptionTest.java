package kr.co.saraminhr.esassingment.ExceptionsTests;

import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BadRequestExceptionTest {
    String message = "Bad request";
    Throwable cause = new Throwable(message);

    @Test
    @DisplayName("message construct test")
    void messageConstructTest(){
        //when
        BadRequestException exception = new BadRequestException(message);

        //then
        assertEquals(message,exception.getMessage());
    }

    @Test
    @DisplayName("message & throwable construct test")
    void messageAndThrowalbeConstructTest(){
        //when
        BadRequestException exception = new BadRequestException(message,cause);

        //then
        assertEquals(message,exception.getMessage());
        assertEquals(message,exception.getCause().getMessage());
    }
}
