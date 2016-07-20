package com.camon.connector.advice;

import com.camon.connector.exception.NestConnectorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by camon on 2016-07-19.
 */
@ControllerAdvice
@Slf4j
public class NestConnectorAdvice {

    @ExceptionHandler(NestConnectorException.class)
    @ResponseBody
    public String handlerNestConnectorException(NestConnectorException e) {
        log.error("ConnectorAdvice: {}", e.toString());
        return e.getMessage();
    }
}
