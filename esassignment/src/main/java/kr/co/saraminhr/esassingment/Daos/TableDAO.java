package kr.co.saraminhr.esassingment.Daos;

import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static kr.co.saraminhr.esassingment.Utils.PageUtil.listToPage;


@Repository
public class TableDAO implements DataDAO<String> {
    private final static int PK_NAME_INDEX = 4;
    private final static String DATABASE_NAME = "second_db";
    private final JdbcTemplate jdbcTemplate;
    public TableDAO(@Qualifier("secondJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 모든 테이블 조회
     *
     * @return List 테이블이름을 String형태의 List로 응답
     * */
    @Override
    public List<String> findAll() {
        String sql = "show tables";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    /**
     * 테이블 정보를 페이징 조회
     *
     * @param pageable 페이지 변수
     * @return Page 테이블 이름을 String형태로 Page로 Wrap하여 응답
     * @thorws BadRequestException 현재 존재하는 page보다 over된 요청의 경우 발생
     * */
    @Override
    public Page<String> findAll(Pageable pageable) {
        try {
            String sql = "show tables";
            List<String> tables = jdbcTemplate.queryForList(sql, String.class);
            return listToPage(pageable, tables);
        }  catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorMessage.OVER_PAGE.getMessage());
        }
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
     * 특정 테이블의 pk 필드명을 검색
     *
     * @param tableName 테이블 명
     * @return String .pk 필드명
     * @thorws SQLException connection get 실패시 에러
     * */
    public String findPKName(String tableName){
        try{
            Connection connection = jdbcTemplate.getDataSource().getConnection();

            DatabaseMetaData meta = connection.getMetaData();
            ResultSet pk = meta.getPrimaryKeys(DATABASE_NAME,"",tableName);
            pk.next();
            String pkName = pk.getString(PK_NAME_INDEX);
            pk.close();

            return pkName;
        }catch (SQLException e){
            throw new BadRequestException(ErrorMessage.NOT_EXIST_TABLE_NAME.getMessage(), e.getCause());
        }
    }

    /**
     * 특정 테이블의 pk를 이용해 한개 row 검색
     *
     * @param tableName 테이블 명
     * @param columnName pk 명
     * @param value pk 값
     * @return Map 한개 row
     * */
    public Map<String,Object> findRowByTableNameAndPk(String tableName, String columnName,String value){
        String sql = "select * from " + tableName + " where " + columnName + " = ?";
        Map<String,Object> result =  jdbcTemplate.queryForMap(sql,value);
        return result;
    }
}
