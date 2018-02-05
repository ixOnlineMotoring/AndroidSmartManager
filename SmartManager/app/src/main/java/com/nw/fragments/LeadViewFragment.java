package com.nw.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nw.interfaces.DateListener;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Leads;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.VehicleType;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.activity.CustomerActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Date;

public class LeadViewFragment extends BaseFragement implements OnClickListener
{
	TextView tvProspect, tvPhone, tvEmail, tvEnquiredOn, tvTiming, tvSource, tvDate;
	TextView tvLastUpdate, tvCurrentVehicle, tvYearModel, tvMileage;
	LinearLayout llSoldDelivered;
	EditText edActivity, edComment,edGender,edRaces,edVehicleDept,edVehicleType,edInvoiceNo,edDate,edInclusive,
										edInvoiceTo,edSalesPerson,edStockCode,edAge;
	CheckBox cbChangeStatus;
	Leads leads;
	Button bSubmit;
	String [] gender ={"Male","Female"},
			  races ={"Unknown","Black","Coloured","Indian","Oriental","White"},
			  type ={"Cars","Motorcycles","Trucks","Boats","Outboard Motors","Jetskis","Tractors"},
			  department = {"New","Used"};
	int [] type_id = {1,2,3,4,6,7,12},race_id = {0,1,2,3,4,5};
	int selectedRaceID=-1,selectedTypeID=0,selectedDepartmentID=0,selectedGenderID=0;
	String selected_date="";

	/** One second (in milliseconds) */
	private static final int _A_SECOND = 1000;
	/** One minute (in milliseconds) */
	private static final int _A_MINUTE = 60 * _A_SECOND;
	/** One hour (in milliseconds) */
	private static final int _AN_HOUR = 60 * _A_MINUTE;
	/** One day (in milliseconds) */
	private static final int _A_DAY = 24 * _AN_HOUR;
	private int RECORD_CALL=101,RECORD_EMAIL=102;
	ArrayList<SmartObject> activityOptions;
	LinearLayout llTradeIn,llUpdateNow;
	boolean isSoldandDelivered=false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_lead_view, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		getLeadsDetails();
		return view;
	}

	private void initialise(View view){
		tvProspect = (TextView) view.findViewById(R.id.tvProspectValue);
		tvPhone = (TextView) view.findViewById(R.id.tvPhoneValue);
		tvEmail = (TextView) view.findViewById(R.id.tvEmailValue);
		tvEnquiredOn = (TextView) view.findViewById(R.id.tvEnquiredValue);
		tvTiming = (TextView) view.findViewById(R.id.tvTimeValue);
		tvSource = (TextView) view.findViewById(R.id.tvSourceValue);
		tvDate = (TextView) view.findViewById(R.id.tvDateValue);
		tvLastUpdate = (TextView) view.findViewById(R.id.tvLastUpdateValue);
		tvCurrentVehicle = (TextView) view.findViewById(R.id.tvCurrentVehicleValue);
		tvYearModel = (TextView) view.findViewById(R.id.tvYearModeValue);
		tvMileage = (TextView) view.findViewById(R.id.tvMilageValue);	
		edActivity = (EditText) view.findViewById(R.id.edActivity);
		edComment = (EditText) view.findViewById(R.id.edComment);
		edInvoiceNo= (EditText) view.findViewById(R.id.edInvoiceNo);
		edDate= (EditText) view.findViewById(R.id.edDate);
		edInclusive= (EditText) view.findViewById(R.id.edInclusive);
		edInvoiceTo= (EditText) view.findViewById(R.id.edInvoiceTo);
		edSalesPerson= (EditText) view.findViewById(R.id.edSalesPerson);
		edStockCode= (EditText) view.findViewById(R.id.edStockCode);
		edGender = (EditText) view.findViewById(R.id.edGener);
		edRaces = (EditText) view.findViewById(R.id.edRaces);
		edVehicleDept = (EditText) view.findViewById(R.id.edVehicleDept);
		edVehicleType= (EditText) view.findViewById(R.id.edVehicleType);
		edRaces = (EditText) view.findViewById(R.id.edRaces);
		edAge = (EditText) view.findViewById(R.id.edAge);
		edGender.setOnClickListener(this);
		edRaces.setOnClickListener(this);
		edVehicleType.setOnClickListener(this);
		edDate.setOnClickListener(this);
		edVehicleDept.setOnClickListener(this);
		
		cbChangeStatus = (CheckBox) view.findViewById(R.id.cbChangeStatus);
		bSubmit = (Button) view.findViewById(R.id.bSubmit);
		llTradeIn = (LinearLayout) view.findViewById(R.id.llTradeIn);
		llUpdateNow = (LinearLayout) view.findViewById(R.id.llUpdateNow);
		llSoldDelivered = (LinearLayout) view.findViewById(R.id.llSoldDelivered);
		tvPhone.setOnClickListener(new OnClickListener() 
		{
		@Override
		public void onClick(View arg0) {
			 recordActivity(RECORD_CALL);
		}
		});
		/*final ImageView ivIcon= (ImageView) view.findViewById(R.id.ivIcon);
		ivIcon.setRotation(90);
		final ImageView ivIconTradeIn= (ImageView) view.findViewById(R.id.ivIconTradeIn);
		view.findViewById(R.id.vwTradeIn).setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View arg0){
				if(llTradeIn.getVisibility()==View.VISIBLE){
					ivIconTradeIn.setRotation(0);
					llTradeIn.setVisibility(View.GONE);
				}else{
					ivIconTradeIn.setRotation(90);
					llTradeIn.setVisibility(View.VISIBLE);
				}
			}
		});*/
		edActivity.setOnClickListener(this);
		bSubmit.setOnClickListener(this);
	}
	
	private void getLeadsDetails()
	{
		if (HelperHttp.isNetworkAvailable(getActivity())){
			// Add parameters to request in array list
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("leadID", getArguments().getInt("leadID"), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("LoadLead");
			inObj.setNamespace(Constants.LEADS_NAMESPACE);
			inObj.setSoapAction(Constants.LEADS_NAMESPACE + "/ILeadService/LoadLead");
			inObj.setUrl(Constants.LEADS_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener(){
				// Network callback
				@Override
				public void onTaskComplete(Object result){
					if (result != null){
						leads= ParserManager.parseLeadDetails(result);
						try
						{
							if (leads!=null)
							{
								putValues();
							}else {
								CustomDialogManager.showOkDialog(getActivity(),"You are not authorised to see this lead.",new DialogListener()
								{
									
									@Override
									public void onButtonClicked(int type)
									{
										getFragmentManager().popBackStack();
									}
								});
							}
						} catch (Exception e)
						{
							e.printStackTrace();
							CustomDialogManager.showOkDialog(getActivity(),getString(R.string.error_occured_try_later),new DialogListener()
							{
								
								@Override
								public void onButtonClicked(int type)
								{
									getFragmentManager().popBackStack();
								}
							});
						}
					}
				}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
	private void putValues()
	{
		tvProspect.setText(leads.getUsername());
		String data = "";
		if (!TextUtils.isEmpty(Helper.formatMobileNumber(leads.getPhoneNumber())))
			data = Helper.formatMobileNumber(leads.getPhoneNumber());
		else if (!TextUtils.isEmpty(Helper.formatMobileNumber(leads.getWorkNumber()))){
				data = Helper.formatMobileNumber(leads.getWorkNumber());
		}else if (!TextUtils.isEmpty(Helper.formatMobileNumber(leads.getHomeNumber()))){
			data = Helper.formatMobileNumber(leads.getHomeNumber());
		}
		if(!TextUtils.isEmpty(data)){
			tvPhone.setText(Html.fromHtml("<U>"+data+"</U>"));
			tvPhone.setTextColor(getResources().getColor(R.color.dark_blue));
		}
		else
		{
			tvPhone.setText("Phone number?");
		}
		if(!TextUtils.isEmpty(leads.getUserEmail()))
		{
			tvEmail.setText(Html.fromHtml("<U>"+leads.getUserEmail()+"</U>"));
			tvEmail.setTextColor(getResources().getColor(R.color.dark_blue));
			tvEmail.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					recordActivity(RECORD_EMAIL);
				}
			});
		}else{
			tvEmail.setText("Email address?");
		}
		if(leads.isMatched())
		{
			if(leads.getVehicleType()==VehicleType.New){
				 if (leads.getFriendlyName().equals("EnquiredOn"))
				{
					 tvEnquiredOn.setText("Enquired on?");
				}else {
					tvEnquiredOn.setText(leads.getFriendlyName());
				}
			}else if(leads.getVehicleType()==VehicleType.Used){
				tvEnquiredOn.setText(leads.getYear() +" | "+ leads.getFriendlyName() +" | "+ leads.getColor() + " | " +Helper.getFormattedDistance(leads.getMilage()+"")+" | "+ leads.getStockCode());
			}
		}else{
			if (TextUtils.isEmpty(leads.getVariant()))
			{
				 tvEnquiredOn.setText("Enquired on?");
			}else {
				tvEnquiredOn.setText(TextUtils.isEmpty(leads.getVariant()) ? leads.getFriendlyName() : leads.getVariant() + " | " + leads.getFriendlyName());
			}
		}
		tvTiming.setText("Timing?");
		if(!TextUtils.isEmpty(leads.getSource()))
			tvSource.setText(leads.getSource());
		else
			tvSource.setText("N/A");
		tvDate.setText("" + leads.getSubmitted());
		if (!TextUtils.isEmpty(leads.getSubmitted())){
			final CharSequence relativeTimeSpan = getTimeAgo(Helper.getDateInMillis(leads.getSubmitted()));
			tvDate.setText(leads.getSubmitted() + " | " + relativeTimeSpan);
		}
		if(leads.getLastUpdate()!=null){
			tvLastUpdate.setText(leads.getActivity() +" | " + leads.getUser() + " | " + Helper.getTimeAgo(leads.getDate()));	
		}else{
			tvLastUpdate.setText("N/A");
		}
		
		if (TextUtils.isEmpty(leads.getTradeIn())){
			llTradeIn.setVisibility(View.GONE);
		}else{
			llTradeIn.setVisibility(View.VISIBLE);
			tvCurrentVehicle.setText(leads.getMakdetradeIn());
			if (TextUtils.isEmpty(leads.getYeartradeIn())&&TextUtils.isEmpty(leads.getModeltradeIn()))
			{
				tvYearModel.setText("No Year Model");
			}else {
				tvYearModel.setText(leads.getYeartradeIn()+leads.getModeltradeIn());
			}
			tvMileage.setText(leads.getMileagetradeIn());
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Lead "+ getArguments().getInt("leadID"));
	}

	public String getTimeAgo(long time)
	{
		if (time < 1000000000000L)
			// if timestamp given in seconds, convert to millis
			time *= 1000;
		Date nowDate = new Date();
		long now = nowDate.getTime();
		// final long now = getCurrentTime(context);
		if (time > now || time <= 0)
			return "";
		final Resources res = getResources();
		final long time_difference = now - time;
		if (time_difference < _A_MINUTE)
			return res.getString(R.string.just_now);
		else if (time_difference < 50 * _A_MINUTE)
			return res.getString(R.string.time_ago,
					res.getQuantityString(R.plurals.minutes, (int) time_difference / _A_MINUTE, time_difference / _A_MINUTE));
		else if (time_difference < 24 * _AN_HOUR)
			return res.getString(R.string.time_ago,
					res.getQuantityString(R.plurals.hours, (int) time_difference / _AN_HOUR, time_difference / _AN_HOUR));
		else if (time_difference < 48 * _AN_HOUR)
			return res.getString(R.string.yesterday);
		else
			return res.getString(R.string.time_ago,
					res.getQuantityString(R.plurals.days, (int) time_difference / _A_DAY, time_difference / _A_DAY));
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.edActivity:
				hideKeyboard();
				if (activityOptions == null){
					activityOptions = new ArrayList<SmartObject>();
					getActivityOptions();
				}else{
					if (!activityOptions.isEmpty())
						showActivityOptions();
					else
						getActivityOptions();
				}
				break;

			case R.id.edGener:
				Helper.showDropDown(edGender, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, gender), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						edGender.setText(gender[position]);
						selectedGenderID = position+1;
					}
				});
				break;
				
			case R.id.edRaces:
				Helper.showDropDown(edRaces, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, races), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						edRaces.setText(races[position]);
						selectedRaceID = race_id[position];
					}
				});
				break;	
				
			case R.id.edVehicleDept:
				Helper.showDropDown(edVehicleDept, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, department), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						edVehicleDept.setText(department[position]);
						selectedDepartmentID = position+1;
					}
				});
				break;	
				
			case R.id.edVehicleType:
				Helper.showDropDown(edVehicleType, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, type), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						edVehicleType.setText(type[position]);
						selectedTypeID = type_id[position];
					}
				});
				break;	
				
			case R.id.edDate:
				DatePickerFragment startDate = new DatePickerFragment();
				startDate.setDateListener(new DateListener()
				{
					@Override
					public void onDateSet(int year, int monthOfYear, int dayOfMonth)
					{
						edDate.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
						selected_date = Helper.createStringDateNew(year, monthOfYear, dayOfMonth)+"T00:00:00";
					}
				});
				startDate.show(getActivity().getFragmentManager(), "datePicker");
				break;	
				
			case R.id.bSubmit:
				
				if (llSoldDelivered.getVisibility()==View.VISIBLE)
				{
					if (TextUtils.isEmpty(edInvoiceNo.getText().toString().trim()))
					{
						Helper.showToast("Please enter Invoice number", getActivity());
						return;
					}
					if (TextUtils.isEmpty(selected_date))
					{
						Helper.showToast("Please select invoice date", getActivity());
						return;
					}
					if (TextUtils.isEmpty(edInclusive.getText().toString().trim()))
					{
						Helper.showToast("Please enter Invoice amount", getActivity());
						return;
					}
					if (TextUtils.isEmpty(edInvoiceTo.getText().toString().trim()))
					{
						Helper.showToast("Please enter recepient of Invoice", getActivity());
						return;
					}
					if (TextUtils.isEmpty(edSalesPerson.getText().toString().trim()))
					{
						Helper.showToast("Please enter name of salesperson", getActivity());
						return;
					}
					
					if (TextUtils.isEmpty(edStockCode.getText().toString().trim()))
					{
						Helper.showToast("Please enter stock code", getActivity());
						return;
					}
					
					if (edVehicleDept.getText().toString().trim().equals("Select"))
					{
						Helper.showToast("Please select vehicle department", getActivity());
						return;
					}
					
					if (edVehicleType.getText().toString().trim().equals("Select"))
					{
						Helper.showToast("Please select vehicle type", getActivity());
						return;
					}
					
					if (edGender.getText().toString().trim().equals("Select"))
					{
						Helper.showToast("Please select Gender", getActivity());
						return;
					}
					if (edRaces.getText().toString().trim().equals("Select"))
					{
						Helper.showToast("Please select Race", getActivity());
						return;
					}
					if (TextUtils.isEmpty(edAge.getText().toString().trim()))
					{
						Helper.showToast("Please enter Age", getActivity());
						return;
					}
					int age  = Integer.parseInt(edAge.getText().toString().trim());
					if (!(age>=18 && age<90))
					{
						Helper.showToast("Please enter age between 18 and 90", getActivity());
						return;
					}
				}
				if (TextUtils.isEmpty(edComment.getText().toString().trim()))
				{
					Helper.showToast("Please enter a comment", getActivity());
					return;
				}
				addActivity();
				break;
			
		}
	}
	

	private void recordActivity(final int TYPE)
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
			StringBuilder soapMessage=new StringBuilder();
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<AddActivity xmlns=\""+Constants.LEADS_NAMESPACE+"\">");
			soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash()+ "</userHash>");
			soapMessage.append("<clientID>" + DataManager.getInstance().user.getDefaultClient().getId()+ "</clientID>");
			soapMessage.append("<leadID>"+leads.getId()+"</leadID>");
			if (TYPE==RECORD_CALL) {
				soapMessage.append("<Activity>5</Activity>");
			}else {
				soapMessage.append("<Activity>38</Activity>");
			}
			soapMessage.append(" <ChangeStatus>1</ChangeStatus>");
			if (TYPE==RECORD_CALL) {
				soapMessage.append("<Comment>Phone call via Smart Manager App</Comment>");
			}else {
				soapMessage.append("<Comment>Email sent via Smart Manager App</Comment>");
			}
			soapMessage.append("</AddActivity>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");
			VollyResponseListener listener=new VollyResponseListener()
			{
				@Override
				public void onErrorResponse(VolleyError error)
				{
				}
				
				@Override
				public void onResponse(String response)
				{
					Helper.Log("Record call and Email actions", "" + response);
					if(response==null)
					{
						return;
					}
					if(!TextUtils.isEmpty(response))
					{
						if(response.contains("Success"))
							if(TYPE==RECORD_CALL)
							{
								String uri = "tel:" + tvPhone.getText().toString().trim() ;
								 Intent intent = new Intent(Intent.ACTION_CALL);
								 intent.setData(Uri.parse(uri));
								 startActivity(intent);
							}	
							else{
								 Intent emailIntent = new Intent(Intent.ACTION_SEND);
							      emailIntent.setData(Uri.parse("mailto: "+tvEmail.getText().toString().trim()));
							      emailIntent.setType("message/rfc822");
							      String aEmailList[] = { tvEmail.getText().toString().trim() };
							      emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);
							      if (tvEnquiredOn.getText().toString().trim().equals("EnquiredOn"))
									{
							    	  emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Response to your enquiry on"+leads.getSource());
									}else {
										 emailIntent.putExtra(Intent.EXTRA_SUBJECT,tvEnquiredOn.getText().toString().trim());
									}

							      try {
							         startActivity(Intent.createChooser(emailIntent, "Send mail..."));
							      } catch (android.content.ActivityNotFoundException ex) {
							    	  ex.printStackTrace();
							      }
							}
						else
							return;
					}
					else
						return;
				}
			};
			VollyCustomRequest request=new VollyCustomRequest(Constants.LEADS_WEBSERVICE_URL,soapMessage.toString(),
							Constants.LEADS_NAMESPACE+"/ILeadService/AddActivity",listener);
			try
			{
				request.init();
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

	private void addActivity()
	{
		if (edActivity.getTag() == null){
			Helper.showToast(getString(R.string.please_select_activity), getActivity());
			return;
		}
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("leadID", getArguments().getInt("leadID"), Integer.class));
			parameterList.add(new Parameter("Activity", edActivity.getTag(), Integer.class));
			parameterList.add(new Parameter("ChangeStatus", cbChangeStatus.isChecked(), Boolean.class));
			parameterList.add(new Parameter("Comment", edComment.getText().toString().trim(), String.class));
			if (isSoldandDelivered)
			{
				parameterList.add(new Parameter("invoiceNumber", edInvoiceNo.getText().toString().trim(), String.class));
				parameterList.add(new Parameter("invoiceDate", selected_date, String.class));
				parameterList.add(new Parameter("invoiceAmount", edInclusive.getText().toString().trim(), Double.class));
				parameterList.add(new Parameter("invoiceTo", edInvoiceTo.getText().toString().trim(), String.class));
				parameterList.add(new Parameter("invoiceSalesman", edSalesPerson.getText().toString().trim(), String.class));
				parameterList.add(new Parameter("stockNumber", edStockCode.getText().toString().trim(), String.class));
				parameterList.add(new Parameter("departmentID", selectedDepartmentID, Integer.class));
				parameterList.add(new Parameter("typeID", selectedTypeID, Integer.class));
				parameterList.add(new Parameter("customerGenderID", selectedGenderID, Integer.class));
				parameterList.add(new Parameter("customerRaceID", selectedRaceID, Integer.class));
				parameterList.add(new Parameter("customerAge", edAge.getText().toString().trim(), Integer.class));
			}

			// create web service inputs
						DataInObject inObj = new DataInObject();
			if (isSoldandDelivered)
			{
			inObj.setMethodname("AddSoldAndDeliveredLeadDetails");
			inObj.setNamespace(Constants.LEADS_NAMESPACE);
			inObj.setSoapAction(Constants.LEADS_NAMESPACE + "/ILeadService/AddSoldAndDeliveredLeadDetails");
			}else {
				inObj.setMethodname("AddActivity");
				inObj.setNamespace(Constants.LEADS_NAMESPACE);
				inObj.setSoapAction(Constants.LEADS_NAMESPACE + "/ILeadService/AddActivity");
			}
			inObj.setUrl(Constants.LEADS_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener(){
				// Network callback
				@Override
				public void onTaskComplete(Object result){
					hideProgressDialog();
					if (result == null)
						return;
					try{
						Helper.Log("Response", "" + result.toString());
						
						SoapObject obj = (SoapObject) result;
						SoapObject dataObj = (SoapObject) obj.getPropertySafely("Result", "default");
						String output=dataObj.getPrimitivePropertySafelyAsString("Status");
						
						if(!TextUtils.isEmpty(output))
						{
							if(output.equals("Success"))
								CustomDialogManager.showOkDialog(getActivity(), getString(R.string.leads_updated_successfully), new DialogListener() {
									
									@Override
									public void onButtonClicked(int type) 
									{
										if(getArguments().getInt("groupPosition")==0)
											CustomerActivity.UnseenleadUpdated=true;
										else
											CustomerActivity.WipleadUpdated=true;
										getFragmentManager().popBackStack();
									}
								});
							else
								showErrorDialog();
						}
						else
							showErrorDialog();
					}catch (Exception e){
						e.printStackTrace();
					}					
				}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	private void getActivityOptions()
	{
		showProgressDialog();
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
		parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
		parameterList.add(new Parameter("leadID", getArguments().getInt("leadID"), String.class));

		// create web service inputs
		DataInObject inObj = new DataInObject();
		inObj.setMethodname("ActivityOptionsForLead");
		inObj.setNamespace(Constants.LEADS_NAMESPACE);
		inObj.setSoapAction(Constants.LEADS_NAMESPACE + "/ILeadService/ActivityOptionsForLead");
		inObj.setUrl(Constants.LEADS_WEBSERVICE_URL);
		inObj.setParameterList(parameterList);
		// Network call
		new WebServiceTask(getActivity(), inObj, false, new TaskListener(){
			// Network callback
			@Override
			public void onTaskComplete(Object result){
				hideProgressDialog();
				if (result == null)
					return;
				try{
					Helper.Log("Response", "" + result.toString());
					SoapObject obj = (SoapObject) result;
					SoapObject dataObj = (SoapObject) obj.getPropertySafely("Activities", "default");
					for (int i = 0; i < dataObj.getPropertyCount(); i++){
						SoapObject soapObject = (SoapObject) dataObj.getProperty(i);
						SmartObject smartObject = new SmartObject();
						smartObject.setId(Integer.parseInt(soapObject.getPrimitivePropertyAsString("ID")));
						smartObject.setName(soapObject.getPrimitivePropertyAsString("Name"));
						activityOptions.add(smartObject);
					}
				}catch (Exception e){
					e.printStackTrace();
				}finally{
					showActivityOptions();
				}
			}
		}).execute();
	}

	private void showActivityOptions(){
		showSearch(getActivity());
	}
	
	public  void showSearch(Context context) 
	{
		Helper.showDropDown(edActivity, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, activityOptions), new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				edActivity.setText("" + activityOptions.get(position).getName());
				edActivity.setTag(activityOptions.get(position).getId());
				if (activityOptions.get(position).getName().equalsIgnoreCase("Sold & Delivered"))
				{
					isSoldandDelivered =true;
					llSoldDelivered.setVisibility(View.VISIBLE);
					cbChangeStatus.setVisibility(View.GONE);
				}else {
					isSoldandDelivered= false;
					llSoldDelivered.setVisibility(View.GONE);
					cbChangeStatus.setVisibility(View.VISIBLE);
				}
			}
		});
		
		/*AlertDialog.Builder build = new AlertDialog.Builder(context);
		final AlertDialog dialog;
		final ArrayAdapter<SmartObject> adapter = new ArrayAdapter<SmartObject>(getActivity(), R.layout.list_item_text2,R.id.tvText, activityOptions);
		build.setAdapter(adapter, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int position)
			{
				edActivity.setText("" + adapter.getItem(position).getName());
				edActivity.setTag(adapter.getItem(position).getId());
				if (adapter.getItem(position).getName().equalsIgnoreCase("Sold & Delivered"))
				{
					isSoldandDelivered =true;
					llSoldDelivered.setVisibility(View.VISIBLE);
					cbChangeStatus.setVisibility(View.GONE);
				}else {
					isSoldandDelivered= false;
					llSoldDelivered.setVisibility(View.GONE);
					cbChangeStatus.setVisibility(View.VISIBLE);
				}
			}
		});
		dialog = build.create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		dialog.show();*/
	}

}
