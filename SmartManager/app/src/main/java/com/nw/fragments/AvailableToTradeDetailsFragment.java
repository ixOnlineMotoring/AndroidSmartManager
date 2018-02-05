package com.nw.fragments;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.nw.adapters.HorizontalListViewAdapter;
import com.nw.model.DataInObject;
import com.nw.model.MyImage;
import com.nw.model.Parameter;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class AvailableToTradeDetailsFragment extends BaseFragement {

	TextView tvVehicleName, tvMileage, tvColour, tvLocation, tvPrice;
	TextView tvType, tvStockCode, tvOfferAmt, tvOfferDate, tvOfferStart, tvOfferEnd, tvOfferId, tvOfferStatus, tvSource;
	NetworkImageView ivVehicleImage;
	HorizontalListView hlvVehicleImages;
	ArrayList<MyImage> list;
	ImageLoader imageLoader;
	HorizontalListViewAdapter adapter;
	LinearLayout llSellSection, llMyBidsSection;
	ArrayList<MyImage> tempList;
	FrameLayout frVehicleImage;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_available_to_trade, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		putValues();
		if(list.isEmpty())
			loadVehicle();
		else{
			ivVehicleImage.setImageUrl(list.get(0).getFull(), imageLoader);
			hlvVehicleImages.setAdapter(adapter);
		}
		return view;
	}
	
	//Function to initialise views
	private void initialise(View view){
		tvVehicleName= (TextView) view.findViewById(R.id.tvVehicleNameYear);
		tvType= (TextView) view.findViewById(R.id.tvType);
		tvColour= (TextView) view.findViewById(R.id.tvVehicleColour);
		tvLocation= (TextView) view.findViewById(R.id.tvLocation);
		tvPrice= (TextView) view.findViewById(R.id.tvAskingPrice);
		frVehicleImage= (FrameLayout) view.findViewById(R.id.frVehicleImage);
		tvMileage= (TextView) view.findViewById(R.id.tvVehicleMileage);
		tvStockCode= (TextView) view.findViewById(R.id.tvStockCode);
		tvOfferAmt= (TextView) view.findViewById(R.id.tvOfferAmount);
		tvOfferDate= (TextView) view.findViewById(R.id.tvOfferDate);
		tvOfferStart= (TextView) view.findViewById(R.id.tvOfferStart);
		tvOfferEnd= (TextView) view.findViewById(R.id.tvOfferEnd);
		tvOfferId= (TextView) view.findViewById(R.id.tvOfferId);
		tvOfferStatus= (TextView) view.findViewById(R.id.tvOfferStatus);
		tvSource= (TextView) view.findViewById(R.id.tvSource);
		llSellSection= (LinearLayout) view.findViewById(R.id.llSellSection);
		llMyBidsSection= (LinearLayout) view.findViewById(R.id.llMyBidsSection);
		
		ivVehicleImage= (NetworkImageView) view.findViewById(R.id.ivVehicleImage);
		ivVehicleImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				navigateToLargeImage(0, list);
			}
		});
		hlvVehicleImages= (HorizontalListView) view.findViewById(R.id.hlvCarImages);
		hlvVehicleImages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				navigateToLargeImage(position+1, list);
			}
		});
		imageLoader = VolleySingleton.getInstance().getImageLoader();
	}
	
	/*Function to put values in views*/
	private void putValues(){
		try{
			tvVehicleName.setText(Html.fromHtml(
					"<font color=#ffffff>"+ getArguments().getInt("year")
					+ "</font> <font color="+ getResources().getColor(R.color.dark_blue)+ ">"
					+ getArguments().getString("friendlyName") + "</font>"));
			
			tvType.setText(getArguments().getString("type"));
			tvColour.setText(getArguments().getString("colour"));
			if(!getArguments().getString("location").trim().equalsIgnoreCase(""))
				tvLocation.setText(getArguments().getString("location"));
			else
				tvLocation.setText(("Location?"));
			tvPrice.setText(Helper.formatPrice(new BigDecimal(getArguments().getDouble("price"))+""));
			tvMileage.setText(Helper.getFormattedDistance(getArguments().getInt("mileage")+"")+" "+getArguments().getString("mileageType"));
			tvStockCode.setText(getArguments().getString("stockCode"));
			tvOfferAmt.setText(Helper.formatPrice(new BigDecimal(getArguments().getDouble("offerAmount"))+""));
			if(getArguments().getString("from").equals("sell")){
				llSellSection.setVisibility(View.VISIBLE);
				llMyBidsSection.setVisibility(View.GONE);
				tvOfferDate.setText(getArguments().getString("offerDate"));
				tvOfferStart.setText(getArguments().getString("offerStart"));
				tvOfferEnd.setText(getArguments().getString("offerEnd"));
			}else if(getArguments().getString("from").equals("mybids")){
				llSellSection.setVisibility(View.GONE);
				llMyBidsSection.setVisibility(View.VISIBLE);
				tvOfferId.setText(getArguments().getInt("offerId")+"");
				tvOfferStatus.setText(getArguments().getInt("offerStatus")+"");
				tvSource.setText(getArguments().getString("source"));
			}
			ivVehicleImage.setDefaultImageResId(R.drawable.noimage);
			if(list==null)
				list= new ArrayList<MyImage>();
			if(tempList==null)
				tempList= new ArrayList<MyImage>();
			adapter=new HorizontalListViewAdapter(getActivity(),tempList);
			hlvVehicleImages.setAdapter(adapter);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// function loads vehicle 
		private void loadVehicle(){
			if(HelperHttp.isNetworkAvailable(getActivity())){
				//Add parameters to request in arraylist
					ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
					parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
					parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
					parameterList.add(new Parameter("Vehicle", getArguments().getInt("vehicleId"),Integer.class));
					
					//create web service inputs
					DataInObject inObj= new DataInObject();
					inObj.setMethodname("LoadVehicle");
					inObj.setNamespace(Constants.TRADER_NAMESPACE);
					inObj.setSoapAction(Constants.TRADER_NAMESPACE+"/ITradeService/LoadVehicle");
					inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
					inObj.setParameterList(parameterList);
					
					//Network call
					new WebServiceTask(getActivity(),inObj, true,new TaskListener() {
					
					@Override
					public void onTaskComplete(Object result) {
							try{
								Helper.Log("soap Response", result.toString());
								SoapObject obj= (SoapObject) result;
				 				SoapObject vehicleObj= (SoapObject) obj.getPropertySafely("Vehicle", "default");
		 	 					SoapObject imageObj= (SoapObject) vehicleObj.getPropertySafely("Images");
		 	 					MyImage image;
		 	 					for(int j=0;j<imageObj.getPropertyCount();j++){
		 	 						
		 	 						if(imageObj.getProperty(j) instanceof SoapObject){
		 	 							image= new MyImage();
		 	 							SoapObject object=(SoapObject) imageObj.getProperty(j);
		 	 							image.setFull(object.getPropertySafelyAsString("Full", ""));
		 	 							image.setThumb(object.getPropertySafelyAsString("Thumb", ""));
		 	 							list.add(j,image);
		 	 						}
		 	 					}
		 	 					tempList.addAll(list);
		 	 					if(!list.isEmpty())
		 	 						tempList.remove(0);
		 	 					if(list.isEmpty()){
		 	 						hlvVehicleImages.setVisibility(View.GONE);
		 	 						frVehicleImage.setVisibility(View.GONE);
		 	 					}else{
		 	 						adapter.notifyDataSetChanged();
		 	 						ivVehicleImage.setImageUrl(list.get(0).getFull(), imageLoader);
		 	 						if(TextUtils.isEmpty(list.get(0).getFull()))
		 	 							frVehicleImage.setVisibility(View.GONE);
		 	 						Helper.Log("link", list.get(0).getFull(), false);
		 	 					}
		 	 					if(tempList.isEmpty()){
		 	 						hlvVehicleImages.setVisibility(View.GONE);
		 	 					}
						}catch(Exception e){
							e.printStackTrace();
							hlvVehicleImages.setVisibility(View.GONE);
							frVehicleImage.setVisibility(View.GONE);
						}
					}
			}).execute();
			}else{
				CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
			}
		}
	
		/*
		 * Funtion to display images in gallery view 
		 * Parameters- position clicked, imageList, title name*/
		private void navigateToLargeImage(int position, ArrayList<MyImage> list){
			Fragment f= new ImageDetailFragment();
			 Bundle args = new Bundle();
			 args.putParcelableArrayList("imagelist", list);
			 args.putInt("index", position);
			 args.putString("from", getArguments().getString("from"));	
			 args.putString("vehicleName", "Sell: Active Bids Received");
			 f.setArguments(args);
			 
			getFragmentManager().beginTransaction().replace(this.getId(),f).addToBackStack("active_bids").commit();
		}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().getFragmentManager().popBackStack();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		showActionBar(getArguments().getString("friendlyName"));
		//getActivity().getActionBar().setSubtitle(null);
	}
}
