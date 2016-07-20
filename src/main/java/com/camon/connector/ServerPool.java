package com.camon.connector;

import com.camon.connector.exception.NestConnectorException;
import com.camon.connector.model.Server;
import com.camon.connector.model.ServerStatus;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by camon on 2016-07-18.
 */
public class ServerPool {

    public static SortedSet<Server> GOOD_SERVERS = new TreeSet<>(Comparator.comparing(Server::getPriority));

    public static Set<Server> BAD_SERVERS = new HashSet<>();

    static {
        // TODO  개발 테스트용
        registerDummyServers();
    }

    private static void registerDummyServers() {
        GOOD_SERVERS.add(new Server("http://localhost:8100", 100, ServerStatus.OPEN));
        GOOD_SERVERS.add(new Server("http://localhost:8200", 200, ServerStatus.OPEN));
        GOOD_SERVERS.add(new Server("http://localhost:8300", 300, ServerStatus.OPEN));
    }

    private static void registerServers(SortedSet<Server> servers) {
        GOOD_SERVERS = servers;
    }

    /**
     * OPEN 상태이면서, 우선 순위가 높은 서버 반환
     * @return
     */
    public static Server getBestServer() {
        Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
        Supplier<SortedSet<Server>> supplier = () -> new TreeSet<>(byPriority);
        SortedSet<Server> filteredServers = GOOD_SERVERS.stream()
                .filter(server -> ServerStatus.OPEN.equals(server.getStatus()))
                .collect(Collectors.toCollection(supplier));

        if (filteredServers.size() == 0) {
            throw new NestConnectorException("모든 서버 다운");
        }

        return filteredServers.first();
    }

    /**
     * 특정 서버 상태 변경. 성공 시 true 반환
     * @param selectedServer
     * @param status
     * @return
     */
    public static boolean changeStatus(Server selectedServer, ServerStatus status) {
        for (Server server : GOOD_SERVERS) {
            if (selectedServer == server) {
                server.setStatus(status);
                return true;
            }
        }

        return false;
    }

    /**
     * 특정 서버 우선 순위 변경
     * @param selectedServer
     * @param priority
     * @return
     */
    public static boolean changePriority(Server selectedServer, int priority) {
        for (Server server : GOOD_SERVERS) {
            if (selectedServer == server) {
                server.setPriority(priority);
                return true;
            }
        }

        return false;
    }

    // 서버 추가
    public static void addServer(Server server) {
        GOOD_SERVERS.add(server);
    }

    // 서버 제거
    public static void removeServer(String url) {

    }

}
