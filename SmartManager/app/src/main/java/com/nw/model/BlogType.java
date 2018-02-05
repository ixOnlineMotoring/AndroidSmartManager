package com.nw.model;

public class BlogType 
{
	private boolean active;
	private int blogPostTypeID;
	private String name;
	private int order;
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getBlogPostTypeID() {
		return blogPostTypeID;
	}
	public void setBlogPostTypeID(int blogPostTypeID) {
		this.blogPostTypeID = blogPostTypeID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}
	
}
