package kr.co.saraminhr.esassingment.Services;

import kr.co.saraminhr.esassingment.Daos.CoffeeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CoffeeService {

    private final CoffeeDAO coffeeDAO;

    /**
     * coffee 테이블에서 id로 데이터 조회 하는 메서드
     *
     * @param value 조회할 id
     * @return Map
     * */
    @Transactional(readOnly = true)
    public Map<String,Object> getData(String value){
        return coffeeDAO.findById(value);
    }
}
