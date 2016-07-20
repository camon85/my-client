package com.camon;

import com.camon.connector.FailOverRestTemplate;
import com.camon.connector.model.Server;
import com.camon.connector.model.ServerStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by camon on 2016-07-18.
 */
@Configuration
public class Config {

    @Bean
    public FailOverRestTemplate failOverRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        FailOverRestTemplate failOverRestTemplate = new FailOverRestTemplate(requestFactory);

        List<Server> servers = new ArrayList<>();
        servers.add(new Server("http://localhost:8100", 100, ServerStatus.OPEN));
        servers.add(new Server("http://localhost:8200", 200, ServerStatus.OPEN));
        servers.add(new Server("http://localhost:8300", 300, ServerStatus.OPEN));
        failOverRestTemplate.registerServers(servers);

        return failOverRestTemplate;
    }
}
