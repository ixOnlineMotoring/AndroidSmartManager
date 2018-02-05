package com.nw.model;

public class Wanted {
	
	int wantedId;
	int variantId;
	String friendlyName;
	String yearRange;
	String proviences;
	int searchCount;
	
	int usedVehicleStockId;
	int modelId;
	int year;
	String location;
	int dealershipId;
	int mileage;
	String mileageType;
	float price;
	float tradePrice;
	String stockCode;
	String colour;
	String mdc;
	int imageId;
	String bidCloseDate;
	String tradeCloseDate;
	
	public int getUsedVehicleStockId() {
		return usedVehicleStockId;
	}
	public void setUsedVehicleStockId(int usedVehicleStockId) {
		this.usedVehicleStockId = usedVehicleStockId;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		if(location.equals("anyType{}"))
			this.location="Suburb/City";
		else
		    this.location = location;
	}
	public int getDealershipId() {
		return dealershipId;
	}
	public void setDealershipId(int dealershipId) {
		this.dealershipId = dealershipId;
	}
	public int getMileage() {
		return mileage;
	}
	public void setMileage(int mileage) {
		this.mileage = mileage;
	}
	public String getMileageType() {
		return mileageType;
	}
	public void setMileageType(String mileageType) {
		this.mileageType = mileageType;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getTradePrice() {
		return tradePrice;
	}
	public void setTradePrice(float tradePrice) {
		this.tradePrice = tradePrice;
	}
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	public String getMdc() {
		return mdc;
	}
	public void setMdc(String mdc) {
		this.mdc = mdc;
	}
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	public String getBidCloseDate() {
		return bidCloseDate;
	}
	public void setBidCloseDate(String bidCloseDate) {
		this.bidCloseDate = bidCloseDate;
	}
	public String getTradeCloseDate() {
		return tradeCloseDate;
	}
	public void setTradeCloseDate(String tradeCloseDate) {
		this.tradeCloseDate = tradeCloseDate;
	}
	public int getSearchCount() {
		return searchCount;
	}
	public void setSearchCount(int searchCount) {
		this.searchCount = searchCount;
	}
	public int getWantedId() {
		return wantedId;
	}
	public void setWantedId(int wantedId) {
		this.wantedId = wantedId;
	}
	public int getVariantId() {
		return variantId;
	}
	public void setVariantId(int variantId) {
		this.variantId = variantId;
	}
	public String getFriendlyName() {
		return friendlyName;
	}
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	public String getYearRange() {
		return yearRange;
	}
	public void setYearRange(String yearRange) {
		this.yearRange = yearRange;
	}
	public String getProviences() {
		return proviences;
	}
	public void setProviences(String proviences) {
		if(proviences.equals("anyType{}"))
			this.proviences = "";
		else
			this.proviences = proviences;
	}
	
	
}
