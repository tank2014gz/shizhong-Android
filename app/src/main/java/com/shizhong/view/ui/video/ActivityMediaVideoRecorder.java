package com.shizhong.view.ui.video;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.utils.CameraManager;
import com.shizhong.view.ui.base.utils.DataCleanManager;
import com.shizhong.view.ui.base.utils.DeviceUtils;
import com.shizhong.view.ui.base.utils.DrawableUitls;
import com.shizhong.view.ui.base.utils.FileUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.SDUtils;
import com.shizhong.view.ui.base.utils.StringUtils;
//import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.utils.VideoUtils;
import com.shizhong.view.ui.base.view.ChoiseVideoWindow;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.ProgressView;
import com.shizhong.view.ui.bean.RcordVideoPart;
import com.wind.ffmpeghelper.FFmpegNativeHelper;
import com.wind.ffmpeghelper.FFmpegUtils;
import com.wind.ffmpeghelper.VideoHandlerCallBack;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings({ "deprecation", "unused" })
public class ActivityMediaVideoRecorder extends BaseMediaActivity implements OnClickListener {

	private static final String TAG = ActivityMediaVideoRecorder.class.getSimpleName();

	/** 录制最长时间 */
	public final static int RECORD_TIME_MAX = 60 * 1000;
	/** 录制最小时间 */
	public final static int RECORD_TIME_MIN = 5 * 1000;

	private ImageView mBackImage;
	private ImageView mChangeFlighting;
	private ImageView mChangeCamera;

	private ImageView mFouctionImage;
	private SurfaceView mCameraSurfaceView;
	private ProgressView mRecoderProgressView;

	private ImageView mRecoderButton;
	private TextView mDelectOrImportBtn;// 导入或删除视频按钮

	private int mWindowWidth;
	private int mFouctionWidth;
	private String topic_name;
	private int topic_page;
	private String topic_id;
	private Animation mFocusAnimation;

	/** 录制视频集合 */
	private ArrayList<RcordVideoPart> list;

	/** 录制视频的类 */
	private MediaRecorder mMediaRecorder;
	/** 摄像头对象 */
	private Camera mCamera;
	/** 摄像头参数 */
	private Parameters mParameters;
	// /** 视频输出质量 */
	private CamcorderProfile mProfile;
	/** 文本属性获取器 */
	private SharedPreferences mPreferences;
	/** 刷新界面的回调 */
	private SurfaceHolder mCameraHolder;
	/** 1表示后置，0表示前置 */
	private int cameraPosition = 1;
	private String mTargetPath;
	private String mTargetFile;
	private String mThumbPath;
	private final int HANDLE_HIDE_RECORD_FOCUS = 0x4;
	private final int RECODER_CAN_ENABLE = 0x5;// 录制按钮可点击进行下一步操作（可进行视频拼接或crop）
	private final int RECODER_FINISH = 0x6;
	private boolean isRcording;// 是否正在录制
	private int mCurrentProgress;
	private int mLastRecorderProgress;
	private String mFilterPngName;
	int Standardbitrate = 1000 * 5000;// 设置视频的码率
	private static final int ACTION_CROP_AND_ADD_WATER = 2;
	private double mCutDuring = 0;
	private final int ACTION_GOTO_PUSH = 0x21;
	private TextView mTimeText;
	private ChoiseVideoWindow mChoiseVideoWindow;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLE_HIDE_RECORD_FOCUS:
				if (mFouctionImage.getVisibility() == View.VISIBLE) {
					mFouctionImage.setVisibility(View.GONE);
				}
				break;
			case RECODER_CAN_ENABLE:
				mRecoderButton.setEnabled(true);
				mRecoderButton.setSelected(true);
				break;
			case RECODER_FINISH:
				stopRecord();
				// ToastUtils.showLong(ActivityMediaVideoRecorder.this, "录制完成");
			default:
				break;
			}

		};
	};

	@Override
	protected void initBundle() {
		list = new ArrayList<RcordVideoPart>();
		mFouctionWidth = UIUtils.dipToPx(ActivityMediaVideoRecorder.this, 60);
		mWindowWidth = UIUtils.getScreenWidthPixels(ActivityMediaVideoRecorder.this);
		topic_name = getIntent().getStringExtra(ContantsActivity.Topic.TOPIC_NAME);
		topic_id=getIntent().getStringExtra(ContantsActivity.Topic.TOPIC_ID);
		topic_page = getIntent().getIntExtra(ContantsActivity.Topic.TOPIC_PAGE, 1);

	}

	@Override
	protected void initView() {
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.recode_common_layout_activity);
		// 标题栏按钮
		mBackImage = (ImageView) findViewById(R.id.btn_camera_close);
		mChangeCamera = (ImageView) findViewById(R.id.btn_swap);
		mChangeFlighting = (ImageView) findViewById(R.id.btn_flight);

		mBackImage.setOnClickListener(this);
		mChangeCamera.setOnClickListener(this);
		mChangeFlighting.setOnClickListener(this);

		// 相机显示View
		mFouctionImage = (ImageView) findViewById(R.id.record_focusing);
		mCameraSurfaceView = (SurfaceView) findViewById(R.id.camera_surfaceview);
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mCameraSurfaceView.getLayoutParams();
		lp.width = mWindowWidth;
		lp.height = mWindowWidth * 4 / 3;
		mCameraSurfaceView.setLayoutParams(lp);
		mRecoderProgressView = (ProgressView) findViewById(R.id.recorder_progress);

		if (DeviceUtils.hasICS()) {
			mCameraSurfaceView.setOnTouchListener(mOnSurfaveViewTouchListener);
		}
		if (DeviceUtils.isSupportCameraLedFlash(getPackageManager())) {
			mChangeFlighting.setOnClickListener(this);
		} else {
			mChangeFlighting.setVisibility(View.INVISIBLE);
		}
		if (DeviceUtils.isSupportFrontCamera()) {
			mChangeCamera.setOnClickListener(this);
		} else {
			mChangeCamera.setVisibility(View.INVISIBLE);
		}

		try {
			mFouctionImage.setImageResource(R.drawable.video_focus);
			// mFocusImage.setVisibility(View.VISIBLE);
		} catch (OutOfMemoryError e) {
			LogUtils.e(TAG, e.toString());
		}
		mRecoderProgressView.setMaxProgress(RECORD_TIME_MAX);
		mTimeText = (TextView) findViewById(R.id.time_text);
		mRecoderProgressView.setmTimeTextView(mTimeText);
		mRecoderButton = (ImageView) findViewById(R.id.video_option_btn);
		mRecoderButton.setOnClickListener(this);
		mDelectOrImportBtn = (TextView) findViewById(R.id.video_input_btn);
		mDelectOrImportBtn.setOnClickListener(this);

		mCameraHolder = mCameraSurfaceView.getHolder();
		mCameraHolder.addCallback(mCarmeraSurfaceCallBack);
		mCameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		readVideoPreferences();
		setCancleRecodeVideo(false);
		mChoiseVideoWindow = new ChoiseVideoWindow(ActivityMediaVideoRecorder.this);
		mChoiseVideoWindow.setTopic(topic_name,topic_id, topic_page);
	}

	@Override
	protected void initData() {
		mFilterPngName = CameraManager.getFilterPngName();
		String fileName = System.currentTimeMillis() + "";
		mTargetPath = CameraManager.getVideoCachePath().concat(fileName);
		mThumbPath = mTargetPath.concat(File.separator).concat("thumbs").concat(File.separator);

	}

	public void setCancleRecodeVideo(boolean isCancel) {
		if (isCancel) {
			mDelectOrImportBtn.setText("取消录制");
			DrawableUitls.setCompoundDrawablesWithIntrinsicBounds(mDelectOrImportBtn, 0, R.drawable.video_delect_icon,
					0, 0);
		} else {
			mDelectOrImportBtn.setText("导入视频");
			DrawableUitls.setCompoundDrawablesWithIntrinsicBounds(mDelectOrImportBtn, 0, R.drawable.video_up_icon, 0,
					0);
		}
	}

	/**
	 * 设置摄像头参数
	 *
	 * @version 1.0
	 * @createTime 2015年6月15日,下午5:12:31
	 * @updateTime 2015年6月15日,下午5:12:31
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void readVideoPreferences() {
		mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
		mProfile.videoBitRate = Standardbitrate;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_swap:
			switchCamera();
			break;
		case R.id.btn_flight:
			if (cameraPosition != 0) {// 前置摄像头的时候不能切换闪光灯
				if (mParameters != null) {
					if (mParameters.getFlashMode() != null
							&& mParameters.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
						mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
						mChangeFlighting.setSelected(false);
					} else if (mParameters.getFlashMode() != null
							&& mParameters.getFlashMode().equals(Parameters.FLASH_MODE_OFF)) {
						mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
						mChangeFlighting.setSelected(true);
					}
					if (mCamera != null) {
						mCamera.setParameters(mParameters);
					}
				}
			}

			break;
		case R.id.btn_camera_close:
			onBackPressed();
			break;
		case R.id.video_option_btn:
			if (isRcording) {
				stopRecord();
				// ToastUtils.showShort(ActivityMediaVideoRecorder.this,
				// "开始转码");
			} else {
				File file = new File(mTargetPath);
				if (!file.exists()) {
					file.mkdirs();
				}
				File file1 = new File(mThumbPath);
				if (!file1.exists()) {
					file1.mkdirs();
				}
				startRecord();

			}

			break;
		case R.id.video_input_btn:
			if (isRcording) {
				isRcording = false;
				mRecoderButton.setEnabled(true);
				mRecoderProgressView.stopRecode();
				mHandler.removeMessages(RECODER_CAN_ENABLE);
				mHandler.removeMessages(RECODER_FINISH);
				mChangeCamera.setVisibility(View.VISIBLE);
				mChangeFlighting.setVisibility(View.VISIBLE);
				setCancleRecodeVideo(false);
				DataCleanManager.deleteFolderFile(mThumbPath, false);
				releastRecoder();

			} else {
				if (mChoiseVideoWindow != null && !mChoiseVideoWindow.isShowing()) {
					mChoiseVideoWindow.show(mChangeCamera);
				} else {

				}
			}
			break;
		}

	}

	@Override
	public void onBackPressed() {
		if (isRcording) {
			showAlertDialog();
		} else {
			finish();
			overridePendingTransition(0, R.anim.push_bottom_out);
		}

	}

	private Dialog mAlertDialog;

	private void showAlertDialog() {
		if (mAlertDialog == null) {
			mAlertDialog = DialogUtils.confirmDialog(ActivityMediaVideoRecorder.this, "视频正在录制中，您确定要退出", "确定退出", "继续录制",
					new DialogUtils.ConfirmDialog() {

						@Override
						public void onOKClick(Dialog dialog) {
							releastRecoder();
							releaseCamera();
							DataCleanManager.deleteFolderFile(mTargetPath, true);
							finish();
							overridePendingTransition(0, R.anim.push_bottom_out);
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

	// private RcordVideoPart bean;
	private String mThumbVideoName;

	@SuppressLint("NewApi")
	private void startRecord() {
		try {
			// bean = new RcordVideoPart();
			mThumbVideoName = mThumbPath + System.currentTimeMillis() + ".mp4";
			// bean.path = mThumbVideoName;
			setCancleRecodeVideo(true);

			mMediaRecorder = new MediaRecorder();// 创建mediaRecorder对象
													// BEGIN_INCLUDE
													// (configure_media_recorder)
			mCurrentProgress = mRecoderProgressView.getCurrentProgress();
			mCamera.unlock();

			mMediaRecorder.setCamera(mCamera);
			// 设置录制视频源为Camera(相机)
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setProfile(mProfile);

			// 设置视频一次写多少字节(可调节视频空间大小)

			// 最大期限
			mMediaRecorder.setMaxDuration(RECORD_TIME_MAX - mCurrentProgress);

			// 第4步:指定输出文件 ， 设置视频文件输出的路径

			mMediaRecorder.setOutputFile(mThumbVideoName);

			mMediaRecorder.setPreviewDisplay(mCameraHolder.getSurface());

			// // 设置保存录像方向
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				if (cameraPosition == 1) {
					// 由于不支持竖屏录制，后置摄像头需要把视频顺时针旋转90度、、但是视频本身在电脑上看还是逆时针旋转了90度
					mMediaRecorder.setOrientationHint(90);
				} else if (cameraPosition == 0) {
					// 由于不支持竖屏录制，前置摄像头需要把视频顺时针旋转270度、、而前置摄像头在电脑上则是顺时针旋转了90度
					mMediaRecorder.setOrientationHint(180);
				}
			}

			mMediaRecorder.setOnInfoListener(new OnInfoListener() {

				@Override
				public void onInfo(MediaRecorder mr, int what, int extra) {

				}
			});

			mMediaRecorder.setOnErrorListener(new OnErrorListener() {

				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
					recodError();
				}
			});

			// 第6步:根据以上配置准备MediaRecorder

			mMediaRecorder.prepare();
			mMediaRecorder.start();
			mRecoderProgressView.startRecode();
			mChangeCamera.setVisibility(View.INVISIBLE);
			mChangeFlighting.setVisibility(View.INVISIBLE);
			isRcording = true;
			mHandler.sendEmptyMessageDelayed(RECODER_FINISH, RECORD_TIME_MAX - mCurrentProgress);
			if (mCurrentProgress < RECORD_TIME_MIN) {
				mRecoderButton.setEnabled(false);
				mHandler.sendEmptyMessageDelayed(RECODER_CAN_ENABLE, RECORD_TIME_MIN - mCurrentProgress);
			} else {
				mRecoderButton.setSelected(true);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
			recodError();
		} catch (IOException e) {
			e.printStackTrace();
			recodError();
		} catch (RuntimeException e) {
			e.printStackTrace();
			recodError();
		}

	}

	/**
	 * 结束录制
	 *
	 * @version 1.0
	 * @createTime 2015年6月15日,下午4:48:53
	 * @updateTime 2015年6月15日,下午4:48:53
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void stopRecord() {
		mRecoderProgressView.pauseRecode();
		mLastRecorderProgress = mRecoderProgressView.getCurrentProgress();
		mChangeCamera.setVisibility(View.VISIBLE);
		mChangeFlighting.setVisibility(View.VISIBLE);
		mRecoderButton.setSelected(false);
		mHandler.removeMessages(RECODER_FINISH);
		setCancleRecodeVideo(false);
		isRcording = false;
		releastRecoder();
		startPadEncoding();
	}

	private void releastRecoder() {
		if (mMediaRecorder != null) {
			try {
				// 停止录像，释放camera
				mMediaRecorder.setOnErrorListener(null);
				mMediaRecorder.setOnInfoListener(null);
				mMediaRecorder.stop();
				// 清除recorder配置
				mMediaRecorder.reset();
				// 释放recorder对象
				mMediaRecorder.release();
				mMediaRecorder = null;
			} catch (Exception e) {
				// clearList();
			}
		}
	}

	/**
	 * 异常处理
	 *
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:49:18
	 * @updateTime 2015年6月16日,上午10:49:18
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void recodError() {
		Builder builder = new Builder(ActivityMediaVideoRecorder.this);
		builder.setMessage("该设备暂不支持视频录制");
		builder.setTitle("出错啦");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}

		});
		builder.create().show();

	}

	@SuppressLint("NewApi")
	private void switchCamera() {
		// 切换前后摄像头
		int cameraCount = 0;
		CameraInfo cameraInfo = new CameraInfo();
		cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数

		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
			if (cameraPosition == 1) {
				// 现在是后置，变更为前置
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
					// CAMERA_FACING_BACK后置
					// 前置摄像头时必须关闭闪光灯，不然会报错
					if (mParameters != null) {
						if (mParameters.getFlashMode() != null
								&& mParameters.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
							mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
							mChangeFlighting.setVisibility(View.GONE);
							// img_flashlight.setImageResource(R.drawable.img_video_new_flashlight_close);
						}
						if (mParameters.isAutoExposureLockSupported()) {
							mParameters.setAutoExposureLock(true);
						} else {
							mParameters.setAutoExposureLock(false);
						}
						if (mParameters.isAutoWhiteBalanceLockSupported()) {
							mParameters.setAutoWhiteBalanceLock(true);
						} else {
							mParameters.setAutoWhiteBalanceLock(false);
						}
						if (mCamera != null) {
							mCamera.setParameters(mParameters);
						}

					}

					// 释放Camera
					releaseCamera();

					// 打开当前选中的摄像头
					mCamera = Camera.open(i);
					mCamera.setDisplayOrientation(90);
					// mCamera.lock();
					// 通过surfaceview显示取景画面
					setStartPreview(mCameraHolder);

					cameraPosition = 0;

					break;
				}
			} else {
				// 现在是前置， 变更为后置
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
					// CAMERA_FACING_BACK后置
					// 释放Camera
					releaseCamera();
					// 打开当前选中的摄像头
					mCamera = Camera.open(i);
					mCamera.setDisplayOrientation(90);
					mCamera.lock();
					mChangeFlighting.setVisibility(View.VISIBLE);
					mChangeFlighting.setSelected(false);
					// 通过surfaceview显示取景画面
					setStartPreview(mCameraHolder);
					cameraPosition = 1;

					break;
				}
			}

		}
	}

	/**
	 * 释放Camera
	 *
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:38:08
	 * @updateTime 2015年6月16日,上午10:38:08
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();// 停掉原来摄像头的预览
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mCamera = getCamera();
		if (mCamera != null) {
			// 因为android不支持竖屏录制，所以需要顺时针转90度，让其游览器显示正常
			mCamera.setDisplayOrientation(90);
			mCamera.lock();
			mCamera.startPreview();
			initCameraParameters();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	/**
	 * 初始化摄像头参数
	 *
	 * @version 1.0
	 * @createTime 2015年6月15日,下午4:53:41
	 * @updateTime 2015年6月15日,下午4:53:41
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void initCameraParameters() {
		// 初始化摄像头参数

		mParameters = mCamera.getParameters();
		mParameters.setPreviewSize(mProfile.videoFrameWidth, mProfile.videoFrameHeight);
		mParameters.setPreviewFrameRate(mProfile.videoFrameRate);
		mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
		mParameters.setJpegQuality(90);
		mParameters.setSceneMode(Parameters.SCENE_MODE_ACTION);

		// 设置白平衡参数。
		List<String> whiteBalance = mParameters.getSupportedWhiteBalance();
		// [auto, incandescent, fluorescent, warm-fluorescent, daylight,
		// cloudy-daylight, twilight, shade, manual]
		System.out.println(whiteBalance.toString());
		if (whiteBalance.size() > 0) {
			mParameters.setWhiteBalance(whiteBalance.get(0));
		}
		// 参数设置颜色效果。
		List<String> colorEffect = mParameters.getSupportedColorEffects();
		// [none, mono, negative, solarize, sepia, posterize, whiteboard,
		// blackboard, aqua, emboss, sketch, neon]
		if (colorEffect.size() > 1) {
			mParameters.setColorEffect(colorEffect.get(0));
		}
		List<String> Antibanding = mParameters.getSupportedAntibanding();
		// [off, 60hz, 50hz, auto]
		if (Antibanding.size() > 0) {
			mParameters.setAntibanding("auto");
		}

		try {
			mCamera.setParameters(mParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isSupported(String value, List<String> supported) {
		return supported == null ? false : supported.indexOf(value) >= 0;
	}

	/**
	 * 点击屏幕录制
	 */
	private View.OnTouchListener mOnSurfaveViewTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// 检测是否手动对焦
				if (checkCameraFocus(event))
					return true;
				break;
			}
			return true;
		}

	};

	private boolean checkCameraFocus(MotionEvent event) {
		mFouctionImage.setVisibility(View.GONE);
		float x = event.getX();
		float y = event.getY();
		float touchMajor = event.getTouchMajor();
		float touchMinor = event.getTouchMinor();

		Rect touchRect = new Rect((int) (x - touchMajor / 2), (int) (y - touchMinor / 2), (int) (x + touchMajor / 2),
				(int) (y + touchMinor / 2));
		// The direction is relative to the sensor orientation, that is, what
		// the sensor sees. The direction is not affected by the rotation or
		// mirroring of setDisplayOrientation(int). Coordinates of the rectangle
		// range from -1000 to 1000. (-1000, -1000) is the upper left point.
		// (1000, 1000) is the lower right point. The width and height of focus
		// areas cannot be 0 or negative.
		// No matter what the zoom level is, (-1000,-1000) represents the top of
		// the currently visible camera frame
		if (touchRect.right > 1000)
			touchRect.right = 1000;
		if (touchRect.bottom > 1000)
			touchRect.bottom = 1000;
		if (touchRect.left < 0)
			touchRect.left = 0;
		if (touchRect.right < 0)
			touchRect.right = 0;

		if (touchRect.left >= touchRect.right || touchRect.top >= touchRect.bottom)
			return false;

		ArrayList<Area> focusAreas = new ArrayList<Area>();
		focusAreas.add(new Area(touchRect, 1000));
		if (!manualFocus(new AutoFocusCallback() {

			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				// if (success) {
				mFouctionImage.setVisibility(View.GONE);
				// }
			}
		}, focusAreas)) {
			mFouctionImage.setVisibility(View.GONE);
		}

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFouctionImage.getLayoutParams();
		int left = touchRect.left - (mFouctionWidth / 2);// (int) x -
		// (focusingImage.getWidth()
		// / 2);
		int top = touchRect.top - (mFouctionWidth / 2);// (int) y -
		// (focusingImage.getHeight()
		// / 2);
		if (left < 0)
			left = 0;
		else if (left + mFouctionWidth > mWindowWidth)
			left = mWindowWidth - mFouctionWidth;
		if (top + mFouctionWidth > mWindowWidth)
			top = mWindowWidth - mFouctionWidth;

		lp.leftMargin = left;
		lp.topMargin = top;
		mFouctionImage.setLayoutParams(lp);
		mFouctionImage.setVisibility(View.VISIBLE);

		if (mFocusAnimation == null)
			mFocusAnimation = AnimationUtils.loadAnimation(this, R.anim.record_focus);

		mFouctionImage.startAnimation(mFocusAnimation);

		return true;
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public boolean manualFocus(AutoFocusCallback cb, List<Area> focusAreas) {
		if (mCamera != null && focusAreas != null && mParameters != null && DeviceUtils.hasICS()) {
			try {
				mCamera.cancelAutoFocus();
				// getMaxNumFocusAreas检测设备是否支持
				if (mParameters.getMaxNumFocusAreas() > 0) {
					// mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);//
					// Macro(close-up) focus mode
					mParameters.setFocusAreas(focusAreas);
				}

				if (mParameters.getMaxNumMeteringAreas() > 0)
					mParameters.setMeteringAreas(focusAreas);

				mParameters.setFocusMode(Parameters.FOCUS_MODE_MACRO);
				mCamera.setParameters(mParameters);
				mCamera.autoFocus(cb);
				return true;
			} catch (Exception e) {

				if (e != null)
					Log.e("Yixia", "autoFocus", e);
			}
		}
		return false;
	}

	/**
	 * 获取摄像头实例
	 *
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:44:11
	 * @updateTime 2015年6月16日,上午10:44:11
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 * @return
	 */
	private Camera getCamera() {
		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception e) {
			camera = null;
		}
		return camera;
	}

	private Callback mCarmeraSurfaceCallBack = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			setStartPreview(holder);

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 设置camera显示取景画面,并预览
	 *
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:48:15
	 * @updateTime 2015年6月16日,上午10:48:15
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 * @param holder
	 */
	private void setStartPreview(SurfaceHolder holder) {
		try {
			if (mParameters != null) {
				List<String> focusModes = mParameters.getSupportedFocusModes();

				if (focusModes != null && focusModes.contains(Parameters.FOCUS_MODE_AUTO)) {
					if (mCamera != null) {
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								mCamera.autoFocus(new AutoFocusCallback() {
									@Override
									public void onAutoFocus(boolean success, Camera camera) {
										camera.lock();
									}
								});
							}
						}, 200);

					} else {
						mCamera.lock();
					}
					mCamera.setPreviewDisplay(holder);
					mCamera.startPreview();
				}
			}
		} catch (IOException e) {

		}
	}

	private void startPadEncoding() {
		// 检测磁盘空间
		if (!SDUtils.isAvailableSpace(this)) {
			return;
		}

		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected void onPreExecute() {
				showProgress("处理中...");
				mProgressDialog.setOnKeyListener(dialogOnkeyListener);
			}

			@Override
			protected Boolean doInBackground(Void... params) {

				if (!TextUtils.isEmpty(mThumbVideoName)) {
					File file = new File(mThumbVideoName);
					if (!FileUtils.checkFile(file)) {
						return false;
					}
				} else {
					return false;
				}
				int rotation = VideoUtils.getVideoRotate(mThumbVideoName);

				if (!TextUtils.isEmpty(mTargetPath)) {
					File parentPath = new File(mTargetPath);
					if (!parentPath.exists()) {
						parentPath.mkdirs();
					}
					String cutName = System.currentTimeMillis() + "";
					mTargetFile = mTargetPath.concat(File.separator).concat(cutName).concat(".mp4");

					File file = new File(mTargetFile);
					if (file.exists()) {
						file.delete();
					}
				} else {
					return false;
				}

				if (!TextUtils.isEmpty(mFilterPngName)) {
					File file = new File(mFilterPngName);
					if (!file.exists()) {
						CameraManager.copyLogoToSDCard(getApplicationContext());
					}
				} else {
					return false;
				}

				FFmpegNativeHelper mFfmpegNativeHelper = FFmpegNativeHelper.getHelper();// new
				mFfmpegNativeHelper.InitTheVideoMessage(mThumbVideoName);
				mFfmpegNativeHelper.setVideoHandler(mVideoHandlerCallBack); // FFmpegNativeHelper();
				mCutDuring = FFmpegNativeHelper.getDuration();
				int cutWidth = 0;
				int videoWidth = VideoUtils.getVideoWidth(mThumbVideoName);
				int videoHeight = VideoUtils.getVideoHeight(mThumbVideoName);
				cutWidth = Math.min(videoWidth, videoHeight);
				String command = FFmpegUtils.getCropWithWater(mThumbVideoName, mFilterPngName, Standardbitrate,
						rotation, 0, 0, cutWidth, cutWidth, mTargetFile);
				boolean result = mFfmpegNativeHelper.ffmpegRunCommand(command, ACTION_CROP_AND_ADD_WATER) == 0;
				return result;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (!isFinishing()) {
					hideProgress();
					if (result) {
						DataCleanManager.deleteFolderFile(mThumbPath, true);
						Intent mIntent = new Intent(ActivityMediaVideoRecorder.this,
								ActivityVideoPreviewWithLogo.class);
						mIntent.putExtra(ContantsActivity.Topic.TOPIC_NAME, topic_name);
						mIntent.putExtra(ContantsActivity.Topic.TOPIC_PAGE, topic_page);
						mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID,topic_id);
						mIntent.putExtra(ContantsActivity.Video.VIDEO_PATH, mTargetFile);
						startActivityForResult(mIntent, ACTION_GOTO_PUSH);
					}
				}
			}
		}.execute();

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (mChoiseVideoWindow != null) {
			mChoiseVideoWindow.onActivityResult(requestCode, resultCode, data);
		}
		if (requestCode == ACTION_GOTO_PUSH) {
			if (resultCode == Activity.RESULT_OK) {
				setResult(Activity.RESULT_OK, data);
				finish();
			} else {
				releastRecoder();
				mRecoderProgressView.stopRecode();
			}
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

	VideoHandlerCallBack mVideoHandlerCallBack = new VideoHandlerCallBack() {

		@Override
		public void getCurrentProgress(final int i, final double fps, double allFram) {

			final double totalFrame = mCutDuring * fps;
			mHandler.post(new Runnable() {
				public void run() {
					if (!isFinishing()) {
						if (mProgressDialog.isShowing()) {
							int pre = (int) ((i / totalFrame) * 100);
							if (pre >= 100) {
								pre = 100;
							}
							setProgressMessage(pre + "%");
						}
					}
				}
			});

		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releastRecoder();
		if (mCameraSurfaceView != null) {
			mCameraSurfaceView = null;
		}
		if (mProfile != null) {
			mProfile = null;
		}
		if (mParameters != null) {
			mParameters = null;
		}
		if (mCamera != null) {
			mCamera = null;
		}
		if (mRecoderProgressView != null) {
			mRecoderProgressView.release();
			mRecoderProgressView = null;
		}
		if (mHandler != null) {
			mHandler.removeMessages(HANDLE_HIDE_RECORD_FOCUS);
			mHandler.removeMessages(RECODER_CAN_ENABLE);
			mHandler.removeMessages(RECODER_FINISH);
			mHandler = null;
		}
		if (mChoiseVideoWindow != null) {
			mChoiseVideoWindow.release();
			mChoiseVideoWindow = null;
		}

		if (mAlertDialog != null) {
			mAlertDialog = null;
		}

		System.gc();
	};

}
