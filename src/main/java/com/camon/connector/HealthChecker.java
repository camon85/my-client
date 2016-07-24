package com.camon.connector;

import com.camon.connector.model.Server;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by camon on 2016-07-18.
 */

@Component
@Scope("prototype")
@Slf4j
public class HealthChecker implements Runnable {

    @Autowired
    private RestTemplate restTemplate;

    @Setter
    private ServerPool serverPool;

//    public HealthChecker() {
//    }
//
//    public HealthChecker(ServerPool serverPool) {
//        this.serverPool = serverPool;
//    }

    @Override
    public void run() {
        try {
            while(true) {
                checkStatus();
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            // do nothing
        }
    }

//    @Scheduled(fixedDelay = 2000)
    public void checkStatus() {
        log.info("========== start checkStatus ==========");
        Set<Server> badServers = serverPool.getBadServers();

        log.info("HealthChecker OPEN_SERVERS: " + serverPool.getServers());
        log.info("HealthChecker CLOSED_SERVERS: " + badServers);

        if (badServers.size() > 0) {
            List<Server> availableServers = badServers.stream()
                    .filter(this::isAvailable)
                    .collect(Collectors.toList());

            serverPool.recoverServer(availableServers);
        }
        log.info("========== end checkStatus ==========");
    }

    private boolean isAvailable(Server badServer) {
        boolean available = false;
        String url = badServer.getHost();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                available = true;
            }
        } catch (RestClientException e) {
            // ignore
        }

        log.info("u ok? {}", badServer.getHost());
        log.info("{}: {}", badServer.getHost(), available);
        return available;
    }


}
