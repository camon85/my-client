package com.camon.connector;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by camon on 2016-07-18.
 */
public class ServerList {

    public static SortedSet<Server> GOOD_SERVERS = new TreeSet<>(Comparator.comparing(Server::getPriority));

    public static SortedSet<Server> BAD_SERVERS = new TreeSet<>(Comparator.comparing(Server::getPriority));

    static {
        Server server1 = new Server("http://localhost:8100", 10);
        Server server2 = new Server("http://localhost:8200", 80);
        GOOD_SERVERS.add(server1);
        GOOD_SERVERS.add(server2);
    }

}
