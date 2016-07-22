package com.camon.connector;

import com.camon.connector.model.Server;
import com.camon.connector.model.ServerStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by camon on 2016-07-19.
 */
@Slf4j
public class FailOverRestTemplate extends RestTemplate {

    @Getter
    @Setter
    private ServerPool serverPool;

    private HealthChecker healthChecker;

    public FailOverRestTemplate(ClientHttpRequestFactory requestFactory) {
        super();
        setRequestFactory(requestFactory);
    }

    /**
     * 서버 등록. 전체 서버 개수 리턴
     * @param servers
     * @return
     */
    public int registerServers(List<Server> servers) {
        this.serverPool = new ServerPool(servers);
        this.healthChecker = new HealthChecker(serverPool);
        return serverPool.getServers().size();
    }

    @Override
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
                         ResponseExtractor<T> responseExtractor, Object... urlVariables) throws RestClientException {
        URI expanded = getUriTemplateHandler().expand(url, urlVariables);

        T result;

        try {
            result = super.doExecute(expanded, method, requestCallback, responseExtractor);
        } catch (RestClientException e) {
            String retryUrl = retryUrl(url, expanded.getPath());
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
            String retryUrl = retryUrl(url, expanded.getPath());
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
            String retryUrl = retryUrl(url.toString(), url.getPath());
            execute(retryUrl, method, requestCallback, responseExtractor);
        }

        return result;
    }

    private String retryUrl(String url, String path) {
        Server server = serverPool.getServerByUrl(url);
        log.info("fail Server: {}", server);

        // CLOSE로 변경
        serverPool.changeStatus(server, ServerStatus.CLOSED);

        // best server 반환
        return serverPool.getBestServer().getHost() + path;
    }


}
