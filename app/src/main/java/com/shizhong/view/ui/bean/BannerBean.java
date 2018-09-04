package com.shizhong.view.ui.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yuliyan on 16/1/27.
 */
public class BannerBean implements Parcelable{

    //    {
//        "bannerId": "sadjklasjdlka",
	public String bannerId;
    //            "content": {
	public BannerContentBean content;
    //        "coverUrl": "http://7xqh1v.com1.z0.glb.clouddn.com/3352058b67af244fe683658.jpg",
	public String coverUrl;
    //            "targetType": "5"
	public String targetType;

    protected BannerBean(Parcel in) {
        bannerId = in.readString();
        content = in.readParcelable(BannerContentBean.class.getClassLoader());
        coverUrl = in.readString();
        targetType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bannerId);
        dest.writeParcelable(content, flags);
        dest.writeString(coverUrl);
        dest.writeString(targetType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BannerBean> CREATOR = new Creator<BannerBean>() {
        @Override
        public BannerBean createFromParcel(Parcel in) {
            return new BannerBean(in);
        }

        @Override
        public BannerBean[] newArray(int size) {
            return new BannerBean[size];
        }
    };


    @Override
    public String toString() {
        return "BannerBean{" +
                "bannerId='" + bannerId + '\'' +
                ", content=" + content +
                ", coverUrl='" + coverUrl + '\'' +
                ", targetType='" + targetType + '\'' +
                '}';
    }
}
