package com.nw.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomEditText;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class TradePriceFragment extends BaseFragement
{
	CustomEditText  etTradePrice;
	CustomButton btnSave;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_trade_price, container,false);
		setHasOptionsMenu(true);
		initView(view);
		getTradePrice();
		return view;
	}
	
	private void initView(View view)
	{
		etTradePrice = (CustomEditText) view.findViewById(R.id.etTradePrice);
		btnSave = (CustomButton) view.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (TextUtils.isEmpty(etTradePrice.getText().toString().trim()))
				{
					Helper.showToast("Please enter trade price", getActivity());
					return;
				}
				saveTradePrice();
			}
		});
	}

	protected void saveTradePrice()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("IncrementPercent", etTradePrice.getText().toString().trim(), String.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("SetTradePriceIncrementSettings");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/SetTradePriceIncrementSettings");
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
						hideProgressDialog();
						Helper.Log("SetTradePriceIncrementSettings Response", result.toString());
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
									if (type == Dialog.BUTTON_POSITIVE)
									{
										getActivity().finish();
									}
								}
							});
						}
					} catch (Exception e)
					{
						hideProgressDialog();
						e.printStackTrace();
					}
				}
			}).execute();
		}
		else
		{
			hideProgressDialog();
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
	protected void getTradePrice()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetTradePriceIncrementSetting");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/GetTradePriceIncrementSetting");
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
						hideProgressDialog();
						Helper.Log("GetTradePriceIncrementSetting Response", result.toString());
						//SoapObject outer = (SoapObject) result;
						//SoapObject inner = (SoapObject) outer.getPropertySafely("GetTradePriceIncrementSettingResponse");
					//	String Purchase = outer.getPropertySafelyAsString("GetTradePriceIncrementSettingResult");
						float f = Float.parseFloat(result.toString());
						etTradePrice.setText(""+Math.round(f));
						
					} catch (Exception e)
					{
						hideProgressDialog();
						e.printStackTrace();
					}
				}
			}).execute();
		}
		else
		{
			hideProgressDialog();
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		showActionBar("Trade Price");
		//getActivity().getActionBar().setSubtitle(null);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case android.R.id.home:
			getActivity().finish();
			hideKeyboard(etTradePrice);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


}
