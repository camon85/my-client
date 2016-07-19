package com.camon;

import com.camon.connector.ServerPool;
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
        String host = ServerPool.getBestServer().getHost();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(host + apiUrl, String.class);
        return responseEntity.getBody();

//        FailOverClient client = new FailOverClient();
//        return client.get(apoiUrl);
    }
}
