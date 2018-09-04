package com.shizhong.view.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import cn.jpush.android.api.TagAliasCallback;

import java.util.HashSet;
import java.util.Set;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.shizhong.view.ui.jpush.JpushUtils;
import com.umeng.analytics.MobclickAgent;
import com.hyphenate.easeui.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.easemob.chat.EMChatManager;
import com.hyphenate.easeui.ContantsActivity;

/**
 * Created by yuliyan on 16/1/29.
 */
public class ActivitySetting extends BaseFragmentActivity implements View.OnClickListener {
	private View mSeetingLetter;// 私信通知
	private View mSettingMessage;// 消息设置
	private View mSettingInviteFriends;
	private View mSettingHightOption;
	private View mSettingSuggess;
	private View mSettingAbout;
	private View mSettingClearCache;
	// private TextView mSettingCacheText;
	private TextView mQuitAPP;
	String name;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_setting);
		findViewById(R.id.left_bt).setOnClickListener(this);
		((TextView) findViewById(R.id.title_tv)).setText("设置");
		mSeetingLetter = findViewById(R.id.rl_setting_toast_letter);
		mSeetingLetter.setOnClickListener(this);
		mSettingMessage = findViewById(R.id.rl_setting_message_toast);
		mSettingMessage.setOnClickListener(this);
		mSettingInviteFriends = findViewById(R.id.rl_setting_invite_friends);
		mSettingInviteFriends.setOnClickListener(this);
		mSettingHightOption = findViewById(R.id.rl_setting_hight_opinion);
		mSettingHightOption.setOnClickListener(this);
		mSettingSuggess = findViewById(R.id.rl_setting_suggestion);
		mSettingSuggess.setOnClickListener(this);
		mSettingAbout = findViewById(R.id.rl_setting_about);
		mSettingAbout.setOnClickListener(this);
		mSettingClearCache = findViewById(R.id.rl_setting_clear_cache);
		mSettingClearCache.setOnClickListener(this);
		mQuitAPP = (TextView) findViewById(R.id.quite);
		mQuitAPP.setOnClickListener(this);
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
			finish();
			break;
		case R.id.rl_setting_toast_letter:
			mIntent.setClass(ActivitySetting.this, ActivitySettingLetterMessage.class);
			startActivityForResult(mIntent, -1);
			break;
		case R.id.rl_setting_message_toast:
			// mIntent.setClass(ActivitySetting.this,)
			mIntent.setClass(ActivitySetting.this, ActivitySettingMessage.class);
			startActivityForResult(mIntent, -1);
			break;
		case R.id.rl_setting_invite_friends:
			mIntent.setClass(ActivitySetting.this, ActivitySettingInviteFriends.class);
			startActivityForResult(mIntent, -1);
			break;
		case R.id.rl_setting_hight_opinion:
			break;
		case R.id.rl_setting_suggestion:
			mIntent.setClass(ActivitySetting.this, ActivitySettingSuggess.class);
			startActivityForResult(mIntent, -1);
			break;
		case R.id.rl_setting_about:
			mIntent.setClass(ActivitySetting.this, ActivitySettingAbout.class);
			startActivityForResult(mIntent, -1);
			break;
		case R.id.rl_setting_clear_cache:
			showClearCacheWindow();
			break;
		case R.id.quite:
			showExitDialog();
			break;

		}
	}

	private double cacheSize = 0;
	private final int CALCULATE_CACHESIZE = 0x1001; // 缓存tag值
	private final int CLEAR_CACHE_SUCCESS = 0x1003;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CALCULATE_CACHESIZE:
				cacheSize = (Double) msg.obj;
				if (cacheSize <= 0) {
					// mSettingCacheText.setText("清除缓存");
				} else {
					// mSettingCacheText.setText("清除缓存(" +
					// DataCleanManager.getFormatSize(cacheSize) + ")");
				}
				break;
			case CLEAR_CACHE_SUCCESS:
				// mSettingCacheText.setText("清除缓存");
				dismissLoadingDialog();
				break;
			default:
				break;
			}

		};
	};

	/**
	 * 清除缓存对话框
	 */
	private void showClearCacheWindow() {
		Dialog dialog = DialogUtils.confirmDialog(ActivitySetting.this, "您确定要清除缓存数据吗？", "确定", "取消",
				new ConfirmDialog() {

					@Override
					public void onOKClick(Dialog dialog) {
						showLoadingDialog();

						Thread clearCacheThread = new Thread(new Runnable() {
							@Override
							public void run() {
								Glide.get(ActivitySetting.this).clearDiskCache();
								mHandler.sendEmptyMessageDelayed(CLEAR_CACHE_SUCCESS, 200);
								ImagePipeline imagePipeline = Fresco.getImagePipeline();
								imagePipeline.clearMemoryCaches();
								imagePipeline.clearDiskCaches();

// combines above two lines
								imagePipeline.clearCaches();
							}
						});
						clearCacheThread.start();
						Glide.get(ActivitySetting.this).clearMemory();
					}

					@Override
					public void onCancleClick(Dialog dialog) {

					}
				});

		dialog.show();
	}

	/**
	 * 退出应用提示对话框
	 */
	private void showExitDialog() {
		Dialog exitDialog = DialogUtils.confirmDialog(ActivitySetting.this, "您确定要退出应用吗", "确定", "取消",
				new ConfirmDialog() {

					@Override
					public void onOKClick(Dialog dialog) {
						onClickExit();
						dialog.dismiss();

					}

					@Override
					public void onCancleClick(Dialog dialog) {
						dialog.dismiss();

					}
				});
		exitDialog.show();
	}

	private void onClickExit() {
		new Thread() {
			public void run() {
				JpushUtils.getInstance(ActivitySetting.this).setAliasAndTags(ActivitySetting.this, "",
						new HashSet<String>(), new TagAliasCallback() {
							@Override
							public void gotResult(int arg0, String arg1, Set<String> arg2) {
								LogUtils.i("jpush", "arg0:{" + arg0 + "},arg1:{" + arg1 + "},");
							}

						});
				EMChatManager.getInstance().logout(true);
				MobclickAgent.onProfileSignOff();
				PrefUtils.remove(ActivitySetting.this,ContantsActivity.GeTui.CLIENT_ID);
				MobclickAgent.onKillProcess(ActivitySetting.this);
			};
		}.start();
		PrefUtils.putBoolean(ActivitySetting.this, ContantsActivity.LoginModle.IS_LOGIN, false);
		setResult(ContantsActivity.Action.RESULT_LOGOUT);
		onBackPressed();

	}

}
