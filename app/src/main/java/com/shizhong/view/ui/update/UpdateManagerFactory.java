package com.shizhong.view.ui.update;

public class UpdateManagerFactory {

	public static final String UPDATE_BASE = "base";
	public static final String UPDATE_XIAOMI = "xiaomi";

	public static UpdateManager createUpdateManager(String type) {
		UpdateManager mUpdateManager;
		if (type.equals(UPDATE_BASE)) {
			mUpdateManager = new BaseUpdateManager();
		} else {
			mUpdateManager = new XMVersionManager();
		}
		return mUpdateManager;
	}

}
