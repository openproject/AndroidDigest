package com.jayfeng.androiddigest.activity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jayfeng.androiddigest.R;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends ActionBarActivity {

    protected Toolbar toolbar;

    protected void showToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
