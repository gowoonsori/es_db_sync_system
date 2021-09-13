package kr.co.saraminhr.esassingment.Services;

import kr.co.saraminhr.esassingment.Daos.TableDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TableService {

    private final TableDAO tableDAO;

    /**
     * DB의 모든 테이블을 조회하는 메서드
     *
     * @return List
     * */
    @Transactional(readOnly = true)
    public List<String> getAllTables(){
        return tableDAO.findAll();
    }

    /**
     * DB의 테이블을 조회하여 page로 반환하는 메서드
     *
     * @param pageable pageable 페이징을 위한 페이징 변수
     * @return Page
     * */
    @Transactional(readOnly = true)
    public Page<String> getTables(Pageable pageable){
        return tableDAO.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Map<String,Object> getData(String tableName, String columnName, String value){
        return tableDAO.findRowByTableNameAndPk(tableName,columnName,value);
    }
}
