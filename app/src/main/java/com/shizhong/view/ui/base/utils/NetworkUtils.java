package com.shizhong.view.ui.base.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 判断网络连接
 *
 * @author Administrator
 */
public class NetworkUtils {

	/**
	 * 获取当前网络连接的类型信息 --1联通2G --2移动2G --4电信2G --5电信3G --6电信3G --12电信3G --3电信3G
	 * --8联通3G --13LTE --11IDEN --9HSUPA --10HSPA --15HSPAP
	 *
	 * @param context
	 * @return
	 */

	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
				return mNetworkInfo.getType();// 返回类型为1,ConnectivityManager.TYPE_MOBILE;
				// 2,ConnectivityManager.TYPE_WIFI;
			}
		}
		return -1;// -1表示网络不可用或者没有连接上
	}

	/**
	 * 判断是否有网络连接
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isConnected();// isConnected表示连接上,isAvailable表示可用
			}
		}
		return false;
	}

	/**
	 * 判断WIFI网络是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isConnected();// isConnected表示连接上,isAvailable表示可用
			}
		}
		return false;
	}

	/**
	 * 判断MOBILE网络是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isConnected();// isConnected表示连接上,isAvailable表示可用
			}
		}
		return false;
	}

	public static boolean checkURL(String url) {
		boolean value = false;
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			int code = conn.getResponseCode();
			if (code != 200) {
				value = false;
			} else {
				value = true;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

}
