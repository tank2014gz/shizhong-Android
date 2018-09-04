package com.shizhong.view.ui.fragment;

import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.NearByPeopleAdapter;
import com.shizhong.view.ui.base.BaseFragment;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.PullToRefreshLayout.OnRefreshListener;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.shizhong.view.ui.bean.UserExtendsListDataPakage;
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
public class NearByPeopleFragment extends BaseFragment {
	private ListView listView;
	private PullToRefreshLayout mPullToRefreshListView;

	private NearByPeopleAdapter mAdapter;
	private int nowPage = 1;// 现在要去请求第几页
	private final int recordNum = DebugConfig.BASE_PAGE_SIZE;// 每次请求条目
	private boolean isHasMore;// 是否还有更多数据
	private boolean isLoadMore;// 是否加载下一页数据
	private ArrayList<UserExtendsInfo> mDatas = new ArrayList<UserExtendsInfo>();
	private View mContentNullView;
	private TextView mNullText;
	private Handler mHandler = new Handler();

	private String loginToken; // 用户token
	private String lat;
	private String lng;

	@Override
	public void initBundle() {
		loginToken = PrefUtils.getString(getActivity(), ContantsActivity.LoginModle.LOGIN_TOKEN, "");
	}

	@Override
	public int initRootView() {
		return R.layout.sz_listview_center_layout;
	}

	@Override
	public void initView() {
		mContentNullView = findViewById(R.id.null_view);
		mNullText = (TextView) findViewById(R.id.tv_null_text);
		mNullText.setText("抱歉！搜不到您附近的好友信息");
		mAdapter = new NearByPeopleAdapter(getActivity(), mDatas);
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

	}

	/**
	 * 主动请求网络数据
	 */
	private void loadAUTO() {
		isLoadMore = false;
		mPullToRefreshListView.autoRefresh();
	}

	public void requestData(final PullToRefreshLayout pullToRefreshLayout, final boolean isLoadMore) {
		String rootURL = "/member/getNearbyPersons";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", loginToken);
		params.put("lng", lng);
		params.put("lat", lat);
		params.put("nowPage", nowPage + "");
		params.put("recordNum", recordNum + "");
		LogUtils.e("附近用户信息列表", "-------");
		BaseHttpNetMananger.getInstance(getActivity()).postJSON(getActivity(), rootURL, params, new IRequestResult() {

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

				UserExtendsListDataPakage data = GsonUtils.json2Bean(req, UserExtendsListDataPakage.class);
				if (data == null) {
					loadNoMore();
					return;
				}
				List<UserExtendsInfo> list = data.data;
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
	public void initData() {

	}

	public void refreshPeopleLocationInfo(String province, String city, String lng2, String lat2) {
		this.lat = lat2;
		this.lng = lng2;
		mAdapter.setLoaction(this.lng, this.lat);
		if (mDatas.size() <= 0) {
			loadAUTO();
		}
	}
}
