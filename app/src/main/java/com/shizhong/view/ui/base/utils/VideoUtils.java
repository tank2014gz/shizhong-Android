package com.shizhong.view.ui.base.utils;

import com.wind.ffmpeghelper.FFmpegNativeHelper;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

public class VideoUtils {
	private static MediaMetadataRetriever mMetadataRetriever = new MediaMetadataRetriever();

	public static int getVideoWidth(String path) {
		if (DeviceUtils.hasICS()) {
			if (mMetadataRetriever == null) {
				mMetadataRetriever = new MediaMetadataRetriever();
			}
			try {
				mMetadataRetriever.setDataSource(path);
			} catch (Exception e) {
				return 0;
			}
			return Integer
					.parseInt(mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); //
		} else {
			return FFmpegNativeHelper.getWidth();
		}
	}

	public static int getVideoHeight(String path) {
		if (DeviceUtils.hasICS()) {
			if (mMetadataRetriever == null) {
				mMetadataRetriever = new MediaMetadataRetriever();
			}
			try {
				mMetadataRetriever.setDataSource(path);
			} catch (Exception e) {
				return 0;
			}
			return Integer
					.parseInt(mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
		} else {
			return FFmpegNativeHelper.getHeight();
		}
	}

	public static int getVideoRotate(String path) {
		if (DeviceUtils.hasJellyBeanMr1()) {
			if (mMetadataRetriever == null) {
				mMetadataRetriever = new MediaMetadataRetriever();
			}
			try {
				mMetadataRetriever.setDataSource(path);
			} catch (Exception e) {
				return 0;
			}
			return Integer
					.parseInt(mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
		} else {
			return FFmpegNativeHelper.getRotate();
		}
	}

	public static int getVideoDuring(String path) {
		if (DeviceUtils.hasICS()) {
			if (mMetadataRetriever == null) {
				mMetadataRetriever = new MediaMetadataRetriever();
			}
			try {
				mMetadataRetriever.setDataSource(path);
			} catch (Exception e) {
				return 0;
			}
			String obj = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			if (obj != null) {
				return Integer.parseInt(obj);
			} else {
				return 0;
			}
		} else {
			return (int) FFmpegNativeHelper.getDuration() * 1000;
		}
	}

	public static boolean hasAudioResources(String path) {
		if (DeviceUtils.hasICS()) {
			if (mMetadataRetriever == null) {
				mMetadataRetriever = new MediaMetadataRetriever();
			}
			try {
				mMetadataRetriever.setDataSource(path);
			} catch (Exception e) {
				return false;
			}
			String obj = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
			if (obj != null) {
				return obj.equals("yes");
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static Bitmap getFramBitmap(String path) {
		if (DeviceUtils.hasHoneycomb()) {
			if (mMetadataRetriever == null) {
				mMetadataRetriever = new MediaMetadataRetriever();
			}
			mMetadataRetriever.setDataSource(path);
			return mMetadataRetriever.getFrameAtTime();
		} else {
			return null;
		}
	}

	public static Bitmap getFramBitamp(String path, float timeFrame) {
		if (DeviceUtils.hasHoneycomb()) {
			if (mMetadataRetriever == null) {
				mMetadataRetriever = new MediaMetadataRetriever();
			}
			mMetadataRetriever.setDataSource(path);
			return mMetadataRetriever.getFrameAtTime((int) timeFrame * 1000,
					MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
		} else {
			return null;
		}
	}
}
