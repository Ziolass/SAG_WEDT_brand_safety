package com.sag_wedt.brand_safety.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;

import java.util.List;

public class ApacheHttpClient {
    private static final String USER_AGENT = "Mozilla/5.0";

    // HTTP GET request
    public static HttpResponse sendGet(String url) throws Exception {

        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        return client.execute(request);

    }

    // HTTP POST request
    public static HttpResponse sendPost(String url, List<NameValuePair> urlParameters) throws Exception {

        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        return client.execute(post);

    }
}
