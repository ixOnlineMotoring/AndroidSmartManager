package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VehicleClass implements Parcelable {

	private String Code;
	private String VehicleRestriction;
	private String FirstIssueDate;
	
	
	public VehicleClass() {
		
	}
	
	public VehicleClass(Parcel parcel) {
		readParcel(parcel);
	} 
	public String getFirstIssueDate() {
		return FirstIssueDate;
	}
	public void setFirstIssueDate(String firstIssueDate) {
		FirstIssueDate = firstIssueDate;
	}
	public String getVehicleRestriction() {
		return VehicleRestriction;
	}
	public void setVehicleRestriction(String vehicleRestriction) {
		VehicleRestriction = vehicleRestriction;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(Code);
		dest.writeString(VehicleRestriction);
		dest.writeString(FirstIssueDate);
	}
	
	 private void readParcel(Parcel in)
	 {
		 Code = in.readString();
		 VehicleRestriction = in.readString();
		 FirstIssueDate = in.readString();
    }
        
    public static final Creator<VehicleClass> CREATOR= new Creator<VehicleClass>() {

		@Override
		public VehicleClass createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new VehicleClass(source);
		}

		@Override
		public VehicleClass[] newArray(int size) {
			// TODO Auto-generated method stub
			return new VehicleClass[size];
		}
	};
}
