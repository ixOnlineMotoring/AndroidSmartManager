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
import android.widget.EditText;
import android.widget.ListView;

import com.nw.adapters.SaleListSalesAdapter;
import com.nw.interfaces.DateListener;
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

public class ListSalesFragment extends BaseFragement implements OnClickListener
{
	ListView lvListSales;
	EditText edToDate, edFromDate;

	SaleListSalesAdapter saleListSalesAdapter;
	ArrayList<VehicleDetails> vehicleDetails;
	int total_no_of_records_bayersSummary = 0, selectedPageNumber_bayersSummary = 0, total_no_of_records_ListSales = 0, selectedPageNumber_ListSales = 0;
	int ClientID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_list_sales, container, false);
		setHasOptionsMenu(true);
		// context = getActivity();
		/*
		 * if (vehicleDetails == null) { vehicleDetails = new
		 * ArrayList<VehicleDetails>(); for (int i = 0; i < 4; i++) {
		 * vehicleDetails.add(new VehicleDetails()); } }
		 */
		initialise(view);

		return view;
	}

	@SuppressLint("SimpleDateFormat")
	private void initialise(View view)
	{
		ClientID = getArguments().getInt("ClientID");

		// EditText
		edFromDate = (EditText) view.findViewById(R.id.edStartDate);
		edToDate = (EditText) view.findViewById(R.id.edEndDate);

		// ListView
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
				getFragmentManager().beginTransaction().replace(ListSalesFragment.this.getId(), soldFragment).addToBackStack("listFragment").commit();
				
				/*
				 * SoldFragment soldFragment = new SoldFragment(); Bundle bundle
				 * = new Bundle(); bundle.putInt("UsedVehicleStockID",
				 * vehicleDetails.get(arg2).getUsedVehicleStockID());
				 * soldFragment.setArguments(bundle);
				 * getFragmentManager().beginTransaction
				 * ().replace(ListSalesFragment.this.getId(),
				 * soldFragment).addToBackStack("listFragment").commit();
				 */
			}
		});

		edFromDate.setOnClickListener(this);
		edToDate.setOnClickListener(this);

		Calendar calendar = Calendar.getInstance();
		String today = new SimpleDateFormat("dd MMM yyyy").format(calendar.getTime());
		calendar.add(Calendar.MONTH, -1);
		String pastDate = new SimpleDateFormat("dd MMM yyyy").format(calendar.getTime());
		edFromDate.setText(pastDate);

		edToDate.setText(today);

		vehicleDetails = new ArrayList<VehicleDetails>();
		selectedPageNumber_ListSales = 0;
		listsalesList(selectedPageNumber_ListSales);

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
							if (vehicleDetails.size() < total_no_of_records_ListSales)
							{
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
				getActivity().getFragmentManager().popBackStack();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void listsalesList(int pageNumber)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", ClientID, Integer.class));
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
								vehicle.setCap(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Cap", "")));
								vehicle.setIncrement(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Increment", "")));
								vehicle.setTradeprice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "")));
								vehicle.setSource(vehicleObj.getPropertySafelyAsString("Source", ""));
								vehicle.setClientName(vehicleObj.getPropertySafelyAsString("ClientName", ""));
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

						if (saleListSalesAdapter == null || selectedPageNumber_ListSales == 0)
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
						Calendar cal = Calendar.getInstance();
						edFromDate.setText(Helper.showDateTime(Helper.createStringDateTime(year, monthOfYear, dayOfMonth, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))));
						vehicleDetails = new ArrayList<VehicleDetails>();
						selectedPageNumber_ListSales = 0;
						listsalesList(selectedPageNumber_ListSales);
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
						Calendar cal = Calendar.getInstance();
						edToDate.setText(Helper.showDateTime(Helper.createStringDateTime(year, monthOfYear, dayOfMonth, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))));
						vehicleDetails = new ArrayList<VehicleDetails>();
						selectedPageNumber_ListSales = 0;
						listsalesList(selectedPageNumber_ListSales);
					}
				});
				endDate.show(getActivity().getFragmentManager(), "datePicker");
				break;

			default:
				break;
		}

	}
}
