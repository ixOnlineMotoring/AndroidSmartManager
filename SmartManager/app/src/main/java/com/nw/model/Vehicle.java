
package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Vehicle implements Parcelable
{
	private String Comments;
	private String FriendlyName;
	private int Count;
	private String Extras;
	private String Videos;
	private String department;
	private String VIN;
	private float MyHighestBid;
	private String Expires;
	private String Location;
	private float HightestBid;
	private float BuyNow;
	private int Mileage;
	private String RegNumber;
	private String TimeLeft;
	private String Colour;
	private int Year;
	private String MileageType;
	private float Increment;
	private int ID;
	private int OwnerID;
	private float MinBid;
	private float RetailPrice;
	private float TradePrice;
	private String StockNumber;
	private String OwnerName;
	ArrayList<MyImage> ImageList;

	private boolean isBuyNow;
	private boolean isTrade;

	private int numOfPhotos;
	private int numOfVideos;
	private int total;

	private boolean isBought = false;

	private boolean EBrochureFlag;
	private String internalNote;

	public String getInternalNote()
	{

		return internalNote;
	}

	public void setInternalNote(String internalNote)
	{

		this.internalNote = internalNote;
	}

	public boolean isEBrochureFlag()
	{

		return EBrochureFlag;
	}

	public void setEBrochureFlag(boolean eBrochureFlag)
	{

		EBrochureFlag = eBrochureFlag;
	}

	public Vehicle(Parcel source)
	{
		// TODO Auto-generated constructor stub
		readParcel(source);
	}

	public Vehicle()
	{

	}

	public int getTotal()
	{

		return total;
	}

	public void setTotal(int total)
	{

		this.total = total;
	}

	public boolean isBought()
	{

		return isBought;
	}

	public void setBought(boolean isBought)
	{

		this.isBought = isBought;
	}

	public int getNumOfPhotos()
	{

		return numOfPhotos;
	}

	public void setNumOfPhotos(int numOfPhotos)
	{

		this.numOfPhotos = numOfPhotos;
	}

	public int getNumOfVideos()
	{

		return numOfVideos;
	}

	public void setNumOfVideos(int numOfVideos)
	{

		this.numOfVideos = numOfVideos;
	}

	public boolean isBuyNow()
	{

		return isBuyNow;
	}

	public void setBuyNow(boolean isBuyNow)
	{

		this.isBuyNow = isBuyNow;
	}

	public String getComments()
	{

		return Comments;
	}

	public void setComments(String comments)
	{

		if (comments.equals("anyType{}") || comments.equals(" ")
				|| comments == null)
		{
			Comments = "No Comment(s)";
		} else
		{
			Comments = comments;
		}

	}

	public String getFriendlyName()
	{

		return FriendlyName;
	}

	public void setFriendlyName(String friendlyName)
	{

		if (friendlyName.equals("anyType{}") || friendlyName.equals("null"))
			FriendlyName = "No Vehicle Name #";
		else
			FriendlyName = friendlyName;
	}

	public int getCount()
	{

		return Count;
	}

	public void setCount(int count)
	{

		Count = count;
	}

	public String getExtras()
	{

		return Extras;
	}

	public void setExtras(String extras)
	{

		if (extras.equals("anyType{}") || extras.equals(" ") || extras == null)
		{
			Extras = "No Extra(s)";
		} else
		{
			Extras = extras;
		}
	}

	public String getVIN()
	{

		return VIN;
	}

	public void setVIN(String vIN)
	{

		if (vIN.equals("anyType{}") || vIN.contains("null"))
		{
			VIN = "VIN?";
		} else
		{
			VIN = vIN;
		}
	}

	public float getMyHighestBid()
	{

		return MyHighestBid;
	}

	public void setMyHighestBid(float myHighestBid)
	{

		MyHighestBid = myHighestBid;
	}

	public String getExpires()
	{

		return Expires;
	}

	public void setExpires(String expires)
	{

		Expires = expires;
	}

	public String getLocation()
	{

		return Location;
	}

	public void setLocation(String location)
	{

		if (location.equals("anyType{}"))
		{
			Location = "Suburb/City";
		} else
		{
			Location = location;
		}
	}

	public float getHightestBid()
	{

		return HightestBid;
	}

	public void setHightestBid(float hightestBid)
	{

		HightestBid = hightestBid;
	}

	public float getBuyNow()
	{

		return BuyNow;
	}

	public void setBuyNow(float buyNow)
	{

		BuyNow = buyNow;
	}

	public int getMileage()
	{

		return Mileage;
	}

	public void setMileage(int mileage)
	{

		Mileage = mileage;
	}

	public String getRegNumber()
	{

		return RegNumber;
	}

	public void setRegNumber(String regNumber)
	{

		if (regNumber.equals("anyType{}"))
		{
			RegNumber = "Reg?";
		} else if (regNumber.contains("null") || regNumber.equals("")
				|| regNumber == null || regNumber.equals("No Registration")
				|| regNumber.equals("0"))
		{
			RegNumber = "Reg?";
		} else
		{
			RegNumber = regNumber;
		}

	}

	public String getTimeLeft()
	{

		return TimeLeft;
	}

	public void setTimeLeft(String timeLeft)
	{

		TimeLeft = timeLeft;
	}

	public String getColour()
	{

		return Colour;
	}

	public void setColour(String colour)
	{

		if (colour.trim().equals("anyType{}"))
		{
			Colour = "Colour?";
		} else if (colour.trim().contains("null") || colour.trim().equals("")
				|| colour.trim() == null
				|| colour.trim().equalsIgnoreCase("No colour #"))
		{
			Colour = "Colour?";
		} else
		{
			Colour = colour;
		}
	}

	public int getYear()
	{

		return Year;
	}

	public void setYear(int year)
	{

		Year = year;
	}

	public String getMileageType()
	{

		return MileageType;
	}

	public void setMileageType(String mileageType)
	{

		MileageType = mileageType;
	}

	public float getIncrement()
	{

		return Increment;
	}

	public void setIncrement(float increment)
	{

		Increment = increment;
	}

	public int getID()
	{

		return ID;
	}

	public void setID(int iD)
	{

		ID = iD;
	}

	public int getOwnerID()
	{

		return OwnerID;
	}

	public void setOwnerID(int ownerID)
	{

		OwnerID = ownerID;
	}

	public float getMinBid()
	{

		return MinBid;
	}

	public void setMinBid(float minBid)
	{

		MinBid = minBid;
	}

	public float getRetailPrice()
	{

		return RetailPrice;
	}

	public void setRetailPrice(float retailPrice)
	{

		RetailPrice = retailPrice;
	}

	public String getStockNumber()
	{

		return (StockNumber);
	}

	public void setStockNumber(String stockNumber)
	{

		if (stockNumber.trim().equals("anyType{}")
				|| stockNumber.trim().equals("No Stock #"))
		{
			StockNumber = "Stock code?";
		} else if (stockNumber.trim().contains("null")
				|| stockNumber.trim().equals("") || stockNumber.trim() == null
				|| stockNumber.trim().contains("No Stock#"))
		{
			StockNumber = "Stock code?";
		} else
		{
			StockNumber = stockNumber;
		}
	}

	public String getOwnerName()
	{

		return OwnerName;
	}

	public void setOwnerName(String ownerName)
	{

		OwnerName = ownerName;
	}

	public ArrayList<MyImage> getImageList()
	{

		return ImageList;
	}

	public void setImageList(ArrayList<MyImage> imageList)
	{

		ImageList = imageList;
	}

	@Override
	public int describeContents()
	{

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{

		// TODO Auto-generated method stub
		dest.writeInt(ID);
		dest.writeInt(OwnerID);
		dest.writeString(OwnerName);
		dest.writeInt(Year);
		dest.writeString(FriendlyName);
		dest.writeInt(Mileage);
		dest.writeString(MileageType);
		dest.writeString(Colour);
		dest.writeString(department);
		dest.writeString(Location);
		dest.writeFloat(RetailPrice);
		dest.writeFloat(TradePrice);
		dest.writeInt(Count);
		dest.writeString(Expires);
		dest.writeString(TimeLeft);
		dest.writeFloat(BuyNow);
		dest.writeFloat(MinBid);
		dest.writeFloat(MyHighestBid);
		dest.writeFloat(HightestBid);
		dest.writeFloat(Increment);
		dest.writeString(StockNumber);
		dest.writeString(RegNumber);
		dest.writeString(VIN);
		dest.writeString(Comments);
		dest.writeString(Extras);
		dest.writeString(Videos);
		if (ImageList == null)
			ImageList = new ArrayList<MyImage>();
		dest.writeTypedList(ImageList);
		dest.writeByte((byte) (isBuyNow ? 1 : 0));
		dest.writeByte((byte) (isTrade ? 1 : 0));
		dest.writeInt(numOfPhotos);
		dest.writeInt(numOfVideos);
		dest.writeByte((byte) (isBought ? 1 : 0));
		dest.writeInt(total);
	}

	private void readParcel(Parcel in)
	{

		ID = in.readInt();
		OwnerID = in.readInt();
		OwnerName = in.readString();
		Year = in.readInt();
		FriendlyName = in.readString();
		Mileage = in.readInt();
		MileageType = in.readString();
		department = in.readString();
		Colour = in.readString();
		Location = in.readString();
		RetailPrice = in.readFloat();
		Count = in.readInt();
		Expires = in.readString();
		TimeLeft = in.readString();
		BuyNow = in.readFloat();
		MinBid = in.readFloat();
		MyHighestBid = in.readFloat();
		HightestBid = in.readFloat();
		TradePrice = in.readFloat();
		Increment = in.readFloat();
		StockNumber = in.readString();
		RegNumber = in.readString();
		VIN = in.readString();
		Comments = in.readString();
		Videos = in.readString();
		Extras = in.readString();
		ImageList = new ArrayList<MyImage>();
		in.readTypedList(ImageList, MyImage.CREATOR);
		isBuyNow = in.readByte() != 0;
		isTrade = in.readByte() != 0;
		numOfPhotos = in.readInt();
		numOfVideos = in.readInt();
		isBought = in.readByte() != 0;
		total = in.readInt();
	}

	public float getTradePrice()
	{

		return TradePrice;
	}

	public void setTradePrice(float tradePrice)
	{

		TradePrice = tradePrice;
	}

	public String getDepartment()
	{

		return department;
	}

	public void setDepartment(String department)
	{

		if (department.trim().equals("anyType{}"))
		{
			this.department = "Type?";
		} else if (department.trim().contains("null")
				|| department.trim().equals("") || department.trim() == null
				|| department.trim().equalsIgnoreCase("No type"))
		{
			this.department = "Type?";
		} else
		{
			this.department = department;
		}
	}

	public String getVideos()
	{

		return Videos;
	}

	public void setVideos(String videos)
	{

		Videos = videos;
	}

	public boolean isTrade()
	{

		return isTrade;
	}

	public void setTrade(boolean isTrade)
	{

		this.isTrade = isTrade;
	}

	public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>()
	{

		@Override
		public Vehicle createFromParcel(Parcel source)
		{

			// TODO Auto-generated method stub
			return new Vehicle(source);
		}

		@Override
		public Vehicle[] newArray(int size)
		{

			// TODO Auto-generated method stub
			return new Vehicle[size];
		}
	};
}