package com.shizhong.view.ui.base;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.hyphenate.chatuidemo.DemoHelper;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.MD5;

import android.content.Context;

public class BaseHXOptionManager {

	public static void reg_HuanXin(final Context context, final String memberId) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				String hx_pwd = MD5.md5(memberId + memberId).toUpperCase();
				try {
					EMChatManager.getInstance().createAccountOnServer(memberId, hx_pwd);
					DemoHelper.getInstance().setCurrentUserName(memberId);
					login_Huanxin(context, memberId);
				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					String errorInfo = e.getMessage();
					LogUtils.e("huanxin error", errorInfo);
				}

			}
		});
		t.start();

	}

	public static void login_Huanxin(Context context, final String memberId) {
		new Thread() {
			public void run() {
				String hx_pwd = MD5.md5(memberId + memberId).toUpperCase();
				EMChatManager.getInstance().login(memberId, hx_pwd, new EMCallBack() {
					@Override
					public void onError(int arg0, String arg1) {
						LogUtils.e("huanxin error", arg1);
					}

					@Override
					public void onProgress(int arg0, String arg1) {
						LogUtils.e("huanxin onProgress", "arg0:" + arg0 + ",arg1" + arg1);
					}

					@Override
					public void onSuccess() {
						LogUtils.e("huanxin onSuccess", "---------------");
						DemoHelper.getInstance().setCurrentUserName(memberId);
						DemoHelper.getInstance().registerGroupAndContactListener();
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
					}
				});
			};

		}.start();

	}

}
