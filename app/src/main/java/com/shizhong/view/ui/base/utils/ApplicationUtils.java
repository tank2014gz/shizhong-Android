package com.shizhong.view.ui.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Created by yuliyan on 15/12/24.
 */
public class ApplicationUtils {
    public static final String KEY_APP = "JPUSH_APPKEY";

    public static String getAppKey(Context context) {
        Bundle bundle = null;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                bundle = ai.metaData;
                if (null != bundle) {
                    appKey = bundle.getString(KEY_APP);
                    if ((null == appKey) || (appKey.length() != 24)) {
                        appKey = null;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appKey;
    }

    // 取得版本号
    public static String GetVersion(Context context) {
        PackageInfo manager = null;
        try {
            manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return manager.versionName;
    }
}
