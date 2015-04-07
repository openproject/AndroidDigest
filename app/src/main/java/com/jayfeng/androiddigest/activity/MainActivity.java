package com.jayfeng.androiddigest.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.jayfeng.lesscode.core.ViewLess;
import com.yy317.joke.R;
import com.yy317.joke.fragment.HomeFragment;
import com.yy317.joke.fragment.OfflineFragment;


public class MainActivity extends ActionBarActivity implements RadioButton.OnCheckedChangeListener {

    private Toolbar toolbar;
    private FragmentManager fragmentManager;

    private RadioButton homeTabBtn;
    private RadioButton weixinTabBtn;
    private RadioButton offlineTabBtn;

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
        weixinTabBtn = ViewLess.$(this, R.id.tab_weixin_btn);
        offlineTabBtn = ViewLess.$(this, R.id.tab_offline_btn);

        homeTabBtn.setOnCheckedChangeListener(this);
        weixinTabBtn.setOnCheckedChangeListener(this);
        offlineTabBtn.setOnCheckedChangeListener(this);

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
                case R.id.tab_weixin_btn:
                    changeFrament(offlineFragment);
                    break;
                case R.id.tab_offline_btn:
                    changeFrament(offlineFragment);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
