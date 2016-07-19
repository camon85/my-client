package com.camon;

import com.camon.connector.FailOverRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by camon on 2016-07-18.
 */
@Configuration
public class Config {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        RestTemplate restTemplate = new FailOverRestTemplate(requestFactory);
        return restTemplate;
    }
}
