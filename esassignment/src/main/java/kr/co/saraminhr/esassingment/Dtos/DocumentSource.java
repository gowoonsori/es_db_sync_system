package kr.co.saraminhr.esassingment.Dtos;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder @Getter
@Setter @AllArgsConstructor
public class DocumentSource<T> {
    T data;
    T highlight;

    public static DocumentSource fromSearchHit(SearchHit searchHit){
        //highlight map 생성
        Map<String, List<String>> highlight = new HashMap<>();
        for(Map.Entry<String, HighlightField> entry : searchHit.getHighlightFields().entrySet()){
            //field get
            HighlightField field = searchHit.getHighlightFields().get(entry.getKey());

            //List 초기화
            highlight.putIfAbsent(field.getName(),new ArrayList<>());

            //매치하는 문장 모두 List 에 push
            for(Text text : field.fragments()){
                highlight.get(field.getName()).add(text.string());
            }
        }

        return DocumentSource.builder()
                .data(searchHit.getSourceAsMap())
                .highlight(highlight)
                .build();
    }

    @Override
    public String toString(){
        var builder =  new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("data", data);
        if(highlight != null)builder.append("highlight", highlight);

        return builder.build();
    }
}
