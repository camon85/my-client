package com.camon.connector;

import com.camon.connector.model.Server;
import com.camon.connector.model.ServerStatus;
import org.junit.Test;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by camon on 2016-07-18.
 */
public class CollectionTest {

    @Test
    public void sdfsd() {
        List<String> BAD_SERVERS = new ArrayList<>();
        BAD_SERVERS.add("http://localhost:8100");
        BAD_SERVERS.add("http://localhost:8200");

        Iterator<String> iterator = BAD_SERVERS.iterator();

        while(iterator.hasNext()) {
            String next = iterator.next();

            if ("http://localhost:8100".equals(next)) {
                iterator.remove();
                break;
            }

            System.out.println(next);
        }

        System.out.println("result: " + BAD_SERVERS);
    }

    @Test
    public void adasd() {
        SortedSet<Server> GOOD_SERVERS = new TreeSet<>(Comparator.comparing(Server::getPriority));
        Server server1 = new Server("http://localhost:8100", 10);
        Server server2 = new Server("http://localhost:8200", 80);
        GOOD_SERVERS.add(server1);
        GOOD_SERVERS.add(server2);

        Comparator<Server> byPriority = Comparator.comparing(Server::getPriority);
        Supplier<SortedSet<Server>> supplier = () -> new TreeSet<>(byPriority);
        SortedSet<Server> collect = GOOD_SERVERS.stream()
                .filter(s -> s.getHost().equals("http://localhost:8100"))
                .collect(Collectors.toCollection(supplier));


        System.out.println(collect.first());

    }

    @Test
    public void empty() {
        SortedSet<Server> servers = new TreeSet<>(Comparator.comparing(Server::getPriority));
        System.out.println(servers.size());

    }

    @Test
    public void contains() {
        Set<Server> servers = new HashSet<>();
        servers.add(new Server("http://localhost:8100", 100, ServerStatus.OPEN));
        servers.add(new Server("http://localhost:8200", 200, ServerStatus.OPEN));
        servers.add(new Server("http://localhost:8300", 300, ServerStatus.OPEN));

        String url = "http://localhost:8200/ping";

        servers.stream()
                .filter(server -> url.contains(server.getHost()))
                .forEach(server -> System.out.println("1" + server)
        );
    }

    @Test
    public void remove() {
        Set<Server> servers = Collections.synchronizedSet(new HashSet<>());
        servers.add(new Server("http://localhost:8100", 100, ServerStatus.OPEN));
        servers.add(new Server("http://localhost:8200", 200, ServerStatus.OPEN));
        servers.add(new Server("http://localhost:8300", 300, ServerStatus.OPEN));
        System.out.println(servers);

        Server removeTargetServer = new Server("http://localhost:8100", 100, ServerStatus.OPEN);
        boolean b = servers.removeIf(server -> server.getHost().equals(removeTargetServer.getHost()));
        System.out.println(b);
        System.out.println(servers);
    }

}
