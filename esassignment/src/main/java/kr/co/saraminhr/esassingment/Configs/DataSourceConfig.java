package kr.co.saraminhr.esassingment.Configs;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

public interface DataSourceConfig {
    public JdbcTemplate jdbcTemplate(DataSource dataSource);
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource);
}
