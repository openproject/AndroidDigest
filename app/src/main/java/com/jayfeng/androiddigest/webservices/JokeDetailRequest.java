package com.jayfeng.androiddigest.webservices;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.jayfeng.androiddigest.webservices.json.JokeDetailJson;

import java.util.HashMap;

public class JokeDetailRequest extends BaseGoogleHttpClientSpiceRequest<JokeDetailJson> {

    String url = null;

    HashMap<String, String > postParameters;

    public JokeDetailRequest() {
        super(JokeDetailJson.class);
    }

    @Override
    public JokeDetailJson loadDataFromNetwork() throws Exception {

        HttpRequest request = null;
        GenericUrl genericUrl = new GenericUrl(url);

        if (postParameters == null) {
            request = getHttpRequestFactory().buildGetRequest(genericUrl);
        } else {
            HttpContent content = new UrlEncodedContent(postParameters);
            request = buildPostRequest(genericUrl, content);
        }
        request.setParser(new JacksonFactory().createJsonObjectParser());

        return request.execute().parseAs(getResultType());
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPostParameters(HashMap<String, String> postParameters) {
        this.postParameters = postParameters;
    }
}
