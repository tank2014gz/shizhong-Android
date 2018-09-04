package com.shizhong.view.ui.base;

import java.io.File;

import com.shizhong.view.ui.base.utils.CameraManager;
import com.shizhong.view.ui.base.utils.DeviceUtils;

import android.content.Context;
import android.os.Environment;

public class RecoderVideoManager {

	public static void initRecode(final Context context, int v) {
		new Thread() {
			public void run() {
				File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
				if (DeviceUtils.isZte()) {
					if (dcim.exists()) {
						CameraManager.setVideoCachePath(dcim + "/Camera/ShiZhong/");
						CameraManager.setPhotoAlbumPath(dcim + "/Camera");
					} else {
						CameraManager.setVideoCachePath(
								dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/Camera/ShiZhong/");
						CameraManager.setPhotoAlbumPath(dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/Camera");
					}
				} else {
					CameraManager.setVideoCachePath(dcim + "/Camera/ShiZhong/");
					CameraManager.setPhotoAlbumPath(dcim + "/Camera");
				}
				CameraManager.copyLogoToSDCard(context);
				CameraManager.initThumbImagePath(context);
			};

		}.start();

	}

}
