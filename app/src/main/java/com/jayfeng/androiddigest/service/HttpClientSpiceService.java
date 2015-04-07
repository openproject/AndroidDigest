package com.jayfeng.androiddigest.service;

import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;

public class HttpClientSpiceService extends Jackson2GoogleHttpClientSpiceService {

    @Override
    public int getThreadCount() {
        return 4;
    }

}
