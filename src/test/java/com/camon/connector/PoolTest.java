package com.camon.connector;

import java.io.IOException;

/**
 * Created by camon on 2016-07-19.
 */
public class PoolTest {
    public static void main(String[] args) throws IOException {

//        HttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
//        CloseableHttpClient client = HttpClients.custom().setConnectionManager(poolingConnManager).build();
//        client.execute(new HttpGet("http://www.baeldung.com/"));
//
//        HttpGet get1 = new HttpGet("http://www.baeldung.com/");
//        HttpGet get2 = new HttpGet("http://google.com");
//        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
//        CloseableHttpClient client1 = HttpClients.custom().setConnectionManager
//                (connManager).build();
//        CloseableHttpClient client2 = HttpClients.custom().setConnectionManager
//                (connManager).build();
//        MultiHttpClientConnThread thread1 = new MultiHttpClientConnThread(client1, get1);
//        MultiHttpClientConnThread thread2 = new MultiHttpClientConnThread(client2, get2);
//        thread1.start();
//        thread2.start();
//        thread1.join();
//        thread2.join();
    }
}
