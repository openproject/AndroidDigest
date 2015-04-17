package com.jayfeng.androiddigest.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.config.Config;
import com.jayfeng.androiddigest.service.HttpClientSpiceService;
import com.jayfeng.androiddigest.webservices.JsonRequest;
import com.jayfeng.androiddigest.webservices.json.UpdateJson;
import com.jayfeng.lesscode.core.AppLess;
import com.jayfeng.lesscode.core.ToastLess;
import com.jayfeng.lesscode.core.UpdateLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Settings
 * Created by Codywang on 2015/4/15.
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    public static final int MSG_CLEAR_CACHE_COMPLET = 100;

    private SpiceManager spiceManager = new SpiceManager(HttpClientSpiceService.class);

    private TextView cacheSizeView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CLEAR_CACHE_COMPLET:
                    cacheSizeView.setText(getResources().getString(R.string.settings_wipe_cache_description) + " " + getCacheSize(SettingsActivity.this));
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        showToolbar();

        initView();
    }

    private void initView() {
        LinearLayout ratingAppContainer = ViewLess.$(this, R.id.ratingapp_container);
        LinearLayout updateAppContainer = ViewLess.$(this, R.id.updateapp_container);
        LinearLayout wipeCacheContainer = ViewLess.$(this, R.id.wipecache_container);
        LinearLayout aboutusContainer = ViewLess.$(this, R.id.aboutus_container);
        TextView currentVersionView = ViewLess.$(this, R.id.version_current);
        cacheSizeView = ViewLess.$(this, R.id.settings_cachesize_tv);

        ratingAppContainer.setOnClickListener(this);
        updateAppContainer.setOnClickListener(this);
        wipeCacheContainer.setOnClickListener(this);
        aboutusContainer.setOnClickListener(this);

        cacheSizeView.setText(getResources().getString(R.string.settings_wipe_cache_description) + " " + getCacheSize(this));
        currentVersionView.setText(getResources().getString(R.string.settings_update_description) + " " + AppLess.$vername(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ratingapp_container:
                ratingApp();
                break;
            case R.id.updateapp_container:
                requestUpdateData();
                break;
            case R.id.wipecache_container:
                wipeAppCahce();
                break;
            case R.id.aboutus_container:
                Intent intent = new Intent(this, AboutUsActivity.class);
                intent.putExtra("versionName", AppLess.$vername(this));
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * clean the cache
     */
    private void wipeAppCahce() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.settings_confirm_wipecache);
        builder.setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            deleteFile(SettingsActivity.this.getCacheDir());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(MSG_CLEAR_CACHE_COMPLET);
                    }
                }).start();
            }
        });
        builder.setNegativeButton(R.string.action_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    /**
     * open the app in market
     */
    private void ratingApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.settings_rating_no_market, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * check update
     */
    private void requestUpdateData() {
        JsonRequest<UpdateJson> request = new JsonRequest<>(UpdateJson.class);
        request.setUrl(Config.getCheckUpdateUrl());
        spiceManager.execute(request, new RequestListener<UpdateJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(UpdateJson updateJson) {
                boolean hasUpdate = UpdateLess.$check(SettingsActivity.this,
                        updateJson.getVercode(),
                        updateJson.getVername(),
                        updateJson.getDownload(),
                        updateJson.getLog());
                // if no update, toast a no update tips
                if (!hasUpdate) {
                    ToastLess.$(SettingsActivity.this, R.string.settings_update_no_update);
                }
            }
        });
    }

    private String getCacheSize(Context context) {
        File cacheDir = context.getCacheDir();
        double size = 0;
        long dirSize = getDirSize(cacheDir);
        size = (dirSize + 0.0) / (1024 * 1024);

        DecimalFormat df = new DecimalFormat("0.0");
        String filesize = df.format(size);
        return filesize + " M";
    }

    private long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file);
            }
        }
        return dirSize;
    }

    private void deleteFile(File file) throws Exception {
        if (file != null && file.exists() && file.isDirectory()) {
            File[] listFile = file.listFiles();
            if (listFile != null) {
                for (File file1 : listFile) {
                    if (file1.isDirectory()) {
                        deleteFile(file1);
                        file1.delete();
                    } else {
                        file1.delete();
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }
}
