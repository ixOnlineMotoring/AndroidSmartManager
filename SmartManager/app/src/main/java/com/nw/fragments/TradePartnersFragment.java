package com.nw.fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import com.android.volley.VolleyError;
import com.nw.adapters.TradePartnersSettingAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SettingsUser;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class TradePartnersFragment extends BaseFragement implements OnClickListener
{
	ListView lvListTradePartners;
	TradePartnersSettingAdapter tradePartnersSettingAdapter;
	ArrayList<SettingsUser> tradePartners;
	Button btAddPartner;
	boolean isGlobalAllowed=false;
	RadioButton rbEveryOne,rbLimitedAccess;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_trade_partners, container, false);
		setHasOptionsMenu(true);
		if (tradePartners == null)
		{
			tradePartners = new ArrayList<SettingsUser>();
		}
		getTradePartner();
		initialise(view);
		return view;
	}
	
	private void initialise(View view)
	{
		rbEveryOne = (RadioButton) view.findViewById(R.id.rbEveryone);
		rbLimitedAccess = (RadioButton) view.findViewById(R.id.rbLimitedAccess);
		lvListTradePartners = (ListView) view.findViewById(R.id.lvListTradePartners);
		btAddPartner = (Button) view.findViewById(R.id.btAddPartner);
		tradePartnersSettingAdapter = new TradePartnersSettingAdapter(getActivity(), 0, tradePartners);
		lvListTradePartners.setAdapter(tradePartnersSettingAdapter);
		btAddPartner.setOnClickListener(this);
		lvListTradePartners.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				AddEditTradePartnersFragment addEditTradePartnersFragment = new AddEditTradePartnersFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("trader_id",tradePartners.get(position).getID());
				bundle.putParcelable("TraderDetail", tradePartners.get(position));
				addEditTradePartnersFragment.setArguments(bundle);
				getFragmentManager().beginTransaction().replace(R.id.Container, addEditTradePartnersFragment).addToBackStack(null).commit();
			}
		});
		rbEveryOne.setOnClickListener(this);
		rbLimitedAccess.setOnClickListener(this);
	}

	protected void saveTradePartnerSetting(boolean isEveryOne)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			StringBuilder soapMessage = new StringBuilder();
			soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<SaveTradePartnerSetting xmlns=\"" + Constants.TRADER_NAMESPACE + "\">");
			soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
			soapMessage.append("<ClientID>"+ DataManager.getInstance().user.getDefaultClient().getId()+ "</ClientID>");
			if (isEveryOne)
			{
				soapMessage.append("<Everyone>"+ 1+ "</Everyone>");
			}else {
				soapMessage.append("<Everyone>"+ 0+ "</Everyone>");
			}
			soapMessage.append("</SaveTradePartnerSetting>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");

			Helper.Log("SaveTradePartnerSetting request:", soapMessage.toString());

			VollyResponseListener listener = new VollyResponseListener()
			{

				@Override
				public void onErrorResponse(VolleyError error)
				{
					hideProgressDialog();
					Helper.Log("Error: ", error.getMessage());
				}

				@Override
				public void onResponse(String response)
				{
					hideProgressDialog();
					if (response == null)
					{
						return;
					}
					Helper.Log("SaveTradePartnerSetting Response:", response);
					String msg = ParserManager.parseMessageChecker(response);
					hideProgressDialog();
					CustomDialogManager.showOkDialog(getActivity(), msg, new DialogListener()
					{
							@Override
							public void onButtonClicked(int type)
							{
								getActivity().getFragmentManager().popBackStack();	
							}
					});
				}
			};

			VollyCustomRequest request = new VollyCustomRequest(Constants.TRADER_WEBSERVICE_URL, soapMessage.toString(), 
					Constants.TRADER_NAMESPACE + "/ITradeService/SaveTradePartnerSetting",listener);

			try
			{
				request.init("getListMembersForClients");
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
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
		showActionBar("Trade Partners");
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

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btAddPartner:
				AddEditTradePartnersFragment addEditTradePartnersFragment = new AddEditTradePartnersFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("trader_id",0);
				addEditTradePartnersFragment.setArguments(bundle);
				getFragmentManager().beginTransaction().replace(R.id.Container, addEditTradePartnersFragment).addToBackStack(null).commit();
			break;
			case R.id.rbEveryone:
				saveTradePartnerSetting(true);
				break;

			case R.id.rbLimitedAccess:
				saveTradePartnerSetting(false);
				break;
		}
	}
	
	private void getTradePartner()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetTradePartnerSettings");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/GetTradePartnerSettings");
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
						SoapObject settingObj = (SoapObject) outer.getPropertySafely("TradePartnerSetting");
						isGlobalAllowed =  Boolean.parseBoolean(settingObj.getPropertySafelyAsString("AllowGlobal"));
						if (isGlobalAllowed)
						{
							rbEveryOne.setChecked(true);
						}else {
							rbLimitedAccess.setChecked(true);
						}
						SoapObject inner = (SoapObject) settingObj.getPropertySafely("TradePartners");
						SettingsUser settingsUser;
						tradePartners.clear();
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								settingsUser = new SettingsUser();
								settingsUser.setSettingID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("SettingID")));
								settingsUser.setTraderPeriod(Integer.parseInt(vehicleObj.getPropertySafelyAsString("TraderPeriod")));
								settingsUser.setAuctionAccess(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("AuctionAccess")));
								settingsUser.setTenderAccess(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("TenderAccess")));
								settingsUser.setID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("ID")));
								settingsUser.setType(vehicleObj.getPropertySafelyAsString("Type"));
								settingsUser.setTypeID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("TypeID")));
								settingsUser.setMemberName(vehicleObj.getPropertySafelyAsString("Name"));
								tradePartners.add(settingsUser);
							}
						}
						tradePartnersSettingAdapter.notifyDataSetChanged();
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
	
}
