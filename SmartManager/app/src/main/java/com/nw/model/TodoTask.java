package com.nw.model;

import com.nw.adapters.CustomGridAdapter;

import java.util.ArrayList;


public class TodoTask 
{
	
	int id;
	String title;
	String dueDate;
	boolean overdue=false,
			newItem=true;
	String author;
	String createdDate;
	String details;
	String message;
	int state; 
	String openTag;
	boolean detailsAvailabe=false;
	String assignee;
	int taskTargetClientID;
	String taskTargetClientName;
	boolean status;
	ArrayList<String> imageList;
	CustomGridAdapter adapter;
	
	public CustomGridAdapter getAdapter() {
		return adapter;
	}
	public void setAdapter(CustomGridAdapter adapter) {
		this.adapter = adapter;
	}
	public ArrayList<String> getImageList() {
		return imageList;
	}
	public void setImageList(ArrayList<String> imageList) {
		this.imageList = imageList;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public int getTaskTargetClientID() {
		return taskTargetClientID;
	}
	public void setTaskTargetClientID(int taskTargetClientID) {
		this.taskTargetClientID = taskTargetClientID;
	}
	public String getTaskTargetClientName() {
		return taskTargetClientName;
	}
	public void setTaskTargetClientName(String taskTargetClientName) {
		if(!taskTargetClientName.equals("anyType{}"))
			this.taskTargetClientName = taskTargetClientName;
		else
			this.taskTargetClientName="";
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	
	public boolean isOverdue() {
		return overdue;
	}
	
	public void setOverdue(boolean overdue) {
		this.overdue = overdue;
	}
	
	public boolean isNewItem() {
		return newItem;
	}
	
	public void setNewItem(boolean newItem) {
		this.newItem = newItem;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		if(details.equals("anyType{}"))
			this.details="No details available";
		else
			this.details = details;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public boolean isDetailsAvailabe() {
		return detailsAvailabe;
	}
	public void setDetailsAvailabe(boolean detailsAvailabe) {
		this.detailsAvailabe = detailsAvailabe;
	}
	@Override
	public String toString() {
		return "TodoTask [id=" + id + ", title=" + title + ", dueDate="
				+ dueDate + ", overdue=" + overdue + ", newItem=" + newItem
				+ ", author=" + author + ", createdDate=" + createdDate
				+ ", details=" + details + ", message=" + message + ", state="
				+ state + ", detailsAvailabe=" + detailsAvailabe + "]";
	}
	public String getOpenTag() {
		return openTag;
	}
	public void setOpenTag(String openTag) {
		this.openTag = openTag;
	}
	
	
}
