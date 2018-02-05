package com.nw.model;

public class YearYounger
{
	String VariantID, Year, VariantName, Transmission, FuelType;

	public String getVariantID()
	{
		return VariantID;
	}

	public void setVariantID(String variantID)
	{
		VariantID = variantID;
	}

	public String getYear()
	{
		return Year;
	}

	public void setYear(String year)
	{
		Year = year;
	}

	public String getVariantName()
	{
		return VariantName;
	}

	public void setVariantName(String variantName)
	{
		VariantName = variantName;
	}

	public String getTransmission()
	{
		return Transmission;
	}

	public void setTransmission(String transmission)
	{
		Transmission = transmission;
	}

	public String getFuelType()
	{
		return FuelType;
	}

	public void setFuelType(String fuelType)
	{
		FuelType = fuelType;
	}

	public YearYounger(String VariantID, String Year , String VariantName, String Transmission ,String FuelType)
	{
		super();
		this.VariantID = VariantID;
		this.Year = Year;
		this.VariantName = VariantName;
		this.Transmission = Transmission;
		this.FuelType = FuelType;
	}

}
