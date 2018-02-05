package com.nw.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
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

public class SimpleLogicFragment extends BaseFragement
{
	VehicleDetails vehicleDetails;
	TextView tvVehicleName,tvAgeforVehicle,tvMileageInKm,
					tvLatestPrice,tvSuggestedRetails,tvSuggestedTrade,tvMileageAdjustmentAdd,tvMileageAdjustmentLess,tvAgeDeprication;
	boolean isDataloaded =false;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(getArguments()!=null)
		{
		vehicleDetails = getArguments().getParcelable("vehicleDetails");}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_simple_logic, container, false);
		initialise(view);
		if (!isDataloaded)
		{
			getValueForSimpleLogic();
		}
		return view;
	}

	private void initialise(View view)
	{
		tvVehicleName  = (TextView) view.findViewById(R.id.tvVehicleName);
		if (vehicleDetails!=null)
		{
			tvVehicleName.setText(Html.fromHtml("<font color=#ffffff>" + vehicleDetails.getYear() + "</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue) + ">"
					+ vehicleDetails.getFriendlyName() + "</font>"));
		}
		tvAgeforVehicle  = (TextView) view.findViewById(R.id.tvAgeforVehicle);
		tvMileageInKm  = (TextView) view.findViewById(R.id.tvMileageInKm);
		tvLatestPrice  = (TextView) view.findViewById(R.id.tvLatestPrice);
		tvMileageAdjustmentAdd = (TextView) view.findViewById(R.id.tvMileageAdjustmentAdd);
		tvMileageAdjustmentLess = (TextView) view.findViewById(R.id.tvMileageAdjustmentLess);
		tvAgeDeprication= (TextView) view.findViewById(R.id.tvAgeDeprication);
		tvSuggestedRetails  = (TextView) view.findViewById(R.id.tvSuggestedRetails);
		tvSuggestedTrade  = (TextView) view.findViewById(R.id.tvSuggestedTrade);
	}
	
	private void getValueForSimpleLogic()
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
			//Add parameters to request in arraylist
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("variantID",vehicleDetails.getVariantID(),Integer.class));
			parameterList.add(new Parameter("year",vehicleDetails.getYear(),Integer.class));
			parameterList.add(new Parameter("mileage",vehicleDetails.getMileage(),Integer.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("LoadVehicleRetailDetailsForVariant");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/LoadVehicleRetailDetailsForVariant");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			showProgressDialog();
			new WebServiceTask(getActivity(),inObj, false,new TaskListener() {
			
			@Override
			public void onTaskComplete(Object result) 
			{
				
				try{
					SoapObject outer= (SoapObject) result;
					SoapObject inner= (SoapObject) outer.getPropertySafely("VehicleRetailDetails");
					if(inner.getPropertySafelyAsString("Age").equals("anyType{}"))
						tvAgeforVehicle.setText("0");
					else {
						tvAgeforVehicle.setText(inner.getPropertySafelyAsString("Age"));
					}
					if(inner.getPropertySafelyAsString("Mileage").equals("anyType{}"))
						tvMileageInKm.setText("0");
					else {
						tvMileageInKm.setText(inner.getPropertySafelyAsString("Mileage")+" Km");
					}
					tvLatestPrice.setText(Helper.formatPrice(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("LatestPrice")).intValue()));
					tvAgeDeprication.setText(Helper.formatPrice(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("AgeDepreciation")).intValue()));
					try
					{
						tvMileageAdjustmentAdd.setText("("+Helper.formatPrice(""+Integer.parseInt(inner.getPropertySafelyAsString("MileageAdjustment")))+")");
						tvMileageAdjustmentLess.setText("  -  ");
					} catch (Exception e)
					{
						tvMileageAdjustmentAdd.setText("  -  ");
						tvMileageAdjustmentLess.setText("("+Helper.formatPrice(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("MileageAdjustment")).intValue())+")");
					}
					tvSuggestedRetails.setText(Helper.formatPrice(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("Retail")).intValue()));
					tvSuggestedTrade.setText(Helper.formatPrice(""+NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("Trade")).intValue()));
					hideProgressDialog();
					isDataloaded=true;
				}catch(Exception e){
					e.printStackTrace();
					hideProgressDialog();
					CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found),new DialogListener()
					{
						@Override
						public void onButtonClicked(int type)
						{
							getFragmentManager().popBackStack();
						}
					});
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
		showActionBar("Simple Logic");
	}
	
}
