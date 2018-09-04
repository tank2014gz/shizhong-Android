package com.hyphenate.easeui.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yuliyan on 16/1/4.
 */
public class DateUtils {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	// @SuppressWarnings("unused")
	// private static SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日");
	// @SuppressWarnings("unused")
	// private static SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
	// @SuppressWarnings("unused")
	// private static SimpleDateFormat sdf3 = new SimpleDateFormat("MM月dd日
	// HH:mm");
	// @SuppressWarnings("unused")
	// private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy年MM月dd日
	// HH:mm");
	// @SuppressWarnings("unused")
	// private static SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy年");
	// @SuppressWarnings("unused")
	// private static SimpleDateFormat sdf6 = new SimpleDateFormat("MM月");
	// @SuppressWarnings("unused")
	// private static SimpleDateFormat sdf7 = new SimpleDateFormat("yyyy年MM月");
	// @SuppressWarnings("unused")
	// private static SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdf9 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String format1(Date date) {
		return sdf.format(date);
	}

	public static String format9(Date date) {
		return sdf9.format(date);
	}

	public static String format9(long date) {
		return sdf9.format(new Date(date));
	}

	public static String formateVideoCreateTime(String timeStr) {
		// Date date = new Date(timeStr);
		// 2016-01-30 15:41:46
		try {
			return com.easemob.util.DateUtils.getTimestampString(sdf9.parse(timeStr));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}



	public static final String[] constellationArr = { "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座",
			"天蝎座", "射手座", "魔羯座" };
	public static final int[] constellationEdgeDay = { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };

	/**
	 * 根据日期获取星座
	 * 
	 * @return
	 */
	public static String getConstellation(String date) {
		Date dateD = null;
		try {
			dateD = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (date == null) {
			return "";
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateD);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if (day < constellationEdgeDay[month]) {
			month = month - 1;
		}
		if (month >= 0) {
			return constellationArr[month];
		}
		// default to return 魔羯
		return constellationArr[11];
	}

	public static long parseTime(String time) {
		try {
			if (!TextUtils.isEmpty(time)) {

				return sdf9.parse(time).getTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0l;

	}
}
