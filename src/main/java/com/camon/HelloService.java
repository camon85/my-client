package com.camon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by camon on 2016-07-18.
 */
@Service
@Slf4j
public class HelloService {

    @Autowired
    private RestTemplate restTemplate;

    public String failOverGet(String apiUrl) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8100/" + apiUrl, String.class);
        return responseEntity.getBody();

//        FailOverClient client = new FailOverClient();
//        return client.get(apoiUrl);
    }
}
