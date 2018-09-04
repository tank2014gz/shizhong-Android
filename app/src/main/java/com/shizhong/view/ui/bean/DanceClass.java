package com.shizhong.view.ui.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yuliyan on 15/12/26.
 */
public class DanceClass  implements Parcelable{
	public int position;
	public boolean isSelected;
	public String categoryId;
	public String categoryName;
	public String fileUrl;

    public  DanceClass(){
        super();
    }
    protected DanceClass(Parcel in) {
        position = in.readInt();
        isSelected = in.readByte() != 0;
        categoryId = in.readString();
        categoryName = in.readString();
        fileUrl = in.readString();
    }

    public static final Creator<DanceClass> CREATOR = new Creator<DanceClass>() {
        @Override
        public DanceClass createFromParcel(Parcel in) {
            return new DanceClass(in);
        }

        @Override
        public DanceClass[] newArray(int size) {
            return new DanceClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(position);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeString(categoryId);
        parcel.writeString(categoryName);
        parcel.writeString(fileUrl);
    }


    @Override
    public String toString() {
        return "DanceClass{" +
                "position=" + position +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
