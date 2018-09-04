package com.shizhong.view.ui.bean;

import android.graphics.Bitmap;

public class VideoLocalBean {
	/** 视频id */
	public long _id;
	/** 视频名字 */
	public String name;
	/** 视频路径 */
	public String path;
	/** 视频时长 */
	public String durationDes;
	public long during;
	/** 视频宽 */
	public int width;
	/** 视频高 */
	public int height;
	/** 视频方向 */
	public int orientation;
	/** 视频修改时间 */
	public long modified;
	public Bitmap bitmap;

	@Override
	public String toString() {
		return "VideoLocalBean [_id=" + _id + ", name=" + name + ", path=" + path + ", duration=" + durationDes
				+ ", width=" + width + ", height=" + height + "]";
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof VideoLocalBean){
			return path.equals(((VideoLocalBean)o).path);
		}
		return false;
		
	}

	public VideoLocalBean() {
		super();
	}

}
