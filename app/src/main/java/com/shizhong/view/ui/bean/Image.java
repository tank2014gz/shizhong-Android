package com.shizhong.view.ui.bean;


/**
 * Created by yuliyan on 15/12/29.
 */
public class Image {
	public String localPath;
	public String thumbPath;
	public boolean isSelected;

	@Override
	public String toString() {
		return "Image{" + "localPath='" + localPath + '\'' + ", thumbPath='" + thumbPath + '\'' + ", isSelected="
				+ isSelected + '}';
	}

}
