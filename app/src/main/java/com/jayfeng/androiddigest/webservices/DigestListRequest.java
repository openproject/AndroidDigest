package com.jayfeng.androiddigest.webservices;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.jayfeng.androiddigest.webservices.json.DigestListJson;

public class DigestListRequest extends BaseGoogleHttpClientSpiceRequest<DigestListJson> {

    public DigestListRequest() {
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
}
