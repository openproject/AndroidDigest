package com.jayfeng.androiddigest.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.jayfeng.androiddigest.webservices.UpdateRequest;
import com.jayfeng.androiddigest.webservices.json.UpdateJson;
import com.jayfeng.lesscode.core.UpdateLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 设置页面
 * Created by Codywang on 2015/4/15.
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    public static final int MSG_CLEAR_CACHE_COMPLET = 100;
    private TextView settings_cachesize_tv;
    private SpiceManager spiceManager = new SpiceManager(HttpClientSpiceService.class);

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CLEAR_CACHE_COMPLET:
                    settings_cachesize_tv.setText(getResources().getString(R.string.settings_wipe_cache_description) + " " + getCacheSize(SettingsActivity.this));
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        showToolbar();
        toolbar.setSubtitleTextColor(getResources().getColor(android.R.color.white));
        initView();
    }

    private void initView() {
        LinearLayout settings_likeapp_ll = ViewLess.$(this, R.id.settings_ratingapp_ll);//喜欢app
        LinearLayout settings_updateapp_ll = ViewLess.$(this, R.id.settings_updateapp_ll);//更新app
        LinearLayout settings_wipecache_ll = ViewLess.$(this, R.id.settings_wipecache_ll);//清理缓存
        LinearLayout settings_aboutus_tv = ViewLess.$(this, R.id.settings_aboutus_tv);//关于我们
        TextView settings_currentversion_tv = ViewLess.$(this, R.id.settings_currentversion_tv);//当前版本
        settings_cachesize_tv = ViewLess.$(this, R.id.settings_cachesize_tv);//缓存大小

        settings_likeapp_ll.setOnClickListener(this);
        settings_updateapp_ll.setOnClickListener(this);
        settings_wipecache_ll.setOnClickListener(this);
        settings_aboutus_tv.setOnClickListener(this);

        settings_cachesize_tv.setText(getResources().getString(R.string.settings_wipe_cache_description) + " " + getCacheSize(this));
        settings_currentversion_tv.setText(getResources().getString(R.string.settings_update_description) + " " + getVersionName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_ratingapp_ll:
                ratingApp();
                //打分
                break;
            case R.id.settings_updateapp_ll:
                //更新
                requestUpdateData();
                break;
            case R.id.settings_wipecache_ll:
                wipeAppCahce();
                //清理缓存
                break;
            case R.id.settings_aboutus_tv:
                Intent intent = new Intent(this, AboutUsActivity.class);
                intent.putExtra("versionName", getVersionName());
                startActivity(intent);
                //关于我们
                break;
        }
    }

    /**
     * 清理应用缓存
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
        builder.setNegativeButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    /**
     * 给应用好评
     */
    private void ratingApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "亲，请先安装任一款应用市场！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 请求更新
     */
    private void requestUpdateData() {
        UpdateRequest request = new UpdateRequest();
        request.setUrl(Config.getCheckUpdateUrl());
        spiceManager.execute(request, new RequestListener<UpdateJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(UpdateJson updateJson) {
                UpdateLess.$check(SettingsActivity.this,
                        updateJson.getVercode(),
                        updateJson.getVername(),
                        updateJson.getDownload(),
                        updateJson.getLog());
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
                dirSize += getDirSize(file); // 如果遇到目录则通过递归调用继续统计
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

    private String getVersionName() {
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
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
