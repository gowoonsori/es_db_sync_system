package kr.co.saraminhr.esassingment.Configs;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties
@EnableTransactionManagement
public class LocalDataSourceConfig implements DataSourceConfig {
    @Bean(name="localProperties")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.local")
    public DataSourceProperties dataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean(name="localDataSource")
    public DataSource dataSource(@Qualifier("localProperties") DataSourceProperties properties){
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }


    @Bean(name = "localJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("localDataSource") DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "localTransactionManager")
    public PlatformTransactionManager platformTransactionManager(@Qualifier("localDataSource") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
}
