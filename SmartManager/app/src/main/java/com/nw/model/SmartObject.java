package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SmartObject implements Parcelable
{
    int id;
    String ID;
    String name;
    String type;
    String ranking;

    boolean isChecked;

    public SmartObject()
    {
    }

    public SmartObject(int id, String name)
    {
        super();
        this.id = id;
        this.name = name;
    }

    public SmartObject(int id, String name, String type)
    {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public SmartObject(int id, String name, String type, String ranking)
    {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.ranking = ranking;
    }

    public SmartObject(Parcel in)
    {
        id = in.readInt();
        name = in.readString();
        type = in.readString();
        isChecked = in.readByte() != 0;
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String iD)
    {
        ID = iD;
    }

    public String getRanking()
    {
        return ranking;
    }

    public void setRanking(String ranking)
    {
        this.ranking = ranking;
    }

    public boolean isChecked()
    {
        return isChecked;
    }

    public void setChecked(boolean isChecked)
    {
        this.isChecked = isChecked;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        if (name.equals("anyType{}"))
            this.name = "N/A";
        else
            this.name = name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        if (type.equals("anyType{}"))
            this.type = "N/A";
        else
            this.type = type;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }


    public static final Parcelable.Creator<SmartObject> CREATOR = new Parcelable.Creator<SmartObject>()
    {
        @Override
        public SmartObject createFromParcel(Parcel in)
        {
            return new SmartObject(in);
        }

        @Override
        public SmartObject[] newArray(int size)
        {
            return new SmartObject[size];
        }
    };
}
