package kr.co.saraminhr.esassingment.Daos;

import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Exceptions.DuplicateServiceIdException;
import kr.co.saraminhr.esassingment.Domains.Worker;
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

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static kr.co.saraminhr.esassingment.Utils.PageUtil.makePage;

@Repository
public class WorkerDAO implements DataDAO<Worker> {
    private final static int PK_NAME_INDEX = 4;
    private final static String MODEL_NAME = "worker";
    private final static String DATABASE_NAME = "es_assignment";
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Worker> rowMapper = BeanPropertyRowMapper.newInstance(Worker.class);

    public WorkerDAO(@Qualifier("localJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 모든 서비스들 조회
     *
     * @return List 테이블이름을 Worker List로 응답
     */
    @Override
    public List<Worker> findAll() {
        String sql = "select * from " + MODEL_NAME;
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 서비스 정보를 페이징 조회
     *
     * @param pageable 페이지 변수
     * @return Page 테이블 이름을 Worker 형태로 Page로 Wrap하여 응답
     */
    @Override
    public Page<Worker> findAll(Pageable pageable) {
        //paging 조회
        String sql = "select * from "
                + MODEL_NAME +
                " order by created_at desc limit ? offset ?";
        List<Worker> workerList = jdbcTemplate.query(sql, rowMapper
                , pageable.getPageSize(), pageable.getOffset());

        //total 조회
        sql = "select count(*) from " + MODEL_NAME;
        int totalSize = jdbcTemplate.queryForObject(sql, Integer.class);

        return makePage(pageable, workerList, totalSize);
    }

    /**
     * 서비스 id로 서비스 조회
     *
     * @param serviceId service id
     * @return Optional null 체킹을 위해 Worker를 Optional로 Wrap하여 응답
     */
    public Optional<Worker> findByServiceId(String serviceId) {
        try {
            String sql = "select * from "
                    + MODEL_NAME
                    + " where uuid = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, serviceId));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 서비스 id로 서비스들 조회
     *
     * @param serviceIds service id
     * @return List 조회한 서비스들 list
     */
    public List<Worker> findByServiceIds(String[] serviceIds) {
        StringBuilder sb = new StringBuilder();
        for (String id : serviceIds) {
            sb.append("'").append(id).append("'");
            if (!id.equals(serviceIds[serviceIds.length - 1])) {
                sb.append(",");
            }
        }
        String ids = sb.toString();
        String sql = String.format("select * from "
                + MODEL_NAME
                + " where uuid in (%s)", ids);
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 서비스 생성
     *
     * @param worker 요청 정보
     * @return int
     * - 1 : 생성 성공
     * - 0 : 생성 실패
     * @thorws DuplicateServiceIdException 중복된 service_id일경우 응답
     */
    public int insert(Worker worker) {
        try {
            String sql = "insert into "
                    + MODEL_NAME
                    + " (uuid,sql_string,index_name,table_name,pk_name) values (?,?,?,?,?)";
            return jdbcTemplate.update(sql, worker.getUuid(), worker.getSqlString(), worker.getIndexName(),
                    worker.getTableName(), worker.getPkName());
        } catch (DuplicateKeyException e) {
            throw new DuplicateServiceIdException(ErrorMessage.DUPLICATE_SERVICE_ID.getMessage(), e.getCause());
        } catch (DataAccessException e) {
            throw new DuplicateServiceIdException(ErrorMessage.BAD_REQUEST.getMessage(), e.getCause());
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
     * 서비스의 index 명 수정
     *
     * @param worker 요청 정보
     * @return int
     * - 1 : 수정 성공
     * @thorws BadRequestException
     * - service Id가 없는 경우
     * - index 명이 중복되는 경우
     */
    public int updateIndexName(Worker worker) {
        try {
            String sql = "update "
                    + MODEL_NAME
                    + " set index_name = ?"
                    + " where uuid = ?";

            int result = jdbcTemplate.update(sql, worker.getIndexName(), worker.getUuid());
            if (result == 0) throw new BadRequestException(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage());

            return result;
        } catch (DataAccessException e) {
            throw new BadRequestException(ErrorMessage.DUPLICATE_INDEX_NAME.getMessage(), e.getCause());
        }
    }

    /**
     * 서비스 삭제
     *
     * @param worker 요청 정보
     * @return int
     * - 1 : 삭제 성공
     * @thorws BadRequestException service Id가 없는 경우
     */
    public int delete(Worker worker) {
        String sql = "delete from "
                + MODEL_NAME
                + " where uuid=?";
        int result = jdbcTemplate.update(sql, worker.getUuid());
        if (result == 0) throw new BadRequestException(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage());

        return result;
    }
}
