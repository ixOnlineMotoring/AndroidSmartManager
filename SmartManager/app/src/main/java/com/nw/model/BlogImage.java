package com.nw.model;

import android.os.Parcel;

public class BlogImage extends BaseImage  {
	
	int blogPostID;
	String originalFileName;

	public BlogImage() {}
	
	public BlogImage(Parcel source) {
		readParcel(source);
	}
	
	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public int getBlogPostID() {
		return blogPostID;
	}

	public void setBlogPostID(int blogPostID) {
		this.blogPostID = blogPostID;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeInt(id);
		dest.writeInt(priority);
		dest.writeString((link==null)?"":link);
		dest.writeInt(type);
		dest.writeInt((local==true)? 1 : 0);
		dest.writeString((path==null?"":path));
		dest.writeString((thumbPath==null)?"":thumbPath);
		dest.writeString((caption==null)?"":caption);
		dest.writeString((uri==null)?"":uri);		
		dest.writeInt((createNewFile==true) ? 1 : 0);
		
		dest.writeInt(blogPostID);
		dest.writeString(originalFileName==null?"":originalFileName);
	}

	@Override
	public void readParcel(Parcel in)
	{
		id= in.readInt();
		priority= in.readInt();
		link= in.readString();
		type= in.readInt();
		local= (in.readInt()!=0?true:false);
		path= in.readString();
		thumbPath= in.readString();
		caption= in.readString();
		uri=in.readString();
		createNewFile=(in.readInt()!=0?true:false);
		
		blogPostID= in.readInt();
		originalFileName= in.readString();
	}
	
	 public static final Creator<BlogImage> CREATOR= new Creator<BlogImage>() {

			@Override
			public BlogImage createFromParcel(Parcel source) {
				// TODO Auto-generated method stub
				return new BlogImage(source);
			}

			@Override
			public BlogImage[] newArray(int size) {
				// TODO Auto-generated method stub
				return new BlogImage[size];
			}
		};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}