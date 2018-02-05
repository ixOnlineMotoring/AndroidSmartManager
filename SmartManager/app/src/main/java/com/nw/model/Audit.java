
package com.nw.model;

import android.text.TextUtils;

public class Audit
{
	private int Completed;
	private int Matched;
	private String Age;
	private String Time;
	private String VIN;
	private String GEO;
	private String Model;
	private String Make;
	private String StockNumber;
	private String LicenseImagePath;
	private String VehicleImagePath;
	private String auditDate;
	private String auditedVehicles;
	private String year;
	private String color;
	private String mileageKM;
	private String RegNumber;
	private String daysRemaining;
	private String vehicleType;
	private float RetailPrice;
	private float TradePrice;

	public int getCompleted()
	{

		return Completed;
	}

	public void setCompleted(int completed)
	{

		Completed = completed;
	}

	public int getMatched()
	{

		return Matched;
	}

	public void setMatched(int matched)
	{

		Matched = matched;
	}
	public String getAge()
	{
		
		return Age;
	}
	
	public void setAge(String age)
	{
		
		Age = age;
	}

	public String getTime()
	{

		return Time;
	}

	public void setTime(String time)
	{

		if (TextUtils.isEmpty(time))
		{
			this.Time = "No Geo";
		} else
		{
			this.Time = time;
		}
	}

	public String getVIN()
	{

		return VIN;
	}

	public void setVIN(String vIN)
	{

		if (TextUtils.isEmpty(vIN))
		{
			VIN = "No Vin#";
		} else
		{
			VIN = vIN;
		}

	}

	public String getGEO()
	{

		return GEO;
	}

	public void setGEO(String gEO)
	{

		if (TextUtils.isEmpty(gEO))
		{
			GEO = "No Geo";
		} else
		{
			GEO = gEO;
		}
	}

	public String getModel()
	{

		return Model;
	}

	public void setModel(String model)
	{

		if (TextUtils.isEmpty(model))
		{
			Model = "Model?";
		} else
		{
			Model = model;
		}
	}

	public String getStockNumber() {
		return (StockNumber);
	}

	public void setStockNumber(String stockNumber) 
	{
		if(stockNumber.trim().equals("anyType{}")|| stockNumber.trim().equals("No Stock #")){
			StockNumber = "Stock code?";
		}else if(stockNumber.trim().contains("null") || stockNumber.trim().equals("")|| stockNumber.trim()==null || stockNumber.trim().contains("No Stock#")){
			StockNumber = "Stock code?";
		}else{
			StockNumber = stockNumber;
		}
	}

	public String getLicenseImagePath()
	{

		return LicenseImagePath;
	}

	public void setLicenseImagePath(String licenseImagePath)
	{

		LicenseImagePath = licenseImagePath;
	}

	public String getVehicleImagePath()
	{

		return VehicleImagePath;
	}

	public void setVehicleImagePath(String vehicleImagePath)
	{

		VehicleImagePath = vehicleImagePath;
	}

	public String getMake()
	{

		return Make;
	}

	public void setMake(String make)
	{

		if (TextUtils.isEmpty(make))
		{
			Make = "No Make";
		} else
		{

			Make = make;
		}
	}

	public String getAuditedVehicles()
	{

		return auditedVehicles;
	}

	public void setAuditedVehicles(String auditedVehicles)
	{

		this.auditedVehicles = auditedVehicles;
	}

	public String getAuditDate()
	{

		return auditDate;
	}

	public void setAuditDate(String auditDate)
	{

		if (TextUtils.isEmpty(auditDate))
		{
			this.auditDate = "No Date";
		} else
		{
			this.auditDate = auditDate;
		}
	}

	public String getColor()
	{

		return color;
	}

	public void setColor(String color)
	{
		if(color.equals("anyType{}")){
			this.color = "Colour?";
		}else if(color.contains("null") || color.equals("")|| color==null||color.equals("No color#")||color.equals("0")){
			this.color = "Colour?";
		}else{
			this.color = color;
		}
	}

	public String getYear()
	{

		return year;
	}

	public void setYear(String year)
	{

		if (TextUtils.isEmpty(year))
		{
			this.year = "No Year";
		} else
		{
			this.year = year;
		}
	}

	public String getMileageKM()
	{

		return mileageKM;
	}
	public String getRegNumber() {
		return RegNumber;
	}

	public void setRegNumber(String regNumber) {

		if(regNumber.equals("anyType{}")){
			RegNumber = "Reg?";
		}else if(regNumber.contains("null") || regNumber.equals("")|| regNumber==null||regNumber.equals("No Registration")||regNumber.equals("0")){
			RegNumber = "Reg?";
		}
		else{
			RegNumber = regNumber;
		}

	}


	public void setMileageKM(String mileageKM)
	{

		if (TextUtils.isEmpty(mileageKM))
		{
			this.mileageKM = "No Mileage";
		} else
		{
			this.mileageKM = mileageKM;
		}
	}

	public float getTradePrice()
	{

		return TradePrice;
	}

	public void setTradePrice(float tradePrice)
	{

		TradePrice = tradePrice;
	}

	public float getRetailPrice()
	{

		return RetailPrice;
	}

	public void setRetailPrice(float retailPrice)
	{

		RetailPrice = retailPrice;
	}

	public String getVehicleType()
	{

		return vehicleType;
	}

	public void setVehicleType(String vehicleType)
	{

		this.vehicleType = vehicleType;
	}

	public String getDaysRemaining()
	{

		return daysRemaining;
	}

	public void setDaysRemaining(String daysRemaining)
	{

		this.daysRemaining = daysRemaining;
	}

}
