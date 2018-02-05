package com.nw.model;

/**
 * Created by Swapnil on 26-12-2016.
 */

public class OptionsConditions
{
    private int Identity;
    private String Value;
    private boolean Selected;

    public int getIdentity()
    {
        return Identity;
    }

    public void setIdentity(int identity)
    {
        Identity = identity;
    }

    public String getValue()
    {
        return Value;
    }

    public void setValue(String value)
    {
        Value = value;
    }

    public boolean isSelected()
    {
        return Selected;
    }

    public void setSelected(boolean selected)
    {
        Selected = selected;
    }
}
