package com.es.es_sync.Daos;

import com.es.es_sync.Domains.Coffee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CoffeeDAO {
    private final JdbcTemplate jdbcTemplate;
    private final static String ModelName = "coffee";
    private final RowMapper<Coffee> rowMapper = BeanPropertyRowMapper.newInstance(Coffee.class);


    /**
     * 특정 SQL 문을 실행
     *
     * @param sql 실행할 SQL 문
     * @return List 테이블 정보를 Map형태로 담아 List로 응답
     * @thorws BadRequestException 유효하지 않은 SQL 문일 경우 응답
     * */
    public List<Map<String, Object>> findBySqlOrderByPkAsc(String sql, String pkName) {
        sql += " order by " + pkName + " asc";
        return jdbcTemplate.queryForList(sql);
    }
}
