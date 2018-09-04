package com.shizhong.view.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.DancerViewPagerAdpater;
import com.shizhong.view.ui.base.BaseFragment;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.DanceClassEditWindow;
import com.shizhong.view.ui.base.view.DanceClassWindow;
import com.shizhong.view.ui.bean.DanceClass;
import com.shizhong.view.ui.bean.DanceList;
import com.viewpagerindicator.TabPageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment_HomeRecommend extends BaseFragment implements OnClickListener {
	private TabPageIndicator mTitleIndicator;
	private ImageView mDownMenuBtn;
	private ViewPager mDancePagers;
	private DancerViewPagerAdpater mDancesPagerAdapter;
	private Fragment_Dances_Page[] mDancerFragments;
	private int position;
	private String token;
	private List<DanceClass> titles = new ArrayList<DanceClass>();
	private final String mRequestTag = "/category/getAll";
//	public  View[] viewArrayList ;

	private boolean[] isPullToRefresh;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		setUserVisibleHint(true);
		super.onCreate(savedInstanceState);
	}

	/**
	 * 获取titles 标题
	 */
	private void getTitles() {

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		LogUtils.e("获取标题列表", "-------");
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
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}

						DanceList list = GsonUtils.json2Bean(req, DanceList.class);
						titles.clear();
						DanceClass hotDance = new DanceClass();
						hotDance.categoryName = "热门";
						hotDance.categoryId = "remen";
						hotDance.fileUrl = "http://7xqco9.com1.z0.glb.clouddn.com/111365259852fd164dl.jpg";
						titles.add(hotDance);
						hotDance = null;
						System.gc();
						titles.addAll(list.data);
						int size=titles.size();

						isPullToRefresh=new boolean[size];
						for (int i=0;i<size;i++){
							isPullToRefresh[i]=true;
						}
						isPullToRefresh[0]=false;

//						viewArrayList=new View[size];
						mDancerFragments=new Fragment_Dances_Page[size];
						mTitleIndicator = (TabPageIndicator) findViewById(R.id.titles_indicator);
						mDancePagers = (ViewPager) findViewById(R.id.dances_kind_pager);
						mDancesPagerAdapter = new DancerViewPagerAdpater(getChildFragmentManager(), mDancerFragments,
								titles);
						mDancePagers.setOffscreenPageLimit(size-1);
						mDancePagers.setAdapter(mDancesPagerAdapter);
						mTitleIndicator.setViewPager(mDancePagers);
						mTitleIndicator.setVisibility(View.VISIBLE);
						mTitleIndicator.setOnPageChangeListener(new OnPageChangeListener() {

							@Override
							public void onPageSelected(int arg0) {
								// TODO Auto-generated method stub
//								ToastUtils.showShort(getActivity(),"arg0"+arg0);
								mDancerFragments[arg0].rereshView(isPullToRefresh[arg0]);
								isPullToRefresh[arg0]=false;

							}

							@Override
							public void onPageScrolled(int arg0, float arg1, int arg2) {
								// TODO Auto-generated method stub
								position = arg0;
							}

							@Override
							public void onPageScrollStateChanged(int arg0) {
								// TODO Auto-generated method stub

							}
						});
						mTitleIndicator.setCurrentItem(0);
//						mDancerFragments[0].rereshView(isPullToRefresh[0]);
//						isPullToRefresh[0]=false;
						mDownMenuBtn = (ImageView) findViewById(R.id.actor_down_menu);
						mDownMenuBtn.setOnClickListener(Fragment_HomeRecommend.this);
					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(getActivity(), getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(getActivity(), getString(R.string.net_conected_error));
					}
				}, true);

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) {
			if (BaseHttpNetMananger.getRequstQueue() != null && mRequestTag != null) {
				BaseHttpNetMananger.getRequstQueue().cancelAll(mRequestTag);
			}
		} else {
			if (titles != null && titles.size() <= 0) {
				getTitles();
			}
		}
	}

	@Override
	public void initBundle() {
		token = PrefUtils.getString(getActivity(), ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		getTitles();
	}

	@Override
	public int initRootView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_home_recommend_center_layout;
	}

	@Override
	public void initView() {
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (mDancerFragments[position] != null && mDancerFragments[position].isVisible()) {
			mDancerFragments[position].onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actor_down_menu:
			showDanceListWindow(v);
			break;

		default:
			break;
		}

	}

	private  DanceClassWindow mDanceClassWindow;
	private void showDanceListWindow(View view){
		if (mDanceClassWindow == null && titles != null && titles.size() > 0) {
			mDanceClassWindow = new DanceClassWindow(getActivity(), titles, "全部舞种");
			mDanceClassWindow.setOnDanceChoiseListener(new DanceClassWindow.OnDanceChoiseListener() {
				@Override
				public void choiseListener(int position) {
					mTitleIndicator.setCurrentItem(position);
				}
			});
	}
		if (!mDanceClassWindow.isShowing() && view != null) {
			mDanceClassWindow.show(view);
		}
	}

//	private DanceClassEditWindow mDanceClassWindow;
//
//	private void showDanceListWindow(View view) {
//		if (mDanceClassWindow == null && titles != null && titles.size() > 0) {
//			mDanceClassWindow = new DanceClassEditWindow(getActivity(), titles, "全部舞种");
//			mDanceClassWindow.setOnDanceChoiseListener(new DanceClassEditWindow.OnDanceChoiseListener() {
//				@Override
//				public void choiseListener(int position) {
//					mTitleIndicator.setCurrentItem(position);
//				}
//			});
//			mDanceClassWindow.setEditListener(new DanceClassEditWindow.EidtListener() {
//				@Override
//				public void edit(List<DanceClass> datas) {
//					titles=datas;
//					mDancesPagerAdapter.notifyDataSetChanged();
//					LogUtils.e("shizhong",titles.toString());
//
////					int size=titles.size();
////					for (int i=0;i<size;i++){
////						isPullToRefresh[i]=true;
////					}
////					isPullToRefresh[0]=false;
////
//////						viewArrayList=new View[size];
////					mDancerFragments=new Fragment_Dances_Page[size];
////					mTitleIndicator = (TabPageIndicator) findViewById(R.id.titles_indicator);
////					mDancePagers = (ViewPager) findViewById(R.id.dances_kind_pager);
////					mDancesPagerAdapter = new DancerViewPagerAdpater(getChildFragmentManager(), mDancerFragments,
////							titles);
////					mDancePagers.setOffscreenPageLimit(size-1);
////					mDancePagers.setAdapter(mDancesPagerAdapter);
////					mTitleIndicator.setViewPager(mDancePagers);
////					mTitleIndicator.setVisibility(View.VISIBLE);
////					mTitleIndicator.setOnPageChangeListener(new OnPageChangeListener() {
////
////						@Override
////						public void onPageSelected(int arg0) {
////							// TODO Auto-generated method stub
//////								ToastUtils.showShort(getActivity(),"arg0"+arg0);
////							mDancerFragments[arg0].rereshView(isPullToRefresh[arg0]);
////							isPullToRefresh[arg0]=false;
////
////						}
////
////						@Override
////						public void onPageScrolled(int arg0, float arg1, int arg2) {
////							// TODO Auto-generated method stub
////							position = arg0;
////						}
////
////						@Override
////						public void onPageScrollStateChanged(int arg0) {
////							// TODO Auto-generated method stub
////
////						}
////					});
////					mTitleIndicator.setCurrentItem(0);
//
//				}
//			});
//		}
//		if (!mDanceClassWindow.isShowing() && view != null) {
//			mDanceClassWindow.show(view);
//		}
//	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDanceClassWindow != null) {
			mDanceClassWindow.release();
			mDanceClassWindow = null;
		}
		System.gc();
	}

}
