package com.shizhong.view.ui.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import cn.jpush.android.api.JPushInterface;

import com.shizhong.view.ui.base.view.LoadingDialog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;

/**
 * Created by yuliyan on 16/1/14.
 */
public abstract class BaseMediaActivity extends Activity {

	protected LoadingDialog mProgressDialog;
	protected Intent mIntent = new Intent();
	private PowerManager.WakeLock mWakeLock;

	public LoadingDialog showProgress(String message) {
		return showProgress(message, -1);
	}

	public LoadingDialog showProgress(String message, int theme) {
		if (mProgressDialog == null) {
			mProgressDialog = new LoadingDialog(this, message);
		}

		mProgressDialog.setMessage(message);
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
		return mProgressDialog;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		getWindow().setBackgroundDrawable(null);
		MobclickAgent.setScenarioType(BaseMediaActivity.this, EScenarioType.E_UM_NORMAL);
		initBundle();
		initView();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
		initData();
	}

	/**
	 * 初始化布局
	 */
	protected abstract void initView();

	/**
	 * 初始化bundle
	 */
	protected abstract void initBundle();

	/**
	 * 初始化数据
	 */
	protected abstract void initData();

	public void hideProgress() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	public void setProgressMessage(String msg) {
		if (mProgressDialog == null || !mProgressDialog.isShowing()) {
			mProgressDialog = new LoadingDialog(this, msg);
			mProgressDialog.show();
		}
		mProgressDialog.setMessage(msg);
	}

	@Override
	protected void onStop() {
		super.onStop();
		hideProgress();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(this);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mWakeLock != null) {
			try {
				mWakeLock.release();
			} catch (Exception e) {

			}
		}
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (mWakeLock != null) {
			try {
				mWakeLock.acquire();
			} catch (Exception e) {

			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mWakeLock != null) {
			mWakeLock = null;
		}
		if (mIntent != null) {
			mIntent = null;
		}
		if (mProgressDialog != null) {
			mProgressDialog = null;
		}
		System.gc();

	}

}
