package kr.co.saraminhr.esassingment.Utils;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@NoArgsConstructor
public class PageUtil {

    /**
     * list 에서 일부 잘라 를 Page 로 변환하여 반환해주는 함수
     *
     * @param pageable 페이징을 위한 페이징 변수
     * @param list page 로 변환할 list
     * @return Page
     * */
    public static <T> Page<T> listToPage(Pageable pageable, List<T> list){
        int start = (int)pageable.getOffset();
        int end = Math.min((start+pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start,end), pageable, list.size());
    }

    /**
     * list 를 Page 로 변환하여 반환해주는 함수
     *
     * @param pageable 페이징을 위한 페이징 변수
     * @param list page 로 변환할 list
     * @return Page
     * */
    public static <T> Page<T> makePage(Pageable pageable,List<T> list,int totalSize){
        return new PageImpl<>(list, pageable, totalSize);
    }
}
