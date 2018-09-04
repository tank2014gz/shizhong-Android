package com.shizhong.view.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.BannerHolderView2;
import com.shizhong.view.ui.adapter.RecommendDancerAdapter;
import com.shizhong.view.ui.base.BaseFragment;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.SystemSetting;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.net.JiequHttpRequest;
import com.shizhong.view.ui.base.utils.ACache;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.PullToRefreshLayout.OnRefreshListener;
import com.shizhong.view.ui.base.view.convenientbanner.ConvenientBanner;
import com.shizhong.view.ui.base.view.convenientbanner.holder.CBViewHolderCreator;
import com.shizhong.view.ui.bean.BannerBean;
import com.shizhong.view.ui.bean.BaseVideoBean;
import com.shizhong.view.ui.bean.BaseVideoList;
import com.shizhong.view.ui.bean.DanceClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment_Dances_Page extends BaseFragment  {

	private final static String ARG_DATA = "dance_type";
	private ListView listView;
	private PullToRefreshLayout mPullToRefreshListView;

	private RecommendDancerAdapter mAdapter;
	private String loginToken; // 用户token
	private String catagroyId;// 舞种的ID
	private int nowPage = 1;// 现在要去请求第几页
	private final int recordNum = DebugConfig.BASE_PAGE_SIZE;// 每次请求条目
	private boolean isHasMore;// 是否还有更多数据
	private boolean isLoadMore;// 是否加载下一页数据
	private ArrayList<BaseVideoBean> mDatas = new ArrayList<BaseVideoBean>();

	private Handler mHandler = new Handler();
	private View mBannerLayout;
	private List<BannerBean> bannerBeanList = new ArrayList<BannerBean>();
	@SuppressWarnings("rawtypes")
	private ConvenientBanner mConvenientBanner;
	private View mContentNullView;
	private TextView mNullText;
	private String mRequestTag = "";
//	private boolean isRequestFirstPage = true;

	private boolean mHasLoadedOnce = true;
	private ACache mCache;
	private String mCachName;

	private boolean isHasCacheData;

	private int position;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		setUserVisibleHint(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initBundle() {
		mCache = ACache.get(getActivity());
		Bundle bundle = getArguments();
		DanceClass danceClass = bundle.getParcelable(ARG_DATA);
		if (danceClass != null) {
			catagroyId = danceClass.categoryId;
		}
		position=bundle.getInt("position");
		mCachName = danceClass.categoryName
				+ PrefUtils.getString(getActivity(), ContantsActivity.LoginModle.LOGIN_USER_ID, "");
		loginToken = PrefUtils.getString(getActivity(), ContantsActivity.LoginModle.LOGIN_TOKEN, "");
	}






	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if(mRootView==null){
				mRootView = inflater.inflate(initRootView(), container, false);
			    initBundle();
				initView();
			}
		return mRootView;
	}



	public static Fragment_Dances_Page newInstance(DanceClass content, int position) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(ARG_DATA, content);
		bundle.putInt("position",position);
		Fragment_Dances_Page fragment = new Fragment_Dances_Page();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public int initRootView() {
		return R.layout.sz_listview_center_layout;
	}

	private int mBannerWidth, mBannerHeight;

	@SuppressWarnings("rawtypes")
	@Override
	public void initView() {
		mBannerWidth = UIUtils.getScreenWidthPixels(getActivity());
		mBannerHeight = (mBannerWidth / 16) * 4;
		mBannerLayout = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_jiqu_banner_layout, null);
		mConvenientBanner = (ConvenientBanner) mBannerLayout.findViewById(R.id.convenient_banner);
		LayoutParams lp = mConvenientBanner.getLayoutParams();
		lp.width = mBannerWidth;
		lp.height = mBannerHeight;
		mConvenientBanner.setLayoutParams(lp);
		mConvenientBanner.setVisibility(View.GONE);
		mContentNullView = findViewById(R.id.null_view);
		mNullText = (TextView) findViewById(R.id.tv_null_text);
		mNullText.setText("暂时没有最新动态");
		mPullToRefreshListView = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		listView = (ListView) findViewById(R.id.content_view);
		// listView.setOnScrollListener(mScrollListener);
		mAdapter = new RecommendDancerAdapter(getActivity(), mDatas);
		listView.addHeaderView(mBannerLayout);
		listView.setAdapter(mAdapter);

		if(position==0){
			rereshView(true);
		}

		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				if (!isHasCacheData) {
					isLoadMore = false;
					nowPage = 1;
					requestData(pullToRefreshLayout, isLoadMore, false, false);
				} else {
					isLoadMore = false;
					nowPage=1;
					if (catagroyId.equals("remen")) {
						requestData(pullToRefreshLayout, isLoadMore, SystemSetting.isUseHotRandom, false);
					} else {
						requestData(pullToRefreshLayout, isLoadMore, false, SystemSetting.isUseCategoryRandom);
					}
				}
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				isLoadMore = true;
				if (isHasMore) {
					nowPage++;
					LogUtils.e("xxxxxxxxxxxxxxx","load more isHasMore"+isHasMore);
					requestData(pullToRefreshLayout, isLoadMore, false, false);
				} else {
					LogUtils.e("xxxxxxxxxxxxxxx","load no more isHasMore"+isHasMore);
					loadNoMore();
				}

			}
		});

		JiequHttpRequest.requestBanner(getActivity(), loginToken, catagroyId,
				new JiequHttpRequest.HttpRequstBannerCallBack() {
					@Override
					public void callback(String req, final List<BannerBean> list) {
						bannerBeanList = list;
						if (bannerBeanList == null || bannerBeanList.size() == 0) {
							mConvenientBanner.setVisibility(View.GONE);
//							mConvenientBanner=null;
//							bannerBeanList.clear();
//							bannerBeanList=null;
//							System.gc();

						} else {
							mConvenientBanner.setVisibility(View.VISIBLE);
							mHandler.post(new Runnable() {
								@SuppressWarnings({ "unchecked" })
								@Override
								public void run() {
									mConvenientBanner.setPages(new CBViewHolderCreator() {
										@Override
										public Object createHolder() {
											return new BannerHolderView2(getActivity(), bannerBeanList);
										}
									}, bannerBeanList).setPageIndicator(new int[] { R.drawable.ic_page_indicator,
											R.drawable.ic_page_indicator_focused });
								}
							});
						}


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


	public void requestData(final PullToRefreshLayout pullToRefreshLayout, final boolean isLoadMore,
							final boolean isRandomHot, final boolean isRandomCatergray) {
		Map<String, String> params = new HashMap<String, String>();
		String rootURL = null;
		if (catagroyId.equals("remen")) {
			if (isRandomHot) {
				rootURL = "/video/getRandomHotVideos";
			} else {
				rootURL = "/video/getHotVideos";
			}
		} else {
			if (isRandomCatergray) {
				rootURL = "/video/getRandomHomeRecommendVideos";
			} else {
				rootURL = "/video/getHomeRecommendVideos";
			}
			params.put("categoryId", catagroyId);
		}
		mRequestTag = rootURL;

		params.put("token", loginToken);

		if (!isRandomCatergray && !isRandomHot) {
			params.put("nowPage", nowPage + "");
		}
		params.put("recordNum", recordNum + "");
		LogUtils.e("获取视频播放列表", "-------");
		LogUtils.i("nowPage", nowPage + "");
		BaseHttpNetMananger.getInstance(getActivity()).postJSON(getActivity(), mRequestTag, params,
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
						if (!isLoadMore) {
							mDatas.clear();
						}

						if (list.size() < recordNum) {
							isHasMore = false;
						} else {
							isHasMore = true;
						}
						if (nowPage == 1 || isRandomHot || isRandomCatergray) {
							isHasCacheData=true;
							mCache.put(mCachName, req);
							isHasMore=true;
						}

						mDatas.addAll(list);


//						if(mDataCacheCallBack!=null){
//							mDataCacheCallBack.callBackDatas(mDatas,position,nowPage);
//						}
						mAdapter.notifyDataSetChanged();
						if (mContentNullView.getVisibility() == View.VISIBLE) {
							mContentNullView.setVisibility(View.GONE);
						}
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (isLoadMore) {
									pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
								} else {
									pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
								}

							}
						});
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

	@Override
	public void initData() {

	}


	public void stopRefresh(){
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(mRequestTag);
		}
		if (isLoadMore) {
			mPullToRefreshListView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
		} else {
			mPullToRefreshListView.refreshFinish(PullToRefreshLayout.SUCCEED);
		}
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
				if ( mDatas.size() <= 0) {
					mContentNullView.setVisibility(View.VISIBLE);
				} else {
					mContentNullView.setVisibility(View.GONE);
				}
			}
		},1000);

	}






	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (mAdapter != null) {
			mAdapter.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mConvenientBanner != null && bannerBeanList != null && bannerBeanList.size() > 1) {
			mConvenientBanner.startTurning(5000);
		}
	}

	public  void rereshView(boolean isRefresh){

		if(isRefresh) {
			final  String cacheString = mCache.getAsString(mCachName);
			if (TextUtils.isEmpty(cacheString)) {
				isHasCacheData = false;
				if (mDatas == null || mDatas.size() <= 0) {
					loadAUTO();
				}
			} else {
				isHasMore = true;
				isHasCacheData = true;


				mDatas.clear();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						BaseVideoList data = GsonUtils.json2Bean(cacheString, BaseVideoList.class);
						List<BaseVideoBean> list = data.data;
						mDatas.addAll(list);
						mAdapter.notifyDataSetChanged();
						loadAUTO();
					}});

			}
		}
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDatas != null) {
			mDatas.clear();
			mDatas = null;
		}
		if (mAdapter != null) {
			mAdapter = null;
		}
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView.removeAllViews();
			mPullToRefreshListView = null;
		}
		if (BaseHttpNetMananger.getRequstQueue() != null && mRequestTag != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(mRequestTag);
		}
		System.gc();

	}



	@Override
	public void onDestroyView()	{		//移除当前视图，防止重复加载相同视图使得程序闪退
		  	((ViewGroup)mRootView.getParent()).removeView(mRootView);
		super.onDestroyView();
	}

}
