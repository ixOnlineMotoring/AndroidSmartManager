package com.nw.webservice;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.nw.model.DataInObject;
import com.nw.widget.CustomDialogManager;

public class WebServiceTask extends AsyncTask<String, String, Object> {
 
    Context mContext;
    TaskListener mListener;
    Boolean mProgress = true;
    DataInObject mInObj;
    Dialog proDialog;
    
    public WebServiceTask(Context context,DataInObject inObj,  boolean progress, TaskListener listener) 
    {
        mContext = context;
        mListener=listener;
        mProgress = progress;
        mInObj=inObj;
    }
 
    @Override
    protected void onPreExecute() 
    {
        super.onPreExecute();
        if (mProgress) 
        	proDialog=CustomDialogManager.showProgressDialog(mContext);
    }
 
    @Override
    protected Object doInBackground(String... params) {
        Object result = null;
        try 
        {
        	if(mInObj.getSoapObject()==null)        	
        		result = new WebserviceCall(mContext, mInObj).postDataToSOAPService();
        	else
        		result = new WebserviceCall(mContext, mInObj).postDataToService();
        		
        } 
        catch (Exception ee) 
        {
        	ee.printStackTrace();
        }
        return result;
    }
 
    @Override
    protected void onPostExecute(Object result) 
    {
        super.onPostExecute(result);
        if (mProgress) 
        	proDialog.dismiss();
        if(mListener!=null)
        	mListener.onTaskComplete(result);
    }
 
}
