package com.hyphenate.easeui.utils;

/**
 * Created by yuliyan on 16/1/26.
 */
public class FormatImageURLUtils {

	@SuppressWarnings("static-access")
	public static String formatURL(String orgUrl, int width, int height) {
		if (orgUrl != null) {
			if (orgUrl.contains("?")) {
				orgUrl = orgUrl.substring(0, orgUrl.lastIndexOf("?"));
			}
			return new String().format(orgUrl + "?imageView2/0/w/%s/h/%s", width, height);
		} else {
			return "";
		}
	}

}
