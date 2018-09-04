package com.shizhong.view.ui.video;

import java.io.File;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.utils.CameraManager;
import com.shizhong.view.ui.base.utils.DataCleanManager;
import com.shizhong.view.ui.base.utils.FileUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.SDUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.utils.VideoUtils;
import com.shizhong.view.ui.base.view.TextureVideoView.OnPlayStateListener;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.VideoSelectionView;
import com.shizhong.view.ui.base.view.VideoSelectionView.OnSeekBarChangeListener;
import com.shizhong.view.ui.base.view.VideoSelectionView.OnSwich60sListener;
import com.shizhong.view.ui.base.view.VideoSelectionView.OnVideoChangeScaleTypeListener;
import com.wind.ffmpeghelper.FFmpegNativeHelper;
import com.wind.ffmpeghelper.FFmpegUtils;
import com.wind.ffmpeghelper.VideoHandlerCallBack;
import com.shizhong.view.ui.base.view.VideoViewTouch;
import com.hyphenate.easeui.ContantsActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
//import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VideoCutOutActivity extends BaseMediaActivity implements OnClickListener, OnPreparedListener,
		OnPlayStateListener, OnInfoListener, OnVideoSizeChangedListener, OnErrorListener, OnSeekCompleteListener,
		OnSeekBarChangeListener, OnSwich60sListener, OnVideoChangeScaleTypeListener {
	/** 返回 */
	private ImageView img_back;
	/** 确认 */
	private ImageView txt_enter;
	private VideoViewTouch mVideoView;
	/** 显示播放 */
	private ImageView mPlayController;
	/** 操作提示 */
	private ImageView mTipsMoveZhongxiang, mTipsMoveHengxiang;
	/** 视频区域选择 */
	private VideoSelectionView mVideoSelection;
	private LinearLayout mPreviewLinearLayout;
	/** 屏幕的宽度 */
	private int mWindowWidth;
	/** 视频旋转角度 */
	private int mVideoRotation = 0;
	/** 播放路径 */
	private String mSourcePath;

	private String mTempFilePath;// 临时文件所保存的路径

	private String mTempSourcesCopyName;
	private String mTempVideoName;
	private String mTempAudioName;
	private String mTargetFilePath;// 目标文件所存路径
	private String mTargetFileName;

	private boolean mIsFitCenter;
	/** 预先裁剪是否完成 */
	private int scale = 1;
	private static boolean isFirstImport_H = false;
	private static boolean isFirstImport_V = false;
	private boolean isRetangleVideo;
	private int mVideoWidth;
	private int mVideoHeight;
	private final int CODE_IMPORT_VIDEO = 0x901;
	private String topic_name;
	private int topic_page;
	private String topic_id;
	private final int Standardbitrate = 640 * 1000;// 设置视频的码率
	private final int Cropbitrate = 960 * 1000;
	private static final int ACTION_ADD_WATER = 2;

	private String mFilterPngName;
	private boolean hasAudioResourse;// 判断是否存在音频文件
	private int cutVideoDuring;
	private File mThumbImagePath;
	private boolean isDecode;
	private boolean isCompose;// 是否走一步

	/** 视频时长 */
	private int mDuration = -1;

	@Override
	protected void initBundle() {
		mSourcePath = getIntent().getStringExtra(ContantsActivity.Video.VIDEO_PATH);
		topic_name = getIntent().getStringExtra(ContantsActivity.Topic.TOPIC_NAME);
		topic_page = getIntent().getIntExtra(ContantsActivity.Topic.TOPIC_PAGE, 1);
		topic_id=getIntent().getStringExtra(ContantsActivity.Topic.TOPIC_ID);

	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_video_cut_out_layout);

		mWindowWidth = UIUtils.getScreenWidthPixels(VideoCutOutActivity.this);
		img_back = (ImageView) findViewById(R.id.left_bt);
		img_back.setOnClickListener(this);
		txt_enter = (ImageView) findViewById(R.id.right_bt);
		txt_enter.setImageResource(R.drawable.sz_finish_icon);
		txt_enter.setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText("裁剪");

		mVideoView = (VideoViewTouch) findViewById(R.id.preview);
		mPreviewLinearLayout = (LinearLayout) mVideoView.getParent();
		mPlayController = (ImageView) findViewById(R.id.play_controller);
		mVideoSelection = (VideoSelectionView) findViewById(R.id.video_selection_view);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnPlayStateListener(this);
		mVideoView.setOnTouchEventListener(mOnVideoTouchListener);
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnVideoSizeChangedListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnSeekCompleteListener(this);
		mVideoSelection.setOnSeekBarChangeListener(this);
		mVideoSelection.setOnSwich60sListener(this);
		// mVideoSelection.setOnBackgroundColorListener(this);
		mVideoSelection.setOnVideoChangeScaleTypeListener(this);

		initSurfaceView();

		new AsyncTask<Void, Void, Boolean>() {
			protected void onPreExecute() {
				showProgress("视频载入...");
			};

			@Override
			protected Boolean doInBackground(Void... params) {
				String fileName = System.currentTimeMillis() + "";
				mTempFilePath = CameraManager.getVideoCachePath().concat(fileName).concat(File.separator)
						.concat("themp");
				mTargetFilePath = CameraManager.getVideoCachePath().concat(fileName);
				mThumbImagePath = CameraManager.getThumbImages();
				if (!FileUtils.checkFile(mTempFilePath)) {
					new File(mTempFilePath).mkdirs();
				}
				mTempSourcesCopyName = mTargetFilePath.concat(File.separator).concat(fileName).concat(".mp4");
				return FileUtils.fileCopy(mSourcePath, mTempSourcesCopyName);
			}

			protected void onPostExecute(Boolean result) {
				hideProgress();
				if (result) {
					mVideoRotation = VideoUtils.getVideoRotate(mTempSourcesCopyName);
					hasAudioResourse = VideoUtils.hasAudioResources(mTempSourcesCopyName);
					switch (mVideoRotation % 360) {
					case 180:
					case 0:
						mVideoWidth = VideoUtils.getVideoWidth(mTempSourcesCopyName);
						mVideoHeight = VideoUtils.getVideoHeight(mTempSourcesCopyName);
						break;
					case 90:
					case 270:
						mVideoHeight = VideoUtils.getVideoWidth(mTempSourcesCopyName);
						mVideoWidth = VideoUtils.getVideoHeight(mTempSourcesCopyName);
					}
					if (mVideoHeight == mVideoWidth) {
						isRetangleVideo = false;
						isFirstImport_H = false;
						isFirstImport_V = false;
					} else {
						isRetangleVideo = true;
						if (mVideoHeight > mVideoWidth) {
							isFirstImport_H = true;
						} else {
							isFirstImport_V = true;
						}
					}
					mVideoSelection.setChangeBackGroundVisiblty(isRetangleVideo);
					if (mVideoHeight > mVideoWidth) {
						mVideoSelection.setRectangleBackground(R.drawable.cut_landscape1_selector);
					} else if (mVideoHeight < mVideoWidth) {
						mVideoSelection.setRectangleBackground(R.drawable.cut_landscape_selector);
					}
					mVideoSelection.setDefaultModel();
					mVideoView.setVideoPath(mTempSourcesCopyName);
					mFilterPngName = CameraManager.getFilterPngName();
				}

			};
		}.execute();

	}

	private void initSurfaceView() {

		// 宽高一致
		View preview_layout = findViewById(R.id.preview_layout);
		RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) preview_layout.getLayoutParams();
		mParams.height = mWindowWidth;
		preview_layout.setVisibility(View.VISIBLE);

		View cropView = findViewById(R.id.cropView);
		int cropHeight = (int) (mWindowWidth * 1.0f * 9 / 16);
		int topMargin = UIUtils.dipToPx(this, 49) + (mWindowWidth - cropHeight) / 2;
		RelativeLayout.LayoutParams cropViewParam = (RelativeLayout.LayoutParams) cropView.getLayoutParams();
		cropViewParam.width = mWindowWidth;
		cropViewParam.height = cropHeight;
		cropViewParam.topMargin = topMargin;
		cropView.setLayoutParams(cropViewParam);
	}

	private VideoViewTouch.OnTouchEventListener mOnVideoTouchListener = new VideoViewTouch.OnTouchEventListener() {

		@Override
		public boolean onClick() {

			if (mVideoView.isPlaying()) {
				mVideoView.pauseClearDelayed();
				mPlayController.setVisibility(View.VISIBLE);
			} else {
				mVideoView.start();
				mPlayController.setVisibility(View.GONE);
				mHandler.sendEmptyMessage(HANDLE_PROGRESS);
			}
			return true;
		}

		@Override
		public void onVideoViewDown() {
		}

		@Override
		public void onVideoViewUp() {

		}
	};

	@Override
	protected void initData() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			onBackPressed();

			break;
		case R.id.right_bt:
			mVideoView.pauseClearDelayed();
			// startaCut();
			if (mIsFitCenter || !isRetangleVideo) {
				// pad+clip
				videoPadAndClipOption();
			} else {
				// crop+clip
				videoCropAndClipOption();
			}

			break;
		case R.id.trim_hengxiang:
			isFirstImport_V = false;

			mTipsMoveHengxiang.setVisibility(View.GONE);
			break;
		case R.id.trim_zhongxiang:
			isFirstImport_H = false;

			mTipsMoveZhongxiang.setVisibility(View.GONE);
			break;
		}

	}

	private void videoPadAndClipOption() {
		if (!SDUtils.isAvailableSpace(this)) {
			return;
		}

		new AsyncTask<Void, Void, Boolean>() {
			private int startTime;
			private int endTime;

			@Override
			protected void onPreExecute() {
				isDecode = true;
				showProgress("裁剪中...");
				mProgressDialog.setOnKeyListener(dialogOnkeyListener);
				mVideoView.pauseClearDelayed();
				int startTimetmp = 0;
				int endTimetmp = 0;
				if (mIsChangeTime) {
					startTimetmp = mPreChangedStartTime;
					endTimetmp = mPreChangedEndTime;
				} else {
					startTimetmp = mVideoSelection.getStartTime();
					endTimetmp = mVideoSelection.getEndTime();
				}
				startTime = startTimetmp;
				endTime = endTimetmp;
				cutVideoDuring = endTime - startTime;
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				String fileName = System.currentTimeMillis() + "";
				mTempVideoName = mTempFilePath.concat(File.separator).concat(fileName).concat(".yuv");
				// if (hasAudioResourse) {
				mTempAudioName = mTempFilePath.concat(File.separator).concat(fileName).concat(".pcm");
				// }
				File tempPath = new File(mTempFilePath);
				if (!tempPath.exists()) {
					tempPath.mkdirs();
				}

				File tempVideo = new File(mTempVideoName);
				if (tempVideo.exists()) {
					tempVideo.delete();
				}
				File tempAudio = new File(mTempAudioName);
				if (tempAudio.exists()) {
					tempAudio.delete();
				}

				int width = 0;
				int height = 0;
				switch (mVideoRotation % 360) {
				case 180:
				case 0:
					width = mVideoView.getVideoWidth();
					height = mVideoView.getVideoHeight();

					break;
				case 90:
				case 270:
					width = mVideoView.getVideoHeight();
					height = mVideoView.getVideoWidth();
				}

				File targPath = new File(mTargetFilePath);
				if (!targPath.exists()) {
					targPath.mkdirs();
				}
				String targetName = System.currentTimeMillis() + "";
				mTargetFileName = mTargetFilePath.concat(File.separator).concat(targetName).concat(".mp4");
				File targetVideo = new File(mTargetFileName);
				if (targetVideo.exists()) {
					targetVideo.delete();
				}
				if (!TextUtils.isEmpty(mFilterPngName)) {
					File file = new File(mFilterPngName);
					if (!file.exists()) {
						CameraManager.copyLogoToSDCard(getApplicationContext());
					}
				} else {
					return false;
				}

				String cmd = null;
				FFmpegNativeHelper mFFmpegNativeHelper = FFmpegNativeHelper.getHelper(); // new
				int count = mFFmpegNativeHelper.GetIKeyInterval(mTempSourcesCopyName);
				if (count < 0) {
					return false;
				}
				isCompose = (count < 101 && count > 0);
				if (isCompose) {
					cmd = FFmpegUtils.getPadAndClipWithWater(mTempSourcesCopyName, mFilterPngName, startTime, endTime,
							width, height, mVideoRotation, Standardbitrate, mTargetFileName);
				} else {
					cmd = FFmpegUtils.getPadAndClipVideoCommond(mTempSourcesCopyName, startTime, endTime, width, height,
							mVideoRotation, hasAudioResourse, mTempVideoName, mTempAudioName);
				}
				LogUtils.e("error", cmd);
				mFFmpegNativeHelper.InitTheVideoMessage(mTempSourcesCopyName);
				mFFmpegNativeHelper.setVideoHandler(mVideoHandlerCallBack);
				return mFFmpegNativeHelper.ffmpegRunCommand(cmd, ACTION_ADD_WATER) == 0;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				hideProgress();
				if (!isFinishing()) {
					if (result) {
						if (isCompose) {
							DataCleanManager.deleteFolderFile(mTempFilePath, true);
							mIntent.setClass(VideoCutOutActivity.this, ActivityVideoPreviewWithLogo.class);
							mIntent.putExtra(ContantsActivity.Video.VIDEO_IS_IMPORT, true);
							mIntent.putExtra(ContantsActivity.Topic.TOPIC_NAME, topic_name);
							mIntent.putExtra(ContantsActivity.Topic.TOPIC_PAGE, topic_page);
							mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID,topic_id);
							mIntent.putExtra(ContantsActivity.Video.VIDEO_PATH, mTargetFileName);
							startActivityForResult(mIntent, CODE_IMPORT_VIDEO);
						} else {
							vidoAddLogo();
						}
					} else {
						hideProgress();
						ToastUtils.showShort(VideoCutOutActivity.this, "视频裁剪失败");
					}
				}
			}
		}.execute();
	}

	private void videoCropAndClipOption() {
		if (!SDUtils.isAvailableSpace(this)) {
			return;
		}
		new AsyncTask<Void, Void, Boolean>() {
			private int startTime;
			private int endTime;

			@Override
			protected void onPreExecute() {
				isDecode = true;
				showProgress("裁剪中...");
				mProgressDialog.setOnKeyListener(dialogOnkeyListener);
				mVideoView.pauseClearDelayed();
				int startTimetmp = 0;
				int endTimetmp = 0;
				if (mIsChangeTime) {
					startTimetmp = mPreChangedStartTime;
					endTimetmp = mPreChangedEndTime;
				} else {
					startTimetmp = mVideoSelection.getStartTime();
					endTimetmp = mVideoSelection.getEndTime();
				}
				startTime = startTimetmp;
				endTime = endTimetmp;
				cutVideoDuring = endTime - startTime;
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				String fileName = System.currentTimeMillis() + "";
				mTempVideoName = mTempFilePath.concat(File.separator).concat(fileName).concat(".yuv");
				// if (hasAudioResourse) {
				mTempAudioName = mTempFilePath.concat(File.separator).concat(fileName).concat(".pcm");
				// }
				File tempPath = new File(mTempFilePath);
				if (!tempPath.exists()) {
					tempPath.mkdirs();
				}

				File tempVideo = new File(mTempVideoName);
				if (tempVideo.exists()) {
					tempVideo.delete();
				}
				File tempAudio = new File(mTempAudioName);
				if (tempAudio.exists()) {
					tempAudio.delete();
				}

				int width = 0;
				int height = 0;
				switch (mVideoRotation % 360) {
				case 180:
				case 0:
					width = mVideoView.getVideoWidth();
					height = mVideoView.getVideoHeight();

					break;
				case 90:
				case 270:
					width = mVideoView.getVideoHeight();
					height = mVideoView.getVideoWidth();
				}

				int cropX = mVideoView.getCropX(); // 420
				int cropY = mVideoView.getCropY();
				final float scale = mVideoView.getScale();
				int cutWidth;
				if (width > height) {
					cutWidth = height;
					if (cropX != 0) {
						cropX = (int) ((float) cropX / scale);
					}
				} else {
					cutWidth = width;
					if (cropY != 0) {
						cropY = (int) ((float) cropY / scale);
					}
				}

				File targPath = new File(mTargetFilePath);
				if (!targPath.exists()) {
					targPath.mkdirs();
				}
				String targetName = System.currentTimeMillis() + "";
				mTargetFileName = mTargetFilePath.concat(File.separator).concat(targetName).concat(".mp4");
				File targetVideo = new File(mTargetFileName);
				if (targetVideo.exists()) {
					targetVideo.delete();
				}
				if (!TextUtils.isEmpty(mFilterPngName)) {
					File file = new File(mFilterPngName);
					if (!file.exists()) {
						CameraManager.copyLogoToSDCard(getApplicationContext());
					}
				} else {
					return false;
				}
				FFmpegNativeHelper mFFmpegNativeHelper = FFmpegNativeHelper.getHelper();// new
				String cmd = null;
				int count = mFFmpegNativeHelper.GetIKeyInterval(mTempSourcesCopyName);
				if (count < 0) {
					return false;
				}
				isCompose = (count < 101 && count > 0);
				if (isCompose) {
					cmd = FFmpegUtils.getCropAndClipWithWater(mTempSourcesCopyName, mFilterPngName, startTime, endTime,
							cutWidth, cutWidth, cropX, cropY, mVideoRotation, Cropbitrate, mTargetFileName);
				} else {
					cmd = FFmpegUtils.getCropAndClipVideo(mTempSourcesCopyName, startTime, endTime, cutWidth, cutWidth,
							cropX, cropY, mVideoRotation, hasAudioResourse, mTempVideoName, mTempAudioName);
				}
				LogUtils.e("error", cmd);
				mFFmpegNativeHelper.setVideoHandler(mVideoHandlerCallBack); // FFmpegNativeHelper();
				mFFmpegNativeHelper.InitTheVideoMessage(mTempSourcesCopyName);
				return mFFmpegNativeHelper.ffmpegRunCommand(cmd, ACTION_ADD_WATER) == 0;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (!isFinishing()) {
					if (result) {
						if (isCompose) {
							DataCleanManager.deleteFolderFile(mTempFilePath, true);
							mIntent.setClass(VideoCutOutActivity.this, ActivityVideoPreviewWithLogo.class);
							mIntent.putExtra(ContantsActivity.Video.VIDEO_IS_IMPORT, true);
							mIntent.putExtra(ContantsActivity.Topic.TOPIC_NAME, topic_name);
							mIntent.putExtra(ContantsActivity.Topic.TOPIC_PAGE, topic_page);
							mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID,topic_id);
							mIntent.putExtra(ContantsActivity.Video.VIDEO_PATH, mTargetFileName);
							startActivityForResult(mIntent, CODE_IMPORT_VIDEO);
						} else {
							vidoAddLogo();
						}
					} else {
						hideProgress();
						ToastUtils.showShort(VideoCutOutActivity.this, "视频裁剪失败");
					}
				}
			}
		}.execute();
	}

	private void vidoAddLogo() {
		if (!SDUtils.isAvailableSpace(this)) {
			return;
		}

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				setProgressMessage("处理中...");
				isDecode = false;
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				File tempPath = new File(mTargetFilePath);
				if (!tempPath.exists()) {
					tempPath.mkdirs();
				}
				String fileName = System.currentTimeMillis() + "";
				mTargetFileName = mTargetFilePath.concat(File.separator).concat(fileName).concat(".mp4");
				File targetVideo = new File(mTargetFileName);
				if (targetVideo.exists()) {
					targetVideo.delete();
				}
				File tempVideo = new File(mTempVideoName);
				if (!tempVideo.exists() || !FileUtils.checkFile(tempVideo)) {
					return false;
				}

				if (hasAudioResourse) {
					File tempAudio = new File(mTempAudioName);
					if (!tempAudio.exists() || !FileUtils.checkFile(tempAudio)) {
						return false;
					}
				}
				if (!TextUtils.isEmpty(mFilterPngName)) {
					File file = new File(mFilterPngName);
					if (!file.exists()) {
						CameraManager.copyLogoToSDCard(getApplicationContext());
					}
				} else {
					return false;
				}
				String cmd = FFmpegUtils.getMuxAndLogoVideo(mTempVideoName, hasAudioResourse, mTempAudioName,
						mFilterPngName, Standardbitrate, mTargetFileName);
				FFmpegNativeHelper mFFmpegNativeHelper = FFmpegNativeHelper.getHelper(); // new
				mFFmpegNativeHelper.setVideoHandler(mVideoHandlerCallBack);
				// FFmpegNativeHelper();
				return mFFmpegNativeHelper.ffmpegRunCommand(cmd, ACTION_ADD_WATER) == 0;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				hideProgress();
				if (!isFinishing()) {
					if (result) {
						DataCleanManager.deleteFolderFile(mTempFilePath, true);
						mIntent.setClass(VideoCutOutActivity.this, ActivityVideoPreviewWithLogo.class);
						mIntent.putExtra(ContantsActivity.Video.VIDEO_IS_IMPORT, true);
						mIntent.putExtra(ContantsActivity.Topic.TOPIC_NAME, topic_name);
						mIntent.putExtra(ContantsActivity.Topic.TOPIC_PAGE, topic_page);
						mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID,topic_id);
						mIntent.putExtra(ContantsActivity.Video.VIDEO_PATH, mTargetFileName);
						startActivityForResult(mIntent, CODE_IMPORT_VIDEO);
					}
				}
			}
		}.execute();
	}

	private VideoHandlerCallBack mVideoHandlerCallBack = new VideoHandlerCallBack() {

		@Override
		public void getCurrentProgress(final int i, double fps, double allFrame) {

			if (isDecode) {

				final double totalFrame = (double) ((cutVideoDuring / 1000) * fps);
				if(mHandler!=null) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							if (mVideoView != null) {
								if (mVideoView.isPlaying()) {
									mVideoView.pauseClearDelayed();
									mPlayController.setVisibility(View.VISIBLE);
								}
							}
							double allCount = 0;
							if (isCompose) {
								allCount = totalFrame * 0.6;
							} else {
								allCount = totalFrame;
							}
							// TODO Auto-generated method stub
							int pre = (int) ((i / allCount) * 100);
							if (pre > 100) {
								pre = 100;
							}
							// if (isCompose) {
							setProgressMessage("裁剪中" + pre + "%");
							// } else {
							// setProgressMessage("当前裁剪进度 " + pre + "%");
							// }

						}
					});
				}
			} else {
				final double totalFrame = allFrame;
				if(mHandler!=null){
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if(mVideoView!=null) {
							if (mVideoView.isPlaying()) {
								mVideoView.pauseClearDelayed();
								mPlayController.setVisibility(View.VISIBLE);
							}
						}
						int pre = (int) ((i / totalFrame) * 100);
						if (pre > 100) {
							pre = 100;
						}
						setProgressMessage("处理中 " + pre + "%");
					}
				});
			}}

		}
	};

	@Override
	protected void onActivityResult(int req, int res, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(req, res, data);
		switch (req) {
		case CODE_IMPORT_VIDEO:
			if (res == Activity.RESULT_OK) {
				setResult(Activity.RESULT_OK, data);
				finish();
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {

		if (FileUtils.checkFile(mTempFilePath) || FileUtils.checkFile(mTargetFileName)) {
			showAlertDialog();
		} else {
			finish();
		}

	}

	private Dialog mAlertDialog;

	private void showAlertDialog() {
		if (mAlertDialog == null) {
			mAlertDialog = DialogUtils.confirmDialog(VideoCutOutActivity.this, "您确定要放弃当前视频", "确定", "取消",
					new DialogUtils.ConfirmDialog() {

						@Override
						public void onOKClick(Dialog dialog) {
							DataCleanManager.deleteFolderFile(mTargetFilePath, true);
							finish();

						}

						@Override
						public void onCancleClick(Dialog dialog) {

						}
					});
		}
		if (!mAlertDialog.isShowing()) {
			mAlertDialog.show();
		} else {
			mAlertDialog.dismiss();
		}
	}

	OnKeyListener dialogOnkeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				return true;
			}
			return false;
		}
	};

	@Override
	public void onPrepared(MediaPlayer mp) {
		// mVideoLoading.setVisibility(View.GONE);
		mDuration = mVideoView.getDuration();

		if (mDuration < 5000) {
			ToastUtils.showShort(this, "导入视频太短");
			finish();
			return;
		}

		setVideoMode(scale);
		mVideoSelection.init(mThumbImagePath, mTempSourcesCopyName, mDuration,
				mDuration <= 18 * 10 * 1000 ? mDuration : 18 * 10 * 1000, 5 * 1000);
		mVideoView.start();
	}

	private void setVideoMode(int scale) {
		if (scale == VideoSelectionView.FIT_XY) {
			isShowTipImage(isFirstImport_H, isFirstImport_V);
			mVideoView.resize();
			if (mVideoView.getCropX() == 0 && mVideoView.getCropY() == 0) {
				mVideoView.centerXY();
			}
			mIsFitCenter = false;
			mPreviewLinearLayout.setGravity(Gravity.NO_GRAVITY);
		} else if (scale == VideoSelectionView.FIT_CENTER) {
			if (mTipsMoveZhongxiang != null && mTipsMoveZhongxiang.getVisibility() == View.VISIBLE) {
				mTipsMoveZhongxiang.setVisibility(View.GONE);
			}
			if (mTipsMoveHengxiang != null && mTipsMoveHengxiang.getVisibility() == View.VISIBLE) {
				mTipsMoveHengxiang.setVisibility(View.GONE);
			}

			mVideoView.resize();
			mVideoView.fitCenter();
			mPreviewLinearLayout.setGravity(Gravity.CENTER);
			mIsFitCenter = true;
		}
	}

	private void isShowTipImage(boolean isFirstH, boolean isFirstV) {
		if (isFirstH) {
			if (mTipsMoveZhongxiang == null) {
				mTipsMoveZhongxiang = (ImageView) findViewById(R.id.trim_zhongxiang);
			}
			mTipsMoveZhongxiang.setOnClickListener(this);
			mTipsMoveZhongxiang.setVisibility(View.VISIBLE);
		}
		if (isFirstV) {
			if (mTipsMoveHengxiang == null) {
				mTipsMoveHengxiang = (ImageView) findViewById(R.id.trim_hengxiang);
			}
			mTipsMoveHengxiang.setOnClickListener(this);
			mTipsMoveHengxiang.setVisibility(View.VISIBLE);

		}

	}

	/** 显示进度 */
	private static final int HANDLE_PROGRESS = 1;
	/** 选区延迟检测 */
	private static final int HANDLE_SEEKTO = 2;

	/**
	 * 重置时间前的开始时间（关键帧的问题，导致可能需要重新设置开始和结束时间）
	 */
	private int mPreChangedStartTime;
	/**
	 * 重置时间前的结束时间
	 */
	private int mPreChangedEndTime;
	/**
	 * 是否重置时间标记
	 */
	private boolean mIsChangeTime;

	private long lastPosition = 0;

	/**
	 * 更新进度线的位置
	 */
	private void setLinePosition() {
		if (mVideoView != null) {

			int startTime = mVideoSelection.getStartTime();
			int endTime = mVideoSelection.getEndTime();

			long position = mVideoView.getCurrentPosition();
			if (lastPosition != 0 && Math.abs(position - lastPosition) > 500) {

				mPreChangedStartTime = startTime;
				mPreChangedEndTime = endTime;
				endTime = (int) position + endTime - startTime;
				startTime = (int) position;
				mVideoSelection.setStartTime(startTime);
				mVideoSelection.setEndTime(endTime);
				mVideoSelection.setStartTime(startTime);
				mVideoSelection.setEndTime(endTime);
				mIsChangeTime = true;
			}
			lastPosition = position;

			if (mVideoSelection != null) {
				if (mVideoSelection.mVideoSelection != null) {
					mVideoSelection.mVideoSelection.setLinePosition(position, startTime, endTime);
				}
			}
		}
	}

	private long setProgress() {
		// if (mVideoView == null)
		return 0;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_PROGRESS:
				if (mVideoView.isPlaying()) {
					// 播放到结束时间的位置 则暂停不要循环
					long position = mVideoView.getCurrentPosition();
					if ((position >= mVideoSelection.getEndTime()
							&& (lastPosition != 0 && Math.abs(position - lastPosition) < 500))
							|| position == mVideoView.getDuration()) {
						LogUtils.e("simon", "step1");
						if (mIsChangeTime) {
							LogUtils.e("simon", "当前重设的历史StartTime>>" + mPreChangedStartTime + ">>>当前记录的历史endTime>>>"
									+ mPreChangedEndTime);
							mVideoSelection.setStartTime(mPreChangedStartTime);
							mVideoSelection.setEndTime(mPreChangedEndTime);
							mIsChangeTime = false;
						}
						LogUtils.e("simon", "暂停了?position ::" + position + "endTime::" + mVideoSelection.getEndTime()
								+ "view.getDuration::" + mVideoView.getDuration());
						final int startTime = mVideoSelection.getStartTime();
						mVideoView.pauseClearDelayed();
						mVideoView.seekTo(startTime);
					} else {
						LogUtils.e("simon", "step2");
						setLinePosition();
						sendEmptyMessageDelayed(HANDLE_PROGRESS, 20);
					}
					setProgress();
				} else if (mVideoView.isPaused()) {
					LogUtils.e("simon", "step3");
					if (mIsChangeTime) {
						LogUtils.e("", "当前重设的历史StartTime>>" + mPreChangedStartTime + ">>>当前记录的历史endTime>>>"
								+ mPreChangedEndTime);
						mVideoSelection.setStartTime(mPreChangedStartTime);
						mVideoSelection.setEndTime(mPreChangedEndTime);
						mIsChangeTime = false;
					}
					final int startTime = mVideoSelection.getStartTime();
					mVideoView.seekTo(startTime);
					setProgress();
				}
				break;
			case HANDLE_SEEKTO:
				if (!isFinishing()) {
					final int startTime = mVideoSelection.getStartTime();
					// Log.e("simon","HANDLE_SEEKTO::StartTime>>"+startTime+">>>endTime>>>"+mVideoSelection.getEndTime());
					if (mVideoView.isPlaying())
						mVideoView.loopDelayed(startTime, mVideoSelection.getEndTime());
					else
						mVideoView.seekTo(startTime);
					setProgress();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onStateChanged(boolean isPlaying) {
		if (isPlaying) {
			mHandler.removeMessages(HANDLE_PROGRESS);
			mHandler.sendEmptyMessage(HANDLE_PROGRESS);
			mPlayController.setVisibility(View.GONE);
		} else {
			clearLine();
			mHandler.removeMessages(HANDLE_PROGRESS);
			mPlayController.setVisibility(View.VISIBLE);
		}
	}

	/** 视频暂停时 把进度线也隐藏 */
	private void clearLine() {
		if (mVideoSelection != null) {
			if (mVideoSelection.mVideoSelection != null) {
				mVideoSelection.mVideoSelection.clearLine();
			}
		}
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub
		LogUtils.e("simon", "[ImportVideoActivity]onSeekComplete...");
		// mVideoView.start();
		lastPosition = 0;
		mPreChangedStartTime = 0;
		mPreChangedEndTime = 0;
		mIsChangeTime = false;
	}

	@Override
	public void onProgressChanged() {
		// 拖动手柄或者滚动缩略图片的时候把视频停止 并把指针移到起始位置
		if (mVideoView != null) {
			// hideFirstTips();
			if (mVideoView.isPlaying()) {
				mVideoView.pauseClearDelayed();
			}
			int startTime = mVideoSelection.getStartTime();

			// Log.e("simon","onProgressChanged::StartTime>>"+startTime+">>>endTime>>>"+mVideoSelection.getEndTime());

			mVideoView.seekTo(startTime);
			setProgress();
			// if (mVideoTime != null && mVideoSelection != null) {
			// mVideoTime.setText(getString(R.string.left_second_tips,
			// mVideoSelection.getVideoCutTime()));
			// }
		}
	}

	@Override
	public void onProgressEnd() {
		if (mHandler.hasMessages(HANDLE_SEEKTO))
			mHandler.removeMessages(HANDLE_SEEKTO);
		mHandler.sendEmptyMessageDelayed(HANDLE_SEEKTO, 20);

	}

	@Override
	public void onChanged(int scale) {
		this.scale = scale;
		setVideoMode(scale);

	}

	@Override
	public void onChanged() {
		if (mVideoView != null) {
			mVideoView.pauseClearDelayed();
			mVideoView.seekTo(0);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mHandler != null) {
			mHandler.removeMessages(HANDLE_PROGRESS);
			mHandler.removeMessages(HANDLE_SEEKTO);
		}
		if (mThumbImagePath != null) {
			mThumbImagePath = null;
		}
		if (mVideoSelection != null) {
			mVideoSelection.release();
			mVideoSelection = null;
		}
		if (mVideoView != null) {
			mVideoView.pauseClearDelayed();
			mVideoView = null;
		}
		System.gc();

	}

}
