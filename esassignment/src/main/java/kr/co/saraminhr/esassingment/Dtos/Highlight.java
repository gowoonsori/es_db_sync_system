package kr.co.saraminhr.esassingment.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Highlight {
    @JsonProperty("columns")
    private Column[] columns;

    @JsonProperty("prefix_tag")
    private String prefixTag;

    @JsonProperty("postfix_tag")
    private String postfixTag;

    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("columns", columns)
                .append("prefix_tag", prefixTag)
                .append("postfix_tag", postfixTag)
                .build();
    }

    @Getter
    public static class Column{
        @JsonProperty("column")
        private String column;

        @Override
        public String toString(){
            return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                    .append("column", column)
                    .build();
        }

    }
}
