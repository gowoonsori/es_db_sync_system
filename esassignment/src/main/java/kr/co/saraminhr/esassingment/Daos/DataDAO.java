package kr.co.saraminhr.esassingment.Daos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DataDAO<T> {
    /**
     * 모든 데이터 조회하는 메서드
     * */
    public List<T> findAll();


    public Page<T> findAll(Pageable pageable);
}
