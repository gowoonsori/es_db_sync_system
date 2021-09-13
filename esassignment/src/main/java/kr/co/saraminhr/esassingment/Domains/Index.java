package kr.co.saraminhr.esassingment.Domains;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode
public class Index {
    private String uuid;
    private String status;
    private String index;
    private int count;
    private String health;
    private int primary;
    private int replica;
    private int deleted;
    private String size;
    private String primarySize;

    public static Index newInstance(Map<String, String> indexMap){
        return Index.builder()
                .index(indexMap.get("index"))
                .count(Integer.parseInt(indexMap.get("docs.count")))
                .status(indexMap.get("status"))
                .uuid(indexMap.get("uuid"))
                .health(indexMap.get("health"))
                .primary(Integer.parseInt(indexMap.get("pri")))
                .replica(Integer.parseInt(indexMap.get("rep")))
                .deleted(Integer.parseInt(indexMap.get("docs.deleted")))
                .size(indexMap.get("store.size"))
                .primarySize(indexMap.get("pri.store.size"))
                .build();
    }

    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("uuid", uuid)
                .append("status",status)
                .append("index", index)
                .append("count", count)
                .append("health", health)
                .append("primary",primary)
                .append("replica",replica)
                .append("deleted",deleted)
                .append("size",size)
                .append("primary_size",primarySize)
                .build();
    }
}
