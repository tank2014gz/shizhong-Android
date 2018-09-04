package com.shizhong.view.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shizhong.view.ui.base.BaseHXOptionManager;
import com.shizhong.view.ui.base.BaseUmengActivity;
import com.shizhong.view.ui.base.BaseUmengManager;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.InputMethodUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.RexUtils;
import com.shizhong.view.ui.base.utils.SecureUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.ClearEditText;
import com.shizhong.view.ui.bean.ShareContentBean;
import com.shizhong.view.ui.bean.UserBean;
import com.shizhong.view.ui.bean.UserBeanDataPackage;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseUmengActivity implements OnClickListener {
	private ClearEditText mPhoneEdit;
	private ClearEditText mPwdEdit;
	BaseUmengManager umengManager;
	private SHARE_MEDIA share_media;
	private TextView mQQLogin;
	private TextView mWeiChartLogin;
	private TextView mSinaLogin;

	public static final int ACTION_LOGIN_STYLE_PHONE=0;//手机
	public static final int ACTION_LOGIN_STYLE_WEIXIN=1;//微信
	public static final int ACTION_LOGIN_STYLE_WEIBO=2;//微博
	public static final int ACTION_LOGIN_STYLE_QQ=3;//QQ


	private static final int ACTION_REG_PHONE=4;//手机注册
	private static final int ACTION_REG_WEIXIN=5;//微信注册
	private static final int  ACTION_REG_WEIBO=6;//微博注册
	private static final int ACTION_REG_QQ=7;//QQ注册
	private int current_reg_action=-1;


	private final  int  ACTION_GO_REG=8;
	private final  int  ACTION_GO_FIND_BACK_PWD=9;
	private final  int  ACTION_GO_LOGIN=10;


	private String getThreadRegTag = "/member/thirdReg";
	private String getTreadLoginTag = "/member/thirdLogin";
	private String getPhoneLoginTag = "/member/login";

	@Override
	protected void initView() {
		setContentView(R.layout.activity_login);
		findViewById(R.id.login_door).setOnClickListener(this);
		findViewById(R.id.reg_door).setOnClickListener(this);
		findViewById(R.id.find_pwd_door).setOnClickListener(this);

		mPhoneEdit = (ClearEditText) findViewById(R.id.et_denglu_userid);
		mPwdEdit = (ClearEditText) findViewById(R.id.et_denglu_pwd);
		mSinaLogin = (TextView) findViewById(R.id.sina_login);
		mSinaLogin.setOnClickListener(this);
		mQQLogin = (TextView) findViewById(R.id.qq_login);
		mQQLogin.setOnClickListener(this);
		mWeiChartLogin = (TextView) findViewById(R.id.wechat_login);
		mWeiChartLogin.setOnClickListener(this);
//		if (!umengManager.isInstallApp(SHARE_MEDIA.SINA, LoginActivity.this)) {
//			mSinaLogin.setVisibility(View.GONE);
//		}
		if (!umengManager.isInstallApp(SHARE_MEDIA.QQ, LoginActivity.this)) {
			mQQLogin.setVisibility(View.GONE);
		}
		if (!umengManager.isInstallApp(SHARE_MEDIA.WEIXIN, LoginActivity.this)) {
			mWeiChartLogin.setVisibility(View.GONE);
		}
	}

	private ShareContentBean inviteFriendsInfoBean;

	@Override
	protected void initBundle() {
		umengManager = new BaseUmengManager();
		umengManager.addLoginPermission(LoginActivity.this);
		inviteFriendsInfoBean = new ShareContentBean();
		inviteFriendsInfoBean.shareUrl = "http://shizhongapp.com";
		inviteFriendsInfoBean.app_icon_id = R.drawable.sz_shar_image;

	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View view) {
		InputMethodUtils.hide(LoginActivity.this, view);
		switch (view.getId()) {
		case R.id.login_door:
			String phone = mPhoneEdit.getText().toString().trim();
			String pwd = mPwdEdit.getText().toString().trim();
			if (TextUtils.isEmpty(phone)) {
				ToastUtils.showShort(LoginActivity.this, getString(R.string.phone_null_err));
				return;
			}

			if (!phone.matches(RexUtils.PHOEN_REX)) {
				ToastUtils.showShort(LoginActivity.this, getString(R.string.phone_not_match_err));
				return;
			}

			if (TextUtils.isEmpty(pwd)) {
				ToastUtils.showShort(LoginActivity.this, getString(R.string.pwd_no_error));
				return;
			}
			try {
				pwd = SecureUtils.aesEncryptToBytes(pwd, "shizhongshizhong");
			} catch (Exception e) {
				e.printStackTrace();
			}
//			try {
//				String exs = SecureUtils.aesDecryptByBytes(pwd, "shizhongshizhong");
////				System.out.println(exs);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			PrefUtils.putInt(LoginActivity.this, ContantsActivity.LoginModle.LOGIN_PLATFORM, ACTION_LOGIN_STYLE_PHONE);
			loginRequest(phone, pwd);

			break;
		case R.id.reg_door:
			current_reg_action=ACTION_REG_PHONE;
			mIntent.setClass(this, RegNav1Activity.class);
			startActivityForResult(mIntent, current_reg_action);
			break;
		case R.id.find_pwd_door:
			mIntent.setClass(this, FindBackPwdActivity.class);
			startActivityForResult(mIntent,ACTION_GO_FIND_BACK_PWD);

			break;
		case R.id.qq_login:
			isFinishAuthor = false;
			umengManager.doAuthor(LoginActivity.this, SHARE_MEDIA.QQ, mAuthorListener);
			break;
		case R.id.sina_login:
			isFinishAuthor = false;
			umengManager.doAuthor(LoginActivity.this, SHARE_MEDIA.SINA, mAuthorListener);
			break;
		case R.id.wechat_login:
			isFinishAuthor = false;
			umengManager.doAuthor(LoginActivity.this, SHARE_MEDIA.WEIXIN, mAuthorListener);

			break;

		default:
			break;
		}
	}









	/**
	 * 手机平台登录注册
	 * 
	 * @param phone
	 * @param pwd
	 */

	private void loginRequest(final String phone, final String pwd) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("password", pwd);
		showLoadingDialog();
		LogUtils.e("手机登录", "-------");
		
		BaseHttpNetMananger.getInstance(LoginActivity.this).postJSON(LoginActivity.this, getPhoneLoginTag, params,
				new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						int code = 0;
						String msg = "";
						try {
							JSONObject root = new JSONObject(req);
							code = root.getInt("code");
							if (code != 100001) {
								if (code == 900001) {
									msg = root.getString("msg");
								}
								ToastUtils.showErrorToast(LoginActivity.this, code, msg, true);

								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}

						PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.PHONE, phone);
						PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.PWD, pwd);
						entureLoginSucsess(req, null, null, false);
					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(LoginActivity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(LoginActivity.this, getString(R.string.net_conected_error));
					}
				}, false);

	}

	private boolean isFinishAuthor;
	private UMAuthListener mAuthorListener = new UMAuthListener() {

		@Override
		public void onCancel(SHARE_MEDIA arg0) {
			ToastUtils.showShort(LoginActivity.this, arg0.name() + "取消授权");
			dismissLoadingDialog();

		}

		@Override
		public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
			if (!isFinishAuthor) {
				isFinishAuthor = true;
				share_media = arg1;
				showLoadingDialog();
				umengManager.getPlactformInfo(LoginActivity.this, arg1, mDataListener);
			}
		}

		@Override
		public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
			ToastUtils.showShort(LoginActivity.this, arg1.name() + "授权异常");
			dismissLoadingDialog();
		}

		@Override
		public void onStart(SHARE_MEDIA arg0) {
			// ToastUtils.showShort(LoginActivity.this, "开始" + arg0.name() +
			// "授权");
			showLoadingDialog();
		}

	};

	private UMDataListener mDataListener = new UMDataListener() {

		@Override
		public void onComplete(int arg0, Map<String, Object> map) {

			String type = null;
			String appUid = null;
			switch (share_media) {
			case QQ:
				type = "qq";
				appUid = (String) map.get("openid");

				break;
			case WEIXIN:

				type = "wechat";
				appUid = (String) map.get("openid");
				break;
			case SINA:
				type = "weibo";
				try {
					appUid = (Integer) map.get("uid") + "";
				} catch (Exception e) {
					// TODO: handle exception
					appUid = (Long) map.get("uid") + "";
				}

				break;
			default:
				break;
			}

			PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.APPID_PLATEFORM, appUid);
			PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.PLATFORM_TYPE, type);
			threadPlateformLogin(type, appUid, share_media, map);
		}

		@Override
		public void onStart() {
			showLoadingDialog();
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		umengManager.onActivityResult(requestCode, resultCode, data);

		switch (resultCode){
			case ContantsActivity.Action.RESULT_ACTION_REG_SUCCESS:
			case ContantsActivity.Action.RESULT_ACTION_FIND_BACK_SUCCESS:
				mIntent.setClass(LoginActivity.this, MainActivity.class);
				PrefUtils.putBoolean(LoginActivity.this, ContantsActivity.LoginModle.IS_LOGIN, true);
				startActivity(mIntent);
				onBackPressed();
				break;
		}

	}


	private void threadPlateformLogin(final String type, final String appUid, final SHARE_MEDIA share_media,
			final Map<String, Object> userInfo) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		params.put("appUid", appUid);
		LogUtils.e("第三方登录", "-------");
		BaseHttpNetMananger.getInstance(LoginActivity.this).postJSON(LoginActivity.this, getTreadLoginTag, params,
				new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						int code = 0;
						String msg = null;
						try {
							JSONObject rootOBJ = new JSONObject(req);
							code = rootOBJ.getInt("code");
							if (code != 100001) {
								if (code == 430002) {
									regThreadPlateForm(type, appUid, share_media, userInfo);
								}
								if (code == 900001) {
									msg = rootOBJ.getString("msg");
								}

								ToastUtils.showErrorToast(LoginActivity.this, code, msg, true);
								return;
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						entureLoginSucsess(req, share_media, userInfo, false);

					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(LoginActivity.this, getString(R.string.net_conected_error));
						dismissLoadingDialog();

					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(LoginActivity.this, getString(R.string.net_error));

					}
				}, false);
	}

	private void regThreadPlateForm(String type, String appUid, final SHARE_MEDIA share_media,
			final Map<String, Object> userInfo) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		params.put("appUid", appUid);
		params.put("regPlatform", ContantsActivity.LoginModle.REG_PLATFORM);
		params.put("deviceId", PrefUtils.getString(this, ContantsActivity.LoginModle.DEVICES_ID, ""));
		LogUtils.e("三方注册", "-------");
		BaseHttpNetMananger.getInstance(LoginActivity.this).postJSON(LoginActivity.this, getThreadRegTag, params,
				new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						int code = 0;
						String msg = "";
						try {
							JSONObject root = new JSONObject(req);
							code = root.getInt("code");
							if (code != 100001) {
								if (code == 900001) {
									msg = root.getString("msg");
								}
								ToastUtils.showErrorToast(LoginActivity.this, code, msg, true);

								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
						entureLoginSucsess(req, share_media, userInfo, true);

					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
					}
				}, false);
	}

	private void entureLoginSucsess(String req, SHARE_MEDIA share_media, Map<String, Object> threadPlatformUserInfo,
			boolean isReg) {
		UserBeanDataPackage root = GsonUtils.json2Bean(req, UserBeanDataPackage.class);
		int code = root.code;
		switch (code) {
		case 100001:
			UserBean bean = root.data;
			if (bean != null) {
				String loginToken = bean.token;
				String memberId = bean.memberId;
				PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.LOGIN_TOKEN, loginToken);
				PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.LOGIN_USER_ID, memberId);
				dismissLoadingDialog();
				if (isReg) {
					BaseHXOptionManager.reg_HuanXin(LoginActivity.this, memberId);
					if (threadPlatformUserInfo != null && share_media != null) {
						String headUrl = null;
						String nickName = null;
						String sex = null;
						switch (share_media) {
						case QQ:
							headUrl = (String) threadPlatformUserInfo.get("profile_image_url");
							nickName = (String) threadPlatformUserInfo.get("screen_name");
							sex = threadPlatformUserInfo.get("gender").equals("男") ? "1" : "2";
							current_reg_action=ACTION_REG_QQ;
							PrefUtils.putInt(LoginActivity.this, ContantsActivity.LoginModle.LOGIN_PLATFORM, ACTION_LOGIN_STYLE_QQ);
							break;
						case WEIXIN:
							headUrl = (String) threadPlatformUserInfo.get("headimgurl");
							nickName = (String) threadPlatformUserInfo.get("nickname");
							sex = (Integer) threadPlatformUserInfo.get("sex") + "";
							current_reg_action=ACTION_REG_WEIXIN;
							PrefUtils.putInt(LoginActivity.this, ContantsActivity.LoginModle.LOGIN_PLATFORM, ACTION_LOGIN_STYLE_WEIXIN);
							break;

						case SINA:
							headUrl = (String) threadPlatformUserInfo.get("profile_image_url");
							nickName = (String) threadPlatformUserInfo.get("screen_name");
							sex = (Integer) threadPlatformUserInfo.get("gender") + "";
							// 新浪邀请好友开始
							PrefUtils.putInt(LoginActivity.this, ContantsActivity.LoginModle.LOGIN_PLATFORM, ACTION_LOGIN_STYLE_WEIBO);
							umengManager.inviteNewSinaUser(LoginActivity.this, inviteFriendsInfoBean);
							current_reg_action=ACTION_REG_WEIBO;
							break;

						default:
							break;
						}
						PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.HEAD_URL, headUrl);
						PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.NICK_NAME, nickName);
						PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.SEX, sex);
						mIntent.setClass(LoginActivity.this, RegFinishUserInfoActivity.class);
//						mIntent.putExtra(ContantsActivity.LoginModle.HEAD_URL, headUrl);
//						mIntent.putExtra(ContantsActivity.LoginModle.NICK_NAME, nickName);
//						mIntent.putExtra(ContantsActivity.LoginModle.SEX, sex);
						startActivityForResult(mIntent, current_reg_action);
					}
				} else {
					BaseHXOptionManager.login_Huanxin(LoginActivity.this, memberId);
					PrefUtils.putBoolean(LoginActivity.this, ContantsActivity.LoginModle.IS_LOGIN, true);
					if (threadPlatformUserInfo != null && share_media != null) {
						String headUrl = null;
						String nickName = null;
						String sex = null;
						switch (share_media) {
						case QQ:
							headUrl = (String) threadPlatformUserInfo.get("profile_image_url");
							nickName = (String) threadPlatformUserInfo.get("screen_name");
							sex = threadPlatformUserInfo.get("gender").equals("男") ? "1" : "2";
							PrefUtils.putInt(LoginActivity.this, ContantsActivity.LoginModle.LOGIN_PLATFORM, ACTION_LOGIN_STYLE_QQ);
							break;
						case WEIXIN:
							headUrl = (String) threadPlatformUserInfo.get("headimgurl");
							nickName = (String) threadPlatformUserInfo.get("nickname");
							sex = (Integer) threadPlatformUserInfo.get("sex") + "";
							PrefUtils.putInt(LoginActivity.this, ContantsActivity.LoginModle.LOGIN_PLATFORM, ACTION_LOGIN_STYLE_WEIXIN);
							break;

						case SINA:
							headUrl = (String) threadPlatformUserInfo.get("profile_image_url");
							nickName = (String) threadPlatformUserInfo.get("screen_name");
							sex = (Integer) threadPlatformUserInfo.get("gender") + "";
							PrefUtils.putInt(LoginActivity.this, ContantsActivity.LoginModle.LOGIN_PLATFORM, ACTION_LOGIN_STYLE_WEIBO);
							break;
						default:
							break;
						}
						PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.HEAD_URL, headUrl);
						PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.NICK_NAME, nickName);
						PrefUtils.putString(LoginActivity.this, ContantsActivity.LoginModle.SEX, sex);
//						mIntent.putExtra(ContantsActivity.LoginModle.HEAD_URL, headUrl);
//						mIntent.putExtra(ContantsActivity.LoginModle.NICK_NAME, nickName);
//						mIntent.putExtra(ContantsActivity.LoginModle.SEX, sex);
					}
					mIntent.setClass(LoginActivity.this, MainActivity.class);
					startActivity(mIntent);
//					Intent finishIntent = new Intent();
//					finishIntent.setAction(ContantsActivity.Action.ACTION_APP_REG_SUCCESS);// 登录或注册
//					finishIntent.putExtra(ContantsActivity.Extra.IS_FINISH_BACK_ACTIVITY, true);
//					sendBroadcast(finishIntent);
					finish();
				}

			}
			break;
		}
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getPhoneLoginTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getThreadRegTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getTreadLoginTag);
		}
		if (umengManager != null) {
			umengManager = null;
		}
		System.gc();

	}
}
