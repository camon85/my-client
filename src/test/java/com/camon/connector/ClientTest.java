package com.camon.connector;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * Created by camon on 2016-07-19.
 */
public class ClientTest {

    public static void main(String[] args) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://localhost:8100");
        HttpEntity entity;
        String content = null;

        try (CloseableHttpResponse response = httpclient.execute(httpget)) {
            System.out.println(response);
            entity = response.getEntity();
            content = IOUtils.toString(entity.getContent(), "UTF-8");
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        System.out.println(content);
    }
}
