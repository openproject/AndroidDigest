package com.jayfeng.androiddigest.webservices.json;

import com.google.api.client.util.Key;

public class StatusJson {
    @Key
    private String s_status;
    @Key
    private String s_message;
    @Key
    private int s_code;

    public String getS_status() {
        return s_status;
    }

    public void setS_status(String s_status) {
        this.s_status = s_status;
    }

    public String getS_message() {
        return s_message;
    }

    public void setS_message(String s_message) {
        this.s_message = s_message;
    }

    public int getS_code() {
        return s_code;
    }

    public void setS_code(int s_code) {
        this.s_code = s_code;
    }
}
