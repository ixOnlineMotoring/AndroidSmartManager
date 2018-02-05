package com.nw.model;

public class MessageListing
{
	public MessageListing(String name, String date, String message)
	{

		super();
		this.name = name;
		Date = date;
		Message = message;
	}

	public MessageListing()
	{

	}

	private String name;
	private String Date;
	private String Message;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getMessage()
	{
		return Message;
	}

	public void setMessage(String message)
	{
		Message = message;
	}

	public String getDate()
	{
		return Date;
	}

	public void setDate(String date)
	{
		Date = date;
	}

}
