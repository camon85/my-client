package com.camon.connector;

import com.camon.connector.model.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by camon on 2016-07-18.
 */
@Component
@Slf4j
public class HealthChecker {

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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);

        try (CloseableHttpResponse response = httpclient.execute(httpget)) {
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                return true;
            }
        } catch (ClientProtocolException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        }

        return false;
    }
}
