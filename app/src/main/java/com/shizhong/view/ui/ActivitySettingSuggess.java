package com.shizhong.view.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.view.ClearEditText;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class ActivitySettingSuggess extends BaseFragmentActivity implements OnClickListener {

	private EditText mSuggessContent;
	private ClearEditText mPhoneNumEdit;
	private TextView mCommiteBtn;
	private String login_token;

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_setting_suggess_layout);
		findViewById(R.id.left_bt).setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText("意见反馈");
		mSuggessContent = (EditText) findViewById(R.id.suggess_edit);
		mPhoneNumEdit = (ClearEditText) findViewById(R.id.content_phone);
		mCommiteBtn = (TextView) findViewById(R.id.commit_btn);
		mCommiteBtn.setOnClickListener(this);
	}

	@Override
	protected void initBundle() {
		// TODO Auto-generated method stub
		login_token = PrefUtils.getString(ActivitySettingSuggess.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_bt:
			finish();
			break;
		case R.id.commit_btn:
			String content = mSuggessContent.getText().toString().trim();
			if (TextUtils.isEmpty(content)) {
				ToastUtils.showShort(ActivitySettingSuggess.this, "内容不能为空");
				return;
			}
			String phone = mPhoneNumEdit.getText().toString();
			if (TextUtils.isEmpty(phone)) {
				ToastUtils.showShort(ActivitySettingSuggess.this, "联系方式不能为空");
			}
			commitSuggess(content, phone);
			break;
		default:
			break;
		}

	}

	private void commitSuggess(String content, String phone) {
		String rootURL = "/member/memberFeedback";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("comment", content);
		params.put("contactInfo", phone);
		showLoadingDialog();
		LogUtils.e("提交用户建议", "-------" );
		BaseHttpNetMananger.getInstance(ActivitySettingSuggess.this).postJSON(ActivitySettingSuggess.this, rootURL,
				params, new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						dismissLoadingDialog();
						try {
							JSONObject rootObj = new JSONObject(req);
							int code = rootObj.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								ToastUtils.showShort(ActivitySettingSuggess.this, "提交成功");
								finish();
								break;
							case 900001:
								msg = rootObj.getString("msg");
							default:
								ToastUtils.showErrorToast(ActivitySettingSuggess.this, code, msg, true);
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void requestNetExeption() {
						dismissLoadingDialog();
						ToastUtils.showShort(ActivitySettingSuggess.this, getString(R.string.net_error));
					}

					@Override
					public void requestFail() {
						dismissLoadingDialog();
						ToastUtils.showShort(ActivitySettingSuggess.this, getString(R.string.net_conected_error));
					}
				}, false);
	}

}
