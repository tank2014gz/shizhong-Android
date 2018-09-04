package com.shizhong.view.ui.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yuliyan on 16/1/25.
 */
public class DanceList implements Parcelable {
	public int code;
	public List<DanceClass> data;

    protected DanceList(Parcel in) {
        code = in.readInt();
        data = in.createTypedArrayList(DanceClass.CREATOR);
    }

    public static final Creator<DanceList> CREATOR = new Creator<DanceList>() {
        @Override
        public DanceList createFromParcel(Parcel in) {
            return new DanceList(in);
        }

        @Override
        public DanceList[] newArray(int size) {
            return new DanceList[size];
        }
    };


    @Override
    public String toString() {
        return "DanceList{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(code);
        parcel.writeTypedList(data);
    }
}
