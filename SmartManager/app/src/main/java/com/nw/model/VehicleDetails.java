package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;

public class VehicleDetails implements Parcelable
{

    int usedVehicleStockID;
    String stockCode;
    int age;
    int variantID;
    String friendlyName;
    String mmcode;
    int year;
    String registration;
    String clientname;
    String UserName;
    String Date;
    String clientpnno;
    String solddate;
    double price;
    double tradeprice;
    double AverageTradePrice;
    double AveragePrice;
    double MarketPrice;
    double amount;
    double retailprice;
    String colour;
    int mileage;
    String comments;
    String location;
    String importComments;
    String extras;
    String condition;
    String vin;
    String engine;
    String oem;
    float cost;
    float standin;
    boolean cpaerror;
    String internalnote;
    String programname;
    boolean istender;
    boolean istrade;
    boolean isretail;
    boolean isprogram;
    boolean isexcluded;
    boolean isinvalid;
    boolean override;
    boolean ignoreonimport;
    boolean editable;
    float offerAmt;
    String type;
    String trim;
    String offerDate;
    String statusWhen;
    String statusWho;
    boolean hasOffer;
    String offerStart;
    String offerEnd;
    ArrayList<VehicleImage> imageList;
    int makeId;
    int modelId;
    int OfferStatus;
    String Source;
    Float Highest;
    int OfferID;
    String offerMember;
    String offerClient;
    String Department;
    String AgeValue;
    int offerClientId;
    boolean isBidChecked;
    double Cap;
    double Increment;
    int ClientID;
    int Sales;
    int ID;
    int Sources;
    String transmission;
    String imageURL;
    String Transmission_Type;
    String Gears;
    String Fuel_Type;
    int Power_KW;
    int Torque_NM;
    int Engine_CC;
    String Gearbox;
    String vehicle_mileage_100km;
    String acceleration0_100;
    String max_Speed;
    String warranty_Period_RSA;
    String Warranty;
    String maintenance_Plan_Period;
    String maintenance_Plan;
    ArrayList<SmartObject> demandSummaryList;
    ArrayList<SmartObject> averageAvailableSummaryList;
    ArrayList<SmartObject> averageDaysInStockSummaryList;
    ArrayList<SmartObject> leadPoolSummaryList;
    ArrayList<SmartObject> warrantySummaryList;
    int reviewCounts;
    String online_price, simple_logic_trade, simple_logic_retail, ix_trade, private_advert;
    String TUADate;
    String TUATradePrice, TUARetailPrice;
    String modelName, variantName;

    // Do apprisal screen data
    String str_Seller;
    String str_purchaseDetails;
    String str_Condition;
    String str_VahicleExtra;
    String str_InteriorReconditioning;
    String str_EngineDrivetrain;
    String str_ExteriorReconditioning;
    String str_ValuationStartRang;
    String str_ValuationEndRang;
    String str_CreateDate;
    String str_AppraisalId;

    public String getStr_CreateDate()
    {
        return str_CreateDate;
    }

    public void setStr_CreateDate(String str_CreateDate)
    {
        if (str_CreateDate.trim().equals("anyType{}"))
        {
            this.str_CreateDate = "0";
        } else
        {
            this.str_CreateDate = str_CreateDate;
        }
    }

    public String getStr_AppraisalId()
    {
        return str_AppraisalId;
    }

    public void setStr_AppraisalId(String str_AppraisalId)
    {
        if (str_AppraisalId.trim().equals("anyType{}"))
        {
            this.str_AppraisalId = "0";
        } else
        {
            this.str_AppraisalId = str_AppraisalId;
        }
    }

    public String getGetStr_ValuationEndRang()
    {
        return str_ValuationEndRang;
    }

    public void setGetStr_ValuationEndRang(String str_ValuationEndRang)
    {
        if (str_ValuationEndRang.trim().equals("anyType{}"))
        {
            this.str_ValuationEndRang = "0";
        } else
        {
            this.str_ValuationEndRang = str_ValuationEndRang;
        }
    }

    public String getStr_Seller()
    {
        return str_Seller;
    }

    public void setStr_Seller(String str_Seller)
    {
        if (str_Seller.trim().equals("anyType{}"))
        {
            this.str_Seller = "";
        } else
        {
            this.str_Seller = str_Seller;
        }
    }

    public String getStr_purchaseDetails()
    {
        return str_purchaseDetails;
    }

    public void setStr_purchaseDetails(String str_purchaseDetails)
    {
        if (str_purchaseDetails.trim().equals("anyType{}"))
        {
            this.str_purchaseDetails = "";
        } else
        {
            this.str_purchaseDetails = str_purchaseDetails;
        }
    }

    public String getStr_Condition()
    {
        return str_Condition;
    }

    public void setStr_Condition(String str_Condition)
    {
        if (str_Condition.trim().equals("anyType{}"))
        {
            this.str_Condition = "";
        } else
        {
            this.str_Condition = str_Condition;
        }
    }

    public String getStr_VahicleExtra()
    {
        return str_VahicleExtra;
    }

    public void setStr_VahicleExtra(String str_VahicleExtra)
    {
        if (str_VahicleExtra.trim().equals("anyType{}"))
        {
            this.str_VahicleExtra = "0";
        } else
        {
            this.str_VahicleExtra = str_VahicleExtra;
        }
    }

    public String getStr_InteriorReconditioning()
    {
        return str_InteriorReconditioning;
    }

    public void setStr_InteriorReconditioning(String str_InteriorReconditioning)
    {
        if (str_InteriorReconditioning.trim().equals("anyType{}"))
        {
            this.str_InteriorReconditioning = "0";
        } else
        {
            this.str_InteriorReconditioning = str_InteriorReconditioning;
        }
    }

    public String getStr_EngineDrivetrain()
    {
        return str_EngineDrivetrain;
    }

    public void setStr_EngineDrivetrain(String str_EngineDrivetrain)
    {
        if (str_EngineDrivetrain.trim().equals("anyType{}"))
        {
            this.str_EngineDrivetrain = "0";
        } else
        {
            this.str_EngineDrivetrain = str_EngineDrivetrain;
        }
    }

    public String getStr_ExteriorReconditioning()
    {
        return str_ExteriorReconditioning;
    }

    public void setStr_ExteriorReconditioning(String str_ExteriorReconditioning)
    {
        if (str_ExteriorReconditioning.trim().equals("anyType{}"))
        {
            this.str_ExteriorReconditioning = "0";
        } else
        {
            this.str_ExteriorReconditioning = str_ExteriorReconditioning;
        }
    }

    public String getStr_ValuationStartRang()
    {
        return str_ValuationStartRang;
    }

    public void setStr_ValuationStartRang(String str_ValuationStartRang)
    {
        if (str_ValuationStartRang.trim().equals("anyType{}"))
        {
            this.str_ValuationStartRang = "0";
        } else
        {
            this.str_ValuationStartRang = str_ValuationStartRang;
        }
    }

    public String getModelName()
    {

        return modelName;
    }

    public void setModelName(String modelName)
    {

        this.modelName = modelName;
    }

    public String getVariantName()
    {

        return variantName;
    }

    public void setVariantName(String variantName)
    {

        this.variantName = variantName;
    }

    public String getOnline_price()
    {
        return online_price;
    }

    public void setOnline_price(String online_price)
    {

        this.online_price = online_price;
    }

    public String getSimple_logic_trade()
    {

        return simple_logic_trade;
    }

    public void setSimple_logic_trade(String simple_logic_trade)
    {

        this.simple_logic_trade = simple_logic_trade;
    }

    public String getSimple_logic_retail()
    {

        return simple_logic_retail;
    }

    public void setSimple_logic_retail(String simple_logic_retail)
    {

        this.simple_logic_retail = simple_logic_retail;
    }

    public String getIx_trade()
    {

        return ix_trade;
    }

    public void setIx_trade(String ix_trade)
    {

        this.ix_trade = ix_trade;
    }

    public String getPrivate_advert()
    {

        return private_advert;
    }

    public void setPrivate_advert(String private_advert)
    {

        this.private_advert = private_advert;
    }

    public String getTUATradePrice()
    {

        return TUATradePrice;
    }

    public void setTUATradePrice(String tUATradePrice)
    {

        TUATradePrice = tUATradePrice;
    }

    public String getTUARetailPrice()
    {

        return TUARetailPrice;
    }

    public void setTUARetailPrice(String tUARetailPrice)
    {

        TUARetailPrice = tUARetailPrice;
    }

    public String getTUADate()
    {

        return TUADate;
    }

    public void setTUADate(String tUADate)
    {

        TUADate = tUADate;
    }

    public String getVehicle_mileage_100km()
    {

        return vehicle_mileage_100km;
    }

    public void setVehicle_mileage_100km(String vehicle_mileage_100km)
    {

        this.vehicle_mileage_100km = vehicle_mileage_100km;
    }

    public String getAcceleration0_100()
    {

        return acceleration0_100;
    }

    public void setAcceleration0_100(String acceleration0_100)
    {

        this.acceleration0_100 = acceleration0_100;
    }

    public String getMax_Speed()
    {

        return max_Speed;
    }

    public void setMax_Speed(String max_Speed)
    {

        this.max_Speed = max_Speed;
    }

    public String getWarranty_Period_RSA()
    {

        return warranty_Period_RSA;
    }

    public void setWarranty_Period_RSA(String warranty_Period_RSA)
    {

        this.warranty_Period_RSA = warranty_Period_RSA;
    }

    public String getWarranty()
    {

        return Warranty;
    }

    public void setWarranty(String warranty)
    {

        Warranty = warranty;
    }

    public String getMaintenance_Plan_Period()
    {

        return maintenance_Plan_Period;
    }

    public void setMaintenance_Plan_Period(String maintenance_Plan_Period)
    {

        this.maintenance_Plan_Period = maintenance_Plan_Period;
    }

    public String getMaintenance_Plan()
    {

        return maintenance_Plan;
    }

    public void setMaintenance_Plan(String maintenance_Plan)
    {

        this.maintenance_Plan = maintenance_Plan;
    }

    public ArrayList<SmartObject> getDemandSummaryList()
    {
        if (demandSummaryList == null)
        {
            demandSummaryList = new ArrayList<SmartObject>();
        }

        return demandSummaryList;
    }

    public void setDemandSummaryList(ArrayList<SmartObject> demandSummaryList)
    {

        this.demandSummaryList = demandSummaryList;
    }

    public ArrayList<SmartObject> getAverageAvailableSummaryList()
    {

        if (averageAvailableSummaryList == null)
        {
            averageAvailableSummaryList = new ArrayList<SmartObject>();
        }

        return averageAvailableSummaryList;
    }

    public void setAverageAvailableSummaryList(
            ArrayList<SmartObject> averageAvailableSummaryList)
    {

        this.averageAvailableSummaryList = averageAvailableSummaryList;
    }

    public ArrayList<SmartObject> getAverageDaysInStockSummaryList()
    {
        if (averageDaysInStockSummaryList == null)
        {
            averageDaysInStockSummaryList = new ArrayList<SmartObject>();
        }
        return averageDaysInStockSummaryList;
    }

    public void setAverageDaysInStockSummaryList(
            ArrayList<SmartObject> averageDaysInStockSummaryList)
    {

        this.averageDaysInStockSummaryList = averageDaysInStockSummaryList;
    }

    public ArrayList<SmartObject> getLeadPoolSummaryList()
    {

        if (leadPoolSummaryList == null)
        {
            leadPoolSummaryList = new ArrayList<SmartObject>();
        }
        return leadPoolSummaryList;
    }

    public void setLeadPoolSummaryList(ArrayList<SmartObject> leadPoolSummaryList)
    {

        this.leadPoolSummaryList = leadPoolSummaryList;
    }

    public ArrayList<SmartObject> getWarrantySummaryList()
    {

        if (warrantySummaryList == null)
        {
            warrantySummaryList = new ArrayList<SmartObject>();
        }
        return warrantySummaryList;
    }

    public void setWarrantySummaryList(ArrayList<SmartObject> warrantySummaryList)
    {

        this.warrantySummaryList = warrantySummaryList;
    }

    public int getReviewCounts()
    {

        return reviewCounts;
    }

    public void setReviewCounts(int reviewCounts)
    {

        this.reviewCounts = reviewCounts;
    }

    public VehicleDetails()
    {
        super();
    }

    public String getTransmission_Type()
    {

        return Transmission_Type;
    }

    public void setTransmission_Type(String transmission_Type)
    {

        Transmission_Type = transmission_Type;
    }

    public String getGears()
    {

        return Gears;
    }

    public void setGears(String gears)
    {

        Gears = gears;
    }

    public String getFuel_Type()
    {

        return Fuel_Type;
    }

    public void setFuel_Type(String fuel_Type)
    {

        Fuel_Type = fuel_Type;
    }

    public int getPower_KW()
    {

        return Power_KW;
    }

    public void setPower_KW(int power_KW)
    {

        Power_KW = power_KW;
    }

    public int getTorque_NM()
    {

        return Torque_NM;
    }

    public void setTorque_NM(int torque_NM)
    {

        Torque_NM = torque_NM;
    }

    public int getEngine_CC()
    {

        return Engine_CC;
    }

    public void setEngine_CC(int engine_CC)
    {

        Engine_CC = engine_CC;
    }

    public String getGearbox()
    {

        return Gearbox;
    }

    public void setGearbox(String gearbox)
    {

        Gearbox = gearbox;
    }

    public String getClientname()
    {

        return clientname;
    }

    public void setClientname(String clientname)
    {

        this.clientname = clientname;
    }

    public String getClientpnno()
    {

        return clientpnno;
    }

    public void setClientpnno(String clientpnno)
    {

        this.clientpnno = clientpnno;
    }

    public String getSolddate()
    {

        return solddate;
    }

    public void setSolddate(String solddate)
    {

        this.solddate = solddate;
    }

    public double getAverageTradePrice()
    {

        return AverageTradePrice;
    }

    public void setAverageTradePrice(double averageTradePrice)
    {

        AverageTradePrice = averageTradePrice;
    }

    public double getAveragePrice()
    {

        return AveragePrice;
    }

    public void setAveragePrice(double averagePrice)
    {

        AveragePrice = averagePrice;
    }

    public double getMarketPrice()
    {

        return MarketPrice;
    }

    public void setMarketPrice(double marketPrice)
    {

        MarketPrice = marketPrice;
    }

    public double getRetailprice()
    {

        return retailprice;
    }

    public void setRetailprice(double retailprice)
    {

        this.retailprice = retailprice;
    }

    public String getImageURL()
    {

        return imageURL;
    }

    public void setImageURL(String imageURL)
    {

        this.imageURL = imageURL;
    }

    public int getSources()
    {

        return Sources;
    }

    public void setSources(int sources)
    {

        Sources = sources;
    }

    public String getTransmission()
    {

        return transmission;
    }

    public void setTransmission(String transmission)
    {

        this.transmission = transmission;
    }

    public int getID()
    {
        return ID;
    }

    public void setID(int ID)
    {
        this.ID = ID;
    }

    public boolean isBidChecked()
    {
        return isBidChecked;
    }

    public void setBidChecked(boolean isBidChecked)
    {
        this.isBidChecked = isBidChecked;
    }

    public int getOfferClientId()
    {
        return offerClientId;
    }

    public void setOfferClientId(int offerClientId)
    {
        this.offerClientId = offerClientId;
    }

    public String getDepartment()
    {
        return Department;
    }

    public void setDepartment(String department)
    {
        Department = department;
    }

    public String getAgeValue()
    {
        return AgeValue;
    }

    public void setAgeValue(String ageValue)
    {
        AgeValue = ageValue;
    }

    public String getOfferMember()
    {
        return offerMember;
    }

    public void setOfferMember(String offerMember)
    {
        if (offerMember.equals("anyType{}"))
            this.offerMember = "No Member";
        else
            this.offerMember = offerMember;
    }

    public String getOfferClient()
    {
        return offerClient;
    }

    public void setOfferClient(String offerClient)
    {
        if (offerClient.equals("anyType{}"))
            this.offerClient = "No Client";
        else
            this.offerClient = offerClient;
    }

    public int getOfferStatus()
    {
        return OfferStatus;
    }

    public void setOfferStatus(int offerStatus)
    {
        OfferStatus = offerStatus;
    }

    public String getSource()
    {
        return Source;
    }

    public void setSource(String source)
    {
        Source = source;
    }

    public Float getHighest()
    {
        return Highest;
    }

    public void setHighest(Float highest)
    {
        Highest = highest;
    }

    public int getOfferID()
    {
        return OfferID;
    }

    public void setOfferID(int offerID)
    {
        OfferID = offerID;
    }

    public int getMakeId()
    {
        return makeId;
    }

    public void setMakeId(int makeId)
    {
        this.makeId = makeId;
    }

    public int getModelId()
    {
        return modelId;
    }

    public void setModelId(int modelId)
    {
        this.modelId = modelId;
    }

    public int getClientID()
    {
        return ClientID;
    }

    public void setClientID(int ClientID)
    {
        this.ClientID = ClientID;
    }

    public int getSales()
    {
        return Sales;
    }

    public void setSales(int Sales)
    {
        this.Sales = Sales;
    }

    public float getOfferAmt()
    {
        return offerAmt;
    }

    public void setOfferAmt(float offerAmt)
    {
        this.offerAmt = offerAmt;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        if (type.equals("anyType{}"))
            this.type = "";
        else
            this.type = type;
    }

    public String getOfferDate()
    {
        return offerDate;
    }

    public void setOfferDate(String offerDate)
    {
        this.offerDate = offerDate;
    }

    public String getStatusWhen()
    {
        return statusWhen;
    }

    public void setStatusWhen(String statusWhen)
    {
        this.statusWhen = statusWhen;
    }

    public String getStatusWho()
    {
        return statusWho;
    }

    public void setStatusWho(String statusWho)
    {
        this.statusWho = statusWho;
    }

    public boolean isHasOffer()
    {
        return hasOffer;
    }

    public void setHasOffer(boolean hasOffer)
    {
        this.hasOffer = hasOffer;
    }

    public String getOfferStart()
    {
        return offerStart;
    }

    public void setOfferStart(String offerStart)
    {
        this.offerStart = offerStart;
    }

    public String getOfferEnd()
    {
        return offerEnd;
    }

    public void setOfferEnd(String offerEnd)
    {
        this.offerEnd = offerEnd;
    }

    public String getTrim()
    {
        return trim;
    }

    public void setTrim(String trim)
    {
        if (trim.equals("anyType{}"))
            this.trim = "";
        else
            this.trim = trim;
    }

    public int getUsedVehicleStockID()
    {
        return usedVehicleStockID;
    }

    public void setUsedVehicleStockID(int usedVehicleStockID)
    {
        this.usedVehicleStockID = usedVehicleStockID;
    }

    public String getStockCode()
    {
        return stockCode;
    }

    public void setStockCode(String stockCode)
    {
        this.stockCode = stockCode;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public int getVariantID()
    {
        return variantID;
    }

    public void setVariantID(int variantID)
    {
        this.variantID = variantID;
    }

    public String getFriendlyName()
    {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }

    public String getMmcode()
    {
        return mmcode;
    }

    public void setMmcode(String mmcode)
    {
        this.mmcode = mmcode;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public String getRegistration()
    {
        return registration;
    }

    public void setRegistration(String registration)
    {
        if (registration.equals("anyType{}"))
        {
            this.registration = "No Reg loaded";
        } else if (registration.contains("null") || registration.equals("")
                || registration == null || registration.equals("No Reg loaded")
                || registration.equals("0"))
        {
            this.registration = "No Reg loaded";
        } else
        {
            this.registration = registration;
        }
    }

    public String getClientName()
    {
        return clientname;
    }

    public void setClientName(String ClientName)
    {
        if (ClientName.equals("anyType{}"))
            this.clientname = "";
        else
            this.clientname = ClientName;
    }

    public String getUserName()
    {
        return UserName;
    }

    public void setUserName(String UserName)
    {
        if (UserName.equals("anyType{}"))
            this.UserName = "";
        else
            this.UserName = UserName;
    }

    public String getDate()
    {
        return Date;
    }

    public void setDate(String Date)
    {
        if (Date.equals("anyType{}"))
            this.Date = "";
        else
            this.Date = Date;
    }

    public String getClientPnno()
    {
        return clientpnno;
    }

    public void setClientPnno(String ClientPhno)
    {
        if (ClientPhno.equals("anyType{}"))
            this.clientpnno = "";
        else
            this.clientpnno = ClientPhno;
    }

    public String getsolddate()
    {
        return solddate;
    }

    public void setsolddate(String solddate)
    {
        if (solddate.equals("anyType{}"))
            this.solddate = "";
        else
            this.solddate = solddate;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public double getTradeprice()
    {
        return tradeprice;
    }

    public void setTradeprice(double tradeprice)
    {
        this.tradeprice = tradeprice;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public double getRetailPrice()
    {
        return retailprice;
    }

    public void setRetailPrice(double retailprice)
    {
        this.retailprice = retailprice;
    }

    public String getColour()
    {
        return colour;
    }

    public void setColour(String colour)
    {
        if (colour.equals("anyType{}") || colour.equals("No colour #"))
            this.colour = "Colour?";
        else
            this.colour = colour;
    }

    public int getMileage()
    {
        return mileage;
    }

    public void setMileage(int mileage)
    {
        this.mileage = mileage;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        if (comments.equals("anyType{}"))
            this.comments = "";
        else
            this.comments = comments;
    }

    public String getLocation()
    {
        if (!TextUtils.isEmpty(location))
            return location;
        else
            return "";
    }

    public void setLocation(String location)
    {
        if (TextUtils.isEmpty(location) || location.equals("anyType{}"))
            this.location = "";
        else
            this.location = location;
    }

    public String getImportComments()
    {
        return importComments;
    }

    public void setImportComments(String importComments)
    {
        this.importComments = importComments;
    }

    public String getExtras()
    {
        return extras;
    }

    public void setExtras(String extras)
    {
        this.extras = extras;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        if (condition.equals("anyType{}"))
            this.condition = "";
        else
            this.condition = condition;
    }

    public String getVin()
    {
        return vin;
    }

    public void setVin(String vin)
    {
        if (vin.equals("anyType{}"))
        {
            this.vin = "No VIN loaded";
        } else if (vin.contains("null") || vin.equals("")
                || vin == null || vin.equals("No VIN loaded")
                || vin.equals("0"))
        {
            this.vin = "No VIN loaded";
        } else
        {
            this.vin = vin;
        }
    }

    public String getEngine()
    {
        return engine;
    }

    public void setEngine(String engine)
    {
        this.engine = engine;
    }

    public String getOem()
    {
        return oem;
    }

    public void setOem(String oem)
    {
        if (oem.equals("anyType{}"))
            this.oem = "";
        else
            this.oem = oem;
    }

    public float getCost()
    {
        return cost;
    }

    public void setCost(float cost)
    {
        this.cost = cost;
    }

    public float getStandin()
    {
        return standin;
    }

    public void setStandin(float standin)
    {
        this.standin = standin;
    }

    public boolean isCpaerror()
    {
        return cpaerror;
    }

    public void setCpaerror(boolean cpaerror)
    {
        this.cpaerror = cpaerror;
    }

    public String getInternalnote()
    {
        return internalnote;
    }

    public void setInternalnote(String internalnote)
    {
        if (internalnote.equals("anyType{}"))
            this.internalnote = "";
        else
            this.internalnote = internalnote;
    }

    public String getProgramname()
    {
        return programname;
    }

    public void setProgramname(String programname)
    {
        if (programname.equals("anyType{}"))
            this.programname = "";
        else
            this.programname = programname;
    }

    public boolean isIstender()
    {
        return istender;
    }

    public void setIstender(boolean istender)
    {
        this.istender = istender;
    }

    public boolean isIstrade()
    {
        return istrade;
    }

    public void setIstrade(boolean istrade)
    {
        this.istrade = istrade;
    }

    public boolean isIsretail()
    {
        return isretail;
    }

    public void setIsretail(boolean isretail)
    {
        this.isretail = isretail;
    }

    public boolean isIsprogram()
    {
        return isprogram;
    }

    public void setIsprogram(boolean isprogram)
    {
        this.isprogram = isprogram;
    }

    public boolean isIsexcluded()
    {
        return isexcluded;
    }

    public void setIsexcluded(boolean isexcluded)
    {
        this.isexcluded = isexcluded;
    }

    public boolean isIsinvalid()
    {
        return isinvalid;
    }

    public void setIsinvalid(boolean isinvalid)
    {
        this.isinvalid = isinvalid;
    }

    public boolean isOverride()
    {
        return override;
    }

    public void setOverride(boolean override)
    {
        this.override = override;
    }

    public boolean isIgnoreonimport()
    {
        return ignoreonimport;
    }

    public void setIgnoreonimport(boolean ignoreonimport)
    {
        this.ignoreonimport = ignoreonimport;
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

    public double getCap()
    {

        return Cap;
    }

    public void setCap(double cap)
    {

        Cap = cap;
    }

    public double getIncrement()
    {

        return Increment;
    }

    public void setIncrement(double increment)
    {

        Increment = increment;
    }

    @Override
    public String toString()
    {
        return friendlyName;
    }

    protected VehicleDetails(Parcel in)
    {
        usedVehicleStockID = in.readInt();
        stockCode = in.readString();
        age = in.readInt();
        variantID = in.readInt();
        friendlyName = in.readString();
        mmcode = in.readString();
        year = in.readInt();
        registration = in.readString();
        clientname = in.readString();
        UserName = in.readString();
        Date = in.readString();
        clientpnno = in.readString();
        solddate = in.readString();
        price = in.readDouble();
        tradeprice = in.readDouble();
        AverageTradePrice = in.readDouble();
        AveragePrice = in.readDouble();
        MarketPrice = in.readDouble();
        amount = in.readDouble();
        retailprice = in.readDouble();
        colour = in.readString();
        mileage = in.readInt();
        comments = in.readString();
        location = in.readString();
        importComments = in.readString();
        extras = in.readString();
        condition = in.readString();
        vin = in.readString();
        engine = in.readString();
        oem = in.readString();
        cost = in.readFloat();
        standin = in.readFloat();
        cpaerror = in.readByte() != 0x00;
        internalnote = in.readString();
        programname = in.readString();
        istender = in.readByte() != 0x00;
        istrade = in.readByte() != 0x00;
        isretail = in.readByte() != 0x00;
        isprogram = in.readByte() != 0x00;
        isexcluded = in.readByte() != 0x00;
        isinvalid = in.readByte() != 0x00;
        override = in.readByte() != 0x00;
        ignoreonimport = in.readByte() != 0x00;
        editable = in.readByte() != 0x00;
        offerAmt = in.readFloat();
        type = in.readString();
        trim = in.readString();
        offerDate = in.readString();
        statusWhen = in.readString();
        statusWho = in.readString();
        hasOffer = in.readByte() != 0x00;
        offerStart = in.readString();
        offerEnd = in.readString();
        if (in.readByte() == 0x01)
        {
            imageList = new ArrayList<VehicleImage>();
            in.readList(imageList, VehicleImage.class.getClassLoader());
        } else
        {
            imageList = null;
        }
        makeId = in.readInt();
        modelId = in.readInt();
        OfferStatus = in.readInt();
        Source = in.readString();
        Highest = in.readByte() == 0x00 ? null : in.readFloat();
        OfferID = in.readInt();
        offerMember = in.readString();
        offerClient = in.readString();
        Department = in.readString();
        AgeValue = in.readString();
        offerClientId = in.readInt();
        isBidChecked = in.readByte() != 0x00;
        Cap = in.readDouble();
        Increment = in.readDouble();
        ClientID = in.readInt();
        Sales = in.readInt();
        ID = in.readInt();
        Sources = in.readInt();
        transmission = in.readString();
        imageURL = in.readString();
        Transmission_Type = in.readString();
        Gears = in.readString();
        Fuel_Type = in.readString();
        Power_KW = in.readInt();
        Torque_NM = in.readInt();
        Engine_CC = in.readInt();
        Gearbox = in.readString();
        vehicle_mileage_100km = in.readString();
        acceleration0_100 = in.readString();
        max_Speed = in.readString();
        warranty_Period_RSA = in.readString();
        Warranty = in.readString();
        maintenance_Plan_Period = in.readString();
        maintenance_Plan = in.readString();
        if (in.readByte() == 0x01)
        {
            demandSummaryList = new ArrayList<SmartObject>();
            in.readList(demandSummaryList, SmartObject.class.getClassLoader());
        } else
        {
            demandSummaryList = null;
        }
        if (in.readByte() == 0x01)
        {
            averageAvailableSummaryList = new ArrayList<SmartObject>();
            in.readList(averageAvailableSummaryList, SmartObject.class.getClassLoader());
        } else
        {
            averageAvailableSummaryList = null;
        }
        if (in.readByte() == 0x01)
        {
            averageDaysInStockSummaryList = new ArrayList<SmartObject>();
            in.readList(averageDaysInStockSummaryList, SmartObject.class.getClassLoader());
        } else
        {
            averageDaysInStockSummaryList = null;
        }
        if (in.readByte() == 0x01)
        {
            leadPoolSummaryList = new ArrayList<SmartObject>();
            in.readList(leadPoolSummaryList, SmartObject.class.getClassLoader());
        } else
        {
            leadPoolSummaryList = null;
        }
        if (in.readByte() == 0x01)
        {
            warrantySummaryList = new ArrayList<SmartObject>();
            in.readList(warrantySummaryList, SmartObject.class.getClassLoader());
        } else
        {
            warrantySummaryList = null;
        }
        reviewCounts = in.readInt();
        simple_logic_retail = in.readString();
        simple_logic_trade = in.readString();
        online_price = in.readString();
        private_advert = in.readString();
        ix_trade = in.readString();
        TUADate = in.readString();
        TUARetailPrice = in.readString();
        TUATradePrice = in.readString();
        modelName = in.readString();
        variantName = in.readString();

        str_Seller = in.readString();
        str_purchaseDetails = in.readString();
        str_Condition = in.readString();
        str_VahicleExtra = in.readString();
        str_InteriorReconditioning = in.readString();
        str_EngineDrivetrain = in.readString();
        str_ExteriorReconditioning = in.readString();
        str_ValuationStartRang = in.readString();
        str_ValuationEndRang = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(usedVehicleStockID);
        dest.writeString(stockCode);
        dest.writeInt(age);
        dest.writeInt(variantID);
        dest.writeString(friendlyName);
        dest.writeString(mmcode);
        dest.writeInt(year);
        dest.writeString(registration);
        dest.writeString(clientname);
        dest.writeString(UserName);
        dest.writeString(Date);
        dest.writeString(clientpnno);
        dest.writeString(solddate);
        dest.writeDouble(price);
        dest.writeDouble(tradeprice);
        dest.writeDouble(AverageTradePrice);
        dest.writeDouble(AveragePrice);
        dest.writeDouble(MarketPrice);
        dest.writeDouble(amount);
        dest.writeDouble(retailprice);
        dest.writeString(colour);
        dest.writeInt(mileage);
        dest.writeString(comments);
        dest.writeString(location);
        dest.writeString(importComments);
        dest.writeString(extras);
        dest.writeString(condition);
        dest.writeString(vin);
        dest.writeString(engine);
        dest.writeString(oem);
        dest.writeFloat(cost);
        dest.writeFloat(standin);
        dest.writeByte((byte) (cpaerror ? 0x01 : 0x00));
        dest.writeString(internalnote);
        dest.writeString(programname);
        dest.writeByte((byte) (istender ? 0x01 : 0x00));
        dest.writeByte((byte) (istrade ? 0x01 : 0x00));
        dest.writeByte((byte) (isretail ? 0x01 : 0x00));
        dest.writeByte((byte) (isprogram ? 0x01 : 0x00));
        dest.writeByte((byte) (isexcluded ? 0x01 : 0x00));
        dest.writeByte((byte) (isinvalid ? 0x01 : 0x00));
        dest.writeByte((byte) (override ? 0x01 : 0x00));
        dest.writeByte((byte) (ignoreonimport ? 0x01 : 0x00));
        dest.writeByte((byte) (editable ? 0x01 : 0x00));
        dest.writeFloat(offerAmt);
        dest.writeString(type);
        dest.writeString(trim);
        dest.writeString(offerDate);
        dest.writeString(statusWhen);
        dest.writeString(statusWho);
        dest.writeByte((byte) (hasOffer ? 0x01 : 0x00));
        dest.writeString(offerStart);
        dest.writeString(offerEnd);
        if (imageList == null)
        {
            dest.writeByte((byte) (0x00));
        } else
        {
            dest.writeByte((byte) (0x01));
            dest.writeList(imageList);
        }
        dest.writeInt(makeId);
        dest.writeInt(modelId);
        dest.writeInt(OfferStatus);
        dest.writeString(Source);
        if (Highest == null)
        {
            dest.writeByte((byte) (0x00));
        } else
        {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(Highest);
        }
        dest.writeInt(OfferID);
        dest.writeString(offerMember);
        dest.writeString(offerClient);
        dest.writeString(Department);
        dest.writeString(AgeValue);
        dest.writeInt(offerClientId);
        dest.writeByte((byte) (isBidChecked ? 0x01 : 0x00));
        dest.writeDouble(Cap);
        dest.writeDouble(Increment);
        dest.writeInt(ClientID);
        dest.writeInt(Sales);
        dest.writeInt(ID);
        dest.writeInt(Sources);
        dest.writeString(transmission);
        dest.writeString(imageURL);
        dest.writeString(Transmission_Type);
        dest.writeString(Gears);
        dest.writeString(Fuel_Type);
        dest.writeInt(Power_KW);
        dest.writeInt(Torque_NM);
        dest.writeInt(Engine_CC);
        dest.writeString(Gearbox);
        dest.writeString(vehicle_mileage_100km);
        dest.writeString(acceleration0_100);
        dest.writeString(max_Speed);
        dest.writeString(warranty_Period_RSA);
        dest.writeString(Warranty);
        dest.writeString(maintenance_Plan_Period);
        dest.writeString(maintenance_Plan);
        if (demandSummaryList == null)
        {
            dest.writeByte((byte) (0x00));
        } else
        {
            dest.writeByte((byte) (0x01));
            dest.writeList(demandSummaryList);
        }
        if (averageAvailableSummaryList == null)
        {
            dest.writeByte((byte) (0x00));
        } else
        {
            dest.writeByte((byte) (0x01));
            dest.writeList(averageAvailableSummaryList);
        }
        if (averageDaysInStockSummaryList == null)
        {
            dest.writeByte((byte) (0x00));
        } else
        {
            dest.writeByte((byte) (0x01));
            dest.writeList(averageDaysInStockSummaryList);
        }
        if (leadPoolSummaryList == null)
        {
            dest.writeByte((byte) (0x00));
        } else
        {
            dest.writeByte((byte) (0x01));
            dest.writeList(leadPoolSummaryList);
        }
        if (warrantySummaryList == null)
        {
            dest.writeByte((byte) (0x00));
        } else
        {
            dest.writeByte((byte) (0x01));
            dest.writeList(warrantySummaryList);
        }
        dest.writeInt(reviewCounts);
        dest.writeString(simple_logic_retail);
        dest.writeString(simple_logic_trade);
        dest.writeString(private_advert);
        dest.writeString(ix_trade);
        dest.writeString(TUARetailPrice);
        dest.writeString(TUATradePrice);
        dest.writeString(TUADate);
        dest.writeString(variantName);
        dest.writeString(modelName);

    }

    public static final Parcelable.Creator<VehicleDetails> CREATOR = new Parcelable.Creator<VehicleDetails>()
    {
        @Override
        public VehicleDetails createFromParcel(Parcel in)
        {
            return new VehicleDetails(in);
        }

        @Override
        public VehicleDetails[] newArray(int size)
        {
            return new VehicleDetails[size];
        }
    };
}