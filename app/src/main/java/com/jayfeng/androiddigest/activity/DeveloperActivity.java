package com.jayfeng.androiddigest.activity;

import android.os.Bundle;

import com.jayfeng.androiddigest.R;

public class DeveloperActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        showToolbar();
    }
}
