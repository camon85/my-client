package com.camon.connector;

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
        registerServers();
    }

    private static void registerServers() {
        GOOD_SERVERS.add(new Server("http://localhost:8100", 100));
        GOOD_SERVERS.add(new Server("http://localhost:8200", 200));
        GOOD_SERVERS.add(new Server("http://localhost:8300", 300));
    }

    synchronized public static void addServer(Server server) {

    }

    synchronized public static void removeServer(String url) {

    }

    public static Server getBestServer() {
        if (GOOD_SERVERS.size() == 0) {
            throw new IllegalStateException("모든 서버 다운");
        }

        return GOOD_SERVERS.first();
    }
}
