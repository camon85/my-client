package com.camon.connector.model;

import lombok.Data;

/**
 * Created by camon on 2016-07-18.
 */
@Data
public class Server {

    private String host;

    // 낮을수록 우선순위 높음
    private int priority;

    private ServerStatus status;

    public Server() {

    }

    public Server(String host, int priority) {
        this.host = host;
        this.priority = priority;
    }

    public Server(String host, int priority, ServerStatus status) {
        this.host = host;
        this.priority = priority;
        this.status = status;
    }
}
