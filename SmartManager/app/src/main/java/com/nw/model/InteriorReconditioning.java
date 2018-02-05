package com.nw.model;

public class InteriorReconditioning
{
    private String InteriorReconditioningValueID;
    private String ReconditioningTypeID;
    private String ReconditioningType;
    private String CustomType;
    private int Value;
    private boolean IsExtraValue;

    private boolean IsActive;


    public boolean getExtraValue()
    {
        return IsExtraValue;
    }

    public void setExtraValue(boolean extraValue)
    {
        IsExtraValue = extraValue;
    }


    public String getInteriorReconditioningValueID()
    {
        return InteriorReconditioningValueID;
    }

    public void setInteriorReconditioningValueID(String interiorReconditioningValueID)
    {
        InteriorReconditioningValueID = interiorReconditioningValueID;
    }

    public String getReconditioningTypeID()
    {
        return ReconditioningTypeID;
    }

    public void setReconditioningTypeID(String reconditioningTypeID)
    {
        ReconditioningTypeID = reconditioningTypeID;
    }

    public String getReconditioningType()
    {
        return ReconditioningType;
    }

    public void setReconditioningType(String reconditioningType)
    {
        if (reconditioningType.trim().equals("anyType{}"))
        {
            ReconditioningType = "";
        } else
        {
            ReconditioningType = reconditioningType;
        }
    }

    public String getCustomType()
    {
        return CustomType;
    }

    public void setCustomType(String customType)
    {
        if (customType.trim().equals("anyType{}"))
        {
            CustomType = "";
        } else
        {
            CustomType = customType;
        }
    }

    public int getValue()
    {
        return Value;
    }

    public void setValue(int value)
    {
        Value = value;
    }

    public boolean isActive()
    {
        return IsActive;
    }

    public void setActive(boolean active)
    {
        IsActive = active;
    }
}
