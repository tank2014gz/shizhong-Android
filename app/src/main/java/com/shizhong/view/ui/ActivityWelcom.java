package com.shizhong.view.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.BaseHXOptionManager;
import com.shizhong.view.ui.base.SystemSetting;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.DeviceIdentify;
import com.shizhong.view.ui.base.utils.DeviceUtils;
import com.shizhong.view.ui.base.utils.FileUtils;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.ImageCacheManager;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.utils.ImageCacheManager.ImageCacheListener;
import com.shizhong.view.ui.bean.UserBean;
import com.shizhong.view.ui.bean.UserBeanDataPackage;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class ActivityWelcom extends BaseFragmentActivity implements OnClickListener {

	private int login_plateform;
	private volatile boolean isFirstLogin;
	private volatile boolean isSkipLogin;
	private volatile boolean isSkipMainActivity;
	private SimpleDraweeView mBackImage;

	private String coverImageFilePath;
	private String coverImageLoaclPath;
	private File coverImageFile;
	private int Image_type = 0; // 0:小 1：中 2：大
	private String targetType;
	private String url;
	private String ad_id;
	private final int timeRunning=0x1001;
	private final int timeToSkip=0x1002;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case  timeRunning:
					if (isFirstLogin || isSkipLogin || isSkipMainActivity) {
						mHandler.sendEmptyMessage(timeToSkip);
					}
					break;
				case timeToSkip:
					startAnimaton();
					break;
			}

		}
	};







	private void checkInfo() {
		if (PrefUtils.getBoolean(ActivityWelcom.this, ContantsActivity.LoginModle.IS_OPENT_FIRST, true)) {
			isFirstLogin = true;
			PrefUtils.putBoolean(ActivityWelcom.this, ContantsActivity.LoginModle.IS_OPENT_FIRST, false);
		} else {
			if (PrefUtils.getBoolean(ActivityWelcom.this, ContantsActivity.LoginModle.IS_LOGIN, false)) {
				if (PrefUtils.hasString(ActivityWelcom.this, ContantsActivity.LoginModle.LOGIN_PLATFORM)) {
					login_plateform = PrefUtils.getInt(ActivityWelcom.this, ContantsActivity.LoginModle.LOGIN_PLATFORM,
							-1);
					switch (login_plateform){
						case  LoginActivity.ACTION_LOGIN_STYLE_PHONE:
							String phone = PrefUtils.getString(ActivityWelcom.this, ContantsActivity.LoginModle.PHONE, "");
							String pwd=PrefUtils.getString(ActivityWelcom.this, ContantsActivity.LoginModle.PWD, "");
                            if(TextUtils.isEmpty(phone)||TextUtils.isEmpty(pwd)){
								isSkipLogin = true;
								return;
							}
							loginRequest(phone, pwd);

							break;
						case  LoginActivity.ACTION_LOGIN_STYLE_WEIXIN:
						case  LoginActivity.ACTION_LOGIN_STYLE_WEIBO:
						case  LoginActivity.ACTION_LOGIN_STYLE_QQ:
							String type = PrefUtils.getString(ActivityWelcom.this, ContantsActivity.LoginModle.PLATFORM_TYPE,"");
							String appid=PrefUtils.getString(ActivityWelcom.this, ContantsActivity.LoginModle.APPID_PLATEFORM, "");
							if(TextUtils.isEmpty(type)||TextUtils.isEmpty(appid)){
								isSkipLogin = true;
								return;
							}
							threadPlateformLogin(type, appid);
							break;
						default:
							isSkipLogin = true;
							break;
					}
				} else {
					isSkipLogin = true;
				}
			} else {
				isSkipLogin = true;
			}
		}
		if (TextUtils.isEmpty(PrefUtils.getString(this, ContantsActivity.LoginModle.DEVICES_ID, ""))) {
			PrefUtils.putString(this, ContantsActivity.LoginModle.DEVICES_ID, DeviceIdentify.IMEI(this));
		}
	}


	private void startAnimaton() {
		ScaleAnimation animation = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.2f);
		animation.setDuration(1000);
		animation.setFillAfter(true);
		animation.setAnimationListener(animationListener);
		mBackImage.startAnimation(animation);
	}

	private AnimationListener animationListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (isFirstLogin) {
				mIntent.setClass(ActivityWelcom.this, ActivityNavigationImages.class);
			} else if (isSkipLogin) {
				mIntent.setClass(ActivityWelcom.this, LoginActivity.class);
			} else if (isSkipMainActivity) {
				mIntent.setClass(ActivityWelcom.this, MainActivity.class);
			}
			startActivityForResult(mIntent, -1);
			finish();
		}
	};

	@Override
	protected void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_flash_);
		mBackImage = (SimpleDraweeView) findViewById(R.id.flash_image);


	}


	private void getADInfo() {
		String rootUrl = "/advert/getAdvert";
		LogUtils.e("获取广告列表", "-------");
		BaseHttpNetMananger.getInstance(ActivityWelcom.this).postJSON(ActivityWelcom.this, rootUrl,
				new HashMap<String, String>(), new IRequestResult() {

					@Override
					public void requestSuccess(String req) {

						try {
							JSONObject resultObject = new JSONObject(req);
							int code = resultObject.getInt("code");
							if (code == 100001) {
								JSONObject dataObject = resultObject.getJSONObject("data");
								switch (Image_type) {
								case 0:
									coverImageFilePath = dataObject.getString("smallCoverUrl");
									break;
								case 1:
									coverImageFilePath = dataObject.getString("middleCoverUrl");
									break;
								case 2:
									coverImageFilePath = dataObject.getString("bigCoverUrl");
									break;
								}
								targetType = dataObject.getString("targetType");
								PrefUtils.putString(ActivityWelcom.this, ContantsActivity.AD.AD_TARGET_TYPE,
										targetType);
								if (targetType.equals("5") || targetType.equals("3")) {
									url = dataObject.getString("url");
									PrefUtils.putString(ActivityWelcom.this, ContantsActivity.AD.AD_URL, url);
								} else {
									ad_id = dataObject.getString("ad_id");
									PrefUtils.putString(ActivityWelcom.this, ContantsActivity.AD.AD_ID, ad_id);
								}
								ImageCacheManager.downLoadImage(ActivityWelcom.this, coverImageFilePath,
										imageCacheListener);

							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// {"data":{"bigCoverUrl":"http://7xpa3q.com1.z0.glb.clouddn.com/QQ20160130142956.png","middleCoverUrl":"http://7xpa3q.com1.z0.glb.clouddn.com/b8014a90f603738db3f5dc2cb41bb051f819ec05.jpg",
						// "smallCoverUrl":"http://7xpa3q.com1.z0.glb.clouddn.com/b8014a90f603738db3f5dc2cb41bb051f819ec05.jpg","targetType":"1","url":"aa"},"code":100001}
						// TODO Auto-generated method stub
						// ToastUtils.showShort(ActivityWelcom.this, req);

					}

					@Override
					public void requestNetExeption() {
						// TODO Auto-generated method stub

					}

					@Override
					public void requestFail() {
						// TODO Auto-generated method stub

					}
				}, false);

	}

	private ImageCacheListener imageCacheListener = new ImageCacheListener() {

		@Override
		public void success(File file) {
			if (file.exists())
				coverImageFile = file;
			coverImageLoaclPath = coverImageFile.getAbsolutePath();
			PrefUtils.putString(ActivityWelcom.this, ContantsActivity.AD.AD_IMAGE_FILE, coverImageLoaclPath);
		}

		@Override
		public void fail(String reasion) {
//			ToastUtils.showShort(ActivityWelcom.this, reasion);
			LogUtils.e("shizhong error" ,reasion);
		}
	};

	@Override
	protected void initBundle() {
		float height = UIUtils.getScreenHeightPixels(ActivityWelcom.this);
		float width = UIUtils.getScreenWidthPixels(ActivityWelcom.this);
		float h_w = height / width;
		float value1 = Math.abs(h_w - 1.78f);
		float value2 = Math.abs(h_w - 1.50f);
		float value3 = Math.abs(h_w - 1.30f);
		if (value1 > value2) {
			if (value2 > value3) {
				Image_type = 2;
			} else {
				Image_type = 1;
			}
		} else {
			if (value1 > value3) {
				Image_type = 2;
			} else {
				Image_type = 0;
			}
		}

	}

	@Override
	protected void initData() {
		super.initData();
		targetType = PrefUtils.getString(ActivityWelcom.this, ContantsActivity.AD.AD_TARGET_TYPE, "");
		if (PrefUtils.hasString(ActivityWelcom.this, ContantsActivity.AD.AD_IMAGE_FILE)) {
			coverImageFilePath = PrefUtils.getString(ActivityWelcom.this, ContantsActivity.AD.AD_IMAGE_FILE, "");
			coverImageFile = new File(coverImageFilePath);
			mBackImage.setImageURI(FileUtils.getFileUri(coverImageFile.getAbsolutePath()));
			mBackImage.setOnClickListener(this);
		}
		if (PrefUtils.hasString(ActivityWelcom.this, ContantsActivity.AD.AD_ID)) {
			ad_id = PrefUtils.getString(ActivityWelcom.this, ContantsActivity.AD.AD_ID, "");
		}

		if (PrefUtils.hasString(ActivityWelcom.this, ContantsActivity.AD.AD_URL)) {
			url = PrefUtils.getString(ActivityWelcom.this, ContantsActivity.AD.AD_URL, "");
		}

		getADInfo();
		checkInfo();
		mHandler.sendEmptyMessageDelayed(timeRunning,2000);
		getSystemSetting();
	}

	private void loginRequest(final String phone, final String pwd) {
		String rootUrl = "/member/login";
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("password", pwd);
		LogUtils.e("手机登录", "-------");
		BaseHttpNetMananger.getInstance(ActivityWelcom.this).postJSON(ActivityWelcom.this, rootUrl, params,
				new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						int code = 0;
						try {
							JSONObject root = new JSONObject(req);
							code = root.getInt("code");
							if (code != 100001) {
								isSkipLogin = true;
								return;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							isSkipLogin = true;
							return;
						}
						PrefUtils.putInt(ActivityWelcom.this, ContantsActivity.LoginModle.LOGIN_PLATFORM, LoginActivity.ACTION_LOGIN_STYLE_PHONE);
						PrefUtils.putString(ActivityWelcom.this, ContantsActivity.LoginModle.PHONE, phone);
						PrefUtils.putString(ActivityWelcom.this, ContantsActivity.LoginModle.PWD, pwd);
						UserBeanDataPackage root = GsonUtils.json2Bean(req, UserBeanDataPackage.class);

						UserBean bean = root.data;
						if (bean != null) {
							String loginToken = bean.token;
							String memberId = bean.memberId;
							PrefUtils.putString(ActivityWelcom.this, ContantsActivity.LoginModle.LOGIN_TOKEN,
									loginToken);
							PrefUtils.putString(ActivityWelcom.this, ContantsActivity.LoginModle.LOGIN_USER_ID,
									memberId);
							PrefUtils.putBoolean(ActivityWelcom.this, ContantsActivity.LoginModle.IS_LOGIN, true);
							PrefUtils.putBoolean(ActivityWelcom.this, ContantsActivity.LoginModle.IS_OPENT_FIRST,
									false);
							BaseHXOptionManager.login_Huanxin(ActivityWelcom.this, memberId);
							isSkipMainActivity = true;
						}
					}

					@Override
					public void requestFail() {
						LogUtils.e("shizhong error",getString(R.string.net_error));
//						ToastUtils.showShort(ActivityWelcom.this, getString(R.string.net_error));
						isSkipLogin = true;
					}

					@Override
					public void requestNetExeption() {
						LogUtils.e("shizhong error",getString(R.string.net_conected_error));
//						ToastUtils.showShort(ActivityWelcom.this, getString(R.string.net_conected_error));
						isSkipLogin = true;
					}
				}, false);

	}

	private void threadPlateformLogin(final String type, final String appUid) {
		String rootUrl = "/member/thirdLogin";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		params.put("appUid", appUid);
		LogUtils.e("第三方登录", "-------");
		BaseHttpNetMananger.getInstance(ActivityWelcom.this).postJSON(ActivityWelcom.this, rootUrl, params,
				new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						int code = 0;
						try {
							JSONObject rootOBJ = new JSONObject(req);
							code = rootOBJ.getInt("code");
							if (code != 100001) {
								isSkipLogin = true;
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							isSkipLogin = true;
							return;
						}
						UserBeanDataPackage root = GsonUtils.json2Bean(req, UserBeanDataPackage.class);
						UserBean bean = root.data;
						if (bean != null) {
							String loginToken = bean.token;
							String memberId = bean.memberId;
							PrefUtils.putString(ActivityWelcom.this, ContantsActivity.LoginModle.LOGIN_TOKEN,
									loginToken);
							PrefUtils.putString(ActivityWelcom.this, ContantsActivity.LoginModle.LOGIN_USER_ID,
									memberId);
							PrefUtils.putBoolean(ActivityWelcom.this, ContantsActivity.LoginModle.IS_LOGIN, true);
							PrefUtils.putBoolean(ActivityWelcom.this, ContantsActivity.LoginModle.IS_OPENT_FIRST,
									false);
							BaseHXOptionManager.login_Huanxin(ActivityWelcom.this, memberId);
							isSkipMainActivity = true;
						}
					}

					@Override
					public void requestNetExeption() {
						LogUtils.e("shizhong error",getString(R.string.net_conected_error));
//						ToastUtils.showShort(ActivityWelcom.this, getString(R.string.net_conected_error));
						isSkipLogin = true;
					}

					@Override
					public void requestFail() {
//						ToastUtils.showShort(ActivityWelcom.this, getString(R.string.net_error));
						LogUtils.e("shizhong error",getString(R.string.net_error));
						isSkipLogin = true;
					}

				}, false);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mHandler!=null){
			mHandler.removeMessages(timeRunning);
			mHandler.removeMessages(timeToSkip);
			mHandler=null;
		}
		System.gc();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.flash_image:
			if (TextUtils.isEmpty(targetType)) {
				return;
			}
			if (targetType.equals("5") || targetType.equals("3")) {
				mIntent.setClass(ActivityWelcom.this, ActivityNewsWebContent.class);
				mIntent.putExtra(ContantsActivity.JieQu.NEWS_URL, url);
				startActivity(mIntent);
			}

			break;

		default:
			break;
		}

	}



	private void getSystemSetting(){
		String url = "/setting/getAppSetting";
		BaseHttpNetMananger.getInstance(ActivityWelcom.this).postJSON(ActivityWelcom.this, url, new HashMap<String, String>(), new IRequestResult() {
			@Override
			public void requestSuccess(String req) {
				try {
					JSONObject resultObj=new JSONObject(req);
					LogUtils.e("shizhong",req);
					int code=resultObj.getInt("code");
					if(code==100001){
                        JSONObject data=resultObj.getJSONObject("data");
						if(data!=null){

							boolean isUseCategoryRandom=data.getInt("isUseCategoryRandom")==1;
							boolean isUseHotRandom=data.getInt("isUseHotRandom")==1;
							LogUtils.e("shizhong","isUseCategoryRandom:"+isUseCategoryRandom+"isUseHotRandom="+isUseHotRandom);
							SystemSetting.isUseCategoryRandom=isUseCategoryRandom;
							SystemSetting.isUseHotRandom=isUseHotRandom;
						}
					}
				}catch (JSONException e){

				}

			}

			@Override
			public void requestFail() {

			}

			@Override
			public void requestNetExeption() {

			}
		},false);
	}
}
