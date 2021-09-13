package kr.co.saraminhr.esassingment.Daos;

import kr.co.saraminhr.esassingment.Domains.Coffee;
import kr.co.saraminhr.esassingment.Domains.Coffee;
import kr.co.saraminhr.esassingment.Domains.Worker;
import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Exceptions.DuplicateServiceIdException;
import kr.co.saraminhr.esassingment.Exceptions.InternalServerException;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static kr.co.saraminhr.esassingment.Utils.PageUtil.makePage;

@Repository
public class CoffeeDAO implements DataDAO<Coffee>{
    private final static String ModelName = "coffee";
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Coffee> rowMapper = BeanPropertyRowMapper.newInstance(Coffee.class);

    public CoffeeDAO(@Qualifier("localJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 모든 서비스들 조회
     *
     * @return List 테이블이름을 Coffee List로 응답
     * */
    @Override
    public List<Coffee> findAll() {
        String sql = "select * from " + ModelName;
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 서비스 정보를 페이징 조회
     *
     * @param pageable 페이지 변수
     * @return Page 테이블 이름을 Coffee 형태로 Page로 Wrap하여 응답
     * @thorws InternalServerException select 쿼리 중 에러 발생한 경우
     * */
    @Override
    public Page<Coffee> findAll(Pageable pageable) {
        //paging 조회
        String sql = "select * from "
                + ModelName +
                " order by created_at desc limit ? offset ?";
        List<Coffee> workerList = jdbcTemplate.query(sql, rowMapper
                , pageable.getPageSize(), pageable.getOffset());

        //total 조회
        sql = "select count(*) from " + ModelName;
        Integer totalSize = jdbcTemplate.queryForObject(sql, Integer.class);

        return makePage(pageable, workerList, totalSize);
    }

    /**
     * 특정 SQL 문을 실행
     *
     * @param sql 실행할 SQL 문
     * @return List 테이블 정보를 Map형태로 담아 List로 응답
     * @thorws BadRequestException 유효하지 않은 SQL 문일 경우 응답
     * */
    public List<Map<String, Object>> findBySQL(String sql) {
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (DataAccessException e) {
            throw new BadRequestException(ErrorMessage.INVALID_SQL.getMessage(), e.getCause());
        }
    }

    /**
     * 특정 테이블의 pk를 이용해 한개 row 검색
     *
     * @param value pk 값
     * @return Map 한개 row
     * */
    public Map<String,Object> findById(String value){
        String sql = "select * from " + ModelName + " where id = ?";
        Map<String,Object> result =  jdbcTemplate.queryForMap(sql,value);
        return result;
    }

    /**
     * 서비스 생성
     *
     * @param coffee 요청 정보
     * @return int
     * - 1 : 생성 성공
     * - 0 : 생성 실패
     * @thorws DuplicateServiceIdException 중복된 service_id일경우 응답
     */
    public int insert(Coffee coffee) {
        try {
            String sql = "insert into "
                    + ModelName
                    + " (name,description,created_at,updated_at) values (?,?,?,?)";
            return jdbcTemplate.update(sql, coffee.getName(),coffee.getDescription(), coffee.getCreatedAt(), coffee.getUpdatedAt());
        } catch (DuplicateKeyException e) {
            throw new DuplicateServiceIdException(ErrorMessage.DUPLICATE_SERVICE_ID.getMessage(), e.getCause());
        } catch (DataAccessException e) {
            throw new DuplicateServiceIdException(ErrorMessage.BAD_REQUEST.getMessage(), e.getCause());
        }
    }

}
