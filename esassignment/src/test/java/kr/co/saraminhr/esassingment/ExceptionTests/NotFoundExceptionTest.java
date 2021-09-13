package kr.co.saraminhr.esassingment.ExceptionsTests;

import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotFoundExceptionTest {
    String message = "not found";
    Throwable cause = new Throwable(message);

    @Test
    @DisplayName("message construct test")
    void messageConstructTest(){
        //when
        NotFoundException exception = new NotFoundException(message);

        //then
        assertEquals(message,exception.getMessage());
    }

    @Test
    @DisplayName("message & throwable construct test")
    void messageAndThrowalbeConstructTest(){
        //when
        NotFoundException exception = new NotFoundException(message,cause);

        //then
        assertEquals(message,exception.getMessage());
        assertEquals(message,exception.getCause().getMessage());
    }
}
