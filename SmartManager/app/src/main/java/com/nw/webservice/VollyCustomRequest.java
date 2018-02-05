package com.nw.webservice;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom String Request Library 
 * @author Shivaji.Sul
 *
 */
public class VollyCustomRequest 
{
	public int requestType=Request.Method.POST;
	public static final String CONTENT_TYPE="text/xml;charset=utf-8";
	public static final String APPLICATION_CONTENT_TYPE="application/soap+xml;charset=utf-8";
	public static final String CHARACTER_SET="UTF-8";
	
	private String webserviceURL;	
	
	private VollyResponseListener vollyResponseListener;
	
	private String soapMessage, soapAction;
	
	public VollyCustomRequest()
	{
		
	}
	
	public VollyCustomRequest(int requestType)
	{
		this.requestType=requestType;
	}
	
	public VollyCustomRequest(String webserviceURL)
	{
		this.webserviceURL=webserviceURL;
	}
	
	public VollyCustomRequest(String webserviceURL,final String soapMessage ,final String soapAction)
	{
		this.webserviceURL=webserviceURL;
		this.soapMessage=soapMessage;
		this.soapAction=soapAction;
	}
	
	public VollyCustomRequest(String webserviceURL,final String soapMessage ,final String soapAction,VollyResponseListener vollyResponseListener)
	{
		this.webserviceURL=webserviceURL;
		this.soapMessage=soapMessage;
		this.soapAction=soapAction;
		this.vollyResponseListener=vollyResponseListener;
	}
	
	public void init() throws Exception
	{
		
		if(webserviceURL==null)
		{
			throw new Exception("webserviceURL can not be null");
		}
		
		if(soapMessage==null)
		{
			throw new Exception("soapMessage can not be null");
		}
		
		if(soapAction==null)
		{
			throw new Exception("soapAction can not be null");
		}
		
		
		StringRequest stringRequest=new StringRequest(requestType,webserviceURL, vollyResponseListener, vollyResponseListener)
		{
			@Override
			public String getBodyContentType() 
			{
				return CONTENT_TYPE;
			}
			
			@Override
	        public byte[] getBody() throws AuthFailureError 
	          {
	            	try 
	            	{
						return soapMessage.getBytes(CHARACTER_SET);
					} 
	            	catch (UnsupportedEncodingException e) 
					{
						e.printStackTrace();
					}
	            	catch (Exception e) 
					{
						e.printStackTrace();
					}
					return null;
	            }
		
			 @Override
	            public Map<String, String> getHeaders() throws AuthFailureError 
	            {
	                Map<String, String> headers = new HashMap<String, String>();
	                headers.put("SOAPAction", soapAction);
	                headers.put("Content-Type", APPLICATION_CONTENT_TYPE);
	                headers.put("Content-Type", CONTENT_TYPE);
	                //headers.put("Content-Length", "" + soapMessage.length());
	                return headers;
	            }
		};		
		VolleySingleton.getInstance().addToRequestQueue(stringRequest);
		
	}
	
	public void init(String tag) throws Exception
	{
		
		if(webserviceURL==null)
		{
			throw new Exception("webserviceURL can not be null");
		}
		
		if(soapMessage==null)
		{
			throw new Exception("soapMessage can not be null");
		}
		
		if(soapAction==null)
		{
			throw new Exception("soapAction can not be null");
		}
		
		
		StringRequest stringRequest=new StringRequest(requestType,webserviceURL, vollyResponseListener, vollyResponseListener)
		{
			@Override
			public String getBodyContentType() 
			{
				return CONTENT_TYPE;
			}
			
			@Override
	        public byte[] getBody() throws AuthFailureError 
	          {
	            	try 
	            	{
						return soapMessage.getBytes(CHARACTER_SET);
					} 
	            	catch (UnsupportedEncodingException e) 
					{
						e.printStackTrace();
					}
	            	catch (Exception e) 
					{
						e.printStackTrace();
					}
					return null;
	            }
		
			 @Override
	            public Map<String, String> getHeaders() throws AuthFailureError 
	            {
	                Map<String, String> headers = new HashMap<String, String>();
	                headers.put("SOAPAction", soapAction);
	                headers.put("Content-Type", APPLICATION_CONTENT_TYPE);
	                headers.put("Content-Type", CONTENT_TYPE);
	                headers.put("Content-Length", "" + soapMessage.length());
	                return headers;
	            }
		};		
		VolleySingleton.getInstance().addToRequestQueue(stringRequest,tag);
		
	}
	
	public void init(final String soapMessage ,final String soapAction) throws Exception
	{
		
		if(webserviceURL==null)
		{
			throw new Exception("webserviceURL can not be null");
		}
		
		if(soapMessage==null)
		{
			throw new Exception("soapMessage can not be null");
		}
		
		if(soapAction==null)
		{
			throw new Exception("soapAction can not be null");
		}
		
		
		StringRequest stringRequest=new StringRequest(requestType,webserviceURL, vollyResponseListener, vollyResponseListener)
		{
			@Override
			public String getBodyContentType() 
			{
				return CONTENT_TYPE;
			}
			
			@Override
	        public byte[] getBody() throws AuthFailureError 
	          {
	            	try 
	            	{
						return soapMessage.getBytes(CHARACTER_SET);
					} 
	            	catch (UnsupportedEncodingException e) 
					{
						e.printStackTrace();
					}
	            	catch (Exception e) 
					{
						e.printStackTrace();
					}
					return null;
	            }
		
			 @Override
	            public Map<String, String> getHeaders() throws AuthFailureError 
	            {
	                Map<String, String> headers = new HashMap<String, String>();
	                headers.put("SOAPAction", soapAction);
	                headers.put("Content-Type", APPLICATION_CONTENT_TYPE);
	                headers.put("Content-Type", CONTENT_TYPE);
	                headers.put("Content-Length", "" + soapMessage.length());
	                return headers;
	            }
		};		
		VolleySingleton.getInstance().addToRequestQueue(stringRequest);
		
	}

	public void setVollyResponseListener(VollyResponseListener vollyResponseListener)
	{
		this.vollyResponseListener=vollyResponseListener;
	}
	
}
