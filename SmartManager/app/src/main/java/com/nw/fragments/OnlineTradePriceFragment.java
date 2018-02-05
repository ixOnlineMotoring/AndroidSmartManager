package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.PriceNowAdapter;
import com.nw.interfaces.DialogListener;
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

import java.text.NumberFormat;
import java.util.ArrayList;

public class OnlineTradePriceFragment extends BaseFragement implements OnClickListener
{
	ListView lvOnlinePrices;
	ArrayList<SmartObject> arrayList = new ArrayList<SmartObject>();
	PriceNowAdapter priceNowAdapter;
	EditText edtAdvertRegions;
	ArrayList<String> regions = new ArrayList<String>(), values = new ArrayList<String>();
	String[] listItems = { "Avg. Retail", "Highest Retail", "Lowest Retail", "Avg. Km", "Ad Count", "Outlier Ads Excl." };
	int cityID = 0, groupID = 0, provienceID = 0;
	VehicleDetails vehicleDetails;
	TextView tvColumnName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_online_trade, container, false);
		setHasOptionsMenu(true);
		if (getArguments() != null)
		{
			vehicleDetails = getArguments().getParcelable("vehicleDetails");
		}
		initialise(view);
		getValueforAdvertSelected(2);
		return view;
	}

	private void initialise(View view)
	{
		lvOnlinePrices = (ListView) view.findViewById(R.id.listview);
		edtAdvertRegions = (EditText) view.findViewById(R.id.edtAdvertRegions);
		tvColumnName = (TextView) view.findViewById(R.id.tvColumnName);
		tvColumnName.setText("Online Pricing");
		edtAdvertRegions.setOnClickListener(this);
		priceNowAdapter = new PriceNowAdapter(getActivity(), R.layout.list_item_lead_pool, arrayList);
		lvOnlinePrices.setAdapter(priceNowAdapter);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Online Pricing Now");
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.edtAdvertRegions:
				if (regions.size() == 0)
				{
					getRegionsForClient();
				}
				else
				{
					Helper.showDropDown(edtAdvertRegions, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, regions), new OnItemClickListener()
					{
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id)
						{
							edtAdvertRegions.setText(regions.get(position));
							getValueforAdvertSelected(position);
						}
					});
				}
				break;
		}
	}

	private void getRegionsForClient()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("LoadAdvertRegionForClient");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadAdvertRegionForClient");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("AdvertRegion");
						if(isValidString(inner.getPropertySafelyAsString("CityName"))){
                            regions.add(inner.getPropertySafelyAsString("CityName"));
                        }
                        if(isValidString(inner.getPropertySafelyAsString("ProvinceName"))){
                            regions.add(inner.getPropertySafelyAsString("ProvinceName"));
                        }
                        if (regions.isEmpty()){
                            CustomDialogManager.showOkDialog(getActivity(), "No associated group found", new DialogListener() {
                                @Override
                                public void onButtonClicked(int type) {
                                    getFragmentManager().popBackStack();
                                }
                            });
                            return;
                        }
						regions.add("National");
						if (inner.hasProperty("GroupName"))
							regions.add(inner.getPropertySafelyAsString("GroupName") + " Dealers");
						regions.add(inner.getPropertySafelyAsString("GroupName") + " " + inner.getPropertySafelyAsString("CityName") + " Dealers");
						regions.add(inner.getPropertySafelyAsString("GroupName") + " " + inner.getPropertySafelyAsString("ProvinceName") + " Dealers");
						cityID = Integer.parseInt(inner.getPropertySafelyAsString("CityID"));
						groupID = Integer.parseInt(inner.getPropertySafelyAsString("GroupID"));
						provienceID = Integer.parseInt(inner.getPropertySafelyAsString("ProvinceID"));
						if (regions.size() != 0)
						{
							edtAdvertRegions.performClick();
						}
						else
						{
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found));
						}
					} catch (Exception e)
					{
						e.printStackTrace();
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found));
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

    private boolean isValidString(String s) {
        if(s.equalsIgnoreCase("anyType{}")){
            return false;
        }
        return true;
    }

    DataInObject inObj;

	private void getValueforAdvertSelected(int position)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			values.clear();
			arrayList.clear();
			inObj = new DataInObject();

			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("variantID", vehicleDetails.getVariantID(), Integer.class));
			parameterList.add(new Parameter("year", vehicleDetails.getYear(), Integer.class));
			switch (position)
			{
				case 0:
					parameterList.add(new Parameter("cityID", cityID, Integer.class));
					inObj.setMethodname("LoadRetailPricingHistoryForCity");
					inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
					inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadRetailPricingHistoryForCity");
					break;
				case 1:
					parameterList.add(new Parameter("provinceID", provienceID, Integer.class));
					inObj.setMethodname("LoadRetailPricingHistoryForProvince");
					inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
					inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadRetailPricingHistoryForProvince");
					break;
				case 2:
					// No extra id required
					inObj.setMethodname("LoadRetailPricingHistoryForNational");
					inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
					inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadRetailPricingHistoryForNational");
					break;
				case 3:
					parameterList.add(new Parameter("groupID", groupID, Integer.class));
					inObj.setMethodname("LoadRetailPricingHistoryForGroup");
					inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
					inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadRetailPricingHistoryForGroup");
					break;
				case 4:
					parameterList.add(new Parameter("groupID", groupID, Integer.class));
					parameterList.add(new Parameter("cityID", cityID, Integer.class));
					inObj.setMethodname("LoadRetailPricingHistoryForGroupAndCity");
					inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
					inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadRetailPricingHistoryForGroupAndCity");
					break;
				case 5:
					parameterList.add(new Parameter("groupID", groupID, Integer.class));
					parameterList.add(new Parameter("provinceID", provienceID, Integer.class));
					inObj.setMethodname("LoadRetailPricingHistoryForGroupAndProvince");
					inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
					inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadRetailPricingHistoryForGroupAndProvince");
					break;
			}
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("PricingHistory");
						SmartObject object;
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("RetailAvg30Days")).intValue());
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("RetailHigh30Days")).intValue());
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("RetailLow30Days")).intValue());
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("MileageAvg30Days")).intValue());
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("AdvertsCount30Days")).intValue());
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("AdvertsExcludedCount30Days")).intValue());
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("RetailAvg90Days")).intValue());
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("RetailHigh90Days")).intValue());
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("RetailLow90Days")).intValue());
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("MileageAvg90Days")).intValue());
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("AdvertsCount90Days")).intValue());
						values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("AdvertsExcludedCount90Days")).intValue());

						for (int i = 0; i < listItems.length; i++)
						{
							object = new SmartObject();
							object.setName(listItems[i]);
							if (i <= 2)
							{
								object.setID(Helper.formatPrice(values.get(i)));
								object.setType(Helper.formatPrice(values.get(i + 6)));
							}
							else if (i == 3)
							{
								object.setID(Helper.formatPrice(values.get(i)).replaceAll("R", "") + "Km");
								object.setType(Helper.formatPrice(values.get(i + 6)).replaceAll("R", "") + "Km");

							}
							else
							{
								object.setID(values.get(i));
								object.setType(values.get(i + 6));
							}
							arrayList.add(object);
						}
						priceNowAdapter.notifyDataSetChanged();
					} catch (Exception e)
					{
						e.printStackTrace();
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found));
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

}
