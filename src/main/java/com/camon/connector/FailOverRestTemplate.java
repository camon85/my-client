package com.camon.connector;

import com.camon.connector.model.Server;
import com.camon.connector.model.ServerStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static com.camon.connector.ServerPool.BAD_SERVERS;

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

        // CLOSE로 변경
        ServerPool.changeStatus(currentServer, ServerStatus.CLOSE);

        // BAD 목록에 추가
        BAD_SERVERS.add(currentServer);

        return ServerPool.getBestServer().getHost() + path;

    }

}
