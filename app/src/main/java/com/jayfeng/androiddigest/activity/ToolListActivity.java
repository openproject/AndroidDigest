package com.jayfeng.androiddigest.activity;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.config.Config;
import com.jayfeng.androiddigest.service.HttpClientSpiceService;
import com.jayfeng.androiddigest.webservices.JsonRequest;
import com.jayfeng.androiddigest.webservices.json.OfflineJson;
import com.jayfeng.androiddigest.webservices.json.ToolJson;
import com.jayfeng.androiddigest.webservices.json.ToolListJson;
import com.jayfeng.lesscode.core.AdapterLess;
import com.jayfeng.lesscode.core.DisplayLess;
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

    private static final int CONTEXT_ITEM_OPEN_IN_BROWSER = 0;

    public static final String KEY_TITLE = "title";
    public static final String KEY_TYPE = "type";

    private SpiceManager spiceManager = new SpiceManager(HttpClientSpiceService.class);

    private ListView listView;
    private List<ToolJson> listData;
    private BaseAdapter adapter;

    private PtrClassicFrameLayout ptrFrame;
    private View errorView;

    private String title;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_list);

        showToolbar();

        title = getIntent().getStringExtra(KEY_TITLE);
        type = getIntent().getStringExtra(KEY_TYPE);
        if (title != null) {
            setTitle(title);
        }

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
                if (position < 0 || position >= listData.size()) {
                    return;
                }
                String url = listData.get(position).getHomepage();
                if (TextUtils.isEmpty(url)) {
                    url = listData.get(position).getUrl();
                }
                Intent intent = new Intent(ToolListActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.KEY_URL, url);
                startActivity(intent);
            }
        });
        registerForContextMenu(listView);
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
                        final SimpleDraweeView draweeView = viewHolder.$view(view,R.id.thumbnail);

                        titleView.setText(toolJson.getTitle());
                        descriptionView.setText(toolJson.getDescription());
                        if (!TextUtils.isEmpty(toolJson.getThumbnail())) {
                            Uri uri = Uri.parse(toolJson.getThumbnail());
                            ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
                                @Override
                                public void onFinalImageSet(
                                        String id,
                                        @Nullable ImageInfo imageInfo,
                                        @Nullable Animatable anim) {
                                    if (imageInfo.getWidth() > imageInfo.getHeight()) {
                                        draweeView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                                    } else {
                                        draweeView.getLayoutParams().width = DisplayLess.$dp2px(200);
                                    }
                                    draweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
                                }

                                @Override
                                public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                                    if (imageInfo.getWidth() > imageInfo.getHeight()) {
                                        draweeView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                                    } else {
                                        draweeView.getLayoutParams().width = DisplayLess.$dp2px(200);
                                    }
                                    draweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
                                }

                                @Override
                                public void onFailure(String id, Throwable throwable) {
                                }
                            };
                            DraweeController controller = Fresco.newDraweeControllerBuilder()
                                    .setControllerListener(controllerListener)
                                    .setUri(uri)
                                    .setAutoPlayAnimations(true)
                                    .build();
                            draweeView.setController(controller);
                            draweeView.setVisibility(View.VISIBLE);
                        } else {
                            draweeView.setVisibility(View.GONE);
                        }
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("More");
        menu.add(0, CONTEXT_ITEM_OPEN_IN_BROWSER, 0, "Open in browser");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case CONTEXT_ITEM_OPEN_IN_BROWSER:
                ToolJson toolJson = listData.get(menuInfo.position);
                String url = toolJson.getHomepage();
                if (TextUtils.isEmpty(url)) {
                    url = toolJson.getUrl();
                }
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
                return true;
        }
        return super.onContextItemSelected(item);
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
