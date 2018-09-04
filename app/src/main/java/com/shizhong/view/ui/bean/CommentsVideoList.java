package com.shizhong.view.ui.bean;


import java.util.List;

/**
 * Created by yuliyan on 16/1/26.
 */
public class CommentsVideoList  {
	public int code;
	public List<CommentsVideoBean> data;
	
    @Override
    public String toString() {
        return "CommentsVideoList{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

}
