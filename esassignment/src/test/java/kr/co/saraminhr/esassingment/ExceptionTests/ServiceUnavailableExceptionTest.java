package kr.co.saraminhr.esassingment.ExceptionsTests;

import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Exceptions.ServiceUnavailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceUnavailableExceptionTest {
    String message = "service unavailable exception";
    Throwable cause = new Throwable(message);

    @Test
    @DisplayName("message construct test")
    void messageConstructTest(){
        //when
        ServiceUnavailableException exception = new ServiceUnavailableException(message);

        //then
        assertEquals(message,exception.getMessage());
    }

    @Test
    @DisplayName("message & throwable construct test")
    void messageAndThrowalbeConstructTest(){
        //when
        ServiceUnavailableException exception = new ServiceUnavailableException(message,cause);

        //then
        assertEquals(message,exception.getMessage());
        assertEquals(message,exception.getCause().getMessage());
    }
}
