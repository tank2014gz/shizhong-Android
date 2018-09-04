package com.shizhong.view.ui.base.net;

import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.base.utils.MD5;
import com.hyphenate.easeui.ContantsActivity;
/**
 * Created by yuliyan on 16/1/4.
 */
public class BaseQiNiuRequest {
	public static String mCurrentBaseUrl;
	@SuppressWarnings("unused")
	private static final String COM_URL = "https://portal.qiniu.com/bucket";

	// 0:头像
	// 1:视频
	// 2:视频封面
	// 7:舞社logo
	public final static String MODEL_ACTION_MEMBER_HEAD = "0";// 用户头像
	public final static String MODEL_ACTION_CLUB_LOGO = "7";// 舞社头像
	public final static String MODEL_ACTION_VIDEO_COVER = "2";// 视频封面
	public final static String MODEL_ACTION_VIDEO = "1";// 视频

	public static String getImageFileKey() {
		return "Android_" + MD5.md5(System.currentTimeMillis() + "") + ".png";
	}

	public static String getVideoFileKey() {
		return "Android_" + MD5.md5(System.currentTimeMillis() + "") + ".mp4";
	}

	public static String getFileUrl(String key) {
		return "http://7xpj9s.com1.z0.glb.clouddn.com/" + key;
	}

	public static String getSmallImageUrl(String key) {
		return FormatImageURLUtils.formatURL(getFileUrl(key), ContantsActivity.Image.SMALL,
				ContantsActivity.Image.SMALL);
	}
}
