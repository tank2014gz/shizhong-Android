package com.shizhong.view.ui.newapi;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.BannerHolderView;
import com.shizhong.view.ui.adapter.EventsAndNewsAAdapter;
import com.shizhong.view.ui.base.BaseUmengActivity;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.net.JiequHttpRequest;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.DividerItemDecoration;
import com.shizhong.view.ui.base.view.convenientbanner.ConvenientBanner;
import com.shizhong.view.ui.base.view.convenientbanner.holder.CBViewHolderCreator;
import com.shizhong.view.ui.bean.BannerBean;
import com.shizhong.view.ui.bean.EventsNewsBean;
import com.shizhong.view.ui.bean.EventsNewsDataPackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

/**
 * Created by yuliyan on 16/8/6.
 */
public class ActivityEventsAndNews  extends BaseUmengActivity implements View.OnClickListener{

    private boolean isHasMore;// 是否还有更多数据
    private boolean isLoadMore;// 是否加载下一页数据
    private int nowPage = 1;// 现在要去请求第几页
    private final int recordNum = 10;// 每次请求条目


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    private EventsAndNewsAAdapter adapter;
    private List<EventsNewsBean> mDatas = new ArrayList<EventsNewsBean>();
    private View mContentNullView;
    private TextView mNullText;
    private String login_token;
    private String type = "0";
    private int lastVisibleItem;


    @Override
    protected void initBundle() {
        login_token = PrefUtils.getString(ActivityEventsAndNews.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
    }


    @Override
    protected void initView() {
        setContentView(R.layout.newapi_activity_event_and_news_layout);
        ((TextView) findViewById(R.id.title_tv)).setText("赛事资讯");
        mContentNullView = findViewById(R.id.null_view);
        mNullText = (TextView) findViewById(R.id.tv_null_text);
        mNullText.setText("抱歉！搜不到更多资讯内容");
        findViewById(R.id.left_bt).setOnClickListener(this);
        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mRecyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.sz_yellow);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.black_2c2c2c);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,OrientationHelper.VERTICAL));
        adapter=new EventsAndNewsAAdapter(this,mDatas);
        mRecyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoadMore = false;
                nowPage = 1;
                requestData(mSwipeRefreshLayout, isLoadMore);
            }
        });

        if(!mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(true);
        }
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1==adapter.getItemCount()) {
                    isLoadMore=true;
                   if(isHasMore) {
                       adapter.changeMoreStatus(adapter.LOADING_MORE);
                       nowPage++;
                       requestData(mSwipeRefreshLayout, isLoadMore);
                   }else{
                       adapter.changeMoreStatus(adapter.LOADING_MORE);
                       loadNoMore();
                   }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView,dx, dy);
                lastVisibleItem =linearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }




    public void requestData(final SwipeRefreshLayout pullToRefreshLayout, final boolean isLoadMore) {
        String rootURL = "/news/getList";
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", login_token);
        params.put("nowPage", nowPage + "");
        params.put("recordNum", recordNum + "");
        params.put("type", type);
        LogUtils.e("修改资讯列表", "-------");
        BaseHttpNetMananger.getInstance(ActivityEventsAndNews.this).postJSON(ActivityEventsAndNews.this, rootURL,
                params, new IRequestResult() {

                    @Override
                    public void requestSuccess(String req) {
                        int code = 0;
                        String msg = "";

                        try {
                            JSONObject root = new JSONObject(req);
                            code = root.getInt("code");
                            if (code != 100001) {
                                if (code == 900001) {
                                    msg = root.getString("msg");
                                }
                                ToastUtils.showErrorToast(ActivityEventsAndNews.this, code, msg, true);
                                loadNoMore();
                                return;
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            return;
                        }

                        EventsNewsDataPackage dataPackage = GsonUtils.json2Bean(req, EventsNewsDataPackage.class);
                        List<EventsNewsBean> list = dataPackage.data;

                        if (list == null || list.size() <= 0) {
                            loadNoMore();
                            return;
                        }

                        if (list.size() < recordNum) {
                            isHasMore = false;
                        } else {
                            isHasMore = true;
                        }

                        if (!isLoadMore) {
                            mDatas.clear();
                        }
                        nowPage++;
                        mDatas.addAll(list);
                        adapter.notifyDataSetChanged();

                        if (mDatas.size() > 0 && mContentNullView.getVisibility() == View.VISIBLE) {
                            mContentNullView.setVisibility(View.GONE);
                        }
                        mMainHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (isLoadMore) {
                                    adapter.changeMoreStatus(adapter.PULLUP_LOAD_MORE);
                                } else {
                                    pullToRefreshLayout.setRefreshing(false);

                                }

                            }
                        }, 300);
                    }

                    @Override
                    public void requestFail() {
                        loadNoMore();

                    }

                    @Override
                    public void requestNetExeption() {
                        loadNoMore();
                    }
                }, false);
    }

    private void loadNoMore() {
        mMainHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isLoadMore) {
                    LogUtils.e("shizhong","set load no more");
                    adapter.changeMoreStatus(adapter.LOAD_NO_MORE);
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);

                }
                if (mDatas.size() <= 0) {
                    mContentNullView.setVisibility(View.VISIBLE);
                } else {
                    mContentNullView.setVisibility(View.GONE);
                }
            }
        }, 1000);
    }


    @Override
    protected void initData() {
        isLoadMore = false;
        nowPage = 1;
        requestData(mSwipeRefreshLayout, false );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.left_bt:
                onBackPressed();
                break;
        }
    }
}
