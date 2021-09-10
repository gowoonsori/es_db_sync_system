package com.es.es_sync.Dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexingRequestDto {
    @JsonProperty("service_ids")
    private String[] serviceIds;

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