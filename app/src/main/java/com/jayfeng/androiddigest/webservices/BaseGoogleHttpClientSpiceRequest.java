package com.jayfeng.androiddigest.webservices;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.IOException;
import java.util.HashMap;

public abstract class BaseGoogleHttpClientSpiceRequest<RESULT> extends GoogleHttpClientSpiceRequest<RESULT> {

    String url = null;
    HashMap<String, String > postParameters;

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

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPostParameters(HashMap<String, String> postParameters) {
        this.postParameters = postParameters;
    }
}
