package com.shizhong.view.ui;

import com.shizhong.view.ui.base.BaseUmengActivity;
import com.umeng.analytics.AnalyticsConfig;
import com.shizhong.view.ui.base.utils.LogUtils;
import android.view.Window;
import android.view.WindowManager;

public class Flash_Activity extends BaseUmengActivity {

	private void skipNext() {
		LogUtils.e("flash_activity","skip next ActivityWelcome");
		mIntent.setClass(Flash_Activity.this, ActivityWelcom.class);
		startActivity(mIntent);
		finish();
		overridePendingTransition(0, 0);
	}

	@Override
	protected void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		mIntent.setClass(Flash_Activity.this, .class);
//		startService(mIntent);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// AnalyticsConfig.setChannel(ChannelManager.channels[7]);
		// AnalyticsConfig.enableEncrypt(false);
		AnalyticsConfig.sEncrypt = false;
		setContentView(R.layout.activity_flash_);



	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMainHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				skipNext();
			}
		}, 500);
	}

	@Override
	protected void initBundle() {

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

}
