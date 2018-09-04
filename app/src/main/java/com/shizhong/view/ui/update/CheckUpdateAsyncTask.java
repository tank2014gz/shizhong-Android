package com.shizhong.view.ui.update;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.utils.NetworkUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

public class CheckUpdateAsyncTask extends AsyncTask<Integer, Integer, String> {

	private Context mContext;
	private final static String NOTE = "亲，有最新的软件包，赶紧下载吧~";
	private final static String SETTING_UPDATE_APK_INFO = "setting_updateapkinfo";
	private final static String CHECK_DATE = "checkdate";
	private final static String UPDATE_DATE = "updatedate";
	private final static String APK_VERSION = "apkversion";
	private final static String APK_VERCODE = "apkvercode";

	private final String getAPKInfoUrl = "";
	private final int defaultMinUpdateDay = 0;

	private AlertDialog noticeDialog; // 提示弹出框
	private UpdateApkInfo apkInfo;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public CheckUpdateAsyncTask(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	protected String doInBackground(Integer... params) {
		String result = "";
		// 检查是否能够连接网络,根据日期判断是否需要进行更新
		if (checkTodayUpdate() && NetworkUtils.isNetworkConnected(mContext)) {
			getUpateApkInfo();
			if (apkInfo != null && checkApkVersion()) { // 检查版本号
				alreayCheckTodayUpdate(); // 设置今天已经检查过更新
				result = "success";
			} else {
				Log.i("---------检查应用更新-------------", "从服务器获取下载数据失败或者该版本code不需要升级");
				result = "fail";
			}
		} else {
			Log.i("---------检查应用更新-------------", "无法连接网络或者根据日期判断不需要更新软件");
			result = "fail";
		}
		return result;
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
		if ("success".equals(result)) {
			showNoticeDialog();
		}
		super.onPostExecute(result);
	}

	/**
	 * 弹出软件更新提示对话框
	 */
	private void showNoticeDialog() {
		Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新").setMessage(NOTE);
		builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction(UpdateReceiver.ACTION_PROCRESS);
				intent.addCategory(Intent.CATEGORY_DEFAULT); // 一定要添加这个属性，不然onReceive(Context,Intent)中的Context参数不等于mContext，并且报错
				intent.putExtra(UpdateReceiver.PARAM_IN, apkInfo);
				dialog.dismiss();
				mContext.sendBroadcast(intent);
			}
		});
		builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 获取升级APK详细信息
	 * {apkVersion:'1.10',apkVerCode:2,apkName:'1.1.apk',apkDownloadUrl:'http://
	 * localhost:8080/myapp/1.1.apk'}
	 * 
	 * @return
	 */
	private void getUpateApkInfo() {

		BaseHttpNetMananger.getInstance(mContext).postJSON(mContext, getAPKInfoUrl, new HashMap<String, String>(),
				new IRequestResult() {

					@Override
					public void requestSuccess(String req) {
						try {
							JSONObject obj = new JSONObject(req);
							String apkVersion = obj.getString("apkVersion");
							int apkVerCode = obj.getInt("apkVerCode");
							String apkName = obj.getString("apkName");
							String apkDownloadUrl = obj.getString("apkDownloadUrl");
							apkInfo = new UpdateApkInfo(apkVersion, apkName, apkDownloadUrl, apkVerCode);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void requestNetExeption() {
						// TODO Auto-generated method stub

					}

					@Override
					public void requestFail() {
						// TODO Auto-generated method stub

					}
				}, false);// .getnetworkInfo(mContext, Const.checkUpdateApk,
							// null);
		// updateApkJson = Escape.unescape(updateApkJson);

	}

	/**
	 * 根据日期检查是否需要进行软件升级
	 * 
	 * @throws Exception
	 */
	private boolean checkTodayUpdate() {
		SharedPreferences sharedPreference = mContext.getSharedPreferences(SETTING_UPDATE_APK_INFO, 0);
		String checkDate = sharedPreference.getString(CHECK_DATE, "");
		String updateDate = sharedPreference.getString(UPDATE_DATE, "");
		Log.i("-------------------checkDate------------", "检查时间：" + checkDate);
		Log.i("-------------------updateDate------------", "最近更新软件时间：" + updateDate);
		if ("".equals(checkDate) && "".equals(updateDate)) { // 刚安装的新版本，设置详细信息
			int verCode = 0;
			String versionName = "";
			try {
				verCode = mContext.getPackageManager().getPackageInfo("com.peacemap.sl.jyg", 0).versionCode;
				versionName = mContext.getPackageManager().getPackageInfo("com.peacemap.sl.jyg", 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			String dateStr = sdf.format(new Date());
			sharedPreference.edit().putString(CHECK_DATE, dateStr).putString(UPDATE_DATE, dateStr)
					.putString(APK_VERSION, versionName).putInt(APK_VERCODE, verCode).commit();
			return false;
		}
		try {
			// 判断defaultMinUpdateDay天内不检查升级
			if ((new Date().getTime() - sdf.parse(updateDate).getTime()) / 1000 / 3600 / 24 < defaultMinUpdateDay) {
				return false;
			} else if (checkDate.equalsIgnoreCase(sdf.format(new Date()))) {// 判断今天是否检查过升级
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 检查版本是否需要更新
	 * 
	 * @return
	 */
	private boolean checkApkVersion() {
		SharedPreferences sharedPreference = mContext.getSharedPreferences(SETTING_UPDATE_APK_INFO, 0);
		int verCode = sharedPreference.getInt(APK_VERCODE, 0);
		if (apkInfo.getAplVerCode() > verCode) { // 如果新版本Code大于系统更新后的Code，则升级
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置今天已经检查过升级
	 * 
	 * @return
	 */
	private void alreayCheckTodayUpdate() {
		String date = sdf.format(new Date());
		SharedPreferences sharedPreference = mContext.getSharedPreferences(SETTING_UPDATE_APK_INFO, 0);
		sharedPreference.edit().putString(CHECK_DATE, date).commit();
	}

}
