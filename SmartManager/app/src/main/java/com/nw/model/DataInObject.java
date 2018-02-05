package com.nw.model;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class DataInObject {
	
	String url;
	String soapAction;
	String namespace;
	String methodname;
	ArrayList<Parameter> parameterList;
	SoapObject soapObject;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSoapAction() {
		return soapAction;
	}
	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getMethodname() {
		return methodname;
	}
	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}
	public ArrayList<Parameter> getParameterList() {
		return parameterList;
	}
	public void setParameterList(ArrayList<Parameter> parameterList) {
		this.parameterList = parameterList;
	}
	
	public SoapObject getSoapObject()
	{
		return soapObject;
	}
	
	public void setSoapObject(SoapObject soapObject)
	{
		this.soapObject = soapObject;
	}
	
	
}
