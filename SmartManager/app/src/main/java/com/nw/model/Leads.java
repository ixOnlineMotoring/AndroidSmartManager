package com.nw.model;

public class Leads
{
	
	
	
	int id;
	String username;
	String vehicleDescription;
	String phoneNumber;
	String userEmail;
	String daysLeft;
	String seenDate;

	String headerTitle;
	public static final int ITEM = 0;
	public static final int SECTION = 1;

	int type;
	// vehicle property
	VehicleType vehicleType;
	boolean matched = false;
	String makeAsked;
	String modelAsked;
	String yearAsked;
	String mileageAsked;
	String colourAsked;
	String priceAsked;

	int count = 0;

	// lead details property
	String homeNumber;
	String workNumber;
	String source;
	String submitted;

	String lastUpdate;
	String lastUpdateDate;
	String tradeIn;
	String variant;
	String friendlyName;

	boolean loadMore = false;

	// vehilcle type used
	String color;
	String year;
	String stockCode;
	String milage;
	String mmCode;
	String usedVehicleStockID;
	String LogimeterNumber;
	
	
	// updated activity
	String date;
	String activity;
	String user;
	
	String makdetradeIn;
	String modeltradeIn;
	String yeartradeIn;
	String mileagetradeIn;
	

	public String getMakdetradeIn()
	{
	
		return makdetradeIn;
	}

	public void setMakdetradeIn(String makdetradeIn)
	{
		if (makdetradeIn.equals("Unknown"))
		{
			this.makdetradeIn = "No Name Loaded";
		}else {
			this.makdetradeIn = makdetradeIn;
		}
	}

	public String getModeltradeIn()
	{
	
		return modeltradeIn;
	}

	public void setModeltradeIn(String modeltradeIn)
	{
		if (modeltradeIn.equals("Unknown"))
		{
			this.modeltradeIn = "";
		}else {
			this.modeltradeIn = modeltradeIn;
		}
	}

	public String getYeartradeIn()
	{
	
		return yeartradeIn;
	}

	public void setYeartradeIn(String yeartradeIn)
	{
		if(yeartradeIn.equals("0")){
			this.yeartradeIn = "";
		}else {
			this.yeartradeIn = yeartradeIn;
		}
		
	}

	public String getMileagetradeIn()
	{
	
		return mileagetradeIn;
	}

	public void setMileagetradeIn(String mileagetradeIn)
	{
		if(mileagetradeIn.equals("0")){
			this.mileagetradeIn = "No Mileage Loaded";
		}else {
			this.mileagetradeIn = mileagetradeIn;
		}
	}

	public String getLogimeterNumber()
	{
	
		return LogimeterNumber;
	}

	public void setLogimeterNumber(String logimeterNumber)
	{
	
		LogimeterNumber = logimeterNumber;
	}
	
	public String getHeaderTitle()
	{
		return headerTitle;
	}

	public void setHeaderTitle(String headerTitle)
	{
		this.headerTitle = headerTitle;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getVehicleDescription()
	{
		return vehicleDescription;
	}

	public void setVehicleDescription(String vehicleDescription)
	{
		this.vehicleDescription = vehicleDescription;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public String getUserEmail()
	{
		return userEmail;
	}

	public void setUserEmail(String userEmail)
	{
		this.userEmail = userEmail;
	}

	public String getDaysLeft()
	{
		return daysLeft;
	}

	public void setDaysLeft(String daysLeft)
	{
		this.daysLeft = daysLeft;
	}

	public boolean isMatched()
	{
		return matched;
	}

	public void setMatched(boolean matched)
	{
		this.matched = matched;
	}

	public String getMakeAsked()
	{
		return makeAsked;
	}

	public void setMakeAsked(String makeAsked)
	{
		this.makeAsked = makeAsked;
	}

	public String getModelAsked()
	{
		return modelAsked;
	}

	public void setModelAsked(String modelAsked)
	{
		this.modelAsked = modelAsked;
	}

	public String getYearAsked()
	{
		return yearAsked;
	}

	public void setYearAsked(String yearAsked)
	{
		this.yearAsked = yearAsked;
	}

	public String getMileageAsked()
	{
		return mileageAsked;
	}

	public void setMileageAsked(String mileageAsked)
	{
		this.mileageAsked = mileageAsked;
	}

	public String getColourAsked()
	{
		return colourAsked;
	}

	public void setColourAsked(String colourAsked)
	{
		this.colourAsked = colourAsked;
	}

	public String getPriceAsked()
	{
		return priceAsked;
	}

	public void setPriceAsked(String priceAsked)
	{
		this.priceAsked = priceAsked;
	}

	public void setVehicleType(VehicleType vehicleType)
	{
		this.vehicleType = vehicleType;
	}

	public VehicleType getVehicleType()
	{
		return vehicleType;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public void setHomeNumber(String homeNumber)
	{
		this.homeNumber = homeNumber;
	}

	public String getHomeNumber()
	{
		return homeNumber;
	}

	public void setWorkNumber(String workNumber)
	{
		this.workNumber = workNumber;
	}

	public String getWorkNumber()
	{
		return workNumber;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getSubmitted()
	{
		return submitted;
	}

	public void setSubmitted(String submitted)
	{
		this.submitted = submitted;
	}

	public void setLastUpdate(String lastUpdate)
	{
		this.lastUpdate = lastUpdate;
	}

	public String getLastUpdate()
	{
		return lastUpdate;
	}

	public String getTradeIn()
	{
		return tradeIn;
	}

	public void setTradeIn(String tradeIn)
	{
		this.tradeIn = tradeIn;
	}

	public String getFriendlyName()
	{
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName)
	{
		if(friendlyName.equals("anyType{}")|| friendlyName.contains("null") || friendlyName.equals("")|| friendlyName==null||friendlyName.equals("Not Given")){
			this.friendlyName = "EnquiredOn?";
		}else {
			this.friendlyName = friendlyName;
		}
		
	}

	public String getVariant()
	{
		return variant;
	}

	public void setVariant(String variant)
	{
		this.variant = variant;
	}

	public void setLoadMore(boolean loadMore)
	{
		this.loadMore = loadMore;
	}

	public boolean isLoadMore()
	{
		return loadMore;
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	public String getYear()
	{
		return year;
	}

	public void setYear(String year)
	{
		this.year = year;
	}

	public String getStockCode()
	{
		return stockCode;
	}

	public void setStockCode(String stockCode)
	{
		this.stockCode = stockCode;
	}

	public String getMilage()
	{
		return milage;
	}

	public void setMilage(String milage)
	{
		this.milage = milage;
	}

	public String getMmCode()
	{
		return mmCode;
	}

	public void setMmCode(String mmCode)
	{
		this.mmCode = mmCode;
	}

	public String getUsedVehicleStockID()
	{
		return usedVehicleStockID;
	}
	
	public void setUsedVehicleStockID(String usedVehicleStockID)
	{
		this.usedVehicleStockID = usedVehicleStockID;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getActivity()
	{
		return activity;
	}

	public void setActivity(String activity)
	{
		this.activity = activity;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}
	
	public String getLastUpdateDate()
	{
		return lastUpdateDate;
	}
	
	public void setLastUpdateDate(String lastUpdateDate)
	{
		this.lastUpdateDate = lastUpdateDate;
	}
	
	
}
