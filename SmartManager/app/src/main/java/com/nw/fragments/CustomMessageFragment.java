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

public class CustomMessageFragment extends BaseFragement implements OnClickListener
{
	private EditText edPurchasesMessage, edMakeAnOfferMessage, edSecretTenderMessage;
	private Button btnSave;
	ArrayList<SettingsUser> settingsUsersList = new ArrayList<SettingsUser>();

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_custom_message, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		edPurchasesMessage = (EditText) view.findViewById(R.id.edPurchasesMessage);
		edMakeAnOfferMessage = (EditText) view.findViewById(R.id.edMakeAnOfferMessage);
		edSecretTenderMessage = (EditText) view.findViewById(R.id.edSecretTenderMessage);

		btnSave = (Button) view.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		getMessages();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Custom Messages");
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

	private void getMessages()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ListTradeCustomMessages");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ListTradeCustomMessages");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("TradeCustomMessages");
						String Purchase = inner.getPropertySafelyAsString("Purchase");
						String Offer = inner.getPropertySafelyAsString("Offer");
						String Tender = inner.getPropertySafelyAsString("Tender");

						edPurchasesMessage.setText(Purchase);
						edMakeAnOfferMessage.setText(Offer);
						edSecretTenderMessage.setText(Tender);

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
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnSave:
				if (checkValidsationProcess() == true)
				{
					SetMessages();
				}
				break;

			default:
				break;
		}
	}

	private void SetMessages()
	{

		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("purchase", edPurchasesMessage.getText().toString(), String.class));
			parameterList.add(new Parameter("offer", edMakeAnOfferMessage.getText().toString(), String.class));
			parameterList.add(new Parameter("tender", edSecretTenderMessage.getText().toString(), String.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("SaveTradeCustomMessages");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/SaveTradeCustomMessages");
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
									if (type == Dialog.BUTTON_POSITIVE)
									{
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

	private boolean checkValidsationProcess()
	{
		if (edPurchasesMessage.getText().toString().trim().equalsIgnoreCase(""))
		{
			Helper.showToast("Please enter Purchases Message ", getActivity());
			return false;
		}
		if (edMakeAnOfferMessage.getText().toString().trim().equalsIgnoreCase(""))
		{
			Helper.showToast("Please enter MakeAnOffer Message ", getActivity());
			return false;
		}
		if (edSecretTenderMessage.getText().toString().trim().equalsIgnoreCase(""))
		{
			Helper.showToast("Please enter SecretTender Message ", getActivity());
			return false;
		}
		return true;
	}
}
