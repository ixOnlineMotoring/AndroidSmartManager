package com.nw.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import android.widget.ListView;

import com.nw.adapters.BuyerSummaryAdapter;
import com.nw.adapters.SaleListSalesAdapter;
import com.nw.interfaces.DateListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomTextView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SalesFragment extends BaseFragement implements OnClickListener
{
	EditText edToDate, edFromDate;
	ListView lvBuyersSummary, lvListSales;
	Button btnbuySummary, btnListSales;

	SaleListSalesAdapter saleListSalesAdapter;
	BuyerSummaryAdapter buyerSummaryAdapter;
	ArrayList<VehicleDetails> vehicleDetails;
	ArrayList<VehicleDetails> bayersSummaryList;
	CustomTextView tvListNote, tvListclickNote;
	int total_no_of_records_bayersSummary = 0, selectedPageNumber_bayersSummary = 0, total_no_of_records_ListSales = 0, selectedPageNumber_ListSales = 0;
	boolean selectionFlag;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_sales, container, false);
		setHasOptionsMenu(true);

		initialise(view);

		return view;
	}

	@SuppressLint("SimpleDateFormat")
	private void initialise(View view)
	{
		// EditText
		edFromDate = (EditText) view.findViewById(R.id.edStartDate);
		edToDate = (EditText) view.findViewById(R.id.edEndDate);
		tvListNote = (CustomTextView) view.findViewById(R.id.tvListNote);
		tvListclickNote = (CustomTextView) view.findViewById(R.id.tvListclickNote);

		// Button
		btnbuySummary = (Button) view.findViewById(R.id.btnbuySummary);
		btnListSales = (Button) view.findViewById(R.id.btnListSales);

		// ListView
		lvBuyersSummary = (ListView) view.findViewById(R.id.lvBuyersSummary);
		lvListSales = (ListView) view.findViewById(R.id.lvListSales);
		lvListSales.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				SoldFragment soldFragment = new SoldFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("UsedVehicleStockID", vehicleDetails.get(arg2).getUsedVehicleStockID());
				bundle.putInt("OfferID", vehicleDetails.get(arg2).getID());
				bundle.putString("Date", vehicleDetails.get(arg2).getDate());
				bundle.putString("UserName", vehicleDetails.get(arg2).getUserName());
				bundle.putString("ClientName", vehicleDetails.get(arg2).getClientName());
				bundle.putDouble("Amount", vehicleDetails.get(arg2).getAmount());
				soldFragment.setArguments(bundle);
				getFragmentManager().beginTransaction().replace(SalesFragment.this.getId(), soldFragment).addToBackStack("listFragment").commit();
			}
		});

		lvBuyersSummary.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				ListSalesFragment listSalesFragment = new ListSalesFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("ClientID", bayersSummaryList.get(arg2).getClientID());
				listSalesFragment.setArguments(bundle);
				getFragmentManager().beginTransaction().replace(SalesFragment.this.getId(), listSalesFragment).addToBackStack("listFragment").commit();
			}
		});

		edFromDate.setOnClickListener(this);
		edToDate.setOnClickListener(this);

		btnbuySummary.setOnClickListener(this);
		btnListSales.setOnClickListener(this);

		Calendar calendar = Calendar.getInstance();
		String today = new SimpleDateFormat("dd MMM yyyy").format(calendar.getTime());
		calendar.add(Calendar.MONTH, -1);
		String pastDate = new SimpleDateFormat("dd MMM yyyy").format(calendar.getTime());
		edFromDate.setText(pastDate);
		edToDate.setText(today);
		tvListNote.setText("Sales List for the period "+edFromDate.getText().toString().trim()+" to "+edToDate.getText().toString().trim());

		selectionFlag = true;
		vehicleDetails = new ArrayList<VehicleDetails>();
		bayersSummaryList = new ArrayList<VehicleDetails>();
		
		selectedPageNumber_bayersSummary = 0;
		selectedPageNumber_ListSales = 0;
		listsalesList(selectedPageNumber_ListSales);

		// Add Pagination to List
		lvBuyersSummary.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				int threshold = 1;
				int count = lvBuyersSummary.getCount();
				if (scrollState == SCROLL_STATE_IDLE)
				{
					if (lvBuyersSummary.getLastVisiblePosition() >= count - threshold)
					{
						selectedPageNumber_bayersSummary++;
						if (!bayersSummaryList.isEmpty())
						{
							if (bayersSummaryList.size() < total_no_of_records_bayersSummary)
							{
								tradeSalesSummaryList(selectedPageNumber_bayersSummary);
							}
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
			}
		});
		
		lvListSales.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				int threshold = 1;
				int count = lvListSales.getCount();
				if (scrollState == SCROLL_STATE_IDLE)
				{
					if (lvListSales.getLastVisiblePosition() >= count - threshold)
					{
						selectedPageNumber_ListSales++;
						if (!vehicleDetails.isEmpty())
						{
							System.out.println("sscroll 1");
							if (vehicleDetails.size() < total_no_of_records_ListSales)
							{
								System.out.println("sscroll 2");
								listsalesList(selectedPageNumber_ListSales);
							}
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

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Sales");
		//getActivity().getActionBar().setSubtitle(null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				hideKeyboard();
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
			case R.id.btnbuySummary:
				btnListSales.setBackgroundResource(R.drawable.rectangle_dark_gray);
				btnbuySummary.setBackgroundResource(R.drawable.rectangle_dark_blue);

				lvBuyersSummary.setVisibility(View.VISIBLE);
				lvListSales.setVisibility(View.GONE);
				tvListNote.setText("Buyer Summary for the period "+edFromDate.getText().toString().trim()+" to "+edToDate.getText().toString().trim());
				tvListclickNote.setText("Click on sales count to view a list of sales-");

				selectionFlag = false;
				if (bayersSummaryList.isEmpty())
					tradeSalesSummaryList(selectedPageNumber_bayersSummary);

				break;
			case R.id.btnListSales:
				btnListSales.setBackgroundResource(R.drawable.rectangle_dark_blue);
				btnbuySummary.setBackgroundResource(R.drawable.rectangle_dark_gray);

				lvBuyersSummary.setVisibility(View.GONE);
				lvListSales.setVisibility(View.VISIBLE);
				tvListNote.setText("Sales List for the period "+edFromDate.getText().toString().trim()+" to "+edToDate.getText().toString().trim());
				tvListclickNote.setText("Click on vehicle to view details-");

				selectionFlag = true;
				if (vehicleDetails.isEmpty())
					listsalesList(selectedPageNumber_ListSales);

				break;
			case R.id.edStartDate:
				DatePickerFragment startDate = new DatePickerFragment();
				startDate.setDateListener(new DateListener()
				{
					@Override
					public void onDateSet(int year, int monthOfYear, int dayOfMonth)
					{
						edFromDate.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
						if (selectionFlag == true)
						{
							vehicleDetails = new ArrayList<VehicleDetails>();
							selectedPageNumber_ListSales = 0;
							tvListNote.setText("Sales List for the period "+edFromDate.getText().toString().trim()+" to "+edToDate.getText().toString().trim());
							listsalesList(selectedPageNumber_ListSales);
						}
						else
						{
							bayersSummaryList = new ArrayList<VehicleDetails>();
							selectedPageNumber_bayersSummary = 0;
							tvListNote.setText("Buyer Summary for the period "+edFromDate.getText().toString().trim()+" to "+edToDate.getText().toString().trim());
							tradeSalesSummaryList(selectedPageNumber_bayersSummary);
						}
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
						if (selectionFlag == true)
						{
							vehicleDetails = new ArrayList<VehicleDetails>();
							selectedPageNumber_ListSales = 0;
							tvListNote.setText("Sales List for the period "+edFromDate.getText().toString().trim()+" to "+edToDate.getText().toString().trim());
							listsalesList(selectedPageNumber_ListSales);
						}
						else
						{
							bayersSummaryList = new ArrayList<VehicleDetails>();
							selectedPageNumber_bayersSummary = 0;
							tvListNote.setText("Buyer Summary for the period "+edFromDate.getText().toString().trim()+" to "+edToDate.getText().toString().trim());
							tradeSalesSummaryList(selectedPageNumber_bayersSummary);
						}
					}
				});
				endDate.show(getActivity().getFragmentManager(), "datePicker");
				break;

			default:
				break;
		}
	}

	private void tradeSalesSummaryList(final int pageNumber)
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
			inObj.setMethodname("TradeSalesSummary");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/TradeSalesSummary");
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

								vehicle.setClientID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("ClientID", "")));
								vehicle.setClientName(vehicleObj.getPropertySafelyAsString("ClientName", ""));
								vehicle.setSales(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Sales", "")));
								bayersSummaryList.add(vehicle);

							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) inner.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records_bayersSummary = Integer.parseInt(total);
								Helper.Log("Total", total);
							}
						}
//						if (buyerSummaryAdapter == null || pageNumber == 0)
//						{
							buyerSummaryAdapter = new BuyerSummaryAdapter(getActivity(), R.layout.list_item_available_to_trade, bayersSummaryList);
							lvBuyersSummary.setAdapter(buyerSummaryAdapter);
//						}
//						else
//						{
//							buyerSummaryAdapter.notifyDataSetChanged();
//						}
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

	private void listsalesList(final int pageNumber)
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
			inObj.setMethodname("ListTradeSales");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ListTradeSales");
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

								vehicle.setID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("ID", "")));
								vehicle.setUsedVehicleStockID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "")));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Department", ""));
								vehicle.setUserName(vehicleObj.getPropertySafelyAsString("UserName", ""));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", ""));
								vehicle.setHighest(Float.parseFloat(vehicleObj.getPropertySafelyAsString("HeighestBid", "")));
								vehicle.setAmount(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Amount", "")));
								vehicle.setCap(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Cap", "")));
								vehicle.setIncrement(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Increment", "")));
								vehicle.setTradeprice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "")));
								vehicle.setSource(vehicleObj.getPropertySafelyAsString("Source", ""));
								vehicle.setClientName(vehicleObj.getPropertySafelyAsString("ClientName", ""));
								vehicle.setDate(vehicleObj.getPropertySafelyAsString("Date", ""));
								vehicleDetails.add(vehicle);
							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) inner.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records_ListSales = Integer.parseInt(total);
								Helper.Log("Total", total);
							}
						}

						if (saleListSalesAdapter == null || pageNumber == 0)
						{
							saleListSalesAdapter = new SaleListSalesAdapter(getActivity(), R.layout.list_item_available_to_trade, vehicleDetails);
							lvListSales.setAdapter(saleListSalesAdapter);
						}
						else
						{
							saleListSalesAdapter.notifyDataSetChanged();
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
