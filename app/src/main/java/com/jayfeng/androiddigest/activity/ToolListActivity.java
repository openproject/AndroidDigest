package com.jayfeng.androiddigest.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.config.Config;
import com.jayfeng.androiddigest.service.HttpClientSpiceService;
import com.jayfeng.androiddigest.webservices.JsonRequest;
import com.jayfeng.androiddigest.webservices.json.ToolJson;
import com.jayfeng.androiddigest.webservices.json.ToolListJson;
import com.jayfeng.lesscode.core.AdapterLess;
import com.jayfeng.lesscode.core.EncodeLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class ToolListActivity extends BaseActivity {

    public static final String KEY_TYPE = "type";

    private SpiceManager spiceManager = new SpiceManager(HttpClientSpiceService.class);

    private ListView listView;
    private List<ToolJson> listData;
    private BaseAdapter adapter;

    private PtrClassicFrameLayout ptrFrame;
    private View errorView;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_list);

        showToolbar();

        type = getIntent().getStringExtra(KEY_TYPE);

        listView = ViewLess.$(this, R.id.listview);
        errorView = ViewLess.$(this, R.id.error);

        ptrFrame = ViewLess.$(this, R.id.fragment_rotate_header_with_listview_frame);
        ptrFrame.setLastUpdateTimeRelateObject(this);
        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                requestNetworkData();
                errorView.setVisibility(View.GONE);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, listView, header) ;
            }
        });

        showCacheData();
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.autoRefresh();
            }
        }, 100);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String type = listData.get(position).getType();
//                String url = listData.get(position).getUrl();
//                String title = listData.get(position).getTitle();
//                if (Config.OFFLINE_TYPE_DIR.equals(type)) {
//                    Intent intent = new Intent(ToolListActivity.this, OfflineActivity.class);
//                    intent.putExtra(OfflineActivity.KEY_URL, url);
//                    intent.putExtra(OfflineActivity.KEY_TITLE, title);
//                    startActivity(intent);
//                } else if (Config.OFFLINE_TYPE_HTML.equals(type)) {
//                    Intent intent = new Intent(ToolListActivity.this, WebViewActivity.class);
//                    intent.putExtra(WebViewActivity.KEY_URL, url);
//                    startActivity(intent);
//                }
            }
        });
    }

    /*
     * =============================================================
     * request list data
     * =============================================================
     */

    private void requestNetworkData() {
        JsonRequest<ToolListJson> request = new JsonRequest<>(ToolListJson.class);
        request.setUrl(getListUrl());
        spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request,
                getCacheKey(),
                DurationInMillis.NEVER, new RequestListener<ToolListJson>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        ptrFrame.refreshComplete();
                        ptrFrame.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (listView.getAdapter() == null || listView.getAdapter().getCount() == 0) {
                                    errorView.setVisibility(View.VISIBLE);
                                }
                            }
                        }, 400);
                    }

                    @Override
                    public void onRequestSuccess(ToolListJson toolListJson) {
                        fillAdapterToListView(toolListJson);
                        errorView.setVisibility(View.GONE);
                        ptrFrame.refreshComplete();
                    }
                });
    }

    private void showCacheData() {
        spiceManager.getFromCache(ToolListJson.class,
                getCacheKey(),
                DurationInMillis.ALWAYS_RETURNED, new RequestListener<ToolListJson>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                    }

                    @Override
                    public void onRequestSuccess(ToolListJson toolListJson) {
                        fillAdapterToListView(toolListJson);
                    }
                });
    }

    private void fillAdapterToListView(ToolListJson toolListJson) {
        if (toolListJson == null) {
            return;
        }
        listData = toolListJson;
        adapter = AdapterLess.$base(this,
                listData,
                R.layout.activity_tool_list_item,
                new AdapterLess.CallBack<ToolJson>() {
                    @Override
                    public View getView(int i, View view, AdapterLess.ViewHolder viewHolder, ToolJson toolJson) {
                        TextView titleView = viewHolder.$view(view, R.id.title);
                        TextView descriptionView = viewHolder.$view(view, R.id.description);

                        titleView.setText(toolJson.getTitle());
                        descriptionView.setText(toolJson.getDescription());
                        return view;
                    }
                });
        listView.setAdapter(adapter);
    }

    private String getListUrl() {
        return Config.getToolListUrl(type);
    }

    private String getCacheKey() {
        return EncodeLess.$md5(getListUrl());
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
