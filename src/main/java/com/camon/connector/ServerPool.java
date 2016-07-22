package com.camon.connector;

import com.camon.connector.exception.NestConnectorException;
import com.camon.connector.model.Server;
import com.camon.connector.model.ServerStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by camon on 2016-07-18.
 */

@Slf4j
public class ServerPool {
//    @Getter
//    private static SortedSet<Server> SERVERS = new TreeSet<>(Comparator.comparing(Server::getPriority));

    private static Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);

    @Getter
    private SortedSet<Server> servers;


    public ServerPool() {
        // do nothing
    }

    public ServerPool(List<Server> serverList) {
        this.servers = Collections.synchronizedSortedSet(new TreeSet<>(byPriority));
        servers.addAll(serverList);
    }

    /**
     * 서버 추가
     * @param servers
     * @return
     */
    public int registerServers(List<Server> servers) {
        servers.addAll(servers);
        return servers.size();
    }

    /**
     * 서버 추가
     * @param server
     */
    public int registerServer(Server server) {
        servers.add(server);
        return servers.size();
    }

    /**
     * 서버 등록 해제
     * @param targetServer
     * @return
     */
    public boolean unregisterServer(Server targetServer) {
        return servers.removeIf(server -> server.getHost().equals(targetServer.getHost()));
    }

    /**
     * 서버 등록 해제
     * @param url
     * @return
     */
    public boolean unregisterServer(String url) {
        return servers.removeIf(server -> url.contains(server.getHost()));
    }

    /**
     * OPEN 상태이면서, 우선 순위가 높은 서버 반환
     * @return
     */
    public Server getBestServer() {
        Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
        Supplier<SortedSet<Server>> supplier = () -> new TreeSet<>(byPriority);
        SortedSet<Server> filteredServers = servers.stream()
                .filter(server -> ServerStatus.OPEN.equals(server.getStatus()))
                .collect(Collectors.toCollection(supplier));

        if (filteredServers.size() == 0) {
            throw new NestConnectorException("모든 서버 다운");
        }

        return filteredServers.first();
    }

    /**
     * CLOSE 상태 서버 반환
     * @return
     */
    public SortedSet<Server> getBadServers() {
        Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
        Supplier<SortedSet<Server>> supplier = () -> new TreeSet<>(byPriority);
        SortedSet<Server> filteredServers = servers.stream()
                .filter(server -> ServerStatus.CLOSED.equals(server.getStatus()))
                .collect(Collectors.toCollection(supplier));
        return filteredServers;
    }

    /**
     * 대상 서버 상태 변경. 성공 시 true 반환
     * @param targetServer
     * @param status
     * @return
     */
    public boolean changeStatus(Server targetServer, ServerStatus status) {
        for (Server server : servers) {
            if (targetServer.getHost().equals(server.getHost())) {
                server.setStatus(status);
                return true;
            }
        }

        return false;
    }

    /**
     * 대상 서버 우선 순위 변경
     * @param targetServer
     * @param priority
     * @return
     */
    public boolean changePriority(Server targetServer, int priority) {
        for (Server server : servers) {
            if (targetServer.getHost().equals(server.getHost())) {
                server.setPriority(priority);
                return true;
            }
        }

        return false;
    }


    public Server getServerByUrl(String url) {
        return servers.stream().filter(server -> url.contains(server.getHost())).findFirst().get();
    }

    public void recoverServer(List<Server> availableServers) {
        for (Server availableServer : availableServers) {
            changeStatus(availableServer, ServerStatus.OPEN);
            log.info("recovered server: {}", availableServer);
        }
    }

}
