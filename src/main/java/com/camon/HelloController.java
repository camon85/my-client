package com.camon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by camon on 2016-07-18.
 */
@RestController
public class HelloController {

    @Autowired
    private HelloService service;

    @RequestMapping(value = "/stamp", method = RequestMethod.GET)
    public String stamp() {
        return service.stampFailOverGet("/ping");
    }

    @RequestMapping(value = "/console", method = RequestMethod.GET)
    public String console() {
        return service.consoleFailOverGet("/ping");
    }
}
