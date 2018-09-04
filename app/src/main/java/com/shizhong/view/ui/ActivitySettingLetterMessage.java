package com.shizhong.view.ui;

import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.DemoModel;
import com.shizhong.view.ui.base.BaseFragmentActivity;

/**
 * Created by yuliyan on 16/1/29.
 */
public class ActivitySettingLetterMessage extends BaseFragmentActivity implements View.OnClickListener {

	private ToggleButton mSettingNotification;
	private ToggleButton mSettingSound;
	private ToggleButton mSettingVibrat;
	/**
	 * 设置新消息通知布局
	 */
	private View rl_switch_notification;
	/**
	 * 设置声音布局
	 */
	private View rl_switch_sound;
	/**
	 * 设置震动布局
	 */
	private View rl_switch_vibrate;

	private DemoModel settingsModel;
	TextView text;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_setting_letter_toast);
		((TextView) findViewById(R.id.title_tv)).setText("私信设置");
		findViewById(R.id.left_bt).setOnClickListener(this);
		rl_switch_notification = findViewById(R.id.rl_switch_notification);
		rl_switch_sound = findViewById(R.id.rl_switch_sound);
		rl_switch_vibrate = findViewById(R.id.rl_switch_vibrate);
		mSettingNotification = (ToggleButton) findViewById(R.id.iv_switch_notification);
		mSettingSound = (ToggleButton) findViewById(R.id.iv_switch_sound);
		mSettingVibrat = (ToggleButton) findViewById(R.id.iv_switch_vibrate);
		rl_switch_notification.setOnClickListener(this);
		rl_switch_sound.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);

		settingsModel = DemoHelper.getInstance().getModel();
		mSettingNotification.setChecked(settingsModel.getSettingMsgNotification());
		mSettingSound.setChecked(settingsModel.getSettingMsgSound());
		mSettingVibrat.setChecked(settingsModel.getSettingMsgVibrate());
	}

	@Override
	protected void initBundle() {

	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.left_bt:
			onBackPressed();
			break;
		case R.id.rl_switch_notification:
			boolean isNotification = mSettingNotification.isChecked();

			mSettingNotification.setChecked(!isNotification);
			settingsModel.setSettingMsgNotification(!isNotification);
			break;
		case R.id.rl_switch_sound:

			boolean isSound = mSettingSound.isChecked();

			mSettingSound.setChecked(!isSound);
			settingsModel.setSettingMsgSound(!isSound);
			break;
		case R.id.rl_switch_vibrate:
			boolean isVibrate = mSettingVibrat.isChecked();
			mSettingVibrat.setChecked(!isVibrate);
			settingsModel.setSettingMsgVibrate(!isVibrate);
			break;
		}
	}

}
