package com.utils;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nw.interfaces.DialogListener;
import com.nw.webservice.DataManager;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * @author NRT
 *         <p>
 *         HelperHttp class purpose is to make network related operations across
 *         the app.
 *         </p>
 *         Dependancy: No Dependancy
 * */
public class HelperHttp {
	static Dialog dialog= null;

	public static HttpClient httpclient;

	private static List<NameValuePair> buildNameValuePair(
			Hashtable<String, String> httpPost) {
		if (httpPost == null)
			return null;

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Enumeration<String> keys = httpPost.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = httpPost.get(key);
			BasicNameValuePair nv = new BasicNameValuePair(key, value);
			nvps.add(nv);
		}

		return nvps;
	}

	private static String buildGetUrl(List<NameValuePair> params, String url) {
		String paramString = URLEncodedUtils.format(params, "utf-8");
		if (!url.endsWith("?"))
			url += "?";

		url += paramString;
		return url;
	}

	public static DefaultHttpClient getThreadSafeClient() {
		if (httpclient != null)
			return (DefaultHttpClient) httpclient;
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 100);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		// Set the timeout in milliseconds until a connection is
		// established.
		int timeoutConnection = 30000;
		HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 300000;
		HttpConnectionParams.setSoTimeout(params, timeoutSocket);
		// Create and initialize scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,schemeRegistry);
		httpclient = new DefaultHttpClient(cm, params);

		return (DefaultHttpClient) httpclient;
	}
	
	public static String uploadVideoFile(File file, int usedVehicleStockID,String title,String description,String tags,boolean isSearchable)
	{
		String youtubeVideoName = null;
		StringBuffer data = new StringBuffer();
		HttpPost httpost = new HttpPost(Constants.VIDEO_WEBSERVICE);
		httpost.addHeader("userHash", DataManager.getInstance().user.getUserHash());
		httpost.addHeader("Client", ""+DataManager.getInstance().user.getDefaultClient().getId());
		httpost.addHeader("title", title);
		httpost.addHeader("description",description);
		httpost.addHeader("tags", tags);
		httpost.addHeader("fileName", StringUtils.substringAfterLast(file.getAbsolutePath(), "/"));
		httpost.addHeader("usedVehicleStockID", ""+usedVehicleStockID);
		if (isSearchable)
		{
			httpost.addHeader("searchable", "true");
		}else {
			httpost.addHeader("searchable", "false");
		}
		Helper.Log("Upload file", Constants.VIDEO_WEBSERVICE);
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("uploadfile", new FileBody(file));
		httpost.setEntity(entity);
		HttpResponse response;
		try

		{
			response = getThreadSafeClient().execute(httpost);
			Helper.Log("Upload file response",""+ response.getStatusLine());
			HttpEntity entity2 = response.getEntity();

			youtubeVideoName = EntityUtils.toString(entity2, "UTF-8");
			System.out.println(youtubeVideoName);
			Helper.Log("Response==>", data.toString() + "");
            if (youtubeVideoName.contains("<Errors>")){
                return null;
            }
			return youtubeVideoName;
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
			return null;
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String uploadVideoVariantFile(File file, int variantID,String title,String description,String tags,boolean isSearchable)
	{
		String youtubeVideoName = null;
		StringBuffer data = new StringBuffer();
		HttpPost httpost = new HttpPost(Constants.VIDEO_WEBSERVICE);
		httpost.addHeader("userHash", DataManager.getInstance().user.getUserHash());
		httpost.addHeader("Client", ""+DataManager.getInstance().user.getDefaultClient().getId());
		httpost.addHeader("title", title);
		httpost.addHeader("description",description);
		httpost.addHeader("tags", tags);
		httpost.addHeader("fileName", StringUtils.substringAfterLast(file.getAbsolutePath(), "/"));
		httpost.addHeader("variantID", ""+variantID);
		if (isSearchable)
		{
			httpost.addHeader("searchable", "true");
		}else {
			httpost.addHeader("searchable", "false");
		}
		Helper.Log("Upload file", Constants.VIDEO_WEBSERVICE);
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("uploadfile", new FileBody(file));
		httpost.setEntity(entity);
		HttpResponse response;
		try
		{
			response = getThreadSafeClient().execute(httpost);
			Helper.Log("Upload file response",""+ response.getStatusLine());
			HttpEntity entity2 = response.getEntity();
			youtubeVideoName = EntityUtils.toString(entity2, "UTF-8");
			System.out.println(youtubeVideoName);
			Helper.Log("Response==>", data.toString() + "");
            if (youtubeVideoName.contains("<Errors>")){
                return null;
            }
			return youtubeVideoName;
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
			return null;
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	

	public static String getJSONResponseFromURL(String url,	Hashtable<String, String> httpGetParams) 
	{
		StringBuffer json_string = new StringBuffer();
		if (httpGetParams != null) {
			List<NameValuePair> nvps = buildNameValuePair(httpGetParams);
			url = buildGetUrl(nvps, url);
		}
		Helper.Log("URL==>", url);
		InputStream is = null;
		try {
			HttpGet httpget = new HttpGet(url.replace(" ", "%20"));
			httpget.setHeader("Accept", "application/json");
			httpget.setHeader("Content-type", "application/json");
			HttpResponse response = getThreadSafeClient().execute(httpget);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			BufferedReader reader = new BufferedReader(	new InputStreamReader(is), 8 * 1024);
			String line = null;
			while ((line = reader.readLine()) != null) {
				json_string.append(line);
			}
			response.getEntity().consumeContent();
			Helper.Log("Json Response==>", json_string.toString());
		} catch (ConnectTimeoutException cte) {
			String st = "{\"status\":\"0\",\"msg\":\"Request Timed out\"}";
			return st;
		} catch (SocketTimeoutException ste) {
			Helper.Log("log_tag",
					"Error in socket time out connection" + ste.toString());
			return "{\"status\":\"0\",\"msg\":\"Request Timed out\"}";
		} catch (Exception e) {
			Helper.Log("log_tag", "Error in http connection" + e.toString());
			return "{\"status\":\"0\"}";
		}
		return json_string.toString();
	}

	public static String getJSONResponse(String url, String data) {
		StringBuffer json_string = new StringBuffer();

		StringEntity stringEntity = null;
		try {
			stringEntity = new StringEntity(data, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Helper.Log("URL==>", url);
		Helper.Log("PARAM==>", data);
		InputStream is;
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(stringEntity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			HttpResponse response = getThreadSafeClient().execute(httpPost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is), 8 * 1024);
			String line = null;
			while ((line = reader.readLine()) != null) {
				json_string.append(line);
			}
			response.getEntity().consumeContent();
			Helper.Log("Json Response==>", json_string.toString());
		} catch (ConnectTimeoutException cte) {
			String st = "{\"status\":\"0\",\"msg\":\"Request Timed out\"}";
			return st;
		} catch (Exception e) {
			Helper.Log("log_tag", "Error in http connection" + e.toString());
			String st = "{\"status\":\"0\",\"msg\":\"Request Timed out\"}";
			return st;
		}
		return json_string.toString();
	}

	public static String getJSONResponseFromPOSTURL(String url,List<NameValuePair> nvps) 
	{
		StringBuffer json_string = new StringBuffer();
		String s = "";

		for (NameValuePair nvp : nvps) 
		{
			s = s + nvp.getName() + "=" + nvp.getValue() + "&";
		}
		Helper.Log("URL==>", url);
		Helper.Log("PARAM==>", s);
		InputStream is = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			HttpResponse response = getThreadSafeClient().execute(httpPost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is), 8 * 1024);
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				json_string.append(line);
			}
			response.getEntity().consumeContent();
			Helper.Log("Json Response==>", json_string.toString());
		} catch (ConnectTimeoutException cte) {
			String st = "{\"status\":\"0\",\"msg\":\"Request Timed out\"}";
			return st;
		} catch (Exception e) {
			Helper.Log("log_tag", "Error in http connection" + e.toString());
			String st = "{\"status\":\"0\",\"msg\":\"Request Timed out\"}";
			return st;
		}
		return json_string.toString();
	}

	public static void showNoInternetDialog(Context context) 
	{
		CustomDialogManager.showOkDialog(context, context.getText(R.string.app_name),context.getText(R.string.no_internet_connection));
	}
	
	public static void showNoInternetDialog(Context context,DialogListener dialogListener) 
	{
		CustomDialogManager.showOkDialog(context, context.getText(R.string.no_internet_connection),dialogListener);
	}

	public static boolean isNetworkAvailable(Context context) 
	{
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) 
		{
			return false;
		}
		else 
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) 
					{
						return true;
					}
				}
			}
		}

		return false;
	}

}
