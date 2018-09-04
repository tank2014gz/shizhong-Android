package com.shizhong.view.ui.base.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Created by yuliyan on 15/12/23.
 */
public class DeviceIdentify {

    /**
     * 获取设备的IMEI码 平板设别没有，需要READ_PHONE_STATE权限
     */

    public static String IMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String id = telephonyManager.getDeviceId();
        return id == null ? "" : id;

    }


    /**
     * Pseudo-Unique ID
     *
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String PUID() {
        String likeIMEI = "35" + Build.BOARD.length() % 10
                + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
                + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10
                + Build.HOST.length() % 10 + Build.ID.length() % 10
                + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
                + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10
                + Build.TYPE.length() % 10 + Build.USER.length() % 10;
        return likeIMEI;
    }

    /**
     * MAC地址
     *
     * @param context
     * @return
     */
    public static String MAC(Context context) {
        try {
            WifiManager wm = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            String MAC = wm.getConnectionInfo().getMacAddress();
            return null == MAC ? "" : MAC;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 蓝牙地址
     *
     * @return
     */
    public static String BlueToothMAC() {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            String BT_MAC = adapter.getAddress();
            return null == BT_MAC ? "" : BT_MAC;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 组合以上各种信息的设备码
     *
     * @param context
     * @return
     */
    public static String CombinedDeviceID(Context context) {
        return new StringBuilder("PUID:").append(PUID()).append("  ")
                .append("IMEI:").append(IMEI(context)).append("  ")
                .append("MAC:").append(MAC(context)).append("  ")
                .append("BT-MAC:").append(BlueToothMAC()).toString();
    }
    
    

}
