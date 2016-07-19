package com.camon.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
 * Created by camon on 2016-07-19.
 */
@Component
@Slf4j
public class FailOverClient {

    public String get(String apuUrl) {
        Server currentServer = getBestServer();
        String host = currentServer.getHost();
        log.info("ServerPool.CURRENT_SERVER: " + host);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(host + apuUrl);
        HttpEntity entity;
        String content = null;

        try (CloseableHttpResponse response = httpclient.execute(httpget)) {
            entity = response.getEntity();
            content = IOUtils.toString(entity.getContent(), "UTF-8");
        } catch (ClientProtocolException e) {
            log.info("##### ClientProtocolException");
        } catch (IOException e) {
            log.info("##### IOException");
            Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
            Supplier<SortedSet<Server>> supplier = () -> new TreeSet<>(byPriority);
            SortedSet<Server> collect = GOOD_SERVERS.stream()
                    .filter(s -> !s.getHost().equals(host))
                    .collect(Collectors.toCollection(supplier));


            GOOD_SERVERS = collect;
            BAD_SERVERS.add(currentServer);

            content = get(apuUrl);
        }

        return content;
    }

    private Server getBestServer() {
        if (GOOD_SERVERS.size() == 0) {
            throw new IllegalStateException("모든 서버 다운");
        }

        return GOOD_SERVERS.first();
    }
}
