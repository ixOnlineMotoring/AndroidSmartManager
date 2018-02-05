package com.nw.model;

public class Member 
{
	int id,memberID;
	
	
	String name;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getMemberID()
	{
	
		return memberID;
	}
	public void setMemberID(int memberID)
	{
	
		this.memberID = memberID;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}
}
