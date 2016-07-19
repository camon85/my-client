package com.camon;

import com.camon.connector.FailOverClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by camon on 2016-07-18.
 */
@Service
@Slf4j
public class HelloService {

    public String failOverGet(String apoiUrl) {
        FailOverClient client = new FailOverClient();
        return client.get(apoiUrl);
    }
}
