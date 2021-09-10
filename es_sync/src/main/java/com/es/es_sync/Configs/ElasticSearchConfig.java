package com.es.es_sync.Configs;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
    @Bean
    public RestHighLevelClient client(ElasticsearchProperties properties){
        return new RestHighLevelClient(RestClient.builder(properties.hosts()));
    }
}
