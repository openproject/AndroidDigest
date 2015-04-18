package com.jayfeng.androiddigest.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.adapter.HomePagerAdapter;
import com.jayfeng.lesscode.core.ViewLess;

public class TabHomeFragment extends BaseFragment {

    private ViewPager viewPager;
    private HomePagerAdapter pagerAdapter;

    public TabHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = ViewLess.$(contentView, R.id.viewpager);
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }

}
