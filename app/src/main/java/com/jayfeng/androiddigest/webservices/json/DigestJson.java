package com.jayfeng.androiddigest.webservices.json;

import com.google.api.client.util.Key;

public class DigestJson extends StatusJson {

    @Key
    private int id;
    @Key
    private String title;
    @Key("abstract")
    private String abstractStr;
    @Key
    private String content;
    @Key
    private String thumbnail;
    @Key
    private int more;
    @Key
    private String url;
    @Key
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractStr() {
        return abstractStr;
    }

    public void setAbstractStr(String abstractStr) {
        this.abstractStr = abstractStr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getMore() {
        return more;
    }

    public void setMore(int more) {
        this.more = more;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
