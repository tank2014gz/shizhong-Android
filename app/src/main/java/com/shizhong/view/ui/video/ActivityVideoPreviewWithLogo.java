package com.shizhong.view.ui.video;

import java.io.File;
import java.io.FileOutputStream;

import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.utils.DataCleanManager;
import com.shizhong.view.ui.base.utils.FileUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.utils.VideoUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.SurfaceVideoView;
import com.shizhong.view.ui.base.view.SurfaceVideoView.OnPlayStateListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ActivityVideoPreviewWithLogo extends BaseMediaActivity implements OnClickListener {

	/**
	 * 播放按钮、主题音量按钮
	 */
	private ImageView mPlayStatus;
	/**
	 * 上一步、下一步
	 */
	private ImageView mTitleLeft;
	private TextView mTitleNext, mTitleText;
	private View mPreviewView;
	/**
	 * 导出视频，导出封面
	 */
	private String mVideoPath, mCoverPath;

	private final int ACTION_GOTO_PUSH = 0x21;
	private String topic_name;
	private int topic_page;
	private String  topic_id;
	private int mVideoDuring;
	private LayoutParams mPreviewLayoutParams;
	private SurfaceVideoView mVideoPlayer;
	private View mLoadingView;

	@Override
	protected void initBundle() {
		topic_name = getIntent().getStringExtra(ContantsActivity.Topic.TOPIC_NAME);
		topic_page = getIntent().getIntExtra(ContantsActivity.Topic.TOPIC_PAGE, 1);
		topic_id=getIntent().getStringExtra(ContantsActivity.Topic.TOPIC_ID);
		mVideoPath = getIntent().getStringExtra(ContantsActivity.Video.VIDEO_PATH);
		mVideoDuring = VideoUtils.getVideoDuring(mVideoPath);
		if (!TextUtils.isEmpty(mVideoPath)) {
			mCoverPath = mVideoPath.replace("mp4", "jpg");
		}
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_video_preview_layout);
		mTitleLeft = (ImageView) findViewById(R.id.left_bt);
		mTitleText = (TextView) findViewById(R.id.title_tv);
		mTitleText.setText("预览");
		mTitleNext = (TextView) findViewById(R.id.titleRight);
		mTitleNext.setOnClickListener(this);
		mTitleLeft.setOnClickListener(this);
		mPreviewView = findViewById(R.id.preview_layout);
		mPreviewLayoutParams = (LayoutParams) mPreviewView.getLayoutParams();
		mPreviewLayoutParams.width = UIUtils.getScreenWidthPixels(ActivityVideoPreviewWithLogo.this);
		mPreviewLayoutParams.height = UIUtils.getScreenWidthPixels(ActivityVideoPreviewWithLogo.this);
		// mPreviewView.setLayoutParams(mPreviewLayoutParams);
		mVideoPlayer = (SurfaceVideoView) findViewById(R.id.preview_theme);
		mPlayStatus = (ImageView) findViewById(R.id.play_status);
		// mPlayStatus.setOnClickListener(this);
		mLoadingView = findViewById(R.id.loading);
		mLoadingView.setVisibility(View.GONE);
		mVideoPlayer.openVideo(Uri.parse(mVideoPath));
		mVideoPlayer.setOnInfoListener(mVideoOnInfoListener);
		mVideoPlayer.setOnPreparedListener(mVideoOnPreparedListener);
		mVideoPlayer.setOnCompletionListener(mVideoOnCompletionListener);
		mVideoPlayer.setOnPlayStateListener(mVideoOnPlayStateListener);
		mVideoPlayer.setOnVideoSizeChangedListener(mVideoOnVideoSizeChangedListener);
		mVideoPlayer.setOnErrorListener(mVideoOnErrorListener);
		mVideoPlayer.setOnClickListener(this);

	}

	private OnInfoListener mVideoOnInfoListener = new OnInfoListener() {

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			return false;
		}
	};

	private OnPreparedListener mVideoOnPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			mVideoPlayer.start();
			if (mPlayStatus != null) {
				mPlayStatus.setVisibility(View.INVISIBLE);
			}
		}
	};

	private OnCompletionListener mVideoOnCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			mPlayStatus.setVisibility(View.VISIBLE);
		}
	};
	private OnPlayStateListener mVideoOnPlayStateListener = new OnPlayStateListener() {

		@Override
		public void onStateChanged(boolean isPlaying) {
			// TODO Auto-generated method stub

		}
	};
	private OnErrorListener mVideoOnErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			mPlayStatus.setVisibility(View.VISIBLE);
			return false;
		}
	};

	private OnVideoSizeChangedListener mVideoOnVideoSizeChangedListener = new OnVideoSizeChangedListener() {

		@Override
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			onBackPressed();
			break;
		case R.id.titleRight:
			new CaptureThumbnailsTask().execute();

			break;
		case R.id.preview_theme:
			if (mVideoPlayer != null) {
				if (mVideoPlayer.isPlaying()) {
					mVideoPlayer.pause();
					mPlayStatus.setVisibility(View.VISIBLE);
				} else {
					mVideoPlayer.start();
					mPlayStatus.setVisibility(View.INVISIBLE);
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (FileUtils.checkFile(new File(mVideoPath))) {
			showAlertDialog();
		} else {
			finish();
		}

	}

	private Dialog mAlertDialog;

	private void showAlertDialog() {
		if (mAlertDialog == null) {
			mAlertDialog = DialogUtils.confirmDialog(ActivityVideoPreviewWithLogo.this, "确定要放弃当前视频", "确定放弃", "取消",
					new DialogUtils.ConfirmDialog() {

						@Override
						public void onOKClick(Dialog dialog) {
							DataCleanManager.deleteFolderFile(mVideoPath, true);
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

	public class CaptureThumbnailsTask extends AsyncTask<Void, Void, Boolean> {
		FileOutputStream fos = null;
		Bitmap bitmap = null;

		@Override
		protected Boolean doInBackground(Void... params) {
			bitmap = VideoUtils.getFramBitmap(mVideoPath);
			try {
				fos = new FileOutputStream(mCoverPath);
				bitmap.compress(CompressFormat.JPEG, 90, fos);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap != null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress("正在处理");
			mProgressDialog.setOnKeyListener(dialogOnkeyListener);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			hideProgress();
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
				System.gc();
			}
			if (result) {
				Intent intent = new Intent(ActivityVideoPreviewWithLogo.this, ActivityShareVideo.class);
				intent.putExtra(ContantsActivity.Video.VIDEO_PATH, mVideoPath);
				intent.putExtra(ContantsActivity.Video.VIDEO_COVER_PATH, mCoverPath);
				intent.putExtra(ContantsActivity.Video.VIDEO_DURING, mVideoDuring);
				if (!TextUtils.isEmpty(topic_name)) {
					intent.putExtra(ContantsActivity.Topic.TOPIC_NAME, topic_name);
					intent.putExtra(ContantsActivity.Topic.TOPIC_ID,topic_id);
					intent.putExtra(ContantsActivity.Topic.TOPIC_PAGE, topic_page);
				}
				startActivityForResult(intent, ACTION_GOTO_PUSH);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTION_GOTO_PUSH) {
			if (resultCode == Activity.RESULT_OK) {
				setResult(Activity.RESULT_OK, data);
				finish();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				mVideoPlayer.start();
				mPlayStatus.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

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
	protected void onDestroy() {
		super.onDestroy();
		if (dialogOnkeyListener != null) {
			dialogOnkeyListener = null;
		}
		if (mVideoPlayer != null) {
			mVideoPlayer.release();
			mVideoPlayer = null;
		}
		if (mVideoOnInfoListener != null) {
			mVideoOnInfoListener = null;
		}
		if (mVideoOnPreparedListener != null) {
			mVideoOnPreparedListener = null;
		}
		if (mVideoOnPlayStateListener != null) {
			mVideoOnPlayStateListener = null;
		}
		if (mVideoOnErrorListener != null) {
			mVideoOnErrorListener = null;
		}
		if (mVideoOnVideoSizeChangedListener != null) {
			mVideoOnVideoSizeChangedListener = null;
		}
		if (mAlertDialog != null) {
			mAlertDialog = null;
		}
		System.gc();

	};

}
