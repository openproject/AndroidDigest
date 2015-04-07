package com.jayfeng.androiddigest.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.fragment.HomeFragment;
import com.jayfeng.androiddigest.fragment.OfflineFragment;
import com.jayfeng.lesscode.core.ViewLess;


public class MainActivity extends ActionBarActivity implements RadioButton.OnCheckedChangeListener {

    private Toolbar toolbar;
    private FragmentManager fragmentManager;

    private RadioButton homeTabBtn;
    private RadioButton blogTabBtn;
    private RadioButton toolTabBtn;

    private Fragment currentFragment;
    private Fragment homeFragment;
    private Fragment offlineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        init();
    }

    private void init() {
        homeTabBtn = ViewLess.$(this, R.id.tab_home_btn);
        blogTabBtn = ViewLess.$(this, R.id.tab_blog_btn);
        toolTabBtn = ViewLess.$(this, R.id.tab_tool_btn);

        homeTabBtn.setOnCheckedChangeListener(this);
        blogTabBtn.setOnCheckedChangeListener(this);
        toolTabBtn.setOnCheckedChangeListener(this);

        currentFragment = homeFragment = new HomeFragment();
        offlineFragment = new OfflineFragment();

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
                    changeFrament(offlineFragment);
                    break;
                case R.id.tab_tool_btn:
                    changeFrament(offlineFragment);
                    break;
                default:
                    break;
            }
        }
    }

}
