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

import com.nw.adapters.TraderVehiclesAdapter;
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

public class TradeVehiclesFragment extends BaseFragement
{
	private ListView listview;

	TraderVehiclesAdapter adapter;
	LinearLayout llayoutnote;
	ArrayList<VehicleDetails> arraylistvehiclelist;
	int total_no_of_records = 0, selectedPageNumber = 0;
	ActiveTradeFragment detailsFragment;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_trade_vehicles, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		listview = (ListView) view.findViewById(R.id.listview);

		if (adapter == null)
		{
			arraylistvehiclelist = new ArrayList<VehicleDetails>();
			tradeVehiclesList(selectedPageNumber);
		}
		else
		{
			listview.setAdapter(adapter);
		}

		listview.setOnScrollListener(new OnScrollListener()
		{

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				int threshold = 1;
				int count = listview.getCount();
				if (scrollState == SCROLL_STATE_IDLE)
				{
					if (listview.getLastVisiblePosition() >= count - threshold)
					{
						selectedPageNumber++;

						if (!arraylistvehiclelist.isEmpty())
						{
							if (arraylistvehiclelist.size() < total_no_of_records)
							{
								tradeVehiclesList(selectedPageNumber);
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

		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				detailsFragment = new ActiveTradeFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("vehicleId", arraylistvehiclelist.get(position).getID());
				bundle.putDouble("RetailPrice", arraylistvehiclelist.get(position).getRetailPrice());
				bundle.putDouble("BidPrice", arraylistvehiclelist.get(position).getOfferAmt());
				bundle.putDouble("TradePrice", arraylistvehiclelist.get(position).getPrice());
				bundle.putString("Days", ""+arraylistvehiclelist.get(position).getAge());
				bundle.putString("Department", arraylistvehiclelist.get(position).getType());
				bundle.putString("Screentype", "trade");
				detailsFragment.setArguments(bundle);
				getFragmentManager().beginTransaction().replace(TradeVehiclesFragment.this.getId(), detailsFragment).addToBackStack("listFragment").commit();
			}
		});
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Trade Vehicles");
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
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
		if (detailsFragment != null)
			detailsFragment.onActivityResult(requestCode, resultCode, data);

	}

	private void tradeVehiclesList(int pageNumber)
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
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Results");
						VehicleDetails vehicle;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								vehicle = new VehicleDetails();

								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "")));
								vehicle.setID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", 0)));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
								vehicle.setAge(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Age", "")));
								vehicle.setLocation(vehicleObj.getPropertySafelyAsString("Location", ""));
								vehicle.setStockCode(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setType(vehicleObj.getPropertySafelyAsString("Type", ""));
								vehicle.setPrice(Double.parseDouble(vehicleObj.getPropertySafelyAsString("Price", "")));
								vehicle.setRetailPrice(Double.parseDouble(vehicleObj.getPropertySafelyAsString("RetailPrice", "")));
								vehicle.setRegistration(vehicleObj.getPropertySafelyAsString("Registration", ""));
								vehicle.setOfferAmt(Float.parseFloat(vehicleObj.getPropertySafelyAsString("OfferAmount", "0.0")));
								arraylistvehiclelist.add(vehicle);
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
							adapter = new TraderVehiclesAdapter(getActivity(), R.layout.list_item_tradevehicles, arraylistvehiclelist);
							listview.setAdapter(adapter);
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
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
}
