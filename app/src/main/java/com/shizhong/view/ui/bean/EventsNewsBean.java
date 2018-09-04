package com.shizhong.view.ui.bean;

/**
 * Created by yuliyan on 16/1/27.
 */
public class EventsNewsBean   {

	// "content": {}
	public EventsNewsContentBean content;
	// "coverUrl": "sdadsadad",
	public String coverUrl;
	// "createTime": "2016-01-10 15:42:44",
	public String createTime;
	// "newsId": "asdada",
	public String newsId;
	// "title": "霍营BOYS获2015年最具影响力舞团"
	public String title;


	@Override
	public String toString() {
		return "EventsNewsBean{" + "content=" + content + ", coverUrl='" + coverUrl + '\'' + ", createTime='"
				+ createTime + '\'' + ", newsId='" + newsId + '\'' + ", title='" + title + '\'' + '}';
	}
}
