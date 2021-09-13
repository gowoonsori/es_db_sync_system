package kr.co.saraminhr.esassingment.Domains;

import kr.co.saraminhr.esassingment.Dtos.WorkerRequestDto;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

@Getter @Setter
@EqualsAndHashCode @Builder
@AllArgsConstructor @NoArgsConstructor
public class Worker {
    private Integer id;
    private String uuid;
    private String sqlString;
    private String tableName;
    private String pkName;
    private String indexName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("uuid", uuid)
                .append("sql_string", sqlString)
                .append("table_name",tableName)
                .append("pk_name",pkName)
                .append("index_name", indexName)
                .append("created_at", createdAt)
                .append("updated_at", updatedAt)
                .build();
    }
}
