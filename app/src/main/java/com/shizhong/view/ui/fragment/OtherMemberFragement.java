package com.shizhong.view.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityUserModify;
import com.shizhong.view.ui.ChatActivity;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.MemberAttentionAdapter;
import com.shizhong.view.ui.adapter.MemberFansAdapter;
import com.shizhong.view.ui.adapter.VideoPlayingListAdapter;
import com.shizhong.view.ui.base.BaseFragment;
import com.shizhong.view.ui.base.DebugConfig;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.net.VideoHttpRequest;
import com.shizhong.view.ui.base.net.VideoHttpRequest.HttpRequestCallBack2;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.GlideCircleTransform;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.StringUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.MyRadioGroup;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.shizhong.view.ui.base.view.PullToRefreshLayout.OnRefreshListener;
import com.shizhong.view.ui.bean.BaseVideoBean;
import com.shizhong.view.ui.bean.BaseVideoList;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.shizhong.view.ui.bean.UserExtendsListDataPakage;
import com.hyphenate.easeui.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.EaseConstant;
import com.shizhong.view.ui.mine.ActivityMineMusicList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuliyan on 16/1/29.
 */
public class OtherMemberFragement extends BaseFragment implements View.OnClickListener {

	private ImageView mRightBtn;
	private SimpleDraweeView mMemberHead;
	private TextView mMemberNickName;
	private ImageView mMemberSex;
	private TextView mMemberSignture;

	private TextView mMemberMusicCount;
	private TextView mMemberVideoCount;
	private TextView mMemberAttentCount;
	private TextView mMemberFansCount;

	private TextView mTitleText;
	private ImageView mBackImage;
	private MyRadioGroup mMemberRadioGroup;
	private String login_token;
	private String member_id;

	private ListView listView;
	private PullToRefreshLayout mPullToRefreshListView;
	private View mHeadView;
	private LayoutInflater mLayoutInflater;

	private final int MODLE_VIDEO = 0x1;
	private final int MODLE_ATTENT = 0x2;
	private final int MODLE_FANS = 0x3;

	private int mCurrentModle;
	private final int recordNum = DebugConfig.BASE_PAGE_SIZE;

	private VideoPlayingListAdapter mVideoAdapter;
	private ArrayList<BaseVideoBean> mVideoDatas = new ArrayList<BaseVideoBean>();
	private boolean isLoadMore_video;
	private boolean isHasMore_video;
	private int nowPage_video;

	private MemberAttentionAdapter mAttentAdapter;
	private ArrayList<UserExtendsInfo> mAttentsDatas = new ArrayList<UserExtendsInfo>();
	private boolean isLoadMore_attent;
	private boolean isHasMore_attent;
	private int nowPage_attent;

	private MemberFansAdapter mFansAdapter;
	private ArrayList<UserExtendsInfo> mFansDatas = new ArrayList<UserExtendsInfo>();
	private boolean isLoadMore_fans;
	private boolean isHasMore_fans;
	private int nowPage_fans;
	private Handler mHandler = new Handler();
	private String nickName;
	private String headUrl;
	private boolean isCanBack;
	private ImageView mAddAttent;
	private String attententValue;// 加关注的值
	private View mUserInfoDatilBtn;
	private UserExtendsInfo mUserExtendsInfos;
	private String mUserHeadUrl = null;

	private String getMemberTag = "/member/getMember";
	private String getVideoTag = "/video/getMemberVideos";
	private String getAttentTag = "/member/getMemberAttentions";
	private String getFansTag = "/member/getMemberFans";

	private int width_1_4;
	@Override
	public void initBundle() {
		login_token = PrefUtils.getString(getActivity(), ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		member_id = getArguments().getString(ContantsActivity.LoginModle.LOGIN_USER_ID);
		Bundle bundle = getArguments();
		if (bundle != null) {
			isCanBack = bundle.getBoolean(ContantsActivity.LoginModle.IS_CANBACK);
		}

		mUserHeadUrl = PrefUtils.getString(getActivity(), ContantsActivity.LoginModle.HEAD_URL, "");
		width_1_4= UIUtils.getScreenWidthPixels(getActivity())/4;
	}

	@Override
	public int initRootView() {
		return R.layout.fragment_me;
	}

	@Override
	public void initView() {
		findViewById(R.id.login_layout).setVisibility(View.GONE);
		if (isCanBack) {
			mTitleText = (TextView) findViewById(R.id.title_tv);
			mBackImage = (ImageView) findViewById(R.id.left_bt);
			mBackImage.setVisibility(View.VISIBLE);
			mBackImage.setOnClickListener(this);
			mRightBtn = (ImageView) findViewById(R.id.right_bt);
			mRightBtn.setImageResource(R.drawable.message_letter_icon);
			mRightBtn.setVisibility(View.INVISIBLE);
			mRightBtn.setOnClickListener(this);
		}
		mLayoutInflater = LayoutInflater.from(getActivity());
		mHeadView = mLayoutInflater.inflate(R.layout.person_message_detail_layout, null);
		mMemberHead = (SimpleDraweeView) mHeadView.findViewById(R.id.member_head);
		mMemberNickName = (TextView) mHeadView.findViewById(R.id.member_nickname);
		mAddAttent = (ImageView) mHeadView.findViewById(R.id.add_attent);
		mAddAttent.setOnClickListener(this);
		mMemberSex = (ImageView) mHeadView.findViewById(R.id.member_sex);
		mMemberSignture = (TextView) mHeadView.findViewById(R.id.memeber_signture);
		mUserInfoDatilBtn = mHeadView.findViewById(R.id.skip_info_detail);
		mUserInfoDatilBtn.setOnClickListener(this);
		mPullToRefreshListView = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		listView = (ListView) findViewById(R.id.content_view);
		listView.addHeaderView(mHeadView);
		mMemberMusicCount=(TextView)mHeadView.findViewById(R.id.member_music_count);
		mMemberMusicCount.getLayoutParams().width=width_1_4;
		mMemberVideoCount=(TextView) mHeadView.findViewById(R.id.member_video_count);
		mMemberVideoCount.getLayoutParams().width=width_1_4;
		mMemberAttentCount=(TextView) mHeadView.findViewById(R.id.member_attent_count);
		mMemberAttentCount.getLayoutParams().width=width_1_4;
		mMemberFansCount=(TextView) mHeadView.findViewById(R.id.member_fans_count);
		mMemberFansCount.getLayoutParams().width=width_1_4;
		mMemberRadioGroup = (MyRadioGroup) mHeadView.findViewById(R.id.member_gp);
		mMemberRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				  mMemberVideoCount.setSelected(false);
				  mMemberAttentCount.setSelected(false);
				 mMemberFansCount.setSelected(false);
				mMemberMusicCount.setSelected(false);
				switch (i) {
				case R.id.member_video:
					mMemberVideoCount.setSelected(true);
					mCurrentModle = MODLE_VIDEO;
					if (mVideoAdapter == null) {
						mVideoAdapter = new VideoPlayingListAdapter(getActivity(), mVideoDatas, true);
					}
					listView.setAdapter(mVideoAdapter);
					if (mVideoDatas.size() <= 0) {
						loadAUTO();
					}
					break;
					case R.id.member_music:
						mMemberMusicCount.setSelected(true);
						mIntent.setClass(getActivity(),ActivityMineMusicList.class);
						getActivity().startActivityForResult(mIntent,-1);
						break;
				case R.id.member_attent:
					mMemberAttentCount.setSelected(true);
					mCurrentModle = MODLE_ATTENT;
					if (mAttentAdapter == null) {
						mAttentAdapter = new MemberAttentionAdapter(getActivity(), mAttentsDatas);
					}
					if (mVideoAdapter != null) {
						mVideoAdapter.onFragmentStop();
					}

					listView.setAdapter(mAttentAdapter);
					if (mAttentsDatas.size() <= 0) {
						loadAUTO();
					}
					break;
				case R.id.member_fans:
					mMemberFansCount.setSelected(true);
					mCurrentModle = MODLE_FANS;
					if (mFansAdapter == null) {
						mFansAdapter = new MemberFansAdapter(getActivity(), mFansDatas);
					}
					if (mVideoAdapter != null) {
						mVideoAdapter.onFragmentStop();
					}

					listView.setAdapter(mFansAdapter);
					if (mFansDatas.size() <= 0) {
						loadAUTO();
					}
					break;
				}
			}
		});
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				// 显示最后更新的时间
				switch (mCurrentModle) {
				case MODLE_VIDEO:
					isLoadMore_video = false;
					nowPage_video = 1;
					requestMemberVideo();
					break;
				case MODLE_ATTENT:
					isLoadMore_attent = false;
					nowPage_attent = 1;
					requestMemberAttents();
					break;
				case MODLE_FANS:
					isLoadMore_fans = false;
					nowPage_fans = 1;
					requestMemberFans();

					break;
				}

			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				switch (mCurrentModle) {
				case MODLE_VIDEO:
					isLoadMore_video = true;
					if (isHasMore_video) {
						requestMemberVideo();
					} else {
						loadNoMore();
					}
					break;
				case MODLE_ATTENT:
					isLoadMore_attent = true;
					if (isHasMore_attent) {
						requestMemberAttents();
					} else {
						loadNoMore();
					}
					break;
				case MODLE_FANS:
					isLoadMore_fans = true;
					if (isHasMore_fans) {
						requestMemberFans();
					} else {
						loadNoMore();
					}
					break;
				}

			}
		});

		requestMemberInfo();
		mMemberRadioGroup.check(R.id.member_video);
	}

	/**
	 * 主动请求网络数据
	 */
	private void loadAUTO() {
		switch (mCurrentModle) {
		case MODLE_VIDEO:
			isLoadMore_video = false;
			break;
		case MODLE_ATTENT:
			isLoadMore_attent = false;
			break;
		case MODLE_FANS:
			isLoadMore_fans = false;
			break;
		}
		mPullToRefreshListView.autoRefresh();
	}

	/**
	 * 请求用户视频
	 */
	private void requestMemberVideo() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("otherMemberId", member_id);
		params.put("nowPage", nowPage_video + "");
		params.put("recordNum", recordNum + "");
		LogUtils.e("获取用户视频列表", "-------");

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
							JSONObject expand=root.getJSONObject("expand");
							int videoCount=expand.getInt("videoCount");
							mMemberVideoCount.setText(StringUtils.formCount(videoCount));
							LogUtils.e("=======","mMemberVideoCount"+videoCount);
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
							isHasMore_video = false;
						} else {
							isHasMore_video = true;
						}

						if (!isLoadMore_video) {
							mVideoDatas.clear();
						}
						nowPage_video++;
						mVideoDatas.addAll(list);
						mVideoAdapter.notifyDataSetChanged();
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								if (mPullToRefreshListView != null) {
									if (isLoadMore_video) {
										mPullToRefreshListView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
									} else {
										mPullToRefreshListView.refreshFinish(PullToRefreshLayout.SUCCEED);
									}
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

	/**
	 * 请求粉丝
	 */
	private void requestMemberFans() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("memberId", member_id);
		params.put("nowPage", nowPage_fans + "");
		params.put("recordNum", recordNum + "");
		LogUtils.e("获取粉丝列表", "-------");
		BaseHttpNetMananger.getInstance(getActivity()).postJSON(getActivity(), getFansTag, params,
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
							JSONObject expand=root.getJSONObject("expand");
							int fansCount=expand.getInt("fansCount");
							mMemberFansCount.setText(StringUtils.formCount(fansCount));
							LogUtils.e("=======","fansCount"+fansCount);
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
							isHasMore_fans = false;
						} else {
							isHasMore_fans = true;
						}

						if (!isLoadMore_fans) {

							mFansDatas.clear();
						}
						nowPage_fans++;
						mFansDatas.addAll(list);
						mFansAdapter.notifyDataSetChanged();
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								if (isLoadMore_fans) {
									mPullToRefreshListView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
								} else {
									mPullToRefreshListView.refreshFinish(PullToRefreshLayout.SUCCEED);
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

	/**
	 *
	 */
	private void requestMemberAttents() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("nowPage", nowPage_attent + "");
		params.put("recordNum", recordNum + "");
		params.put("memberId", member_id);
		LogUtils.e("获取关注列表", "-------");
		BaseHttpNetMananger.getInstance(getActivity()).postJSON(getActivity(), getAttentTag, params,
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
							JSONObject expand=root.getJSONObject("expand");
							int attentionCount=expand.getInt("attentionCount");
							mMemberAttentCount.setText(StringUtils.formCount(attentionCount));
							LogUtils.e("=======","attentionCount"+attentionCount);
						} catch (JSONException e) {
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
							isHasMore_attent = false;
						} else {
							isHasMore_attent = true;
						}

						if (!isLoadMore_attent) {

							mAttentsDatas.clear();
						}
						nowPage_attent++;
						mAttentsDatas.addAll(list);
						mAttentAdapter.notifyDataSetChanged();
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								if (isLoadMore_attent) {
									mPullToRefreshListView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
								} else {
									mPullToRefreshListView.refreshFinish(PullToRefreshLayout.SUCCEED);
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

	/**
	 * 没有更多数据
	 */
	private void loadNoMore() {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mPullToRefreshListView != null) {
					if (isLoadMore_attent || isLoadMore_fans || isLoadMore_video) {
						mPullToRefreshListView.loadmoreFinish(PullToRefreshLayout.FAIL);
					} else {
						mPullToRefreshListView.refreshFinish(PullToRefreshLayout.FAIL);
					}
				}
			}
		}, 1000);
	}

	private void requestMemberInfo() {
		VideoHttpRequest.getMememberInfo(getMemberTag, getActivity(), login_token, member_id, false,
				new VideoHttpRequest.HttpRequestCallBack1() {
					@Override
					public void callBack(UserExtendsInfo info) {
						mUserExtendsInfos = info;
						fillInfo(mUserExtendsInfos);
					}

					@Override
					public void callBackFail() {
						// TODO Auto-generated method stub

					}
				});
	}

	@Override
	public void initData() {

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.left_bt:
			getActivity().finish();
			break;
		case R.id.right_bt:
			if (attententValue.equals("1")) {
				mIntent.setClass(getActivity(), ChatActivity.class);
				mIntent.putExtra(EaseConstant.EXTRA_USER_ID, member_id);
				mIntent.putExtra(ContantsActivity.LoginModle.HEAD_URL, mUserHeadUrl);
				mIntent.putExtra(EaseConstant.EXTRA_CHAT_NICKNAME, nickName);
				mIntent.putExtra(EaseConstant.EXTRA_CHAT_HEADURL, headUrl);
				getActivity().startActivityForResult(mIntent, -1);
			} else {
				showAttentionDialog();
			}
			break;
		case R.id.add_attent:

			addAttention();
			break;
		case R.id.skip_info_detail:
			mIntent.setClass(getActivity(), ActivityUserModify.class);
			mIntent.putExtra("user_info", mUserExtendsInfos);
			mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, member_id);
			startActivityForResult(mIntent, ContantsActivity.Action.ACTION_MODIFY_USERINFO);
			break;
		}
	}

	private Dialog mAddAttentionDialog;

	private void showAttentionDialog() {
		if (mAddAttentionDialog == null) {
			mAddAttentionDialog = DialogUtils.confirmDialog(getActivity(), "是否去加关注", "加关注", "取消", new ConfirmDialog() {

				@Override
				public void onOKClick(Dialog dialog) {
					addAttention();

				}

				@Override
				public void onCancleClick(Dialog dialog) {
					// TODO Auto-generated method stub

				}

			});
		}
		if (!mAddAttentionDialog.isShowing()) {
			mAddAttentionDialog.show();
		}
	}

	private void fillInfo(UserExtendsInfo info) {
		mRightBtn.setVisibility(View.VISIBLE);
		headUrl = info.headerUrl;
		if (TextUtils.isEmpty(headUrl)) {
			headUrl = "";
		} else {
			headUrl = FormatImageURLUtils.formatURL(headUrl, ContantsActivity.Image.MODLE,
					ContantsActivity.Image.MODLE);
		}
		mMemberHead.setImageURI(Uri.parse(headUrl));
//		Glide.with(getActivity()).load(headUrl).placeholder(R.drawable.sz_head_default)
//				.error(R.drawable.sz_head_default).transform(new GlideCircleTransform(getActivity()))
//				.diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(mMemberHead);
		nickName = info.nickname;
		if (TextUtils.isEmpty(nickName)) {
			nickName = "匿名舞者";
		}
		mMemberNickName.setText(nickName);

		boolean isMen = "1".equals(info.sex);
		if (isMen) {
			mMemberSex.setImageResource(R.drawable.sz_mine_boy);
		} else {
			mMemberSex.setImageResource(R.drawable.sz_mine_girl);
		}
		String signture = info.signature;
		if (TextUtils.isEmpty(signture)) {
			mMemberSignture.setHint("说句牛逼的话让大家记住你！！！");
		} else {
			mMemberSignture.setText(signture);
		}

		mAddAttent.setVisibility(View.VISIBLE);
		attententValue = info.isAttention;
		if ("0".equals(attententValue)) {
			mAddAttent.setImageResource(R.drawable.add_attention);
		} else if ("1".equals(attententValue)) {
			mAddAttent.setImageResource(R.drawable.has_attention);
		}

		if (isCanBack) {
			mTitleText.setText(nickName);
		}
		int videoCount=info.videoCount;
		mMemberVideoCount.setText(StringUtils.formCount(videoCount));

		int attentCount=info.attentionCount;
		mMemberAttentCount.setText(StringUtils.formCount(attentCount));

		int fansCount=info.fansCount;
		mMemberFansCount.setText(StringUtils.formCount(fansCount));
	}

	private void addAttention() {
		String type = attententValue;
		if (type.equals("0")) {
			type = "1";
		} else if (type.equals("1")) {
			type = "0";
		}
		if ("1".equals(attententValue)) {
			mAddAttent.setImageResource(R.drawable.add_attention);
			attententValue = "0";
		} else if ("0".equals(attententValue)) {
			mAddAttent.setImageResource(R.drawable.has_attention);
			attententValue = "1";
		}
		VideoHttpRequest.addAttention(getActivity(), login_token, member_id, 0, type, new HttpRequestCallBack2() {

			@Override
			public void callBackFail() {
				if (attententValue.equals("1")) {
					mAddAttent.setImageResource(R.drawable.add_attention);
					attententValue = "0";
				} else if (attententValue.equals("0")) {
					mAddAttent.setImageResource(R.drawable.has_attention);
					attententValue = "1";
				}
			}
		});
	}

	public void hide() {
		if (mVideoAdapter != null) {
			mVideoAdapter.onFragmentStop();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (mVideoAdapter != null) {
			mVideoAdapter.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getAttentTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getFansTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getVideoTag);
		}

		if(mHandler!=null){
			mHandler=null;
		}
		if (mAttentsDatas != null) {
			mAttentsDatas = null;
		}
		if (mFansDatas != null) {
			mFansDatas = null;
		}
		if (mVideoDatas != null) {
			mVideoDatas = null;
		}

		if (mAttentAdapter != null) {
			mAttentAdapter = null;
		}
		if (mVideoAdapter != null) {
			mVideoAdapter = null;
		}
		if (mFansAdapter != null) {
			mFansAdapter = null;
		}
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView = null;
		}
		System.gc();

	}
}
