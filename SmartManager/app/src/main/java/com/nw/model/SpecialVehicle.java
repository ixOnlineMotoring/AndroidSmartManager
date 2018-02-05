package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class SpecialVehicle implements Parcelable{

	int SpecialTypeID;
	int SpecialID;
	float NormalPrice;
	float SpecialPrice;
	String Specialstart;
	String SpecialEnd;
	String SpecialCreated;
	String Summary;
	String Details;
	int ImageID;
	int UsedYear;
	int itemID;
	int VariantID;
	int MakeId;
	int ModelID;
	int TotalCount;
	float SavePrice;
	String friendlyName;
	String type;
	int cmUserId;
	String stockCode;
	String colour;
	int mileage;
	String mileageType;
	String endStatus;
	String makeName;
	String modelName;
	String variantName;
	String retailPrice;
	boolean isCorrection;
	boolean CanPublish;
	String vin;
    String reg;

    public SpecialVehicle(){

    }

    public SpecialVehicle(Parcel source) {
        readParcel(source);
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

	public boolean isCorrection()
	{
	
		return isCorrection;
	}
	public void setCorrection(boolean isCorrection)
	{
	
		this.isCorrection = isCorrection;
	}
	
	public String getRetailPrice()
	{
	
		return retailPrice;
	}
	public void setRetailPrice(String retailPrice)
	{
	
		this.retailPrice = retailPrice;
	}
	
	public boolean isCanPublish()
	{
	
		return CanPublish;
	}
	public void setCanPublish(boolean canPublish)
	{
	
		CanPublish = canPublish;
	}
	
	public String getMakeName() {
		if(TextUtils.isEmpty(makeName))
			return "";
		else
			return makeName;
	}

    public void setMakeName(String makeName) {
		this.makeName = makeName;
	}

    public String getModelName() {
		if(TextUtils.isEmpty(modelName))
			return "";
		else
			return modelName;
	}

    public void setModelName(String modelName) {
		this.modelName = modelName;
	}

    public String getVariantName() {
		if(TextUtils.isEmpty(variantName))
			return "";
		else
			return variantName;
	}

    public void setVariantName(String variantName) {
		this.variantName = variantName;
	}

    public String getEndStatus() {
		return endStatus;
	}

    public void setEndStatus(String endStatus) {
		this.endStatus = endStatus;
	}

    public int getMileage() {
		return mileage;
	}

    public void setMileage(int mileage) {
		this.mileage = mileage;
	}

    public String getMileageType() {
		return mileageType;
	}

    public void setMileageType(String mileageType) {
		if(TextUtils.isEmpty(mileageType) || mileageType.equals("null")||mileageType.equalsIgnoreCase("anyType{}"))
			this.mileageType="Mileage?";
		else
			this.mileageType = mileageType;
	}

    public String getColour() {
		if(colour==null)
			return "Colour?";
		else
			return colour;
	}

	public void setColour(String colour) {
		if(TextUtils.isEmpty(colour) || colour.equals("null")||colour.equalsIgnoreCase("anyType{}"))
			this.colour="Colour?";
		else
			this.colour = colour;
	}
	
	public String getStockCode() {
		if(stockCode==null)
			return "Stock Code?";
		else
			return stockCode;
	}

    public void setStockCode(String stockCode) {
		if(TextUtils.isEmpty(stockCode) || stockCode.equals("null")||stockCode.equalsIgnoreCase("anyType{}"))
			this.stockCode="Stock Code?";
		else
			this.stockCode = stockCode;
	}

    public int getCmUserId() {
		return cmUserId;
	}

    public void setCmUserId(int cmUserId) {
		this.cmUserId = cmUserId;
	}

    public String getSpecialCreated() {
		return SpecialCreated;
	}

	public void setSpecialCreated(String specialCreated) {
		SpecialCreated = specialCreated;
	}

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFriendlyName() {
		if(friendlyName==null)
			return "No Vehicle Name Loaded";
		else
			return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		if(friendlyName==null || friendlyName.equals("null") || TextUtils.isEmpty(friendlyName)||friendlyName.equalsIgnoreCase("anyType{}"))
			this.friendlyName = "VehicleName?";
		else
			this.friendlyName = friendlyName;
	}

    public int getSpecialTypeID() {
		return SpecialTypeID;
	}

    public void setSpecialTypeID(int specialTypeID) {
		SpecialTypeID = specialTypeID;
	}

    public int getSpecialID() {
		return SpecialID;
	}

    public void setSpecialID(int specialID) {
		SpecialID = specialID;
	}

    public float getNormalPrice() {
		return NormalPrice;
	}

	public void setNormalPrice(float normalPrice) {
		NormalPrice = normalPrice;
	}

    public float getSpecialPrice() {
		return SpecialPrice;
	}

    public void setSpecialPrice(float specialPrice) {
		SpecialPrice = specialPrice;
	}

    public String getSpecialstart() {
		return Specialstart;
	}

    public void setSpecialstart(String specialstart) {
		Specialstart = specialstart;
	}

    public String getSpecialEnd() {
		return SpecialEnd;
	}

    public void setSpecialEnd(String specialEnd) {
		SpecialEnd = specialEnd;
	}

    public String getSummary() {
		if(Summary==null)
			return "Title?";
		else
			return Summary;
	}

    public void setSummary(String summary) {
		if(TextUtils.isEmpty(summary) || summary.equals("null") || summary==null ||summary.equalsIgnoreCase("anyType{}"))
			this.Summary="Title?";
		else
			Summary = summary;
	}

	public String getDetails() {
		return Details;
	}

	public void setDetails(String details) {
		Details = details;
	}

	public int getImageID() {
		return ImageID;
	}

    public void setImageID(int imageID) {
		ImageID = imageID;
	}

    public int getUsedYear() {
		return UsedYear;
	}

    public void setUsedYear(int usedYear) {
		if (usedYear == (int)usedYear)
		{
			UsedYear = usedYear; // Number is integer
		}else {
			UsedYear=0;
		}
		
	}

	public int getItemID() {
		return itemID;
	}

    public void setItemID(int itemID) {
		this.itemID = itemID;
	}

    public int getVariantID() {
		return VariantID;
	}

    public void setVariantID(int variantID) {
		VariantID = variantID;
	}

    public int getMakeId() {
		return MakeId;
	}

    public void setMakeId(int makeId) {
		MakeId = makeId;
	}

    public int getModelID() {
		return ModelID;
	}

    public void setModelID(int modelID) {
		ModelID = modelID;
	}

    public int getTotalCount() {
		return TotalCount;
	}

    public void setTotalCount(int totalCount) {
		TotalCount = totalCount;
	}

	public float getSavePrice() {
		return SavePrice;
	}

    public void setSavePrice(float savePrice) {
		SavePrice = savePrice;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public String toString() {
		return friendlyName;
	}

    @Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(SpecialTypeID);
		dest.writeInt(SpecialID);
		dest.writeFloat(NormalPrice);
		dest.writeFloat(SpecialPrice);
		dest.writeString(Specialstart);
		dest.writeString(SpecialEnd);
		dest.writeString(SpecialCreated);
		dest.writeString(Summary);
		dest.writeString(Details);
		dest.writeInt(ImageID);
		dest.writeInt(UsedYear);
		dest.writeInt(itemID);
		dest.writeInt(VariantID);
		dest.writeInt(MakeId);
		dest.writeInt(ModelID);
		dest.writeInt(TotalCount);
		dest.writeFloat(SavePrice);
		dest.writeString(friendlyName);
		dest.writeString(type);
		dest.writeInt(cmUserId);
		dest.writeString(stockCode);
		dest.writeString(colour);
		dest.writeInt(mileage);
		dest.writeString(mileageType);
		dest.writeString(endStatus);
		dest.writeString(makeName);
		dest.writeString(modelName);
		dest.writeString(variantName);
		dest.writeByte((byte) (isCorrection ? 1 : 0));   //if isCorrection == true, byte == 1
		dest.writeByte((byte) (CanPublish ? 1 : 0));
        dest.writeString(vin);
        dest.writeString(reg);
	}
	
	private void readParcel(Parcel in){
		
		 SpecialTypeID= in.readInt();
		 SpecialID= in.readInt();
		 NormalPrice= in.readFloat();
		 SpecialPrice= in.readFloat();
		 Specialstart= in.readString();
		 SpecialEnd= in.readString();
		 SpecialCreated= in.readString();
		 Summary= in.readString();
		 Details= in.readString();
		 ImageID= in.readInt();
		 UsedYear= in.readInt();
		 itemID= in.readInt();
		 VariantID= in.readInt();
		 MakeId= in.readInt();
		 ModelID= in.readInt();
		 TotalCount= in.readInt();
		 SavePrice= in.readFloat();
		 friendlyName= in.readString();
		 type= in.readString();
		 cmUserId= in.readInt();
		 stockCode= in.readString();
		 colour= in.readString();
		 mileage= in.readInt();
		 mileageType= in.readString();
		 endStatus= in.readString();
		 makeName= in.readString();
		 modelName= in.readString();
		 variantName=in.readString();
		 isCorrection = in.readByte() != 0; //isCorrection == true if byte != 0
		 CanPublish = in.readByte() != 0;
         reg = in.readString();
         vin = in.readString();
	}

    public static final Creator<SpecialVehicle> CREATOR= new Creator<SpecialVehicle>() {

			@Override
			public SpecialVehicle createFromParcel(Parcel source) {
				return new SpecialVehicle(source);
			}

			@Override
			public SpecialVehicle[] newArray(int size) {
				return new SpecialVehicle[size];
			}
		};
}
