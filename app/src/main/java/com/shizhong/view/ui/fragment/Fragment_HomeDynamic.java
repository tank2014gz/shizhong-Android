package com.shizhong.view.ui.fragment;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.VideoPlayingListAdapter;
import com.shizhong.view.ui.base.BaseFragment;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.ACache;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.PullToRefreshLayout.OnRefreshListener;
import com.shizhong.view.ui.bean.BaseVideoBean;
import com.shizhong.view.ui.bean.BaseVideoList;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Fragment_HomeDynamic extends BaseFragment {
	private static final String TAG = Fragment_HomeDynamic.class.getSimpleName();
	private ListView listView;
	private PullToRefreshLayout mPullToRefreshListView;
	private View mContentNullView;
	private TextView mNullText;
	private VideoPlayingListAdapter mAdapter;
	private String loginToken; // 用户token
	private int nowPage = 1;// 现在要去请求第几页
	private final int recordNum = DebugConfig.BASE_PAGE_SIZE;// 每次请求条目
	private boolean isHasMore;// 是否还有更多数据
	private boolean isLoadMore;// 是否加载下一页数据
	private ArrayList<BaseVideoBean> mDatas = new ArrayList<BaseVideoBean>();

	private ACache mFirestPageCache;
    public  static  final String mDynamicCache="dynamicCache";

	private final String getVideoTag = "/video/getHomeDynamicVideos";
	private boolean isHasNewDate=true;
	private Handler mHandler = new Handler();


	public  void setHasNewDate(boolean hasNew){
		this.isHasNewDate=hasNew;
	}

	@Override
	public void initBundle() {
		loginToken = PrefUtils.getString(getActivity(), ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		mFirestPageCache=ACache.get(getActivity());
	}

	@Override
	public int initRootView() {
		// TODO Auto-generated method stub
		return R.layout.sz_listview_center_layout;
	}

	@Override
	public void initView() {

		// TODO Auto-generated method stub
		mContentNullView = findViewById(R.id.null_view);
		mNullText = (TextView) findViewById(R.id.tv_null_text);
		mNullText.setText("暂时没有关注好友，快去关注好友");

		mPullToRefreshListView = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		listView = (ListView) findViewById(R.id.content_view);
		mAdapter = new VideoPlayingListAdapter(getActivity(), mDatas);
		listView.setAdapter(mAdapter);


	}

	public void requestData(final PullToRefreshLayout pullToRefreshLayout, final boolean isLoadMore) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", loginToken);
		params.put("nowPage", nowPage + "");
		params.put("recordNum", recordNum + "");
		LogUtils.i("recommend", params.toString());
		LogUtils.e("获取动态视频列表信息", "-------");
		BaseHttpNetMananger.getInstance(getActivity()).postJSON(getActivity(), getVideoTag, params,
				new IRequestResult() {

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
								ToastUtils.showErrorToast(getActivity(), code, msg, true);
								loadNoMore();
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}

						BaseVideoList data = GsonUtils.json2Bean(req, BaseVideoList.class);
						if (data == null) {
							loadNoMore();
							return;
						}
						List<BaseVideoBean> list = data.data;
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
						if(nowPage==1){
							mFirestPageCache.put(mDynamicCache,req);
						}

						mDatas.addAll(list);
						mAdapter.notifyDataSetChanged();
						if (mContentNullView.getVisibility() == View.VISIBLE) {
							mContentNullView.setVisibility(View.GONE);
						}
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								if (isLoadMore) {
									pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
								} else {
									pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
								}

							}
						}, 1000);
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
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (isLoadMore) {
					mPullToRefreshListView.loadmoreFinish(PullToRefreshLayout.FAIL);
				} else {
					mPullToRefreshListView.refreshFinish(PullToRefreshLayout.FAIL);
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
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) {
			if (BaseHttpNetMananger.getRequstQueue() != null && getVideoTag != null) {
				BaseHttpNetMananger.getRequstQueue().cancelAll(getVideoTag);
			}
		}
	}

	@Override
	public void initData() {
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				isLoadMore = false;
				nowPage = 1;
				requestData(pullToRefreshLayout, isLoadMore);
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				isLoadMore = true;
				if (isHasMore) {
					nowPage++;
					requestData(pullToRefreshLayout, isLoadMore);
				} else {
					loadNoMore();
				}

			}
		});
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LogUtils.i(TAG, "Fragment_HomeDynamic+====onPause()");
		if (mAdapter != null) {
			mAdapter.onFragmentStop();
		}

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LogUtils.i(TAG, "Fragment_HomeDynamic+====onPause()");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (mAdapter != null) {
			mAdapter.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void refreshView() {
		if(mDatas.size()<=0){
			if(isHasNewDate){
			     mPullToRefreshListView.autoRefresh();
		   }else{
				String req=mFirestPageCache.getAsString(mDynamicCache);
				BaseVideoList data = GsonUtils.json2Bean(req, BaseVideoList.class);
				List<BaseVideoBean> list = data.data;
				mDatas.clear();
				mDatas.addAll(list);
				mAdapter.notifyDataSetChanged();
			}
		}
	}
}
