package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nw.adapters.SalesHistoryAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.text.NumberFormat;
import java.util.ArrayList;

public class SalesHistoryFragment extends BaseFragement
{
	StaticListView lv_average_sales;
	ArrayList<SmartObject> histories;
	SalesHistoryAdapter salesHistoryAdapter;
	VehicleDetails vehicleDetails;
	LinearLayout llSalesHistory;
	TextView tvAvgSalesLine,tvPageTitle,tvModelLine, tvStockPM, tvStock30days, tvStock45days, tvStock60days, tvTotalSales,tvStockInFocus;

	int int_total_count = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_sales_history, container, false);
		setHasOptionsMenu(true);
		if (getArguments() != null)
		{
			vehicleDetails = getArguments().getParcelable("vehicleDetails");
		}
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		llSalesHistory = (LinearLayout) view.findViewById(R.id.llSalesHistory);
		lv_average_sales = (StaticListView) view.findViewById(R.id.lv_average_sales);
		tvPageTitle= (TextView) view.findViewById(R.id.tvPageTitle);
		tvPageTitle.setText(vehicleDetails.getModelName()+" sales during the last 6 months-");
		tvAvgSalesLine= (TextView) view.findViewById(R.id.tvAvgSalesLine);
		tvAvgSalesLine.setText("Avg. "+vehicleDetails.getModelName()+" sales pm");
		tvModelLine= (TextView) view.findViewById(R.id.tvModelLine);
		tvModelLine.setText(vehicleDetails.getModelName() +"'s in stock now");
		tvStockPM = (TextView) view.findViewById(R.id.tvStockPM);
		tvStock30days = (TextView) view.findViewById(R.id.tvStock30days);
		tvStock45days = (TextView) view.findViewById(R.id.tvStock45days);
		tvStock60days = (TextView) view.findViewById(R.id.tvStock60days);
		tvTotalSales = (TextView) view.findViewById(R.id.tvTotalSales);
		tvStockInFocus= (TextView) view.findViewById(R.id.tvStockInFocus);
		getSalesHistoryData(); 
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Sales History");
	}

	private void getSalesHistoryData()
	{
		histories = new ArrayList<SmartObject>();
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("variantID", vehicleDetails.getVariantID(), Integer.class));
			parameterList.add(new Parameter("year", vehicleDetails.getYear(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("LoadSalesHistoryByID");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadSalesHistoryByID");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			showProgressDialog();
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{
				@Override
				public void onTaskComplete(Object result)
				{
					histories.clear();
					try
					{
						SoapObject outer = (SoapObject) result;
						SoapObject resultObj = (SoapObject) outer.getPropertySafely("Result");
						SoapObject inner = (SoapObject) resultObj.getPropertySafely("SalesHistory");
						SmartObject smartObject = null;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject availabilityObj = (SoapObject) inner.getProperty(i);
							smartObject = new SmartObject();
							smartObject.setName(availabilityObj.getPropertySafelyAsString("VariantName", "0"));
							smartObject.setID(availabilityObj.getPropertySafelyAsString("SalesCount"));
							int_total_count = int_total_count + Integer.parseInt(availabilityObj.getPropertySafelyAsString("SalesCount"));
							histories.add(smartObject); 
						}
						SoapObject stockholdingObj = (SoapObject) resultObj.getPropertySafely("StockHolding");
						tvStockPM.setText("" + NumberFormat.getInstance().parse(stockholdingObj.getPropertySafelyAsString("AverageSalesPerMonth")).intValue());
						tvStock30days.setText("" + NumberFormat.getInstance().parse(stockholdingObj.getPropertySafelyAsString("AverageStockHolding30Days")).intValue());
						tvStock45days.setText("" + NumberFormat.getInstance().parse(stockholdingObj.getPropertySafelyAsString("AverageStockHolding45Days")).intValue());
						tvStock60days.setText("" + NumberFormat.getInstance().parse(stockholdingObj.getPropertySafelyAsString("AverageStockHolding60Days")).intValue());
						tvTotalSales.setText("" + int_total_count);
						tvStockInFocus.setText("" + NumberFormat.getInstance().parse(stockholdingObj.getPropertySafelyAsString("InStock")).intValue());
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						if (histories.size()==0)
						{
							llSalesHistory.setVisibility(View.GONE);
						}else {
							llSalesHistory.setVisibility(View.VISIBLE);
							salesHistoryAdapter = new SalesHistoryAdapter(getActivity(), R.layout.list_item_sales_history, histories);
							lv_average_sales.setAdapter(salesHistoryAdapter);
						}
						hideProgressDialog();
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
