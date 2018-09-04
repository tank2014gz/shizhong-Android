package com.shizhong.view.ui.base.utils;

import java.io.File;

import com.shizhong.view.ui.base.db.VideoUploadTaskDBManager;
import com.shizhong.view.ui.bean.UploadVideoTaskBean;
import android.content.Context;

public class FileOptionsUtils {

	public static void removeVideo(final Context context, final String member_id, final String taskId) {
		new Thread() {
			public void run() {
				UploadVideoTaskBean bean = VideoUploadTaskDBManager.getInstance(context, member_id).findTask(taskId);
				if (bean != null) {
					String videoPath = bean.videoPath;
					String photoAlbumFile = System.currentTimeMillis() + ".mp4";
					if (!FileUtils.checkFile(CameraManager.getPhotoAlbumPath())) {
						new File(CameraManager.getPhotoAlbumPath()).mkdirs();
					}
					String saveVideo = CameraManager.getPhotoAlbumPath().concat(File.separator).concat(photoAlbumFile);
					FileUtils.fileCopy(videoPath, saveVideo);
					String videoSourceFile = videoPath.substring(0, videoPath.lastIndexOf("."));
					DataCleanManager.deleteFolderFile(videoPath, true);
					DataCleanManager.deleteFolderFile(videoSourceFile, true);
				}
			};
		}.start();

	}

}
