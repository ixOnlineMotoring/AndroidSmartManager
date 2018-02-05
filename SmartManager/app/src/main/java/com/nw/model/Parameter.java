package com.nw.model;

import org.ksoap2.serialization.PropertyInfo;

public class Parameter extends PropertyInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3192617741523437266L;

	public Parameter(String key, Object value, Class<?> type) {
		super();
		this.name = key;
		this.value = value;
		this.type = type;
	}
	
	public Parameter(String key, Object value, Class<?> type,String namespace) 
	{
		super();
		this.name = key;
		this.value = value;
		this.type = type;
		this.namespace=namespace;
	}

	
}
