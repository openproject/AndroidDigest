package com.jayfeng.androiddigest.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.fragment.DigestListFragment;
import com.jayfeng.androiddigest.fragment.ToolListFragment;
import com.jayfeng.androiddigest.listener.Searchable;
import com.jayfeng.lesscode.core.ViewLess;


public class SearchActivity extends BaseActivity
        implements RadioButton.OnCheckedChangeListener {

    public static final String KEY_TYPE = "type";
    public static final String TYPE_SEARCH = "search";

    private static final String TAG_DIGEST = "digest";
    private static final String TAG_TOOL = "tool";

    private FragmentManager fragmentManager;

    private EditText searchEdit;
    private ImageView searchIcon;
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
        searchEdit = ViewLess.$(this, R.id.search_edit);
        searchIcon = ViewLess.$(this, R.id.search_icon);
        digestTabBtn = ViewLess.$(this, R.id.search_digest_btn);
        toolTabBtn = ViewLess.$(this, R.id.search_tool_btn);

        digestTabBtn.setOnCheckedChangeListener(this);
        toolTabBtn.setOnCheckedChangeListener(this);

        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE, TYPE_SEARCH);
        currentFragment = digestFragment = new DigestListFragment();
        toolFragment = new ToolListFragment();
        digestFragment.setArguments(bundle);
        toolFragment.setArguments(bundle);

        initFragment();

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = searchEdit.getText().toString();
                if (digestTabBtn.isChecked()) {
                    ((Searchable)digestFragment).search(key);
                } else if (toolTabBtn.isChecked()) {
                    ((Searchable)toolFragment).search(key);
                }
            }
        });
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
