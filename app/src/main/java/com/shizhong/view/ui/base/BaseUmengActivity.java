package com.shizhong.view.ui.base;

import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.shizhong.view.ui.base.view.ErrorDialog;
import com.shizhong.view.ui.base.view.LoadingDialog;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import cn.jpush.android.api.JPushInterface;

public abstract class BaseUmengActivity extends EaseBaseActivity{
	public Intent mIntent = new Intent();
	protected Handler mMainHandler = new Handler();
	public static final boolean _IS_DEBUG = SZApplication.IS_DEBUG;
	protected LoadingDialog mProgressDialog;
	private ErrorDialog mErrorDialog;
	public static boolean isFinishApp;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		getWindow().setBackgroundDrawable(null);
		initBundle();
		initView();
		initData();
	}

	public void showLoadingDialog() {
		showProgress("正在处理中...", 0);
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

	public void changeProgress(final int progress) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.setMessage("当前进度" + progress + "%");
		}
	}

	protected void dismissLoadingDialog() {
		if (mMainHandler != null) {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mProgressDialog != null && mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
				}
			});
		}
	}

	protected void showErrorDialog(final String message) {
		if (mMainHandler != null) {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mErrorDialog == null) {
						mErrorDialog = new ErrorDialog(BaseUmengActivity.this);
					}
					if (!mErrorDialog.isShowing()) {
						mErrorDialog.show();
					}
					mErrorDialog.setMessage(message);
				}
			});
		}
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

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		JPushInterface.onResume(this);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mMainHandler != null) {
			mMainHandler = null;
		}
		if (mProgressDialog != null) {
			mProgressDialog = null;
		}
		if (mErrorDialog != null) {
			mErrorDialog = null;
		}

		System.gc();
	}
}
