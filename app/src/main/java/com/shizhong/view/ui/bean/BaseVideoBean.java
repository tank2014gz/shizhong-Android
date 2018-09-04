package com.shizhong.view.ui.bean;

import java.io.Serializable;

/**
 * Created by yuliyan on 16/1/26.
 */
public class BaseVideoBean implements Serializable{
	public int position;
	// "memberId": "aa860ea1c62c4f04b8cbb03a32aee89b",
	public String memberId;
	// "videoId": "kannzz6740091803852543sad",
	public String videoId;
	// "videoUrl": "http://7xpj9q.media1.z0.glb.clouddn.com/Party_Favors.mp4"
	public String videoUrl;
	// "description": "有感觉。。",
	public String description;
	// "likeCount": 1,
	public long likeCount;
	public long commentCount;
	// "coverUrl": "http://7xpj9s.com1.z0.glb.clouddn.com/60A6.tmp.png",
	public String coverUrl;
	public long clickCount;
	public String createTime;
	public long videoLength;
	public String isLike;
	public TopicBean topic;
	public String shareUrl;// 分享URL

	// public String topicId;
	// public String topicName;

	public UserExtendsInfo memberInfo;
	// "isLike": "0",

	//
	// // "nickname": "NO",
	// public String nickname;
	// // "headerUrl":
	// //
	// "http://7xpj9r.com1.z0.glb.clouddn.com/IOS_20160111235011THNMFQPLUWNJOXMOIS.png",
	// public String headerUrl;
	// public String isAttention;
	// public double lng;
	// public double lat;


	@Override
	public String toString() {
		return "BaseVideoBean{" +
				"position=" + position +
				", memberId='" + memberId + '\'' +
				", videoId='" + videoId + '\'' +
				", videoUrl='" + videoUrl + '\'' +
				", description='" + description + '\'' +
				", likeCount=" + likeCount +
				", commentCount=" + commentCount +
				", coverUrl='" + coverUrl + '\'' +
				", clickCount=" + clickCount +
				", createTime='" + createTime + '\'' +
				", videoLength=" + videoLength +
				", isLike='" + isLike + '\'' +
				", topic=" + topic +
				", shareUrl='" + shareUrl + '\'' +
				", memberInfo=" + memberInfo +
				'}';
	}

}
