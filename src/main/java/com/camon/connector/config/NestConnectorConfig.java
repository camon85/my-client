package com.camon.connector.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * Created by camon on 2016-07-18.
 */

@Configuration
@EnableScheduling
@Slf4j
public class NestConnectorConfig {
    static {
        log.info("NestConnectorConfig is running");
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        return new RestTemplate(requestFactory);
    }
}
