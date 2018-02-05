package com.nw.model;

public class AverageDays {

	String variantName;
	int clientAverageDays;
	int clientTotalStockMovements;
	int cityAverageDays;
	int cityTotalStockMovements=37; 
	int nationalAverageDays;
	int nationalTotalStockMovements;
	public AverageDays()
	{
		
	}
	public String getVariantName() {
		return variantName;
	}
	public void setVariantName(String variantName) {
		this.variantName = variantName;
	}
	public int getClientAverageDays() {
		return clientAverageDays;
	}
	public void setClientAverageDays(int clientAverageDays) {
		this.clientAverageDays = clientAverageDays;
	}
	public int getClientTotalStockMovements() {
		return clientTotalStockMovements;
	}
	public void setClientTotalStockMovements(int clientTotalStockMovements) {
		this.clientTotalStockMovements = clientTotalStockMovements;
	}
	public int getCityAverageDays() {
		return cityAverageDays;
	}
	public void setCityAverageDays(int cityAverageDays) {
		this.cityAverageDays = cityAverageDays;
	}
	public int getCityTotalStockMovements() {
		return cityTotalStockMovements;
	}
	public void setCityTotalStockMovements(int cityTotalStockMovements) {
		this.cityTotalStockMovements = cityTotalStockMovements;
	}
	public int getNationalAverageDays() {
		return nationalAverageDays;
	}
	public void setNationalAverageDays(int nationalAverageDays) {
		this.nationalAverageDays = nationalAverageDays;
	}
	public int getNationalTotalStockMovements() {
		return nationalTotalStockMovements;
	}
	public void setNationalTotalStockMovements(int nationalTotalStockMovements) {
		this.nationalTotalStockMovements = nationalTotalStockMovements;
	}
	
}
