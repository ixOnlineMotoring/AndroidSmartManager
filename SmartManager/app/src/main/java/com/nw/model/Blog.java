package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Blog implements Parcelable
{

	private static long serialVersionUID = 1L;
	private int blogPostID;
	private String title;
	private String details;
	private String blogType;
	private String createdDate;
	private String publishDate;
	private String endDate;
	private String name;
	private String imagePath;
	int totalCount;
	private int imageCount;
	private String endStatus;
	boolean active;
	String author;
	int blogPostTypeId;
	ArrayList<BaseImage> gridImages;

	public Blog(){}

	public Blog(Parcel source) {
		// TODO Auto-generated constructor stub
		readParcel(source);
	}

	public int getBlogPostID() {
		return blogPostID;
	}
	public void setBlogPostID(int blogPostID) {
		this.blogPostID = blogPostID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public int getImageCount() {
		return imageCount;
	}
	public void setImageCount(int imageCount) {
		this.imageCount = imageCount;
	}
	public String getEndStatus() {
		return endStatus;
	}
	public void setEndStatus(String column1) {
		this.endStatus = column1;
	}

	public ArrayList<BaseImage> getGridImages() {
		return gridImages;
	}

	public void setGridImages(ArrayList<BaseImage> gridImages) {
		this.gridImages = gridImages;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	public int getBlogPostTypeId() {
		return blogPostTypeId;
	}

	public void setBlogPostTypeId(int blogPostTypeId) {
		this.blogPostTypeId = blogPostTypeId;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalCount() {
		return totalCount;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeLong(serialVersionUID);
		dest.writeInt(blogPostID);
		dest.writeString(title);
		dest.writeString(details);
		dest.writeString(createdDate);
		dest.writeString(publishDate);
		dest.writeString(endDate);
		dest.writeString(name);
		dest.writeString(blogType);
		dest.writeString(imagePath);
		dest.writeInt(totalCount);
		dest.writeInt(imageCount);
		dest.writeString(endStatus);
		dest.writeByte((byte) (active ? 1 : 0)); 
		dest.writeString(author);
		dest.writeInt(blogPostTypeId);
		dest.writeTypedList(gridImages);
	}

	private void readParcel(Parcel in){
		serialVersionUID= in.readLong();
		blogPostID= in.readInt();
		title= in.readString();
		details= in.readString();
		createdDate= in.readString();
		publishDate= in.readString();
		endDate= in.readString();
		name= in.readString();
		blogType =  in.readString();
		imagePath= in.readString();
		totalCount= in.readInt();
		imageCount= in.readInt();
		endStatus= in.readString();
		active= in.readByte()!=0;
		author= in.readString();
		blogPostTypeId= in.readInt();
		in.readTypedList(gridImages, BaseImage.CREATOR);


	}

	public String getBlogType()
	{

		return blogType;
	}

	public void setBlogType(String blogType)
	{

		this.blogType = blogType;
	}

	public static final Creator<Blog> CREATOR= new Creator<Blog>() {

		@Override
		public Blog createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Blog(source);
		}

		@Override
		public Blog[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Blog[size];
		}
	};
}
