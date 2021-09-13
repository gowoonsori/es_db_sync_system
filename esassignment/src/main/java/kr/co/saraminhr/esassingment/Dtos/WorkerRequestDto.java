package kr.co.saraminhr.esassingment.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.regex.Matcher;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class WorkerRequestDto {
    @NotBlank(message = "공백은 입력이 불가능합니다.",groups = NotBlank.class)
    @Size(min=1, max=30, message = "서비스 id는 1~30자 사이로 입력해주세요.",groups = Size.class)
    @JsonProperty("service_id")
    private String uuid;

    @NotBlank(message = "공백은 입력이 불가능합니다.", groups = NotBlank.class)
    @Size(min=15, max=255, message = "잘못된 형식입니다.", groups = Size.class)
    @Pattern(regexp = "(select )[0-9_a-zA-Z,* ]* from [0-9_a-zA-Z]+((?!order).)*", message = "잘못된 형식입니다.", groups = Pattern.class)
    @JsonProperty("sql_string")
    private String sqlString;

    private static java.util.regex.Pattern columnPattern = java.util.regex.Pattern.compile("(select )(?<columns>[0-9_a-zA-Z, *]*) from *");

    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("service_id", uuid)
                .append("sql_string", sqlString)
                .build();
    }

    public boolean isExistPk(String pkName){
        Matcher matcher = columnPattern.matcher(this.sqlString); matcher.find();
        String columns = matcher.group("columns");
        if("*".equals(columns.trim())) return true;

        String[] columnList = columns.split(",");
        for(String column : columnList){
            if(column.trim().equals(pkName) || "*".equals(column.trim())) return true;
        }
        return false;
    }
}
