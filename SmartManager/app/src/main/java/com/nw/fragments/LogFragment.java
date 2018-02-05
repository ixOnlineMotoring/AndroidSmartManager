package com.nw.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nw.adapters.ClientAdapter;
import com.nw.interfaces.DateListener;
import com.nw.interfaces.DialogListener;
import com.nw.model.Client;
import com.nw.model.LogClient;
import com.nw.model.PlannerType;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.activity.PlannerActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LogFragment extends BaseFragement implements OnClickListener, TextWatcher
{

	String []time={"5 mins","10 mins","15 mins","20 mins","25 mins",
				   "30 mins","35 mins","40 mins","45 mins","50 mins","55 mins","60 mins",
				   	"90 mins","2 hours","3 hours","4 hours","5 hours","6 hours","7 hours",
				   	"8 hours","9 hours","10 hours","11 hours","12 hours"};
	
	int selectedClientId;
	EditText edtClientFilter,edtClient,edtType,edtDetails,edtPerson,edtStartDate,edtTimeSpent,edtCheckIn;
	CheckBox cbInternal,cbToday;
	Button btnSave,btnCheckIn;
	Location location;
	TextView tvAddress;
	LogClient logClient;
	ArrayList<Client>clients;
	ArrayList<Client>allClients;
	ArrayList<PlannerType>plannerType;
	ClientAdapter clientAdapter;
	ArrayAdapter<PlannerType>plannerAdapter;
	boolean isShown=false;
	ListPopupWindow popupMenu;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View view=inflater.inflate(R.layout.fragment_log_activity, container,false);
		setHasOptionsMenu(true);
		edtClientFilter=(EditText) view.findViewById(R.id.edtClientFilter);
		edtClient=(EditText) view.findViewById(R.id.tvClient);
		edtType=(EditText) view.findViewById(R.id.tvType);
		edtDetails=(EditText) view.findViewById(R.id.edtDetails);
		edtPerson=(EditText) view.findViewById(R.id.tvPersonType);
		edtStartDate=(EditText) view.findViewById(R.id.edtDate);
		edtTimeSpent=(EditText) view.findViewById(R.id.edtTimeSpent);
		edtTimeSpent.setTag("-1");
		edtCheckIn=(EditText) view.findViewById(R.id.tvCheckIn);
		
		cbInternal=(CheckBox) view.findViewById(R.id.cbInternal);
		cbToday=(CheckBox) view.findViewById(R.id.cbToday);
		cbToday.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1){
				if(arg1)
					edtStartDate.setText("");
			}
		});
		
		btnSave=(Button) view.findViewById(R.id.btnSave);
		btnCheckIn=(Button) view.findViewById(R.id.btnCheckIn);
		tvAddress=(TextView) view.findViewById(R.id.tvAddress);
		edtClient.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnCheckIn.setOnClickListener(this);
		edtStartDate.setOnClickListener(this);
		edtTimeSpent.setOnClickListener(this);
		edtType.setOnClickListener(this);
		edtClientFilter.addTextChangedListener(this);
		
		if(allClients==null)
			allClients=new ArrayList<Client>();	
		
		hideKeyboard(view);
		return view;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	hideKeyboard();
        	getActivity().finish();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
	public void onResume() 
	{
		super.onResume();
		showActionBar(getActivity().getResources().getString(R.string.log_activity));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.btnCheckIn:
			location=((PlannerActivity)getActivity()).getUserLocation();
			if(location!=null)
				doGeoTag();
			else
			{
				 LocationManager lm = null;
				 boolean gps_enabled = false,network_enabled = false;
				    if(lm==null)
				        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
				    try
				    {
				    	gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
				    }
				    catch(Exception ex){}
				    try
				    {
				    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				    }
				    catch(Exception ex){}
				    
				    if(!gps_enabled && !network_enabled)
				    {
				    	CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.location_service_disable_message), new DialogListener()
						{
							@Override
							public void onButtonClicked(int type)
							{
								if(type==Dialog.BUTTON_POSITIVE)
								{
									 Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						             startActivity(callGPSSettingIntent);
								}
								else
								{
									tvAddress.setVisibility(View.VISIBLE);
									tvAddress.setText(getString(R.string.location_not_found));
								}
							}
						});
				    }
				    else
				    {
				    	Helper.showToast("Getting location..", getActivity());
				    }				
			}
			break;
		case R.id.edtDate:
			DatePickerFragment startDate = new DatePickerFragment();
			startDate.setDateListener(new DateListener() 
			{
				@Override
				public void onDateSet(int year, int monthOfYear,int dayOfMonth) 
				{
					edtStartDate.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
					cbToday.setChecked(false);
				}
			});
			startDate.show(getActivity().getFragmentManager(), "datePicker");
			break;
			
		case R.id.tvClient:
			if(clients!=null){
				if(!clients.isEmpty()){
					Helper.showDropDown(v, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, clients), new OnItemClickListener() {
		
						@Override
						public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
							edtClient.setText(clients.get(position)+"");
							selectedClientId= clients.get(position).getId();
						}
					});
				}
			}
			break;
		case R.id.edtTimeSpent:
			showSelectTimeDialog();
			break;
		case R.id.tvType:
			if(plannerType==null || plannerType.isEmpty()){
				showProgressDialog();
				getPlannerType();
			
			}else{
				showTypePopup(v);
			}
			break;
		case R.id.botttomLayout:	
		case R.id.btnSave:	
			saveLogActivity();
		}
	}
	
	private void showTypePopup(View v){
		if(plannerAdapter!=null)
		{
			Helper.showDropDown(v, plannerAdapter, new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,	int itemPosition, long arg3) 
				{
					edtType.setText(plannerAdapter.getItem(itemPosition).toString());
					edtType.setTag(""+plannerAdapter.getItem(itemPosition).getActivityId());
				}
			});
			
		}
		else
			Helper.showToast(getString(R.string.no_record_found), getActivity());
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		getClients();
		
	}
	
	private void showSelectTimeDialog()
	{
		
	
		/*final AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
	    builderSingle.setTitle("Select time spent");*/
	    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_text2,time);
		Helper.showDropDownYear(edtTimeSpent, arrayAdapter, new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				 edtTimeSpent.setText(""+arrayAdapter.getItem(arg2));
				 edtTimeSpent.setTag(arg2);
				
			}
		});
	  /*  builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() 
	    {
	    	@Override
	        public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
	        }
	    });

	    builderSingle.setSingleChoiceItems(arrayAdapter, Integer.parseInt(edtTimeSpent.getTag().toString()), new DialogInterface.OnClickListener()
	    {
			@Override
			public void onClick(DialogInterface arg0, int which) 
			{
				 edtTimeSpent.setText(""+arrayAdapter.getItem(which));
				 edtTimeSpent.setTag(which);
				 arg0.dismiss();
			}
		});
	    builderSingle.show();*/
	}
	
	private void doGeoTag()
	{
		if(HelperHttp.isNetworkAvailable(getActivity())){
			showProgressDialog();
			final StringBuilder soapBuilder=new StringBuilder();
			soapBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\"><Body><DoGeoTag xmlns=\""+Constants.TEMP_URI_NAMESPACE+"\">");
			soapBuilder.append("<userHash>"+DataManager.getInstance().user.getUserHash()+"</userHash>");
			/*soapBuilder.append("<googleLAtitude>-29.8369444</googleLAtitude>");
			soapBuilder.append("<googleLongitude>30.914722</googleLongitude>");*/
			soapBuilder.append("<googleLAtitude>"+location.getLatitude()+"</googleLAtitude>");
			soapBuilder.append("<googleLongitude>"+location.getLongitude()+"</googleLongitude>");
			soapBuilder.append("</DoGeoTag></Body></Envelope>");
			Helper.Log("doGeoTag request:", soapBuilder.toString());
			
			
			VollyResponseListener vollyResponseListener=new VollyResponseListener()
			{
				
				@Override
				public void onErrorResponse(VolleyError error)
				{
	            	hideProgressDialog();
	                Helper.Log("Error: ", error.getMessage());
				}
				
				@Override
				public void onResponse(String response)
				{
					
					if(response!=null)
	            	{
	            		Helper.Log("Response:", ""+response);
	                	logClient=ParserManager.parsedoGet(response);
	                	if(logClient!=null)
	                		allClients.addAll(logClient.getClients());
	                	if(!TextUtils.isEmpty(logClient.getAddress()))
	                	{
	                		tvAddress.setVisibility(View.VISIBLE);
	                		tvAddress.setText(""+logClient.getAddress());
	                	/*	logClient.setLongitude("-29.8369444");
	                		logClient.setLatetude("30.914722");*/
	                		logClient.setLongitude(""+location.getLongitude());
	                		logClient.setLatetude(""+location.getLatitude());
	                	}
	                	if(clientAdapter!=null)
	                	{
	                		clientAdapter=new ClientAdapter(getActivity(), R.layout.list_item_text2, allClients);
	                		clientAdapter.notifyDataSetChanged();
	                	}
	            	}
	            	hideProgressDialog();
				}
			};
			
			
			VollyCustomRequest request=new VollyCustomRequest( Constants.PLANNER_WEBSERVICE_URL,soapBuilder.toString(), Constants.TEMP_URI_NAMESPACE+"IPlannerService/DoGeoTag",vollyResponseListener);
			try
			{
				request.init("getBlogType");
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
	private void getClients()
	{
		if(clients!=null)
			clients.clear();
		
		if(HelperHttp.isNetworkAvailable(getActivity()))
			{
				final StringBuilder soapBuilder=new StringBuilder();
				soapBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\"><Body><ListAvailableClientsXML xmlns=\""+Constants.TEMP_URI_NAMESPACE+"\">");
				soapBuilder.append("<userHash>"+DataManager.getInstance().user.getUserHash()+"</userHash>");
				soapBuilder.append("</ListAvailableClientsXML></Body></Envelope>");
				
				Helper.Log("getClients request:", soapBuilder.toString());
				
				
				VollyResponseListener responseListener=new VollyResponseListener()
				{
					
					@Override
					public void onErrorResponse(VolleyError error)
					{
						Helper.Log("Error: ", error.getMessage());
		                getPlannerType();
						
					}
					
					@Override
					public void onResponse(String response)
					{
						if(response!=null)
		            	{
		            		Helper.Log("getClients Response:", ""+response);
		                	clients=ParserManager.parseClientList(response);
		                	if(clients!=null)
		                		allClients.addAll(clients);
		            	}
		            	if(getActivity()!=null){
			            	if(allClients!=null)
			            		clientAdapter=new ClientAdapter(getActivity(), R.layout.list_item_text2, allClients);
			            	else
			            		clientAdapter=new ClientAdapter(getActivity(), R.layout.list_item_text2, new ArrayList<Client>());
		            	}
						
					}
				};
				
				
				VollyCustomRequest request=new VollyCustomRequest(Constants.PLANNER_WEBSERVICE_URL,soapBuilder.toString(),Constants.TEMP_URI_NAMESPACE+"IPlannerService/ListAvailableClientsXML",responseListener);
				
				try
				{
					request.init("getClients");
				}
				catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}		
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
	private void getPlannerType()
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
			
			StringBuilder soapMessage=new StringBuilder();
			soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<ListPlannerTypeXML xmlns=\""+Constants.TEMP_URI_NAMESPACE+"\">");
			soapMessage.append("<userHash>"+DataManager.getInstance().user.getUserHash()+"</userHash>");
			soapMessage.append("<coreClientID>"+DataManager.getInstance().user.getDefaultClient().getId()+"</coreClientID>");
			soapMessage.append("</ListPlannerTypeXML>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");
			
			
			Helper.Log("getPlannerType request:", soapMessage.toString());
			
			
			VollyResponseListener vollyResponseListener=new VollyResponseListener()
			{
				
				@Override
				public void onErrorResponse(VolleyError error)
				{
					hideProgressDialog();
	            	Helper.Log("getPlannerType Error: ", error.getMessage());					
				}
				
				@Override
				public void onResponse(String response)
				{
					if(response==null){
	            		hideProgressDialog();
	            		return;
	            	}
	            	Helper.Log("getPlannerType Response:", response);
	            	plannerType=ParserManager.parsePlannerTypeList(response,0);
	            	if(plannerType!=null)
	            		plannerAdapter=new ArrayAdapter<PlannerType>(getActivity(), R.layout.list_item_text2, plannerType);
	            	hideProgressDialog();
	            	showTypePopup(edtType);
					
				}
			};
			
			VollyCustomRequest request=new VollyCustomRequest(Constants.PLANNER_WEBSERVICE_URL,soapMessage.toString(),Constants.TEMP_URI_NAMESPACE+"IPlannerService/ListPlannerTypeXML",vollyResponseListener);
			
			try
			{
				request.init("getPlannerType");
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(),getString(R.string.no_internet_connection));
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		/*if(!TextUtils.isEmpty(arg0))
		{
			if(clientAdapter!=null)
			{
				clientAdapter.getFilter().filter(arg0);
				showPopup();
			}
		}
		else
		{
			if(popupMenu!=null)
				popupMenu.dismiss();
		}*/
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
	{
		if(!TextUtils.isEmpty(arg0))
		{
			if(clientAdapter!=null)
			{
				clientAdapter.getFilter().filter(arg0);
				showPopup();
			}
		}
		else
		{
			if(popupMenu!=null)
				popupMenu.dismiss();
		}
	}
	
	private void showPopup()
	{
		if(popupMenu==null)
		{
			popupMenu=new ListPopupWindow(getActivity());
			popupMenu.setAnchorView(edtClient);
			popupMenu.setAdapter(clientAdapter);
			popupMenu.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,	int itemPosition, long arg3) 
				{
					popupMenu.dismiss();
					edtClient.setText(""+clientAdapter.getItem(itemPosition));
					edtClient.setTag(""+clientAdapter.getItem(itemPosition).getId());
					selectedClientId= clientAdapter.getItem(itemPosition).getId();
					isShown=false;
				}
			});
			popupMenu.show();
		}
		else
		{
			clientAdapter.notifyDataSetChanged();			
			if(!popupMenu.isShowing())
				popupMenu.show();
		}
	}

	public void saveLogActivity()
	{
		if(TextUtils.isEmpty(edtClient.getText()))
		{
			Helper.showToast(getString(R.string.please_select_client), getActivity());
			edtClient.requestFocus();
			return;
		}
		
		if(TextUtils.isEmpty(edtType.getText()))
		{
			Helper.showToast(getString(R.string.please_select_planner_type), getActivity());
			edtType.requestFocus();
			return;
		}
		
		if(TextUtils.isEmpty(edtDetails.getText()))
		{
			Helper.showToast(getString(R.string.please_enter_details), getActivity());
			edtDetails.requestFocus();
			return;
		}
		
		if(TextUtils.isEmpty(edtTimeSpent.getText()))
		{
			Helper.showToast(getString(R.string.please_select_time_spent), getActivity());
			return;
		}
		if(!cbToday.isChecked())
		{
			if(TextUtils.isEmpty(edtStartDate.getText()))
			{
				Helper.showToast(getString(R.string.please_select_date), getActivity());
				return;
			}
		}
		
		if(HelperHttp.isNetworkAvailable(getActivity()))
 		{
			showProgressDialog();
			final StringBuilder soapBuilder=new StringBuilder();
			soapBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapBuilder.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapBuilder.append("<Body><LogActivity xmlns=\""+Constants.TEMP_URI_NAMESPACE+"\">");
			soapBuilder.append("<userHash>"+DataManager.getInstance().user.getUserHash()+"</userHash>");
			soapBuilder.append("<coreClientID>"+selectedClientId+"</coreClientID>");
			soapBuilder.append("<plannerTypeID>"+edtType.getTag().toString()+"</plannerTypeID>");
			soapBuilder.append("<details>"+Helper.getCDATAString(edtDetails)+"</details>");
			soapBuilder.append("<isInternal>"+cbInternal.isChecked()+"</isInternal>");
			soapBuilder.append("<timeSpent>"+getMinutes(edtTimeSpent.getText().toString())+"</timeSpent>");
			soapBuilder.append("<isToday>"+cbToday.isChecked()+"</isToday>");
			if(cbToday.isChecked())
				soapBuilder.append("<alternateDate>"+getTodaysDate()+"</alternateDate>");
			else
				soapBuilder.append("<alternateDate>"+formateDateTime(edtStartDate.getText().toString(),"00:00")+"</alternateDate>");
			
			 if(logClient!=null)
		        {
					 soapBuilder.append("<locationLatitude>"+logClient.getLatetude()+"</locationLatitude>");
					 soapBuilder.append( "<locationLongitude>"+logClient.getLongitude()+"</locationLongitude>");
					 soapBuilder.append( "<locationAddress>"+logClient.getAddress()+"</locationAddress>");
		        }
			 else
			 {
				 soapBuilder.append("<locationLatitude>"+0+"</locationLatitude>");
				 soapBuilder.append( "<locationLongitude>"+0+"</locationLongitude>");
				 soapBuilder.append( "<locationAddress></locationAddress>");
			 }
			 soapBuilder.append("<contactPerson>"+Helper.getCDATAString(edtPerson)+"</contactPerson>");
			 soapBuilder.append("</LogActivity></Body></Envelope>");
			 
			 Helper.Log("saveLogActivity request:", soapBuilder.toString());
			
			 
			 VollyResponseListener responseListener=new VollyResponseListener()
			{
				
				@Override
				public void onErrorResponse(VolleyError error)
				{
					hideProgressDialog();
	            	Helper.Log("Error: ", error.getMessage());
	            	showErrorDialog(getString(R.string.log_activity), getString(R.string.could_not_log_activity));
					
				}
				
				@Override
				public void onResponse(String response)
				{
					hideProgressDialog();
	            	if(response==null){
	            		return;
	            	}
	            	Helper.Log("getClients Response:", response);
	            	String result=ParserManager.parseSaveLogActivityResponse(response);
	            	if(!TextUtils.isEmpty(result))
	            	{
	            		CustomDialogManager.showOkDialog(getActivity(), getString(R.string.log_activity_saved_successfully));
	            		resetData();
	            	}
	            	else{
	            		showErrorDialog(getString(R.string.log_activity), getString(R.string.could_not_log_activity));
	            	}
					
				}
			};
			 
			 
			VollyCustomRequest request=new VollyCustomRequest(Constants.PLANNER_WEBSERVICE_URL,soapBuilder.toString(),Constants.TEMP_URI_NAMESPACE+"IPlannerService/LogActivity",responseListener);
			try
			{
				request.init("saveLogActivity");
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}		
 		}
		else
			HelperHttp.showNoInternetDialog(getActivity());
	}

	private int getMinutes(String time)
	{
		if(TextUtils.isEmpty(time))
			return 0;
		else
		{
			if(time.contains("Min"))
				return Integer.parseInt(time.replace("Min","").trim());
			else if(time.contains("Hour")){
				return 60* Integer.parseInt(time.replace("Hour","").trim());
			}
		}
		return 0;
	}
	
	private String formateDateTime(String dateInString ,String timeInString)
	{
		//dd MMM yyyy
		String formattedDate="";
		try 
		{
			DateFormat originalFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
			Date date = originalFormat.parse(dateInString+" "+timeInString);
			String format="yyyy-MM-dd'T'HH:mm:ss";
			SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
			return sdf.format(date);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return formattedDate;
	}
	
	private String getTodaysDate()
	{
		String format="yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		return sdf.format(new Date());
	}

	private void resetData()
	{
		edtClientFilter.setText("");
		edtClient.setText("");
		edtClient.setTag(null);
		edtType.setText("");
		edtType.setTag(null);
		edtPerson.setText("");
		edtTimeSpent.setText("");
		edtStartDate.setText("");
		cbToday.setChecked(true);
		cbInternal.setChecked(false);
		edtDetails.setText("");
	}
}
