package com.shizhong.view.ui.map;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.shizhong.view.ui.base.PlaceDBManager;
import com.shizhong.view.ui.base.utils.LogUtils;

import android.content.Context;
import android.text.TextUtils;

public class GeoMapMananger implements SZLocationManager, AMapLocationListener  {


	private AMapLocationClient mLocationClient = null;
	private AMapLocationClientOption mLocationOption = null;
	private PlaceDBManager mDBManager;
	private ILoactionInfoCaller mLoacionInfoListener;

	private GeoMapMananger() {
		super();
	}

	public GeoMapMananger(Context context) {
		// 初始化定位
		mLocationClient = new AMapLocationClient(context.getApplicationContext());
		// 设置定位回调监听
		mLocationClient.setLocationListener(this);
		// 初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		// 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// 设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		// 设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(false);
		// 设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		// 设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		// 设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);

		// 给定位客户端对象设置定位参数
		mDBManager = new PlaceDBManager (context);
	}


	@Override
	public void setLoacionInfoListener(ILoactionInfoCaller call) {
		this.mLoacionInfoListener = call;
	}

	@Override
	public void startLoacation() {
		if (mLocationClient != null && mLocationOption != null) {
			mLocationClient.setLocationOption(mLocationOption);
			// 启动定位
			mLocationClient.startLocation();
		}
	}

		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			if (amapLocation != null) {
				if (amapLocation.getErrorCode() == 0) {
					// 定位成功回调信息，设置相关消息
					amapLocation.getLocationType();// 获取当前定位结果来源，如网络定位结果，详见定位类型表
					amapLocation.getLatitude();// 获取纬度
					amapLocation.getLongitude();// 获取经度
					amapLocation.getAccuracy();// 获取精度信息
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date(amapLocation.getTime());
					df.format(date);// 定位时间
					amapLocation.getAddress();// 地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
					amapLocation.getCountry();// 国家信息
					amapLocation.getProvince();// 省信息
					amapLocation.getCity();// 城市信息
					amapLocation.getDistrict();// 城区信息
					amapLocation.getStreet();// 街道信息
					amapLocation.getStreetNum();// 街道门牌号信息
					amapLocation.getCityCode();// 城市编码
					amapLocation.getAdCode();// 地区编码
					amapLocation.getAoiName();// 获取当前定位点的AOI信息
					String province = amapLocation.getProvince();
					String city = amapLocation.getCity();
					String lat = amapLocation.getLatitude() + "0000";
					String lng = amapLocation.getLongitude() + "0000";

					String provinceId = "";
					if (!TextUtils.isEmpty(province)) {
						mDBManager.getProvinceId(province);
					}
					String cityId = "";
					if (!TextUtils.isEmpty(city)) {
						cityId = mDBManager.getCityId(city);
					}

					if (mLoacionInfoListener != null) {
						mLoacionInfoListener.callback(provinceId, cityId, lng, lat);
					}
				} else {
					// 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
					LogUtils.e("AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:"
							+ amapLocation.getErrorInfo());
				}
			}

		}

	@Override
	public void stopLoaction() {
		if (mLocationClient != null)
			mLocationClient.stopLocation();
	}

	@Override
	public void onDestory() {
		if (mLocationClient != null) {
			mLocationClient.onDestroy();
		}
		if(mLoacionInfoListener!=null){
			mLoacionInfoListener=null;
		}
		if(mDBManager!=null){
			mDBManager=null;
		}
	}

}
