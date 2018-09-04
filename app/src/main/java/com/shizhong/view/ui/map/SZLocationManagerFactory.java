package com.shizhong.view.ui.map;

import android.content.Context;

public class SZLocationManagerFactory {
	public static final String TYPE_BAIDU = "baidu";
	public static final String TYPE_GAODE = "gaode";

	public static  SZLocationManager getLoactionManager(Context context, String type) {
		SZLocationManager locationManager = null;
		if (type.equals(TYPE_BAIDU)) {
			locationManager = new BaiDuMapManager(context);
		} else if (type.equals(TYPE_GAODE)) {
			locationManager = new GeoMapMananger(context);
		}
		return locationManager;
	}

}
