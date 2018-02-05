package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.AvailabilityAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.Availability;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class AvailabilityFragment extends BaseFragement
{
	ListView listview;
	AvailabilityAdapter availabilityAdapter;
	ArrayList<SmartObject> groupList;
	ArrayList<Availability> availabilities;
	VehicleDetails vehicleDetails;
	TextView tvGroupName,tvProvience,tvTitle,tvUsCount,tvGroupCount,tvProvienceCount,tvNationalCount;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_availability, container, false);
		setHasOptionsMenu(true);
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
		tvGroupName = (TextView) view.findViewById(R.id.tvGroupName);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvUsCount= (TextView) view.findViewById(R.id.tvUsCount);
		tvGroupCount= (TextView) view.findViewById(R.id.tvGroupCount);
		tvProvienceCount= (TextView) view.findViewById(R.id.tvProvienceCount);
		tvNationalCount= (TextView) view.findViewById(R.id. tvNationalCount);
		tvTitle.setText(vehicleDetails.getModelName()+"'s Availability");
		tvProvience = (TextView) view.findViewById(R.id.tvProvience);
		if (groupList ==null)
		{
			getGroupList();
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Availability");
	}
	
	private void getGroupList()
	{
		groupList = new ArrayList<SmartObject>();
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetClientCorprateGroups");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IAuthenticate/GetClientCorprateGroups");
			inObj.setUrl(Constants.WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{
				// Network callback
				@Override
				public void onTaskComplete(Object result)
				{
					groupList.clear();
					try
					{
						Helper.Log("GroupList response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Results");
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject groupObj = (SoapObject) inner.getProperty(i);
							String groupid = groupObj.getPropertySafelyAsString("ID", "0");
							String groupname = groupObj.getPropertySafelyAsString("Name", "-");
							groupList.add(i, new SmartObject(Integer.parseInt(groupid), groupname));
						}
					} catch (Exception e)
					{
						e.printStackTrace();

					}
					finally
					{
                        hideProgressDialog();
						if (availabilities==null && !groupList.isEmpty())
						{
							 getAvailability();
						}else {
							CustomDialogManager.showOkDialog(getActivity(), "No associated group found", new DialogListener() {
								@Override
								public void onButtonClicked(int type) {
									getFragmentManager().popBackStack();
								}
							});
						}
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

    int us=0,group=0,national=0,Province=0;
	private void getAvailability()
	{
		availabilities = new ArrayList<Availability>();
		if(HelperHttp.isNetworkAvailable(getActivity())){
			//Add parameters to request in arraylist
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("modelID", vehicleDetails.getModelId(),Integer.class));
			parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(),Integer.class));
			parameterList.add(new Parameter("groupID", groupList.get(0).getId(),Integer.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("LoadAvailability");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/LoadAvailability");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			showProgressDialog();
			new WebServiceTask(getActivity(),inObj, false,new TaskListener() {
			
			@Override
			public void onTaskComplete(Object result) 
			{
				availabilities.clear();
                try{
					SoapObject outer= (SoapObject) result;
					SoapObject resultObj= (SoapObject) outer.getPropertySafely("Result");
					SoapObject inner= (SoapObject) resultObj.getPropertySafely("Availability");
					Availability availability = null;

					for(int i=0;i<inner.getPropertyCount();i++)
					{
						SoapObject availabilityObj= (SoapObject) inner.getProperty(i);
						availability = new Availability ();
 						availability.setVariantName(availabilityObj.getPropertySafelyAsString("VariantName", "0"));
                       // us = Integer.parseInt(availabilityObj.getPropertySafelyAsString("ClientAvailability", "0"));
                        us += Integer.parseInt(availabilityObj.getPropertySafelyAsString("ClientAvailability", "0"));
						availability.setClientAvailability(Integer.parseInt(availabilityObj.getPropertySafelyAsString("ClientAvailability", "0")));
                   //     group = Integer.parseInt(availabilityObj.getPropertySafelyAsString("GroupAvailability", "0"));
                        group += Integer.parseInt(availabilityObj.getPropertySafelyAsString("GroupAvailability", "0"));
						availability.setGroupAvailability(Integer.parseInt(availabilityObj.getPropertySafelyAsString("GroupAvailability", "0")));
                  //      Province = Integer.parseInt(availabilityObj.getPropertySafelyAsString("ProvinceAvailability", "0"));
                        Province += Integer.parseInt(availabilityObj.getPropertySafelyAsString("ProvinceAvailability", "0"));
						availability.setProvinceAvailability(Integer.parseInt(availabilityObj.getPropertySafelyAsString("ProvinceAvailability", "0")));
                     //   national =Integer.parseInt(availabilityObj.getPropertySafelyAsString("NationalAvailability", "0"));
                        national += Integer.parseInt(availabilityObj.getPropertySafelyAsString("NationalAvailability", "0"));
						availability.setNationalAvailability(Integer.parseInt(availabilityObj.getPropertySafelyAsString("NationalAvailability", "0")));
						availabilities.add(availability);
					}
					SoapObject clientObj= (SoapObject) resultObj.getPropertySafely("ClientDetails");
					tvGroupName.setText(clientObj.getPropertySafelyAsString("GroupName"));
					tvProvience.setText(clientObj.getPropertySafelyAsString("ProvinceName"));
				}catch(Exception e){
					e.printStackTrace();
                    tvUsCount.setText("0");
                    tvGroupCount.setText("0");
                    tvProvienceCount.setText("0");
                    tvNationalCount.setText("0");
                    tvGroupName.setText("Group?");
                    tvProvience.setText("Provience");
                    hideProgressDialog();
				}
				finally
				{
					if (!availabilities.isEmpty()) {
                        tvUsCount.setText(""+us);
                        tvGroupCount.setText(""+group);
                        tvProvienceCount.setText(""+Province);
                        tvNationalCount.setText(""+national);
                        availabilityAdapter = new AvailabilityAdapter(getActivity(), R.layout.list_item_availability, availabilities);
                        listview.setAdapter(availabilityAdapter);
                    }else {
                        tvUsCount.setText("0");
                        tvGroupCount.setText("0");
                        tvProvienceCount.setText("0");
                        tvNationalCount.setText("0");
                        tvGroupName.setText("Group?");
                        tvProvience.setText("Provience");
                    }
                    hideProgressDialog();
				}
			}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
}
