package com.shizhong.view.ui.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yuliyan on 16/1/27.
 */
public class BannerContentBean implements Parcelable{
    //    content": {
//            "id": "",
	public String id;
    //            "url": "http://7xpj9v.com1.z0.glb.clouddn.com/firstTopic.html"
	public String url;

    protected BannerContentBean(Parcel in) {
        id = in.readString();
        url = in.readString();
    }

    public static final Creator<BannerContentBean> CREATOR = new Creator<BannerContentBean>() {
        @Override
        public BannerContentBean createFromParcel(Parcel in) {
            return new BannerContentBean(in);
        }

        @Override
        public BannerContentBean[] newArray(int size) {
            return new BannerContentBean[size];
        }
    };

    //},

    @Override
    public String toString() {
        return "BannerContentBean{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(url);
    }
}
