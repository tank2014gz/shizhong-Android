package com.shizhong.view.ui;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.base.BaseHXOptionManager;
import com.shizhong.view.ui.base.BaseUmengActivity;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.GsonUtils;
import com.shizhong.view.ui.base.utils.InputMethodUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.RexUtils;
import com.shizhong.view.ui.base.utils.SecureUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.ClearEditText;
import com.shizhong.view.ui.bean.UserBean;
import com.shizhong.view.ui.bean.UserBeanDataPackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuliyan on 15/12/23.
 */
public class FindBackPwdActivity extends BaseUmengActivity implements View.OnClickListener, TextWatcher {
	private ClearEditText mPhoneEdit;
	private ClearEditText mIdentifyEdit;
	private ClearEditText mPasswordEdit;
	private TextView mIdentifyBt;
	private TextView mNextBt;
	private String getFindBackPWDTag = "/member/modifyPassword";
	private String getVerifyCodeTag = "/member/sendVerifyCode";
	private String getPhoneLoginTag = "/member/login";

	private final int ACTION_START_TIME=0x1;
	private final int ACTIION_START_RUNNING=0x2;
	private final int ACTION_START_FINISH=0x3;


	int currentTime=0;
	private Handler mTimeHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what){
				case ACTION_START_TIME:
					currentTime=60;
					mIdentifyBt.setText("倒计时" + (currentTime) + "秒");
					mIdentifyBt.setEnabled(false);
					mIdentifyBt.setClickable(false);
					mTimeHandler.sendEmptyMessageDelayed(ACTIION_START_RUNNING,1000);
					break;
				case ACTIION_START_RUNNING:
					if(currentTime>0){
						currentTime--;
						mIdentifyBt.setText("倒计时" + (currentTime) + "秒");
						mTimeHandler.sendEmptyMessageDelayed(ACTIION_START_RUNNING,1000);
					}else{
						mTimeHandler.sendEmptyMessage(ACTION_START_FINISH);
					}
					break;
				case  ACTION_START_FINISH:
					mTimeHandler.removeMessages(ACTIION_START_RUNNING);
					mTimeHandler.removeMessages(ACTION_START_TIME);
					mIdentifyBt.setText(getString(R.string.identify_text));
					mIdentifyBt.setEnabled(true);
					mIdentifyBt.setClickable(true);
					break;
			}
		}
	};

	@Override
	protected void initView() {
		setContentView(R.layout.activity_reg_nav1_layout);
		((TextView) findViewById(R.id.title_tv)).setText(R.string.find_back_pwd_title);
		findViewById(R.id.left_bt).setOnClickListener(this);
		mNextBt = (TextView) findViewById(R.id.next);
		mNextBt.setOnClickListener(this);
		mNextBt.setEnabled(false);
		mPhoneEdit = (ClearEditText) findViewById(R.id.phone_et);
		mPhoneEdit.addTextChangedListener(this);
		mIdentifyBt = (TextView) findViewById(R.id.identify_bt);
		mIdentifyBt.setOnClickListener(this);
		mIdentifyBt.setEnabled(false);
		mIdentifyEdit = (ClearEditText) findViewById(R.id.et_denglu_pwd);
		mPasswordEdit = (ClearEditText) findViewById(R.id.pwd_edit);
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		if (charSequence.length() > 0) {
			mIdentifyBt.setEnabled(true);
			mNextBt.setEnabled(true);
		} else {
			mIdentifyBt.setEnabled(false);
			mNextBt.setEnabled(false);
		}
	}

	@Override
	public void afterTextChanged(Editable editable) {

	}

	@Override
	protected void initBundle() {

	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View view) {
		InputMethodUtils.hide(FindBackPwdActivity.this, view);
		switch (view.getId()) {
		case R.id.left_bt:
			finish();
			break;
		case R.id.identify_bt:
			String number = mPhoneEdit.getText().toString().trim();
			if (TextUtils.isEmpty(number)) {
				ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.phone_null_err));
				return;
			}
			if (!number.matches(RexUtils.PHOEN_REX)) {
				ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.phone_rex_error));
				return;
			}
			requestIdentify(number);

			break;
		case R.id.next:
			number = mPhoneEdit.getText().toString().trim();
			String identify = mIdentifyEdit.getText().toString().trim();
			String password = mPasswordEdit.getText().toString().trim();
			// skipNext();／
			if (TextUtils.isEmpty(number)) {
				ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.phone_null_err));
				return;
			}
			if (!number.matches(RexUtils.PHOEN_REX)) {
				ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.phone_rex_error));
				return;
			}
			if (TextUtils.isEmpty(identify)) {
				ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.identify_code_null_err));
				return;
			}
			if (TextUtils.isEmpty(password)) {
				ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.pwd_no_error));
				return;
			}
			if (!password.matches(RexUtils.PWD_REX)) {
				ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.pwd_rex_error));
				// return;
			}
			try {
				password = SecureUtils.aesEncryptToBytes(password, "shizhongshizhong");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			identyIdentifyCode(identify, password, number);

			break;

		}
	}

	/**
	 * 请求验证码
	 *
	 * @param number
	 */
	private void requestIdentify(String number) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", number);
		params.put("type", "1");// 0:注册 1:找回密码 2:绑定手机号
		showLoadingDialog();
		mIdentifyBt.setEnabled(false);
		mIdentifyBt.setClickable(false);
		mTimeHandler.sendEmptyMessage(ACTION_START_TIME);
		LogUtils.e("获取验证码", "-------");
		BaseHttpNetMananger.getInstance(FindBackPwdActivity.this).postJSON(FindBackPwdActivity.this, getVerifyCodeTag,
				params, new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						try {
							JSONObject root = new JSONObject(req);
							int code = root.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								ToastUtils.showShort(FindBackPwdActivity.this, "请求验证码成功，稍后以短信形式发送到手机上");
								break;
							case 900001:
								msg = root.getString("msg");
								break;
							default:
								ToastUtils.showErrorToast(FindBackPwdActivity.this, code, msg, true);
								mTimeHandler.sendEmptyMessage(ACTION_START_FINISH);
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.net_conected_error));
					}
				}, false);

	}

	/**
	 * 验证验证码是否正确
	 *
	 * @param identifyCode
	 * @param password
	 * @param number
	 */
	private void identyIdentifyCode(final String identifyCode, final String password, final String number) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", number);
		params.put("password", password);
		params.put("verifyCode", identifyCode);
		showLoadingDialog();
		LogUtils.e("修改用户密码", "-------");
		BaseHttpNetMananger.getInstance(FindBackPwdActivity.this).postJSON(FindBackPwdActivity.this, getFindBackPWDTag,
				params, new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						int code = 0;
						String msg = "";
						try {
							JSONObject rootObj = new JSONObject(req);
							code = rootObj.getInt("code");
							if (code != 100001) {
								if (code == 900001) {
									msg = rootObj.getString("msg");
								}
								ToastUtils.showErrorToast(FindBackPwdActivity.this, code, msg, true);
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
						loginRequest(number,password);
					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.net_conected_error));
					}
				}, false);

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
		LogUtils.e("手机登录", "-------");

		BaseHttpNetMananger.getInstance(FindBackPwdActivity.this).postJSON(FindBackPwdActivity.this, getPhoneLoginTag, params,
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
								ToastUtils.showErrorToast(FindBackPwdActivity.this, code, msg, true);
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}

						PrefUtils.putString(FindBackPwdActivity.this, ContantsActivity.LoginModle.PHONE, phone);
						PrefUtils.putString(FindBackPwdActivity.this, ContantsActivity.LoginModle.PWD, pwd);
						PrefUtils.putInt(FindBackPwdActivity.this, ContantsActivity.LoginModle.LOGIN_PLATFORM, LoginActivity.ACTION_LOGIN_STYLE_PHONE);
						UserBeanDataPackage root = GsonUtils.json2Bean(req, UserBeanDataPackage.class);
						UserBean bean = root.data;
						String loginToken = bean.token;
						String memberId = bean.memberId;
						BaseHXOptionManager.login_Huanxin(FindBackPwdActivity.this, memberId);
						PrefUtils.putString(FindBackPwdActivity.this, ContantsActivity.LoginModle.LOGIN_TOKEN, loginToken);
						PrefUtils.putString(FindBackPwdActivity.this, ContantsActivity.LoginModle.LOGIN_USER_ID, memberId);
						setResult(ContantsActivity.Action.RESULT_ACTION_FIND_BACK_SUCCESS);
						onBackPressed();
					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(FindBackPwdActivity.this, getString(R.string.net_conected_error));
					}
				}, false);

	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (BaseHttpNetMananger.getRequstQueue() != null) {
			BaseHttpNetMananger.getRequstQueue().cancelAll(getFindBackPWDTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getVerifyCodeTag);
			BaseHttpNetMananger.getRequstQueue().cancelAll(getPhoneLoginTag);
		}
		if (mTimeHandler != null) {
			mTimeHandler.removeMessages(ACTION_START_TIME);
			mTimeHandler.removeMessages(ACTIION_START_RUNNING);
			mTimeHandler.removeMessages(ACTION_START_FINISH);
			mTimeHandler = null;
		}
		System.gc();
	}

}
