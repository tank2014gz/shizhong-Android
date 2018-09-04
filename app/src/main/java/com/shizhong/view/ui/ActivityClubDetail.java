package com.shizhong.view.ui;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.adapter.RecommendDancerAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.PullToRefreshLayout.OnRefreshListener;
import com.shizhong.view.ui.bean.BaseVideoBean;
import com.shizhong.view.ui.bean.BaseVideoList;
import com.shizhong.view.ui.bean.ClubBeanDatapackage;
import com.shizhong.view.ui.bean.Clubbean;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.EaseConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuliyan on 16/1/28. 舞社详情
 */
public class ActivityClubDetail extends BaseFragmentActivity implements View.OnClickListener {

	private View mClubHeadView;
	private LayoutInflater mLayoutInfalter;
	private SimpleDraweeView mClubLoginImage;
	private TextView mClubDes;
	private TextView mClubPhoneNumber;
	private TextView mClubPlace;
	private ImageView mClubChat;

	private TextView mClubTitle;

	private RecommendDancerAdapter mAdapter;
	private ListView listView;
	private PullToRefreshLayout mPullToRefreshListView;
	private int nowPage = 1;// 现在要去请求第几页
	private final int recordNum = DebugConfig.BASE_PAGE_SIZE;// 每次请求条目
	private boolean isHasMore;// 是否还有更多数据
	private boolean isLoadMore;// 是否加载下一页数据
	private ArrayList<BaseVideoBean> mDatas = new ArrayList<BaseVideoBean>();
	private String club_id = null;
	private String club_name = null;
	private String login_token = null;

	private String clubLogoUrl;
	private String clubName;
	private String memberId;
	private String mMineHeader;
	private String getClubVideosTag = "/video/getDanceClubVideos";
	private String getClubDetailTag = "/club/getClubDetail";

	@Override
	protected void initView() {
		setContentView(R.layout.activity_layout_club_detail);
		mClubTitle = (TextView) findViewById(R.id.title_tv);
		mClubTitle.setText(club_name);

		findViewById(R.id.left_bt).setOnClickListener(this);

		mLayoutInfalter = LayoutInflater.from(ActivityClubDetail.this);
		mClubHeadView = mLayoutInfalter.inflate(R.layout.club_detail_head_layout, null);
		mClubLoginImage = (SimpleDraweeView) mClubHeadView.findViewById(R.id.club_logo_image);
		mClubDes = (TextView) mClubHeadView.findViewById(R.id.club_des);
		mClubPhoneNumber = (TextView) mClubHeadView.findViewById(R.id.club_phone);
		mClubPlace = (TextView) mClubHeadView.findViewById(R.id.club_address);
		mClubChat = (ImageView) mClubHeadView.findViewById(R.id.chat_club);
		mClubChat.setOnClickListener(this);
		mPullToRefreshListView = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		listView = (ListView) findViewById(R.id.content_view);
		listView.addHeaderView(mClubHeadView);
		mAdapter = new RecommendDancerAdapter(ActivityClubDetail.this, mDatas);
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

		requestClubDetail();

	}

	/**
	 * 主动请求网络数据
	 */
	private void loadAUTO() {
		isLoadMore = false;
		mPullToRefreshListView.autoRefresh();
	}

	/**
	 * 获取舞社视频详情
	 */
	public void requestData(final PullToRefreshLayout pullToRefreshLayout, final boolean isLoadMore) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("nowPage", nowPage + "");
		params.put("clubId", club_id);
		params.put("recordNum", recordNum + "");
		LogUtils.e("获取舞社详情视频", "-------");
		BaseHttpNetMananger.getInstance(ActivityClubDetail.this).postJSON(ActivityClubDetail.this, getClubVideosTag,
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
								ToastUtils.showErrorToast(ActivityClubDetail.this, code, msg, true);
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
						nowPage++;
						mDatas.addAll(list);
						mAdapter.notifyDataSetChanged();
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
		mMainHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (isLoadMore) {
					mPullToRefreshListView.loadmoreFinish(PullToRefreshLayout.FAIL);
				} else {
					mPullToRefreshListView.refreshFinish(PullToRefreshLayout.FAIL);
				}
			}
		}, 1000);
	}

	@Override
	protected void initBundle() {
		club_id = getIntent().getStringExtra(ContantsActivity.Club.CLUB_ID);
		club_name = getIntent().getStringExtra(ContantsActivity.Club.CLUB_NAME);
		login_token = PrefUtils.getString(ActivityClubDetail.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		mMineHeader = PrefUtils.getString(ActivityClubDetail.this, ContantsActivity.LoginModle.HEAD_URL, "");
	}

	/**
	 * 获取舞社详情
	 */
	private void requestClubDetail() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("clubId", club_id);
		LogUtils.e("获取舞社详情", "-------");
		BaseHttpNetMananger.getInstance(ActivityClubDetail.this).postJSON(ActivityClubDetail.this, getClubDetailTag,
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
								ToastUtils.showErrorToast(ActivityClubDetail.this, code, msg, true);
								loadNoMore();
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}

						ClubBeanDatapackage data = GsonUtils.json2Bean(req, ClubBeanDatapackage.class);
						Clubbean clubInfo = data.data;
						if (clubInfo != null) {
							mPullToRefreshListView.setVisibility(View.VISIBLE);
							flashClubInfo(clubInfo);
						} else {
							mPullToRefreshListView.setVisibility(View.INVISIBLE);
							showErrorDialog("舞社信息异常");
						}

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

	private void flashClubInfo(Clubbean clubbean) {
		if (clubbean != null) {
			if (mDatas.size() <= 0) {
				loadAUTO();
			}
			clubLogoUrl = clubbean.logoUrl;
			if (TextUtils.isEmpty(clubLogoUrl)) {
				clubLogoUrl = "";
			} else {
				clubLogoUrl = FormatImageURLUtils.formatURL(clubLogoUrl, ContantsActivity.Image.MODLE,
						ContantsActivity.Image.SMALL);
			}
			mClubLoginImage.setImageURI(Uri.parse(clubLogoUrl));
			// imageLoad.displayImage(clubLogoUrl, mClubLogoBackImage, options);
			// imageLoad.displayImage(clubLogoUrl, mClubLoginImage, options);
//			Glide.with(ActivityClubDetail.this).load(clubLogoUrl).placeholder(R.drawable.sz_activity_default)
//					.error(R.drawable.sz_activity_default).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//					.into(mClubLoginImage);

			clubName = clubbean.clubName;
			if (TextUtils.isEmpty(clubName)) {
				clubName = this.club_name;
			}
			mClubTitle.setText(clubName);

			String clubDes = clubbean.description;
			if (TextUtils.isEmpty(clubDes)) {
				clubDes = "。。。";
			}
			mClubDes.setText(clubDes);

			String clubPhoneNum = clubbean.clubContact;
			if (TextUtils.isEmpty(clubPhoneNum)) {
				clubPhoneNum = "";
			}
			mClubPhoneNumber.setText(clubPhoneNum);

			String clubPlace = clubbean.address;
			if (TextUtils.isEmpty(clubPlace)) {
				clubPlace = "";
			}
			mClubPlace.setText(clubPlace);
			memberId = clubbean.regMemberId;

		}
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
		case R.id.chat_club:
			if (TextUtils.isEmpty(memberId)) {
				ToastUtils.showShort(ActivityClubDetail.this, "获取舞社ID失败");
				return;
			}
			if (TextUtils.isEmpty(club_name)) {
				ToastUtils.showShort(ActivityClubDetail.this, "获取舞社昵称失败");
				return;
			}
			if (TextUtils.isEmpty(clubLogoUrl)) {
				ToastUtils.showShort(ActivityClubDetail.this, "获取舞社logo头像失败");
				return;
			}
			mIntent.setClass(ActivityClubDetail.this, ChatActivity.class);
			mIntent.putExtra(EaseConstant.EXTRA_USER_ID, memberId);
			mIntent.putExtra(EaseConstant.EXTRA_CHAT_NICKNAME, club_name);
			mIntent.putExtra(EaseConstant.EXTRA_CHAT_HEADURL, clubLogoUrl);
			mIntent.putExtra(ContantsActivity.LoginModle.HEAD_URL, mMineHeader);
			startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_CHART_LIST);
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (mAdapter != null) {
			mAdapter.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getClubVideosTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getClubDetailTag);
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
