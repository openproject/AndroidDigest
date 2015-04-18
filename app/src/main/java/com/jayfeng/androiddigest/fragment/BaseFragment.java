package com.jayfeng.androiddigest.fragment;

import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

public class BaseFragment extends Fragment {

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }
}
