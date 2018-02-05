package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SettingsUser implements Parcelable 
{
	private int ID;
	private int Days;
	private int MemberID,settingID,typeID,traderPeriod;
	private boolean TradeBuy, TradeSell, TenderAccept, TenderDecline, TenderManager, TenderAuditor,TenderAccess,AuctionAccess;
	private String MemberName,type;

	public SettingsUser()
		{

		}
	
	public int getID()
	{
		return ID;
	}

	public void setID(int ID)
	{
		this.ID = ID;
	}

	public int getDays()
	{
		return Days;
	}

	public void setDays(int Days)
	{
		this.Days = Days;
	}
	public int getMemberID()
	{
		return MemberID;
	}
	
	public void setMemberID(int MemberID)
	{
		this.MemberID = MemberID;
	}

	public boolean isTradeBuy()
	{
		return TradeBuy;
	}

	public void setTradeBuy(boolean TradeBuy)
	{
		this.TradeBuy = TradeBuy;
	}

	public boolean isTradeSell()
	{
		return TradeSell;
	}

	public void setTradeSell(boolean TradeSell)
	{
		this.TradeSell = TradeSell;
	}

	public boolean isTenderAccept()
	{
		return TenderAccept;
	}

	public void setTenderAccept(boolean TenderAccept)
	{
		this.TenderAccept = TenderAccept;
	}

	public boolean isTenderDecline()
	{
		return TenderDecline;
	}

	public void setTenderDecline(boolean TenderDecline)
	{
		this.TenderDecline = TenderDecline;
	}

	public boolean isTenderManager()
	{
		return TenderManager;
	}

	public void setTenderManager(boolean TenderManager)
	{
		this.TenderManager = TenderManager;
	}

	public boolean isTenderAuditor()
	{
		return TenderAuditor;
	}

	public void setTenderAuditor(boolean TenderAuditor)
	{
		this.TenderAuditor = TenderAuditor;
	}
	public String getMemberName()
	{
		return MemberName;
	}
	
	public void setMemberName(String MemberName)
	{
		this.MemberName = MemberName;
	}
	
	protected SettingsUser(Parcel in) 
	{
        ID = in.readInt();
        Days = in.readInt();
        MemberID = in.readInt();
        MemberName = in.readString();
        type = in.readString();
        traderPeriod= in.readInt();
        settingID= in.readInt();
        typeID = in.readInt();
        TradeBuy = in.readByte() != 0; 
        TradeSell = in.readByte() != 0; 
        TenderAccept = in.readByte() != 0; 
        TenderDecline = in.readByte() != 0; 
        TenderManager = in.readByte() != 0; 
        TenderAuditor = in.readByte() != 0; 
        TenderAccess = in.readByte() != 0; 
        AuctionAccess = in.readByte() != 0; 
    }

	@Override
    public int describeContents() {
        return 0;
    }

	
    @Override
    public void writeToParcel(Parcel dest, int flags) 
    {
        dest.writeInt(ID);
        dest.writeInt(Days);
        dest.writeInt(MemberID);
        dest.writeString(MemberName);
        dest.writeString(type);
        dest.writeInt(typeID);
        dest.writeInt(traderPeriod);
        dest.writeByte((byte) (TradeBuy ? 1 : 0)); 
        dest.writeByte((byte) (TradeSell ? 1 : 0)); 
        dest.writeByte((byte) (TenderAccept ? 1 : 0)); 
        dest.writeByte((byte) (TenderDecline ? 1 : 0)); 
        dest.writeByte((byte) (TenderManager ? 1 : 0)); 
        dest.writeByte((byte) (TenderAuditor ? 1 : 0)); 
        dest.writeByte((byte) (TenderAccess ? 1 : 0)); 
        dest.writeByte((byte) (AuctionAccess ? 1 : 0)); 
    }

    public int getSettingID()
	{

		return settingID;
	}

	public void setSettingID(int settingID)
	{

		this.settingID = settingID;
	}

	public int getTypeID()
	{

		return typeID;
	}

	public void setTypeID(int typeID)
	{

		this.typeID = typeID;
	}

	public int getTraderPeriod()
	{

		return traderPeriod;
	}

	public void setTraderPeriod(int traderPeriod)
	{

		this.traderPeriod = traderPeriod;
	}

	public boolean isTenderAccess()
	{

		return TenderAccess;
	}

	public void setTenderAccess(boolean tenderAccess)
	{

		TenderAccess = tenderAccess;
	}

	public boolean isAuctionAccess()
	{

		return AuctionAccess;
	}

	public void setAuctionAccess(boolean auctionAccess)
	{

		AuctionAccess = auctionAccess;
	}

	public String getType()
	{

		return type;
	}

	public void setType(String type)
	{

		this.type = type;
	}

    public static final Creator<SettingsUser> CREATOR = new Creator<SettingsUser>() {
        @Override
        public SettingsUser createFromParcel(Parcel in) {
            return new SettingsUser(in);
        }

        @Override
        public SettingsUser[] newArray(int size) {
            return new SettingsUser[size];
        }
    };

}
