package com.nw.model;

import android.os.Parcel;

public class Client extends SmartObject
{
	boolean checkIn=false;
	
	public Client() 
	{
		super();
	}
	
	public Client(int id, String name) 
	{
		super(id, name);
	}
	
	public Client(Parcel in) 
	{
		super(in);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.name;
	}
	
	public boolean isCheckIn() {
		return checkIn;
	}
	
	public void setCheckIn(boolean checkIn) {
		this.checkIn = checkIn;
	}
	
}
