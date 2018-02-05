package com.nw.model;

/**
 * Created by Akshay on 06-07-2017.
 */

public class SendOfferViewMessageResponse
{
    private String Name;

    private String Email;

    private String Subject;

    private String Mobile;

    private String Days;

    private String BodySMS;

    private String Surname;

    private String Company;

    private String SelectedToSendSMS;

    private String SelectedToSendEmail;

    private String BodyEmail;

    public String getName ()
    {
        return Name;
    }

    public void setName (String Name)
    {
        this.Name = Name;
    }

    public String getEmail ()
    {
        return Email;
    }

    public void setEmail (String Email)
    {
        this.Email = Email;
    }

    public String getSubject ()
    {
        return Subject;
    }

    public void setSubject (String Subject)
    {
        this.Subject = Subject;
    }

    public String getMobile ()
    {
        return Mobile;
    }

    public void setMobile (String Mobile)
    {
        this.Mobile = Mobile;
    }

    public String getDays ()
    {
        return Days;
    }

    public void setDays (String Days)
    {
        this.Days = Days;
    }

    public String getBodySMS ()
    {
        return BodySMS;
    }

    public void setBodySMS (String BodySMS)
    {
        this.BodySMS = BodySMS;
    }

    public String getSurname ()
    {
        return Surname;
    }

    public void setSurname (String Surname)
    {
        this.Surname = Surname;
    }

    public String getCompany ()
    {
        return Company;
    }

    public void setCompany (String Company)
    {
        this.Company = Company;
    }

    public String getSelectedToSendSMS ()
    {
        return SelectedToSendSMS;
    }

    public void setSelectedToSendSMS (String SelectedToSendSMS)
    {
        this.SelectedToSendSMS = SelectedToSendSMS;
    }

    public String getSelectedToSendEmail ()
    {
        return SelectedToSendEmail;
    }

    public void setSelectedToSendEmail (String SelectedToSendEmail)
    {
        this.SelectedToSendEmail = SelectedToSendEmail;
    }

    public String getBodyEmail ()
    {
        return BodyEmail;
    }

    public void setBodyEmail (String BodyEmail)
    {
        this.BodyEmail = BodyEmail;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Name = "+Name+", Email = "+Email+", Subject = "+Subject+", Mobile = "+Mobile+", Days = "+Days+", BodySMS = "+BodySMS+", Surname = "+Surname+", Company = "+Company+", SelectedToSend = "+SelectedToSendSMS+", BodyEmail = "+BodyEmail+"]";
    }
}

