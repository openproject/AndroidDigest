package com.jayfeng.androiddigest.config;

public class Config {

    /*
     * ========================================================================
     * App Config
     * ========================================================================
     */

    public static final int PAGE_SIZE = 15;
    public static final int PAGE_START = 1;

    public static final String JOKE_TYPE_TEXT = "text";
    public static final String JOKE_TYPE_HTML = "html";


    public static final String BLOG_TYPE_HEADER = "header";
    public static final String BLOG_TYPE_DIR = "dir";
    public static final String BLOG_TYPE_HTML = "html";

    public static final String OFFLINE_TYPE_DIR = "dir";
    public static final String OFFLINE_TYPE_HTML = "html";

    /*
     * ========================================================================
     * Web Config
     * ========================================================================
     */

    public static String sDomain = "http://www.yy317.com/";

    public static String getDigestList(int page, int size) {
        return sDomain + "android/list.php?page=" + page + "&size=" + size;
    }

    public static String getDigestDetailUrl(int id) {
        return sDomain + "android/detail.php?id=" + id;
    }

    public static String getBlogListUrl() {
        return sDomain + "android/blog/list.json";
    }

    public static String getOfflineListUrl() {
        return sDomain + "android/offline/list.json";
    }
}
