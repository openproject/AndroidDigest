package com.jayfeng.androiddigest;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jayfeng.lesscode.core.$;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);

        $.getInstance()
                .context(getApplicationContext())
                .log(BuildConfig.DEBUG, "Digest")
                .update(null, 3)
                .build();
    }
}
