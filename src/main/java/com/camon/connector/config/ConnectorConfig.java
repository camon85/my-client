package com.camon.connector.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by camon on 2016-07-18.
 */

@Configuration
@EnableScheduling
@Slf4j
public class ConnectorConfig {
    static {
        log.info("##### ConnectorConfig");
    }
}
