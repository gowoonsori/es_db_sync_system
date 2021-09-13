package kr.co.saraminhr.esassingment.UtilTests;

import kr.co.saraminhr.esassingment.Dtos.Highlight;
import kr.co.saraminhr.esassingment.Utils.ApiError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public class ApiErrorTest {
    @Test
    @DisplayName("ApiError toString 테스트")
    public void toStringTest(){
        //given
        final String message = "error";
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        //when
        ApiError request = new ApiError(message,status);

        //then
        Assertions.assertEquals("{\"message\":\"" + message
                        +"\",\"status\":" + status.value()
                        +"}"
                ,request.toString());
    }
}
