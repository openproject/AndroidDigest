package com.jayfeng.androiddigest.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.jayfeng.androiddigest.R;

public class OfflineActivity extends BaseActivity {

    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";

    private String toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        showToolbar();

        toolbarTitle = getIntent().getStringExtra(KEY_TITLE);
        if (!TextUtils.isEmpty(toolbarTitle)) {
            setTitle(toolbarTitle);
        }
    }
}
