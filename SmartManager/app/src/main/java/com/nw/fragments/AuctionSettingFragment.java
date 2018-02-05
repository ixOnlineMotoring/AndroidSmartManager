package com.nw.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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

public class AuctionSettingFragment extends BaseFragement implements OnClickListener
{
	EditText edMyBidsAskingPrice, etMinBidIncrement, edByeNowPrice, edAvaliableFrom, edAvaliableFor, edExtendedBy;
	CheckBox cbAcceptBids, cbNoBidExtend;
	Button btnSave;
	ArrayList<String> mylist;
	String[] AvailableFor = { "8 hours", "12 hours", "24 hours", "48 hours", "7 days", "2 weeks", "1 Month", "Indefinite" };
	int avaliable_from_value = 0;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_auctionsetting, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		edMyBidsAskingPrice = (EditText) view.findViewById(R.id.edMyBidsAskingPrice);
		etMinBidIncrement = (EditText) view.findViewById(R.id.etMinBidIncrement);
		edByeNowPrice = (EditText) view.findViewById(R.id.edByeNowPrice);
		edAvaliableFrom = (EditText) view.findViewById(R.id.edAvaliableFrom);
		edAvaliableFor = (EditText) view.findViewById(R.id.edAvaliableFor);
		edExtendedBy = (EditText) view.findViewById(R.id.edExtendedBy);

		cbAcceptBids = (CheckBox) view.findViewById(R.id.cbAcceptBids);
		cbNoBidExtend = (CheckBox) view.findViewById(R.id.cbNoBidExtend);

		btnSave = (Button) view.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);

		edMyBidsAskingPrice.setOnClickListener(this);
		etMinBidIncrement.setOnClickListener(this);
		edByeNowPrice.setOnClickListener(this);
		edAvaliableFrom.setOnClickListener(this);
		edAvaliableFor.setOnClickListener(this);
		edExtendedBy.setOnClickListener(this);

		GetAuctionList();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("My Auctions Settings");
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.edMyBidsAskingPrice:
				mylist = new ArrayList<String>();
				for (int i = 50; i <= 100; i += 5)
				{
					mylist.add(i + "%");
				}
				Helper.showDropDown(edMyBidsAskingPrice, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, mylist), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						edMyBidsAskingPrice.setText(mylist.get(position));
					}
				});
				break;
			case R.id.etMinBidIncrement:
				mylist = new ArrayList<String>();
				for (int i = 1; i <= 30; i++)
				{
					mylist.add(i + "%");
				}
				Helper.showDropDown(etMinBidIncrement, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, mylist), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						etMinBidIncrement.setText(mylist.get(position));
					}
				});
				break;
			case R.id.edByeNowPrice:
				mylist = new ArrayList<String>();
				for (int i = 1; i <= 50; i++)
				{
					mylist.add(i + "%");
				}
				Helper.showDropDown(edByeNowPrice, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, mylist), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						edByeNowPrice.setText(mylist.get(position));
					}
				});
				break;
			case R.id.edAvaliableFrom:
				mylist = new ArrayList<String>();
				for (int i = 6; i <= 17; i++)
				{
					if (i == 6)
						mylist.add("First Bid");
					else
						mylist.add("Daily at " + i + "h00");
				}

				Helper.showDropDown(edAvaliableFrom, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, mylist), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						avaliable_from_value = position;
						edAvaliableFrom.setText(mylist.get(position));
					}
				});
				break;
			case R.id.edAvaliableFor:
				mylist = new ArrayList<String>();
				for (int i = 0; i < AvailableFor.length; i++)
				{
					mylist.add("" + AvailableFor[i]);
				}
				Helper.showDropDown(edAvaliableFor, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, mylist), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						edAvaliableFor.setText(mylist.get(position));
					}
				});
				break;
			case R.id.edExtendedBy:
				mylist = new ArrayList<String>();
				for (int i = 5; i <= 50; i += 5)
				{
					mylist.add(i + "min");
				}
				Helper.showDropDown(edExtendedBy, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, mylist), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						edExtendedBy.setText(mylist.get(position));
					}
				});
				break;
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

	private boolean checkValidsationProcess()
	{
		if (edMyBidsAskingPrice.getText().toString().trim().equalsIgnoreCase("-- None --"))
		{
			Helper.showToast("Please select Min bid as % of asking price", getActivity());
			return false;
		}
		if (etMinBidIncrement.getText().toString().trim().equalsIgnoreCase("-- None --"))
		{
			Helper.showToast("Please select Min bid increase %", getActivity());
			return false;
		}
		if (edByeNowPrice.getText().toString().trim().equalsIgnoreCase("-- None --"))
		{
			Helper.showToast("Please select Buy now price; Trade price + %", getActivity());
			return false;
		}
		if (edAvaliableFrom.getText().toString().trim().equalsIgnoreCase("-- None --"))
		{
			Helper.showToast("Please select Available from", getActivity());
			return false;
		}
		if (edAvaliableFor.getText().toString().trim().equalsIgnoreCase("-- None --"))
		{
			Helper.showToast("Please select Available for", getActivity());
			return false;
		}

		if (edExtendedBy.getText().toString().trim().equalsIgnoreCase("-- None --"))
		{
			Helper.showToast("Please select If bid received within last 5 mins, extend by", getActivity());
			return false;
		}

		return true;
	}

	private void SetRedimlessList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			if (cbAcceptBids.isChecked())
			{
				parameterList.add(new Parameter("acceptBids", 1, Integer.class));
			}
			else
			{
				parameterList.add(new Parameter("acceptBids", 0, Integer.class));
			}

			parameterList.add(new Parameter("minBidPercent", Integer.parseInt((edMyBidsAskingPrice.getText().toString().trim()).replace("%", "")), Integer.class));
			parameterList.add(new Parameter("minBidIncrease", Integer.parseInt((etMinBidIncrement.getText().toString().trim()).replace("%", "")), Integer.class));
			parameterList.add(new Parameter("buyNowPrice", (edByeNowPrice.getText().toString()).replace("%", "").trim(), Integer.class));
			/*
			 * if
			 * (edAvaliableFrom.getText().toString().equalsIgnoreCase("First Bid"
			 * )) { parameterList.add(new Parameter("availableFrom", "0",
			 * Integer.class)); } else {
			 */
			// int avaliblefrom =
			// Integer.parseInt(edAvaliableFrom.getText().toString().trim().substring(9,
			// edAvaliableFrom.getText().toString().length() - 3));
			parameterList.add(new Parameter("availableFrom", avaliable_from_value, Integer.class));
			// }

			if (edAvaliableFor.getText().toString().equalsIgnoreCase("Indefinite"))
			{
				parameterList.add(new Parameter("availableFor", "0", Integer.class));
			}
			else if (edAvaliableFor.getText().toString().equalsIgnoreCase("1 Month"))
			{
				parameterList.add(new Parameter("availableFor", "720", Integer.class));
			}
			else if (edAvaliableFor.getText().toString().equalsIgnoreCase("2 weeks"))
			{
				parameterList.add(new Parameter("availableFor", "336", Integer.class));
			}
			else if (edAvaliableFor.getText().toString().equalsIgnoreCase("7 days"))
			{
				parameterList.add(new Parameter("availableFor", "168", Integer.class));
			}
			else
			{
				parameterList.add(new Parameter("availableFor", edAvaliableFor.getText().toString().trim().substring(0, edAvaliableFor.getText().toString().length() - 5), Integer.class));
			}
			if (cbNoBidExtend.isChecked())
			{
				parameterList.add(new Parameter("noBidExtend", 1, Integer.class));
			}
			else
			{
				parameterList.add(new Parameter("noBidExtend", 0, Integer.class));
			}
			parameterList.add(new Parameter("extendPeriod", (edExtendedBy.getText().toString().trim()).replaceAll("min", ""), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("SetTradeAuctionSettings");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/SetTradeAuctionSettings");
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

	private void GetAuctionList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetTradeAuctionSettings");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/GetTradeAuctionSettings");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("TradeAuction");
						boolean acceptBids = Boolean.parseBoolean(inner.getPropertySafelyAsString("AcceptBids"));
						int minBidPercent = Integer.parseInt(inner.getPropertySafelyAsString("MinBidPercent"));
						int minBidIncrease = Integer.parseInt(inner.getPropertySafelyAsString("MinBidIncrease"));
						int buyNowPrice = Integer.parseInt(inner.getPropertySafelyAsString("BuyNowPrice"));
						int availableFrom = Integer.parseInt(inner.getPropertySafelyAsString("AvailableFrom"));
						int availableFor = Integer.parseInt(inner.getPropertySafelyAsString("AvailableFor"));
						boolean noBidExtend = Boolean.parseBoolean(inner.getPropertySafelyAsString("NoBidExtend"));
						int extendPeriod = Integer.parseInt(inner.getPropertySafelyAsString("ExtendPeriod"));

						cbAcceptBids.setChecked(acceptBids);
						edMyBidsAskingPrice.setText(minBidPercent + "%");
						etMinBidIncrement.setText(minBidIncrease + "%");
						edByeNowPrice.setText(buyNowPrice + "%");
						if (availableFrom == 0)
						{
							edAvaliableFrom.setText("First Bid");
						}
						else
						{
							availableFrom = (availableFrom + 6);
							edAvaliableFrom.setText("Daily at " + availableFrom + "h00");
						}

						if (availableFor == 0)
						{
							edAvaliableFor.setText("Indefinite");

						}
						else if (availableFor == 720)
						{
							edAvaliableFor.setText("1 Month");
						}
						else if (availableFor == 336)
						{
							edAvaliableFor.setText("2 weeks");
						}
						else if (availableFor == 168)
						{
							edAvaliableFor.setText("7 days");
						}
						else
						{
							edAvaliableFor.setText(availableFor + " hours");
						}

						cbNoBidExtend.setChecked(noBidExtend);
						edExtendedBy.setText(extendPeriod + "min");
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
