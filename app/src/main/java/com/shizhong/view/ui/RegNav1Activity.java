package com.shizhong.view.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

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
import com.shizhong.view.ui.bean.RegisterDataPackage;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 快速注册导航 1 Created by yuliyan on 15/12/23.
 */
public class RegNav1Activity extends BaseUmengActivity implements View.OnClickListener, TextWatcher {
	private static final String TAG = RegNav1Activity.class.getSimpleName();
	private ClearEditText mPhoneEdit;
	private ClearEditText mIdentifyEdit;
	private ClearEditText mPasswordEdit;
	private TextView mIdentifyBt;
	private TextView mNextBt;

	private final int ACTION_SKIP_NEXT=0x1021;

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
		((TextView) findViewById(R.id.title_tv)).setText(R.string.reg);
		findViewById(R.id.left_bt).setOnClickListener(this);
		findViewById(R.id.clear_userInfo).setOnClickListener(this);
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
		InputMethodUtils.hide(RegNav1Activity.this, view);
		switch (view.getId()) {
		case R.id.left_bt:
			onBackPressed();
			break;
		case R.id.identify_bt:
			String number = mPhoneEdit.getText().toString().trim();
			if (TextUtils.isEmpty(number)) {
				ToastUtils.showShort(RegNav1Activity.this, getString(R.string.phone_null_err));
				return;
			}
			if (!number.matches(RexUtils.PHOEN_REX)) {
				ToastUtils.showShort(RegNav1Activity.this, getString(R.string.phone_rex_error));
				return;
			}
			requestIdentify(number);

			break;
		case R.id.next:
			number = mPhoneEdit.getText().toString().trim();
			String identify = mIdentifyEdit.getText().toString().trim();
			String password = mPasswordEdit.getText().toString().trim();
			if (TextUtils.isEmpty(number)) {
				ToastUtils.showShort(RegNav1Activity.this, getString(R.string.phone_null_err));
				return;
			}
			if (!number.matches(RexUtils.PHOEN_REX)) {
				ToastUtils.showShort(RegNav1Activity.this, getString(R.string.phone_rex_error));
				return;
			}
			if (TextUtils.isEmpty(identify)) {
				ToastUtils.showShort(RegNav1Activity.this, getString(R.string.identify_code_null_err));
				return;
			}
			if (TextUtils.isEmpty(password)) {
				ToastUtils.showShort(RegNav1Activity.this, getString(R.string.pwd_no_error));
				return;
			}
			if (!password.matches(RexUtils.PWD_REX)) {
				ToastUtils.showShort(RegNav1Activity.this, getString(R.string.pwd_rex_error));
				return;
			}

			try {
				password = SecureUtils.aesEncryptToBytes(password, "shizhongshizhong");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			identyIdentifyCode(identify, password, number);

			break;
		case R.id.clear_userInfo:
			clearUserInfo();
			break;

		}
	}

	/**
	 * 请求验证码
	 *
	 * @param number
	 */
	private void requestIdentify(String number) {
		String baseUrl = "/member/sendVerifyCode";
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", number);
		params.put("type", "0");
		showLoadingDialog();
		mIdentifyBt.setEnabled(false);
		mIdentifyBt.setClickable(false);
		mTimeHandler.sendEmptyMessage(ACTION_START_TIME);
		LogUtils.e("获取验证码", "-------");
		BaseHttpNetMananger.getInstance(RegNav1Activity.this).postJSON(RegNav1Activity.this, baseUrl, params,
				new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						LogUtils.i(TAG, req);
						dismissLoadingDialog();
						try {
							JSONObject root = new JSONObject(req);
							int code = root.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								ToastUtils.showShort(RegNav1Activity.this, getString(R.string.ok_find_identify));
								break;
							case 900001:
								msg = root.getString("msg");
							default:
								ToastUtils.showErrorToast(RegNav1Activity.this, code, msg, true);
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
						ToastUtils.showShort(RegNav1Activity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(RegNav1Activity.this, getString(R.string.net_conected_error));
					}
				}, false);

	}

	/**
	 * 注册用户
	 *
	 * @param identifyCode
	 * @param password
	 * @param number
	 */
	private void identyIdentifyCode(final String identifyCode, final String password, final String number) {
		String baseUrl = "/member/reg";
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", number);
		params.put("password", password);
		params.put("verifyCode", identifyCode);
		params.put("regPlatform", ContantsActivity.LoginModle.REG_PLATFORM);
		params.put("deviceId",
				PrefUtils.getString(RegNav1Activity.this, ContantsActivity.LoginModle.DEVICES_ID, "Android"));
		showLoadingDialog();
		LogUtils.e("注册验证接口", "-------");
		BaseHttpNetMananger.getInstance(RegNav1Activity.this).postJSON(RegNav1Activity.this, baseUrl, params,
				new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						int code = 0;
						String msg = "";
						try {
							JSONObject rootObj = new JSONObject(req);
							code = rootObj.getInt("code");
							if (code != 100001) {
								if (code == 900001) {
									msg = rootObj.getString("msg");
								}
								ToastUtils.showErrorToast(RegNav1Activity.this, code, msg, true);
//								mIdentifyBt.setText(getString(R.string.identify_text));
//								mIdentifyBt.setEnabled(true);
//								mIdentifyBt.setClickable(true);
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
						PrefUtils.putString(RegNav1Activity.this, ContantsActivity.LoginModle.PHONE, number);
						PrefUtils.putString(RegNav1Activity.this, ContantsActivity.LoginModle.PWD, password);
						PrefUtils.putInt(RegNav1Activity.this, ContantsActivity.LoginModle.LOGIN_PLATFORM, LoginActivity.ACTION_LOGIN_STYLE_PHONE);
						RegisterDataPackage root = GsonUtils.json2Bean(req, RegisterDataPackage.class);
						RegisterDataPackage.RegisterData data = root.data;
						if (data != null) {
							String token = data.token;
							if (TextUtils.isEmpty(token)) {
								ToastUtils.showShort(RegNav1Activity.this, "服务器返回TOKEN NULL");
								return;
							}
							PrefUtils.putString(RegNav1Activity.this, ContantsActivity.LoginModle.LOGIN_TOKEN, token);
							String userId = data.memberId;
							PrefUtils.putString(RegNav1Activity.this, ContantsActivity.LoginModle.LOGIN_USER_ID,
									userId);
							BaseHXOptionManager.reg_HuanXin(RegNav1Activity.this, userId);
							skipNext();
						}

					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(RegNav1Activity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(RegNav1Activity.this, getString(R.string.net_conected_error));
					}
				}, false);

	}

	private void clearUserInfo() {
		String baseUrl = "/member/deleteAllMember";
		Map<String, String> params = new HashMap<String, String>();
		LogUtils.e("删除用户", "-------");
		BaseHttpNetMananger.getInstance(RegNav1Activity.this).postJSON(RegNav1Activity.this, baseUrl, params,
				new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						LogUtils.i(TAG, req);
						JSONObject root = null;
						try {
							root = new JSONObject(req);
							if (req != null) {
								int code = root.getInt("code");
								switch (code) {
								case 100001:
									ToastUtils.showShort(RegNav1Activity.this, getString(R.string.ok_clear_user));
									break;
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(RegNav1Activity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(RegNav1Activity.this, getString(R.string.net_conected_error));
					}
				}, false);

	}

	/**
	 * 验证通过之后跳转到完善个人资料页
	 */
	private void skipNext() {
		mIntent.setClass(RegNav1Activity.this, RegFinishUserInfoActivity.class);
		startActivityForResult(mIntent, ACTION_SKIP_NEXT);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode){
			case  ACTION_SKIP_NEXT:
				if(resultCode==ContantsActivity.Action.RESULT_ACTION_REG_SUCCESS){
					setResult(resultCode);
					onBackPressed();
				}

				break;
		}

	}
}
