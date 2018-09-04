package com.shizhong.view.ui.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.shizhong.view.ui.base.PlaceDBManager;
import com.shizhong.view.ui.base.utils.LogUtils;

import android.content.Context;
import android.text.TextUtils;

public class BaiDuMapManager implements SZLocationManager,BDLocationListener {
	private LocationClient mLocationClient = null;
	private PlaceDBManager mDBManager;
	private ILoactionInfoCaller mLoacionInfoListener;

	private BaiDuMapManager() {
		super();
	}

	public BaiDuMapManager(Context context) {
		super();
		mLocationClient = new LocationClient(context.getApplicationContext());
		mLocationClient.registerLocationListener(this);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
		mDBManager = new PlaceDBManager(context);
	}


	@Override
	public void setLoacionInfoListener(ILoactionInfoCaller call) {
		// TODO Auto-generated method stub
		this.mLoacionInfoListener = call;
	}

	@Override
	public void startLoacation() {
		// TODO Auto-generated method stub
		mLocationClient.start();
	}

	@Override
	public void stopLoaction() {
		// TODO Auto-generated method stub
		mLocationClient.stop();
	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub
		mLocationClient.unRegisterLocationListener(this);
	}


		@Override
		public void onReceiveLocation(BDLocation location) {
			String longitude = location.getLongitude()+"0000";
			String latitude = location.getLatitude()+"0000";
			int type = location.getLocType();
			String cityId = null;
			String provinceId = null;
			String city = location.getCity();
			String province = location.getProvince();
			if (mDBManager != null) {
				if (!TextUtils.isEmpty(city)) {
					cityId = mDBManager.getCityId(city);
				}
				if (!TextUtils.isEmpty(province)) {
					provinceId = mDBManager.getProvinceId(province);
				}
			}

			if (type == BDLocation.TypeGpsLocation) {
				if (mLoacionInfoListener != null) {
					mLoacionInfoListener.callback(provinceId, cityId, longitude+"", latitude+"");
				}
				LogUtils.e("baidu loaction", "gps定位成功");
			} else if (type == BDLocation.TypeNetWorkLocation) {
				if (mLoacionInfoListener != null) {
					mLoacionInfoListener.callback(provinceId, cityId, longitude+"", latitude+"");
				}
				LogUtils.e("baidu loaction", "网络定位成功");
			} else if (type == BDLocation.TypeOffLineLocation) {
				if (mLoacionInfoListener != null) {
					mLoacionInfoListener.callback(provinceId, cityId, longitude+"", latitude+"");
				}
				LogUtils.e("baidu loaction", "离线定位成功，离线定位结果也是有效的");
			} else if (type == BDLocation.TypeServerError) {
				LogUtils.e("baidu loaction", "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

			} else if (type == BDLocation.TypeNetWorkException) {
				LogUtils.e("baidu loaction", "网络不同导致定位失败，请检查网络是否通畅");

			} else if (type == BDLocation.TypeCriteriaException) {
				LogUtils.e("baidu loaction", "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

			}

		}


}
