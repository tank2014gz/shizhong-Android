package com.shizhong.view.ui;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shizhong.view.ui.adapter.TopicListAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.PullToRefreshLayout.OnRefreshListener;
import com.shizhong.view.ui.bean.TopicBean;
import com.shizhong.view.ui.bean.TopicDataPackage;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuliyan on 16/1/29.
 */
public class ActivityTopicList extends BaseFragmentActivity implements View.OnClickListener {
	private ListView listView;
	private PullToRefreshLayout mPullToRefreshListView;
	private BaseAdapter mAdapter;
	private int nowPage = 1;// 现在要去请求第几页
	private final int recordNum = DebugConfig.BASE_PAGE_SIZE;// 每次请求条目
	private boolean isHasMore;// 是否还有更多数据
	private boolean isLoadMore;// 是否加载下一页数据
	private ArrayList<TopicBean> mDatas = new ArrayList<TopicBean>();
	private ProgressBar mLoadingProgress;

	private String loginToken; // 用户token
	private View mContentNullView;
	private TextView mNullText;
	private String getTopicListTag = "/topic/getTopics";

	@Override
	protected void initView() {
		setContentView(R.layout.activity_layout_crew_list);
		findViewById(R.id.apply_club).setVisibility(View.GONE);
		((TextView) findViewById(R.id.title_tv)).setText("话题");
		findViewById(R.id.left_bt).setOnClickListener(this);
		mContentNullView = findViewById(R.id.null_view);
		mNullText = (TextView) findViewById(R.id.tv_null_text);
		mNullText.setText("抱歉！搜不到更多话题");
		mLoadingProgress = (ProgressBar) findViewById(R.id.loading_dialog_progressBar);
		mAdapter = new TopicListAdapter(ActivityTopicList.this, mDatas);
		mPullToRefreshListView = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		listView = (ListView) findViewById(R.id.content_view);
		listView.setAdapter(mAdapter);
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
					requestData(pullToRefreshLayout, isLoadMore);
				} else {
					loadNoMore();
				}

			}
		});

		if (mDatas.size() <= 0) {
			loadAUTO();
		}
	}

	/**
	 * 主动请求网络数据
	 */
	private void loadAUTO() {
		isLoadMore = false;
		mPullToRefreshListView.autoRefresh();
	}

	public void requestData(final PullToRefreshLayout pullToRefreshLayout, final boolean isLoadMore) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", loginToken);
		params.put("nowPage", nowPage + "");
		params.put("recordNum", recordNum + "");
		LogUtils.e("获取话题列表", "-------");
		BaseHttpNetMananger.getInstance(ActivityTopicList.this).postJSON(ActivityTopicList.this, getTopicListTag,
				params, new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						mLoadingProgress.setVisibility(View.GONE);

						int code = 0;
						String msg = "";

						try {
							JSONObject root = new JSONObject(req);
							code = root.getInt("code");
							if (code != 100001) {
								if (code == 900001) {
									msg = root.getString("msg");
								}
								ToastUtils.showErrorToast(ActivityTopicList.this, code, msg, true);
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}

						TopicDataPackage dataPackage = GsonUtils.json2Bean(req, TopicDataPackage.class);

						List<TopicBean> list = dataPackage.data;

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
						mAdapter.notifyDataSetChanged();
						if (mDatas.size() > 0 && mContentNullView.getVisibility() == View.VISIBLE) {
							mContentNullView.setVisibility(View.GONE);
						}
						mMainHandler.postDelayed(new Runnable() {

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
						mLoadingProgress.setVisibility(View.GONE);

					}

					@Override
					public void requestNetExeption() {
						loadNoMore();
						mLoadingProgress.setVisibility(View.GONE);
					}
				}, false);
	}

	private void loadNoMore() {
		mMainHandler.postDelayed(new Runnable() {
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
	protected void initBundle() {
		loginToken = PrefUtils.getString(ActivityTopicList.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");

	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.left_bt:
			onBackPressed();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getTopicListTag);
		}
		if (mDatas != null) {
			mDatas.clear();
			mDatas = null;
		}
		if (mAdapter != null) {
			mAdapter = null;
		}
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView = null;
		}
		System.gc();
	}
}
