package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.MyBuyersAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.MyBuyers;
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

public class MySellersFragment extends BaseFragement
{
	private ListView listview;
	private TextView tvContactNote,tvTopNote;
	ArrayList<MyBuyers> almyBuyers;
	MyBuyersAdapter adapter;
	LinearLayout llayoutnote;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_my_buyers, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		listview = (ListView) view.findViewById(R.id.listview);
		llayoutnote = (LinearLayout) view.findViewById(R.id.llayoutnote);
		tvContactNote = (TextView) view.findViewById(R.id.tvContactNote);
		tvTopNote= (TextView) view.findViewById(R.id.tvTopNote);
		tvTopNote.setText(R.string.seller_note);
		MyBuyersList();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("My Sellers");
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

	private void MyBuyersList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetTradeSellerSettings");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/GetTradeSellerSettings");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("TradeSellerSetting");
						llayoutnote.setVisibility(View.GONE);
						tvContactNote.setVisibility(View.VISIBLE);
						SoapObject TradePartner = (SoapObject) inner.getPropertySafely("SellingPartners");
						MyBuyers myBuyers;
						almyBuyers = new ArrayList<MyBuyers>();
						for (int i = 0; i < TradePartner.getPropertyCount(); i++)
						{
							if (TradePartner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) TradePartner.getProperty(i);
								myBuyers = new MyBuyers();

								myBuyers.setSettingID(vehicleObj.getPropertySafelyAsString("SettingID", ""));
								myBuyers.setTraderPeriod(vehicleObj.getPropertySafelyAsString("TraderPeriod", ""));
								myBuyers.setAuctionAccess(vehicleObj.getPropertySafelyAsString("AuctionAccess", ""));
								myBuyers.setTenderAccess(vehicleObj.getPropertySafelyAsString("TenderAccess", ""));
								myBuyers.setID(vehicleObj.getPropertySafelyAsString("ID", ""));
								myBuyers.setName(vehicleObj.getPropertySafelyAsString("Name", ""));
								myBuyers.setType(vehicleObj.getPropertySafelyAsString("Type", ""));
								myBuyers.setTypeID(vehicleObj.getPropertySafelyAsString("TypeID", ""));

								almyBuyers.add(myBuyers);
							}
						}
						if (almyBuyers.size()!=0)
						{
							if (adapter == null)
							{
								adapter = new MyBuyersAdapter(getActivity(), R.layout.list_item_available_to_trade, almyBuyers, "MyByers");
								listview.setAdapter(adapter);
							}
							else
							{
								adapter.notifyDataSetChanged();
							}
						}
		 				else {
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found),new DialogListener()
							{
								@Override
								public void onButtonClicked(int type)
								{
									getActivity().getFragmentManager().popBackStack();
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

}
