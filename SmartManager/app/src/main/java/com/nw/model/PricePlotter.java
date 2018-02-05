package com.nw.model;

public class PricePlotter {

	float value;
	String date;
	public PricePlotter(float value,String date) {
		this.value=value;
		this.date=date;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
