package com.shizhong.view.ui.base.utils;

/**
 * 常见的正则表达式
 *
 * @author yuliyan
 */
public class RexUtils {
	/**
	 * 手机号正则表达
	 */
	public static final String PHOEN_REX = "^1[34578]\\d{9}$";

	/**
	 * 密码正则表达
	 */
	public static  final String PWD_REX="^[A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~\\,\\;\\?]{6,20}$";
//	public static final String PWD_REX = "(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_#@]+$).{6,20}";
	// public static final String PWD_REX = "^[0-9a-zA-Z]{6,18}$";
	// public static final String PWD_REX = "^(?!([]{}（#%-*+=_）\\|~(＜＞$%^&*)_+
	// )){6,18}$";
	//public static final String PWD_REX = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

	/**
	 * 极光推送TAG Alias 只能是数字，英文字母和中文
	 **/
	public static final String NSC_REX = "^[\u4E00-\\u9FA50-9a-zA-Z_-]{0,}$";

}
