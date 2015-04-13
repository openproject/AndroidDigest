package com.jayfeng.androiddigest.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.config.Config;
import com.jayfeng.androiddigest.service.HttpClientSpiceService;
import com.jayfeng.androiddigest.webservices.ReviewDigestRequest;
import com.jayfeng.androiddigest.webservices.json.ReviewDigestJson;
import com.jayfeng.lesscode.core.ToastLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.HashMap;

public class AddReviewDigestActivity extends BaseActivity {

    private SpiceManager spiceManager = new SpiceManager(HttpClientSpiceService.class);

    private EditText titleView;
    private EditText abstractView;
    private EditText thumbnailView;
    private EditText urlView;
    private Button submitBtn;
    private EditText deliverView;
    private EditText reviewerView;

    private String title;
    private String abstracts;
    private String thumbnail;
    private String url;
    private String deliver;
    private String reviewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_digest_add);

        showToolbar();

        initView();
    }

    private void initView() {
        titleView = ViewLess.$(this, R.id.title);
        abstractView = ViewLess.$(this, R.id.abstracts);
        thumbnailView = ViewLess.$(this, R.id.thumbnail);
        urlView = ViewLess.$(this, R.id.url);
        submitBtn = ViewLess.$(this, R.id.submit);
        deliverView = ViewLess.$(this, R.id.deliver);
        reviewerView = ViewLess.$(this, R.id.reviewer);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        title = titleView.getText().toString();
        abstracts = abstractView.getText().toString();
        thumbnail = thumbnailView.getText().toString();
        url = urlView.getText().toString();
        deliver = deliverView.getText().toString();
        reviewer = reviewerView.getText().toString();

        HashMap<String, String> postParamters = new HashMap<>();
        postParamters.put("title", title);
        postParamters.put("abstract", abstracts);
        postParamters.put("thumbnail", thumbnail);
        postParamters.put("url", url);
        postParamters.put("deliver", deliver);
        postParamters.put("reviewer", reviewer);

        ReviewDigestRequest request = new ReviewDigestRequest();
        request.setUrl(Config.getAddReviewDigestUrl());
        request.setPostParameters(postParamters);
        spiceManager.execute(request, new RequestListener<ReviewDigestJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                ToastLess.$(AddReviewDigestActivity.this, "error:" + spiceException.toString());
            }

            @Override
            public void onRequestSuccess(ReviewDigestJson reviewDigestJson) {
                if ("OK".equals(reviewDigestJson.getS_status())) {
                    ToastLess.$(AddReviewDigestActivity.this, "success.");
                } else {

                    ToastLess.$(AddReviewDigestActivity.this, "faillure:" + reviewDigestJson.getS_message());
                }
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
