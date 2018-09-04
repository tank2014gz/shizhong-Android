package com.shizhong.view.ui.bean;


import java.io.Serializable;

/**
 * Created by yuliyan on 16/1/27.
 */
public class TopicBean implements Serializable{
	
	public String topicId;
	// "topicName": "我是最流弊的街舞达人"
	public String topicName;
	// "coverUrl": "http://7xpj9w.com1.z0.glb.clouddn.com/15040211252011.jpg",
	public String coverUrl;
	// "description": "你是不是最牛的街舞达人？",
	public String description;
	// "topicId": "321538c2a47c45deacd43844c7d12040",
	public int partakeCount;//参与量
	
	

	public int position;

	@Override
	public String toString() {
		return "TopicBean{" + "coverUrl='" + coverUrl + '\'' + ", description='" + description + '\'' + ", topicId='"
				+ topicId + '\'' + ", topicName='" + topicName + '\'' + '}';
	}

}
