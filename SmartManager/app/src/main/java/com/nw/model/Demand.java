package com.nw.model;

import com.utils.Helper;

public class Demand
{
	private String ClientVariantLeadCount;

	private String ProvinceVariantSoldLeadCount;

	private String ClientVariantSoldLeadCountRanking;

	private String CityName;

	private String ClientModelSoldLeadCount;

	private String VariantName;

	private String NationalModelLeadCount;

	private String CityVariantLeadCount;

	private String NationalVariantLeadCountRanking;

	private String CityModelLeadCount;

	private String ProvinceModelSoldLeadCountRanking;

	private String NationalVariantSoldLeadCount;

	private String CityModelSoldLeadCount;

	private String CityVariantSoldLeadCount;

	private String ProvinceName;

	private String ProvinceVariantLeadCountRanking;

	private String ClientVariantLeadCountRanking;

	private String ProvinceModelLeadCountRanking;

	private String ClientModelLeadCountRanking;

	private String CityVariantSoldLeadCountRanking;

	private String NationalVariantSoldLeadCountRanking;

	private String CityVariantLeadCountRanking;

	private String NationalModelLeadCountRanking;

	private String NationalVariantLeadCount;

	private String ProvinceModelLeadCount;

	private String NationalModelSoldLeadCountRanking;

	private String ProvinceVariantSoldLeadCountRanking;

	private String ProvinceModelSoldLeadCount;

	private String CityModelLeadCountRanking;

	private String ProvinceVariantLeadCount;

	private String ModelName;

	private String ClientVariantSoldLeadCount;

	private String ClientName;

	private String ClientModelSoldLeadCountRanking;

	private String NationalModelSoldLeadCount;

	private String ClientModelLeadCount;

	private String CityModelSoldLeadCountRanking;

	public String getClientVariantLeadCount()
	{
		return ClientVariantLeadCount;
	}

	public void setClientVariantLeadCount(String ClientVariantLeadCount)
	{
		if (ClientVariantLeadCount.equals("anyType{}"))
			this.ClientVariantLeadCount = "Leads?";
		else
			this.ClientVariantLeadCount = ClientVariantLeadCount;
	}

	public String getProvinceVariantSoldLeadCount()
	{
		return ProvinceVariantSoldLeadCount;
	}

	public void setProvinceVariantSoldLeadCount(String ProvinceVariantSoldLeadCount)
	{
		if (ProvinceVariantSoldLeadCount.equals("anyType{}"))
			this.ProvinceVariantSoldLeadCount = "Sales?";
		else
			this.ProvinceVariantSoldLeadCount = ProvinceVariantSoldLeadCount;
	}

	public String getClientVariantSoldLeadCountRanking()
	{
		return ClientVariantSoldLeadCountRanking;
	}

	public void setClientVariantSoldLeadCountRanking(String ClientVariantSoldLeadCountRanking)
	{
		if (ClientVariantSoldLeadCountRanking.equals("anyType{}"))
			this.ClientVariantSoldLeadCountRanking = "Ranked?";
		else
			this.ClientVariantSoldLeadCountRanking = ClientVariantSoldLeadCountRanking;
	}

	public String getCityName()
	{
		return CityName;
	}

	public void setCityName(String CityName)
	{
		if (CityName.equals("anyType{}"))
			this.CityName = "City?";
		else
			this.CityName = CityName;
	}

	public String getClientModelSoldLeadCount()
	{
		return ClientModelSoldLeadCount;
	}

	public void setClientModelSoldLeadCount(String ClientModelSoldLeadCount)
	{
		if (ClientModelSoldLeadCount.equals("anyType{}"))
			this.ClientModelSoldLeadCount = "Sales?";
		else
			this.ClientModelSoldLeadCount = ClientModelSoldLeadCount;
	}

	public String getVariantName()
	{
		return VariantName;
	}

	public void setVariantName(String VariantName)
	{
		this.VariantName = VariantName;
	}

	public String getNationalModelLeadCount()
	{
		return NationalModelLeadCount;
	}

	public void setNationalModelLeadCount(String NationalModelLeadCount)
	{
		if (NationalModelLeadCount.equals("anyType{}"))
			this.NationalModelLeadCount = "leads?";
		else
			this.NationalModelLeadCount = NationalModelLeadCount;
	}

	public String getCityVariantLeadCount()
	{
		return CityVariantLeadCount;
	}

	public void setCityVariantLeadCount(String CityVariantLeadCount)
	{
		if (CityVariantLeadCount.equals("anyType{}"))
			this.CityVariantLeadCount = "Leads?";
		else
			this.CityVariantLeadCount = CityVariantLeadCount;
	}

	public String getNationalVariantLeadCountRanking()
	{
		return NationalVariantLeadCountRanking;
	}

	public void setNationalVariantLeadCountRanking(String NationalVariantLeadCountRanking)
	{
		if (NationalVariantLeadCountRanking.equals("anyType{}"))
			this.NationalVariantLeadCountRanking = "Ranked?";
		else
			this.NationalVariantLeadCountRanking = Helper.setPrefix(NationalVariantLeadCountRanking);
	}

	public String getCityModelLeadCount()
	{
		return CityModelLeadCount;
	}

	public void setCityModelLeadCount(String CityModelLeadCount)
	{
		if (CityModelLeadCount.equals("anyType{}"))
			this.CityModelLeadCount = "leads?";
		else
			this.CityModelLeadCount = CityModelLeadCount;
	}

	public String getProvinceModelSoldLeadCountRanking()
	{
		return ProvinceModelSoldLeadCountRanking;
	}

	public void setProvinceModelSoldLeadCountRanking(String ProvinceModelSoldLeadCountRanking)
	{
		if (ProvinceModelSoldLeadCountRanking.equals("anyType{}"))
			this.ProvinceModelSoldLeadCountRanking = "Ranked?";
		else
			this.ProvinceModelSoldLeadCountRanking =  Helper.setPrefix(ProvinceModelSoldLeadCountRanking);
	}

	public String getNationalVariantSoldLeadCount()
	{
		return NationalVariantSoldLeadCount;
	}

	public void setNationalVariantSoldLeadCount(String NationalVariantSoldLeadCount)
	{
		if (NationalVariantSoldLeadCount.equals("anyType{}"))
			this.NationalVariantSoldLeadCount = "Sales?";
		else
			this.NationalVariantSoldLeadCount = NationalVariantSoldLeadCount;
	}

	public String getCityModelSoldLeadCount()
	{
		return CityModelSoldLeadCount;
	}

	public void setCityModelSoldLeadCount(String CityModelSoldLeadCount)
	{
		if (CityModelSoldLeadCount.equals("anyType{}"))
			this.CityModelSoldLeadCount = "Sales?";
		else
			this.CityModelSoldLeadCount = CityModelSoldLeadCount;
	}

	public String getCityVariantSoldLeadCount()
	{
		return CityVariantSoldLeadCount;
	}

	public void setCityVariantSoldLeadCount(String CityVariantSoldLeadCount)
	{
		if (CityVariantSoldLeadCount.equals("anyType{}"))
			this.CityVariantSoldLeadCount = "Sales?";
		else
			this.CityVariantSoldLeadCount = CityVariantSoldLeadCount;
	}

	public String getProvinceName()
	{
		return ProvinceName;
	}

	public void setProvinceName(String ProvinceName)
	{
		if (ProvinceName.equals("anyType{}"))
			this.ProvinceName = "Province?";
		else
			this.ProvinceName = ProvinceName;
	}

	public String getProvinceVariantLeadCountRanking()
	{
		return ProvinceVariantLeadCountRanking;
	}

	public void setProvinceVariantLeadCountRanking(String ProvinceVariantLeadCountRanking)
	{
		if (ProvinceVariantLeadCountRanking.equals("anyType{}"))
			this.ProvinceVariantLeadCountRanking = "Ranked?";
		else
			this.ProvinceVariantLeadCountRanking = Helper.setPrefix(ProvinceVariantLeadCountRanking);
	}

	public String getClientVariantLeadCountRanking()
	{
		return ClientVariantLeadCountRanking;
	}

	public void setClientVariantLeadCountRanking(String ClientVariantLeadCountRanking)
	{
		if (ClientVariantLeadCountRanking.equals("anyType{}"))
			this.ClientVariantLeadCountRanking = "Ranked?";
		else
		{
			this.ClientVariantLeadCountRanking = Helper.setPrefix(ClientVariantLeadCountRanking);
		}
	}

	public String getProvinceModelLeadCountRanking()
	{
		return ProvinceModelLeadCountRanking;
	}

	public void setProvinceModelLeadCountRanking(String ProvinceModelLeadCountRanking)
	{

		if (ProvinceModelLeadCountRanking.equals("anyType{}"))
			this.ProvinceModelLeadCountRanking = "Ranked?";
		else
			this.ProvinceModelLeadCountRanking = Helper.setPrefix(ProvinceModelLeadCountRanking);
	}

	public String getClientModelLeadCountRanking()
	{
		return ClientModelLeadCountRanking;
	}

	public void setClientModelLeadCountRanking(String ClientModelLeadCountRanking)
	{
		if (ClientModelLeadCountRanking.equals("anyType{}"))
			this.ClientModelLeadCountRanking = "Ranked?";
		else
			this.ClientModelLeadCountRanking = Helper.setPrefix(ClientModelLeadCountRanking);
	}

	public String getCityVariantSoldLeadCountRanking()
	{
		return CityVariantSoldLeadCountRanking;
	}

	public void setCityVariantSoldLeadCountRanking(String CityVariantSoldLeadCountRanking)
	{
		if (CityVariantSoldLeadCountRanking.equals("anyType{}"))
			this.CityVariantSoldLeadCountRanking = "Ranked?";
		else
			this.CityVariantSoldLeadCountRanking = Helper.setPrefix(CityVariantSoldLeadCountRanking);
	}

	public String getNationalVariantSoldLeadCountRanking()
	{
		return NationalVariantSoldLeadCountRanking;
	}

	public void setNationalVariantSoldLeadCountRanking(String NationalVariantSoldLeadCountRanking)
	{
		if (NationalVariantSoldLeadCountRanking.equals("anyType{}"))
			this.NationalVariantSoldLeadCountRanking = "Ranked?";
		else
			this.NationalVariantSoldLeadCountRanking = Helper.setPrefix(NationalVariantSoldLeadCountRanking);
	}

	public String getCityVariantLeadCountRanking()
	{
		return CityVariantLeadCountRanking;
	}

	public void setCityVariantLeadCountRanking(String CityVariantLeadCountRanking)
	{
		if (CityVariantLeadCountRanking.equals("anyType{}"))
			this.CityVariantLeadCountRanking = "Ranked?";
		else
			this.CityVariantLeadCountRanking = Helper.setPrefix(CityVariantLeadCountRanking);
	}

	public String getNationalModelLeadCountRanking()
	{
		return NationalModelLeadCountRanking;
	}

	public void setNationalModelLeadCountRanking(String NationalModelLeadCountRanking)
	{
		if (NationalModelLeadCountRanking.equals("anyType{}"))
			this.NationalModelLeadCountRanking = "Ranked?";
		else
			this.NationalModelLeadCountRanking = Helper.setPrefix(NationalModelLeadCountRanking);
	}

	public String getNationalVariantLeadCount()
	{
		return NationalVariantLeadCount;
	}

	public void setNationalVariantLeadCount(String NationalVariantLeadCount)
	{
		if (NationalVariantLeadCount.equals("anyType{}"))
			this.NationalVariantLeadCount = "Leads?";
		else
			this.NationalVariantLeadCount = NationalVariantLeadCount;
	}

	public String getProvinceModelLeadCount()
	{
		return ProvinceModelLeadCount;
	}

	public void setProvinceModelLeadCount(String ProvinceModelLeadCount)
	{
		if (ProvinceModelLeadCount.equals("anyType{}"))
			this.ProvinceModelLeadCount = "Leads?";
		else
			this.ProvinceModelLeadCount = ProvinceModelLeadCount;
	}

	public String getNationalModelSoldLeadCountRanking()
	{
		return NationalModelSoldLeadCountRanking;
	}

	public void setNationalModelSoldLeadCountRanking(String NationalModelSoldLeadCountRanking)
	{
		if (NationalModelSoldLeadCountRanking.equals("anyType{}"))
			this.NationalModelSoldLeadCountRanking = "Ranked?";
		else
			this.NationalModelSoldLeadCountRanking =Helper.setPrefix(NationalModelSoldLeadCountRanking);
	}

	public String getProvinceVariantSoldLeadCountRanking()
	{
		return ProvinceVariantSoldLeadCountRanking;
	}

	public void setProvinceVariantSoldLeadCountRanking(String ProvinceVariantSoldLeadCountRanking)
	{
		if (ProvinceVariantSoldLeadCountRanking.equals("anyType{}"))
			this.ProvinceVariantSoldLeadCountRanking = "Ranked?";
		else
			this.ProvinceVariantSoldLeadCountRanking = Helper.setPrefix(ProvinceVariantSoldLeadCountRanking);
	}

	public String getProvinceModelSoldLeadCount()
	{
		return ProvinceModelSoldLeadCount;
	}

	public void setProvinceModelSoldLeadCount(String ProvinceModelSoldLeadCount)
	{
		if (ProvinceModelSoldLeadCount.equals("anyType{}"))
			this.ProvinceModelSoldLeadCount = "Sales?";
		else
			this.ProvinceModelSoldLeadCount = ProvinceModelSoldLeadCount;
	}

	public String getCityModelLeadCountRanking()
	{
		return CityModelLeadCountRanking;
	}

	public void setCityModelLeadCountRanking(String CityModelLeadCountRanking)
	{
		if (CityModelLeadCountRanking.equals("anyType{}"))
			this.CityModelLeadCountRanking = "Ranked?";
		else
			this.CityModelLeadCountRanking = Helper.setPrefix(CityModelLeadCountRanking);
	}

	public String getProvinceVariantLeadCount()
	{
		return ProvinceVariantLeadCount;
	}

	public void setProvinceVariantLeadCount(String ProvinceVariantLeadCount)
	{
		if (ProvinceVariantLeadCount.equals("anyType{}"))
			this.ProvinceVariantLeadCount = "Leads?";
		else
			this.ProvinceVariantLeadCount = ProvinceVariantLeadCount;
	}

	public String getModelName()
	{
		return ModelName;
	}

	public void setModelName(String ModelName)
	{
		this.ModelName = ModelName;
	}

	public String getClientVariantSoldLeadCount()
	{
		return ClientVariantSoldLeadCount;
	}

	public void setClientVariantSoldLeadCount(String ClientVariantSoldLeadCount)
	{
		if (ClientVariantSoldLeadCount.equals("anyType{}"))
			this.ClientVariantSoldLeadCount = "Sales?";
		else
			this.ClientVariantSoldLeadCount = ClientVariantSoldLeadCount;
	}

	public String getClientName()
	{
		return ClientName;
	}

	public void setClientName(String ClientName)
	{
		this.ClientName = ClientName;
	}

	public String getClientModelSoldLeadCountRanking()
	{
		return ClientModelSoldLeadCountRanking;
	}

	public void setClientModelSoldLeadCountRanking(String ClientModelSoldLeadCountRanking)
	{
		if (ClientModelSoldLeadCountRanking.equals("anyType{}"))
			this.ClientModelSoldLeadCountRanking = "Ranked?";
		else
			this.ClientModelSoldLeadCountRanking = Helper.setPrefix(ClientModelSoldLeadCountRanking);
	}

	public String getNationalModelSoldLeadCount()
	{
		return NationalModelSoldLeadCount;
	}

	public void setNationalModelSoldLeadCount(String NationalModelSoldLeadCount)
	{
		if (NationalModelSoldLeadCount.equals("anyType{}"))
			this.NationalModelSoldLeadCount = "Sales?";
		else
			this.NationalModelSoldLeadCount = NationalModelSoldLeadCount;
	}

	public String getClientModelLeadCount()
	{
		return ClientModelLeadCount;
	}

	public void setClientModelLeadCount(String ClientModelLeadCount)
	{
		if (ClientModelLeadCount.equals("anyType{}"))
			this.ClientModelLeadCount = "Leads?";
		else
			this.ClientModelLeadCount = ClientModelLeadCount;
	}

	public String getCityModelSoldLeadCountRanking()
	{
		return CityModelSoldLeadCountRanking;
	}

	public void setCityModelSoldLeadCountRanking(String CityModelSoldLeadCountRanking)
	{
		if (CityModelSoldLeadCountRanking.equals("anyType{}"))
			this.CityModelSoldLeadCountRanking = "Ranked?";
		else
			this.CityModelSoldLeadCountRanking = Helper.setPrefix(CityModelSoldLeadCountRanking);
	}

	@Override
	public String toString()
	{
		return "ClassPojo [ClientVariantLeadCount = " + ClientVariantLeadCount + ", ProvinceVariantSoldLeadCount = " + ProvinceVariantSoldLeadCount + ", ClientVariantSoldLeadCountRanking = "
				+ ClientVariantSoldLeadCountRanking + ", CityName = " + CityName + ", ClientModelSoldLeadCount = " + ClientModelSoldLeadCount + ", VariantName = " + VariantName
				+ ", NationalModelLeadCount = " + NationalModelLeadCount + ", CityVariantLeadCount = " + CityVariantLeadCount + ", NationalVariantLeadCountRanking = "
				+ NationalVariantLeadCountRanking + ", CityModelLeadCount = " + CityModelLeadCount + ", ProvinceModelSoldLeadCountRanking = " + ProvinceModelSoldLeadCountRanking
				+ ", NationalVariantSoldLeadCount = " + NationalVariantSoldLeadCount + ", CityModelSoldLeadCount = " + CityModelSoldLeadCount + ", CityVariantSoldLeadCount = "
				+ CityVariantSoldLeadCount + ", ProvinceName = " + ProvinceName + ", ProvinceVariantLeadCountRanking = " + ProvinceVariantLeadCountRanking + ", ClientVariantLeadCountRanking = "
				+ ClientVariantLeadCountRanking + ", ProvinceModelLeadCountRanking = " + ProvinceModelLeadCountRanking + ", ClientModelLeadCountRanking = " + ClientModelLeadCountRanking
				+ ", CityVariantSoldLeadCountRanking = " + CityVariantSoldLeadCountRanking + ", NationalVariantSoldLeadCountRanking = " + NationalVariantSoldLeadCountRanking
				+ ", CityVariantLeadCountRanking = " + CityVariantLeadCountRanking + ", NationalModelLeadCountRanking = " + NationalModelLeadCountRanking + ", NationalVariantLeadCount = "
				+ NationalVariantLeadCount + ", ProvinceModelLeadCount = " + ProvinceModelLeadCount + ", NationalModelSoldLeadCountRanking = " + NationalModelSoldLeadCountRanking
				+ ", ProvinceVariantSoldLeadCountRanking = " + ProvinceVariantSoldLeadCountRanking + ", ProvinceModelSoldLeadCount = " + ProvinceModelSoldLeadCount + ", CityModelLeadCountRanking = "
				+ CityModelLeadCountRanking + ", ProvinceVariantLeadCount = " + ProvinceVariantLeadCount + ", ModelName = " + ModelName + ", ClientVariantSoldLeadCount = "
				+ ClientVariantSoldLeadCount + ", ClientName = " + ClientName + ", ClientModelSoldLeadCountRanking = " + ClientModelSoldLeadCountRanking + ", NationalModelSoldLeadCount = "
				+ NationalModelSoldLeadCount + ", ClientModelLeadCount = " + ClientModelLeadCount + ", CityModelSoldLeadCountRanking = " + CityModelSoldLeadCountRanking + "]";
	}
}
