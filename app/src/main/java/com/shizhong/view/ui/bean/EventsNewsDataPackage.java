package com.shizhong.view.ui.bean;

import java.util.List;

/**
 * Created by yuliyan on 16/1/27.
 */
public class EventsNewsDataPackage {
	public List<EventsNewsBean> data;
	public int code;

	@Override
	public String toString() {
		return "EventsNewsDataPackage{" + "data=" + data + ", code=" + code + '}';
	}

}
