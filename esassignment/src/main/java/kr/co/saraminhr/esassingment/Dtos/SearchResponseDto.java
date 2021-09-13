package kr.co.saraminhr.esassingment.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Setter @EqualsAndHashCode
@Getter @NoArgsConstructor
@AllArgsConstructor @Builder
public class SearchResponseDto{
    @JsonProperty("total")
    private Long total;

    @JsonProperty("page_size")
    private Integer pageSize;

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("result")
    private DocumentSource[] result;

    @Override
    public String toString(){
        var builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("result", result)
                .append("total", total);

        if(pageSize != null) builder.append("page_size", pageSize);
        if(page != null) builder.append("page", page);

        return builder.build();
    }
}
