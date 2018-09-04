package com.shizhong.view.ui.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "message_list")
public class MessageInfoExtraBean {

	public int position;
	@Column(name = "id", isId = true)
	public int id;
	// message
	@Column(name = "message")
	public String message;
	// extras
	@Column(name = "extras")
	public String extras;
	@Column(name = "isFollow")
	public boolean isFollow;// 是否关注
	@Column(name = "isRead")
	public boolean isRead;// 已读 1 未读 0
	// {"toId":"05f3ca958c7c4ad69ba28f78d5341fb6",
	@Column(name = "toId")
	public String toId;

	@Column(name = "type")
	public String type;
	// "description":"",
	@Column(name = "description")
	public String description;
	// "toNickname":"霍营小区门口",
	@Column(name = "toNickname")
	public String toNickname;
	// "operateTime":"2016-021403:10:24",
	@Column(name = "operateTime")
	public String operateTime;
	// "targetType":"3",
	@Column(name = "targetType")
	public String targetType;
	// “content"{\"id\":\"098978686823mlklsdada\",\"coverUrl\":\"http:\/\/7xpj9s.com1.z0.glb.clouddn.com\/4EE5.tmp.png\",\"commentType\":\"0\"}",
	@Column(name = "content")
	public String content;
	// "fromNickname":"脆骨华实KTV",
	@Column(name = "fromNickname")
	public String fromNickname;
	// "fromId":"fb74bcff697a464eab49187d58864299",
	@Column(name = "fromId")
	public String fromId;
	// "toHeader":"http:\/\/7xpj9r.com1.z0.glb.clouddn.com\/IOS_20160122005753YDZCTPJPDOTZHONRTQ.png",
	@Column(name = "toHeader")
	public String toHeader;
	// "fromHeader":"http:\/\/7xpj9r.com1.z0.glb.clouddn.com\/IOS_20160106185321PFWPLGOLQNITAQHCGR.png"}
	@Column(name = "fromHeader")
	public String fromHeader;

	public String videoId;// 视频ID
	public String videoConver;// 视频封面

	public String clubId;// 舞社id

	public String newId;// 资讯ID
	public String newUrl;// 资讯URL

	public String topId;// 话题ID

	public String recommendPersonGender;// 推荐人的性别
	public String recommendName;// 推荐人姓名
	public String recommendReason;// 推荐人原因

	@Override
	public String toString() {
		return "MessageInfoExtraBean [id=" + id + ", message=" + message + ", extras=" + extras + ", isFollow="
				+ isFollow + ", isRead=" + isRead + ", toId=" + toId + ", type=" + type + ", description=" + description
				+ ", toNickname=" + toNickname + ", operateTime=" + operateTime + ", targetType=" + targetType
				+ ", content=" + content + ", fromNickname=" + fromNickname + ", fromId=" + fromId + ", toHeader="
				+ toHeader + ", fromHeader=" + fromHeader + ", videoId=" + videoId + ", videoConver=" + videoConver
				+ ", clubId=" + clubId + ", newId=" + newId + ", newUrl=" + newUrl + ", topId=" + topId + "]";
	}

}
