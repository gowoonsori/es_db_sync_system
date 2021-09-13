package kr.co.saraminhr.esassingment.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode @Builder
public class SearchRequestDto {
    @NotBlank(message = "공백은 입력이 불가능합니다.",groups = NotBlank.class)
    @Size(min=1, max=30, message = "서비스 id는 1~30자 사이로 입력해주세요.",groups = Size.class)
    @JsonProperty("service_id")
    private String serviceId;

    @Size(min=0, max=255, message = "검색 단어는 0~255자 사이로 입력해주세요.",groups = Size.class)
    @JsonProperty("search_text")
    private String searchText;

    @JsonProperty("result_columns")
    private String[] resultColumns;

    @JsonProperty("highlight")
    private Highlight highlight;

    @JsonProperty("page_size")
    @Min(value = 0, message = "0이상 입력하셔야 합니다.")
    private Integer pageSize;

    @JsonProperty("page_no")
    @Min(value = 0, message = "0이상 입력하셔야 합니다.")
    private Integer pageNo;

    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("service_id", serviceId)
                .append("search_text", searchText)
                .append("result_columns", resultColumns)
                .append("highlight", highlight)
                .append("page_size", pageSize)
                .append("page_no", pageNo)
                .build();
    }
}
