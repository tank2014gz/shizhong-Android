package com.shizhong.view.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.MessageSetting;
import com.shizhong.view.ui.base.utils.ToastUtils;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by yuliyan on 16/1/29.
 */
public class ActivitySettingMessage extends BaseFragmentActivity implements OnClickListener {
	private View mSettingNewFriends;
	private View mSettingComment;
	private View mSettingPraise;

	private ToggleButton mSwitchNewFriends;
	private ToggleButton mSwitchComment;
	private ToggleButton mSwitchPraise;

	private MessageSetting mMessageSetting;
	private boolean isChangeFans = false;
	private boolean isChangeComment = false;
	private boolean isChangeLike = false;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_setting_message_layout);
		findViewById(R.id.left_bt).setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText("消息设置");
		mSettingNewFriends = findViewById(R.id.fl_setting_new_friends);
		mSettingNewFriends.setOnClickListener(this);
		mSettingComment = findViewById(R.id.fl_setting_comment);
		mSettingComment.setOnClickListener(this);
		mSettingPraise = findViewById(R.id.fl_setting_praise);
		mSettingPraise.setOnClickListener(this);

		mSwitchNewFriends = (ToggleButton) findViewById(R.id.iv_switch_newfriends);
		mSwitchComment = (ToggleButton) findViewById(R.id.iv_switch_comment);
		mSwitchPraise = (ToggleButton) findViewById(R.id.iv_switch_praise);
		mMessageSetting = new  MessageSetting(ActivitySettingMessage.this);

		mSwitchNewFriends.setChecked(mMessageSetting.isMessageNewFans());
		mSwitchComment.setChecked(mMessageSetting.isMessageComment());
		mSwitchPraise.setChecked(mMessageSetting.isMessageLike());

		getMessageSetting();

	}

	private String login_token;

	@Override
	protected void initBundle() {
		login_token = PrefUtils.getString(ActivitySettingMessage.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
	}

	@Override
	protected void initData() {
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.left_bt:
			if (isChangeFans || isChangeComment || isChangeLike) {
				setMessageAccept(mSwitchNewFriends.isChecked(), mSwitchComment.isChecked(), mSwitchPraise.isChecked());
			} else {
				finish();
			}
			break;
		case R.id.fl_setting_new_friends:
			boolean isChecked = mSwitchNewFriends.isChecked();
			mSwitchNewFriends.setChecked(!isChecked);
			isChangeFans = !isChangeFans;
			break;
		case R.id.fl_setting_comment:
			isChecked = mSwitchComment.isChecked();
			mSwitchComment.setChecked(!isChecked);
			isChangeComment = !isChangeComment;
			break;
		case R.id.fl_setting_praise:
			isChecked = mSwitchPraise.isChecked();
			mSwitchPraise.setChecked(!isChecked);
			isChangeLike = !isChangeLike;
			break;

		default:
			break;
		}
	}

	private void getMessageSetting() {
		String rootUrl = "/member/getSetting";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		LogUtils.e("获取用户设置", "-------" );
		showLoadingDialog();
		BaseHttpNetMananger.getInstance(ActivitySettingMessage.this).postJSON(ActivitySettingMessage.this, rootUrl,
				params, new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						try {
							dismissLoadingDialog();
							JSONObject root = new JSONObject(req);
							int code = root.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								// {"data":{"commentRemind":"0","fansRemind":"1","likeRemind":"1","memberId":"ab39b23db7354659b98daa5438398853"},"code":100001}
								JSONObject data = root.getJSONObject("data");
								String commentRemind = data.getString("commentRemind");
								String fansRemind = data.getString("fansRemind");
								String likeRemind = data.getString("likeRemind");
								mMessageSetting.setMessageComment(commentRemind.equals("1"));
								mMessageSetting.setMessageNewFans(fansRemind.equals("1"));
								mMessageSetting.setMessageLike(likeRemind.equals("1"));
								mSwitchNewFriends.setChecked(fansRemind.equals("1"));
								mSwitchComment.setChecked(commentRemind.equals("1"));
								mSwitchPraise.setChecked(likeRemind.equals("1"));

								break;
							case 900001:
								msg = root.getString("msg");
							default:
								ToastUtils.showErrorToast(ActivitySettingMessage.this, code, msg, true);
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(ActivitySettingMessage.this, getString(R.string.net_conected_error));
						dismissLoadingDialog();
					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(ActivitySettingMessage.this, getString(R.string.net_error));
						dismissLoadingDialog();
					}
				}, false);
	}

	private void setMessageAccept(boolean isFans, boolean isComment, boolean isLike) {
		String rootUrl = "/member/setting";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("fansRemind", isFans ? "1" : "0");
		params.put("commentRemind", isComment ? "1" : "0");
		params.put("likeRemind", isLike ? "1" : "0");
		showLoadingDialog();
		LogUtils.e("修改用户设置", "-------" );
		BaseHttpNetMananger.getInstance(ActivitySettingMessage.this).postJSON(ActivitySettingMessage.this, rootUrl,
				params, new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						try {
							dismissLoadingDialog();
							JSONObject root = new JSONObject(req);
							int code = root.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:
								// finish();
								ToastUtils.showShort(ActivitySettingMessage.this, "设置成功");
								finish();
								break;
							case 900001:
								msg = root.getString("msg");
							default:
								ToastUtils.showErrorToast(ActivitySettingMessage.this, code, msg, true);
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(ActivitySettingMessage.this, getString(R.string.net_conected_error));
						dismissLoadingDialog();
					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(ActivitySettingMessage.this, getString(R.string.net_error));
						dismissLoadingDialog();
					}
				}, false);
	}

}
