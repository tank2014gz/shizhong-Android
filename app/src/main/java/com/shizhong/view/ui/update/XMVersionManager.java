package com.shizhong.view.ui.update;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.xiaomi.market.sdk.UpdateResponse;
import com.xiaomi.market.sdk.UpdateStatus;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;
import com.xiaomi.market.sdk.XiaomiUpdateListener;

import android.app.Dialog;
import android.content.Context;

public class XMVersionManager implements UpdateManager {

	@Override
	public void update(final Context context) {

		long currentTime = System.currentTimeMillis();
		long lastTime = PrefUtils.getLong(context, ContantsActivity.LoginModle.LAST_CHECKED_VERSION_DATE, 0);
		if (currentTime - lastTime > 172800000) {
			XiaomiUpdateAgent.setCheckUpdateOnlyWifi(true);
			XiaomiUpdateAgent.setUpdateAutoPopup(false);
			XiaomiUpdateAgent.setUpdateListener(new XiaomiUpdateListener() {
				private Dialog uploadDialog;

				@Override
				public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
					switch (updateStatus) {
					case UpdateStatus.STATUS_UPDATE:
						// 有更新， UpdateResponse为本次更新的详细信息
						// 其中包含更新信息，下载地址，MD5校验信息等，可自行处理下载安装
						// 如果希望 SDK继续接管下载安装事宜，可调用
						uploadDialog = DialogUtils.versionUpdateDialog(context, "最新版本－" + updateInfo.versionName,
								updateInfo.updateLog, "确定升级", "忽略版本", new ConfirmDialog() {

									@Override
									public void onOKClick(Dialog dialog) {
										// TODO Auto-generated method stub
										XiaomiUpdateAgent.arrange();
									}

									@Override
									public void onCancleClick(Dialog dialog) {
										// TODO Auto-generated method stub
										dialog.dismiss();
										LogUtils.e("Version time", System.currentTimeMillis() + "");
										PrefUtils.putLong(context,
												ContantsActivity.LoginModle.LAST_CHECKED_VERSION_DATE,
												System.currentTimeMillis());
									}

								});
						uploadDialog.show();
						// XiaomiUpdateAgent.arrange()
						break;
					case UpdateStatus.STATUS_NO_UPDATE:
						// 无更新， UpdateResponse为null
						break;
					case UpdateStatus.STATUS_NO_WIFI:
						// 设置了只在WiFi下更新，且WiFi不可用时， UpdateResponse为null
						break;
					case UpdateStatus.STATUS_NO_NET:
						// 没有网络， UpdateResponse为null
						break;
					case UpdateStatus.STATUS_FAILED:
						// 检查更新与服务器通讯失败，可稍后再试， UpdateResponse为null
						break;
					case UpdateStatus.STATUS_LOCAL_APP_FAILED:
						// 检查更新获取本地安装应用信息失败， UpdateResponse为null
						break;
					default:
						break;
					}
				}
			});
			XiaomiUpdateAgent.update(context);
		}
	}
}
