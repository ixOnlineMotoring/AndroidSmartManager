package com.nw.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nw.adapters.BidsDetailsAdapter;
import com.nw.adapters.BidsDetailsAutomatedBiddingAdapter;
import com.nw.adapters.BidsDetailsPrivateOffersAdapter;
import com.nw.interfaces.DateListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

@SuppressLint("SimpleDateFormat")
public class MyBidsDetailsFragment extends BaseFragement implements OnItemClickListener, OnClickListener
{

	ListView lvMyBids;
	int groupPosition = 0, selectedPageNumber = 0, total_no_of_records = 0;
	ArrayList<VehicleDetails> losingBidsList;
	ArrayList<VehicleDetails> winningBidsList;
	ArrayList<VehicleDetails> wonList;
	ArrayList<VehicleDetails> lostList;
	ArrayList<VehicleDetails> bidsWithdrawList;
	ArrayList<VehicleDetails> bidsCancelledList;
	ArrayList<VehicleDetails> privateOffersList;
	ArrayList<VehicleDetails> automatedBiddingList;
	BidsDetailsAdapter adapter;
	BidsDetailsPrivateOffersAdapter bidsDetailsPrivateOffersAdapter;
	BidsDetailsAutomatedBiddingAdapter automatedBiddingAdapter;
	EditText edFromDate, edToDate;
	Button bSearch;

	LinearLayout llayoutdate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_listview, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}

	@SuppressLint("SimpleDateFormat")
	private void initialise(View view)
	{
		lvMyBids = (ListView) view.findViewById(R.id.listview);
		lvMyBids.setOnItemClickListener(this);

		edFromDate = (EditText) view.findViewById(R.id.edStartDate);
		edToDate = (EditText) view.findViewById(R.id.edEndDate);
		bSearch = (Button) view.findViewById(R.id.bSearch);

		llayoutdate = (LinearLayout) view.findViewById(R.id.llayoutdate);

		edFromDate.setOnClickListener(this);
		edToDate.setOnClickListener(this);
		bSearch.setOnClickListener(this);

		Calendar calendar = Calendar.getInstance();
		String today = new SimpleDateFormat("dd MMM yyyy").format(calendar.getTime());

		calendar.add(Calendar.MONTH, -1);
		String pastDate = new SimpleDateFormat("dd MMM yyyy").format(calendar.getTime());

		edFromDate.setText(pastDate);
		edToDate.setText(today);

		groupPosition = getArguments().getInt("Position");

		// this is to check the particular adaptor is null or not and set the
		// data respectively.
		setAdaptorifnull(groupPosition);

		lvMyBids.setOnScrollListener(new OnScrollListener()
		{

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				int threshold = 1;
				int count = lvMyBids.getCount();
				if (scrollState == SCROLL_STATE_IDLE)
				{
					if (lvMyBids.getLastVisiblePosition() >= count - threshold)
					{
						selectedPageNumber++;

						switch (groupPosition)
						{
							case 0:
								if (!losingBidsList.isEmpty())
								{
									if (losingBidsList.size() < total_no_of_records)
									{
										losingBidsList(selectedPageNumber);
									}
								}
								break;

							case 1:
								if (!winningBidsList.isEmpty())
								{
									if (winningBidsList.size() < total_no_of_records)
									{
										winningBidsList(selectedPageNumber);
									}
								}
								break;

							case 2:
								if (!automatedBiddingList.isEmpty())
								{
									if (automatedBiddingList.size() < total_no_of_records)
									{
										automatedbiddingList(selectedPageNumber);
									}
								}
								break;

							case 3:
								if (!wonList.isEmpty())
								{
									if (wonList.size() < total_no_of_records)
									{
										wonList(selectedPageNumber);
									}
								}
								break;
							case 4:
								if (!lostList.isEmpty())
								{
									if (lostList.size() < total_no_of_records)
									{
										lostList(selectedPageNumber);
									}
								}

								break;
							case 5:
								if (!privateOffersList.isEmpty())
								{
									if (privateOffersList.size() < total_no_of_records)
									{
										privateOfferList(selectedPageNumber);
									}
								}
								break;
							case 6:
								if (!bidsWithdrawList.isEmpty())
								{
									if (bidsWithdrawList.size() < total_no_of_records)
									{
										bidswithdrawnList(selectedPageNumber);
									}
								}
								break;
							case 7:
								if (!bidsCancelledList.isEmpty())
								{
									if (bidsCancelledList.size() < total_no_of_records)
									{
										bidscancleList(selectedPageNumber);
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
	}

	private void setAdaptorifnull(int groupPosition2)
	{
		switch (groupPosition2)
		{
			case 0:
				if (adapter == null)
				{
					loadData(groupPosition);
				}
				else
				{
					llayoutdate.setVisibility(View.GONE);
					lvMyBids.setAdapter(adapter);
				}
				break;
			case 1:
				if (adapter == null)
				{
					loadData(groupPosition);
				}
				else
				{
					llayoutdate.setVisibility(View.GONE);
					lvMyBids.setAdapter(adapter);
				}
				break;
			case 2:
				if (automatedBiddingAdapter == null)
				{
					loadData(groupPosition);
				}
				else
				{
					llayoutdate.setVisibility(View.GONE);
					lvMyBids.setAdapter(automatedBiddingAdapter);
				}
				break;
			case 3:
				if (adapter == null)
				{
					loadData(groupPosition);
				}
				else
				{
					lvMyBids.setAdapter(adapter);
				}
				break;
			case 4:
				if (adapter == null)
				{
					loadData(groupPosition);
				}
				else
				{
					lvMyBids.setAdapter(adapter);
				}
				break;
			case 5:
				if (bidsDetailsPrivateOffersAdapter == null)
				{
					loadData(groupPosition);
				}
				else
				{
					lvMyBids.setAdapter(bidsDetailsPrivateOffersAdapter);
				}
				break;
			case 6:
				if (adapter == null)
				{
					loadData(groupPosition);
				}
				else
				{
					lvMyBids.setAdapter(adapter);
				}
				break;
			case 7:
				if (adapter == null)
				{
					loadData(groupPosition);
				}
				else
				{
					lvMyBids.setAdapter(adapter);
				}
				break;

			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{

		ArrayList<VehicleDetails> list = null;
		if (groupPosition == 0)
			list = losingBidsList;
		else if (groupPosition == 1)
			list = winningBidsList;
		else if (groupPosition == 2)
			list = automatedBiddingList;
		else if (groupPosition == 3)
			list = wonList;
		else if (groupPosition == 4)
			list = lostList;
		else if (groupPosition == 5)
			list = privateOffersList;
		else if (groupPosition == 6)
			list = bidsWithdrawList;
		else if (groupPosition == 7)
			list = bidsCancelledList;

		if (groupPosition == 3)
		{
			WonFragment wonFragment = new WonFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("UsedVehicleStockID", list.get(position).getUsedVehicleStockID());
			bundle.putInt("OfferID", list.get(position).getOfferID());
			bundle.putDouble("Amount", list.get(position).getOfferAmt());
			wonFragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(this.getId(), wonFragment).addToBackStack("listFragment").commit();
		}
		else if (groupPosition == 4)
		{
			AvailableToTradeDetailsFragment detailsFragment = new AvailableToTradeDetailsFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("vehicleId", list.get(position).getUsedVehicleStockID());
			bundle.putInt("year", list.get(position).getYear());
			bundle.putString("friendlyName", list.get(position).getFriendlyName());
			bundle.putString("type", list.get(position).getType());
			bundle.putString("colour", list.get(position).getColour());
			bundle.putString("location", list.get(position).getLocation());
			bundle.putDouble("price", list.get(position).getPrice());
			bundle.putString("stockCode", list.get(position).getStockCode());
			bundle.putDouble("offerAmount", list.get(position).getOfferAmt());
			bundle.putInt("mileage", list.get(position).getMileage());
			bundle.putString("mileageType", "Km");
			bundle.putInt("offerId", list.get(position).getOfferID());
			bundle.putInt("offerStatus", list.get(position).getOfferStatus());
			bundle.putString("source", list.get(position).getSource());
			bundle.putString("from", "mybids");
			detailsFragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(this.getId(), detailsFragment).addToBackStack("listFragment").commit();
		}
		else
		{

			Fragment f = new BuyDetailFragment();
			Bundle args = new Bundle();
			args.putInt("selectedVehicleid", list.get(position).getUsedVehicleStockID());
			f.setArguments(args);
			// selectedVehiclePosition=position;
			getFragmentManager().beginTransaction().replace(this.getId(), f).addToBackStack("listFragment").commit();

		}
	}

	private void loadData(int position)
	{
		selectedPageNumber = 0;
		switch (position)
		{
			case 0:
				llayoutdate.setVisibility(View.GONE);
				losingBidsList = new ArrayList<VehicleDetails>();
				losingBidsList(selectedPageNumber);
				break;

			case 1:
				llayoutdate.setVisibility(View.GONE);
				winningBidsList = new ArrayList<VehicleDetails>();
				winningBidsList(selectedPageNumber);
				break;

			case 2:
				llayoutdate.setVisibility(View.GONE);
				automatedBiddingList = new ArrayList<VehicleDetails>();
				automatedbiddingList(selectedPageNumber);
				break;

			case 3:
				wonList = new ArrayList<VehicleDetails>();
				wonList(selectedPageNumber);
				break;
			case 4:
				lostList = new ArrayList<VehicleDetails>();
				lostList(selectedPageNumber);
				break;
			case 5:
				privateOffersList = new ArrayList<VehicleDetails>();
				privateOfferList(selectedPageNumber);
				break;
			case 6:
				bidsWithdrawList = new ArrayList<VehicleDetails>();
				bidswithdrawnList(selectedPageNumber);
				break;
			case 7:
				bidsCancelledList = new ArrayList<VehicleDetails>();
				bidscancleList(selectedPageNumber);
				break;
			default:
				break;
		}
	}

	private void losingBidsList(final int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			// parameterList.add(new
			// Parameter("DateTimeFrom",edFromDate.getText().toString(),String.class));
			// parameterList.add(new
			// Parameter("DateTimeTo",edToDate.getText().toString(),String.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("LosingBidsPaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/LosingBidsPaged");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("Result");
						VehicleDetails vehicle;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								vehicle = new VehicleDetails();

								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Department", ""));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
								vehicle.setHighest(Float.parseFloat(vehicleObj.getPropertySafelyAsString("HeighestBid", "")));
								vehicle.setLocation(vehicleObj.getPropertySafelyAsString("Location", ""));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", ""));
								vehicle.setOfferAmt(Float.parseFloat(vehicleObj.getPropertySafelyAsString("MyHeighestBid", "")));
								vehicle.setSource(vehicleObj.getPropertySafelyAsString("Source", ""));

								losingBidsList.add(vehicle);

							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) inner.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								Helper.Log("Total", total);
							}
						}

						if (adapter == null || pageNumber == 0)
						{
							adapter = new BidsDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, losingBidsList, "MyBids", groupPosition);
							lvMyBids.setAdapter(adapter);
						}
						else
						{
							adapter.notifyDataSetChanged();
						}

						if (losingBidsList.isEmpty())
						{
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
							{
								@Override
								public void onButtonClicked(int type)
								{
									getActivity().finish();
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

	private void winningBidsList(int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			// parameterList.add(new
			// Parameter("DateTimeFrom",edFromDate.getText().toString(),String.class));
			// parameterList.add(new
			// Parameter("DateTimeTo",edToDate.getText().toString(),String.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("WinningBidsPaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/WinningBidsPaged");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("Result");
						VehicleDetails vehicle;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								vehicle = new VehicleDetails();

								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Department", ""));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
								vehicle.setTradeprice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "")));
								vehicle.setRetailPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("RetailPrice", "")));
								vehicle.setHighest(Float.parseFloat(vehicleObj.getPropertySafelyAsString("HeighestBid", "")));
								vehicle.setLocation(vehicleObj.getPropertySafelyAsString("Location", ""));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", ""));
								vehicle.setOfferAmt(Float.parseFloat(vehicleObj.getPropertySafelyAsString("MyHeighestBid", "")));
								vehicle.setSource(vehicleObj.getPropertySafelyAsString("Source", ""));

								winningBidsList.add(vehicle);
							}

							else
							{
								SoapPrimitive p = (SoapPrimitive) inner.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								Helper.Log("Total", total);
							}
						}
						if (adapter == null)
						{
							adapter = new BidsDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, winningBidsList, "MyBids", groupPosition);
							lvMyBids.setAdapter(adapter);
						}
						else
						{
							adapter.notifyDataSetChanged();
						}
						
						if (winningBidsList.isEmpty())
						{
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
							{
								@Override
								public void onButtonClicked(int type)
								{
									getActivity().finish();
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

	private void wonList(final int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("DateTimeFrom", edFromDate.getText().toString(), String.class));
			parameterList.add(new Parameter("DateTimeTo", edToDate.getText().toString(), String.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("BidsWonPaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/BidsWonPaged");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("Results");
						VehicleDetails vehicle;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								vehicle = new VehicleDetails();

								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Type", ""));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setClientName(vehicleObj.getPropertySafelyAsString("ClientName", ""));
								vehicle.setClientPnno(vehicleObj.getPropertySafelyAsString("ClientPhone", ""));
								vehicle.setsolddate(vehicleObj.getPropertySafelyAsString("SoldDate", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
								vehicle.setLocation(vehicleObj.getPropertySafelyAsString("Location", ""));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", ""));
								vehicle.setOfferAmt(Float.parseFloat(vehicleObj.getPropertySafelyAsString("OfferAmount", "")));
								vehicle.setSource(vehicleObj.getPropertySafelyAsString("Source", ""));
								vehicle.setOfferID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("OfferID", "")));

								wonList.add(vehicle);

							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) inner.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								Helper.Log("Total", total);
							}
						}

						if (adapter == null || pageNumber == 0)
						{
							adapter = new BidsDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, wonList, "MyBids", groupPosition);
							lvMyBids.setAdapter(adapter);
						}
						else
						{
							adapter.notifyDataSetChanged();
						}
						
						if (wonList.isEmpty())
						{
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
							{
								@Override
								public void onButtonClicked(int type)
								{
//									getActivity().finish();
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

	private void lostList(final int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("DateTimeFrom", edFromDate.getText().toString(), String.class));
			parameterList.add(new Parameter("DateTimeTo", edToDate.getText().toString(), String.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("BidsLostPaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/BidsLostPaged");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("Results");
						VehicleDetails vehicle;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								vehicle = new VehicleDetails();

								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Type", ""));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setClientName(vehicleObj.getPropertySafelyAsString("ClientName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
								vehicle.setLocation(vehicleObj.getPropertySafelyAsString("Location", ""));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", ""));
								vehicle.setOfferAmt(Float.parseFloat(vehicleObj.getPropertySafelyAsString("OfferAmount", "")));
								vehicle.setSource(vehicleObj.getPropertySafelyAsString("Source", ""));
								vehicle.setHighest(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Highest", "")));
								vehicle.setOfferID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("OfferID", "")));

								lostList.add(vehicle);

							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) inner.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								Helper.Log("Total", total);
							}
						}
						if (adapter == null || pageNumber == 0)
						{
							adapter = new BidsDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, lostList, "MyBids", groupPosition);
							lvMyBids.setAdapter(adapter);
						}
						else
						{
							adapter.notifyDataSetChanged();
						}
						
						if (lostList.isEmpty())
						{
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
							{
								@Override
								public void onButtonClicked(int type)
								{
									// getActivity().finish();
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

	private void bidswithdrawnList(int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("DateTimeFrom", edFromDate.getText().toString(), String.class));
			parameterList.add(new Parameter("DateTimeTo", edToDate.getText().toString(), String.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("WithdrawnBidsPaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/WithdrawnBidsPaged");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("Result");
						// SoapObject inner = (SoapObject)
						// innerone.getPropertySafely("Bids");
						VehicleDetails vehicle;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								vehicle = new VehicleDetails();

								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Department", ""));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", ""));
								vehicle.setHighest(Float.parseFloat(vehicleObj.getPropertySafelyAsString("HeighestBid", "")));
								vehicle.setSource(vehicleObj.getPropertySafelyAsString("Source", ""));
								bidsWithdrawList.add(vehicle);

							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) inner.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								Helper.Log("Total", total);
							}
						}
						if (adapter == null)
						{
							adapter = new BidsDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, bidsWithdrawList, "MyBids", groupPosition);
							lvMyBids.setAdapter(adapter);
						}
						else
						{
							adapter.notifyDataSetChanged();
						}

						if (bidsWithdrawList.isEmpty())
						{
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
							{
								@Override
								public void onButtonClicked(int type)
								{
								//	getActivity().finish();
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

	private void bidscancleList(int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("DateTimeFrom", edFromDate.getText().toString(), String.class));
			parameterList.add(new Parameter("DateTimeTo", edToDate.getText().toString(), String.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("CancelledBidsPaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/CancelledBidsPaged");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("Result");
						// SoapObject inner = (SoapObject)
						// innerone.getPropertySafely("Bids");
						VehicleDetails vehicle;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								vehicle = new VehicleDetails();

								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Department", ""));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", ""));
								vehicle.setHighest(Float.parseFloat(vehicleObj.getPropertySafelyAsString("HeighestBid", "")));
								vehicle.setSource(vehicleObj.getPropertySafelyAsString("Source", ""));
								bidsCancelledList.add(vehicle);

							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) inner.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								Helper.Log("Total", total);
							}
						}
						if (adapter == null)
						{
							adapter = new BidsDetailsAdapter(getActivity(), R.layout.list_item_available_to_trade, bidsCancelledList, "MyBids", groupPosition);
							lvMyBids.setAdapter(adapter);
						}
						else
						{
							adapter.notifyDataSetChanged();
						}
						
						if (bidsCancelledList.isEmpty())
						{
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
							{
								@Override
								public void onButtonClicked(int type)
								{
									//getActivity().finish();
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

	private void automatedbiddingList(int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("DateTimeFrom", "", String.class));
			parameterList.add(new Parameter("DateTimeTo", "", String.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("AutoBidsPaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/AutoBidsPaged");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("Result");
						VehicleDetails vehicle;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								vehicle = new VehicleDetails();

								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Department", ""));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", ""));
								vehicle.setHighest(Float.parseFloat(vehicleObj.getPropertySafelyAsString("HeighestBid", "")));
								vehicle.setCap(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Cap", "")));
								vehicle.setIncrement(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Increment", "")));
								vehicle.setClientName(vehicleObj.getPropertySafelyAsString("ClientName", ""));
								vehicle.setSource(vehicleObj.getPropertySafelyAsString("Source", ""));
								automatedBiddingList.add(vehicle);

							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) inner.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								Helper.Log("Total", total);
							}
						}
						if (automatedBiddingAdapter == null)
						{
							automatedBiddingAdapter = new BidsDetailsAutomatedBiddingAdapter(getActivity(), R.layout.list_item_available_to_trade, automatedBiddingList, "MyBids");
							lvMyBids.setAdapter(automatedBiddingAdapter);
						}
						else
						{
							automatedBiddingAdapter.notifyDataSetChanged();
						}

						if (automatedBiddingList.isEmpty())
						{
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
							{
								@Override
								public void onButtonClicked(int type)
								{
									getActivity().finish();
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

	private void privateOfferList(int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("DateTimeFrom", edFromDate.getText().toString(), String.class));
			parameterList.add(new Parameter("DateTimeTo", edToDate.getText().toString(), String.class));
			parameterList.add(new Parameter("Page", pageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("PrivateBidsPaged");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/PrivateBidsPaged");
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
						SoapObject inner = (SoapObject) outer.getPropertySafely("Result");
						VehicleDetails vehicle;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								vehicle = new VehicleDetails();

								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Department", ""));
								vehicle.setClientName(vehicleObj.getPropertySafelyAsString("ClientName", ""));
								vehicle.setUserName(vehicleObj.getPropertySafelyAsString("UserName", ""));
								vehicle.setDate(vehicleObj.getPropertySafelyAsString("Date", ""));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", ""));
								vehicle.setHighest(Float.parseFloat(vehicleObj.getPropertySafelyAsString("HeighestBid", "")));
								vehicle.setTradeprice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "")));
								vehicle.setAmount(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Amount", "")));
								vehicle.setCap(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Cap", "")));
								vehicle.setIncrement(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Increment", "")));
								vehicle.setSource(vehicleObj.getPropertySafelyAsString("Source", ""));
								privateOffersList.add(vehicle);

							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) inner.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								Helper.Log("Total", total);
							}
						}
						if (bidsDetailsPrivateOffersAdapter == null)
						{
							bidsDetailsPrivateOffersAdapter = new BidsDetailsPrivateOffersAdapter(getActivity(), R.layout.list_item_available_to_trade, privateOffersList, "MyBids");
							lvMyBids.setAdapter(bidsDetailsPrivateOffersAdapter);
						}
						else
						{
							bidsDetailsPrivateOffersAdapter.notifyDataSetChanged();
						}
						
						if (privateOffersList.isEmpty())
						{
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
							{
								@Override
								public void onButtonClicked(int type)
								{
									//getActivity().finish();
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

	@Override
	public void onResume()
	{
		super.onResume();
		settitles(getArguments().getInt("Position"));
	}

	private void settitles(int postion)
	{
		switch (postion)
		{
			case 0:
				showActionBar("Losing Bids");
				//getActivity().getActionBar().setSubtitle(null);
				break;
			case 1:
				showActionBar("Winning Bids");
				//getActivity().getActionBar().setSubtitle(null);
				break;
			case 2:
				showActionBar("Automated Bidding");
				//getActivity().getActionBar().setSubtitle(null);
				break;
			case 3:
				showActionBar("Won");
				//getActivity().getActionBar().setSubtitle(null);

				break;
			case 4:
				showActionBar("Lost");
				//getActivity().getActionBar().setSubtitle(null);
				break;
			case 5:
				showActionBar("Private Offers");
				//getActivity().getActionBar().setSubtitle(null);
				break;
			case 6:
				showActionBar("Bids Withdrawn");
				//getActivity().getActionBar().setSubtitle(null);
				break;
			case 7:
				showActionBar("Bids Cancelled");
				//getActivity().getActionBar().setSubtitle(null);
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
			case R.id.edStartDate:
				DatePickerFragment startDate = new DatePickerFragment();
				startDate.setDateListener(new DateListener()
				{
					@Override
					public void onDateSet(int year, int monthOfYear, int dayOfMonth)
					{
						edFromDate.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
					}
				});
				startDate.show(getActivity().getFragmentManager(), "datePicker");
				break;

			case R.id.edEndDate:
				DatePickerFragment endDate = new DatePickerFragment();
				endDate.setDateListener(new DateListener()
				{

					@Override
					public void onDateSet(int year, int monthOfYear, int dayOfMonth)
					{
						edToDate.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
					}
				});
				endDate.show(getActivity().getFragmentManager(), "datePicker");
				break;

			case R.id.bSearch:
				loadData(groupPosition);
				break;
			default:
				break;
		}
	}
}
