package com.nw.model;

/**
 * Created by Akshay on 16-06-2017.
 */

public class ExteriorReconditioning
{
    String ExteriorValueId;
    String ExteriorTypeId;
    String ExteriorPositionID;
    String ExteriorType;
    String Replace;
    String ReplaceValue;
    String Repair;
    String RepairValue;
    String Comments;
    int Value;
    boolean IsActive;
    boolean IsSelected;

    public String getExteriorPositionID()
    {
        return ExteriorPositionID;
    }

    public void setExteriorPositionID(String exteriorPositionID)
    {
        ExteriorPositionID = exteriorPositionID;
    }

    public String getExteriorType()
    {
        return ExteriorType;
    }

    public void setExteriorType(String exteriorType)
    {
        ExteriorType = exteriorType;
    }

    public String getExteriorValueId()
    {
        return ExteriorValueId;
    }

    public void setExteriorValueId(String exteriorValueId)
    {
        ExteriorValueId = exteriorValueId;
    }

    public String getExteriorTypeId()
    {
        return ExteriorTypeId;
    }

    public void setExteriorTypeId(String exteriorTypeId)
    {
        ExteriorTypeId = exteriorTypeId;
    }

    public String getReplace()
    {
        return Replace;
    }

    public void setReplace(String replace)
    {
        Replace = replace;
    }

    public String getReplaceValue()
    {
        return ReplaceValue;
    }

    public void setReplaceValue(String replaceValue)
    {
        ReplaceValue = replaceValue;
    }

    public String getRepair()
    {
        return Repair;
    }

    public void setRepair(String repair)
    {
        Repair = repair;
    }

    public String getRepairValue()
    {
        return RepairValue;
    }

    public void setRepairValue(String repairValue)
    {
        RepairValue = repairValue;
    }

    public String getComments()
    {
        return Comments;
    }

    public void setComments(String comments)
    {
        Comments = comments;
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

    public boolean isSelected()
    {
        return IsSelected;
    }

    public void setSelected(boolean selected)
    {
        IsSelected = selected;
    }

}
