package com.camon.connector;

import net.jodah.failsafe.CircuitBreaker;

/**
 * Created by camon on 2016-07-19.
 */
public class FailTest {

    @SuppressWarnings("unused")
    public static void main(String... args) {
        CircuitBreaker breaker = new CircuitBreaker()
                .withFailureThreshold(5);

        breaker.open();
        if (breaker.allowsExecution()) {
            try {
                doSomething();
                breaker.recordSuccess();
            } catch (Exception e) {
                breaker.recordFailure(e);
                System.out.println("Exception");
            }
        }

        System.out.println("end");
    }

    public static void doSomething() {
        System.out.println("hello");
        int i = 0 / 1;
    }
}
