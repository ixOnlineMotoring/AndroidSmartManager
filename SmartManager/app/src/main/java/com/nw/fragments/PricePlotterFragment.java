package com.nw.fragments;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.ksoap2.serialization.SoapObject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.PricePlotter;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.HelperHttp;

public class PricePlotterFragment extends BaseFragement
{
	VehicleDetails vehicleDetails;
	ArrayList<PricePlotter> pricePlottersArrayList;

	LinearLayout chartContainer;
	private GraphicalView mChart;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_price_plotter, container, false);
		setHasOptionsMenu(true);
		if (getArguments() != null)
		{
			vehicleDetails = getArguments().getParcelable("vehicleDetails");
		}
		initialise(view);
		GetLoadNewPricesByID();
		return view;
	}

	private void initialise(View view)
	{
		if (pricePlottersArrayList == null)
			pricePlottersArrayList = new ArrayList<>();

		// Getting a reference to LinearLayout of the MainActivity Layout
		chartContainer = (LinearLayout) view.findViewById(R.id.chart_container);
	}

	private void GetLoadNewPricesByID()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("variantID", vehicleDetails.getVariantID(), Integer.class));
			parameterList.add(new Parameter("year", vehicleDetails.getYear(), Integer.class));
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("LoadNewPricesByID");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadNewPricesByID");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			showProgressDialog();
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{
				// Network callback
				@Override
				public void onTaskComplete(Object result)
				{
					hideProgressDialog();
					SoapObject outer = (SoapObject) result;
					SoapObject inner = (SoapObject) outer.getPropertySafely("NewPricePlotter");
					for (int i = 0; i < inner.getPropertyCount(); i++)
					{
						SoapObject innerObject = (SoapObject) inner.getProperty(i);
						PricePlotter pricePlotter = new PricePlotter(Float.parseFloat(innerObject.getPropertyAsString(("value"))), innerObject.getPropertyAsString(("date")));
						pricePlottersArrayList.add(pricePlotter);
					}
					if (pricePlottersArrayList.size() > 0)
					{
						openChart();
					}
					else
					{
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
						{

							@Override
							public void onButtonClicked(int type)
							{
								getActivity().getFragmentManager().popBackStack();
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

	private void openChart()
	{

		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", getActivity().getResources().getConfiguration().locale);

		Date[] dt = new Date[pricePlottersArrayList.size()];
		for (int i = 0; i < pricePlottersArrayList.size(); i++)
		{
			Date date;
			try
			{
				date = formatter.parse(pricePlottersArrayList.get(i).getDate());
				Calendar c = new GregorianCalendar();
				c.setTime(date);
				dt[i] = c.getTime();
			} catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Creating TimeSeries for Visits
		TimeSeries visitsSeries = new TimeSeries("");

		// Adding data to Visits and Views Series
		for (int i = 0; i < pricePlottersArrayList.size(); i++)
		{
			visitsSeries.add(dt[i], pricePlottersArrayList.get(i).getValue());
		}

		// Creating a dataset to hold each series
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		// Adding Visits Series to the dataset
		dataset.addSeries(visitsSeries);

		// Creating XYSeriesRenderer to customize visitsSeries
		XYSeriesRenderer visitsRenderer = new XYSeriesRenderer();
		visitsRenderer.setColor(getActivity().getResources().getColor(R.color.colorLightBlue));
		visitsRenderer.setPointStyle(PointStyle.CIRCLE);
		
		visitsRenderer.setChartValuesTextSize(15);
		visitsRenderer.setFillPoints(true);
		visitsRenderer.setLineWidth(3);
		visitsRenderer.setDisplayChartValues(true);

		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

		/*
		 * multiRenderer.setChartTitle("Visits vs Views Chart");
		 * multiRenderer.setXTitle("Days"); multiRenderer.setYTitle("Count");
		 */
		multiRenderer.setZoomButtonsVisible(true);

		// Adding visitsRenderer and viewsRenderer to multipleRenderer
		// Note: The order of adding dataseries to dataset and renderers to
		// multipleRenderer
		// should be same
		multiRenderer.addSeriesRenderer(visitsRenderer);

		// Creating a Time Chart
		mChart = (GraphicalView) ChartFactory.getTimeChartView(getActivity(), dataset, multiRenderer, "dd MMM yyyy");

		multiRenderer.setClickEnabled(true);
		multiRenderer.setPointSize(18);
		multiRenderer.setLegendTextSize(10); // below visits view
		multiRenderer.setSelectableBuffer(15);
		multiRenderer.setLabelsTextSize(15);
		multiRenderer.setAxisTitleTextSize(15);
		multiRenderer.setChartTitleTextSize(15);
		multiRenderer.setShowGridX(true);

		// Setting a click event listener for the graph
		mChart.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Format formatter = new SimpleDateFormat("dd MMM yyyy");

				SeriesSelection seriesSelection = mChart.getCurrentSeriesAndPoint();

				if (seriesSelection != null)
				{
					int seriesIndex = seriesSelection.getSeriesIndex();
					/*
					 * String selectedSeries = "Visits"; if (seriesIndex == 0)
					 * selectedSeries = "Visits"; else selectedSeries = "Views";
					 */

					// Getting the clicked Date ( x value )
					long clickedDateSeconds = (long) seriesSelection.getXValue();
					Date clickedDate = new Date(clickedDateSeconds);
					String strDate = formatter.format(clickedDate);

					// Getting the y value
					int amount = (int) seriesSelection.getValue();

					// Displaying Toast Message
					Toast.makeText(getActivity(), strDate + " : " + amount, Toast.LENGTH_SHORT).show();
				}
			}

		});

		// Adding the Line Chart to the LinearLayout
		chartContainer.addView(mChart);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("New Price Plotter");
	}

}
