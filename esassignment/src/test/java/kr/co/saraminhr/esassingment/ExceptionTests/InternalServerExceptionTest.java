package kr.co.saraminhr.esassingment.ExceptionsTests;

import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Exceptions.InternalServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InternalServerExceptionTest {
    String message = "internal server exception";
    Throwable cause = new Throwable(message);

    @Test
    @DisplayName("message construct test")
    void messageConstructTest(){
        //when
        InternalServerException exception = new InternalServerException(message);

        //then
        assertEquals(message,exception.getMessage());
    }

    @Test
    @DisplayName("message & throwable construct test")
    void messageAndThrowalbeConstructTest(){
        //when
        InternalServerException exception = new InternalServerException(message,cause);

        //then
        assertEquals(message,exception.getMessage());
        assertEquals(message,exception.getCause().getMessage());
    }
}
