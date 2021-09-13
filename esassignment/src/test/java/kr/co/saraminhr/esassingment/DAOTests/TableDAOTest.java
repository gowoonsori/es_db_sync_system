package kr.co.saraminhr.esassingment.DAOTests;

import kr.co.saraminhr.esassingment.Configs.TableDataSourceConfig;
import kr.co.saraminhr.esassingment.Daos.TableDAO;
import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {TableDAO.class , TableDataSourceConfig.class}, properties = "classpath:application.yml")
@Transactional
public class TableDAOTest {
    @Autowired
    TableDAO tableDAO;

    @Test
    @DisplayName("모든 테이블 조회 테스트")
    public void findAllTest(){
        //when
        List<String> tables = tableDAO.findAll();

        //then
        assertEquals(true,tables.contains("members"));
    }

    @Test
    @DisplayName("모든 테이블 페이징 조회 테스트")
    public void findAllPagingTest(){
        //given
        Pageable pageable = PageRequest.of(0,10);

        //when
        Page<String> tables = tableDAO.findAll(pageable);

        //then
        assertEquals(10,tables.getSize());
        assertEquals(0,tables.getNumber());
    }

    @Test
    @DisplayName("모든 테이블 페이징 조회 실패 테스트 : 유효하지 않은 페이징변수")
    public void findAllPagingFailTest(){
        //given
        Pageable pageable = PageRequest.of(999999,10);

        //when
        Exception exception = assertThrows(BadRequestException.class,()->{
            tableDAO.findAll(pageable);
        });

        //then
        assertEquals(ErrorMessage.OVER_PAGE.getMessage(),
                exception.getMessage());
    }

    @Test
    @DisplayName("SQL 문으로 데이터 조회 테스트")
    public void findBySQLTest(){
        //given
        String sql = "select * from members where members.mem_idx < 10000 limit 1";

        //when
        List<Map<String,Object>> result = tableDAO.findBySQL(sql);

        //then
        assertEquals(Long.class.getName(),
                result.get(0).get("mem_idx").getClass().getName());
    }

    @Test
    @DisplayName("SQL 문으로 데이터 조회 실패 테스트 : 잘못된 SQL")
    public void findBySQLFailTest(){
        //given
        String sql = "select * from members members.mem_idx < 10000 limit 1";

        //when
        Exception exception = assertThrows(BadRequestException.class,()->{
            tableDAO.findBySQL(sql);
        });
        //then
        assertEquals(ErrorMessage.INVALID_SQL.getMessage(),
                exception.getMessage());
    }

    @Test
    @DisplayName("table의 pk 들 조회")
    public void findPks() {
        //given
        String tableName = "members";
        String expectPKName = "mem_idx";

        //when
        String pkName = tableDAO.findPKName(tableName);

        //then
        assertEquals(expectPKName,pkName);
    }

    @Test
    @DisplayName("table의 pk 들 조회 실패 테스트 : 없는 테이블 명")
    public void findPksFailTest() {
        //given
        String tableName = "memberssss";

        //when
        Exception exception = assertThrows(BadRequestException.class,()->{
            tableDAO.findPKName(tableName);
        });
        //then
        assertEquals(ErrorMessage.NOT_EXIST_TABLE_NAME.getMessage(),
                exception.getMessage());
    }
}
