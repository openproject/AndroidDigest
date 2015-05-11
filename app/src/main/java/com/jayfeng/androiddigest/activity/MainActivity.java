package com.jayfeng.androiddigest.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.config.Config;
import com.jayfeng.androiddigest.fragment.TabBlogFragment;
import com.jayfeng.androiddigest.fragment.TabHomeFragment;
import com.jayfeng.androiddigest.fragment.TabToolFragment;
import com.jayfeng.androiddigest.service.HttpClientSpiceService;
import com.jayfeng.androiddigest.webservices.JsonRequest;
import com.jayfeng.androiddigest.webservices.json.UpdateJson;
import com.jayfeng.lesscode.core.UpdateLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.umeng.fb.FeedbackAgent;


public class MainActivity extends BaseActivity
        implements RadioButton.OnCheckedChangeListener, View.OnClickListener {

    private SpiceManager spiceManager = new SpiceManager(HttpClientSpiceService.class);

    private FragmentManager fragmentManager;

    private static final String TAG_HOME = "home";
    private static final String TAG_BLOG = "blog";
    private static final String TAG_TOOL = "tool";

    private RadioButton homeTabBtn;
    private RadioButton blogTabBtn;
    private RadioButton toolTabBtn;
    private Button moreTabBtn;

    private Fragment currentFragment;
    private Fragment homeFragment;
    private Fragment blogFragment;
    private Fragment toolFragment;

    FeedbackAgent agent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showToolbar();
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setSubtitle(R.string.app_description);
        toolbar.setSubtitleTextColor(getResources().getColor(android.R.color.white));

        //set navigationBar color for Android 5.0 or above
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.primary_dark));
        }

        //add this to optimize OverDraw
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        fragmentManager = getSupportFragmentManager();

        init();

        // receive the feedback notification
        agent = new FeedbackAgent(this);
        agent.sync();

        requestUpdateData();
    }

    private void init() {
        homeTabBtn = ViewLess.$(this, R.id.tab_home_btn);
        blogTabBtn = ViewLess.$(this, R.id.tab_blog_btn);
        toolTabBtn = ViewLess.$(this, R.id.tab_tool_btn);
        moreTabBtn = ViewLess.$(this, R.id.tab_more_btn);

        homeTabBtn.setOnCheckedChangeListener(this);
        blogTabBtn.setOnCheckedChangeListener(this);
        toolTabBtn.setOnCheckedChangeListener(this);
        moreTabBtn.setOnClickListener(this);

        currentFragment = homeFragment = new TabHomeFragment();
        blogFragment = new TabBlogFragment();
        toolFragment = new TabToolFragment();

        initFragment();
    }

    private void initFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragments, homeFragment, TAG_HOME).commit();
    }

    public void changeFrament(Fragment fragment, String fragmentTag) {

        if (fragment == currentFragment) {
            return;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (!fragment.isAdded()) {
            fragmentTransaction.hide(currentFragment).add(R.id.fragments, fragment, fragmentTag).commit();
        } else {
            fragmentTransaction.hide(currentFragment).show(fragment).commit();
        }
        currentFragment = fragment;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.tab_home_btn:
                    changeFrament(homeFragment, TAG_HOME);
                    break;
                case R.id.tab_blog_btn:
                    changeFrament(blogFragment, TAG_BLOG);
                    break;
                case R.id.tab_tool_btn:
                    changeFrament(toolFragment, TAG_TOOL);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_more_btn:
                popupMoreMenu();
                break;
            default:
                break;
        }
    }

    private void popupMoreMenu() {
        PopupMenu popup = new PopupMenu(this, moreTabBtn);
        popup.getMenuInflater().inflate(R.menu.more_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.more_menu_post:
                        agent.startFeedbackActivity();
                        break;
                    case R.id.more_menu_setting:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    protected boolean showNavigationIcon() {
        return false;
    }


    /*
     * =============================================================
     * check update
     * =============================================================
     */

    public void requestUpdateData() {
        JsonRequest<UpdateJson> request = new JsonRequest<>(UpdateJson.class);
        request.setUrl(Config.getCheckUpdateUrl());
        spiceManager.execute(request, new RequestListener<UpdateJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(UpdateJson updateJson) {
                UpdateLess.$check(MainActivity.this,
                        updateJson.getVercode(),
                        updateJson.getVername(),
                        updateJson.getDownload(),
                        updateJson.getLog());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // when turn on developer option "don't keep activity"
        // the fragments show and hide method will be invalid
        // and, remove the onSaveInstranceState can fix that
        // super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            Intent intent = new Intent(this, AddReviewDigestActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
