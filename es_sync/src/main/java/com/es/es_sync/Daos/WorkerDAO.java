package com.es.es_sync.Daos;

import com.es.es_sync.Domains.Worker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkerDAO {
    private final JdbcTemplate jdbcTemplate;
    private final static String ModelName = "worker";
    private final RowMapper<Worker> rowMapper = BeanPropertyRowMapper.newInstance(Worker.class);

    /**
     * 테이블 이름으로 서비스 조회
     *
     * @param tableName table name
     * @return List
     */
    public List<Worker> findByTableName(String tableName) {
        String sql = "select * from "
                + ModelName
                + " where table_name = ?";
        return jdbcTemplate.query(sql, rowMapper,tableName);
    }
}

