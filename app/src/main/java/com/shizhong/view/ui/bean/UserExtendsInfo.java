package com.shizhong.view.ui.bean;

import java.io.Serializable;


/**
 * Created by yuliyan on 16/1/27.
 */
public class UserExtendsInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// "isAttention": 0,
	public String isAttention;
	// "provinceId": "2",
	public String provinceId;
	// "birthday": "1996-01-06",
	public String birthday;
	// "sex": "1",
	public String sex;
	// "videoCount": 0,
	public int videoCount;
	// "attentionCount": 0,
	public int attentionCount;
	// "nickname": "4号楼",
	public String nickname;
	// "cityId": "2",
	public String cityId;
	// "headerUrl":
	// "http://7xpj9r.com1.z0.glb.clouddn.com/FiBws4PV7-BI-uHsS8EuENAoocXo",
	public String headerUrl;
	// "districtId": "35",
	public String districtId;
	// "memberId": "a6c06516ffeb42c687dda9a8ee2c5e9e",
	public String memberId;
	// "signature": "",
	public String signature;
	// "fansCount": 1
	public int fansCount;

	// "commentCount": 0,
	// "coverUrl": "http://7xpj9s.com1.z0.glb.clouddn.com/60A6.tmp.png",
	// "createTime": "2016-01-24 22:22:21",
	// "description": "有感觉。。",
	// "headerUrl":
	// "http://7xpj9r.com1.z0.glb.clouddn.com/IOS_20160216230719VVUXBYUXWHKMUJDZQU.png",
	// "isLike": "0",
	// "lat": 40.0782174221,
	// "likeCount": 1,
	// "lng": 116.3730322395,
	// "memberId": "aa860ea1c62c4f04b8cbb03a32aee89b",
	// "nickname": "NO",
	// "sex": "1",
	// "topicId": "",
	// "topicName": "",
	// "videoId": "kannzz6740091803852543sad",
	// "videoLength": 281,
	// "videoUrl": "http://7xpj9q.media1.z0.glb.clouddn.com/Party_Favors.mp4"

	public String lat;
	public String lng;
	public int postion;

	@Override
	public String toString() {
		return "UserExtendsInfo{" + "isAttention=" + isAttention + ", provinceId='" + provinceId + '\'' + ", birthday='"
				+ birthday + '\'' + ", sex='" + sex + '\'' + ", videoCount=" + videoCount + ", attentionCount="
				+ attentionCount + ", nickName='" + nickname + '\'' + ", cityId='" + cityId + '\'' + ", headerUrl='"
				+ headerUrl + '\'' + ", districtId='" + districtId + '\'' + ", memberId='" + memberId + '\''
				+ ", signature='" + signature + '\'' + ", lat=" + lat + ", lng=" + lng + '}';
	}

}
