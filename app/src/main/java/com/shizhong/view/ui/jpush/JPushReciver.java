package com.shizhong.view.ui.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.shizhong.view.ui.ActivityClubDetail;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.ActivityNewsWebContent;
import com.shizhong.view.ui.ActivityTopicDetail;
import com.shizhong.view.ui.MainActivity;
import com.shizhong.view.ui.VideoPlayerActivity;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.hyphenate.easeui.ContantsActivity;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */

public class JPushReciver extends BroadcastReceiver {

	private static final String TAG = "JPushRecever";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		// LogUtils.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ",
		// extras: " + printBundle(bundle));
		if (bundle != null) {

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				// SDK通过JPush Server注册所得到的注册，全局唯一的ID,可以通过此ID向对应的客户端发送消息和通知
				LogUtils.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				// send the Registration Id to your server...
				// 向自己的服务器发送 REGISTRATION_ID

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				processCustomMessage(context, bundle);// 将自定义的消息发送给activity通过广播

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				LogUtils.d(TAG, "[MyReceiver] 用户点击打开了通知");
				String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
				// {"content":"{\"id\":\"098978686823mlklsdada\"}","targetType":"3"}
				try {
					// {"content":"{\"id\":\"c1ea4ebd5d8f4ff4a2c62c76845e5b74\"}","targetType":"8"}
					JSONObject json = new JSONObject(extras);
					String targetType = json.getString("targetType");
					String content = json.getString("content");
					if (targetType.equals("3") || targetType.equals("0") || targetType.equals("1")
							|| targetType.equals("5")) {
						String id = null;
						if (!TextUtils.isEmpty(content)) {
							id = new JSONObject(content).getString("id");
						}
						intent.setClass(context, VideoPlayerActivity.class);
						intent.putExtra(ContantsActivity.Video.VIDEO_ID, id);
					} else if (targetType.equals("2") || targetType.equals("4")) {
						String id = null;
						if (!TextUtils.isEmpty(content)) {
							id = new JSONObject(content).getString("id");
						}
						intent.setClass(context, ActivityMemberInfor.class);
						intent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, id);
					} else if (targetType.equals("6")) {
						String id = null;
						if (!TextUtils.isEmpty(content)) {
							id = new JSONObject(content).getString("id");
						}
						intent.setClass(context, ActivityTopicDetail.class);
						intent.putExtra(ContantsActivity.Topic.TOPIC_ID, id);
					} else if (targetType.equals("7")) {
						String newsUrl = new JSONObject(content).getString("newsUrl");
						intent.setClass(context, ActivityNewsWebContent.class);
						intent.putExtra(ContantsActivity.JieQu.NEWS_URL, newsUrl);
					} else if (targetType.equals("8")) {
						String id = null;
						if (!TextUtils.isEmpty(content)) {
							id = new JSONObject(content).getString("id");
						}
						intent.setClass(context, ActivityClubDetail.class);
						intent.putExtra(ContantsActivity.Club.CLUB_ID, id);
					} else {
						intent.setClass(context, MainActivity.class);
					}
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				LogUtils.d(TAG,
						"[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));

			} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				LogUtils.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
			} else {
				LogUtils.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		}
	}

	private void processCustomMessage(Context context, Bundle bundle) {
		// if (MainActivity.isForeground) {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
		msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
		msgIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		if (!TextUtils.isEmpty(extras)) {
			try {
				JSONObject extraJson = new JSONObject(extras);
				if (null != extraJson && extraJson.length() > 0) {
					msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
				}
			} catch (JSONException e) {

			}

		}
		context.sendBroadcast(msgIntent);
		// }
	}

}
