package com.camon.connector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.camon.connector.ServerPool.BAD_SERVERS;
import static com.camon.connector.ServerPool.GOOD_SERVERS;

/**
 * Created by camon on 2016-07-19.
 */
@Slf4j
public class FailOverRestTemplate extends RestTemplate {

     public FailOverRestTemplate(ClientHttpRequestFactory requestFactory) {
        super();
        setRequestFactory(requestFactory);
    }

    @Override
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
                         ResponseExtractor<T> responseExtractor, Object... urlVariables) throws RestClientException {

        URI expanded = getUriTemplateHandler().expand(url, urlVariables);

        T result;

        try {
            result = super.doExecute(expanded, method, requestCallback, responseExtractor);
        } catch (RestClientException e) {
            String retryUrl = failOver(expanded.getPath());
            expanded = getUriTemplateHandler().expand(retryUrl, urlVariables);
            result = execute(expanded, method, requestCallback, responseExtractor);
        }

        return result;
    }

    @Override
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
                         ResponseExtractor<T> responseExtractor, Map<String, ?> urlVariables) throws RestClientException {

        URI expanded = getUriTemplateHandler().expand(url, urlVariables);

        T result;
        try {
            result = super.doExecute(expanded, method, requestCallback, responseExtractor);
        } catch (RestClientException e) {
            String retryUrl = failOver(expanded.getPath());
            expanded = getUriTemplateHandler().expand(retryUrl, urlVariables);
            result = execute(expanded, method, requestCallback, responseExtractor);
        }
        return result;
    }

    @Override
    public <T> T execute(URI url, HttpMethod method, RequestCallback requestCallback,
                         ResponseExtractor<T> responseExtractor) throws RestClientException {
        T result = null;

        try {
            result = super.doExecute(url, method, requestCallback, responseExtractor);
        } catch (RestClientException e) {
            String retryUrl = failOver(url.getPath());
            execute(retryUrl, method, requestCallback, responseExtractor);
        }

        return result;
    }

    private String failOver(String path) {
        Server currentServer = ServerPool.getBestServer();
        log.info("currentServer: {}", currentServer);
        String host = currentServer.getHost();
        Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
        Supplier<SortedSet<Server>> supplier = () -> new TreeSet<>(byPriority);
        SortedSet<Server> collect = GOOD_SERVERS.stream()
                .filter(s -> !s.getHost().equals(host))
                .collect(Collectors.toCollection(supplier));

        GOOD_SERVERS = collect;
        BAD_SERVERS.add(currentServer);

        return ServerPool.getBestServer().getHost() + path;

    }

}
