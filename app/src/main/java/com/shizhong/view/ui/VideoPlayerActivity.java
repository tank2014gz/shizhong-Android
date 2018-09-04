package com.shizhong.view.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView.BufferType;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.shizhong.view.ui.adapter.ReplyVideoAdapater;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.net.VideoHttpRequest;
import com.shizhong.view.ui.base.net.VideoHttpRequest.HttpRequestCallBack2;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.InputMethodUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.StringUtils;
import com.shizhong.view.ui.base.utils.TextViewSpannerUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.shizhong.view.ui.base.view.InputPopwindow;
import com.shizhong.view.ui.base.view.InputPopwindow.InputCallBack;
import com.shizhong.view.ui.base.view.PullToRefreshLayout.OnRefreshListener;
import com.shizhong.view.ui.base.view.PullToRefreshLayout;
import com.shizhong.view.ui.base.view.ShareThreePlateformWindow;
import com.shizhong.view.ui.base.view.VideoDeatailRightMenu;
import com.shizhong.view.ui.base.view.VideoDeatailRightMenu.DelecteVideoCallBack;
import com.shizhong.view.ui.base.view.ijksample.IjkVideoView;
import com.shizhong.view.ui.base.view.ijksample.Settings;
import com.shizhong.view.ui.bean.BaseVideoBean;
import com.shizhong.view.ui.bean.CommentsVideoBean;
import com.shizhong.view.ui.bean.CommentsVideoList;
import com.shizhong.view.ui.bean.ShareContentBean;
import com.shizhong.view.ui.bean.TopicBean;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.shizhong.view.ui.bean.VideoBeanItem;
import com.umeng.analytics.MobclickAgent;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.EaseConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnSeekCompleteListener;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoPlayerActivity extends BaseFragmentActivity implements View.OnClickListener, OnPreparedListener,
		OnCompletionListener, OnErrorListener, OnSeekCompleteListener, OnBufferingUpdateListener, OnInfoListener {

	private ImageView mBackImage;
	private ImageView mMoreImage;
	private ListView listView;
	private PullToRefreshLayout mPullToRefreshListView;
	private ReplyVideoAdapater mAdapter;
	private View mListViewHeader;

	private String login_token;
	private String videoId;
	private boolean isLikedVideo;
	private long likeCount;
	private boolean isAttentionVideo;
	private String memberId;
	private long replyCount;

	private SimpleDraweeView mVideoConverImage;
	private ImageView mVideoPlayerBtn;
	private IjkVideoView mVideoPlayer;
	private TextView mCurrentTimeView;
	private SeekBar mVideoSeekBar;
	private TextView mVideoAllTimeView;
	private SimpleDraweeView mHeadView;

	private ViewGroup videoParentLayout;
	private TextView mVideoBrowserCountView;
	private TextView mNickNameView;
	private TextView mVideoUpdateTime;
	private TextView mVideoAttention;
	private TextView mVideoDetail;
	private TextView mVideoLikeView;
	private TextView mVideoReplyView;
	private TextView mVideoShareView;
	private TextView mVideoVideoReplyView;
	private int width;
	private FrameLayout.LayoutParams layoutParams;
	private BaseVideoBean mCurrentVideoBean;
	private final String replyCountText = "评论（%s）";

	private int nowPage = 1;// 现在要去请求第几页
	private final int recordNum = 10;// 每次请求条目
	private boolean isHasMore;// 是否还有更多数据
	private boolean isLoadMore;// 是否加载下一页数据

	private ArrayList<CommentsVideoBean> mDatas = new ArrayList<CommentsVideoBean>();
	private ImageView mOptionEmoj;
	private TextView mSendBtn;
	private TextView mEditView;

	private final String comment_text = "回复：%s";
//	private final String reply_text = "评论：%s";

	private CommentsVideoBean mReplyBean;

	private boolean isReplayVideo = false;
	private int video_max_time;
	private String videoUrl;
	private String shareUrl;
	private final String share_title = "分享 %s 的失重街舞视频，快来围观。";
	private final String share_title_all = "分享 %s 的失重街舞视频， %s ，快来围观。";
	private final String time_format = "%s:%s";
	private final int UPDATE_TIME = 0x3;
	private boolean isUpdateTime = false;

	private String hint_text;
	private ProgressBar mLoadingBar;

	private String user_id;
	private int position;
	private String headUrl;
	private String nickName;
	private long mVideoBrowserCount;
	private boolean isAddBrownCount = true;
	private boolean isFinish = false;

	private String mUserHeadUrl = null;
	private InputPopwindow mInputWindow = null;

	private String getCommontsTag = "/video/getComments";
	private String getVideoDetialTag = "/video/details";

	private Handler timeHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_TIME:
				if (isUpdateTime) {
					if (mVideoPlayer != null) {
						mCurrentTimeView.setText((String) msg.obj);
						mVideoSeekBar.setProgress(msg.arg1);
					}
				}
				break;
			}

		}
	};
	private Thread timeThread = new Thread(new Runnable() {
		@SuppressWarnings("static-access")
		@Override
		public void run() {
			while (!isFinish) {
				if (isUpdateTime) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (timeHander != null) {
						Message msg = timeHander.obtainMessage();
						int currentTime = (mVideoPlayer.getCurrentPosition() / 1000);
						msg.arg1 = currentTime;
						msg.obj = new String().format(time_format, StringUtils.getDoubleByteTime(currentTime / 60),
								StringUtils.getDoubleByteTime(currentTime % 60));
						msg.what = UPDATE_TIME;
						timeHander.sendMessage(msg);
					}
				}
			}
		}
	});

	@Override
	protected void initView() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_video_detail_layout);
		mBackImage = (ImageView) findViewById(R.id.left_bt);
		mBackImage.setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText(getString(R.string.video_detail));
		mMoreImage = (ImageView) findViewById(R.id.right_bt);
		mMoreImage.setOnClickListener(this);
		width = UIUtils.getScreenWidthPixels(VideoPlayerActivity.this);
		layoutParams = new FrameLayout.LayoutParams(width, width, Gravity.CENTER);
		mPullToRefreshListView = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		listView = (ListView) findViewById(R.id.content_view);
		mListViewHeader = LayoutInflater.from(this).inflate(R.layout.layout_head_video_player, null);
		mVideoConverImage = (SimpleDraweeView) mListViewHeader.findViewById(R.id.video_cover_page);
		mVideoConverImage.setLayoutParams(layoutParams);

		mVideoPlayerBtn = (ImageView) mListViewHeader.findViewById(R.id.video_play_btn);
		mVideoPlayerBtn.setLayoutParams(layoutParams);
		mVideoPlayerBtn.setOnClickListener(this);
		videoParentLayout = (ViewGroup) mListViewHeader.findViewById(R.id.video_fragment_layout);
		mCurrentTimeView = (TextView) mListViewHeader.findViewById(R.id.play_time_text);
		mVideoSeekBar = (SeekBar) mListViewHeader.findViewById(R.id.video_seek_bar);
		mLoadingBar = (ProgressBar) mListViewHeader.findViewById(R.id.loading_progress);
		mLoadingBar.setVisibility(View.GONE);
		mVideoSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mVideoPlayer.seekTo(seekBar.getProgress() * 1000);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				if (mVideoPlayer == null) {
					addVideoPlayer();
				}
				if (mVideoPlayer.isPlaying()) {
					mVideoPlayer.pause();
				}
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			}
		});
		mVideoAllTimeView = (TextView) mListViewHeader.findViewById(R.id.video_play_time);
		mHeadView = (SimpleDraweeView) mListViewHeader.findViewById(R.id.user_head);
		mHeadView.setOnClickListener(this);
		mNickNameView = (TextView) mListViewHeader.findViewById(R.id.user_nickname);
		mVideoBrowserCountView = (TextView) mListViewHeader.findViewById(R.id.brower_count);
		mVideoUpdateTime = (TextView) mListViewHeader.findViewById(R.id.video_update_time);
		mVideoAttention = (TextView) mListViewHeader.findViewById(R.id.attention_btn);
		mVideoAttention.setOnClickListener(this);
		mVideoDetail = (TextView) mListViewHeader.findViewById(R.id.video_des_info);
		mVideoLikeView = (TextView) mListViewHeader.findViewById(R.id.like);
		mVideoLikeView.setOnClickListener(this);
		mVideoReplyView = (TextView) mListViewHeader.findViewById(R.id.replay);
		mVideoReplyView.setOnClickListener(this);
		mVideoShareView = (TextView) mListViewHeader.findViewById(R.id.share);
		mVideoShareView.setOnClickListener(this);
		mVideoVideoReplyView = (TextView) mListViewHeader.findViewById(R.id.video_replay);
		listView.addHeaderView(mListViewHeader);
		isReplayVideo = true;
		mAdapter = new ReplyVideoAdapater(this, mDatas);
		mAdapter.setItemCallBack(new ReplyVideoAdapater.ItemCallBack() {
			@Override
			public void callBack(CommentsVideoBean item) {
				String currentCommentId = item.memberId;
				isReplayVideo = false;
				String nickName = item.memberInfo.nickname;
				mReplyBean = new CommentsVideoBean();
				mReplyBean.memberInfo = new UserExtendsInfo();
				mReplyBean.memberInfo.headerUrl = PrefUtils.getString(VideoPlayerActivity.this,
						ContantsActivity.LoginModle.HEAD_URL, "");
				mReplyBean.memberInfo.nickname = PrefUtils.getString(VideoPlayerActivity.this,
						ContantsActivity.LoginModle.NICK_NAME, "");
				mReplyBean.isLike = 0;
				mReplyBean.memberId=user_id;
				mReplyBean.toMemberId = currentCommentId;
				mReplyBean.toMemberNickname = nickName;
				mEditView.setHint(String.format(comment_text, nickName));
				mEditView.setFocusable(true);
				mEditView.requestFocusFromTouch();
				InputMethodUtils.show(VideoPlayerActivity.this, mEditView);
				mInputWindow.hideEmjView();
				mInputWindow.show(mEditView, mEditView.getHint().toString());

			}
		});
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

		// 初始化输入布局
		initInputLayout();
		timeThread.start();

	}

	private void addVideoPlayer() {
		IjkMediaPlayer.loadLibrariesOnce(null);
		IjkMediaPlayer.native_profileBegin("libijkolayer.so");
		IjkMediaPlayer ijkMediaPlayer=new IjkMediaPlayer();
		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
		mVideoPlayer = new IjkVideoView(VideoPlayerActivity.this);
		mVideoPlayer.createPlayer(Settings.PV_PLAYER__IjkExoMediaPlayer);
		mVideoPlayer.setBackgroundResource(R.drawable.shape_white_retangle);
		mVideoPlayer.setRender(2);
		mVideoPlayer.setOnCompletionListener(this);
		mVideoPlayer.setOnErrorListener(this);
		mVideoPlayer.setOnPreparedListener(VideoPlayerActivity.this);
		mVideoPlayer.setLayoutParams(layoutParams);
		mVideoPlayer.setOnInfoListener(this);
		mVideoPlayer.setOnBufferingUpdateListener(this);
		videoParentLayout.removeViewInLayout(mVideoPlayerBtn);
		mVideoPlayer.setOnSeekCompleteListener(this);
		videoParentLayout.addView(mVideoPlayer, 1);
		videoParentLayout.addView(mVideoPlayerBtn, 2);
		mVideoPlayer.setVideoPath(videoUrl);
		if (mLoadingBar != null) {
			mLoadingBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void initBundle() {
		videoId = getIntent().getStringExtra(ContantsActivity.Video.VIDEO_ID);
		if (TextUtils.isEmpty(videoId)) {
			ToastUtils.showShort(VideoPlayerActivity.this, "视频ID获取失败");
		}
		position = getIntent().getIntExtra(ContantsActivity.Video.VIDEO_POSITION, 0);
		login_token = PrefUtils.getString(VideoPlayerActivity.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		user_id = PrefUtils.getString(VideoPlayerActivity.this, ContantsActivity.LoginModle.LOGIN_USER_ID, "");
		mUserHeadUrl = PrefUtils.getString(VideoPlayerActivity.this, ContantsActivity.LoginModle.HEAD_URL, "");

	}

	@Override
	protected void initData() {
		requestVideoDetail();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setBackArg();

	}

	private void setBackArg() {
		Intent intent = new Intent();
		intent.putExtra(ContantsActivity.Video.VIDEO_POSITION, position);
		intent.putExtra(ContantsActivity.Video.VIDEO_IS_LIKED, isLikedVideo);
		intent.putExtra(ContantsActivity.Video.VIDEO_LIKE_NUM, likeCount);
		intent.putExtra(ContantsActivity.Video.VIDEO_REPLY_COUNT, replyCount);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@SuppressWarnings("static-access")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.left_bt:
			onBackPressed();
			break;
		case R.id.right_bt:
			if (mVideoPlayer != null && mVideoPlayer.isPlaying()) {
				stopVideo();
			}
			showVideoRightMenu(view);

			break;

		case R.id.video_play_btn:
			if (TextUtils.isEmpty(videoUrl)) {
				videoUrl = "";
				return;
			}
			if (mVideoPlayer == null) {
				addVideoPlayer();
			}
			if (mVideoPlayer.isPlaying()) {
				stopVideo();
			} else {
				mVideoPlayer.start();
				showPlayerBtn(false);
				if (isAddBrownCount) {
					String video_id = videoId;
					videoClick(login_token, videoId);
					Map<String, String> event = new HashMap<String, String>();
					event.put("video_id", video_id);
					event.put("type", "视频查看量");
					event.put("time", DateUtils.format9(System.currentTimeMillis()));
					MobclickAgent.onEvent(VideoPlayerActivity.this, "video_open_ID", event);
					mVideoBrowserCount++;
					mVideoBrowserCountView.setText(mVideoBrowserCount + "");
					isAddBrownCount = false;
				}
				mVideoConverImage.setVisibility(View.GONE);
				isUpdateTime = true;
			}

			break;
		case R.id.like:
			String type = null;
			if (isLikedVideo) {
				type = "0";
			} else {
				type = "1";
			}
			isLikedVideo = !isLikedVideo;
			likeCount = isLikedVideo ? likeCount + 1 : (likeCount - 1 > 0 ? likeCount - 1 : 0);
			mVideoLikeView.setText(likeCount + "");
			mVideoLikeView.setSelected(isLikedVideo);
			VideoHttpRequest.voteVideo(VideoPlayerActivity.this, videoId, type, 0, login_token,
					new VideoHttpRequest.HttpRequestCallBack() {

						@Override
						public void callFailBack() {
							if (isLikedVideo) {
								likeCount = (likeCount - 1 > 0) ? likeCount - 1 : 0;
							} else {
								likeCount = likeCount + 1;
							}
							mVideoLikeView.setText(likeCount + "");
							isLikedVideo = !isLikedVideo;
							mVideoLikeView.setSelected(isLikedVideo);
						}
					});
			break;
		case R.id.replay:
			if (TextUtils.isEmpty(
					PrefUtils.getString(VideoPlayerActivity.this, ContantsActivity.LoginModle.LOGIN_USER_ID, ""))) {
				return;
			}
			if (!TextUtils.isEmpty(memberId) && memberId.equals(
					PrefUtils.getString(VideoPlayerActivity.this, ContantsActivity.LoginModle.LOGIN_USER_ID, ""))) {
				ToastUtils.showShort(VideoPlayerActivity.this, "不能跟自己私聊");
				return;
			}
			if (isAttentionVideo) {
				mIntent.setClass(VideoPlayerActivity.this, ChatActivity.class);
				mIntent.putExtra(EaseConstant.EXTRA_USER_ID, memberId);
				mIntent.putExtra(ContantsActivity.LoginModle.HEAD_URL, mUserHeadUrl);
				mIntent.putExtra(EaseConstant.EXTRA_CHAT_NICKNAME, nickName);
				mIntent.putExtra(EaseConstant.EXTRA_CHAT_HEADURL, headUrl);
				startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_CHART_LIST);
			} else {
				showAttentionDialog();
			}
			break;

		case R.id.share:
			if (mVideoPlayer != null && mVideoPlayer.isPlaying()) {
				stopVideo();
			}
			if (mCurrentVideoBean != null) {
				String des = mCurrentVideoBean.description;
				String nickName = mCurrentVideoBean.memberInfo.nickname;
				String title = null;
				if (TextUtils.isEmpty(des)) {
					title = String.format(share_title, nickName);
				} else {
					title = new String().format(share_title_all, nickName, des);
				}
				ShareContentBean sharebean = new ShareContentBean();
				sharebean.shareVideo = mCurrentVideoBean.videoUrl;
				sharebean.shareImage = mCurrentVideoBean.coverUrl;
				sharebean.shareContent = title;
				sharebean.shareUrl = this.shareUrl;
				showShareWindow(view, sharebean);
			} else {
				ToastUtils.showShort(VideoPlayerActivity.this, "找不到当前视频对象");
			}
			break;
		case R.id.attention_btn:
			addAttention();
			break;
		case R.id.user_head:
			if (!TextUtils.isEmpty(memberId)) {
				mIntent.setClass(VideoPlayerActivity.this, ActivityMemberInfor.class);
				mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, memberId);
				startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO);
			}

			break;

		case R.id.chart_iemo_btn:
			InputMethodUtils.hide(VideoPlayerActivity.this, view);
			mInputWindow.showEmjView();
			mInputWindow.show(mEditView, mEditView.getHint().toString());
			break;
		case R.id.chart_edit:
			if (mVideoPlayer != null && mVideoPlayer.isPlaying()) {
				stopVideo();
			}
			mEditView.requestFocus();
			mEditView.requestFocusFromTouch();
			InputMethodUtils.show(VideoPlayerActivity.this, view);
			mInputWindow.hideEmjView();
			mInputWindow.show(mEditView, mEditView.getHint().toString());
		}

	}

	private Dialog mAddAttentionDialog;

	private void showAttentionDialog() {
		if (mAddAttentionDialog == null) {
			mAddAttentionDialog = DialogUtils.confirmDialog(VideoPlayerActivity.this, "是否去加关注", "加关注", "取消",
					new ConfirmDialog() {

						@Override
						public void onOKClick(Dialog dialog) {
							addAttention();

						}

						@Override
						public void onCancleClick(Dialog dialog) {

						}

					});
		}
		if (!mAddAttentionDialog.isShowing()) {
			mAddAttentionDialog.show();
		}
	}

	/**
	 *  加关注
	 */
	private void addAttention() {
		if (!isAttentionVideo) {

			String type1 = isAttentionVideo ? "0" : "1";
			if (isAttentionVideo) {
				mVideoAttention.setText("关注");
				mVideoAttention.setEnabled(true);
			} else {
				mVideoAttention.setText("已关注");
				mVideoAttention.setEnabled(false);
			}
			isAttentionVideo = !isAttentionVideo;
			VideoHttpRequest.addAttention(VideoPlayerActivity.this, login_token, memberId, 0, type1,
					new HttpRequestCallBack2() {

						@Override
						public void callBackFail() {
							isAttentionVideo = !isAttentionVideo;
							if (isAttentionVideo) {
								mVideoAttention.setText("关注");
								mVideoAttention.setEnabled(true);
							} else {
								mVideoAttention.setText("已关注");
								mVideoAttention.setEnabled(false);
							}
						}
					});
		}
	}

	public void stopVideo() {
		if (mVideoPlayer != null && mVideoPlayer.isPlaying()) {
			mVideoPlayer.pause();
			showPlayerBtn(true);
			isUpdateTime = false;
			if (mLoadingBar != null) {
				mLoadingBar.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopVideo();
	}

	private void videoClick(String login_token, String videoId) {
		String rootUrl = "/video/click";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("videoId", videoId);
		LogUtils.e("视频点赞", "-------");
		BaseHttpNetMananger.getInstance(VideoPlayerActivity.this).postJSON(VideoPlayerActivity.this, rootUrl, params,
				null, false);
	}

	private VideoDeatailRightMenu mRightMenu;

	private void showVideoRightMenu(View view) {
		if (mRightMenu == null) {
			mRightMenu = new VideoDeatailRightMenu(memberId, videoId, VideoPlayerActivity.this);
		}
		if (user_id.equals(memberId)) {
			mRightMenu.setDelecteVideoCallBack(new DelecteVideoCallBack() {

				@Override
				public void detelectSuccess(String videoId) {
					Intent intent = new Intent();
					intent.putExtra(ContantsActivity.Video.VIDEO_POSITION, position);
					intent.putExtra(ContantsActivity.Video.VIDEO_DELECT, true);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			});
		}

		if (!mRightMenu.isShowing()) {
			mRightMenu.show(view);
		} else {
			mRightMenu.dismiss();
		}
	}

	private ShareThreePlateformWindow mShareWindow;

	private void showShareWindow(View view, ShareContentBean sharebean) {
		if (mShareWindow == null) {
			mShareWindow = new ShareThreePlateformWindow(VideoPlayerActivity.this);
			mShareWindow.setShareBean(sharebean);
		}
		if (!mShareWindow.isShowing()) {
			mShareWindow.show(view);
		} else {
			mShareWindow.dismiss();
		}

	}

	/**
	 * 是否显示playbutton
	 * 
	 * @param flag
	 */
	private void showPlayerBtn(boolean flag) {
		if (flag) {
			mVideoPlayerBtn.setImageResource(R.drawable.video_play_btn);

		} else {
			mVideoPlayerBtn.setImageResource(R.drawable.transparent1);
		}
	}

	@Override
	public void onPrepared(IMediaPlayer mp) {
		video_max_time = (int) (mVideoPlayer.getDuration() / 1000);
		if (mMainHandler != null && !isFinish) {
			mMainHandler.post(new Runnable() {

				@SuppressWarnings("static-access")
				@Override
				public void run() {
					mVideoSeekBar.setMax(video_max_time);
					mVideoAllTimeView.setText(
							new String().format(time_format, StringUtils.getDoubleByteTime(video_max_time / 60),
									StringUtils.getDoubleByteTime(video_max_time % 60)));
					mLoadingBar.setVisibility(View.GONE);
				}
			});
		}
	}

	@Override
	public void onCompletion(IMediaPlayer mp) {
		showPlayerBtn(true);
		isAddBrownCount = true;
		isUpdateTime = false;
		mVideoSeekBar.setProgress(video_max_time);
	}

	@Override
	public boolean onError(IMediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public void onSeekComplete(IMediaPlayer mp) {
		mp.start();
		isUpdateTime = true;
		showPlayerBtn(false);
	}

	public void requestData(final PullToRefreshLayout pullToRefreshLayout, final boolean isLoadMore) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("videoId", videoId);
		params.put("nowPage", nowPage + "");
		params.put("recordNum", recordNum + "");
		LogUtils.i("recommend", params.toString());
		LogUtils.e("获取视频评论列表", "-------");
		BaseHttpNetMananger.getInstance(VideoPlayerActivity.this).postJSON(VideoPlayerActivity.this, getCommontsTag,
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
								ToastUtils.showErrorToast(VideoPlayerActivity.this, code, msg, true);
								loadNoMore();
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}

						CommentsVideoList data = GsonUtils.json2Bean(req, CommentsVideoList.class);
						if (data == null) {
							loadNoMore();
							return;
						}
						List<CommentsVideoBean> list = data.data;
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
							if (mDatas != null) {
								mDatas.clear();
							}
						}
						nowPage++;
						if (mDatas != null) {
							mDatas.addAll(list);
							mAdapter.notifyDataSetChanged();
						}
						if (!isFinish && mMainHandler != null) {
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
		if (mMainHandler != null && mPullToRefreshListView != null)
			mMainHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (mPullToRefreshListView != null) {
						if (isLoadMore) {
							mPullToRefreshListView.loadmoreFinish(PullToRefreshLayout.FAIL);
						} else {
							mPullToRefreshListView.refreshFinish(PullToRefreshLayout.SUCCEED);
						}
					}
				}
			}, 1000);
	}

	/**
	 * 发生消息
	 *
	 * @param message
	 * @param toMemberId
	 */
	private void sendMessage(String message, String toMemberId) {
		Map<String, String> event = new HashMap<String, String>();
		event.put("video_id", videoId);
		event.put("type", "视频评论量");
		event.put("time", DateUtils.format9(System.currentTimeMillis()));
		MobclickAgent.onEvent(VideoPlayerActivity.this, "video_comment_ID", event);
		String rootUrl = "/video/comment";
		Map<String, String> params = new HashMap<String, String>();
		params.put("comment", message);
		params.put("token", login_token);
		if (!TextUtils.isEmpty(toMemberId)) {
			params.put("toMemberId", toMemberId);
		}
		params.put("videoId", videoId);
		LogUtils.e("发送消息", "-------");
		BaseHttpNetMananger.getInstance(VideoPlayerActivity.this).postJSON(VideoPlayerActivity.this, rootUrl, params,
				new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						isReplayVideo = true;
						try {
							JSONObject data = new JSONObject(req);
							int code = data.getInt("code");

							if (code == 100001) {
								mDatas.add(0, mReplyBean);
								mAdapter.notifyDataSetChanged();
								replyCount = replyCount + 1;
								String text = String.format(replyCountText, replyCount);
								mVideoVideoReplyView.setText(text);
								InputMethodUtils.hide(VideoPlayerActivity.this, mEditView);
								mEditView.setText("");

							} else {
								String msg = null;
								if (code == 900001) {
									msg = data.getString("msg");
								}
								ToastUtils.showErrorToast(VideoPlayerActivity.this, code, msg, true);
							}


							mEditView.setHint("请输入评论内容");
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(VideoPlayerActivity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(VideoPlayerActivity.this, getString(R.string.net_conected_error));
					}
				}, false);

	}

	// 获取点击事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(InputMethodUtils.hidSoftInput(ev, VideoPlayerActivity.this)){
			mEditView.setHint("请输入评论内容");
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 获取视频详情
	 */
	private void requestVideoDetail() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("videoId", videoId);
		LogUtils.e("获取视频详情信息", "-------");
		BaseHttpNetMananger.getInstance(VideoPlayerActivity.this).postJSON(VideoPlayerActivity.this, getVideoDetialTag,
				params, new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						LogUtils.i("video", req);
						int code = -1;
						String msg = "";
						try {
							JSONObject reqObj = new JSONObject(req);
							code = reqObj.getInt("code");
							if (code != 100001) {
								if (code == 500001) {
									showErrorDialog("视频不存在，请返回上一页");
								}
								if (code == 900001) {
									msg = reqObj.getString("msg");
								}
								ToastUtils.showErrorToast(VideoPlayerActivity.this, code, msg, true);
								return;
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						VideoBeanItem videoBean = GsonUtils.json2Bean(req, VideoBeanItem.class);
						if (videoBean == null) {
							return;
						}
						mCurrentVideoBean = videoBean.data;

						if (mCurrentVideoBean == null) {
							ToastUtils.showShort(VideoPlayerActivity.this, "获取视频失败");
							return;
						}
						refreshVideoDeatail(mCurrentVideoBean);

					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(VideoPlayerActivity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(VideoPlayerActivity.this, getString(R.string.net_conected_error));
					}
				}, false);
	}


	/**
	 * 主动请求网络数据
	 */
	private void loadAUTO() {
		isLoadMore = false;
		mPullToRefreshListView.autoRefresh();
	}

	/**
	 * 刷新界面
	 *
	 * @param bean
	 */
	@SuppressWarnings("static-access")
	private void refreshVideoDeatail(final BaseVideoBean bean) {
		if (bean != null) {
			if (mDatas.size() <= 0) {
				loadAUTO();
			}
			String coverUrl = bean.coverUrl;
			if (TextUtils.isEmpty(coverUrl)) {
				coverUrl = "";
			} else {
				coverUrl = FormatImageURLUtils.formatURL(coverUrl, ContantsActivity.Image.MODLE,
						ContantsActivity.Image.MODLE);
			}
			mVideoConverImage.setImageURI(Uri.parse(coverUrl));

			headUrl = bean.memberInfo.headerUrl;
			if (TextUtils.isEmpty(headUrl)) {
				headUrl = "";
			} else {
				headUrl = FormatImageURLUtils.formatURL(headUrl, ContantsActivity.Image.SMALL,
						ContantsActivity.Image.SMALL);
			}
			mHeadView.setImageURI(Uri.parse(headUrl));

			nickName = bean.memberInfo.nickname;
			if (TextUtils.isEmpty(nickName)) {
				nickName = "匿名舞者";
			}
			mNickNameView.setText(nickName);

			String createTime = DateUtils.formateVideoCreateTime(bean.createTime);
			mVideoUpdateTime.setText(createTime);

			memberId = bean.memberId;
			if (!TextUtils.isEmpty(memberId) && memberId.equals(
					PrefUtils.getString(VideoPlayerActivity.this, ContantsActivity.LoginModle.LOGIN_USER_ID, ""))) {
				mVideoAttention.setVisibility(View.GONE);
			} else {
				mVideoAttention.setVisibility(View.VISIBLE);
				isAttentionVideo = bean.memberInfo.isAttention.equals("1");
				if (isAttentionVideo) {
					mVideoAttention.setText("已关注");
					mVideoAttention.setEnabled(false);
				} else {
					mVideoAttention.setText("关注");
					mVideoAttention.setEnabled(true);
				}
			}
			likeCount = bean.likeCount;
			if (likeCount <= 0) {
				likeCount = 0;
			}
			mVideoLikeView.setText(likeCount + "");
			isLikedVideo = bean.isLike.equals("1");
			if (isLikedVideo) {
				mVideoLikeView.setSelected(true);
			} else {
				mVideoLikeView.setSelected(false);
			}
			replyCount = bean.commentCount;

			if (replyCount <= 0) {
				replyCount = 0;
			}
			String text = String.format(replyCountText, replyCount);
			mVideoVideoReplyView.setText(text);
			String desVideo = bean.description;

			TopicBean topic = bean.topic;
			String topicName = "";
			if (topic != null) {
				topicName = topic.topicName;
			}
			if (TextUtils.isEmpty(desVideo) && TextUtils.isEmpty(topicName)) {
				mVideoDetail.setVisibility(View.GONE);
			} else {
				mVideoDetail.setVisibility(View.VISIBLE);
				if (TextUtils.isEmpty(topicName)) {

					mVideoDetail.setText(desVideo);
				} else {
					topicName = "#" + topicName + "#";
					TextViewSpannerUtils.handleText(VideoPlayerActivity.this, topicName + desVideo, 0,
							topicName.length(), 0xffede02b, mVideoDetail, new TextViewSpannerUtils.OnClickCallBack() {
								@Override
								public void click() {
									mIntent.setClass(VideoPlayerActivity.this, ActivityTopicDetail.class);
									if (bean.topic != null) {
										mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID, bean.topic.topicId);
										mIntent.putExtra(ContantsActivity.Topic.TOPIC_NAME,
												"#" + bean.topic.topicName + "#");
									}
									startActivityForResult(mIntent, -1);
								}
							});
				}
			}

			videoUrl = bean.videoUrl;
			mEditView.setHint("请输入评论内容");


			mVideoBrowserCount = bean.clickCount;

			mVideoBrowserCountView.setText(mVideoBrowserCount + "观看");
			shareUrl = bean.shareUrl;
		} else {
			ToastUtils.showShort(VideoPlayerActivity.this, "该视频不存在");
		}
	}

	/**
	 * 初始化输入框布局
	 */
	private void initInputLayout() {
		// mEditFramLayout = findViewById(R.id.edit_layout);
		mOptionEmoj = (ImageView) findViewById(R.id.chart_iemo_btn);
		mOptionEmoj.setOnClickListener(this);
		mSendBtn = (TextView) findViewById(R.id.btn_send);
		mSendBtn.setOnClickListener(this);
		mEditView = (TextView) findViewById(R.id.chart_edit);
		mEditView.requestFocusFromTouch();
		mEditView.setFocusable(true);
		mEditView.setOnClickListener(this);
		mInputWindow = new InputPopwindow(VideoPlayerActivity.this);
		mInputWindow.setInputCallBack(mInputCallBack);
	}

	@Override
	public void onBufferingUpdate(IMediaPlayer mp, int percent) {

	}

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		switch (what) {
		case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
			if (mLoadingBar != null) {
				mLoadingBar.setVisibility(View.VISIBLE);
			}
			break;
		case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (mLoadingBar != null) {
				mLoadingBar.setVisibility(View.GONE);
			}
			break;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mShareWindow != null) {
			mShareWindow.onActivityResult(requestCode, resultCode, data);
		}
	}

	private InputCallBack mInputCallBack = new InputCallBack() {

		@Override
		public void getInputCallBack(String input, boolean isSend) {
			mEditView.setText(EaseSmileUtils.getSmiledText(VideoPlayerActivity.this, input), BufferType.SPANNABLE);
			if (!isSend) {
				return;
			}

			InputMethodUtils.hide(VideoPlayerActivity.this, mEditView);
			String comment = mEditView.getText().toString();
			if (TextUtils.isEmpty(comment)) {
				ToastUtils.showShort(VideoPlayerActivity.this, "内容不能为空");
				return;
			}
			int len = comment.length();
			if (len > 140) {
				ToastUtils.showShort(VideoPlayerActivity.this, "内容长度不能超过140字");
				return;
			}
//			if (mReplyBean == null){
//				mReplyBean = new CommentsVideoBean();
//				mReplyBean.memberInfo = new UserExtendsInfo();
//				mReplyBean.memberInfo.headerUrl = PrefUtils.getString(VideoPlayerActivity.this,
//						ContantsActivity.LoginModle.HEAD_URL, "");
//				mReplyBean.memberInfo.nickname = PrefUtils.getString(VideoPlayerActivity.this,
//						ContantsActivity.LoginModle.NICK_NAME, "");
//				mReplyBean.isLike = 0;
//				mReplyBean.memberId = user_id;
//		     }
			String toMemberId = null;
			if (isReplayVideo) {
				mReplyBean = new CommentsVideoBean();
				mReplyBean.memberInfo = new UserExtendsInfo();
				mReplyBean.memberInfo.headerUrl = PrefUtils.getString(VideoPlayerActivity.this,
						ContantsActivity.LoginModle.HEAD_URL, "");
				mReplyBean.memberInfo.nickname = PrefUtils.getString(VideoPlayerActivity.this,
						ContantsActivity.LoginModle.NICK_NAME, "");
				mReplyBean.isLike = 0;
				mReplyBean.memberId = user_id;
				toMemberId = null;
				mReplyBean.toMemberId = null;
				mReplyBean.toMemberNickname = null;
			} else {
				toMemberId = mReplyBean.toMemberId;
			}
			mReplyBean.createTime = DateUtils.format9(new Date());
			mReplyBean.comment = comment;
			showLoadingDialog();
			sendMessage(comment, toMemberId);
		}

		@Override
		public void hideCallBack() {
			mEditView.setHint("请输入评论内容");
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getCommontsTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getVideoDetialTag);
		}
		if (mDatas != null) {
			mDatas = null;
		}
		if (mReplyBean != null) {
			mReplyBean = null;
		}
		if (timeHander != null) {
			timeHander.removeMessages(UPDATE_TIME);
			timeHander = null;
		}
		if (timeThread != null) {
			timeThread = null;
		}
		isFinish = true;
		if (mVideoPlayer != null) {
			if (mVideoPlayer.isPlaying()) {
				mVideoPlayer.release(true);
				mVideoPlayer = null;
			}
		}
		if (mAdapter != null) {
			mAdapter = null;
		}
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView = null;
		}
		if (mLoadingBar != null) {
			mLoadingBar = null;
		}
		if (mInputWindow != null) {
			mInputWindow.release();
			mInputWindow = null;
		}
		ImagePipeline imagePipeline = Fresco.getImagePipeline();
		//清空内存缓存（包括Bitmap缓存和未解码图片的缓存）
		imagePipeline.clearMemoryCaches();
		System.gc();
	}

}
