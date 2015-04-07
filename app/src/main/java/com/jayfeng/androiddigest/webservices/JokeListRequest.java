package com.jayfeng.androiddigest.webservices;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.jayfeng.androiddigest.webservices.json.DigestListJson;

import java.util.HashMap;

public class JokeListRequest extends BaseGoogleHttpClientSpiceRequest<DigestListJson> {

    String url = null;

    HashMap<String, String > postParameters;

    public JokeListRequest() {
        super(DigestListJson.class);
    }

    @Override
    public DigestListJson loadDataFromNetwork() throws Exception {

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
