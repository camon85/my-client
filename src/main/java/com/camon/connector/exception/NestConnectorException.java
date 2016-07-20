package com.camon.connector.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by camon on 2016-07-19.
 */
@Slf4j
public class NestConnectorException extends RuntimeException {

    public NestConnectorException() {

    }

    public NestConnectorException(String message) {
        super(message);
    }
}
