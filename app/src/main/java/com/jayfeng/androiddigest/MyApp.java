package com.jayfeng.androiddigest;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jayfeng.lesscode.core.$;
import com.umeng.analytics.MobclickAgent;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // umeng analysis
        MobclickAgent.updateOnlineConfig(getApplicationContext());
        MobclickAgent.openActivityDurationTrack(false);

        Fresco.initialize(this);

        // lesscode config
        $.getInstance()
                .context(getApplicationContext())
                .log(BuildConfig.DEBUG, "Digest")
                .update(null, 3)
                .build();


    }
}
