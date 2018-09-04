package com.shizhong.view.ui.base.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuliyan on 15/12/29.
 */
public class HeadPhotosCameraManager {
	public static final int ZOOM_CAMERA_OPTION = 0x1;// 拍照获取下一步要进行剪切操作
	public static final int ZOOM_ALIBUM_OPTION = 0x2;
	public static final int CACHE_CAMERA_OPTION = 0x3;// 直接从拍照中获取图片
	public static final int CACHE_ALIBUM_OPTION = 0x4;// 从相册中获取图片
	public static final int CACHE_ZOOM_FILE_OPTION = 0x5;// 剪切图片获取图片

	private File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	private File mCurrentPhotoFile;// 照相机拍照得到的图片
	private File mCurrentMinPhotoFile;

	private PhotoCallBack mPhotoCallBack;

	@SuppressWarnings("unused")
	private HeadPhotosCameraManager() {
		super();
	}

	public HeadPhotosCameraManager(PhotoCallBack callBack) {
		this.mPhotoCallBack = callBack;
	}

	public interface PhotoCallBack {
		void getPhoto(Object path);

		void cancelPhoto();
	}

	/**
	 * 从系统相机中获取图片
	 *
	 * @param activity
	 */

	public void cacheFromCamera(Activity activity, boolean isCut) {

		int state = SDUtils.sd_media_mounted_station();// 检查sd卡当前的状态
		switch (state) {
		case SDUtils.ACTION_RUN_MOUNTED: {
			PHOTO_DIR.mkdirs();// 创建照片的存储目录
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			// 下面这句指定调用相机拍照后的照片存储的路径
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
			// 拍照是否要剪切
			if (isCut) {
				activity.startActivityForResult(intent, ZOOM_CAMERA_OPTION);
			} else {
				activity.startActivityForResult(intent, CACHE_CAMERA_OPTION);
			}
			break;
		}
		case SDUtils.ERROR_ACTION_MEDIA_FULL:
			ToastUtils.showShort(activity, "SD已满");
			break;
		case SDUtils.ERROR_ACTION_UNMOUNTED:
			ToastUtils.showShort(activity, "没有找到sd卡");
			break;
		}

	}

	/**
	 * 用当前时间给取得的图片命名
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyy-MM-dd-HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	// 当前时间命名剪切图片命名
	private String getMinPhotoFileName() {
		return "min_" + getPhotoFileName();
	}

	/**
	 * 从系统相册中获取图片
	 *
	 * @param activity
	 */
	public void cacheFromAlbum(Activity activity, boolean isCut) {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		/**
		 * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		 * intent.setType(""image/*");设置数据类型 如果朋友们要限制上传到服务器的图片类型时可以直接写如：
		 * "image/jpeg 、 image/png等的类型"
		 */
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		// 相册选图是否要剪切
		if (isCut) {
			activity.startActivityForResult(intent, ZOOM_ALIBUM_OPTION);
		} else {
			activity.startActivityForResult(intent, CACHE_ALIBUM_OPTION);
		}
	}

	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		if (data != null) {
			if (data.getData() != null) {
				LogUtils.e("IMAGE URL",
						data.getData().toString() + "data:" + data.getDataString() + "path" + data.getData().getPath());
			}
		}

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case ZOOM_CAMERA_OPTION:
				// 拍照剪切
				if (mCurrentPhotoFile != null) {
//					Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoFile.getAbsolutePath());
					String image = null;
					try {
						image = MediaStore.Images.Media.insertImage(activity.getContentResolver(), mCurrentPhotoFile.getAbsolutePath(), "", "");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					LogUtils.e("camera","调取相机缩略图");
//					LogUtils.e("camera",data.getData().toString());
					startPhotoZoom(activity, Uri.parse(image));

				}
				break;
			case ZOOM_ALIBUM_OPTION:
				// 相册选图剪切
				if (data != null) {
					LogUtils.e("photo","调取相机缩略图");
					LogUtils.e("photo",data.getData().toString());
					startPhotoZoom(activity, data.getData());
				}
				break;
			case CACHE_CAMERA_OPTION:
				// 相机拍照选图
				if (mCurrentPhotoFile != null) {
					if (mPhotoCallBack != null) {
						mPhotoCallBack.getPhoto(mCurrentPhotoFile.getAbsolutePath());
					}
				}
				break;
			case CACHE_ALIBUM_OPTION:
				// 相册选图
				if (data != null && data.getData() != null) {
					Uri selectedImage = data.getData();
					String picturePath = selectedImage.getPath();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = activity.getContentResolver().query(selectedImage, filePathColumn, null, null,
							null);
					if (null != cursor) {
						cursor.moveToFirst();

						int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
						picturePath = cursor.getString(columnIndex);
						cursor.close();
					} else {
						picturePath = selectedImage.getPath();
					}
					if (mPhotoCallBack != null) {
						mPhotoCallBack.getPhoto(picturePath);
					}
				} else {
					ToastUtils.showShort(activity, "找不到您选择的图片");
				}

				break;
			case CACHE_ZOOM_FILE_OPTION:
				if (data != null) {
					if (mPhotoCallBack != null) {
							Bitmap bmap = data.getParcelableExtra("data");
								FileOutputStream foutput = null;
								try {
									foutput = new FileOutputStream(this.mCurrentMinPhotoFile);
									bmap.compress(Bitmap.CompressFormat.PNG, 100, foutput);
									mPhotoCallBack.getPhoto(mCurrentMinPhotoFile);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} finally {
									if (null != foutput) {
										try {
											foutput.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
					}}
				}
				break;

			}

		} else if (resultCode == Activity.RESULT_CANCELED) {
			if (mPhotoCallBack != null) {
				mPhotoCallBack.cancelPhoto();
			}
		}
	}

	/**
	 * 裁剪图片方法实现
	 *
	 * @param uri
	 */
	public void startPhotoZoom(Activity activity, Uri uri) {
		if (uri != null) {
			/*
			 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
			 * yourself_sdk_path/docs/reference/android/content/Intent.html
			 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C
			 * C++ 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
			 */
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
			intent.putExtra("crop", "true");
			// aspectX aspectY 是宽高的比例
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			// outputX outputY 是裁剪图片宽高
			intent.putExtra("outputX", 150);
			intent.putExtra("outputY", 150);
			intent.putExtra("return-data", true);
			mCurrentMinPhotoFile = new File(PHOTO_DIR, getMinPhotoFileName());
			intent.putExtra("output", Uri.fromFile(mCurrentMinPhotoFile));
			intent.putExtra("outputFormat", "JPEG");
			activity.startActivityForResult(intent, CACHE_ZOOM_FILE_OPTION);
		} else {
			LogUtils.e("xlanet error","uri 获取不到图片");
			ToastUtils.showShort(activity, "找不到您选择的图片");
		}
	}

}
