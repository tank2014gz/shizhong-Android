package com.shizhong.view.ui.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateReceiver extends BroadcastReceiver {
	public final static String ACTION_PROCRESS = "com.shizhong.view.ui.intent.action.ACTION_PROCRESS";
	public final static String PARAM_IN = "apkinfo";

	@Override
	public void onReceive(Context context, Intent intent) {
		//获取升级APK的详细信息
        UpdateApkInfo apkInfo = (UpdateApkInfo)intent.getExtras().getSerializable(PARAM_IN);
        //启动升级的异步进程
        UpdateAsyncTask asyncTask = new UpdateAsyncTask(context,apkInfo);
        asyncTask.execute(10);

	}

}
