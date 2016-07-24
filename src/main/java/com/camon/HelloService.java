package com.camon;

import com.camon.connector.FailOverRestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Created by camon on 2016-07-18.
 */
@Service
@Slf4j
public class HelloService {

    @Autowired
    private FailOverRestTemplate stampFailOverRestTemplate;

    @Autowired
    private FailOverRestTemplate consoleFailOverRestTemplate;

    public String stampFailOverGet(String apiUrl) {
        String host = stampFailOverRestTemplate.getServerPool().getBestServer().getHost();
        ResponseEntity<String> responseEntity = stampFailOverRestTemplate.getForEntity(host + apiUrl, String.class);
        return responseEntity.getBody();
    }

    public String consoleFailOverGet(String apiUrl) {
        String host = consoleFailOverRestTemplate.getServerPool().getBestServer().getHost();
        ResponseEntity<String> responseEntity = consoleFailOverRestTemplate.getForEntity(host + apiUrl, String.class);
        return responseEntity.getBody();
    }
}
