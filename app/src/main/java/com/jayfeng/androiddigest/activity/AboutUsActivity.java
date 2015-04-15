package com.jayfeng.androiddigest.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.jayfeng.androiddigest.R;
import com.jayfeng.lesscode.core.ViewLess;

/**
 * 关于我们
 * Created by Codywang on 2015/4/15.
 */
public class AboutUsActivity extends BaseActivity {
    private String versionName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        showToolbar();
        versionName = getIntent().getStringExtra("versionName");
        initView();
    }

    private void initView() {
        if (!TextUtils.isEmpty(versionName)) {
            TextView about_us_versionname = ViewLess.$(this, R.id.about_us_versionname);
            about_us_versionname.setText(getResources().getString(R.string.settings_update_description) + " " +versionName);
        }
        TextView about_us_githubpage = ViewLess.$(this, R.id.about_us_githubpage);
        about_us_githubpage.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
