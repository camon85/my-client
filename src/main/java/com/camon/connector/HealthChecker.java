package com.camon.connector;

import com.camon.connector.model.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class HealthChecker {

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(fixedDelay = 2000)
    public void check() {
        Set<Server> badServers = ServerPool.getBadServers();
        log.info("HealthChecker GOOD_SERVERS: " + ServerPool.getGoodServers());
        log.info("HealthChecker BAD_SERVERS: " + badServers);

        if (badServers.size() > 0) {
            List<Server> availableServers = badServers.stream()
                    .filter(this::available)
                    .collect(Collectors.toList());

            ServerPool.recoverServer(availableServers);
        }
    }

    private boolean available(Server badServer) {
        log.info("hello? {}", badServer);
        String url = badServer.getHost();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return true;
            }
        } catch (RestClientException e) {
            // ignore
        }

        return false;
    }
}
