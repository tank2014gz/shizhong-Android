package com.shizhong.view.ui.base.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.utils.DeviceUtils;
import com.shizhong.view.ui.base.utils.HeadPhotosCameraManager;
import com.shizhong.view.ui.base.utils.ToastUtils;

/**
 * Created by yuliyan on 15/12/29.
 */
public class MenuChoisePhotos extends PopupWindow
		implements View.OnClickListener, HeadPhotosCameraManager.PhotoCallBack {

	private HeadPhotosCameraManager headPhotosCameraManager;
	private boolean isCut;
	private PhotoCallBack mPhotoCallBack;
	private boolean isCamera;

	@Override
	public void getPhoto(Object path) {
		if (mPhotoCallBack != null) {
			mPhotoCallBack.callPhoto(path);
		}
	}

	@Override
	public void cancelPhoto() {
		this.dismiss();
	}

	public interface PhotoCallBack {
		public void callPhoto(Object photo);
	}

	public void setCallBack(PhotoCallBack callBack) {
		this.mPhotoCallBack = callBack;
	}

	public void setCut(boolean isCut) {
		this.isCut = isCut;
	}

	private Context mContext;
	private View mRootView;

	public MenuChoisePhotos(Context context, int width, int height) {
		super(context);
		this.mContext = context;
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setWidth(width);
		setHeight(height);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.setBackgroundDrawable(new ColorDrawable(0x4d000000));
		this.setAnimationStyle(R.style.dialog_anim_bottom);
		mRootView = LayoutInflater.from(mContext).inflate(R.layout.menu_camera_bottom_layout, null);
		mRootView.setOnClickListener(this);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		initView(mRootView);
		setContentView(mRootView);
	}

	public MenuChoisePhotos(Context context) {
		this(context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

	}

	protected void initView(View view) {
		view.setOnClickListener(this);
		view.findViewById(R.id.camera_menu).setOnClickListener(this);
		view.findViewById(R.id.cancel_menu).setOnClickListener(this);
		view.findViewById(R.id.photos_menu).setOnClickListener(this);

	}

	public void show(View view) {
		this.setAnimationStyle(R.style.dialog_anim_bottom);
		this.showAtLocation(view, Gravity.BOTTOM, 0, 0);
	}

	@Override
	public void onClick(View view) {
		dismiss();
		switch (view.getId()) {
		case R.id.camera_menu:
			isCamera = true;
//			if (DeviceUtils.hasM()) {
//				checkCameraPermission();
//			} else {
				if (this.headPhotosCameraManager == null) {
					this.headPhotosCameraManager = new HeadPhotosCameraManager(this);
				}
				this.headPhotosCameraManager.cacheFromCamera((Activity) mContext, isCut);
//			}

			break;
		case R.id.cancel_menu:
		case R.id.window:
			isCamera = false;
			break;
		case R.id.photos_menu:
			isCamera = false;
//			if (DeviceUtils.hasM()) {
//				checkCameraPermission();
//			} else {
				if (this.headPhotosCameraManager == null) {
					this.headPhotosCameraManager = new HeadPhotosCameraManager(this);
				}
				this.headPhotosCameraManager.cacheFromAlbum((Activity) mContext, isCut);
//			}
			break;
		}
	}

	private void checkCameraPermission() {
		// if
		// (!(mContext.checkSelfPermission(android.Manifest.permission.CAMERA)
		// == PackageManager.PERMISSION_GRANTED)) {
		// requestCameraPermission();
		// } else {
		// if (isCamera) {
		// if (this.headPhotosCameraManager == null) {
		// this.headPhotosCameraManager = new HeadPhotosCameraManager(this);
		// }
		// this.headPhotosCameraManager.cacheFromCamera((Activity) mContext,
		// isCut);
		// } else {
		// if (this.headPhotosCameraManager == null) {
		// this.headPhotosCameraManager = new HeadPhotosCameraManager(this);
		// }
		// this.headPhotosCameraManager.cacheFromAlbum((Activity) mContext,
		// isCut);
		// }
		// }
	}

	private static final int REQUEST_PERMISSION_CAMERA_CODE = 0x2003;

	private void requestCameraPermission() {
		// ((Activity) mContext).requestPermissions(new String[] {
		// android.Manifest.permission.CAMERA },
		// REQUEST_PERMISSION_CAMERA_CODE);
	}

	/**
	 * 在Activity中的onActivityResult 方法中执行此方法
	 *
	 * @param act
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(Activity act, int requestCode, int resultCode, Intent data) {
		if (headPhotosCameraManager != null) {
			headPhotosCameraManager.onActivityResult(act, requestCode, resultCode, data);
		}
	}

	public void onRequestPermissionsResult(int requestcode, String[] permissions, int[] grantResult) {
		// TODO Auto-generated method stub
		if (requestcode == REQUEST_PERMISSION_CAMERA_CODE) {
			int grantResultCode = grantResult[0];
			boolean granted = grantResultCode == PackageManager.PERMISSION_GRANTED;
			if (granted) {
				if (isCamera) {
					if (this.headPhotosCameraManager == null) {
						this.headPhotosCameraManager = new HeadPhotosCameraManager(this);
					}
					this.headPhotosCameraManager.cacheFromCamera((Activity) mContext, isCut);
				} else {
					if (this.headPhotosCameraManager == null) {
						this.headPhotosCameraManager = new HeadPhotosCameraManager(this);
					}
					this.headPhotosCameraManager.cacheFromAlbum((Activity) mContext, isCut);
				}
				ToastUtils.showShort(mContext, "您获取相机授权成功");
			} else {
				ToastUtils.showShort(mContext, "您已经拒绝相机授权");
			}
		}

	}

}
