package com.camon;

import com.camon.connector.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.camon.connector.ServerList.BAD_SERVERS;
import static com.camon.connector.ServerList.GOOD_SERVERS;

/**
 * Created by camon on 2016-07-18.
 */
@Service
@Slf4j
public class HelloService {


    private Server getServerInfo() {
        return GOOD_SERVERS.first();
    }

    public String call() {
        Server currentServer = getServerInfo();
        String url = currentServer.getUrl();
        log.info("ServerList.CURRENT_SERVER: " + url);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        HttpEntity entity;
        String content = null;

        try (CloseableHttpResponse response = httpclient.execute(httpget)) {
            entity = response.getEntity();
            content = entity.getContent().toString();
            log.info(String.valueOf(entity));
        } catch (ClientProtocolException e) {
            log.info("##### HelloService ClientProtocolException");
        } catch (IOException e) {
            log.info("##### HelloService IOException");
            Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
            Supplier<SortedSet<Server>> supplier = () -> new TreeSet<>(byPriority);
            SortedSet<Server> collect = GOOD_SERVERS.stream()
                    .filter(s -> !s.getUrl().equals(url))
                    .collect(Collectors.toCollection(supplier));


            GOOD_SERVERS = collect;
            BAD_SERVERS.add(currentServer);

            call();
        }

        log.info("HelloService GOOD_SERVERS: " + GOOD_SERVERS);
        log.info("HelloService BAD_SERVERS: " + BAD_SERVERS);
        return content;

    }
}
