package com.jayfeng.androiddigest.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.config.Config;
import com.jayfeng.androiddigest.service.HttpClientSpiceService;
import com.jayfeng.androiddigest.webservices.JokeDetailRequest;
import com.jayfeng.androiddigest.webservices.json.DigestJson;
import com.jayfeng.lesscode.core.ViewLess;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class DigestDetailActivity extends ActionBarActivity {

    private SpiceManager spiceManager = new SpiceManager(HttpClientSpiceService.class);

    public static final String KEY_ID = "id";
    private int id = 0;

    private Toolbar toolbar;

    private TextView bodyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_detail);

        id = getIntent().getIntExtra(KEY_ID, 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        bodyView = ViewLess.$(this, R.id.body);

        requestNetworkData();
    }

    private void requestNetworkData() {
        JokeDetailRequest request = new JokeDetailRequest();
        request.setUrl(Config.getDigestDetailUrl(id));
        spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request,
                "joke_detail_id_" + id,
                DurationInMillis.NEVER, new RequestListener<DigestJson>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                    }

                    @Override
                    public void onRequestSuccess(DigestJson digestJson) {
                        bodyView.setText(digestJson.getContent());
                    }
                });
    }

    @Override
    public void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }

}
