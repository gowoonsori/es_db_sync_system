package kr.co.saraminhr.esassingment.Exceptions;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import kr.co.saraminhr.esassingment.Utils.ApiError;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
    private ResponseEntity<ApiError> errorResponse(Throwable throwable, HttpStatus httpStatus){
        return errorResponse(throwable.getMessage(), httpStatus);
    }
    private ResponseEntity<ApiError> errorResponse(String message, HttpStatus status){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity<>(new ApiError(message,status), headers, status);
    }

    /**
     * Bad Request
     * */
    @ExceptionHandler({BadRequestException.class,
            HttpMessageNotReadableException.class,
            ClassCastException.class,
            DataIntegrityViolationException.class,
            NullPointerException.class,
            InvalidDefinitionException.class})
    public ResponseEntity<?> handleBadRequestException(Exception e){
        log.info(ErrorLog.createLogMessage("BadRequestException", HttpStatus.NOT_FOUND.value(), e.getMessage()));
        if(e instanceof HttpMessageNotReadableException || e instanceof ClassCastException || e instanceof DataIntegrityViolationException
                || e instanceof NullPointerException || e instanceof InvalidDefinitionException){
            return errorResponse(ErrorMessage.BAD_REQUEST.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return errorResponse(e, HttpStatus.BAD_REQUEST);
    }

    /**
     * Not Found
     * */
    @ExceptionHandler({NotFoundException.class, ChangeSetPersister.NotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(Exception e){
        log.info(ErrorLog.createLogMessage("NotFoundException", HttpStatus.NOT_FOUND.value(), e.getMessage()));
        return errorResponse(e, HttpStatus.NOT_FOUND);
    }

    /**
     * Internal Server error
     * */
    @ExceptionHandler({InternalServerException.class})
    public ResponseEntity<?> handleInternalServerException(Exception e){
        log.info(ErrorLog.createLogMessage("InternalServerException", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        return errorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Service unavailable error
     * */
    @ExceptionHandler({ServiceUnavailableException.class})
    public ResponseEntity<?> handleServiceUnavailableException(Exception e){
        log.info(ErrorLog.createLogMessage("ServiceUnavailableException", HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage()));
        return errorResponse(e, HttpStatus.SERVICE_UNAVAILABLE);
    }


    /**
     * Service 생성 중 service id 가 중복 되는 경우
     * */
    @ExceptionHandler({DuplicateServiceIdException.class})
    public ResponseEntity<?> createServiceBadRequest(Exception e) {
        log.info(ErrorLog.createLogMessage("DuplicateServcieException", HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        return ResponseEntity.badRequest().body(new ApiError<>(Map.of("valid_uuid",e.getMessage()),HttpStatus.BAD_REQUEST));
    }
}
