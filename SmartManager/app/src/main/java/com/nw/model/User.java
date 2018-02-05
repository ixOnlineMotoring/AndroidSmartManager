package com.nw.model;

import java.util.ArrayList;

public class User 
{
	private int identtity;
	private String name;
	private String surName;
	private int memberId;
	private Client client;
	private String userHash;
	private ArrayList<Module>modules;
	private Impersonate impersonate;
	private Client defaultClient;	
	private boolean authenticated;
	private String failureReason;
	private ArrayList<UserImage>memberImages;
	private ArrayList<UserImage>clientImages;
	private ArrayList<SubModule> subModules;
	private String notificationIdentifier;
	
	public int getIdenttity() {
		return identtity;
	}
	public void setIdenttity(int identtity) {
		this.identtity = identtity;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurName() {
		return surName;
	}
	public void setSurName(String surName) {
		this.surName = surName;
	}
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public String getUserHash() {
		return userHash;
	}
	public void setUserHash(String userHash) {
		this.userHash = userHash;
	}
	public ArrayList<Module> getModules()
	{
		if(modules==null)
			modules=new ArrayList<Module>();
		return modules;
	}
	public void setModules(ArrayList<Module> modules) {
		this.modules = modules;
	}
	public boolean isAuthenticated() {
		return authenticated;
	}
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	public String getFailureReason() {
		return failureReason;
	}
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	
	public Impersonate getImpersonate() {
		return impersonate;
	}
	
	public void setImpersonate(Impersonate impersonate) {
		this.impersonate = impersonate;
	}
	
	public Client getDefaultClient() {
		return defaultClient;
	}
	
	public void setDefaultClient(Client defaultClient) {
		this.defaultClient = defaultClient;
	}
	
	public ArrayList<UserImage> getClientImages()
	{
		if(clientImages==null)
			clientImages=new ArrayList<UserImage>();
		return clientImages;
	}
	public void setClientImages(ArrayList<UserImage> clientImages)
	{
		this.clientImages = clientImages;
	}
	
	public ArrayList<UserImage> getMemberImages()
	{
		if(memberImages==null)
			memberImages=new ArrayList<UserImage>();
		return memberImages;
	}
	
	public void setMemberImages(ArrayList<UserImage> memberImages)
	{
		this.memberImages = memberImages;
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
	public String getNotificationIdentifier()
	{

		return notificationIdentifier;
	}
	public void setNotificationIdentifier(String notificationIdentifier)
	{

		this.notificationIdentifier = notificationIdentifier;
	}
}
