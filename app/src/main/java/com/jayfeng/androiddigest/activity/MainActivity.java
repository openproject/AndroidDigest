package com.jayfeng.androiddigest.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.fragment.BlogFragment;
import com.jayfeng.androiddigest.fragment.HomeFragment;
import com.jayfeng.androiddigest.fragment.OfflineFragment;
import com.jayfeng.androiddigest.fragment.ToolFragment;
import com.jayfeng.lesscode.core.ViewLess;
import com.umeng.fb.FeedbackAgent;


public class MainActivity extends BaseActivity
        implements RadioButton.OnCheckedChangeListener, View.OnClickListener {

    private FragmentManager fragmentManager;

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

        fragmentManager = getSupportFragmentManager();

        init();

        // receive the feedback notification
        agent = new FeedbackAgent(this);
        agent.sync();
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

        currentFragment = homeFragment = new HomeFragment();
        blogFragment = new BlogFragment();
        toolFragment = new ToolFragment();

        initFragment();
    }

    private void initFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragments, homeFragment).commit();
    }

    public void changeFrament(Fragment fragment) {

        if (fragment == currentFragment) {
            return;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (!fragment.isAdded()) {
            fragmentTransaction.hide(currentFragment).add(R.id.fragments, fragment).commit();
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
                    changeFrament(homeFragment);
                    break;
                case R.id.tab_blog_btn:
                    changeFrament(blogFragment);
                    break;
                case R.id.tab_tool_btn:
                    changeFrament(toolFragment);
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
                if (item.getItemId() == R.id.more_menu_post) {
                    agent.startFeedbackActivity();
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
}
