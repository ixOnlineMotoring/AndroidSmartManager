package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MyImage extends BaseImage implements Parcelable
{
    private String Thumb;

    private String Full;

    public MyImage() {
		// TODO Auto-generated constructor stub
	}
    public MyImage(Parcel source) {
		// TODO Auto-generated constructor stub
    	readParcel(source);
    }
    

	public String getThumb ()
    {
        return Thumb;
    }

    public void setThumb (String Thumb)
    {
        this.Thumb = Thumb;
    }

    public String getFull ()
    {
        return Full;
    }

    public void setFull (String Full)
    {
        this.Full = Full;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		super.writeToParcel(dest, flags);
		dest.writeString(Full);
		dest.writeString(Thumb);
	}
	
	@Override
	public void readParcel(Parcel in){
		Full= in.readString();
		Thumb= in.readString();
    }
    
    public static final Creator<MyImage> CREATOR= new Creator<MyImage>() {

		@Override
		public MyImage createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new MyImage(source);
		}

		@Override
		public MyImage[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MyImage[size];
		}
	};
}