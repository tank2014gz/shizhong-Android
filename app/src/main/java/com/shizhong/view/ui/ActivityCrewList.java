package com.shizhong.view.ui;

import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shizhong.view.ui.adapter.CrewListAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.PullToRefreshLayout.OnRefreshListener;
import com.shizhong.view.ui.bean.ClubDatapackage;
import com.shizhong.view.ui.bean.Clubbean;
import com.shizhong.view.ui.map.ILoactionInfoCaller;
import com.shizhong.view.ui.map.SZLocationManager;
import com.shizhong.view.ui.map.SZLocationManagerFactory;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuliyan on 16/1/28.
 */
public class ActivityCrewList extends BaseFragmentActivity implements View.OnClickListener,ILoactionInfoCaller {

	private ListView listView;
	private PullToRefreshLayout mPullToRefreshListView;

	private BaseAdapter mAdapter;
	private String loginToken; // 用户token
	private int nowPage = 1;// 现在要去请求第几页
	private final int recordNum = DebugConfig.BASE_PAGE_SIZE;// 每次请求条目
	private boolean isHasMore;// 是否还有更多数据
	private boolean isLoadMore;// 是否加载下一页数据
	private ArrayList<Clubbean> mDatas = new ArrayList<Clubbean>();
	private String cityId;

	private Handler mHandler = new Handler();
	private TextView titleView, applyClub;
	private ImageView backBtn;
	private SZLocationManager mGeoLoactionManager;
	private ProgressBar mLoadingProgressBar;
	private View mContentNullView;
	private TextView mNullText;

	private String mClubsTag = "/club/getClubs";// 获取附近舞社
	private String mModifyTag = "/member/modifyMemberInfo";// 修改用户的个人信息

	@Override
	protected void initView() {
		setContentView(R.layout.activity_layout_crew_list);
		titleView = (TextView) findViewById(R.id.title_tv);
		titleView.setText(getString(R.string.club));
		backBtn = (ImageView) findViewById(R.id.left_bt);
		backBtn.setOnClickListener(this);
		applyClub = (TextView) findViewById(R.id.apply_club);
		applyClub.setOnClickListener(this);
		mContentNullView = findViewById(R.id.null_view);
		mNullText = (TextView) findViewById(R.id.tv_null_text);
		mNullText.setText("抱歉！搜不到您附近的舞社信息");
		mAdapter = new CrewListAdapter(ActivityCrewList.this, mDatas);
		mLoadingProgressBar = (ProgressBar) findViewById(R.id.loading_dialog_progressBar);
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

		mLoadingProgressBar.setVisibility(View.VISIBLE);
		mGeoLoactionManager.startLoacation();
	}

	@Override
	protected void initBundle() {
		loginToken = PrefUtils.getString(ActivityCrewList.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		mGeoLoactionManager = SZLocationManagerFactory.getLoactionManager(ActivityCrewList.this,
				SZLocationManagerFactory.TYPE_GAODE);
		mGeoLoactionManager.setLoacionInfoListener(this);
	}


	/**
	 * 定位回调方法
	 * @param province
	 * @param city
	 * @param lng
     * @param lat
     */
		@Override
		public void callback(String province, String city, String lng, String lat) {
			dismissLoadingDialog();
			cityId = city;
			editUserLoaction(lng, lat);
			mGeoLoactionManager.stopLoaction();
			if (mDatas.size() <= 0) {
				loadAUTO();
			}

		}
	@Override
	protected void initData() {
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
		params.put("cityId", cityId);
		params.put("recordNum", recordNum + "");
		LogUtils.e("获取舞社列表", "-------");
		BaseHttpNetMananger.getInstance(ActivityCrewList.this).postJSON(ActivityCrewList.this, mClubsTag, params,
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
								ToastUtils.showErrorToast(ActivityCrewList.this, code, msg, true);
								loadNoMore();
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}

						ClubDatapackage data = GsonUtils.json2Bean(req, ClubDatapackage.class);
						if (data == null) {
							loadNoMore();
							return;
						}
						List<Clubbean> list = data.data;
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
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.left_bt:
			finish();
			break;
		case R.id.apply_club:
			mIntent.setClass(ActivityCrewList.this, ActivityClubApply.class);
			startActivityForResult(mIntent, -1);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BaseHttpNetMananger.getRequstQueue().cancelAll(mClubsTag);
		BaseHttpNetMananger.getRequstQueue().cancelAll(mModifyTag);
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView = null;
		}
		if(mDatas!=null){
			mDatas.clear();
			mDatas=null;
		}
		if (mAdapter != null) {
			mAdapter = null;
		}
		if(mGeoLoactionManager!=null){
			mGeoLoactionManager.onDestory();
			mGeoLoactionManager=null;
		}
		System.gc();

	}

	private void editUserLoaction(String lng, String lat) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", loginToken);
		params.put("lng", lng);
		params.put("lat", lat);
		LogUtils.e("修改个人资料", "-------");
		BaseHttpNetMananger.getInstance(ActivityCrewList.this).postJSON(ActivityCrewList.this, mModifyTag, params,
				new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						mLoadingProgressBar.setVisibility(View.GONE);
						try {
							JSONObject root = new JSONObject(req);
							int code = root.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								break;
							case 900001:
								msg = root.getString("msg");
							default:
								ToastUtils.showErrorToast(ActivityCrewList.this, code, msg, true);
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void requestFail() {
						mLoadingProgressBar.setVisibility(View.GONE);
						ToastUtils.showShort(ActivityCrewList.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						mLoadingProgressBar.setVisibility(View.GONE);
						ToastUtils.showShort(ActivityCrewList.this, getString(R.string.net_conected_error));

					}
				}, false);
	}
}
