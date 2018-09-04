package com.shizhong.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnSeekCompleteListener;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.ActivityTopicDetail;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.VideoPlayerActivity;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.VideoHttpRequest;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.StringUtils;
import com.shizhong.view.ui.base.utils.TextViewSpannerUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.view.ShareThreePlateformWindow;
import com.shizhong.view.ui.base.view.ijksample.IjkVideoView;
import com.shizhong.view.ui.bean.BaseVideoBean;
import com.shizhong.view.ui.bean.ShareContentBean;
import com.shizhong.view.ui.bean.TopicBean;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.umeng.analytics.MobclickAgent;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuliyan on 16/1/26.
 */
public class VideoPlayingListAdapter extends BaseAdapter implements View.OnClickListener, OnPreparedListener,
		OnCompletionListener, OnErrorListener, OnSeekCompleteListener, OnInfoListener {

	private Context mContext;
	private List<BaseVideoBean> list;
	public LayoutInflater mInflater;

	public Intent mIntent = new Intent();
	private int width;

	private Handler mMainHandler = new Handler();
	private FrameLayout.LayoutParams layoutParams1;
	private FrameLayout.LayoutParams layoutParams2;
	private String login_token;
	private IjkVideoView mVideoPlayer;
	private TextView currentPlayTimeView;
	private TextView maxPlayTimeView;
	private SeekBar progressBarView;
	private ProgressBar loadindBar;
	private FrameLayout videoParentView;
	private ImageView currentPlayBtn;
	private int video_max_time;
	private final String time_format = "%s:%s";
	private final int UPDATE_TIME = 0x3;
	private boolean isUpdateTime = false;
	private int currentPosition = -1;
	private boolean isFinishThread = false;

	private final String share_title = "分享 %s 的失重街舞视频，快来围观。";
	private final String share_title_all = "分享 %s 的失重街舞视频， %s ，快来围观。";

	private ShareThreePlateformWindow mShareWindow;
	private boolean isAddBrowCount = true;

	private String user_id;
	private boolean isUserPager;// 是否属于个人主页的视频列表

	public VideoPlayingListAdapter(Context context, List<BaseVideoBean> list) {
		this(context, list, false);
	}

	public VideoPlayingListAdapter(Context context, List<BaseVideoBean> list, boolean isUserPager) {
		super();
		mContext = context;
		this.list = list;
		mInflater = LayoutInflater.from(mContext);
		user_id = PrefUtils.getString(mContext, ContantsActivity.LoginModle.LOGIN_USER_ID, "");
		width = UIUtils.getScreenWidthPixels(mContext);
		layoutParams1 = new FrameLayout.LayoutParams(width, width + UIUtils.dipToPx(mContext, 22), Gravity.CENTER);
		layoutParams2 = new FrameLayout.LayoutParams(width, width);
		login_token = PrefUtils.getString(mContext, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		this.isUserPager = isUserPager;
	}

	class ViewHolder {
		SimpleDraweeView headImage;
		TextView nickName;
		TextView updateTime;
		TextView desText;
		TextView likeBtn;
		TextView litterBtn;
		TextView shareBtn;
		SimpleDraweeView coverImage;
		ImageView playImage;
		TextView currentTimeText;
		TextView maxTimeText;
		SeekBar progressBar;
		ProgressBar loadingBar;

	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return list != null ? list.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_dynamic_video_layout, null);
			holder = new ViewHolder();
			holder.headImage = (SimpleDraweeView) convertView.findViewById(R.id.user_head);
			if (!isUserPager) {
				holder.headImage.setOnClickListener(this);
			}
			holder.nickName = (TextView) convertView.findViewById(R.id.nick_name);
			holder.updateTime = (TextView) convertView.findViewById(R.id.update_time);
			holder.coverImage = (SimpleDraweeView) convertView.findViewById(R.id.video_cover_page);
			holder.coverImage.setLayoutParams(layoutParams1);
			holder.coverImage.setOnClickListener(this);
			holder.playImage = (ImageView) convertView.findViewById(R.id.video_play_btn);
			holder.playImage.setLayoutParams(layoutParams1);
			holder.playImage.setOnClickListener(this);
			holder.desText = (TextView) convertView.findViewById(R.id.des_video);
			holder.likeBtn = (TextView) convertView.findViewById(R.id.like);
			holder.likeBtn.setOnClickListener(this);
			holder.litterBtn = (TextView) convertView.findViewById(R.id.replay);
			holder.litterBtn.setOnClickListener(this);
			holder.shareBtn = (TextView) convertView.findViewById(R.id.share);
			holder.shareBtn.setOnClickListener(this);
			holder.currentTimeText = (TextView) convertView.findViewById(R.id.play_time_text);
			holder.maxTimeText = (TextView) convertView.findViewById(R.id.video_play_time);
			holder.progressBar = (SeekBar) convertView.findViewById(R.id.video_seek_bar);
			holder.loadingBar = (ProgressBar) convertView.findViewById(R.id.loading_progress);
			convertView.setTag(R.string.app_name, holder);
		} else {
			holder = (ViewHolder) convertView.getTag(R.string.app_name);
		}
		remove();
		holder.progressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (mVideoPlayer != null) {
					isUpdateTime = true;
					mVideoPlayer.seekTo(seekBar.getProgress() * 1000);
					if (timeThread == null) {
						initTimeManager();
					}
				}
				LogUtils.i("progress", "progress:" + seekBar.getProgress() * 1000);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				if (mVideoPlayer != null && mVideoPlayer.isPlaying()) {
					isUpdateTime = false;
					mVideoPlayer.pause();
				}
				if (timeThread == null) {
					initTimeManager();
				}
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			}
		});
		final BaseVideoBean data = list.get(position);
		data.position = position;
		UserExtendsInfo memeberInfo = data.memberInfo;
		String headUrl = memeberInfo.headerUrl;
		if (TextUtils.isEmpty(headUrl)) {
			headUrl = "";
		} else {
			headUrl = FormatImageURLUtils.formatURL(headUrl, ContantsActivity.Image.SMALL,
					ContantsActivity.Image.SMALL);
		}
		holder.headImage.setImageURI(Uri.parse(headUrl));
		// imageLoad.displayImage(headUrl, holder.headImage, headOptions);
//		Glide.with(mContext).load(headUrl).placeholder(R.drawable.sz_head_default).error(R.drawable.sz_head_default)
//				.transform(new GlideCircleTransform(mContext)).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//				.into(holder.headImage);
		String nickName = memeberInfo.nickname;
		if (TextUtils.isEmpty(nickName)) {
			nickName = "匿名舞者";
		}
		holder.nickName.setText(nickName);

		String updateTime = DateUtils.formateVideoCreateTime(data.createTime);
		holder.updateTime.setText(updateTime);

		String coverUrl = data.coverUrl;
		if (TextUtils.isEmpty(coverUrl)) {
			coverUrl = "";
		} else {
			coverUrl = FormatImageURLUtils.formatURL(coverUrl, ContantsActivity.Image.MODLE,
					ContantsActivity.Image.MODLE);
		}
		holder.coverImage.setImageURI(Uri.parse(coverUrl));
//		Glide.with(mContext).load(coverUrl).placeholder(R.drawable.sz_activity_default)
//				.error(R.drawable.sz_activity_default).transform(new GlideRoundTransform(mContext, 10)).centerCrop()
//				.diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(holder.coverImage);
		final TopicBean topic = data.topic;
		String topicName = "";
		if (topic != null) {
			topicName = topic.topicName;
		}
		if (TextUtils.isEmpty(topicName)) {
			topicName = "";
		} else {
			topicName = String.format("#%s#", topicName);
		}

		String desText = data.description;
		if (TextUtils.isEmpty(desText)) {
			desText = "";
		}
		desText = topicName + desText;
		LogUtils.i("destext", desText);
		if (TextUtils.isEmpty(desText.trim())) {
			holder.desText.setVisibility(View.GONE);
		} else {
			holder.desText.setVisibility(View.VISIBLE);
			TextViewSpannerUtils.handleText(mContext, desText, 0, topicName.length(), 0xffede02b, holder.desText,
					new TextViewSpannerUtils.OnClickCallBack() {
						@Override
						public void click() {
							mIntent.setClass(mContext, ActivityTopicDetail.class);
							if (topic != null) {
								mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID, topic.topicId);
								mIntent.putExtra(ContantsActivity.Topic.TOPIC_NAME, topic.topicName);
							}
							((Activity) mContext).startActivityForResult(mIntent, -1);
						}
					});

		}

		long likeCount = data.likeCount;
		if (likeCount <= 0) {
			likeCount = 0;
		}
		holder.likeBtn.setText(likeCount + "");
		String isLike = data.isLike;
		if ("0".equals(isLike)) {
			holder.likeBtn.setSelected(false);
		} else if ("1".equals(isLike)) {
			holder.likeBtn.setSelected(true);
		}

		holder.loadingBar.setVisibility(View.GONE);
		long replyCount = data.commentCount;
		if (replyCount <= 0) {
			replyCount = 0;
		}
		holder.currentTimeText.setText("00:00");
		holder.maxTimeText.setText("00:00");
		holder.progressBar.setProgress(0);
		showPlayerBtn(holder.playImage, true);
		holder.litterBtn.setText(replyCount + "");
		holder.likeBtn.setTag(R.string.app_name, data);
		holder.litterBtn.setTag(R.string.app_name, data);
		holder.headImage.setTag(R.string.app_name, data.memberId);
		holder.playImage.setTag(R.string.app_name, data);
		holder.shareBtn.setTag(R.string.app_name, data);
		return convertView;
	}

	private void showShareWindow(View view, ShareContentBean sharebean) {
		if (mContext != null) {
			if (mShareWindow != null) {
				if (mShareWindow.isShowing()) {
					mShareWindow.dismiss();
				}
			}
			mShareWindow = new ShareThreePlateformWindow(mContext);
			LogUtils.i("sharebean", "------------sharebean[" + sharebean.toString() + "]");
			mShareWindow.setShareBean(sharebean);
			mShareWindow.show(view);
		}
	}

	private Thread timeThread = null;
	private Handler timeHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_TIME:
				if (isUpdateTime) {
					if (currentPlayTimeView != null && progressBarView != null) {
						currentPlayTimeView.setText((String) msg.obj);
						progressBarView.setProgress(msg.arg1);
					}
				}
				break;
			}
		}
	};

	private void initTimeManager() {
		if (timeThread != null) {
			timeThread = null;
		}
		timeThread = new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				while (!isFinishThread) {
					if (isUpdateTime) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (mVideoPlayer != null) {
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
		timeThread.start();
	}

	@SuppressWarnings("static-access")
	@Override
	public void onClick(final View view) {
		switch (view.getId()) {
		case R.id.video_play_btn:
			BaseVideoBean tag = (BaseVideoBean) view.getTag(R.string.app_name);
			int position = tag.position;
			String videoUrl = tag.videoUrl;
			if (timeThread == null) {
				initTimeManager();
			}
			if (currentPosition == position) {
				if (mVideoPlayer == null) {
					remove();
					currentPlayBtn = (ImageView) view;
					videoParentView = (FrameLayout) view.getParent();
					addVideoPlayer(currentPlayBtn, videoUrl);
					isAddBrowCount = true;
				}
			} else {
				remove();
				currentPlayBtn = (ImageView) view;
				videoParentView = (FrameLayout) view.getParent();
				addVideoPlayer(currentPlayBtn, videoUrl);
				isAddBrowCount = true;
			}
			if (mVideoPlayer.isPlaying()) {
				showPlayerBtn((ImageView) view, true);
				isUpdateTime = false;
				mVideoPlayer.pause();
			} else {
				showPlayerBtn(currentPlayBtn, false);
				mVideoPlayer.start();
				isUpdateTime = true;

				if (isAddBrowCount) {
					videoClick(user_id, tag.videoId);
					Map<String, String> event = new HashMap<String, String>();
					event.put("video_id", tag.videoId);
					event.put("type", "视频查看量");
					event.put("time", DateUtils.format9(System.currentTimeMillis()));
					MobclickAgent.onEvent(mContext, "video_open_ID", event);
					isAddBrowCount = false;
				}
			}
			currentPosition = position;
			break;
		case R.id.like:
			BaseVideoBean bean = (BaseVideoBean) view.getTag(R.string.app_name);
			final String videoId = bean.videoId;
			String type = bean.isLike;
			boolean isLike = bean.isLike.equals("1");
			type = isLike ? "0" : "1";
			final int pos = bean.position;
			bean.likeCount = isLike ? (bean.likeCount - 1 > 0 ? bean.likeCount - 1 : 0) : bean.likeCount + 1;
			bean.isLike = type;
			list.set(pos, bean);
			long likeCount = bean.likeCount;
			if (likeCount <= 0) {
				likeCount = 0;
			}
			if ("0".equals(type)) {
				((TextView) view).setSelected(false);
			} else if ("1".equals(type)) {
				((TextView) view).setSelected(true);
			}
			((TextView) view).setText(likeCount + "");
			VideoHttpRequest.voteVideo(mContext, videoId, type, pos, login_token,
					new VideoHttpRequest.HttpRequestCallBack() {

						@Override
						public void callFailBack() {
							BaseVideoBean bean = list.get(pos);
							String type = bean.isLike;
							boolean isLike = type.equals("1");
							bean.isLike = isLike ? "0" : "1";
							bean.likeCount = isLike ? (bean.likeCount - 1 > 0 ? bean.likeCount - 1 : 0)
									: bean.likeCount + 1;

							long likeCount = bean.likeCount;
							if (likeCount <= 0) {
								likeCount = 0;
							}
							if ("0".equals(type)) {
								((TextView) view).setSelected(false);
							} else if ("1".equals(type)) {
								((TextView) view).setSelected(true);
							}
							((TextView) view).setText(likeCount + "");
							list.set(pos, bean);
						}

					});

			break;
		case R.id.replay:
			bean = (BaseVideoBean) view.getTag(R.string.app_name);
			mIntent.setClass(mContext, VideoPlayerActivity.class);
			mIntent.putExtra(ContantsActivity.Video.VIDEO_ID, bean.videoId);
			mIntent.putExtra(ContantsActivity.Video.VIDEO_POSITION, bean.position);
			((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.ACTION_SKIP_VIDEO_DETALE);
			break;
		case R.id.share:
			if (mVideoPlayer != null && mVideoPlayer.isPlaying()) {
				mVideoPlayer.pause();
				isUpdateTime = false;
				showPlayerBtn(currentPlayBtn, true);
			}
			// ToastUtils.showShort(mContext, "分享视频未做");
			BaseVideoBean data = (BaseVideoBean) view.getTag(R.string.app_name);
			String des = data.description;
			String nickName = data.memberInfo.nickname;
			String title = null;
			if (TextUtils.isEmpty(des)) {
				title = String.format(share_title, nickName);
			} else {
				title = new String().format(share_title_all, nickName, des);
			}

			ShareContentBean sharebean = new ShareContentBean();
			sharebean.shareVideo = data.videoUrl;
			sharebean.shareImage = data.coverUrl;
			sharebean.shareContent = title;
			sharebean.shareUrl = data.shareUrl;
			showShareWindow(view, sharebean);
			break;
		case R.id.user_head:
			String memberId = (String) view.getTag(R.string.app_name);
			if (!TextUtils.isEmpty(memberId)) {
				mIntent.setClass(mContext, ActivityMemberInfor.class);
				mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, memberId);
				((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO);
				((Activity) mContext).overridePendingTransition(R.anim.dialog_right_in_anim,
						R.anim.dialog_right_out_anim);
			}
		}

	}

	private void videoClick(String login_token, String videoId) {
		String rootUrl = "/video/click";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("videoId", videoId);
		LogUtils.e("视频播放", "-------");
		BaseHttpNetMananger.getInstance(mContext).postJSON(mContext, rootUrl, params, null, false);
	}

	@Override
	public void onSeekComplete(IMediaPlayer mp) {
		mp.start();
		isUpdateTime = true;
		showPlayerBtn(currentPlayBtn, false);
	}

	@Override
	public boolean onError(IMediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public void onCompletion(IMediaPlayer mp) {
		isUpdateTime = false;
		showPlayerBtn(currentPlayBtn, true);
		progressBarView.setProgress(video_max_time);
		isAddBrowCount = true;
	}

	@Override
	public void onPrepared(IMediaPlayer mp) {
		if (mVideoPlayer != null) {
			video_max_time = (int) (mVideoPlayer.getDuration() / 1000);
			mMainHandler.post(new Runnable() {
				@SuppressWarnings("static-access")
				@Override
				public void run() {
					progressBarView.setMax(video_max_time);
					maxPlayTimeView.setText(
							new String().format(time_format, StringUtils.getDoubleByteTime(video_max_time / 60),
									StringUtils.getDoubleByteTime(video_max_time % 60)));
					loadindBar.setVisibility(View.GONE);
				}
			});
		}
	}

	public void remove() {
		if (videoParentView != null && mVideoPlayer != null) {
			mVideoPlayer.stop();
			progressBarView.setProgress(0);
			loadindBar.setVisibility(View.GONE);
			currentPlayTimeView.setText("00:00");
			videoParentView.removeView(mVideoPlayer);
			videoParentView = null;
			showPlayerBtn(currentPlayBtn, true);
			mVideoPlayer.release(true);
			mVideoPlayer = null;
			System.gc();
		}

	}

	private void addVideoPlayer(View v, final String videoUrl) {
		mVideoPlayer = new IjkVideoView(mContext);
		mVideoPlayer.setOnCompletionListener(this);
		mVideoPlayer.setOnErrorListener(this);
		mVideoPlayer.setOnPreparedListener(this);
		mVideoPlayer.setLayoutParams(layoutParams2);
		mVideoPlayer.setBackgroundResource(R.drawable.shape_black_retangle);
		videoParentView.removeViewInLayout(v);
		mVideoPlayer.setOnSeekCompleteListener(this);
		mVideoPlayer.setOnInfoListener(this);
		videoParentView.addView(mVideoPlayer, 1);
		videoParentView.addView(v, 2);
		mVideoPlayer.setVideoPath(videoUrl);
		mVideoPlayer.setId(R.id.video_player);
		currentPlayTimeView = (TextView) videoParentView.findViewById(R.id.play_time_text);
		maxPlayTimeView = (TextView) videoParentView.findViewById(R.id.video_play_time);
		progressBarView = (SeekBar) videoParentView.findViewById(R.id.video_seek_bar);
		loadindBar = (ProgressBar) videoParentView.findViewById(R.id.loading_progress);
		loadindBar.setVisibility(View.VISIBLE);
	}

	/**
	 * 是否显示playbutton
	 * 
	 * @param flag
	 */
	private void showPlayerBtn(ImageView v, boolean flag) {
		if (flag) {
			v.setImageResource(R.drawable.video_play_btn);

		} else {
			v.setImageResource(R.drawable.transparent1);
		}
	}

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		switch (what) {
		case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
			if (loadindBar != null) {
				loadindBar.setVisibility(View.VISIBLE);
			}
			break;
		case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (loadindBar != null) {
				loadindBar.setVisibility(View.GONE);
			}

			break;
		}
		return false;
	}

	public void onFragmentStop() {
		if (mVideoPlayer != null && mVideoPlayer.isPlaying()) {
			remove();
			showPlayerBtn(currentPlayBtn, true);
			System.gc();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mShareWindow != null) {
			mShareWindow.onActivityResult(requestCode, resultCode, data);
		}
		switch (requestCode) {
		case ContantsActivity.Action.ACTION_SKIP_VIDEO_DETALE:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					int position = data.getIntExtra(ContantsActivity.Video.VIDEO_POSITION, 0);
					boolean isDelect = data.getBooleanExtra(ContantsActivity.Video.VIDEO_DELECT, false);
					if (isDelect) {
						if(list.size()>0) {
							int len = list.size();
							System.out.println(len);
							list.remove(position);
						}
					} else {
						boolean isLike = data.getBooleanExtra(ContantsActivity.Video.VIDEO_IS_LIKED, false);
						long likeCount = data.getLongExtra(ContantsActivity.Video.VIDEO_LIKE_NUM, 0);
						long replyCount = data.getLongExtra(ContantsActivity.Video.VIDEO_REPLY_COUNT, 0);
						if (list.size() > position) {
							BaseVideoBean bean = list.get(position);
							bean.isLike = isLike ? "1" : "0";
							bean.likeCount = likeCount;
							bean.commentCount = replyCount;
							list.set(position, bean);
						}
					}
					notifyDataSetChanged();
				}
			}
			break;

		}
	}

}
