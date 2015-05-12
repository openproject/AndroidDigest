package com.jayfeng.androiddigest.fragment;


import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.activity.DigestDetailActivity;
import com.jayfeng.androiddigest.activity.SearchActivity;
import com.jayfeng.androiddigest.activity.WebViewActivity;
import com.jayfeng.androiddigest.config.Config;
import com.jayfeng.androiddigest.listener.Searchable;
import com.jayfeng.androiddigest.service.HttpClientSpiceService;
import com.jayfeng.androiddigest.webservices.JsonRequest;
import com.jayfeng.androiddigest.webservices.json.DigestJson;
import com.jayfeng.androiddigest.webservices.json.DigestListJson;
import com.jayfeng.lesscode.core.AdapterLess;
import com.jayfeng.lesscode.core.DisplayLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class DigestListFragment extends BaseFragment implements OnScrollListener, Searchable {

    private static final int CONTEXT_ITEM_OPEN_IN_BROWSER = 0;

    private SpiceManager spiceManager = new SpiceManager(HttpClientSpiceService.class);

    private ListView listView;
    private List<DigestJson> listData;
    private BaseAdapter adapter;

    private PtrClassicFrameLayout ptrFrame;
    private View errorView;
    private View footerView;
    private int visibleLastIndex;
    private boolean noMoreData = false;

    private int page = Config.PAGE_START;
    private int size = Config.PAGE_SIZE;

    private boolean isSearch = false;
    private String searchKey;

    public DigestListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null
                &&SearchActivity.TYPE_SEARCH.equals(getArguments().getString(SearchActivity.KEY_TYPE))) {
            isSearch = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_digest, container, false);
        listView = ViewLess.$(contentView, R.id.listview);
        errorView = ViewLess.$(contentView, R.id.error);
        footerView = LayoutInflater.from(getActivity())
                .inflate(R.layout.common_list_footer, null, false);
        listView.addFooterView(footerView);

        listView.setOnScrollListener(this);

        ptrFrame = ViewLess.$(contentView, R.id.fragment_rotate_header_with_listview_frame);
        ptrFrame.setLastUpdateTimeRelateObject(this);
        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                resetPage();
                requestNetworkData();
                errorView.setVisibility(View.GONE);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, listView, header);
            }
        });

        registerForContextMenu(listView);
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isSearch) {
            showCacheData();
            ptrFrame.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ptrFrame.autoRefresh();
                }
            }, 100);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DigestJson digestJson = listData.get(position);
                String type = digestJson.getType();
                if (Config.DIGEST_TYPE_HTML.equals(type)) {
                    String url = digestJson.getUrl();
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra(WebViewActivity.KEY_URL, url);
                    startActivity(intent);
                } else {
                    // default type text
                    int detailId = digestJson.getId();
                    Intent intent = new Intent(getActivity(), DigestDetailActivity.class);
                    intent.putExtra(DigestDetailActivity.KEY_ID, detailId);
                    startActivity(intent);
                }
            }
        });
    }

    /*
     * =============================================================
     * first page data
     * =============================================================
     */

    private void requestNetworkData() {
        JsonRequest<DigestListJson> request = new JsonRequest<>(DigestListJson.class);
        request.setUrl(getUrl(page, size));
        spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request,
                getCacheKey(page, size),
                DurationInMillis.NEVER, new RequestListener<DigestListJson>() {
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
                    public void onRequestSuccess(DigestListJson digestListJson) {
                        fillAdapterToListView(digestListJson);
                        errorView.setVisibility(View.GONE);
                        ptrFrame.refreshComplete();
                    }
                });
    }

    private void showCacheData() {
        spiceManager.getFromCache(DigestListJson.class,
                getCacheKey(page, size),
                DurationInMillis.ALWAYS_RETURNED, new RequestListener<DigestListJson>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                    }

                    @Override
                    public void onRequestSuccess(DigestListJson digestListJson) {
                        fillAdapterToListView(digestListJson);
                        resetPage();
                    }
                });
    }

    private void fillAdapterToListView(DigestListJson digestListJson) {
        if (digestListJson == null) {
            return;
        }
        listData = digestListJson;
        adapter = AdapterLess.$base(getActivity(),
                listData,
                R.layout.fragment_digest_list_item,
                new AdapterLess.CallBack<DigestJson>() {
                    @Override
                    public View getView(int i, View view, AdapterLess.ViewHolder viewHolder, DigestJson digestJson) {
                        TextView titleView = viewHolder.$view(view, R.id.title);
                        TextView abstractView = viewHolder.$view(view, R.id.abstracts);
                        final SimpleDraweeView draweeView = viewHolder.$view(view,R.id.thumbnail);
                        ImageView moreView = viewHolder.$view(view, R.id.more);

                        if (TextUtils.isEmpty(digestJson.getTitle())) {
                            titleView.setVisibility(View.GONE);
                        } else {
                            titleView.setText(digestJson.getTitle());
                            TextPaint titlePaint = titleView.getPaint();
                            titlePaint.setFakeBoldText(true);
                            titleView.setVisibility(View.VISIBLE);
                        }

                        if (TextUtils.isEmpty(digestJson.getAbstractStr())) {
                            abstractView.setVisibility(View.GONE);
                        } else {
                            abstractView.setText(digestJson.getAbstractStr());
                            abstractView.setVisibility(View.VISIBLE);
                        }

                        if (!TextUtils.isEmpty(digestJson.getThumbnail())) {
                            Uri uri = Uri.parse(digestJson.getThumbnail());
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

                        moreView.setVisibility(digestJson.getMore() > 0 ? View.VISIBLE : View.GONE);
                        return view;
                    }
                });
        listView.setAdapter(adapter);
        if (listData.size() < Config.PAGE_SIZE) {
            if (listView.getFooterViewsCount() > 0) {
                listView.removeFooterView(footerView);
            }
            noMoreData = true;
        }
    }

    /*
     * =============================================================
     * more page data
     * =============================================================
     */

    private void moreNetworkData() {
        int nextPage = page + 1;
        JsonRequest<DigestListJson> request = new JsonRequest<>(DigestListJson.class);
        request.setUrl(getUrl(nextPage, size));
        spiceManager.execute(request, new RequestListener<DigestListJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
            }

            @Override
            public void onRequestSuccess(DigestListJson digestListJson) {
                moreAdapterToListView(digestListJson);
            }
        });
    }

    private void moreAdapterToListView(DigestListJson digestListJson) {
        if (digestListJson == null) {
            return;
        }
        listData.addAll(digestListJson);
        if (digestListJson.size() < Config.PAGE_SIZE) {
            if (listView.getFooterViewsCount() > 0) {
                listView.removeFooterView(footerView);
            }
            noMoreData = true;
        }
        adapter.notifyDataSetChanged();
        page++;
    }

    private void resetPage() {
        page = Config.PAGE_START;
        size = Config.PAGE_SIZE;
        noMoreData = false;
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(footerView);
        }
    }

    /*
     * =============================================================
     * search page data
     * =============================================================
     */

    @Override
    public void search(String key) {
        searchKey = key;

        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.autoRefresh();
            }
        }, 100);
    }

    private String getUrl(int page, int size) {
        if (isSearch) {
            return Config.getSearchDigestListUrl(searchKey, page, size);
        }
        return Config.getDigestListUrl(page, size);
    }

    private String getCacheKey(int page, int size) {
        return (isSearch ? "search_" + searchKey + "_" : "") + "digest_list_page_" + page + "_size_" + size;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int lastIndex = adapter.getCount();
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && visibleLastIndex == lastIndex
                && !noMoreData) {
            moreNetworkData();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
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
                DigestJson digestJson = listData.get(menuInfo.position);
                String type = digestJson.getType();
                if (Config.DIGEST_TYPE_HTML.equals(type)) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(digestJson.getUrl());
                    intent.setData(content_url);
                    startActivity(intent);
                }
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onStart() {
        spiceManager.start(getActivity());
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
