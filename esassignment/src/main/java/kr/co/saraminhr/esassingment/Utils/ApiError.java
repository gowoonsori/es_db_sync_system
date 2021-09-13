package kr.co.saraminhr.esassingment.Utils;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

/**
 * Error (Exception) 발생 시 응답 형태를 통일하기 위한 클래스
 *  message : String, Map, Object와 같은 데이터
 *  status : 상태 코드
 * */
@Getter
public class ApiError<T> {
    private final T message;
    private final int status;

    public ApiError(T message, HttpStatus status){
        this.message = message;
        this.status = status.value();
    }

    public static Map<String,String> validateRequest(Errors errors){
        Map<String,String> validateResult = new HashMap<>();

        for(FieldError error : errors.getFieldErrors()){
            String validKey = String.format("valid_%s", error.getField());
            validateResult.put(validKey,error.getDefaultMessage());
        }
        return validateResult;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("message",message)
                .append("status",status)
                .toString();
    }
}
