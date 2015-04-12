package com.jayfeng.androiddigest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jayfeng.androiddigest.fragment.DigestListFragment;
import com.jayfeng.androiddigest.fragment.ReviewDigestListFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

    public HomePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return new DigestListFragment();
        } else if (i == 1) {
            return new ReviewDigestListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
