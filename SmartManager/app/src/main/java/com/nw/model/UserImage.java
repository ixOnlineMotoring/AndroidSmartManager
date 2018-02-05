package com.nw.model;

public class UserImage
{
	String url;
	String height;
	
	public UserImage()
	{
		// TODO Auto-generated constructor stub
	}
	
	
	
	public UserImage(String url, String height)
	{
		super();
		this.url = url;
		this.height = height;
	}



	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public String getHeight()
	{
		return height;
	}
	public void setHeight(String height)
	{
		this.height = height;
	}
	
	
}
