package com.camon.connector;

import com.camon.connector.exception.NestConnectorException;
import com.camon.connector.model.Server;
import com.camon.connector.model.ServerStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

/**
 * Created by camon on 2016-07-18.
 */

@Slf4j
public class ServerPool {
    @Getter
    @Setter
    private static SortedSet<Server> goodServers = new TreeSet<>(Comparator.comparing(Server::getPriority));

    @Getter
    @Setter
    private static Set<Server> badServers = Collections.synchronizedSet(new HashSet<>());


    public static int registerServers(List<Server> servers) {

        goodServers.addAll(servers);
        return goodServers.size();
    }

    /**
     * OPEN 상태이면서, 우선 순위가 높은 서버 반환
     * @return
     */
    public static Server getBestServer() {
        Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
        Supplier<SortedSet<Server>> supplier = () -> new TreeSet<>(byPriority);
        SortedSet<Server> filteredServers = goodServers.stream()
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
        for (Server server : goodServers) {
            if (selectedServer.getHost().equals(server.getHost())) {
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
        for (Server server : goodServers) {
            if (selectedServer.getHost().equals(server.getHost())) {
                server.setPriority(priority);
                return true;
            }
        }

        return false;
    }

    // 서버 추가
    public static void addGoodServer(Server server) {
        goodServers.add(server);
    }

    // 서버 제거
    public static boolean removeGoodServer(Server targetServer) {
        return goodServers.removeIf(server -> server.getHost().equals(targetServer.getHost()));
    }

    public static boolean removeGoodServer(String url) {
        return goodServers.removeIf(server -> url.contains(server.getHost()));
    }


    // 서버 추가
    public static void addBadServer(Server server) {
        badServers.add(server);
    }

    // 서버 제거
    public static boolean removeBadServer(Server targetServer) {
        log.info("targetServer: {}", targetServer);

        log.info("##### badServers a: {}", badServers);
//        boolean b = badServers.removeIf(server -> server.getHost().equals(targetServer.getHost()));
        Set<Server> collect = badServers.stream()
                .filter(server -> server.getHost().equals(targetServer.getHost()))
                .collect(toSet());
        log.info(collect.toString());

        boolean b = badServers.removeIf(server -> server.getHost().equals(targetServer.getHost()));
        log.info("##### badServers b: {}", badServers);
        return b;
    }

    public static boolean removeBadServer(String url) {
        return badServers.removeIf(server -> url.contains(server.getHost()));
    }

    public static Server getServerByUrl(String url) {
        return goodServers.stream().filter(server -> url.contains(server.getHost())).findFirst().get();
    }

    public static void recoverServer(List<Server> availableServers) {
        log.info("availableServers: {}", availableServers);

        for (Server availableServer : availableServers) {
            changeStatus(availableServer, ServerStatus.OPEN);
            removeBadServer(availableServer);
        }
    }

}
