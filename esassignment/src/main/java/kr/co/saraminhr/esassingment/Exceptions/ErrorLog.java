package kr.co.saraminhr.esassingment.Exceptions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * Error Loging 형태롤 토일하기 위한 Class
 * */
public class ErrorLog {
    public static String createLogMessage(String className, int status, String message){
        return new ToStringBuilder(ToStringStyle.JSON_STYLE)
                .append("Exception", className)
                .append("status", status)
                .append("message", message)
                .build();
    }
}
