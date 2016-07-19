package com.camon.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.camon.connector.ServerPool.BAD_SERVERS;
import static com.camon.connector.ServerPool.GOOD_SERVERS;

/**
 * Created by camon on 2016-07-18.
 */
@Component
@Slf4j
public class HealthChecker {

    @Scheduled(fixedRate = 5000)
    public void check() {
        for (Server badServer : BAD_SERVERS) {
            call(badServer);
        }

        log.info("HealthChecker GOOD_SERVERS: " + GOOD_SERVERS);
        log.info("HealthChecker BAD_SERVERS: " + BAD_SERVERS);
    }

    private void call(Server badServer) {
        String url = badServer.getHost();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);

        try (CloseableHttpResponse response = httpclient.execute(httpget)) {
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                return;
            }

            Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
            Supplier<SortedSet<Server>> supplier = () -> new TreeSet<>(byPriority);
            SortedSet<Server> collect = BAD_SERVERS.stream()
                    .filter(s -> !s.getHost().equals(url))
                    .collect(Collectors.toCollection(supplier));


            BAD_SERVERS = collect;
            GOOD_SERVERS.add(badServer); // 호출되면 복구
        } catch (ClientProtocolException e) {
            log.info("HealthChecker ClientProtocolException 에러들은 무시");
        } catch (IOException e) {
            log.info("HealthChecker IOException 에러들은 무시");
        }
    }
}
