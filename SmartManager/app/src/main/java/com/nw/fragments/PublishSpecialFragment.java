package com.nw.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nw.adapters.PublishSpecialAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SpecialVehicle;
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

public class PublishSpecialFragment extends BaseFragement{

	ListView lvUnpublishSpecials;
	PublishSpecialAdapter adapter;
	ArrayList<SpecialVehicle> unpublishedList;
	boolean isLoadMore= false;
	CreateSpecialsFragment mFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_listview, container,false);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}
	
	private void initialise(View view){
		lvUnpublishSpecials= (ListView) view.findViewById(R.id.listview);
		LinearLayout llayoutdate = (LinearLayout) view.findViewById(R.id.llayoutdate);
		llayoutdate.setVisibility(View.GONE);
		if(unpublishedList==null){
			unpublishedList= new ArrayList<SpecialVehicle>();
			getPublishSpecialList();
		}else{
			if(adapter!=null)
				lvUnpublishSpecials.setAdapter(adapter);
		}
		lvUnpublishSpecials.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				hideKeyboard();
				int threshold = 1;
				int count = lvUnpublishSpecials.getCount();
				if (scrollState == SCROLL_STATE_IDLE) {
					if (lvUnpublishSpecials.getLastVisiblePosition() >= count- threshold) {
						if(!unpublishedList.isEmpty()){
							if(unpublishedList.size()<unpublishedList.get(0).getTotalCount()){
								getPublishSpecialList();
							} 
						}
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
		});
	}
	
	public void getPublishSpecialList()
	{
		if(HelperHttp.isNetworkAvailable(getActivity())){
		isLoadMore=true;
		showProgressDialog();
		
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		parameterList.add(new Parameter("userHash",DataManager.getInstance().user.getUserHash(), String.class));
		parameterList.add(new Parameter("coreClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
		parameterList.add(new Parameter("search","", String.class));
		parameterList.add(new Parameter("dateFrom","", String.class));
		parameterList.add(new Parameter("startAt",(unpublishedList.size()), String.class));
		parameterList.add(new Parameter("take",10, Integer.class));
		parameterList.add(new Parameter("sort","", String.class));
		
		// create web service inputs
		DataInObject inObj = new DataInObject();
		inObj.setMethodname("GetAllUnPublishedSpecialsXML");
		inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
		inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+ "ISpecialsService/GetAllUnPublishedSpecialsXML");
		inObj.setUrl(Constants.SPECIAL_WEBSERVICE_URL);
		inObj.setParameterList(parameterList);
		
		// Network call
		new WebServiceTask(getActivity(), inObj, false, new TaskListener() {
			@Override
			public void onTaskComplete(Object response)
			{
					try {            	
				          	if(!TextUtils.isEmpty(response.toString()))
				           	{
				            		Helper.Log("Response:", response.toString());
				            		ArrayList<SpecialVehicle> tempSpecials= new ArrayList<SpecialVehicle>();
									SoapObject outer = (SoapObject) response;
									SoapObject inner = (SoapObject) outer.getPropertySafely("AUTOSpecials");
									SpecialVehicle specialVehicle;
									for (int i = 0; i < inner.getPropertyCount(); i++) {
										SoapObject specialObject = (SoapObject) inner.getProperty(i);
										specialVehicle = new SpecialVehicle();
										specialVehicle.setSpecialID(Integer.parseInt(specialObject.getPropertySafelyAsString("SpecialID", "0")));
										specialVehicle.setSpecialTypeID(Integer.parseInt(specialObject.getPropertySafelyAsString("SpecialTypeID", "0")));
										specialVehicle.setType(specialObject.getPropertySafelyAsString("Type", "type?"));
										specialVehicle.setSpecialstart(specialObject.getPropertySafelyAsString("SpecialStart", "start?"));
										specialVehicle.setSpecialEnd(specialObject.getPropertySafelyAsString("SpecialEnd", "end?"));
										specialVehicle.setSpecialCreated(specialObject.getPropertySafelyAsString("SpecialCreated", "Created?"));
										specialVehicle.setCmUserId(Integer.parseInt(specialObject.getPropertySafelyAsString("COREMemberID", "0")));
										specialVehicle.setItemID(Integer.parseInt(specialObject.getPropertySafelyAsString("ItemID", "0")));
										specialVehicle.setSpecialPrice(Float.parseFloat(specialObject.getPropertySafelyAsString("SpecialPrice", "0")));
										specialVehicle.setNormalPrice(Float.parseFloat(specialObject.getPropertySafelyAsString("NormalPrice", "0")));
										specialVehicle.setSavePrice(Float.parseFloat(specialObject.getPropertySafelyAsString("SavePrice", "0")));
										specialVehicle.setDetails(specialObject.getPropertySafelyAsString("Details", "Details?"));
										specialVehicle.setSummary(specialObject.getPropertySafelyAsString("Title", "Title?"));
										specialVehicle.setImageID(Integer.parseInt(specialObject.getPropertySafelyAsString("ImageID", "0")));
										specialVehicle.setStockCode(specialObject.getPropertySafelyAsString("StockCode", "Stockcode?"));
										specialVehicle.setFriendlyName(specialObject.getPropertySafelyAsString("FriendlyName", "FriendlyName?"));
										specialVehicle.setUsedYear(Integer.parseInt(specialObject.getPropertySafelyAsString("UsedYear", "0")));
										specialVehicle.setColour(specialObject.getPropertySafelyAsString("Colour", "Colour?"));
										if (specialObject.getPropertySafelyAsString("Mileage", "0").equalsIgnoreCase("anyType{}")||
												specialObject.getPropertySafelyAsString("Mileage", "0").equalsIgnoreCase("null"))
										{
											specialVehicle.setMileage(0);
										} else
										{
											specialVehicle.setMileage(Integer.parseInt(specialObject.getPropertySafelyAsString("Mileage", "0")));
										}
										specialVehicle.setMileageType(specialObject.getPropertySafelyAsString("MileageType", "MileageType?"));
										specialVehicle.setMakeId(Integer.parseInt(specialObject.getPropertySafelyAsString("MakeID", "0")));
										specialVehicle.setModelID(Integer.parseInt(specialObject.getPropertySafelyAsString("ModelID", "0")));
										specialVehicle.setVariantID(Integer.parseInt(specialObject.getPropertySafelyAsString("VariantID", "0")));
										specialVehicle.setTotalCount(Integer.parseInt(specialObject.getPropertySafelyAsString("TotalCount", "0")));
										specialVehicle.setEndStatus(specialObject.getPropertySafelyAsString("IsExpired", "false"));
										specialVehicle.setCanPublish(Boolean.parseBoolean(specialObject.getPropertySafelyAsString("CanPublish", "false")));
										tempSpecials.add(specialVehicle);
									}
									isLoadMore = false;
				            		Helper.Log("TempListSize", tempSpecials.size()+"");
				            		hideProgressDialog();
				            		if (tempSpecials.size()!=0) 
				            			unpublishedList.addAll(tempSpecials);
				            		else{
				            			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found));
				            		}
				            		if(adapter==null){
					            		adapter= new PublishSpecialAdapter(getActivity(), R.layout.list_item_active_specials, unpublishedList, new TaskListener() {
											
											@Override
											public void onTaskComplete(Object result) {
												try{
													 int position= Integer.parseInt(result.toString());
													 mFragment= new CreateSpecialsFragment();
													 Bundle args = new Bundle();
													 args.putParcelable("vehicle", unpublishedList.get(position));
													 mFragment.setArguments(args);
													 getFragmentManager().beginTransaction().replace(PublishSpecialFragment.this.getId(), mFragment).addToBackStack("listFragment").commit();
												 }catch (Exception e) {
													e.printStackTrace();
												}
											}
										}, new TaskListener()
										{
											@Override
											public void onTaskComplete(Object result)
											{
												 int position= Integer.parseInt(result.toString());
												unpublishedList.remove(position);
												adapter.notifyDataSetChanged();
											}
										});
					            		lvUnpublishSpecials.setAdapter(adapter);
				            		}else{
				            			adapter.notifyDataSetChanged();
				            		}
				            	}
			            	} catch (Exception e) {
								hideProgressDialog();
								e.printStackTrace();
							}
						}
					}).execute();
 		}
		else
			HelperHttp.showNoInternetDialog(getActivity());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(mFragment!=null){
			mFragment.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		showActionBar(getString(R.string.list_publish_specials));
	}
}
