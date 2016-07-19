package com.camon.connector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by camon on 2016-07-19.
 */
@ControllerAdvice
@Slf4j
public class ConnectorAdvice {

    @ExceptionHandler(NestConnectorException.class)
    public void handlerNestConnectorException(NestConnectorException e) {
        log.error("ConnectorAdvice: {}", e.toString());
    }
}
