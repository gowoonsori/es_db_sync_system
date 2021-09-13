package kr.co.saraminhr.esassingment.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class IndexingDto {
    @NotEmpty(message = "공백은 입력이 불가능합니다.",groups = NotBlank.class)
    @JsonProperty("service_ids")
    private String[] serviceIds;

    @NotBlank(message = "공백은 입력이 불가능합니다.",groups = NotBlank.class)
    @Size(min=1, max=255, message = "서비스 id는 1~255자 사이로 입력해주세요.",groups = Size.class)
    @JsonProperty("contents_id_value")
    private String contentsIdValue;


    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("service_ids", serviceIds)
                .append("contents_id_value", contentsIdValue)
                .build();
    }
}
