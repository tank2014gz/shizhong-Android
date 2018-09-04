package com.shizhong.view.ui.video;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.VideoNewCutAdapter;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.utils.DataCleanManager;
import com.shizhong.view.ui.base.utils.FileUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.utils.VideoUtils;
import com.shizhong.view.ui.base.view.MyRecyclerView;
import com.bumptech.glide.Glide;
import com.hyphenate.easeui.ContantsActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ActivityVideoCoverChange extends BaseFragmentActivity implements OnClickListener {
	private String mVideoPath;
	private TextView finishBtn;
	private String mVideoCoverPath;
	private ImageView mVideoCoverImage;
	private MyRecyclerView mRecyclerView;
	private FrameLayout.LayoutParams mRecyclerViewParams;
	private ImageView mCursorImageView;
	private FrameLayout.LayoutParams mCursorLayoutParams;
	private View mCursorViewBack;
	private LayoutParams mCursorBackParams;

	private int mCursorImageSize;
	private int mVideoDuring;
	private int mWidth;
	private float mTimeUnit;
	private ArrayList<String> mCoverImages = new ArrayList<String>();
	private VideoNewCutAdapter mVideosImageAdapter;

	private float DownX;
	private int mCursorLeftMargin;
	private float UpX;
	private String mCurrentChageVideoCoverImagePath;
	float unitTime = 0;
	private String mThempFilePath;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_video_cover_change_layout);
		findViewById(R.id.left_bt).setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText("选择封面");
		finishBtn = ((TextView) findViewById(R.id.commite));
		finishBtn.setOnClickListener(this);
		finishBtn.setText(getString(R.string.finish));
		mVideoCoverImage = (ImageView) findViewById(R.id.video_cover);
		mRecyclerView = (MyRecyclerView) findViewById(R.id.recyclerview_horizontal);
		mCursorImageView = (ImageView) findViewById(R.id.cover_thumb_view);

		LayoutParams ll = (LayoutParams) mVideoCoverImage.getLayoutParams();
		ll.width = mWidth;
		ll.height = mWidth;
		mVideoCoverImage.setLayoutParams(ll);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		// 设置布局管理器
		mRecyclerView.setLayoutManager(layoutManager);
		mVideosImageAdapter = new VideoNewCutAdapter(ActivityVideoCoverChange.this, mCoverImages);
		mRecyclerView.setAdapter(mVideosImageAdapter);

		mCursorImageSize = mWidth / 8 + UIUtils.dipToPx(ActivityVideoCoverChange.this, 10);
		mCursorLayoutParams = (FrameLayout.LayoutParams) mCursorImageView.getLayoutParams();
		mCursorLayoutParams.width = mCursorImageSize;
		mCursorLayoutParams.height = mCursorImageSize;
		mCursorImageView.setLayoutParams(mCursorLayoutParams);

		mRecyclerViewParams = (FrameLayout.LayoutParams) mRecyclerView.getLayoutParams();
		mRecyclerViewParams.height = mWidth / 8;
		mRecyclerViewParams.gravity = Gravity.CENTER_VERTICAL;
		mRecyclerViewParams.setMargins(0, 5, 0, 0);
		mRecyclerView.setLayoutParams(mRecyclerViewParams);

		mCursorViewBack = findViewById(R.id.cursor_parent_layout);
		mCursorBackParams = (LayoutParams) mCursorViewBack.getLayoutParams();
		mCursorBackParams.height = mCursorImageSize + UIUtils.dipToPx(ActivityVideoCoverChange.this, 20);
		mCursorBackParams.gravity = Gravity.CENTER_VERTICAL;
		mCursorViewBack.setLayoutParams(mCursorBackParams);

		mCursorImageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (UpX > 0) {
						DownX = UpX;
					} else {
						DownX = event.getRawX();
					}
					mCursorLayoutParams = (FrameLayout.LayoutParams) mCursorImageView.getLayoutParams();
					mCursorLeftMargin = mCursorLayoutParams.leftMargin;
					break;
				case MotionEvent.ACTION_MOVE:
					float offect = event.getRawX() - DownX;
					changeImageLayout(offect);
					UpX = event.getRawX();
					isGetImage = true;
					break;
				case MotionEvent.ACTION_UP:
					UpX = event.getRawX();
					isGetImage = true;
					break;

				}
				return true;
			}
		});
	}

	public void changeImageLayout(float moveX) {
		if (mCursorImageView != null && mCursorLayoutParams != null) {
			mCursorLayoutParams.leftMargin = mCursorLeftMargin + (int) moveX;
			if (mCursorLeftMargin + (int) moveX < 0 || mCursorLeftMargin + (int) moveX + mCursorImageSize > mWidth) {
				return;
			}
			mCursorImageView.setLayoutParams(mCursorLayoutParams);

		}
	}

	private boolean isGetImage = false;
	private boolean isRelease = false;

	private Thread mChangeImageRources = new Thread(new Runnable() {

		@Override
		public void run() {
			while (!isRelease) {
				if (isGetImage) {
					float size = UpX;
					if (size < mCursorImageSize) {
						size = 0;
					} else if (size + mCursorImageSize >= mWidth) {
						size = mWidth;
					}
					float timeFrame = unitTime * size * (1000);
					mCurrentChageVideoCoverImagePath = mThempFilePath + timeFrame + ".jpg";
					if (!FileUtils.checkFile(mCurrentChageVideoCoverImagePath)) {
						Bitmap bitmap = VideoUtils.getFramBitamp(mVideoPath, timeFrame);
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(mCurrentChageVideoCoverImagePath);
							bitmap.compress(CompressFormat.JPEG, 80, fos);
							fos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Message message1 = handler.obtainMessage();
					message1.what = 2;
					handler.sendMessage(message1);
					isGetImage = false;
				}
			}

		}
	});

	@Override
	protected void initBundle() {
		mWidth = UIUtils.getScreenWidthPixels(ActivityVideoCoverChange.this);
		mVideoPath = getIntent().getStringExtra(ContantsActivity.Video.VIDEO_PATH);/// storage/emulated/0/DCIM/Camera/VCameraDemo/1459939369109.mp4
		mVideoDuring = getIntent().getIntExtra(ContantsActivity.Video.VIDEO_DURING, 0) / 1000;// 12253
		mVideoCoverPath = getIntent().getStringExtra(ContantsActivity.Video.VIDEO_COVER_PATH);/// storage/emulated/0/DCIM/Camera/VCameraDemo/1459939369109.jpg
		mTimeUnit = (float) mVideoDuring / 8;
		unitTime = (float) mVideoDuring / mWidth;
		if (!TextUtils.isEmpty(mVideoPath)) {
			mThempFilePath = mVideoPath.substring(0, mVideoPath.lastIndexOf("/")).concat(File.separator)
					.concat("thumbs").concat(File.separator);
		}

	}

	@Override
	protected void initData() {
		if (!TextUtils.isEmpty(mVideoCoverPath)) {
			Glide.with(ActivityVideoCoverChange.this).load(mVideoCoverPath).into(mVideoCoverImage);
			Glide.with(ActivityVideoCoverChange.this).load(mVideoCoverPath).into(mCursorImageView);
		}
		if (!TextUtils.isEmpty(mVideoPath) && !TextUtils.isEmpty(mThempFilePath)) {
			File file = new File(mThempFilePath);
			if (!file.exists()) {
				file.mkdir();
			}
			getBitmapsFromVideo();
			mChangeImageRources.start();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				mVideosImageAdapter.notifyItemInserted(msg.arg1);
			} else if (msg.what == 2) {
				mVideoCoverImage.setImageURI(FileUtils.getFileUri(mCurrentChageVideoCoverImagePath));
				mCursorImageView.setImageURI(FileUtils.getFileUri(mCurrentChageVideoCoverImagePath));
			}
		};
	};

	/**
	 * 获取视频帧图片
	 *
	 * @version 1.0
	 * @createTime 2015年6月17日,上午11:49:54
	 * @updateTime 2015年6月17日,上午11:49:54
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 * @param dataPath
	 * @param lenth
	 */
	public void getBitmapsFromVideo() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean flag = true;
				int startIndex = 0;
				// out: for (int i = 0; i < 8; i++) {
				// String path = mThempFilePath + i + ".jpg";
				// if (!FileUtils.checkFile(path)) {
				// flag = true;
				// startIndex = i;
				// break out;
				// } else {
				// mCoverImages.add(path);
				// Message message1 = handler.obtainMessage();
				// message1.what = 1;
				// message1.arg1 = i;
				// handler.sendMessage(message1);
				// }
				// }
				if (flag) {
					// 取得视频的长度(单位为秒)
					Bitmap bitmap = null;
					for (int i = startIndex; i < 8; i++) {
						float timeFrame = (i + 1) * mTimeUnit * 1000;
						bitmap = VideoUtils.getFramBitamp(mVideoPath, timeFrame);
						String path = mThempFilePath + i + ".jpg";
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(path);
							bitmap.compress(CompressFormat.JPEG, 80, fos);
							fos.close();
							mCoverImages.add(path);
							Message message1 = handler.obtainMessage();
							message1.what = 1;
							message1.arg1 = i;
							handler.sendMessage(message1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (bitmap != null && !bitmap.isRecycled()) {
						bitmap.recycle();
						bitmap = null;
						System.gc();
					}
				}
			}
		}).start();

	}

	/**
	 * 加载本地图片 http://bbs.3gstdy.com
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			onBackPressed();
			break;
		case R.id.commite:
			if (!TextUtils.isEmpty(mCurrentChageVideoCoverImagePath)) {
				Bitmap bitmap = getLoacalBitmap(mCurrentChageVideoCoverImagePath);
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(mVideoCoverPath);
					bitmap.compress(CompressFormat.JPEG, 100, fos);
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				DataCleanManager.deleteFolderFile(mThempFilePath, true);
				Intent intent = new Intent(ActivityVideoCoverChange.this, ActivityShareVideo.class);
				setResult(Activity.RESULT_OK, intent);
				finish();
			} else {
				finish();
			}

			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isRelease = true;
		if (handler != null) {
			handler.removeMessages(1);
			handler.removeMessages(2);
			handler = null;
		}
		if (mCoverImages != null) {
			mCoverImages.clear();
			mCoverImages = null;
		}
		if (mVideosImageAdapter != null) {
			mVideosImageAdapter = null;
		}
		if (mRecyclerView != null) {
			mRecyclerView.removeAllViews();
			mRecyclerView = null;
		}
		System.gc();

	}

}
