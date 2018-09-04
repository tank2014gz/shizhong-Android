package com.shizhong.view.ui.base.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

public class CameraManager {

	private static String mVideoCachePath;
	// private static final String FFMPEG_LOG_FILENAME_TEMP = "ffmpeg_log.log";
	private static final String FFMPEG_FILTER_IMAGE = "shizhong_video_logo.png";
	private volatile static String mVideo_logo_path;
	private volatile static File mVideo_thumbs_images_path;// 视频截图的文件路径

	private static String SD_PHOTO_ALBUM_PATH;

	/** 获取视频缓存文件夹 */
	public static String getVideoCachePath() {
		return mVideoCachePath;
	}

	public static void setPhotoAlbumPath(String path) {
		SD_PHOTO_ALBUM_PATH = path;
	}

	public static String getPhotoAlbumPath() {
		return SD_PHOTO_ALBUM_PATH;
	}

	public static String getFilterPngName() {
		return mVideo_logo_path;
	}

	public static File getThumbImages() {
		return mVideo_thumbs_images_path;
	}

	public static void initThumbImagePath(Context context) {
		mVideo_thumbs_images_path = new File(
				context.getCacheDir().getAbsolutePath().concat(File.separator).concat("thumbs"));
		if (!mVideo_thumbs_images_path.exists()) {
			mVideo_thumbs_images_path.mkdirs();
		}
	}

	public static void copyLogoToSDCard(final Context context) {

		new Thread() {
			public void run() {
				try {

					File file = new File(
							context.getCacheDir().getAbsolutePath().concat(File.separator).concat(FFMPEG_FILTER_IMAGE));
					if (file.exists()) {
						mVideo_logo_path = file.getAbsolutePath();
					} else {
						file.createNewFile();
						mVideo_logo_path = file.getAbsolutePath();
						InputStream source = context.getAssets().open(FFMPEG_FILTER_IMAGE);
						OutputStream fs = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int len;
						while ((len = source.read(buf)) > 0) {
							fs.write(buf, 0, len);
						}
						source.close();
						fs.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}

	/** 设置视频缓存路径 */
	public static void setVideoCachePath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		mVideoCachePath = path;

		// // 生成空的日志文件
		// File temp = new File(mVideoCachePath, FFMPEG_LOG_FILENAME_TEMP);
		// if (!temp.exists()) {
		// try {
		// temp.createNewFile();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
	}
}
