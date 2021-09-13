package kr.co.saraminhr.esassingment.Configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionConfig {

    @Primary
    @Bean(name = "chainedTransactionManager")
    public ChainedTransactionManager trasactionManager(
            @Qualifier("tableTransactionManager")PlatformTransactionManager tableTransactionManager,
            @Qualifier("localTransactionManager") PlatformTransactionManager localTransactionManager
    ){
        return new ChainedTransactionManager(tableTransactionManager,localTransactionManager);
    }

}
