package com.jayfeng.androiddigest.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.fragment.DigestListFragment;
import com.jayfeng.androiddigest.fragment.ToolListFragment;
import com.jayfeng.lesscode.core.ViewLess;


public class SearchActivity extends BaseActivity
        implements RadioButton.OnCheckedChangeListener {

    private FragmentManager fragmentManager;

    private static final String TAG_DIGEST = "digest";
    private static final String TAG_TOOL = "tool";

    private RadioButton digestTabBtn;
    private RadioButton toolTabBtn;

    private Fragment currentFragment;
    private Fragment digestFragment;
    private Fragment toolFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        showToolbar();

        fragmentManager = getSupportFragmentManager();

        init();
    }

    private void init() {
        digestTabBtn = ViewLess.$(this, R.id.search_digest_btn);
        toolTabBtn = ViewLess.$(this, R.id.search_tool_btn);

        digestTabBtn.setOnCheckedChangeListener(this);
        toolTabBtn.setOnCheckedChangeListener(this);

        currentFragment = digestFragment = new DigestListFragment();
        toolFragment = new ToolListFragment();

        initFragment();
    }

    private void initFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragments, digestFragment, TAG_DIGEST).commit();
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
                case R.id.search_digest_btn:
                    changeFrament(digestFragment, TAG_DIGEST);
                    break;
                case R.id.search_tool_btn:
                    changeFrament(toolFragment, TAG_TOOL);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // when turn on developer option "don't keep activity"
        // the fragments show and hide method will be invalid
        // and, remove the onSaveInstranceState can fix that
        // super.onSaveInstanceState(outState);
    }
}
