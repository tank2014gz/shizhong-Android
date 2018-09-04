package com.shizhong.view.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.exceptions.EaseMobException;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.igexin.sdk.PushManager;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.hyphenate.chatuidemo.Constant;
import com.hyphenate.chatuidemo.DemoHelper;
import com.shizhong.view.ui.base.BaseFragmentActivity;
import com.shizhong.view.ui.base.db.MessageDBManager;
import com.shizhong.view.ui.base.db.VideoUploadTaskDBManager;
import com.shizhong.view.ui.base.net.BaseHttpNetMananger;
import com.shizhong.view.ui.base.net.IRequestResult;
import com.shizhong.view.ui.base.net.VideoHttpRequest;
import com.shizhong.view.ui.base.net.VideoHttpRequest.HttpRequestCallBack1;
import com.shizhong.view.ui.base.utils.ACache;
import com.shizhong.view.ui.base.utils.DeviceUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.EaseConstant;
import com.shizhong.view.ui.bean.MessageInfoExtraBean;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.shizhong.view.ui.fragment.Fragment_Home;
import com.shizhong.view.ui.fragment.Fragment_JieQu;
import com.shizhong.view.ui.fragment.Fragment_Me;
import com.shizhong.view.ui.fragment.Fragment_Message;
import com.shizhong.view.ui.getui.receiver.GetuiSdkHttpPost;
import com.shizhong.view.ui.map.ILoactionInfoCaller;
import com.shizhong.view.ui.map.SZLocationManager;
import com.shizhong.view.ui.map.SZLocationManagerFactory;
import com.shizhong.view.ui.update.UpdateManagerFactory;
import com.shizhong.view.ui.video.ActivityMediaVideoRecorder;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseFragmentActivity implements OnClickListener, EMEventListener {
	protected static final String TAG = "MainActivity";
	private Intent mIntent = new Intent();
	private Handler mMainHandler = new Handler();
	// 环信未读消息textview
	private TextView mMessageCountText;

	private ImageView mMessageTagView;
	// 是否是切换到了相机界面
	private boolean isSwitchingCamera;
	// 选中index
	private int index;
	// 当前所处的界面的index
	private int currentTabIndex;

	private TextView[] mTabs;
	private ImageView mFirstUseImageTag;

	private Fragment[] fragments;
	private Fragment_Home mFragment_Home;
	private Fragment_JieQu mFragment_JieQu;
	private Fragment_Message mFragment_Message;
	private Fragment_Me mFragment_Me;

	private ImageView mCameraImageView;
	// 极光推送有新消息
	private boolean isJupshHasNoReadMessage;
//	private MessageReceiver mMessageJPushReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.shizhong.ui.view.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	public static boolean isForeground = false;

	private String login_token;
	private String member_id;

	// 环信是否有未读消息
	// private LocalBroadcastManager broadcastManager;

	private boolean isConflictDialogShow;
	private android.app.AlertDialog.Builder conflictBuilder;
	// 账号在别处登录
	public boolean isConflict = false;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isAccountRemovedDialogShow;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;

	private boolean isFirstUser = true;
	private LayoutParams mCenetLayoutParams;
	private SZLocationManager mGeoMapManager;

	private ACache mACache;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mACache=ACache.get(MainActivity.this);
		String message=mACache.getAsString(ContantsActivity.Action.CACHE_GETUI_INFO_DATA);
		if(!TextUtils.isEmpty(message)) {
			LogUtils.e("shizhong-----------------------",message);
			getInfo(message, MainActivity.this, mIntent);
		}

		PushManager.getInstance().initialize(this.getApplicationContext());

		MiPushClient.clearNotification(this);
		// 使用小米的版本升级
		UpdateManagerFactory.createUpdateManager(UpdateManagerFactory.UPDATE_XIAOMI).update(MainActivity.this);

		if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			DemoHelper.getInstance().logout(true, null);
			startActivity(new Intent(this, LoginActivity.class));
			PrefUtils.remove(MainActivity.this,ContantsActivity.GeTui.CLIENT_ID);
			finish();
			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			startActivity(new Intent(this, LoginActivity.class));
			PrefUtils.remove(MainActivity.this,ContantsActivity.GeTui.CLIENT_ID);
			finish();
			return;
		}
		if (getIntent().getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
		DemoHelper.getInstance().registerGroupAndContactListener();
		// registerBroadcastReceiver();
		mGeoMapManager = SZLocationManagerFactory.getLoactionManager(MainActivity.this,
				SZLocationManagerFactory.TYPE_GAODE);
		mGeoMapManager.setLoacionInfoListener(new ILoactionInfoCaller() {

			@Override
			public void callback(String province, String city, String lng, String lat) {
				editUserLoaction(lng, lat);
				mGeoMapManager.stopLoaction();
			}
		});

	}





	@Override
	protected void onResume() {
		super.onResume();

		isForeground = true;

		if (!isConflict && !isCurrentAccountRemoved) {
			updateUnreadHXLabel();
		}

		// 注册消息监听
		DemoHelper.getInstance().pushActivity(this);
		EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged,
						EMNotifierEvent.Event.EventNewCMDMessage, EMNotifierEvent.Event.EventReadAck });
		if (!EMChatManager.getInstance().isConnected() && NetUtils.hasNetwork(this)) {
			EMChatManager.getInstance().reconnect();
		}
		checkHasNoReadMessage();

	}

	@Override
	protected void onPause() {
		super.onPause();
		isForeground = false;

	ImagePipeline imagePipeline = Fresco.getImagePipeline();
	//清空内存缓存（包括Bitmap缓存和未解码图片的缓存）
	imagePipeline.clearMemoryCaches();
}

	@Override
	protected void onStop() {
		// EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		EMChatManager.getInstance().unregisterEventListener(this);
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.popActivity(this);

		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		if (mMessageJPushReceiver != null) {
//			try {
//				unregisterReceiver(mMessageJPushReceiver);
//			} catch (Exception e) {
//
//			}
//			mMessageJPushReceiver = null;
//		}
		if(mGetuiMessageReceiver!=null){
			try {
				unregisterReceiver(mGetuiMessageReceiver);
			}catch (Exception e) {

			}
			mGetuiMessageReceiver=null;
		}
		if (mGeoMapManager != null) {
			mGeoMapManager.onDestory();
			mGeoMapManager=null;
		}
		System.gc();

	}

	@Override
	public void back(View view) {
		super.back(view);
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadHXLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			mMessageCountText.setText(String.valueOf(count));
			mMessageCountText.setVisibility(View.VISIBLE);
		} else {
			mMessageCountText.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
			if (conversation.getType() == EMConversationType.ChatRoom)
				chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal - chatroomUnreadMsgCount;
	}

	/**
	 * 视图初始化之前的参数设计
	 */
	@Override
	protected void initBundle() {

//		registeJPushReceiver();
		login_token = PrefUtils.getString(MainActivity.this, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		VideoHttpRequest.getMememberInfo("/member/getMember", MainActivity.this, login_token,
				PrefUtils.getString(MainActivity.this, ContantsActivity.LoginModle.LOGIN_USER_ID, ""), true,
				new HttpRequestCallBack1() {

					@Override
					public void callBack(UserExtendsInfo info) {
						member_id = PrefUtils.getString(MainActivity.this, ContantsActivity.LoginModle.LOGIN_USER_ID,
								"");
						MobclickAgent.onProfileSignIn(member_id);
						if(!TextUtils.isEmpty(PrefUtils.getString(MainActivity.this,ContantsActivity.LoginModle.NICK_NAME,""))){
							mGeoMapManager.startLoacation();
						}
						new Thread(new Runnable() {

							@Override
							public void run() {

								boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(PrefUtils
										.getString(MainActivity.this, ContantsActivity.LoginModle.NICK_NAME, ""));
								LogUtils.i("updatenick", updatenick + "");
								if (!updatenick) {
									LogUtils.e("LoginActivity", "update current user nick fail");
								}
							}
						}).start();

						// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
					}

					@Override
					public void callBackFail() {

						mMainHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mIntent.setClass(MainActivity.this, RegFinishUserInfoActivity.class);
								mIntent.putExtra(ContantsActivity.LoginModle.IS_USER_FINISH_FAIL,true);
								startActivity(mIntent);
								overridePendingTransition(R.anim.push_bottom_in, 0);
								onBackPressed();
							}
						}, 1000);

					}
				});
		registerGetuiReceiver();
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_main);
		MobclickAgent.enableEncrypt(true);
		if (getIntent().getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}

		mFragment_Home = new Fragment_Home();
		mFragment_JieQu = new Fragment_JieQu();
		mFragment_Message = new Fragment_Message();
		mFragment_Me = new Fragment_Me();
		fragments = new Fragment[] { mFragment_Home, mFragment_JieQu, mFragment_Message, mFragment_Me };
		getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_layout, mFragment_Home)
				.add(R.id.main_fragment_layout, mFragment_Message).hide(mFragment_Message).show(mFragment_Home).commit();

		mTabs = new TextView[4];
		mTabs[0] = (TextView) findViewById(R.id.tab_home);
		mTabs[0].setOnClickListener(this);
		mTabs[1] = (TextView) findViewById(R.id.tab_jiequ);
		mTabs[1].setOnClickListener(this);
		mTabs[2] = (TextView) findViewById(R.id.tab_message);
		mTabs[2].setOnClickListener(this);
		mTabs[3] = (TextView) findViewById(R.id.tab_me);
		mTabs[3].setOnClickListener(this);
		mCameraImageView = (ImageView) findViewById(R.id.tab_camera);
		mMessageCountText = (TextView) findViewById(R.id.message_count);
		mMessageTagView = (ImageView) findViewById(R.id.message_tag);
		mCameraImageView.setOnClickListener(this);
		isForeground = true;
		mTabs[0].setSelected(true);
		isFirstUser = PrefUtils.getBoolean(MainActivity.this, ContantsActivity.LoginModle.IS_FIRST_USER_IN_MAIN, true);
		if (index == 0 && isFirstUser) {
			PrefUtils.putBoolean(MainActivity.this, ContantsActivity.LoginModle.IS_FIRST_USER_IN_MAIN, false);
			mFirstUseImageTag = (ImageView) findViewById(R.id.take_photo_prompt);
			mFirstUseImageTag.setVisibility(View.VISIBLE);
			mFirstUseImageTag.setOnClickListener(this);
			mCenetLayoutParams = (LayoutParams) mFirstUseImageTag.getLayoutParams();
			mCenetLayoutParams.setMargins(UIUtils.dipToPx(MainActivity.this, 15), 0, 0,
					UIUtils.dipToPx(MainActivity.this, 48));
			mFirstUseImageTag.setLayoutParams(mCenetLayoutParams);
		}
	}

	private void refreshUIWithMessage() {
		runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				updateUnreadHXLabel();
				if (mFragment_Message != null) {
					mFragment_Message.onHiddenChanged(false);
				}
			}
		});
	}

	private void checkHasNoReadMessage() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				isJupshHasNoReadMessage = MessageDBManager.getInstance(MainActivity.this, member_id)
						.isHasNoReadMessage();
				refreshJPushMessageTable();
				refreshJPushMessageList();
			}
		});
		thread.start();
	}

	private void refreshJPushMessageTable() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				if (isJupshHasNoReadMessage) {
					mMessageTagView.setVisibility(View.VISIBLE);
				} else {
					mMessageTagView.setVisibility(View.GONE);
				}

			}
		});
	}

	private void refreshJPushMessageList() {
		if (mFragment_Message != null && isJupshHasNoReadMessage) {
			mFragment_Message.refreshHx();
		}
	}




	public   class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String messge = intent.getStringExtra(KEY_MESSAGE);
				String extras = intent.getStringExtra(KEY_EXTRAS);
				MessageInfoExtraBean info = new MessageInfoExtraBean();
				info.extras = extras;
				info.message = messge;
				try {
					JSONObject messageObject = new JSONObject(extras);
					info.description = messageObject.getString("description");
					info.isFollow = false;
					info.isRead = false;
					info.operateTime = messageObject.getString("operateTime");
					info.fromHeader = messageObject.getString("fromHeader");
					info.fromId = messageObject.getString("fromId");
					info.fromNickname = messageObject.getString("fromNickname");
					String targetType = messageObject.getString("targetType");
					info.targetType = targetType;
					info.type = MessageDBManager.getMessageType(targetType);
					info.toHeader = messageObject.getString("toHeader");
					info.toId = messageObject.getString("toId");
					info.toNickname = messageObject.getString("toNickname");
					info.content = messageObject.getString("content");
					MessageDBManager.getInstance(MainActivity.this, member_id).insertMessage(info);
					isJupshHasNoReadMessage = true;
					checkHasNoReadMessage();
				} catch (JSONException e) {
					e.printStackTrace();
				}
//				context.unregisterReceiver(this);
			}
		}
	}
//
//	private void registeJPushReceiver() {
//		mMessageJPushReceiver = new MessageReceiver();
//		IntentFilter filter = new IntentFilter();
//		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//		filter.addAction(MESSAGE_RECEIVED_ACTION);
//		registerReceiver(mMessageJPushReceiver, filter);
//
//	}

	private GetuiReceiver mGetuiMessageReceiver;

	private void registerGetuiReceiver(){
		mGetuiMessageReceiver=new GetuiReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(ContantsActivity.Action.ACTION_GET_GETUI_CLIENT_ID);
		registerReceiver(mGetuiMessageReceiver, filter);

	}
	public  class GetuiReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			String action=intent.getAction();
			LogUtils.e("shizhong","-------------------action==="+action);
			if(action.equals(ContantsActivity.Action.ACTION_GET_GETUI_CLIENT_ID)){
                if(intent.getBooleanExtra(ContantsActivity.Extra.IS_GET_GETUI_CLIENT_ID,false)){
					String clientId=intent.getStringExtra(ContantsActivity.Extra.EXTRA_GET_GETUI_CLIENT_ID);
					String token=PrefUtils.getString(MainActivity.this,ContantsActivity.LoginModle.LOGIN_TOKEN,"");
					LogUtils.e("shizhong","-------------------clientId==="+clientId);

					GetuiSdkHttpPost.postClientId(MainActivity.this,clientId,token);
				}else if(intent.getBooleanExtra(ContantsActivity.Extra.IS_GET_GETUI_MESSAGE,false)){
					String taskId=intent.getStringExtra(ContantsActivity.Extra.EXTRA_GET_GETUI_TASKID);
					String messageId=intent.getStringExtra(ContantsActivity.Extra.EXTRA_GET_GETUI_MESSAGEID);
					String message=intent.getStringExtra(ContantsActivity.Extra.EXTRA_GET_GETUI_MESSAGE);

					LogUtils.e("shizhong","taskid     "+taskId);
					LogUtils.e("shizhong","messageId     "+messageId);
					LogUtils.e("shizhong","message     "+message);
					getInfo( message,  context,  intent);
			}
		}
	}
	}

	private void  getInfo(String  message,Context context,Intent intent){
		MessageInfoExtraBean info = new MessageInfoExtraBean();
		mACache.remove(ContantsActivity.Action.CACHE_GETUI_INFO_DATA);
		try {
			JSONObject messageObject=new JSONObject(message);
			String  isManage=messageObject.getString("isManage");
			if(isManage.equals("1")) {
				isJupshHasNoReadMessage = true;
				checkHasNoReadMessage();

			}else{

				String targetType = messageObject.getString("targetType");
				String content = messageObject.getString("content");
				LogUtils.e("shizhong","targetype:  "+targetType);
				LogUtils.e("shizhong","content:    "+content);
				try {
					if (targetType.equals("3") || targetType.equals("0") || targetType.equals("1")
							|| targetType.equals("5")) {
						String id = null;
						if (!TextUtils.isEmpty(content)) {
							id = new JSONObject(content).getString("id");
						}
						intent.setClass(context, VideoPlayerActivity.class);
						intent.putExtra(ContantsActivity.Video.VIDEO_ID, id);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						MainActivity.this.startActivity(intent);

					} else if (targetType.equals("2") || targetType.equals("4")) {
						String id = null;
						if (!TextUtils.isEmpty(content)) {
							id = new JSONObject(content).getString("id");
						}
						intent.setClass(context, ActivityMemberInfor.class);
						intent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, id);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						MainActivity.this.startActivity(intent);
					} else if (targetType.equals("6")) {
						String id = null;
						if (!TextUtils.isEmpty(content)) {
							id = new JSONObject(content).getString("id");
						}
						intent.setClass(context, ActivityTopicDetail.class);
						intent.putExtra(ContantsActivity.Topic.TOPIC_ID, id);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						MainActivity.this.startActivity(intent);
					} else if (targetType.equals("7")) {
						String newsUrl = new JSONObject(content).getString("newsUrl");
						intent.setClass(context, ActivityNewsWebContent.class);
						intent.putExtra(ContantsActivity.JieQu.NEWS_URL, newsUrl);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						MainActivity.this.startActivity(intent);
					} else if (targetType.equals("8")) {
						String id = null;
						if (!TextUtils.isEmpty(content)) {
							id = new JSONObject(content).getString("id");
						}
						intent.setClass(context, ActivityClubDetail.class);
						intent.putExtra(ContantsActivity.Club.CLUB_ID, id);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						MainActivity.this.startActivity(intent);
					} else {
//								intent.setClass(context, MainActivity.class);
					}

				}catch (Exception e){

				}
			}



		} catch (JSONException e) {
			e.printStackTrace();

			LogUtils.e("shizhong error", e.toString());
		}


	}

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		DemoHelper.getInstance().logout(false, null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						PrefUtils.putBoolean(MainActivity.this, ContantsActivity.LoginModle.IS_LOGIN, false);
						Intent intent = new Intent(MainActivity.this, LoginActivity.class);
						PrefUtils.remove(MainActivity.this,ContantsActivity.GeTui.CLIENT_ID);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						finish();
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
			}

		}
	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		DemoHelper.getInstance().logout(false, null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						accountRemovedBuilder = null;
						PrefUtils.putBoolean(MainActivity.this, ContantsActivity.LoginModle.IS_LOGIN, false);
						startActivity(new Intent(MainActivity.this, LoginActivity.class));
						PrefUtils.remove(MainActivity.this,ContantsActivity.GeTui.CLIENT_ID);
						finish();
					}
				});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
			}

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ContantsActivity.Action.ACTION_PUSH_VIDEO) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			mTabs[currentTabIndex].setSelected(false);
			index = 0;
			trx.show(fragments[index]).commit();
			currentTabIndex = index;
			mTabs[index].setSelected(true);

		}

		if (mFragment_Home != null) {
			mFragment_Home.onActivityResult(requestCode, resultCode, data);
		}
		if (mFragment_Me != null) {
			mFragment_Me.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View view) {
		isSwitchingCamera = false;
		switch (view.getId()) {
		case R.id.tab_camera:
			if (mFirstUseImageTag != null) {
				mFirstUseImageTag.setVisibility(View.GONE);
			}
			isSwitchingCamera = true;
			if (VideoUploadTaskDBManager.getInstance(MainActivity.this, member_id).isHasVideoUpload()) {
				ToastUtils.showShort(MainActivity.this, "当前有视频正在上传请稍后。。。");
			} else {
				if (DeviceUtils.hasM()) {
					checkCameraPermission();
				} else {
					mIntent.setClass(MainActivity.this, ActivityMediaVideoRecorder.class);
					startActivityForResult(mIntent, ContantsActivity.Action.ACTION_PUSH_VIDEO);
				}
			}
			break;
		case R.id.take_photo_prompt:
			mFirstUseImageTag.setVisibility(View.GONE);
			break;
		case R.id.tab_home:
			index = 0;
			break;
		case R.id.tab_jiequ:
			index = 1;
			break;
		case R.id.tab_message:
			index = 2;
			break;
		case R.id.tab_me:
			index = 3;
			break;
		}
		if (!isSwitchingCamera) {
			if (currentTabIndex != index) {
				FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
				trx.hide(fragments[currentTabIndex]);
				if (!fragments[index].isAdded()) {
					trx.add(R.id.main_fragment_layout, fragments[index]);
				}
				if (index != 3 && fragments[3] != null) {
					((Fragment_Me) fragments[3]).hide();
				}
				if (index != 0 && fragments[0] != null) {
					((Fragment_Home) fragments[0]).hide();
				}

				trx.show(fragments[index]).commit();
			}
			mTabs[currentTabIndex].setSelected(false);
			// 把当前tab设为选中状态
			mTabs[index].setSelected(true);
			currentTabIndex = index;
		}
		if (index == 0 && isFirstUser) {
			PrefUtils.putBoolean(MainActivity.this, ContantsActivity.LoginModle.IS_FIRST_USER_IN_MAIN, false);
			if (mFirstUseImageTag != null && mFirstUseImageTag.getVisibility() == View.VISIBLE)
				mFirstUseImageTag.setVisibility(View.VISIBLE);
		} else {
			if (mFirstUseImageTag != null)
				mFirstUseImageTag.setVisibility(View.GONE);
		}

	}

	private void checkCameraPermission() {
		// if (!(checkSelfPermission(android.Manifest.permission.CAMERA) ==
		// PackageManager.PERMISSION_GRANTED)) {
		// requestCameraPermission();
		// } else {
		// mIntent.setClass(MainActivity.this,
		// ActivityMediaVideoRecorder.class);
		// startActivityForResult(mIntent,
		// ContantsActivity.Action.ACTION_PUSH_VIDEO);
		// }
	}

	private static final int REQUEST_PERMISSION_CAMERA_CODE = 0x2003;

	private void requestCameraPermission() {
		// requestPermissions(new String[] { android.Manifest.permission.CAMERA
		// }, REQUEST_PERMISSION_CAMERA_CODE);
	}

	@Override
	public void onRequestPermissionsResult(int requestcode, String[] permissions, int[] grantResult) {
		// TODO Auto-generated method stub
		super.onRequestPermissionsResult(requestcode, permissions, grantResult);
		if (requestcode == REQUEST_PERMISSION_CAMERA_CODE) {
			int grantResultCode = grantResult[0];
			boolean granted = grantResultCode == PackageManager.PERMISSION_GRANTED;
			if (granted) {
				mIntent.setClass(MainActivity.this, ActivityMediaVideoRecorder.class);
				startActivityForResult(mIntent, ContantsActivity.Action.ACTION_PUSH_VIDEO);
				ToastUtils.showShort(MainActivity.this, "您获取相机授权成功");
			} else {
				ToastUtils.showShort(MainActivity.this, "您已经拒绝相机授权");
			}
		}

	}

	private void editUserLoaction(String lng, String lat) {
		final String baseURL = "/member/modifyMemberInfo";
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", login_token);
		params.put("lng", lng);
		params.put("lat", lat);
		LogUtils.e("修改用户资料", "-------");
		BaseHttpNetMananger.getInstance(MainActivity.this).postJSON(MainActivity.this, baseURL, params,
				new IRequestResult() {
					@Override
					public void requestSuccess(String req) {
						try {
							JSONObject root = new JSONObject(req);
							int code = root.getInt("code");
							String msg = null;
							switch (code) {
							case 100001:

								break;
							case 900001:
								msg = root.getString("msg");
							default:
								ToastUtils.showErrorToast(MainActivity.this, code, msg, true);
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void requestFail() {
						ToastUtils.showShort(MainActivity.this, getString(R.string.net_error));
					}

					@Override
					public void requestNetExeption() {
						ToastUtils.showShort(MainActivity.this, getString(R.string.net_conected_error));

					}
				}, false);
	}

	boolean isFinish = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//
			if (isFinish) {
				// moveTaskToBack(false);
				finish();
			} else {
				isFinish = true;
				ToastUtils.showShort(MainActivity.this, "双击退出应用程序");
				mMainHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						isFinish = false;
					}
				}, 2000);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
			EMMessage message = (EMMessage) event.getData();
			// 提示新消息
			DemoHelper.getInstance().getNotifier().onNewMsg(message);

			refreshUIWithMessage();
			break;
		case EventOfflineMessage: {
			refreshUIWithMessage();
			break;
		}

		case EventConversationListChanged: {
			refreshUIWithMessage();
			break;
		}
		case EventNewCMDMessage:
			EMMessage cmdMessage = (EMMessage) event.getData();
			// 获取消息body
			CmdMessageBody cmdMsgBody = (CmdMessageBody) cmdMessage.getBody();
			final String action = cmdMsgBody.action;// 获取自定义action
			if (action.equals(EaseConstant.EASE_ATTR_REVOKE)) {
				EaseCommonUtils.receiveRevokeMessage(this, cmdMessage);
			}
			refreshUIWithMessage();
			break;
		case EventReadAck:
			// TODO 这里当此消息未加载到内存中时，ackMessage会为null，消息的删除会失败
			EMMessage ackMessage = (EMMessage) event.getData();
			EMConversation conversation = EMChatManager.getInstance().getConversation(ackMessage.getTo());
			// 判断接收到ack的这条消息是不是阅后即焚的消息，如果是，则说明对方看过消息了，对方会销毁，这边也删除(现在只有txt iamge
			// file三种消息支持 )
			if (ackMessage.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false)
					&& (ackMessage.getType() == Type.TXT || ackMessage.getType() == Type.VOICE
							|| ackMessage.getType() == Type.IMAGE)) {
				// 判断当前会话是不是只有一条消息，如果只有一条消息，并且这条消息也是阅后即焚类型，当对方阅读后，这边要删除，会话会被过滤掉，因此要加载上一条消息
				if (conversation.getAllMessages().size() == 1
						&& conversation.getLastMessage().getMsgId().equals(ackMessage.getMsgId())) {
					if (ackMessage.getChatType() == ChatType.Chat) {
						conversation.loadMoreMsgFromDB(ackMessage.getMsgId(), 1);
					} else {
						conversation.loadMoreGroupMsgFromDB(ackMessage.getMsgId(), 1);
					}
				}
				conversation.removeMessage(ackMessage.getMsgId());
			}
			refreshUIWithMessage();
			break;
		default:
			break;
		}

	}




}
