package com.shizhong.view.ui.bean;


import java.util.List;

/**
 * Created by yuliyan on 16/1/26.
 */
public class BaseVideoList {
	public int code;
	public List<BaseVideoBean> data;
	public Expand expand;

	@Override
	public String toString() {
		return "BaseVideoList{" + "code=" + code + ", data=" + data + '}';
	}

	class Expand{
      int videoCount;
	}
}
