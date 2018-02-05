package com.nw.webservice;

import android.content.Context;

import com.nw.model.DataInObject;
import com.nw.model.MarshalDouble;
import com.nw.model.VehicleObject;
import com.utils.Helper;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class WebserviceCall {

	int networkTimeOut = 120 * 1000;	//set timeout to call
    Context mContext;
    DataInObject mInObj;
    
    public WebserviceCall(Context context, DataInObject inObj) {
        mContext = context;
        mInObj=inObj;
    }
    
    public Object postDataToSOAPService() throws Exception 
    {
        //Create a SOAP Object.
        SoapObject request = new SoapObject(mInObj.getNamespace(), mInObj.getMethodname());

        // add property info to request
        for(int i=0;i<mInObj.getParameterList().size();i++)
        {
			  request.addProperty(mInObj.getParameterList().get(i));
        }
         
        ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<HeaderProperty>();
        headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
        
        //Create a serializable Object.
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.addMapping("http://schemas.datacontract.org/2004/07/StockServiceNS", "d4p1", VehicleObject.class);

        //SOAP is implemented in dotNet true/false.
        envelope.dotNet = true;
        envelope.setAddAdornments(false);
        envelope.implicitTypes = false;
        MarshalDouble md = new MarshalDouble();
      
        md.register(envelope);
        //Set request data into envelope and send request using HttpTransport
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(mInObj.getUrl(), networkTimeOut);
        
        androidHttpTransport.debug= true;
        androidHttpTransport.call(mInObj.getSoapAction(), envelope,headerPropertyArrayList);
        Helper.Log("Request",""+ androidHttpTransport.requestDump);
        Helper.Log("Response",""+ androidHttpTransport.responseDump);
      
        Object mresponse= envelope.getResponse();
        
        //set variable null to improve efficiency
        request = null;
        envelope = null;
        androidHttpTransport = null;
         
        return mresponse;
    	
    }
    
    public Object postDataToService() throws Exception 
    {
        ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<HeaderProperty>();
        headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
        
        //Create a serializable Object.
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.addMapping("http://schemas.datacontract.org/2004/07/StockServiceNS", "d4p1", VehicleObject.class);
        //SOAP is implemented in dotNet true/false.
        envelope.dotNet = true;
        MarshalDouble md = new MarshalDouble();
        envelope.implicitTypes = true;
        md.register(envelope);
        //Set request data into envelope and send request using HttpTransport
        envelope.setOutputSoapObject(mInObj.getSoapObject());
        HttpTransportSE androidHttpTransport = new HttpTransportSE(mInObj.getUrl(), networkTimeOut);
        
        androidHttpTransport.debug= true;
        androidHttpTransport.call(mInObj.getSoapAction(), envelope,headerPropertyArrayList);
        Helper.Log("Request",""+ androidHttpTransport.requestDump);
        Helper.Log("Response",""+ androidHttpTransport.responseDump);
      
        Object mresponse= envelope.getResponse();
        
        //set variable null to improve efficiency
        //request = null;
        envelope = null;
        androidHttpTransport = null;
         
        return mresponse;
    }
    
    
    
    
}
