package com.jayfeng.androiddigest.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.UpdateLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jayfeng.androiddigest.R;
import com.jayfeng.androiddigest.activity.DigestDetailActivity;
import com.jayfeng.androiddigest.activity.WebViewActivity;
import com.jayfeng.androiddigest.config.Config;
import com.jayfeng.androiddigest.service.HttpClientSpiceService;
import com.jayfeng.androiddigest.webservices.DigestListRequest;
import com.jayfeng.androiddigest.webservices.UpdateRequest;
import com.jayfeng.androiddigest.webservices.json.DigestJson;
import com.jayfeng.androiddigest.webservices.json.DigestListJson;
import com.jayfeng.androiddigest.webservices.json.UpdateJson;
import com.jayfeng.lesscode.core.AdapterLess;
import com.jayfeng.lesscode.core.LogLess;
import com.jayfeng.lesscode.core.UpdateLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class HomeFragment extends Fragment implements OnScrollListener {

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

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestUpdateData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_home, container, false);
        listView = ViewLess.$(contentView, R.id.listview);
        errorView = ViewLess.$(contentView, R.id.error);
        footerView = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_home_list_footer, null, false);
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
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, listView, header) ;
            }
        });

        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                DigestJson digestJson = listData.get(position);
                String type = digestJson.getType();
                if (Config.JOKE_TYPE_HTML.equals(type)) {
                    String url = digestJson.getUrl();
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
//                    url = "http://www.baidu.com";
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
        DigestListRequest request = new DigestListRequest();
        request.setUrl(Config.getDigestList(page, size));
        spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request,
                "digest_list_page_" + page + "_size_" + size,
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
                "digest_list_page_" + page + "_size_" + size,
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
                R.layout.fragment_home_list_item,
                new AdapterLess.CallBack<DigestJson>() {
                    @Override
                    public View getView(int i, View view, AdapterLess.ViewHolder viewHolder, DigestJson digestJson) {
                        TextView titleView = viewHolder.$view(view, R.id.title);
                        TextView abstractView = viewHolder.$view(view, R.id.abstracts);
                        SimpleDraweeView draweeView = viewHolder.$view(view,R.id.thumbnail);
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
                            DraweeController controller = Fresco.newDraweeControllerBuilder()
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
    }

    /*
     * =============================================================
     * more page data
     * =============================================================
     */

    private void moreNetworkData() {
        int nextPage = page + 1;
        DigestListRequest request = new DigestListRequest();
        request.setUrl(Config.getDigestList(nextPage, size));
        spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request,
                "digest_list_page_" + nextPage + "_size_" + size,
                DurationInMillis.NEVER, new RequestListener<DigestListJson>() {
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

    /*
     * =============================================================
     * check update
     * =============================================================
     */

    public void requestUpdateData() {
        UpdateRequest request = new UpdateRequest();
        request.setUrl(Config.getCheckUpdateUrl());
        spiceManager.execute(request, new RequestListener<UpdateJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(UpdateJson updateJson) {
                UpdateLess.$check(getActivity(),
                        updateJson.getVercode(),
                        updateJson.getVername(),
                        updateJson.getDownload(),
                        updateJson.getLog());
            }
        });
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
