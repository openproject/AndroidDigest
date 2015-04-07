package com.jayfeng.androiddigest.webservices.json;

import com.google.api.client.util.Key;

public class OfflineJson {

    @Key
    private String title;
    @Key
    private String type;
    @Key
    private String url;
    @Key
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
