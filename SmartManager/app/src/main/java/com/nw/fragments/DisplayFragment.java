package com.nw.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
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

public class DisplayFragment extends BaseFragement implements OnClickListener
{
	private CheckBox cbTradePriceBreakdown,cbDaysInStock,cbAppraisal;
	private Button btnSave;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_display, container,false);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}
	
	private void initialise(View view)
	{
		cbTradePriceBreakdown = (CheckBox) view.findViewById(R.id.cbTradePriceBreakdown);
		cbDaysInStock = (CheckBox) view.findViewById(R.id.cbDaysInStock);
		cbAppraisal = (CheckBox) view.findViewById(R.id.cbAppraisal);
		
		btnSave = (Button) view.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		GetDisplayList();
	}

	private void SetDisplay()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("tradePriceBreakdown", cbTradePriceBreakdown.isChecked(), String.class));
			parameterList.add(new Parameter("daysInStock", cbDaysInStock.isChecked(), String.class));
			parameterList.add(new Parameter("appraisal", cbAppraisal.isChecked(), String.class));
			

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("SetTradeAdvertSettings");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/SetTradeAdvertSettings");
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{

				// Network callback
				@Override
				public void onTaskComplete(Object result)
				{
					try
					{
						Helper.Log("Response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Package");
						String Status = inner.getPropertySafelyAsString("Status");
						if (Status.toString().equalsIgnoreCase("Saved"))
						{
							CustomDialogManager.showOkDialog(getActivity(), inner.getPropertySafelyAsString("Message"), new DialogListener()
							{
								@Override
								public void onButtonClicked(int type)
								{
									if (type == Dialog.BUTTON_POSITIVE){
										getActivity().finish();
									}
								}
							});

						}

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
	
	private void GetDisplayList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetTradeAdvertSettings");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/GetTradeAdvertSettings");
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{

				// Network callback
				@Override
				public void onTaskComplete(Object result)
				{
					try
					{
						Helper.Log("Response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("TradeAdvertSetting");
						boolean TradePriceBreakDown = Boolean.parseBoolean(inner.getPropertySafelyAsString("TradePriceBreakDown"));
						boolean DaysInStock = Boolean.parseBoolean(inner.getPropertySafelyAsString("DaysInStock"));
						boolean Appraisal = Boolean.parseBoolean(inner.getPropertySafelyAsString("Appraisal"));
						cbTradePriceBreakdown.setChecked(TradePriceBreakDown);
						cbDaysInStock.setChecked(DaysInStock);
						cbAppraisal.setChecked(Appraisal);

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
	public void onResume() {
		super.onResume();
		showActionBar("My Trade Advert Displays");
		//getActivity().getActionBar().setSubtitle(null);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnSave:
				SetDisplay();
				break;

			default:
				break;
		}
		
	}


}
