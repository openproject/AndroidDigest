package com.jayfeng.androiddigest.webservices;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.IOException;

public abstract class BaseGoogleHttpClientSpiceRequest<RESULT> extends GoogleHttpClientSpiceRequest<RESULT> {

    protected BaseGoogleHttpClientSpiceRequest(Class<RESULT> clazz) {
        super(clazz);
    }

    public HttpRequest buildGetRequest(GenericUrl url) throws IOException {
        System.setProperty("http.keepAlive", "false");
        HttpRequest request = getHttpRequestFactory().buildGetRequest(url);
        request.getHeaders().setAcceptEncoding("gzip");
        request.getHeaders().set("Connection", "close");
        request.getHeaders().setAccept("text/html,application/xhtml+xml,application/xml,application/json");
        return request;
    }

    public HttpRequest buildPostRequest(GenericUrl url, HttpContent content) throws IOException {
        System.setProperty("http.keepAlive", "false");
        HttpRequest request = getHttpRequestFactory().buildPostRequest(url, content);
        request.getHeaders().setAcceptEncoding("gzip");
        request.getHeaders().set("Connection", "close");
        request.getHeaders().setAccept("text/html,application/xhtml+xml,application/xml,application/json");
        return request;
    }
}
