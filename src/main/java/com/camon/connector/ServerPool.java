package com.camon.connector;

import com.camon.connector.exception.NestConnectorException;
import com.camon.connector.model.Server;
import com.camon.connector.model.ServerStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by camon on 2016-07-18.
 */

@Slf4j
public class ServerPool {
    @Getter
    @Setter
    private static SortedSet<Server> goodServers = new TreeSet<>(Comparator.comparing(Server::getPriority));

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
     * CLOSE 상태 서버 반환
     * @return
     */
    public static SortedSet<Server> getBadServers() {
        Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
        Supplier<SortedSet<Server>> supplier = () -> new TreeSet<>(byPriority);
        SortedSet<Server> filteredServers = goodServers.stream()
                .filter(server -> ServerStatus.CLOSE.equals(server.getStatus()))
                .collect(Collectors.toCollection(supplier));
        return filteredServers;
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

    public static Server getServerByUrl(String url) {
        return goodServers.stream().filter(server -> url.contains(server.getHost())).findFirst().get();
    }

    public static void recoverServer(List<Server> availableServers) {
        log.info("recovered server: {}", availableServers);

        for (Server availableServer : availableServers) {
            changeStatus(availableServer, ServerStatus.OPEN);
        }
    }

}
