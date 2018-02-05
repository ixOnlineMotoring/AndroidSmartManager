package com.nw.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.VINHistoryAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.Vehicle;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class VinHistoryFragment extends BaseFragement 
{

	TextView tvVariantName,tvVIN,tvEngine;
	ListView lvVinHistory;
	VINHistoryAdapter vinHistoryAdapter;
	ArrayList<Vehicle> historyList;
	VehicleDetails vehicleDetails;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_vin_history, container, false);
		setHasOptionsMenu(true);
		if (getArguments()!=null)
		{
			vehicleDetails = getArguments().getParcelable("vehicleDetails");
		}
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		// TextView
		tvVariantName = (TextView) view.findViewById(R.id.tvVariantName);
		tvVIN = (TextView) view.findViewById(R.id.tvVIN);
		tvEngine = (TextView) view.findViewById(R.id.tvEngine);
		// ListView
		lvVinHistory = (ListView) view.findViewById(R.id.lvVinHistory);
		getVINHistoryData();
	}

	private void getVINHistoryData()
	{
		if(HelperHttp.isNetworkAvailable(getActivity())){
			//Add parameters to request in arraylist
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("VIN", vehicleDetails.getVin(),String.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("LoadVINHistory");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/LoadVINHistory");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			showProgressDialog();
			new WebServiceTask(getActivity(),inObj, false,new TaskListener() {
			
			@Override
			public void onTaskComplete(Object result) 
			{
				try
				{
					SoapObject outer = (SoapObject) result;
					SoapObject specsObj  =  (SoapObject) outer.getPropertySafely("VINHistory");
					String EngineNo = specsObj.getPropertySafelyAsString("EngineNo");
					String VIN = specsObj.getPropertySafelyAsString("VIN");
					SoapObject inner = (SoapObject) specsObj.getPropertySafely("Histories");
					historyList = new ArrayList<Vehicle>();
					Vehicle vehicle;	
					for (int i = 0; i < inner.getPropertyCount(); i++)
					{
						SoapObject historyObject = (SoapObject) inner.getProperty(i);
						vehicle = new Vehicle();
						vehicle.setExpires(historyObject.getPropertySafelyAsString("LastSeen","Last Seen?"));
						vehicle.setOwnerName(historyObject.getPropertySafelyAsString("Dealer"));
						vehicle.setLocation(historyObject.getPropertySafelyAsString("Location"));
						vehicle.setMileage(Integer.parseInt(historyObject.getPropertySafelyAsString("Mileage","0")));
						vehicle.setRetailPrice(Float.parseFloat(historyObject.getPropertySafelyAsString("Price", "0.00")));
						historyList.add(vehicle);
					}
					if (EngineNo.equalsIgnoreCase("anyType{}")||EngineNo.equalsIgnoreCase("0")||EngineNo.equalsIgnoreCase("null"))
					{
						tvEngine.setText("EngineNo?");
					} else
					{
						tvEngine.setText(EngineNo);
					}
					tvVIN.setText(VIN);
					tvVariantName.setText(Html.fromHtml("<font color=#ffffff>" + vehicleDetails.getYear() + "</font> <font color=" + VinHistoryFragment.this.getResources().getColor(R.color.dark_blue) + ">"
					+ vehicleDetails.getFriendlyName() + "</font>"));
					hideProgressDialog();
					if (historyList.isEmpty()){
						CustomDialogManager.showOkDialog(getActivity(), "No record/s found.", new DialogListener() {
							@Override
							public void onButtonClicked(int type) {
								getFragmentManager().popBackStack();
							}
						});
                        return;
					}
					vinHistoryAdapter = new VINHistoryAdapter(getActivity(), historyList);
					lvVinHistory.setAdapter(vinHistoryAdapter);

				} catch (Exception e)
				{
					hideProgressDialog();
					CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data));
				}
			}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}	
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("VIN History");
	}


}
