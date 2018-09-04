package com.shizhong.view.ui.base.view;

import java.io.File;

import com.shizhong.view.ui.base.utils.FileUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ThumbImageOption;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.text.TextUtils;
import android.widget.ImageView;

/**
 * 视频截图
 * 
 * @author tangjun
 *
 */
public class VideoThumbImageView extends ImageView {

	private static final String TAG = VideoThumbImageView.class.getSimpleName();
	/** 截图存放路径 */
	private String mThumbPath;
	/** 屏幕宽度 */
	private int mWindowWidth;
	/** 索引 */
	private int mIndex;
	/** 当前时间 */
	private int mPosition;
	/** 是否已经加载了图片 */
	private boolean mNeedLoad;

	public VideoThumbImageView(Context context, String thumbPath, int mWindowWidth, int index, int position) {
		super(context);
		this.mWindowWidth = mWindowWidth;
		// this.mThumbPosition = position;
		this.mThumbPath = thumbPath;
		this.mNeedLoad = true;
		this.mIndex = index;
		this.mPosition = position;
	}

	/** 获取截图对应的时间戳 */
	public int getThumbPosition() {
		return mPosition;
	}

	public int getThumbIndex() {
		return mIndex;
	}

	public String getThumbPath() {
		return mThumbPath;
	}

	public void log() {
		Rect rect = new Rect();
		getGlobalVisibleRect(rect);
		LogUtils.d(TAG,
				"[VideoThumbImageView]mNeedLoad:" + mNeedLoad + " checkVisible:" + checkVisible() + ":getLeft:"
						+ getLeft() + ":getRight:" + getRight() + " checkThumb:" + checkThumb() + " "
						+ new File(mThumbPath).getName());
	}

	/** 检测是否需要显示 */
	public boolean checkVisible() {
		if (!TextUtils.isEmpty(mThumbPath)) {
			Rect rect = new Rect();
			getGlobalVisibleRect(rect);

			if (rect.right <= mWindowWidth && rect.right > 0)
				return true;
		}
		return false;
	}

	/** 检测是否需要截图 */
	public boolean checkThumb() {
		return FileUtils.checkFile(mThumbPath);
	}

	/** 检查是否需要加载 */
	public boolean needLoad() {
		return mNeedLoad;
	}

	/** 开始截图 */
	public void loadImage() {
		if (FileUtils.checkFile(mThumbPath)) {
//			setImageURI(FileUtils.getFileUri(mThumbPath));
                 setImageBitmap(ThumbImageOption.createImageThumbnail(mThumbPath));
			mNeedLoad = false;
		}
	}

	// else {
	// new AsyncTask<Void, Void, Boolean>() {
	//
	// @Override
	// protected Boolean doInBackground(Void... params) {
	// String cmd = String.format("ffmpeg %s -ss %.3f -i \"%s\" -s %s -vframes 1
	// \"%s\"", FFMpegUtils.getLogCommand(), mThumbPosition / 1000F, mVideoPath,
	// mThumbWH, mThumbPath);
	// return UtilityAdapter.FFmpegRun("", cmd) == 0;
	// }
	//
	// @Override
	// protected void onPostExecute(Boolean result) {
	// super.onPostExecute(result);
	// // if (result && new File(mMediaPart.thumbPath).exists()) {
	// // loadLocalImage(mMediaPart.thumbPath);
	// // } else {
	// // //mIconView.setImageResource(R.drawable.video_part_thumb_default);
	// // }
	// if (mImageFetcher != null)
	// mImageFetcher.loadLocalImage(mThumbPath, VideoThumbImageView.this);
	//
	// mScroll = false;
	// }
	// }.execute();
	// }



}
