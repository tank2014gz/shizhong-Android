package com.shizhong.view.ui.bean;


import java.util.List;

/**
 * Created by yuliyan on 16/1/27.
 */
public class TopicDataPackage {
	public int code;
	public List<TopicBean> data;


    @Override
    public String toString() {
        return "TopicDataPackage{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

}
