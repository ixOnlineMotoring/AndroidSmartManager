package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.ActiveLeadAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Leads;
import com.nw.model.Parameter;
import com.nw.model.SettingsUser;
import com.nw.model.VehicleDetails;
import com.nw.model.VehicleType;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class ActiveLeadFragment extends BaseFragement
{
	ListView listview;
	TextView tvPageTitle;
	ActiveLeadAdapter leadPoolAdapter;
	ArrayList<Leads> arrayList;
	SettingsUser settingsUser;
	VehicleDetails vehicleDetails;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_active_lead, container, false);
		setHasOptionsMenu(true);
		if (arrayList == null)
		{
			arrayList = new ArrayList<Leads>();
		}
		if(getArguments()!=null)
		{
			vehicleDetails = getArguments().getParcelable("vehicleDetails");
		}
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		listview = (ListView) view.findViewById(R.id.listview);
		tvPageTitle = (TextView) view.findViewById(R.id.tvPageTitle);
		tvPageTitle.setText(getArguments().getString("pageName"));
        if (getArguments().getBoolean("isActiveClicked")==true&&getArguments().getBoolean("isGroup")== false)
		{
			getLeadPoolDataForClient(1);
		}else if (getArguments().getBoolean("isActiveClicked")==false&&getArguments().getBoolean("isGroup")== false) {
			getLeadPoolDataForClient(3);
		}else if (getArguments().getBoolean("isActiveClicked")==false&&getArguments().getBoolean("isGroup")== true) {
			getLeadPoolDataForGroup();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Active Leads");
	}

	private void getLeadPoolDataForClient(int typeID)
	{
		arrayList.clear();
		if(HelperHttp.isNetworkAvailable(getActivity())){
			//Add parameters to request in arraylist
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("modelID", vehicleDetails.getModelId(),Integer.class));
			parameterList.add(new Parameter("year", vehicleDetails.getYear(),Integer.class));
			parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(),Integer.class));
			parameterList.add(new Parameter("leadStatusTypeID", typeID, Integer.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("LoadLeadPoolDetailForClient");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/LoadLeadPoolDetailForClient");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			showProgressDialog();
			new WebServiceTask(getActivity(),inObj, false,new TaskListener() {
			
			@Override
			public void onTaskComplete(Object result) 
			{
				try{
					parseLeadPoolResponse(result);
					hideProgressDialog();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
	private void getLeadPoolDataForGroup()
	{
		arrayList.clear();
		if(HelperHttp.isNetworkAvailable(getActivity())){
			//Add parameters to request in arraylist
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("modelID", vehicleDetails.getModelId(),Integer.class));
			parameterList.add(new Parameter("year", vehicleDetails.getYear(),Integer.class));
			parameterList.add(new Parameter("groupID", getArguments().getInt("GroupID"), Integer.class));
			parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(),Integer.class));
			parameterList.add(new Parameter("leadStatusTypeID", 3, Integer.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("LoadLeadPoolDetailForGroupExcludeClient");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/LoadLeadPoolDetailForGroupExcludeClient");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			showProgressDialog();
			new WebServiceTask(getActivity(),inObj, false,new TaskListener() {
			
			@Override
			public void onTaskComplete(Object result) 
			{
				try{
					parseLeadPoolResponse(result);
					leadPoolAdapter.notifyDataSetChanged();
					hideProgressDialog();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	protected void parseLeadPoolResponse(Object result)
	{
		SoapObject outer= (SoapObject) result;
		SoapObject inner= (SoapObject) outer.getPropertySafely("LeadPoolDetail");
		Leads lead;
		for(int i=0;i<inner.getPropertyCount();i++)
		{
			SoapObject leadObj = (SoapObject) inner.getProperty(i);
			
			lead = new Leads();
			lead.setId(Integer.parseInt(leadObj.getPropertySafelyAsString("LeadID","0")));
			lead.setYear(leadObj.getPropertySafelyAsString("Year","Year?"));
			lead.setVehicleType(VehicleType.Used);
			lead.setMakeAsked(leadObj.getPropertySafelyAsString("MakeName","Make?"));
			lead.setModelAsked(leadObj.getPropertySafelyAsString("VariantName","VariantName?"));
			lead.setVariant(leadObj.getPropertySafelyAsString("ModelName","Model?"));
			lead.setUsername(leadObj.getPropertySafelyAsString("ProspectName","ProspectName?"));
			lead.setPhoneNumber(leadObj.getPropertySafelyAsString("ProspectContactNumber","ProspectContactNumber?"));
			lead.setUserEmail(leadObj.getPropertySafelyAsString("ProspectEmail","ProspectEmail?"));
			lead.setFriendlyName(leadObj.getPropertySafelyAsString("SalesPerson","SalesPerson?"));
			lead.setDaysLeft(leadObj.getPropertySafelyAsString("LeadAgeInDays","LeadAgeInDays?"));
			arrayList.add(lead);
		}
		  leadPoolAdapter = new ActiveLeadAdapter(getActivity(), R.layout.list_item_lead, arrayList);
	      listview.setAdapter(leadPoolAdapter);
	}

}
