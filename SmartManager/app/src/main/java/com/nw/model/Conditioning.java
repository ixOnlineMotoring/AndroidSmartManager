package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Conditioning
{

    private int identity;
    private String name;
    private String details;
    ArrayList<OptionsConditions> array_options;

    public int getIdentity()
    {
        return identity;
    }

    public void setIdentity(int Identity)
    {
        this.identity = Identity;
    }

    public String getname()
    {
        return name;
    }

    public void setname(String name)
    {
        this.name = name;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public ArrayList<OptionsConditions> getOptionsConditions()
    {
        return array_options;
    }

    public void setOptionsConditions(ArrayList<OptionsConditions> OptionsConditions)
    {
        this.array_options = OptionsConditions;
    }
}
