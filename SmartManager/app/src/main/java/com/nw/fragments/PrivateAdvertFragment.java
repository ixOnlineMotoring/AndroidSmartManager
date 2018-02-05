package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.PriceNowAdapter;
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

public class PrivateAdvertFragment extends BaseFragement
{
	ListView lvOnlinePrices;
	ArrayList<SmartObject> arrayList  = new ArrayList<SmartObject>();
	PriceNowAdapter priceNowAdapter;
	EditText edtAdvertRegions;
	TextView tvNoteBottom,tvColumnName;
	ArrayList<String> values = new ArrayList<String>();
	VehicleDetails vehicleDetails;
	String [] listItems = {"Avg. Price","Highest Price", "Lowest Price","Avg. Km","Ad Count", "Outlier Ads Excl."};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_online_trade, container, false);
		setHasOptionsMenu(true);
		if(getArguments()!=null)
		{
			vehicleDetails = getArguments().getParcelable("vehicleDetails");
		}
		initialise(view);
		getValueforAdvertSelected();
		return view;
	}

	private void initialise(View view)
	{
		lvOnlinePrices = (ListView) view.findViewById(R.id.listview);
		tvColumnName = (TextView) view.findViewById(R.id.tvColumnName);
		tvColumnName.setText("Private Ads");
		tvNoteBottom = (TextView) view.findViewById(R.id.tvNoteBottom);
		tvNoteBottom.setText("'Now' includes Private adverts active now plus the last 30 days. '90 Days' includes Private adverts active now plus the last 90 days.");
		edtAdvertRegions = (EditText) view.findViewById(R.id.edtAdvertRegions);
		Helper.nonEditableEditText(edtAdvertRegions);
		priceNowAdapter = new PriceNowAdapter(getActivity(), R.layout.list_item_lead_pool, arrayList);
        lvOnlinePrices.setAdapter(priceNowAdapter);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Private Adverts");
	}
	
	private void getValueforAdvertSelected()
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
			values.clear();
			arrayList.clear();
			DataInObject	inObj= new DataInObject();
		
		ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
		parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
		parameterList.add(new Parameter("variantID",vehicleDetails.getVariantID(),Integer.class));
		parameterList.add(new Parameter("year",vehicleDetails.getYear(),Integer.class));
		
		inObj.setMethodname("LoadPrivatePricingHistoryForNational");
		inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
		inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/LoadPrivatePricingHistoryForNational");
		inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
		inObj.setParameterList(parameterList);
		
		//Network call
		showProgressDialog();
		new WebServiceTask(getActivity(),inObj, false,new TaskListener() {
		
		@Override
		public void onTaskComplete(Object result) 
		{
			hideProgressDialog();
			try{
				SoapObject outer= (SoapObject) result;
				SoapObject inner= (SoapObject) outer.getPropertySafely("PricingHistory");
				SmartObject object;
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("PrivateAdvertsAvg30Days")).intValue());
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("PrivateAdvertsHigh30Days")).intValue());
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("PrivateAdvertsLow30Days")).intValue());
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("MileageAvg30Days")).intValue());
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("AdvertsCount30Days")).intValue());
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("AdvertsExcludedCount30Days")).intValue());
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("PrivateAdvertsAvg90Days")).intValue());
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("PrivateAdvertsHigh90Days")).intValue());
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("PrivateAdvertsLow90Days")).intValue());
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("MileageAvg90Days")).intValue());
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("AdvertsCount90Days")).intValue());
				values.add(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("AdvertsExcludedCount90Days")).intValue());
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
			}catch(Exception e){
				e.printStackTrace();
				CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found));
			}
		}}).execute();
	}else{
		CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
	}
  }
	
}
