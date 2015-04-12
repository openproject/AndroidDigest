package com.jayfeng.androiddigest.webservices.json;

import com.google.api.client.util.Key;

public class UpdateJson {
    @Key
    private int vercode;
    @Key
    private String vername;
    @Key
    private String download;
    @Key
    private String log;

    public int getVercode() {
        return vercode;
    }

    public void setVercode(int vercode) {
        this.vercode = vercode;
    }

    public String getVername() {
        return vername;
    }

    public void setVername(String vername) {
        this.vername = vername;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
