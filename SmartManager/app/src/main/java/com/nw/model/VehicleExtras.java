package com.nw.model;

import java.util.ArrayList;

public class VehicleExtras
{
    private String ExtraID;
    private String Name;
    private int Price;
    private boolean isNewField;

    public boolean getNewField()
    {
        return isNewField;
    }

    public void setNewField(boolean newField)
    {
        isNewField = newField;
    }

    public String getExtraID()
    {
        return ExtraID;
    }

    public void setExtraID(String extraID)
    {
        ExtraID = extraID;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public int getPrice()
    {
        return Price;
    }

    public void setPrice(int price)
    {
        Price = price;
    }
}
