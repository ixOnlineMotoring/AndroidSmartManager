package com.nw.model;

import android.os.Parcel;

public class Model extends SmartObject {
	int minYear;
	int maxYear;

	public Model(int id, String name, int minYear, int maxYear) {
		super(id, name);
		this.minYear = minYear;
		this.maxYear = maxYear;
	}

	public Model(Parcel in) {
		super(in);
		minYear = in.readInt();
		maxYear = in.readInt();
	}

	public int getMinYear() {
		return minYear;
	}

	public void setMinYear(int minYear) {
		this.minYear = minYear;
	}

	public int getMaxYear() {
		return maxYear;
	}

	public void setMaxYear(int maxYear) {
		this.maxYear = maxYear;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(minYear);
		dest.writeInt(maxYear);
	}

	public static final Creator<Model> CREATOR = new Creator<Model>() {
		public Model createFromParcel(Parcel in) {
			return new Model(in);
		}

		public Model[] newArray(int size) {
			return new Model[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() 
	{
		return super.toString();
	}
}
