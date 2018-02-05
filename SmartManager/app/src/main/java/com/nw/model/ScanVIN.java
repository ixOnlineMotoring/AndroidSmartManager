package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ScanVIN implements Parcelable
{
    int id;
    String date;
    String VIN;
    String registration;
    String shape;
    String make;
    String model;
    String variantstr;
    int variantID = 0;

    String expiry_Date;
    //String kilometer;
    String colour;
    String engineNumber;
    ArrayList<Variant> variants;
    ArrayList<SmartObject> stocks;
    ArrayList<Model> models;
    boolean hasModel;
    int modelId;
    int makeId;

    boolean existing;
    int minYear;
    int maxYear;
    int type;
    String year;
    Variant variant;
    SmartObject stock;

    //added new properties
    String price;
    String tradePrice;
    //String color;
    String milage;
    String licence;

    String comments;
    String location;
    String extras;
    String trim;

    String condition;
    String oem;
    String cost;
    String standIn;

    String internalNote;
    String programName;

    boolean tender, trade, retail, program, excluded, invalid, override, ignorimport, editable, cpError;
    ArrayList<VehicleImage> imageList;
    int age;

    public ScanVIN()
    {
    }

    public ScanVIN(Variant variant)
    {
        this.variant = variant;
    }

    @SuppressWarnings("unchecked")
    public ScanVIN(Parcel in)
    {
        id = in.readInt();
        date = in.readString();
        VIN = in.readString();
        registration = in.readString();
        licence = in.readString();
        shape = in.readString();
        make = in.readString();
        model = in.readString();
        colour = in.readString();
        engineNumber = in.readString();
        variants = in.readArrayList(Variant.class.getClassLoader());
        stocks = in.readArrayList(SmartObject.class.getClassLoader());
        hasModel = in.readInt() == 1 ? true : false;
        modelId = in.readInt();
        makeId = in.readInt();
        variantID = in.readInt();
        existing = in.readInt() == 1 ? true : false;
        minYear = in.readInt();
        maxYear = in.readInt();
        type = in.readInt();
        year = in.readString();
        expiry_Date = in.readString();
        variant = in.readParcelable(Variant.class.getClassLoader());
        stock = in.readParcelable(SmartObject.class.getClassLoader());
        models = in.readArrayList(Model.class.getClassLoader());


        //DASDAS
        price = in.readString();
        tradePrice = in.readString();
        //color=in.readString();
        milage = in.readString();

        comments = in.readString();
        location = in.readString();
        extras = in.readString();
        trim = in.readString();

        condition = in.readString();
        oem = in.readString();
        cost = in.readString();
        standIn = in.readString();

        internalNote = in.readString();
        programName = in.readString();

        tender = in.readInt() == 1 ? true : false;
        trade = in.readInt() == 1 ? true : false;
        retail = in.readInt() == 1 ? true : false;
        program = in.readInt() == 1 ? true : false;

        excluded = in.readInt() == 1 ? true : false;
        invalid = in.readInt() == 1 ? true : false;
        override = in.readInt() == 1 ? true : false;
        ignorimport = in.readInt() == 1 ? true : false;

        editable = in.readInt() == 1 ? true : false;

        imageList = in.readArrayList(VehicleImage.class.getClassLoader());

        age = in.readInt();
        cpError = in.readInt() == 1 ? true : false;
    }

    public String getVariantstr()
    {
        return variantstr;
    }

    public void setVariantstr(String variantstr)
    {
        if (variantstr.equals("anyType{}"))
        {
            this.variantstr = "Select Variant";
        } else if (variantstr.contains("null") || variantstr.equals("")|| variantstr == null)
        {
            this.variantstr = "Select Variant";
        } else
        {
            this.variantstr = variantstr;
        }
    }

    public String getExpiry_Date()
    {

        return expiry_Date;
    }

    public void setExpiry_Date(String expiry_Date)
    {

        this.expiry_Date = expiry_Date;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getVariantID()
    {

        return variantID;
    }

    public void setVariantID(int variantID)
    {

        this.variantID = variantID;
    }

    public String getDate()
    {
        return date;
    }

    public int getMakeId()
    {

        return makeId;
    }

    public void setMakeId(int makeId)
    {

        this.makeId = makeId;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getVIN()
    {
        return VIN;
    }

    public String getLicence()
    {
        return licence;
    }

    public void setLicence(String licence)
    {
        this.licence = licence;
    }

    public void setVIN(String vIN)
    {
        if (vIN.equals("anyType{}"))
        {
            VIN = "No VIN loaded";
        } else if (vIN.contains("null") || vIN.equals("")
                || vIN == null || vIN.equals("No Vin")
                || vIN.equals("0"))
        {
            VIN = "No VIN loaded";
        } else
        {
            VIN = vIN;
        }
    }

    public String getRegistration()
    {
        return registration;
    }

    public void setRegistration(String registration)
    {
        this.registration = registration;
    }

    public String getShape()
    {
        return shape;
    }

    public void setShape(String shape)
    {
        this.shape = shape;
    }

    public String getMake()
    {
        return make;
    }

    public void setMake(String make)
    {
        this.make = make;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getColour()
    {
        return colour;
    }

    public void setColour(String colour)
    {
        this.colour = colour;
    }

    public String getEngineNumber()
    {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber)
    {
        this.engineNumber = engineNumber;
    }

    public ArrayList<Variant> getVariants()
    {
        if (variants == null)
            variants = new ArrayList<Variant>();
        return variants;
    }

    public void setVariants(ArrayList<Variant> variants)
    {
        this.variants = variants;
    }

    public ArrayList<SmartObject> getStocks()
    {
        if (stocks == null)
            stocks = new ArrayList<SmartObject>();
        return stocks;
    }

    public void setStocks(ArrayList<SmartObject> stocks)
    {
        this.stocks = stocks;
    }

    public ArrayList<Model> getModels()
    {
        if (models == null)
            models = new ArrayList<Model>();
        return models;
    }

    public void setModels(ArrayList<Model> models)
    {
        this.models = models;
    }

    public boolean isExisting()
    {
        return existing;
    }

    public void setExisting(boolean existing)
    {
        this.existing = existing;
    }

    public boolean isHasModel()
    {
        return hasModel;
    }

    public void setHasModel(boolean hasModel)
    {
        this.hasModel = hasModel;
    }

    public int getModelId()
    {
        return modelId;
    }

    public void setModelId(int modelId)
    {
        this.modelId = modelId;
    }

    public void setMinYear(int minYear)
    {
        this.minYear = minYear;
    }

    public void setMaxYear(int maxYear)
    {
        this.maxYear = maxYear;
    }

    public int getMinYear()
    {
        return minYear;
    }

    public int getMaxYear()
    {
        return maxYear;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {

        if (year.equals("anyType{}"))
        {
            this.year = "Year?";
        } else if (year.contains("null") || year.equals("")
                || year == null || year.equals("No Vin")
                || year.equals("0"))
        {
            this.year = "Year?";
        } else
        {
            this.year = year;
        }
    }

    public Variant getVariant()
    {
        return variant;
    }

    public void setVariant(Variant variant)
    {
        this.variant = variant;
    }

    public SmartObject getStock()
    {
        return stock;
    }

    public void setStock(SmartObject stock)
    {
        this.stock = stock;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public String getTradePrice()
    {
        return tradePrice;
    }

    public void setTradePrice(String tradePrice)
    {
        this.tradePrice = tradePrice;
    }

	/*public String getColor()
    {
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}*/

    public String getMilage()
    {
        return milage;
    }

    public void setMilage(String milage)
    {
        this.milage = milage;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getExtras()
    {
        return extras;
    }

    public void setExtras(String extras)
    {
        this.extras = extras;
    }

    public String getTrim()
    {
        return trim;
    }

    public void setTrim(String trim)
    {
        this.trim = trim;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public String getOem()
    {
        return oem;
    }

    public void setOem(String oem)
    {
        this.oem = oem;
    }

    public String getCost()
    {
        return cost;
    }

    public void setCost(String cost)
    {
        this.cost = cost;
    }

    public String getStandIn()
    {
        return standIn;
    }

    public void setStandIn(String standIn)
    {
        this.standIn = standIn;
    }

    public String getInternalNote()
    {
        return internalNote;
    }

    public void setInternalNote(String internalNote)
    {
        this.internalNote = internalNote;
    }

    public String getProgramname()
    {
        return programName;
    }

    public void setProgramname(String programname)
    {
        this.programName = programname;
    }

    public boolean isTender()
    {
        return tender;
    }

    public void setTender(boolean tender)
    {
        this.tender = tender;
    }

    public boolean isTrade()
    {
        return trade;
    }

    public void setTrade(boolean trade)
    {
        this.trade = trade;
    }

    public boolean isRetail()
    {
        return retail;
    }

    public void setRetail(boolean retail)
    {
        this.retail = retail;
    }

    public boolean isProgram()
    {
        return program;
    }

    public void setProgram(boolean program)
    {
        this.program = program;
    }

    public boolean isExcluded()
    {
        return excluded;
    }

    public void setExcluded(boolean excluded)
    {
        this.excluded = excluded;
    }

    public boolean isInvalid()
    {
        return invalid;
    }

    public void setInvalid(boolean invalid)
    {
        this.invalid = invalid;
    }

    public boolean isOverride()
    {
        return override;
    }

    public void setOverride(boolean override)
    {
        this.override = override;
    }

    public boolean isIgnorimport()
    {
        return ignorimport;
    }

    public void setIgnorimport(boolean ignorimport)
    {
        this.ignorimport = ignorimport;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public ArrayList<VehicleImage> getImageList()
    {
        return imageList;
    }

    public void setImageList(ArrayList<VehicleImage> imageList)
    {
        this.imageList = imageList;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public void setCpError(boolean cpError)
    {
        this.cpError = cpError;
    }

    public boolean isCpError()
    {
        return cpError;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeString(VIN);
        dest.writeString(registration);
        dest.writeString(shape);
        dest.writeString(make);
        dest.writeString(licence);
        dest.writeString(model);
        dest.writeString(colour);
        dest.writeString(engineNumber);
        dest.writeList(variants);
        dest.writeList(stocks);
        dest.writeInt(hasModel ? 1 : 0);
        dest.writeInt(modelId);
        dest.writeInt(existing ? 1 : 0);
        dest.writeInt(minYear);
        dest.writeInt(maxYear);
        dest.writeInt(type);
        dest.writeString(year);
        dest.writeInt(makeId);
        dest.writeInt(variantID);
        dest.writeString(expiry_Date);

        dest.writeParcelable(variant, 0);
        dest.writeParcelable(stock, 0);
        dest.writeList(models);


        //ADDED NEW
        dest.writeString(price);
        dest.writeString(tradePrice);
        //dest.writeString(color);
        dest.writeString(milage);

        dest.writeString(comments);
        dest.writeString(location);
        dest.writeString(extras);
        dest.writeString(trim);

        dest.writeString(condition);
        dest.writeString(oem);
        dest.writeString(cost);
        dest.writeString(standIn);

        dest.writeString(internalNote);
        dest.writeString(programName);

        dest.writeInt(tender ? 1 : 0);
        dest.writeInt(trade ? 1 : 0);
        dest.writeInt(retail ? 1 : 0);
        dest.writeInt(program ? 1 : 0);

        dest.writeInt(excluded ? 1 : 0);
        dest.writeInt(invalid ? 1 : 0);
        dest.writeInt(override ? 1 : 0);
        dest.writeInt(ignorimport ? 1 : 0);

        dest.writeInt(editable ? 1 : 0);

        dest.writeList(imageList);

        dest.writeInt(age);
        dest.writeInt(cpError ? 1 : 0);

    }

    public static final Creator<ScanVIN> CREATOR = new Creator<ScanVIN>()
    {
        public ScanVIN createFromParcel(Parcel in)
        {
            return new ScanVIN(in);
        }

        public ScanVIN[] newArray(int size)
        {
            return new ScanVIN[size];
        }
    };

    @Override
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
