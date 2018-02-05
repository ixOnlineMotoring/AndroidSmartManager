package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Variant implements Parcelable
{
    int variantId;
    String meadCode;
    String friendlyName;
    String variantName;
    float price;
    String details;
    String year;
    String minYear;
    String maxYear;
    private boolean EBrochureFlag;

    public Variant()
    {
    }

    public Variant(int variantId, String meadCode, String friendlyName, String variantName)
    {
        super();
        this.variantId = variantId;
        this.meadCode = meadCode;
        this.friendlyName = friendlyName;
        this.variantName = variantName;
    }


    public Variant(int variantId, String meadCode, String friendlyName, String variantName, String minYear, String maxYear)
    {
        super();
        this.variantId = variantId;
        this.meadCode = meadCode;
        this.friendlyName = friendlyName;
        this.variantName = variantName;
        this.minYear = minYear;
        this.maxYear = maxYear;

    }

    public Variant(int variantId, String meadCode, String friendlyName, String variantName, float price, String minYear, String maxYear,boolean EBrochureFlag)
    {
        super();
        this.variantId = variantId;
        this.meadCode = meadCode;
        this.friendlyName = friendlyName;
        this.variantName = variantName;
        this.price = price;
        this.minYear = minYear;
        this.maxYear = maxYear;
        this.EBrochureFlag = EBrochureFlag;
    }

    public Variant(Parcel in)
    {
        variantId = in.readInt();
        meadCode = in.readString();
        friendlyName = in.readString();
        variantName = in.readString();
        price = in.readFloat();
        details = in.readString();
        year = in.readString();
        minYear = in.readString();
        maxYear = in.readString();

    }

    public boolean isEBrochureFlag()
    {

        return EBrochureFlag;
    }

    public void setEBrochureFlag(boolean eBrochureFlag)
    {

        EBrochureFlag = eBrochureFlag;
    }

    public int getVariantId()
    {
        return variantId;
    }

    public void setVariantId(int variantId)
    {
        this.variantId = variantId;
    }

    public String getMeadCode()
    {
        return meadCode;
    }

    public void setMeadCode(String meadCode)
    {
        this.meadCode = meadCode;
    }

    public String getFriendlyName()
    {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }

    public String getVariantName()
    {
        return variantName;
    }

    public void setVariantName(String variantName)
    {
        this.variantName = variantName;
    }

    public float getPrice()
    {
        return price;
    }

    public void setPrice(float price)
    {
        this.price = price;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

    public String getMinYear()
    {
        return minYear;
    }

    public void setMinYear(String minYear)
    {
        this.minYear = minYear;
    }

    public String getMaxYear()
    {
        return maxYear;
    }

    public void setMaxYear(String maxYear)
    {
        this.maxYear = maxYear;
    }

    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return friendlyName;
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
        dest.writeInt(variantId);
        dest.writeString(meadCode);
        dest.writeString(friendlyName);
        dest.writeString(variantName);
        dest.writeFloat(price);
        dest.writeString(details);
        dest.writeString(year);
        dest.writeString(minYear);
        dest.writeString(maxYear);

    }

    public static final Creator<Variant> CREATOR = new Creator<Variant>()
    {
        @Override
        public Variant createFromParcel(Parcel in)
        {
            return new Variant(in);
        }

        @Override
        public Variant[] newArray(int size)
        {
            return new Variant[size];
        }
    };
}
