package com.nw.model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.ArrayList;
import java.util.Hashtable;

public class VehicleObject extends ArrayList<Parameter>  implements KvmSerializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3030309940013710006L;
	ArrayList<Parameter> parameters=new ArrayList<Parameter>();
	public VehicleObject() 
	{
		
	}
	
	@Override
	public Object getProperty(int arg0) 
	{
		// TODO Auto-generated method stub
		return parameters.get(arg0);
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return parameters.size();
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo arg2) 
	{
		arg2.name = parameters.get(arg0).name;
        arg2.type = parameters.get(arg0).type;
	}

	@Override
	public void setProperty(int arg0, Object arg1) 
	{
	}

	

}
