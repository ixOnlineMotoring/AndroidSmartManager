package com.nw.model;

import java.util.ArrayList;

public class StockAudit extends SmartObject
{

	String vin;
	String  name;
	String description;
	String time;
	String stockNumber;
	ArrayList<BaseImage>images;
	
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getStockNumber() {
		return stockNumber;
	}
	public void setStockNumber(String stockNumber) {
		this.stockNumber = stockNumber;
	}
	public ArrayList<BaseImage> getImages() {
		return images;
	}
	public void setImages(ArrayList<BaseImage> images) {
		this.images = images;
	}
	
}
