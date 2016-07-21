package com.camon.connector;

import com.camon.connector.model.Server;
import com.camon.connector.model.ServerStatus;
import org.junit.Test;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by camon on 2016-07-19.
 */
public class ServerPoolTest {

    @Test
    public void bestServer() {
        Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
        SortedSet<Server> GOOD_SERVERS = new TreeSet<>(byPriority);
        GOOD_SERVERS.add(new Server("http://localhost:8100", 100, ServerStatus.CLOSED));
        GOOD_SERVERS.add(new Server("http://localhost:8200", 200, ServerStatus.CLOSED));
        GOOD_SERVERS.add(new Server("http://localhost:8300", 300, ServerStatus.CLOSED));

        Supplier<SortedSet<Server>> supplier = () -> new TreeSet<>(byPriority);

        SortedSet<Server> collect = GOOD_SERVERS.stream()
                .filter(s -> s.getStatus().equals(ServerStatus.OPEN))
                .collect(Collectors.toCollection(supplier));

        System.out.println(collect);

        if (collect.size() != 0) {
            System.out.println(collect.first());
        }
    }

    @Test
    public void changeStatus() {
        Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
        SortedSet<Server> GOOD_SERVERS = new TreeSet<>(byPriority);
        GOOD_SERVERS.add(new Server("http://localhost:8100", 100, ServerStatus.CLOSED));
        GOOD_SERVERS.add(new Server("http://localhost:8200", 200, ServerStatus.CLOSED));
        GOOD_SERVERS.add(new Server("http://localhost:8300", 300, ServerStatus.CLOSED));

        Server selectedServer = GOOD_SERVERS.first();

        for (Server server : GOOD_SERVERS) {
            if (selectedServer == server) {
                System.out.println("!!");
                server.setStatus(ServerStatus.OPEN);
            }
        }
    }

}
