package com.jayfeng.androiddigest;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jayfeng.lesscode.core.$;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;

public class MyApp extends Application {

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        MyApp application = (MyApp) context.getApplicationContext();
        return application.refWatcher;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        refWatcher = LeakCanary.install(this);

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
