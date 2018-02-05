
package com.nw.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable
{
    private String scanID;
    private String telephone;
    private String email_id;
    private String Surname;
    private String StreetAddress;
    private String Company;
    private String Name;
    private String IDNumber;
    private String SellerId;
    private String licenceNumber;
    private String Age;
    private String Initials;
    private String DriverRestriction1;
    private String DriverRestriction2;
    private String DateOfBirth;
    private String PreferenceLanguage;
    private String Gender;
    private String CertificateNumber;
    private String CountryOfIssue;
    private String IssueNumber;
    private String DateValidFrom;
    private String DateValidUntil;
    private String ProfessionalDrivingPermit_Category;
    private String ProfessionalDrivingPermit_DateValidUntil;
    private String Photo;
    private String Identity_Number;
    private String Identity_Type;
    private String Identity_CountryOfIssue;
    private VehicleClass vehicleClass1 = new VehicleClass();
    private VehicleClass vehicleClass2 = new VehicleClass();
    private VehicleClass vehicleClass3 = new VehicleClass();
    private VehicleClass vehicleClass4 = new VehicleClass();
    private int totalCount;

    public Person()
    {

    }

    public Person(Parcel source)
    {

        // TODO Auto-generated constructor stub
        readParcel(source);
    }

    public String getIdentity_CountryOfIssue()
    {

        return Identity_CountryOfIssue;
    }

    public void setIdentity_CountryOfIssue(String identity_CountryOfIssue)
    {

        Identity_CountryOfIssue = identity_CountryOfIssue;
    }

    public VehicleClass getVehicleClass1()
    {

        return vehicleClass1;
    }

    public void setVehicleClass1(VehicleClass vehicleClass1)
    {

        this.vehicleClass1 = vehicleClass1;
    }

    public VehicleClass getVehicleClass2()
    {

        return vehicleClass2;
    }

    public void setVehicleClass2(VehicleClass vehicleClass2)
    {

        this.vehicleClass2 = vehicleClass2;
    }

    public VehicleClass getVehicleClass3()
    {

        return vehicleClass3;
    }

    public void setVehicleClass3(VehicleClass vehicleClass3)
    {

        this.vehicleClass3 = vehicleClass3;
    }

    public VehicleClass getVehicleClass4()
    {

        return vehicleClass4;
    }

    public void setVehicleClass4(VehicleClass vehicleClass4)
    {

        this.vehicleClass4 = vehicleClass4;
    }

    public String getSurname()
    {
        return Surname;
    }

    public void setSurname(String surname)
    {
        if (surname.trim().equals("anyType{}"))
        {
            Surname = "";
        } else
        {
            Surname = surname;
        }
    }

    public String getStreetAddress()
    {

        return StreetAddress;
    }

    public void setStreetAddress(String StreetAddress)
    {
        if (StreetAddress.trim().equals("anyType{}"))
        {
            this.StreetAddress = "";
        } else
        {
            this.StreetAddress = StreetAddress;
        }
    }

    public String getCompany()
    {
        return Company;
    }

    public void setCompany(String Company)
    {
        if (Company.trim().equals("anyType{}"))
        {
            this.Company = "";
        } else
        {
            this.Company = Company;
        }
    }

    public String getName()
    {

        return Name;
    }

    public void setName(String Name)
    {
        this.Name = Name;
    }

    public String getInitials()
    {

        return Initials;
    }

    public void setInitials(String initials)
    {
        if (initials.trim().equals("anyType{}"))
        {
            Initials = "";
        } else
        {
            Initials = initials;
        }
    }

    public String getDriverRestriction1()
    {

        return DriverRestriction1;
    }

    public void setDriverRestriction1(String driverRestriction1)
    {

        DriverRestriction1 = driverRestriction1;
    }

    public String getDriverRestriction2()
    {

        return DriverRestriction2;
    }

    public void setDriverRestriction2(String driverRestriction2)
    {

        DriverRestriction2 = driverRestriction2;
    }

    public String getDateOfBirth()
    {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth)
    {

        DateOfBirth = dateOfBirth;
    }

    public String getPreferenceLanguage()
    {

        return PreferenceLanguage;
    }

    public void setPreferenceLanguage(String preferenceLanguage)
    {

        PreferenceLanguage = preferenceLanguage;
    }

    public String getGender()
    {

        return Gender;
    }

    public void setGender(String Gender)
    {
        if (Gender.trim().equals("anyType{}"))
        {
            this.Gender = "gender?";
        } else
        {
            this.Gender = Gender;
        }
    }

    public String getCertificateNumber()
    {

        return CertificateNumber;
    }


    public void setCertificateNumber(String certificateNumber)
    {

        CertificateNumber = certificateNumber;
    }

    public String getCountryOfIssue()
    {

        return CountryOfIssue;
    }

    public void setCountryOfIssue(String countryOfIssue)
    {

        CountryOfIssue = countryOfIssue;
    }

    public String getIssueNumber()
    {

        return IssueNumber;
    }

    public void setIssueNumber(String issueNumber)
    {

        IssueNumber = issueNumber;
    }

    public String getDateValidFrom()
    {

        return DateValidFrom;
    }

    public void setDateValidFrom(String dateValidFrom)
    {

        DateValidFrom = dateValidFrom;
    }

    public String getDateValidUntil()
    {

        return DateValidUntil;
    }

    public void setDateValidUntil(String dateValidUntil)
    {

        DateValidUntil = dateValidUntil;
    }

    public String getProfessionalDrivingPermit_Category()
    {

        return ProfessionalDrivingPermit_Category;
    }

    public void setProfessionalDrivingPermit_Category(
            String professionalDrivingPermit_Category)
    {

        ProfessionalDrivingPermit_Category = professionalDrivingPermit_Category;
    }

    public String getProfessionalDrivingPermit_DateValidUntil()
    {

        return ProfessionalDrivingPermit_DateValidUntil;
    }

    public void setProfessionalDrivingPermit_DateValidUntil(
            String professionalDrivingPermit_DateValidUntil)
    {

        ProfessionalDrivingPermit_DateValidUntil = professionalDrivingPermit_DateValidUntil;
    }

    public String getPhoto()
    {

        return Photo;
    }

    public void setPhoto(String photo)
    {

        Photo = photo;
    }

    public String getIdentity_Number()
    {

        return Identity_Number;
    }

    public void setIdentity_Number(String identity_Number)
    {
        if (identity_Number.trim().equals("anyType{}"))
        {
            Identity_Number = "";
        } else
        {
            Identity_Number = identity_Number;
        }

    }

    public String getIdentity_Type()
    {

        return Identity_Type;
    }

    public void setIdentity_Type(String identity_Type)
    {

        Identity_Type = identity_Type;
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

        dest.writeString(Surname);
        dest.writeString(StreetAddress);
        dest.writeString(Company);
        dest.writeString(Name);
        dest.writeString(IDNumber);
        dest.writeString(SellerId);
        dest.writeString(licenceNumber);
        dest.writeString(Age);
        dest.writeString(Initials);
        dest.writeString(DriverRestriction1);
        dest.writeString(DriverRestriction2);
        dest.writeString(DateOfBirth);
        dest.writeString(PreferenceLanguage);
        dest.writeString(Gender);
        dest.writeString(CertificateNumber);
        dest.writeString(CountryOfIssue);
        dest.writeString(IssueNumber);
        dest.writeString(DateValidFrom);
        dest.writeString(telephone);
        dest.writeString(email_id);
        dest.writeString(DateValidUntil);
        dest.writeString(ProfessionalDrivingPermit_Category);
        dest.writeString(ProfessionalDrivingPermit_DateValidUntil);
        dest.writeString(Photo);
        dest.writeString(Identity_Number);
        dest.writeString(Identity_Type);
        dest.writeString(Identity_CountryOfIssue);
        dest.writeParcelable(vehicleClass1, flags);
        dest.writeParcelable(vehicleClass2, flags);
        dest.writeParcelable(vehicleClass3, flags);
        dest.writeParcelable(vehicleClass4, flags);

    }

    private void readParcel(Parcel in)
    {

        Surname = in.readString();
        StreetAddress = in.readString();
        Company = in.readString();
        Name = in.readString();
        IDNumber = in.readString();
        SellerId = in.readString();
        licenceNumber = in.readString();
        Age = in.readString();
        Initials = in.readString();
        DriverRestriction1 = in.readString();
        DriverRestriction2 = in.readString();
        telephone = in.readString();
        email_id = in.readString();
        DateOfBirth = in.readString();
        PreferenceLanguage = in.readString();
        Gender = in.readString();
        CertificateNumber = in.readString();
        CountryOfIssue = in.readString();
        IssueNumber = in.readString();
        DateValidFrom = in.readString();
        DateValidUntil = in.readString();
        ProfessionalDrivingPermit_Category = in.readString();
        ProfessionalDrivingPermit_DateValidUntil = in.readString();
        Photo = in.readString();
        Identity_Number = in.readString();
        Identity_Type = in.readString();
        Identity_CountryOfIssue = in.readString();
        vehicleClass1 = in.readParcelable(VehicleClass.class.getClassLoader());
        vehicleClass2 = in.readParcelable(VehicleClass.class.getClassLoader());
        vehicleClass3 = in.readParcelable(VehicleClass.class.getClassLoader());
        vehicleClass4 = in.readParcelable(VehicleClass.class.getClassLoader());

    }

    public String getScanID()
    {

        return scanID;
    }

    public void setScanID(String scanID)
    {

        this.scanID = scanID;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {

        this.totalCount = totalCount;
    }

    public String getIDNumber()
    {
        return IDNumber;
    }

    public void setIDNumber(String IDNumber)
    {
        this.IDNumber = IDNumber;
    }

    public String getSellerId()
    {
        return SellerId;
    }

    public void setSellerId(String SellerId)
    {
        this.SellerId = SellerId;
    }

    public String getlicenceNumber()
    {
        return licenceNumber;
    }

    public void setlicenceNumber(String licenceNumber)
    {
        this.licenceNumber = licenceNumber;
    }

    public String getAge()
    {
        return Age;
    }

    public void setAge(String Age)
    {
        this.Age = Age;
    }

    public String getTelephone()
    {

        return telephone;
    }

    public void setTelephone(String telephone)
    {
        if (telephone.trim().equals("anyType{}"))
        {
            this.telephone = "";
        } else
        {
            this.telephone = telephone;
        }
    }

    public String getEmail_id()
    {

        return email_id;
    }

    public void setEmail_id(String email_id)
    {
        if (email_id.trim().equals("anyType{}"))
        {
            this.email_id = "";
        } else
        {
            this.email_id = email_id;
        }
    }

    public static final Creator<Person> CREATOR = new Creator<Person>()
    {

        @Override
        public Person createFromParcel(Parcel source)
        {

            // TODO Auto-generated method stub
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size)
        {

            // TODO Auto-generated method stub
            return new Person[size];
        }
    };

}
