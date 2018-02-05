package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nw.adapters.DemandListAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Demand;
import com.nw.model.Parameter;
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

public class DemandFragment extends BaseFragement
{
	ListView lvDemand;
	//SettingsUser settingsUser;
	VehicleDetails vehicleDetails;
	ArrayList<Demand> demandlist;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_demand, container, false);
		setHasOptionsMenu(true);
		if (getArguments() != null)
		{
			vehicleDetails = getArguments().getParcelable("vehicleDetails");
		}
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		// Client TextView
		lvDemand = (ListView) view.findViewById(R.id.lvDemand);
		getDemandList();
	}

	private void getDemandList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("variantID", vehicleDetails.getVariantID(), Integer.class));
			parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("LoadDemandForVariantByID");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadDemandForVariantByID");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			showProgressDialog();
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{

				@Override
				public void onTaskComplete(Object result)
				{
					hideProgressDialog();
					try
					{
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Demands");
						SoapObject inner1 = (SoapObject) inner.getPropertySafely("Demand");

						Demand demand = new Demand();
						demand.setClientName(inner1.getPropertySafelyAsString("ClientName", ""));
						demand.setVariantName(inner1.getPropertySafelyAsString("VariantName", ""));
						demand.setModelName(inner1.getPropertySafelyAsString("ModelName", ""));
						demand.setClientVariantLeadCount(inner1.getPropertySafelyAsString("ClientVariantLeadCount", ""));
						demand.setClientVariantLeadCountRanking(inner1.getPropertySafelyAsString("ClientVariantLeadCountRanking", ""));
						demand.setClientVariantSoldLeadCount(inner1.getPropertySafelyAsString("ClientVariantSoldLeadCount", ""));
						demand.setClientVariantSoldLeadCountRanking(inner1.getPropertySafelyAsString("ClientVariantSoldLeadCountRanking", ""));
						demand.setClientModelLeadCount(inner1.getPropertySafelyAsString("ClientModelLeadCount", ""));
						demand.setClientModelLeadCountRanking(inner1.getPropertySafelyAsString("ClientModelLeadCountRanking", ""));
						demand.setClientModelSoldLeadCount(inner1.getPropertySafelyAsString("ClientModelSoldLeadCount", ""));
						demand.setClientModelSoldLeadCountRanking(inner1.getPropertySafelyAsString("ClientModelSoldLeadCountRanking", ""));
						demand.setCityName(inner1.getPropertySafelyAsString("CityName", ""));
						demand.setCityVariantLeadCount(inner1.getPropertySafelyAsString("CityVariantLeadCount", ""));
						demand.setCityVariantLeadCountRanking(inner1.getPropertySafelyAsString("CityVariantLeadCountRanking", ""));
						demand.setCityVariantSoldLeadCount(inner1.getPropertySafelyAsString("CityVariantSoldLeadCount", ""));
						demand.setCityVariantSoldLeadCountRanking(inner1.getPropertySafelyAsString("CityVariantSoldLeadCountRanking", ""));
						demand.setCityModelLeadCount(inner1.getPropertySafelyAsString("CityModelLeadCount", ""));
						demand.setCityModelLeadCountRanking(inner1.getPropertySafelyAsString("CityModelLeadCountRanking", ""));
						demand.setCityModelSoldLeadCount(inner1.getPropertySafelyAsString("CityModelSoldLeadCount", ""));
						demand.setCityModelSoldLeadCountRanking(inner1.getPropertySafelyAsString("CityModelSoldLeadCountRanking", ""));
						demand.setProvinceName(inner1.getPropertySafelyAsString("ProvinceName", ""));
						demand.setProvinceVariantLeadCount(inner1.getPropertySafelyAsString("ProvinceVariantLeadCount", ""));
						demand.setProvinceVariantLeadCountRanking(inner1.getPropertySafelyAsString("ProvinceVariantLeadCountRanking", ""));
						demand.setProvinceVariantSoldLeadCount(inner1.getPropertySafelyAsString("ProvinceVariantSoldLeadCount", ""));
						demand.setProvinceVariantSoldLeadCountRanking(inner1.getPropertySafelyAsString("ProvinceVariantSoldLeadCountRanking", ""));
						demand.setProvinceModelLeadCount(inner1.getPropertySafelyAsString("ProvinceModelLeadCount", ""));
						demand.setProvinceModelLeadCountRanking(inner1.getPropertySafelyAsString("ProvinceModelLeadCountRanking", ""));
						demand.setProvinceModelSoldLeadCount(inner1.getPropertySafelyAsString("ProvinceModelSoldLeadCount", ""));
						demand.setProvinceModelSoldLeadCountRanking(inner1.getPropertySafelyAsString("ProvinceModelSoldLeadCountRanking", ""));
						demand.setNationalVariantLeadCount(inner1.getPropertySafelyAsString("NationalVariantLeadCount", ""));
						demand.setNationalVariantLeadCountRanking(inner1.getPropertySafelyAsString("NationalVariantLeadCountRanking", ""));
						demand.setNationalVariantSoldLeadCount(inner1.getPropertySafelyAsString("NationalVariantSoldLeadCount", ""));
						demand.setNationalVariantSoldLeadCountRanking(inner1.getPropertySafelyAsString("NationalVariantSoldLeadCountRanking", ""));
						demand.setNationalModelLeadCount(inner1.getPropertySafelyAsString("NationalModelLeadCount", ""));
						demand.setNationalModelLeadCountRanking(inner1.getPropertySafelyAsString("NationalModelLeadCountRanking", ""));
						demand.setNationalModelSoldLeadCount(inner1.getPropertySafelyAsString("NationalModelSoldLeadCount", ""));
						demand.setNationalModelSoldLeadCountRanking(inner1.getPropertySafelyAsString("NationalModelSoldLeadCountRanking", ""));

						demandlist = new ArrayList<Demand>();
						demandlist.add(demand);
						DemandListAdapter demandListAdapter = new DemandListAdapter(getActivity(), R.layout.list_item_demand_list, demandlist);
						lvDemand.setAdapter(demandListAdapter);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Demand");
	}

}
