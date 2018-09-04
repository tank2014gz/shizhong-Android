package com.shizhong.view.ui.bean;

public class ShareContentBean {
	public String shareTitle;
	public String shareContent;
	public String shareImage;
	public String shareUrl;
	public String shareVideo;
	public String video_id;
	public int app_icon_id;

	@Override
	public String toString() {
		return "ShareContentBean [shareTitle=" + shareTitle + ", shareContent=" + shareContent + ", shareImage="
				+ shareImage + ", shareUrl=" + shareUrl + ", shareVideo=" + shareVideo + ", video_id=" + video_id + "]";
	}

}
