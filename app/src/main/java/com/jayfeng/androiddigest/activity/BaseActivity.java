package com.jayfeng.androiddigest.activity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.jayfeng.androiddigest.R;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends ActionBarActivity {

    protected Toolbar toolbar;

    protected void showToolbar() {
        showToolbar(null);
    }

    protected void showToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (!TextUtils.isEmpty(title)) {
            toolbar.setTitle(title);
        }
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        if (showNavigationIcon()) {
            toolbar.setNavigationIcon(R.mipmap.arrow_left);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    protected boolean showNavigationIcon() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
