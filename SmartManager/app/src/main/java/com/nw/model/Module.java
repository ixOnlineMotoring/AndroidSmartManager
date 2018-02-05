package com.nw.model;

import java.util.ArrayList;

public class Module 
{
	private String name;
	private boolean quickLink;
	private ArrayList<Page>pages;
	private ArrayList<SubModule>subModules;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isQuickLink() {
		return quickLink;
	}
	public void setQuickLink(boolean quickLink) {
		this.quickLink = quickLink;
	}
	public ArrayList<Page> getPages() 
	{
		if(pages==null)
			pages=new ArrayList<Page>();
		return pages;
	}
	public void setPages(ArrayList<Page> pages) {
		this.pages = pages;
	}
	public ArrayList<SubModule> getSubModules()
	{
		if(subModules==null)
			subModules=new ArrayList<SubModule>();
		return subModules;
	}
	public void setSubModules(ArrayList<SubModule> subModules)
	{

		this.subModules = subModules;
	}
}
