package com.shizhong.view.ui.map;

public interface SZLocationManager {

	public void setLoacionInfoListener(ILoactionInfoCaller call);

	public void startLoacation();

	public void stopLoaction();

	public void onDestory();

}
