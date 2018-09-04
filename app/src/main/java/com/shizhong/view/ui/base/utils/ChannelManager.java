package com.shizhong.view.ui.base.utils;

public class ChannelManager {

	public static String[] channels = new String[] { 
			          "dev", "official", "google", "360", 
			          "baidu", "wandoujia", "huawei","qq",
			          "xiaomi", "appchina", "youmi", "waps", 
			          "gfan", "91", "hiapk", "goapk",
			          "mumayi", "eoe", "nduo","feiliu",
			          "crossmo", "liantong", "3g", "sohu",
			          "samsung", "coolmart", "meizu", "moto", 
			          "lenovo", "zhuamob","iandroid", "uc" };

	private String getChannelName(String channelKey) {
		String result = "";
		if ("dev".equals(channelKey)) {
			result = "开发版";
		} else if ("official".equals(channelKey)) {
			result = "官方版";
		} else if ("google".equals(channelKey)) {
			result = "谷歌版";
		} else if ("appchina".equals(channelKey)) {
			result = "应用汇版";
		} else if ("youmi".equals(channelKey)) {
			result = "有米版";
		} else if ("waps".equals(channelKey)) {
			result = "万普版";
		} else if ("gfan".equals(channelKey)) {
			result = "机锋版";
		} else if ("91".equals(channelKey)) {
			result = "91版";
		} else if ("hiapk".equals(channelKey)) {
			result = "安卓版";
		} else if ("goapk".equals(channelKey)) {
			result = "安智版";
		} else if ("mumayi".equals(channelKey)) {
			result = "木蚂蚁版";
		} else if ("eoe".equals(channelKey)) {
			result = "优亿版";
		} else if ("nduo".equals(channelKey)) {
			result = "N多版";
		} else if ("feiliu".equals(channelKey)) {
			result = "飞流版";
		} else if ("crossmo".equals(channelKey)) {
			result = "十字猫版";
		} else if ("liantong".equals(channelKey)) {
			result = "联通版";
		} else if ("huawei".equals(channelKey)) {
			result = "智汇云版";
		} else if ("qq".equals(channelKey)) {
			result = "腾讯版";
		} else if ("3g".equals(channelKey)) {
			result = "3G版";
		} else if ("360".equals(channelKey)) {
			result = "360版";
		} else if ("baidu".equals(channelKey)) {
			result = "百度版";
		} else if ("sohu".equals(channelKey)) {
			result = "搜狐版";
		} else if ("samsung".equals(channelKey)) {
			result = "三星版";
		} else if ("coolmart".equals(channelKey)) {
			result = "酷派版";
		} else if ("meizu".equals(channelKey)) {
			result = "魅族版";
		} else if ("moto".equals(channelKey)) {
			result = "摩托版";
		} else if ("xiaomi".equals(channelKey)) {
			result = "小米版";
		} else if ("lenovo".equals(channelKey)) {
			result = "联想版";
		} else if ("zhuamob".equals(channelKey)) {
			result = "抓猫版";
		} else if ("iandroid".equals(channelKey)) {
			result = "爱卓版";
		} else if ("imobile".equals(channelKey)) {
			result = "手机之家版";
		} else if ("uc".equals(channelKey)) {
			result = "UC版";
		} else if ("wandoujia".equals(channelKey)) {
			result = "豌豆荚";
		} else {
			result = "山寨版";
		}
		return result;
	}

}
