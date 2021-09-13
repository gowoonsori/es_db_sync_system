package kr.co.saraminhr.esassingment.Exceptions;

public class DuplicateServiceIdException extends RuntimeException{
    public DuplicateServiceIdException(String message) {
        super(message);
    }

    public DuplicateServiceIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
