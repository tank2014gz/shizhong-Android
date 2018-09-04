package com.shizhong.view.ui.bean;

public class MessageTypeListItem {

	public int iconId;
	public String iconName;
	public String type;
	public int hasNoRead;// 0:未读 1:已读
	public int position;

	@Override
	public String toString() {
		return "MessageTypeListItem [iconId=" + iconId + ", iconName=" + iconName + ", type=" + type + ", hasNoRead="
				+ hasNoRead + "]";
	}

}
