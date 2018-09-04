package com.shizhong.view.ui.base.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.text.TextUtils;

public class ImageCacheManager {

	public static void downLoadImage(final Context context, final String url_path, final ImageCacheListener listener) {
		new Thread(){
			@Override
			public void run() {
				URL url = null;
				InputStream is = null;
				FileOutputStream fos = null;
				File file = null;
				try {
					// 构建图片的url地址
					url = new URL(url_path);
					// 开启连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					// 设置超时的时间，5000毫秒即5秒
					conn.setConnectTimeout(5000);
					// 设置获取图片的方式为GET
					conn.setRequestMethod("GET");
					// 响应码为200，则访问成功
					int code = conn.getResponseCode();
					if (code == 200) {
						// 获取连接的输入流，这个输入流就是图片的输入流
						is = conn.getInputStream();
						// 构建一个file对象用于存储图片
						file = getCacheImage(context, url_path);
						fos = new FileOutputStream(file);
						int len = 0;
						byte[] buffer = new byte[1024];
						// 将输入流写入到我们定义好的文件中
						while ((len = is.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
						}
						// 将缓冲刷入文件
						fos.flush();
						if (listener != null) {
							listener.success(file);
						}

					} else {
		                 if(listener!=null){
		                	 listener.fail("net error code="+code);
		                 }
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (listener != null) {
						listener.fail(e.getMessage());
					}
				} finally {
					// 在最后，将各种流关闭
					try {
						if (is != null) {
							is.close();
						}
						if (fos != null) {
							fos.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			};
		}.start();
		
	}

	public static File getCacheImage(Context context, String url_path) {
		if (TextUtils.isEmpty(url_path)) {
			return null;
		}
		String fileName = MD5.md5(url_path).concat(".jpg");
		File file = new File(PathUtils.getSaveImgPath(context), fileName);
		if (file.exists()) {
			file.delete();
		}
		return file;
	}

	public interface ImageCacheListener {
		public void success(File file);

		public void fail(String reasion);
	}

}
