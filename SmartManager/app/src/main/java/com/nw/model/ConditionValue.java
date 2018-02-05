package com.nw.model;

public class ConditionValue
{
    private String Text;

    private int Value;

    public String getText ()
    {
        return Text;
    }

    public void setText (String Text)
    {
        this.Text = Text;
    }

    public int getValue ()
    {
        return Value;
    }

    public void setValue (int Value)
    {
        this.Value = Value;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Text = "+Text+", Value = "+Value+"]";
    }
}

		