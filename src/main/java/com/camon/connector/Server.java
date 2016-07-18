package com.camon.connector;

import lombok.Data;

/**
 * Created by camon on 2016-07-18.
 */
@Data
public class Server {

    private String url;

    // 낮을수록 우선순위 높음
    private int priority;

    Server() {

    }

    Server(String url, int priority) {
        this.url = url;
        this.priority = priority;
    }
}
