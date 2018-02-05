package com.nw.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SettingsUser;
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

public class ReadinessFragment extends BaseFragement implements OnClickListener
{
	EditText edRedimnessNewDay,edRedimnessUsedRetailDay,edRedimnessUsedDemoDay;
	Button btnSave;
	
	ArrayList<SettingsUser> settingsUsersList = new ArrayList<SettingsUser>();
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_readiness, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		edRedimnessNewDay = (EditText) view.findViewById(R.id.edRedimnessNewDay);
		edRedimnessUsedRetailDay = (EditText) view.findViewById(R.id.edRedimnessUsedRetailDay);
		edRedimnessUsedDemoDay = (EditText) view.findViewById(R.id.edRedimnessUsedDemoDay);
		btnSave = (Button) view.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		GetRedimlessList();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Trade Readiness Reminders");
		//getActivity().getActionBar().setSubtitle(null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				getActivity().finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void SetRedimlessList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("newDay", edRedimnessNewDay.getText().toString(), String.class));
			parameterList.add(new Parameter("usedRetailDay", edRedimnessUsedRetailDay.getText().toString(), String.class));
			parameterList.add(new Parameter("usedDemoDay", edRedimnessUsedDemoDay.getText().toString(), String.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("SetTradeReadinessReminderSettings");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/SetTradeReadinessReminderSettings");
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
	
	private void GetRedimlessList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetTradeReadinessReminderSettings");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/GetTradeReadinessReminderSettings");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("TradeReadiness");
						SettingsUser settingsUser;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject settingObj = (SoapObject) inner.getProperty(i);
								settingsUser = new SettingsUser();
								settingsUser.setDays(Integer.parseInt(settingObj.getPropertySafelyAsString("Days", "")));
								settingsUsersList.add(settingsUser);
							}
						}
						if(settingsUsersList != null && settingsUsersList.size() >0){
							setUserData();
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
	
	private void setUserData()
	{
		edRedimnessNewDay.setText(""+settingsUsersList.get(0).getDays());
		edRedimnessUsedRetailDay.setText(""+settingsUsersList.get(1).getDays());
		edRedimnessUsedDemoDay.setText(""+settingsUsersList.get(2).getDays());
	}
	
	private boolean checkValidsationProcess()
	{
		if (edRedimnessNewDay.getText().toString().trim().equalsIgnoreCase(""))
		{
			Helper.showToast("Please enter new vehicles from day",getActivity());
			return false;
		}
		
		if (edRedimnessUsedRetailDay.getText().toString().trim().equalsIgnoreCase(""))
		{
			Helper.showToast("Please enter used retail",getActivity());
			return false;
		}
		if (edRedimnessUsedDemoDay.getText().toString().trim().equalsIgnoreCase(""))
		{
			Helper.showToast("Please enter readiness used demos",getActivity());
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnSave:
				if (checkValidsationProcess() == true)
				{
					SetRedimlessList();
				}
				break;

			default:
				break;
		}
		
	}

}
