package kr.co.saraminhr.esassingment.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Builder
@Setter @NoArgsConstructor
@AllArgsConstructor @EqualsAndHashCode
public class BulkRequestDto {
    @NotBlank(message = "공백은 입력이 불가능합니다.",groups = NotBlank.class)
    @Size(min=1, max=30, message = "서비스 id는 1~30자 사이로 입력해주세요.",groups = Size.class)
    @JsonProperty("service_id")
    private String serviceId;

    @NotBlank(message = "공백은 입력이 불가능합니다.",groups = NotBlank.class)
    @Size(min=1, max=30, message = "인덱스 명은 1~30자 사이로 입력해주세요.",groups = Size.class)
    @JsonProperty("index_name")
    private String indexName;

    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("service_id", serviceId)
                .append("index_name",indexName)
                .build();
    }

}
