package com.shizhong.view.ui.fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.BannerHolderView;
import com.shizhong.view.ui.adapter.JieQuAdapter;
import com.shizhong.view.ui.base.BaseFragment;
import com.shizhong.view.ui.base.net.JiequHttpRequest;
import com.shizhong.view.ui.base.utils.ACache;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.view.convenientbanner.ConvenientBanner;
import com.shizhong.view.ui.base.view.convenientbanner.holder.CBViewHolderCreator;
import com.shizhong.view.ui.bean.BannerBean;
import com.shizhong.view.ui.bean.BannerDataPackage;
import com.shizhong.view.ui.bean.EventsNewsBean;
import com.shizhong.view.ui.bean.EventsNewsDataPackage;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Fragment_JieQu extends BaseFragment {

	// private PullToRefreshListView mPullToRefreshListView;
	private JieQuAdapter mAdapter;
	private ListView listView;
	private View mBannerLayout;
	private String login_token;
	private List<BannerBean> bannerBeanList = new ArrayList<BannerBean>();
	@SuppressWarnings("rawtypes")
	private ConvenientBanner mConvenientBanner;
	private Handler mHander = new Handler();
	private List<EventsNewsBean> mDatas = new ArrayList<EventsNewsBean>();
	private String banner_json_filename = "jiequ_banner";
	private String newWithEvnent_json_filename = "jiequ_newsWithEvents";
	private ACache mJSONCache;

	@Override
	public void initBundle() {
		login_token = PrefUtils.getString(getActivity(), ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		mJSONCache = ACache.get(getActivity(), "jiequ");
	}

	@Override
	public int initRootView() {
		return R.layout.fragment_jiequ;
	}

	private int mBannerWidth, mBannerHeight;

	@SuppressWarnings("rawtypes")
	@Override
	public void initView() {
		mBannerWidth = UIUtils.getScreenWidthPixels(getActivity());
		mBannerHeight = (mBannerWidth / 16) * 6;
//		isUpdateUI = true;
		listView = (ListView) findViewById(R.id.content_view);
		mBannerLayout = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_jiqu_banner_layout, null);
		mConvenientBanner = (ConvenientBanner) mBannerLayout.findViewById(R.id.convenient_banner);
		LayoutParams lp = mConvenientBanner.getLayoutParams();
		lp.width = mBannerWidth;
		lp.height = mBannerHeight;
		mConvenientBanner.setLayoutParams(lp);
		listView.addHeaderView(mBannerLayout);

	}

	@Override
	public void initData() {
		mAdapter = new JieQuAdapter(getActivity(), mDatas);
		mAdapter.setCacherManager(mJSONCache);
		listView.setAdapter(mAdapter);
		String bannerJSON = mJSONCache.getAsString(banner_json_filename);
		if (!TextUtils.isEmpty(bannerJSON)) {
			int code = 0;
			String msg = "";

			try {
				JSONObject root = new JSONObject(bannerJSON);
				code = root.getInt("code");
				if (code != 100001) {
					if (code == 900001) {
						msg = root.getString("msg");
					}
					ToastUtils.showErrorToast(getActivity(), code, msg, true);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}

			BannerDataPackage dataPackage = GsonUtils.json2Bean(bannerJSON, BannerDataPackage.class);
			refreshBannerInfo(dataPackage.data);
		}

		JiequHttpRequest.requestBanner(getActivity(), login_token, "0",
				new JiequHttpRequest.HttpRequstBannerCallBack() {
					@Override
					public void callback(String req, final List<BannerBean> list) {
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
								return;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							return;
						}

						refreshBannerInfo(list);
						mJSONCache.put(banner_json_filename, req);
					}

					@Override
					public void callbackFail() {
						mConvenientBanner.setVisibility(View.GONE);
					}
				});

		String newsWithEventJSON = mJSONCache.getAsString(newWithEvnent_json_filename);
		if (!TextUtils.isEmpty(newsWithEventJSON)) {
			int code = 0;
			String msg = "";

			try {
				JSONObject root = new JSONObject(newsWithEventJSON);
				code = root.getInt("code");
				if (code != 100001) {
					if (code == 900001) {
						msg = root.getString("msg");
					}
					ToastUtils.showErrorToast(getActivity(), code, msg, true);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
			EventsNewsDataPackage dataPackage = GsonUtils.json2Bean(newsWithEventJSON, EventsNewsDataPackage.class);
			mDatas.clear();
			mDatas.addAll(dataPackage.data);
			mAdapter.notifyDataSetChanged();
		}

		JiequHttpRequest.requestEventsAndNews(getActivity(), login_token, "0", 1, 10,
				new JiequHttpRequest.HttpRequestNewsCallBack() {

					@Override
					public void callback(String req, List<EventsNewsBean> list) {
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
								return;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							return;
						}
						
						mDatas.clear();
						mDatas.addAll(list);
						mAdapter.notifyDataSetChanged();
						mJSONCache.put(newWithEvnent_json_filename, req);
					}
				});
//		isUpdateUI = false;
	}

	private void refreshBannerInfo(final List<BannerBean> list) {
		bannerBeanList = list;
		if (bannerBeanList == null || bannerBeanList.size() == 0) {
			mConvenientBanner.setVisibility(View.GONE);
		} else {
			mConvenientBanner.setVisibility(View.VISIBLE);
		}
		mHander.post(new Runnable() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void run() {
				mConvenientBanner.setPages(new CBViewHolderCreator() {
					@Override
					public Object createHolder() {
						return new BannerHolderView(getActivity(), bannerBeanList);
					}
				}, bannerBeanList).setPageIndicator(
						new int[] { R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused });
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mConvenientBanner != null && bannerBeanList.size() > 1) {
			mConvenientBanner.startTurning(5000);
		}

	}

}
