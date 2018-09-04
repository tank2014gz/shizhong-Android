package com.shizhong.view.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shizhong.view.ui.adapter.BannerHolderView;
import com.shizhong.view.ui.adapter.EventsAndNewsAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.net.JiequHttpRequest;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.PullToRefreshLayout.OnRefreshListener;
import com.shizhong.view.ui.base.view.convenientbanner.ConvenientBanner;
import com.shizhong.view.ui.base.view.convenientbanner.holder.CBViewHolderCreator;
import com.shizhong.view.ui.bean.BannerBean;
import com.shizhong.view.ui.bean.EventsNewsBean;
import com.shizhong.view.ui.bean.EventsNewsDataPackage;
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
public class ActivityEvenetsAndNews extends BaseFragmentActivity implements View.OnClickListener {

	private ListView listView;
	private PullToRefreshLayout mPullToRefreshListView;
	private BaseAdapter mAdapter;
	private boolean isHasMore;// 是否还有更多数据
	private boolean isLoadMore;// 是否加载下一页数据
	private int nowPage = 1;// 现在要去请求第几页
	private final int recordNum = DebugConfig.BASE_PAGE_SIZE;// 每次请求条目
	private View mBannerLayout;
	private String login_token;
	private List<BannerBean> bannerBeanList = new ArrayList<BannerBean>();
	@SuppressWarnings("rawtypes")
	private ConvenientBanner mConvenientBanner;
	private String type = "0";
	private ProgressBar mLoadingProgress;

	private List<EventsNewsBean> mDatas = new ArrayList<EventsNewsBean>();
	private View mContentNullView;
	private TextView mNullText;


	@Override
	protected void initBundle() {
		login_token = PrefUtils.getString(ActivityEvenetsAndNews.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void initView() {
		setContentView(R.layout.activity_layout_crew_list);
		findViewById(R.id.apply_club).setVisibility(View.GONE);
		((TextView) findViewById(R.id.title_tv)).setText("赛事资讯");
		findViewById(R.id.left_bt).setOnClickListener(this);




		mBannerLayout = LayoutInflater.from(ActivityEvenetsAndNews.this).inflate(R.layout.fragment_jiqu_banner_layout,
				null);
		mLoadingProgress = (ProgressBar) findViewById(R.id.loading_dialog_progressBar);
		mContentNullView = findViewById(R.id.null_view);
		mNullText = (TextView) findViewById(R.id.tv_null_text);
		mNullText.setText("抱歉！搜不到更多资讯内容");
		mConvenientBanner = (ConvenientBanner) mBannerLayout.findViewById(R.id.convenient_banner);
		mPullToRefreshListView = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		listView = (ListView) findViewById(R.id.content_view);
		listView.addHeaderView(mBannerLayout);
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
		mAdapter = new EventsAndNewsAdapter(ActivityEvenetsAndNews.this, mDatas);
		listView.setAdapter(mAdapter);

		if (mDatas.size() <= 0) {
			loadAUTO();
		}
		JiequHttpRequest.requestBanner(ActivityEvenetsAndNews.this, login_token, "1",
				new JiequHttpRequest.HttpRequstBannerCallBack() {
					@Override
					public void callback(String req, final List<BannerBean> list) {
						if (list == null || list.size() <= 0) {
							mConvenientBanner.setVisibility(View.GONE);
						} else {
							mConvenientBanner.setVisibility(View.VISIBLE);
						}

						bannerBeanList = list;
						LogUtils.i("banner", bannerBeanList.toString());
						mMainHandler.post(new Runnable() {
							@SuppressWarnings("unchecked")
							@Override
							public void run() {
								mConvenientBanner.setPages(new CBViewHolderCreator() {
									@Override
									public Object createHolder() {
										return new BannerHolderView(ActivityEvenetsAndNews.this, bannerBeanList);
									}
								}, bannerBeanList).setPageIndicator(new int[] { R.drawable.ic_page_indicator,
										R.drawable.ic_page_indicator_focused });
							}
						});
					}

					@Override
					public void callbackFail() {
						mConvenientBanner.setVisibility(View.GONE);

					}
				});

	}

	/**
	 * 主动请求网络数据
	 */
	private void loadAUTO() {
		isLoadMore = false;
		mPullToRefreshListView.autoRefresh();
	}

	public void requestData(final PullToRefreshLayout pullToRefreshLayout, final boolean isLoadMore) {
		String rootURL = "/news/getList";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("nowPage", nowPage + "");
		params.put("recordNum", recordNum + "");
		params.put("type", type);
		LogUtils.e("修改资讯列表", "-------");
		BaseHttpNetMananger.getInstance(ActivityEvenetsAndNews.this).postJSON(ActivityEvenetsAndNews.this, rootURL,
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
								ToastUtils.showErrorToast(ActivityEvenetsAndNews.this, code, msg, true);
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
						mLoadingProgress.setVisibility(View.GONE);
						loadNoMore();

					}

					@Override
					public void requestNetExeption() {
						mLoadingProgress.setVisibility(View.GONE);
						loadNoMore();
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
	protected void initData() {

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.left_bt:
			finish();
			break;
		}
	}

}
