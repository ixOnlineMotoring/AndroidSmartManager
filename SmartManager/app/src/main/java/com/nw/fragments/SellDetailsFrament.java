package com.nw.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nw.adapters.SellDetailsAdapter;
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
import org.ksoap2.serialization.SoapPrimitive;

import java.util.ArrayList;

public class SellDetailsFrament extends BaseFragement implements OnItemClickListener
{
	ArrayList<VehicleDetails> availableToTradeList;
	ArrayList<VehicleDetails> notAvailableToTradeList;
	ArrayList<VehicleDetails> listActiveBidsList;
	ArrayList<VehicleDetails> biddingEndedList;
	SellDetailsAdapter adapter;
	// ActiveBidsFragment activeBidsFragment;
	ActiveTradeFragment activeTradeFragment;
	ListView lstData;
	int selectedPageNumber = 0, groupPosition = 0, total_no_of_records = 1000;
	LinearLayout llayoutdate;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_listview, container, false);
		lstData = (ListView) view.findViewById(R.id.listview);
		llayoutdate = (LinearLayout) view.findViewById(R.id.llayoutdate);
		llayoutdate.setVisibility(View.GONE);
		lstData.setOnItemClickListener(this);
		lstData.setOnScrollListener(new OnScrollListener()
		{

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				int threshold = 1;
				int count = lstData.getCount();
				if (scrollState == SCROLL_STATE_IDLE)
				{
					if (lstData.getLastVisiblePosition() >= count - threshold)
					{
						selectedPageNumber++;

						switch (groupPosition)
						{
							case 0:
								if (!biddingEndedList.isEmpty())
								{
									if (biddingEndedList.size() < total_no_of_records)
									{
										biddingEnded(selectedPageNumber);
									}
								}
								break;

							case 1:
								if (!listActiveBidsList.isEmpty())
								{
									if (listActiveBidsList.size() < total_no_of_records)
									{
										listActiveBids(selectedPageNumber);
									}
								}
								break;

							case 2:
								if (!availableToTradeList.isEmpty())
								{
									if (availableToTradeList.size() < total_no_of_records)
									{
										availableToTrade(selectedPageNumber);
									}
								}
								break;

							case 3:
								if (!notAvailableToTradeList.isEmpty())
								{
									if (notAvailableToTradeList.size() < total_no_of_records)
									{
										notAvailableToTrade(selectedPageNumber);
									}
								}
								break;
							default:
								break;
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		groupPosition = getArguments().getInt("groupPosition");
		attachFooterView(groupPosition);
	}

	private void attachFooterView(int groupPosition)
	{
		switch (groupPosition)
		{
			case 0:
				if (biddingEndedList == null)
				{
					loadData(groupPosition);
				}
				else
				{
					adapter = new SellDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, biddingEndedList, "BiddingEnded");
					lstData.setAdapter(adapter);
				}
				break;

			case 1:
				if (listActiveBidsList == null)
				{
					loadData(groupPosition);
				}
				else
				{
					adapter = new SellDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, listActiveBidsList, "BidsRecevied");
					lstData.setAdapter(adapter);
				}
				break;

			case 2:
				if (availableToTradeList == null)
				{
					loadData(groupPosition);
				}
				else
				{

					adapter = new SellDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, availableToTradeList, "Sell");
					lstData.setAdapter(adapter);
				}
				break;

			case 3:
				if (notAvailableToTradeList == null)
				{
					loadData(groupPosition);
				}
				else
				{
					adapter = new SellDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, notAvailableToTradeList, "Sell");
					lstData.setAdapter(adapter);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				getActivity().finish();
				// getActivity().getSupportFragmentManager().popBackStack();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void loadData(int groupPosition)
	{
		switch (groupPosition)
		{
			case 0:
				biddingEndedList = new ArrayList<VehicleDetails>();
				selectedPageNumber = 0;
				total_no_of_records = 1000;
				biddingEnded(selectedPageNumber);
				break;

			case 1:
				listActiveBidsList = new ArrayList<VehicleDetails>();
				selectedPageNumber = 0;
				total_no_of_records = 1000;
				listActiveBids(selectedPageNumber);
				break;

			case 2:
				availableToTradeList = new ArrayList<VehicleDetails>();
				selectedPageNumber = 0;
				total_no_of_records = 1000;
				availableToTrade(selectedPageNumber);
				break;
			case 3:
				notAvailableToTradeList = new ArrayList<VehicleDetails>();
				selectedPageNumber = 0;
				total_no_of_records = 1000;
				notAvailableToTrade(selectedPageNumber);
				break;
		}
	}

	private void biddingEnded(int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("TradePeriodEndedPaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/TradePeriodEndedPaged");
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
						SoapObject obj = (SoapObject) result;
						SoapObject outer = (SoapObject) obj.getPropertySafely("Results", "default");
						VehicleDetails vehicle;

						for (int i = 0; i < outer.getPropertyCount(); i++)
						{
							vehicle = new VehicleDetails();
							if (outer.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) outer.getProperty(i);
								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "0")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Type", ""));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", "Reg?"));
								vehicle.setIstrade(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("IsTrade", "true")));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "0")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "0")));
								vehicle.setPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Price", "0.0")));
								vehicle.setOfferAmt(Float.parseFloat(vehicleObj.getPropertySafelyAsString("OfferAmount", "0.0")));
								vehicle.setTradeprice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "0.0")));
								vehicle.setRetailPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("RetailPrice", "0.0")));
								vehicle.setLocation(vehicleObj.getPropertySafelyAsString("Location", ""));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setDepartment(vehicleObj.getPropertySafelyAsString("Type", ""));
								vehicle.setAgeValue(vehicleObj.getPropertySafelyAsString("Age", "Days?"));

								biddingEndedList.add(vehicle);
							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) outer.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								if (total_no_of_records == 0)
								{
									CustomDialogManager.showOkDialog(getActivity(), getActivity().getResources().getString(R.string.no_record_found), new DialogListener()
									{

										@Override
										public void onButtonClicked(int type)
										{
											getActivity().finish();
										}
									});
								}
								Helper.Log("total", total);
							}
						}

						if (adapter == null)
						{
							adapter = new SellDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, biddingEndedList, "BiddingEnded");
							lstData.setAdapter(adapter);
						}
						else
						{
							adapter.notifyDataSetChanged();
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
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}

	private void listActiveBids(int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ActiveBidsPaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ActiveBidsPaged");
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
						SoapObject obj = (SoapObject) result;
						SoapObject outer = (SoapObject) obj.getPropertySafely("Result", "default");
						VehicleDetails vehicle;

						for (int i = 0; i < outer.getPropertyCount(); i++)
						{
							vehicle = new VehicleDetails();
							if (outer.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) outer.getProperty(i);
								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "0")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Type", ""));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", "Reg?"));
								vehicle.setIstrade(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("IsTrade", "true")));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "0")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "0")));
								vehicle.setPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Price", "0.0")));
								vehicle.setTradeprice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "0.0")));
								vehicle.setRetailPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("RetailPrice", "0.0")));
								vehicle.setLocation(vehicleObj.getPropertySafelyAsString("Location", ""));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setOfferAmt(Float.parseFloat(vehicleObj.getPropertySafelyAsString("MyHeighestBid", "0.0")));
								vehicle.setHighest(Float.parseFloat(vehicleObj.getPropertySafelyAsString("HeighestBid", "0.0")));
								vehicle.setDepartment(vehicleObj.getPropertySafelyAsString("Department", ""));
								vehicle.setAgeValue(vehicleObj.getPropertySafelyAsString("Age", "Days?"));
								// vehicle.setOfferDate(vehicleObj.getPropertySafelyAsString("OfferDate",
								// ""));
								// vehicle.setOfferStart(vehicleObj.getPropertySafelyAsString("OfferDate",
								// ""));
								// vehicle.setOfferID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("OfferID","0")));
								listActiveBidsList.add(vehicle);
							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) outer.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								if (total_no_of_records == 0)
								{
									CustomDialogManager.showOkDialog(getActivity(), getActivity().getResources().getString(R.string.no_record_found), new DialogListener()
									{

										@Override
										public void onButtonClicked(int type)
										{
											getActivity().finish();
										}
									});
								}
								Helper.Log("total", total);
							}
						}
						if (adapter == null)
						{
							adapter = new SellDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, listActiveBidsList, "BidsRecevied");
							lstData.setAdapter(adapter);
						}
						else
						{
							adapter.notifyDataSetChanged();
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
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}

	private void availableToTrade(int pageNumber)
	{

		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("AvailableToTradePaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/AvailableToTradePaged");
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
						SoapObject obj = (SoapObject) result;
						SoapObject outer = (SoapObject) obj.getPropertySafely("Results", "default");
						VehicleDetails vehicle;

						for (int i = 0; i < outer.getPropertyCount(); i++)
						{
							vehicle = new VehicleDetails();
							if (outer.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) outer.getProperty(i);
								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "0")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Type", ""));
								vehicle.setIstrade(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("IsTrade", "true")));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "0")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "0")));
								vehicle.setPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Price", "0.0")));
								vehicle.setLocation(vehicleObj.getPropertySafelyAsString("Location", ""));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setOfferAmt(Float.parseFloat(vehicleObj.getPropertySafelyAsString("OfferAmount", "0.0")));
								vehicle.setOfferDate(vehicleObj.getPropertySafelyAsString("OfferDate", ""));
								vehicle.setStatusWhen(vehicleObj.getPropertySafelyAsString("StatusWhen", ""));
								vehicle.setStatusWho(vehicleObj.getPropertySafelyAsString("StatusWho", ""));
								vehicle.setHasOffer(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("HasOffers", "false")));
								vehicle.setOfferStart(vehicleObj.getPropertySafelyAsString("OfferStart", ""));
								vehicle.setOfferEnd(vehicleObj.getPropertySafelyAsString("OfferEnd", ""));

								availableToTradeList.add(vehicle);
							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) outer.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								Helper.Log("total", total);
							}
						}
						if (adapter == null)
						{
							adapter = new SellDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, availableToTradeList, "TradeVehicles");
							lstData.setAdapter(adapter);
						}
						else
						{
							adapter.notifyDataSetChanged();
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
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}

	private void notAvailableToTrade(int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("NotAvailableToTradePaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/NotAvailableToTradePaged");
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
						SoapObject obj = (SoapObject) result;
						SoapObject outer = (SoapObject) obj.getPropertySafely("Results", "default");
						VehicleDetails vehicle;

						for (int i = 0; i < outer.getPropertyCount(); i++)
						{
							vehicle = new VehicleDetails();
							if (outer.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) outer.getProperty(i);
								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "0")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Type", ""));
								vehicle.setIstrade(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("IsTrade", "true")));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "0")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "0")));
								vehicle.setPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Price", "0.0")));
								vehicle.setLocation(vehicleObj.getPropertySafelyAsString("Location", ""));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setOfferAmt(Float.parseFloat(vehicleObj.getPropertySafelyAsString("OfferAmount", "0.0")));
								vehicle.setOfferDate(vehicleObj.getPropertySafelyAsString("OfferDate", ""));
								vehicle.setStatusWhen(vehicleObj.getPropertySafelyAsString("StatusWhen", ""));
								vehicle.setStatusWho(vehicleObj.getPropertySafelyAsString("StatusWho", ""));
								vehicle.setHasOffer(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("HasOffers", "false")));
								vehicle.setOfferStart(vehicleObj.getPropertySafelyAsString("OfferStart", ""));
								vehicle.setOfferEnd(vehicleObj.getPropertySafelyAsString("OfferEnd", ""));

								notAvailableToTradeList.add(vehicle);
							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) outer.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								Helper.Log("total", total);
							}
						}
						if (adapter == null)
						{
							adapter = new SellDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, notAvailableToTradeList, "Sell");
							lstData.setAdapter(adapter);
						}
						else
						{
							adapter.notifyDataSetChanged();
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
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		if (groupPosition == 2 || groupPosition == 3)
		{
			AvailableToTradeDetailsFragment detailsFragment = new AvailableToTradeDetailsFragment();
			Bundle bundle = new Bundle();
			if (groupPosition == 2)
			{
				bundle.putInt("vehicleId", availableToTradeList.get(arg2).getUsedVehicleStockID());
				bundle.putInt("year", availableToTradeList.get(arg2).getYear());
				bundle.putString("friendlyName", availableToTradeList.get(arg2).getFriendlyName());
				bundle.putString("type", availableToTradeList.get(arg2).getType());
				bundle.putString("colour", availableToTradeList.get(arg2).getColour());
				bundle.putString("location", availableToTradeList.get(arg2).getLocation());
				bundle.putDouble("price", availableToTradeList.get(arg2).getPrice());
				bundle.putString("stockCode", availableToTradeList.get(arg2).getStockCode());
				bundle.putFloat("offerAmount", availableToTradeList.get(arg2).getOfferAmt());
				bundle.putString("offerDate", availableToTradeList.get(arg2).getOfferDate());
				bundle.putString("offerStart", availableToTradeList.get(arg2).getOfferStart());
				bundle.putString("offerEnd", availableToTradeList.get(arg2).getOfferEnd());
				bundle.putInt("mileage", availableToTradeList.get(arg2).getMileage());
				bundle.putString("mileageType", "Km");
			}
			else if (groupPosition == 3)
			{
				bundle.putInt("vehicleId", notAvailableToTradeList.get(arg2).getUsedVehicleStockID());
				bundle.putInt("year", notAvailableToTradeList.get(arg2).getYear());
				bundle.putString("friendlyName", notAvailableToTradeList.get(arg2).getFriendlyName());
				bundle.putString("type", notAvailableToTradeList.get(arg2).getType());
				bundle.putString("colour", notAvailableToTradeList.get(arg2).getColour());
				bundle.putString("location", notAvailableToTradeList.get(arg2).getLocation());
				bundle.putDouble("price", notAvailableToTradeList.get(arg2).getPrice());
				bundle.putString("stockCode", notAvailableToTradeList.get(arg2).getStockCode());
				bundle.putFloat("offerAmount", notAvailableToTradeList.get(arg2).getOfferAmt());
				bundle.putString("offerDate", notAvailableToTradeList.get(arg2).getOfferDate());
				bundle.putString("offerStart", notAvailableToTradeList.get(arg2).getOfferStart());
				bundle.putString("offerEnd", notAvailableToTradeList.get(arg2).getOfferEnd());
				bundle.putInt("mileage", notAvailableToTradeList.get(arg2).getMileage());
				bundle.putString("mileageType", "Km");
			}
			bundle.putString("from", "sell");
			detailsFragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(this.getId(), detailsFragment).addToBackStack("listFragment").commit();
		}
		else
		{
			// activeBidsFragment= new ActiveBidsFragment();
			activeTradeFragment = new ActiveTradeFragment();
			Bundle bundle = new Bundle();
			if (groupPosition == 0)
			{
				bundle.putInt("vehicleId", biddingEndedList.get(arg2).getUsedVehicleStockID());
				bundle.putDouble("RetailPrice", biddingEndedList.get(arg2).getRetailPrice());
				bundle.putString("stock_code", biddingEndedList.get(arg2).getStockCode());
				bundle.putString("offer_client", biddingEndedList.get(arg2).getOfferClient());
				bundle.putDouble("price", biddingEndedList.get(arg2).getPrice());
				bundle.putString("type", biddingEndedList.get(arg2).getType());
				bundle.putString("offerStart", biddingEndedList.get(arg2).getOfferStart());
				bundle.putString("offerEnd", biddingEndedList.get(arg2).getOfferEnd());
				bundle.putFloat("offerAmount", biddingEndedList.get(arg2).getOfferAmt());
				bundle.putString("offerStatus", "Ended");
				bundle.putInt("offerId", biddingEndedList.get(arg2).getOfferID());
				bundle.putDouble("BidPrice", biddingEndedList.get(arg2).getOfferAmt());
				bundle.putDouble("TradePrice", biddingEndedList.get(arg2).getPrice());
				bundle.putString("Days", biddingEndedList.get(arg2).getAgeValue());
				bundle.putString("Department", biddingEndedList.get(arg2).getType());
				bundle.putString("Screentype", "BiddingEnded");
			}
			else if (groupPosition == 1)
			{
				bundle.putInt("vehicleId", listActiveBidsList.get(arg2).getUsedVehicleStockID());
				bundle.putDouble("RetailPrice", listActiveBidsList.get(arg2).getRetailPrice());
				bundle.putString("stock_code", listActiveBidsList.get(arg2).getStockCode());
				bundle.putString("offer_client", listActiveBidsList.get(arg2).getOfferClient());
				bundle.putDouble("price", listActiveBidsList.get(arg2).getPrice());
				bundle.putString("type", listActiveBidsList.get(arg2).getType());
				bundle.putString("offerStart", listActiveBidsList.get(arg2).getOfferStart());
				bundle.putString("offerEnd", "");
				bundle.putFloat("offerAmount", listActiveBidsList.get(arg2).getOfferAmt());
				bundle.putString("offerStatus", "Active");
				bundle.putInt("offerId", listActiveBidsList.get(arg2).getOfferID());
				bundle.putDouble("BidPrice", listActiveBidsList.get(arg2).getHighest());
				bundle.putDouble("TradePrice", listActiveBidsList.get(arg2).getTradeprice());
				bundle.putString("Days", listActiveBidsList.get(arg2).getAgeValue());
				bundle.putString("Department", listActiveBidsList.get(arg2).getDepartment());
				bundle.putString("Screentype", "BidsRecevied");
			}
			activeTradeFragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(this.getId(), activeTradeFragment).addToBackStack("listFragment").commit();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar(getArguments().getString("title"));
		//getActivity().getActionBar().setSubtitle(null);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (activeTradeFragment != null)
			activeTradeFragment.onActivityResult(requestCode, resultCode, data);

	}
}
