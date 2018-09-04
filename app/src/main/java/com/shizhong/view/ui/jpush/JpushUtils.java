package com.shizhong.view.ui.jpush;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.hyphenate.easeui.utils.PrefUtils;

import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by yuliyan on 15/12/22.
 */
public class JpushUtils {
    private static JpushUtils mInstance;
    public static final String PREF_IS_SETTING_JPUSH = "IS_SETTING_JPUSH";
	public final static String APP_KEY="JPUSH_APPKEY";
	public static final  String IS_SETTING_TAGS="is_setting_tags";//设置别名是否成功


    public static final String APPKEY = "79e45d1fce05ba3bbf4932b2";//激光推送 appkey

    private JpushUtils() {

    }

    private JpushUtils(Context context) {

    }

    public static JpushUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new JpushUtils(context);
        }
        return mInstance;
    }

    
    // 取得AppKey
    public static String getAppKey(Context context) {
        Bundle metaData = null;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai)
                metaData = ai.metaData;
            if (null != metaData) {
                appKey = metaData.getString(APP_KEY);
                if ((null == appKey) || appKey.length() != 24) {
                    appKey = null;
                }
            }
        } catch (NameNotFoundException e) {

        }
        return appKey;
    }
    public void initPush(Context context) {
    	
        JPushInterface.setDebugMode(true);
        JPushInterface.init(context);
        JPushInterface.setLatestNotificationNumber(context, 3);
    }

    /**
     * 设置关闭极光推送功能
     *
     * @param context
     */

    public void stopJPush(Context context) {
        JPushInterface.stopPush(context);
        PrefUtils.putBoolean(context, PREF_IS_SETTING_JPUSH, false);
    }

    /**
     * 设置启动极光推送功能
     *
     * @param context
     */
    public void startJPush(Context context) {
        JPushInterface.resumePush(context);
        PrefUtils.putBoolean(context, PREF_IS_SETTING_JPUSH, true);
    }


    /**
     * 统计Activity被打开(通过通知打开应用中Activity)
     * 统计上报用户的通知栏被打开或者用户定义的消息被展示灯客户端需要统计的事件
     *
     * @param context
     */
    public void noticREGISTRATION_ID(Context context, Bundle bundle) {
        JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));

    }

    /**
     * 判断是否要开启推送功能：默认是开启
     *
     * @param context
     * @return
     */
    public boolean isOpenJpush(Context context) {
        return PrefUtils.getBoolean(context, PREF_IS_SETTING_JPUSH, true);
    }

    /**
     * 清除所有的相关通知
     *
     * @param context
     */

    public void clearAllNotification(Context context) {
        JPushInterface.clearAllNotifications(context);
    }

    /**
     * 清除特定的通知
     *
     * @param context
     * @param notifcationID
     */
    public void clearNotification(Context context, int notifcationID) {
        JPushInterface.clearNotificationById(context, notifcationID);
    }

    /**
     * 极光推送设定的推送时间设置
     *
     * @param context
     * @param weekDays  0：星期天 1:星期一以此类推
     * @param startHour
     * @param endHour
     */
    public void setPushTime(Context context, Set<Integer> weekDays, int startHour, int endHour) {
        JPushInterface.setPushTime(context, weekDays, startHour, endHour);
    }

    /**
     * 设置极光推送的静默时间段
     *
     * @param context
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     */
    public void setSilienceTime(Context context, int startHour, int startMinute, int endHour, int endMinute) {
        JPushInterface.setSilenceTime(context, startHour, startMinute, endHour, endMinute);
    }

    /**
     * 设置通知栏样式（default/flags/icon）
     *
     * @param builder
     */
    public void setDefaultPushNotificationBuilder(BasicPushNotificationBuilder builder) {
        JPushInterface.setDefaultPushNotificationBuilder(builder);
    }

    /**
     * 设置自定义的通知栏样式
     *
     * @param builder
     */
    public void setCustomPushNoctifcationBuilder(CustomPushNotificationBuilder builder) {
        JPushInterface.setDefaultPushNotificationBuilder(builder);
    }

    /**
     * @param notificationBuilderID
     * @param builder
     */
    public void setPushNoticationBuilder(int notificationBuilderID, BasicPushNotificationBuilder builder) {
        JPushInterface.setPushNotificationBuilder(notificationBuilderID, builder);
    }

    /**
     * 为安装应用的用户取个别名，以后方便用这个别名来推送这个消息，不同的用户帐号使用不同的别名
     * 为安装应用程序用户设置tag，可以设置多个tag属性
     *
     * @param context
     * @param alias《用户昵称》
     * @param tags《性别》《舞蹈种类》
     * @param callback
     */

    public void setAliasAndTags(Context context, String alias, Set<String> tags, TagAliasCallback callback) {
        JPushInterface.setAliasAndTags(context, alias, tags, callback);
    }


}
