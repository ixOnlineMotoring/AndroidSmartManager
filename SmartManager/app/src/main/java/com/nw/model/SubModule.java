package com.nw.model;

import java.util.ArrayList;

public class SubModule extends Page
{
	private String subModuleName;
	private ArrayList<Page>pages;
	
	public String getSubModuleName() {
		return subModuleName;
	}
	public void setSubModuleName(String name) {
		this.subModuleName = name;
	}
	
	public ArrayList<Page> getSubPages() 
	{
		if(pages==null)
			pages=new ArrayList<Page>();
		return pages;
	}
	public void setSubPages(ArrayList<Page> pages) {
		this.pages = pages;
	}
	
	
}