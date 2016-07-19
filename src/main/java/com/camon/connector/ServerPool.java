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
        GOOD_SERVERS.add(new Server("http://localhost:8100", 10));
        GOOD_SERVERS.add(new Server("http://localhost:8200", 80));
    }

    synchronized public static void addServer(Server server) {

    }

    synchronized public static void removeServer(String url) {

    }
}
