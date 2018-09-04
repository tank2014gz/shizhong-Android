package com.shizhong.view.ui.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yuliyan on 16/1/27.
 */
public class BannerDataPackage implements Parcelable{
	public List<BannerBean> data;
	public int code;

    protected BannerDataPackage(Parcel in) {
        data = in.createTypedArrayList(BannerBean.CREATOR);
        code = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
        dest.writeInt(code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BannerDataPackage> CREATOR = new Creator<BannerDataPackage>() {
        @Override
        public BannerDataPackage createFromParcel(Parcel in) {
            return new BannerDataPackage(in);
        }

        @Override
        public BannerDataPackage[] newArray(int size) {
            return new BannerDataPackage[size];
        }
    };


    @Override
    public String toString() {
        return "BannerDataPackage{" +
                "data=" + data +
                ", code=" + code +
                '}';
    }
}
