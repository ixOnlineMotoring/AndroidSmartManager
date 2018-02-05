package com.nw.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nw.adapters.VehicleAlertAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.ListClickListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.Vehicle;
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

import java.util.ArrayList;

public class VehicleAlertFragment extends BaseFragement
{
	ListView lvVehicleAlerts;
	LinearLayout llayoutdate;
	VehicleAlertAdapter vehicleAlertAdapter;
	int list_type = 0, MISSING_PRICE = 0, ACTIVATE_RETAIL = 1, MISSING_INFO = 2, selectedPageNumber = 0, total_no_of_records = 0;
	ArrayList<Vehicle> vehicles;
	boolean fromLoadMore = false;
	CustomTextView tvInvalidNote;
	ListDetailsFragment listDetailsFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_listview, container, false);
		setHasOptionsMenu(true);
		vehicles = new ArrayList<Vehicle>();
		if (getArguments() != null)
		{
			list_type = getArguments().getInt("Position");
		}
		initialise(view);
		getVehicleAlertDataFor(list_type);
		return view;
	}

	private void initialise(View view)
	{
		llayoutdate = (LinearLayout) view.findViewById(R.id.llayoutdate);
		llayoutdate.setVisibility(View.GONE);
		lvVehicleAlerts = (ListView) view.findViewById(R.id.listview);
		tvInvalidNote = (CustomTextView) view.findViewById(R.id.tvInvalidNote);
		tvInvalidNote.setVisibility(View.VISIBLE);
		lvVehicleAlerts.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				hideKeyboard();
				int threshold = 1;
				int count = lvVehicleAlerts.getCount();
				if (scrollState == SCROLL_STATE_IDLE)
				{
					if (lvVehicleAlerts.getLastVisiblePosition() >= count - threshold)
					{
						if (!vehicles.isEmpty())
						{
							if (vehicles.size() < total_no_of_records)
							{
								fromLoadMore = true;
								selectedPageNumber++;
								getVehicleAlertDataFor(list_type);
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
		vehicleAlertAdapter = new VehicleAlertAdapter(getActivity(), vehicles, list_type, new ListClickListener()
		{
			@Override
			public void onEditClick(int position)
			{
				listDetailsFragment = new ListDetailsFragment();
				Bundle args = new Bundle();
				args.putString("vehicleName", vehicles.get(position).getFriendlyName());
				args.putInt("stockID", vehicles.get(position).getID());
				listDetailsFragment.setArguments(args);
				getFragmentManager().beginTransaction().replace(R.id.Container, listDetailsFragment).addToBackStack(null).commit();
			}

			@Override
			public void onActivateClick(int postion, boolean isTrade, int TradePrice)
			{
				ActivateVehicle(postion, isTrade, TradePrice);

			}
		});

		lvVehicleAlerts.setAdapter(vehicleAlertAdapter);
	}

	protected void ActivateVehicle(int postion, boolean isTrade, int TradePrice)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("UsedvehicleStockID", vehicles.get(postion).getID(), Integer.class));
			parameterList.add(new Parameter("IsTrade", isTrade, Boolean.class));
			parameterList.add(new Parameter("TradePrice", TradePrice, Float.class));
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setMethodname("ActivateVehicle");
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ActivateVehicle");
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{
				@Override
				public void onTaskComplete(Object result)
				{
					try
					{
						Helper.Log("ActivateVehicle response", result.toString());
						hideProgressDialog();
						SoapObject obj = (SoapObject) result;
						SoapObject response = (SoapObject) obj.getPropertySafely("Result", "");
				//		final String status = response.getPrimitivePropertySafelyAsString("Status");
						CustomDialogManager.showOkDialog(getActivity(), response.getPrimitivePropertySafelyAsString("Message"), new DialogListener()
						{
							@Override
							public void onButtonClicked(int type)
							{
								/*
								 * if (status.equalsIgnoreCase("success")); {
								 * vehicles.clear();
								 * getVehicleAlertDataFor(list_type); } if
								 * (status.equalsIgnoreCase("fail")) {
								 */
								getActivity().finish();
								// }

							}
						});
					}

					catch (Exception e)
					{
						e.printStackTrace();
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_occured_try_later), new DialogListener()
						{
							@Override
							public void onButtonClicked(int type)
							{
								getActivity().finish();
							}
						});
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
		if (getArguments() != null)
		{
			showActionBar(getArguments().getString("title"));
		}
		else
		{
			showActionBar("Vehicle Alert");
		}
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

	private void getVehicleAlertDataFor(int type)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("Page", selectedPageNumber, Integer.class));
			parameterList.add(new Parameter("PageSize", 10, Integer.class));
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			if (MISSING_PRICE == type)
			{
				inObj.setMethodname("GetTradeVehiclesMissingPrice");
				inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/GetTradeVehiclesMissingPrice");
			}
			else if (ACTIVATE_RETAIL == type)
			{
				inObj.setMethodname("GetRetailVehiclesNotActivated");
				inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/GetRetailVehiclesNotActivated");
			}
			else
			{
				inObj.setMethodname("GetVehiclesMissingInfo");
				inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/GetVehiclesMissingInfo");
			}
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{
				@Override
				public void onTaskComplete(Object result)
				{
					try
					{
						Helper.Log("soap response", result.toString());
						hideProgressDialog();
						SoapObject obj = (SoapObject) result;
						SoapObject response = (SoapObject) obj.getPropertySafely("Result", "");
						SoapObject inner = (SoapObject) response.getPropertySafely("TradeVehicles", "");
						total_no_of_records = Integer.parseInt(response.getPropertySafelyAsString("Total"));
						// Audit audit;
						Vehicle vehicle;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							vehicle = new Vehicle();
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);

								vehicle.setID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "0")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "0")));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setDepartment(vehicleObj.getPropertySafelyAsString("Department", ""));
								vehicle.setRegNumber(vehicleObj.getPropertySafelyAsString("Registration", ""));
								vehicle.setComments(vehicleObj.getPropertySafelyAsString("Comments", ""));
								vehicle.setExtras(vehicleObj.getPropertySafelyAsString("Extras", ""));
								vehicle.setRetailPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("RetailPrice", "0.00")));
								vehicle.setTradePrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "0.00")));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "0")));
								vehicle.setStockNumber(vehicleObj.getPropertySafelyAsString("StockCode", ""));
								vehicle.setTrade(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("IsTrade")));
								vehicle.setNumOfPhotos(Integer.parseInt(vehicleObj.getPropertySafelyAsString("ImageCount", "0")));
								vehicle.setNumOfVideos(Integer.parseInt(vehicleObj.getPropertySafelyAsString("VideoCount", "0")));
								vehicles.add(vehicle);
							}
						}
						if(inner.getPropertyCount()==0)
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
						vehicleAlertAdapter.notifyDataSetChanged();
						if (fromLoadMore)
							fromLoadMore = false;
					} catch (Exception e)
					{
						e.printStackTrace();
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
						{
							@Override
							public void onButtonClicked(int type)
							{
								getActivity().finish();
							}
						});
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
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
		if (listDetailsFragment != null)
			listDetailsFragment.onActivityResult(requestCode, resultCode, data);

	}
}
