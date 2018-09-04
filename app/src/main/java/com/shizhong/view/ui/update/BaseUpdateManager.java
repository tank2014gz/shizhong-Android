package com.shizhong.view.ui.update;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 
 * @author yuliyan <!-- 广播 -->
 *         <receiver android:name="com.peacemap.sl.jyg.receiver.UpdateReceiver">
 *         <intent-filter> <!--action的name值一定要和UpdateReceiver中的ACTION_PROCRESS一致
 *         --> <action android:name=
 *         "com.shizhong.view.ui.intent.action.ACTION_PROCRESS" />
 *         </intent-filter> </receiver>
 */
public class BaseUpdateManager implements UpdateManager {
	@Override
	public void update(Context context) {
		IntentFilter intentFilter = new IntentFilter(UpdateReceiver.ACTION_PROCRESS);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT); // 添加一个Category属性，CheckUpdateAsyncTask发送广播时候也要添加该属性。保持遥相呼应
		UpdateReceiver receiver = new UpdateReceiver();
		context.registerReceiver(receiver, intentFilter);
		// 启动后台异步执行检查更新
		CheckUpdateAsyncTask checkAsyncTask = new CheckUpdateAsyncTask(context);
		checkAsyncTask.execute(10);
	}

}
