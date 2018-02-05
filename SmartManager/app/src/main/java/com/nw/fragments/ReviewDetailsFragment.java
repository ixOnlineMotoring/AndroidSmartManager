package com.nw.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.nw.adapters.HorizontalListViewAdapter;
import com.nw.model.DataInObject;
import com.nw.model.MyImage;
import com.nw.model.Parameter;
import com.nw.model.Review;
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

public class ReviewDetailsFragment extends BaseFragement
{
	Review reviewDetail;
	ArrayList<MyImage> tempList = new ArrayList<MyImage>();
	HorizontalListViewAdapter horizontalListViewAdapter;
	HorizontalListView hlvCarImages;
	TextView  tvVehicleName,tvTitle;
	TextView tvRoadTest ,tvReview;
	WebView wvReview;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_review_details, container,	false);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if(getArguments()!=null)
		{
			reviewDetail = getArguments().getParcelable("reviewDetail");
		}
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		showActionBar("Review");
	}
	
	private void initialise(View view)
	{
		hlvCarImages=(HorizontalListView) view.findViewById(R.id.hlvCarImages);
		tvVehicleName=(TextView) view.findViewById(R.id.tvVehicleName);
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvRoadTest=(TextView) view.findViewById(R.id.tvRoadTest);
	//	tvReview=(TextView) view.findViewById(R.id.tvVehicleReview);
		wvReview=(WebView) view.findViewById(R.id.wvVehicleReview);
		wvReview.setWebChromeClient(new WebChromeClient());
		wvReview.setInitialScale(5);
		wvReview.getSettings().setAllowFileAccess(true);
		wvReview.setWebChromeClient(new WebChromeClient());
		wvReview.getSettings().setPluginState(WebSettings.PluginState.ON);
		wvReview.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
		wvReview.setWebViewClient(new WebViewClient());
		wvReview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		wvReview.getSettings().setBuiltInZoomControls(true);
		wvReview.getSettings().setSupportZoom(true);
		wvReview.getSettings().setSupportMultipleWindows(true);
		wvReview.getSettings().setJavaScriptEnabled(true);
		wvReview.getSettings().setLoadWithOverviewMode(true);
		wvReview.getSettings().setUseWideViewPort(true);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		Log.e("dimen for webviews", width + "-" + height);
		getReviewById(reviewDetail.getID());
	}

	private void putValues()
	{
		tvTitle.setText(reviewDetail.getTitle());
		tvRoadTest.setText(reviewDetail.getType()+": "+ reviewDetail.getDate()+" | "+reviewDetail.getAuthor()+" | "+reviewDetail.getSource());
	//	tvReview.setText(Html.fromHtml(reviewDetail.getBody()));
		wvReview.loadDataWithBaseURL(null, "<!DOCTYPE html><html><meta charset=\"UTF-8\" /><style>p, span, div, table, li{color:#fff !important;}</style><body style='background-color:black;'>"+reviewDetail.getBody()+"</body> </html>", "text/html", "UTF-8", null);
		tvVehicleName.setText(Html.fromHtml("<font color=#ffffff>" + reviewDetail.getYear() + "</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue) + ">"
					+ reviewDetail.getVehicle_name() + "</font>"));
		if (reviewDetail.getImages()!=null)
		{
			if (reviewDetail.getImages().size()>3)
			{
				tempList.clear();
				for (int i = 0; i < 3; i++)
				{
					tempList.add(reviewDetail.getImages().get(i));
				} 
				hlvCarImages.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3)
					{
						navigateToLargeImage(position);
					}
				});
			}else {
				tempList.addAll(reviewDetail.getImages());
			}
			horizontalListViewAdapter = new HorizontalListViewAdapter(getActivity(), tempList);
			hlvCarImages.setVisibility(View.VISIBLE);
			hlvCarImages.setAdapter(horizontalListViewAdapter);
		}else {
			hlvCarImages.setVisibility(View.GONE);
		}
	}
	
	/*
	 * Function to get Special from special Id
	 * Parameter - special id*/
	private void getReviewById(int reviewID) 
	{
		showProgressDialog();
		if(HelperHttp.isNetworkAvailable(getActivity())){
			
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash",DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("reviewID", reviewID, Integer.class));
	
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("LoadReviewById");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+ "IStockService/LoadReviewById");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
	
			// Network call
			new WebServiceTask(getActivity(), inObj, false, new TaskListener() {
	
				@Override
				public void onTaskComplete(Object result) {
	
					try {
						Helper.Log("soap Response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Reviews");
						SoapObject detailObj = (SoapObject) inner.getPropertySafely("article");
						if (!TextUtils.isEmpty(detailObj.getPropertySafelyAsString("body", "Body?")))
						{
							reviewDetail.setBody(detailObj.getPropertySafelyAsString("body", "Body?").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&"));
						}
						putValues();
						hideProgressDialog();
					} catch (Exception e) {
						e.printStackTrace();
						putValues();
						hideProgressDialog();
					}
				}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
	protected void navigateToLargeImage(int position)
	{
		GallaryFragment imageDetailFragment = new GallaryFragment();
		Bundle args = new Bundle();
		args.putParcelableArrayList("imagelist", reviewDetail.getImages());
		args.putInt("index", position);
		args.putString("vehicleName",tvVehicleName.getText().toString());
		args.putString("from", "image");
		imageDetailFragment.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.Container, imageDetailFragment).addToBackStack(null).commit();
	}
	
}