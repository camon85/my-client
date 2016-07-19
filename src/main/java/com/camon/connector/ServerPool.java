package com.camon.connector;

import com.camon.connector.model.Server;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by camon on 2016-07-18.
 */
public class ServerPool {

    public static SortedSet<Server> GOOD_SERVERS = new TreeSet<>(Comparator.comparing(Server::getPriority));

    public static SortedSet<Server> BAD_SERVERS = new TreeSet<>(Comparator.comparing(Server::getPriority));

    static {
        // TODO  개발 테스트용
        registerDummyServers();
    }

    private static void registerDummyServers() {
        GOOD_SERVERS.add(new Server("http://localhost:8100", 100));
        GOOD_SERVERS.add(new Server("http://localhost:8200", 200));
        GOOD_SERVERS.add(new Server("http://localhost:8300", 300));
    }

    private static void registerServers(SortedSet<Server> servers) {
        GOOD_SERVERS = servers;
    }

    public static Server getBestServer() {
        if (GOOD_SERVERS.size() == 0) {
            throw new IllegalStateException("모든 서버 다운");
        }

        return GOOD_SERVERS.first();
    }

    public static void addServer(Server server) {

    }

    public static void removeServer(String url) {

    }

}
