package com.nw.model;

public class PlannerType 
{
	int activityId;
	String actvityPastName;
	String actvityFutureName;
	
	int type;
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	public String getActvityPastName() {
		return actvityPastName;
	}
	public void setActvityPastName(String actvityPastName) {
		this.actvityPastName = actvityPastName;
	}
	public String getActvityFutureName() {
		return actvityFutureName;
	}
	
	public void setActvityFutureName(String actvityFutureName) {
		this.actvityFutureName = actvityFutureName;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	@Override
	public String toString() 
	{
		if(type==0)
			return actvityPastName;
		else
			return actvityFutureName;
	}
}
