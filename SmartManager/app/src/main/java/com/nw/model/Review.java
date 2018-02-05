package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Review implements Parcelable {
	String title;
	//int type;
	String date;
	String body;
	ArrayList<MyImage> images;
	String vehicle_name;
	int year;
	String type;
	String source;
	String author;
	int ID;
	
	public Review()
	{
		super();
	}
	
	public int getID()
	{
		return ID;
	}


	public void setID(int iD)
	{
	
		ID = iD;
	}
	

	public int getYear()
	{
	
		return year;
	}

	public void setYear(int year)
	{
	
		this.year = year;
	}
	
	public String getSource()
	{
	
		return source;
	}


	public void setSource(String source)
	{
	
		this.source = source;
	}


	public String getAuthor()
	{
	
		return author;
	}


	public void setAuthor(String author)
	{
	
		this.author = author;
	}

	
	public String getVehicle_name()
	{
	
		return vehicle_name;
	}
	public void setVehicle_name(String vehicle_name)
	{
	
		this.vehicle_name = vehicle_name;
	}
	
	public String getTitle()
	{
	
		return title;
	}
	public void setTitle(String title)
	{
	
		this.title = title;
	}
	public String getType()
	{
	
		return type;
	}
	public void setType(String type)
	{
	
		this.type = type;
	}
	public String getDate()
	{
	
		return date;
	}
	public void setDate(String date)
	{
	
		this.date = date;
	}
	public String getBody()
	{
	
		return body;
	}
	public void setBody(String body)
	{
	
		this.body = body;
	}
	public ArrayList<MyImage> getImages()
	{
	
		return images;
	}
	public void setImages(ArrayList<MyImage> images)
	{
		this.images = images;
	}


    protected Review(Parcel in) {
        title = in.readString();
        type = in.readString();
        date = in.readString();
        year = in.readInt();
        body = in.readString();
        ID =in.readInt();
        vehicle_name = in.readString();
        in.readTypedList(images, MyImage.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(date);
        dest.writeInt(year);
        dest.writeInt(ID);
        dest.writeString(author);
        dest.writeString(source);
        dest.writeString(vehicle_name);
        dest.writeString(body);
        if (images == null)
        	images = new ArrayList<MyImage>();
		dest.writeTypedList(images);
    }

    @SuppressWarnings("unused")
    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}