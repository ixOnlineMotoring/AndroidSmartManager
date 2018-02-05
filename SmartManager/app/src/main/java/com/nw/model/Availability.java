package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Availability implements Parcelable {
	public Availability()
	{
		super();
	}

	String VariantName;
	int ClientAvailability;
	int GroupAvailability;
    int ProvinceAvailability;
    int NationalAvailability;

    public String getVariantName()
	{
	
		return VariantName;
	}

	public void setVariantName(String variantName)
	{
	
		VariantName = variantName;
	}

	public int getClientAvailability()
	{
	
		return ClientAvailability;
	}

	public void setClientAvailability(int clientAvailability)
	{
	
		ClientAvailability = clientAvailability;
	}

	public int getGroupAvailability()
	{
	
		return GroupAvailability;
	}

	public void setGroupAvailability(int groupAvailability)
	{
	
		GroupAvailability = groupAvailability;
	}

	public int getProvinceAvailability()
	{
	
		return ProvinceAvailability;
	}

	public void setProvinceAvailability(int provinceAvailability)
	{
	
		ProvinceAvailability = provinceAvailability;
	}

	public int getNationalAvailability()
	{
	
		return NationalAvailability;
	}

	public void setNationalAvailability(int nationalAvailability)
	{
	
		NationalAvailability = nationalAvailability;
	}
    

    protected Availability(Parcel in) {
        VariantName = in.readString();
        ClientAvailability = in.readInt();
        GroupAvailability = in.readInt();
        ProvinceAvailability = in.readInt();
        NationalAvailability = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(VariantName);
        dest.writeInt(ClientAvailability);
        dest.writeInt(GroupAvailability);
        dest.writeInt(ProvinceAvailability);
        dest.writeInt(NationalAvailability);
    }

    public static final Creator<Availability> CREATOR = new Creator<Availability>() {
        @Override
        public Availability createFromParcel(Parcel in) {
            return new Availability(in);
        }

        @Override
        public Availability[] newArray(int size) {
            return new Availability[size];
        }
    };
}
