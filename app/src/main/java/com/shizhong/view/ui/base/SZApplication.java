package com.shizhong.view.ui.base;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import dalvik.system.DexClassLoader;
import android.support.multidex.MultiDexApplication;

import com.easemob.chat.EMChatManager;
import com.igexin.sdk.PushManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hyphenate.chatuidemo.DemoHelper;
import com.shizhong.view.ui.base.utils.AssetsUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.bean.ConstansDBTable;
import com.shizhong.view.ui.jpush.JpushUtils;
//import com.squareup.leakcanary.LeakCanary;
import com.wind.ffmpeghelper.FFmpegNativeHelper;
import android.support.multidex.MultiDex;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.xutils.x;

public class SZApplication extends MultiDexApplication {
	private static SZApplication application;
	// login user name
	public final String PREF_USERNAME = "username";

	public static final boolean IS_DEBUG = true;

	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	private  ActivityManager activityManager;
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this) ;
	}

	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		super.onCreate();
//		LeakCanary.install(this);
		PushManager.getInstance().initialize(this.getApplicationContext());
		application = this;
		LogUtils.setDebug(
				DebugConfig.is_debug || DebugConfig.is_local || DebugConfig.is_local_zhao || DebugConfig.is_debug_yu||DebugConfig.xs_debug);
		x.Ext.init(this);
		AssetsUtils.getInstance(this).copyDBToSD(ConstansDBTable.Address.DB_NAME);
		RecoderVideoManager.initRecode(SZApplication.this, 0);
		// 初始化环信
		DemoHelper.getInstance().init(application);

		JpushUtils.getInstance(this).initPush(this);
		if (DebugConfig.is_debug || DebugConfig.is_local || DebugConfig.is_local_zhao || DebugConfig.is_debug_yu) {
			FFmpegNativeHelper.getHelper().SetDebug(1);
		} else {
			FFmpegNativeHelper.getHelper().SetDebug(0);
		}
		Fresco.initialize(this);
//		Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));
	}



	public static SZApplication getInstance() {
		return application;
	}

}
