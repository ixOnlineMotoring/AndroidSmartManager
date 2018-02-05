package com.nw.fragments;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.nw.adapters.VehicleDetailsAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.Vehicle;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.SegmentedGroup;
import com.smartmanager.activity.VehicleActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Akshay.Belapurkar
 *
 */
public class StockListFragment extends BaseFragement implements OnItemClickListener, OnClickListener {

	ArrayList<Vehicle> vehicleList;
	PhotosAndExtrasDetailFragment photosAndExtrasDetailFragment;
	StockListDetailsFragment stockListDetailsFragment;
	ListDetailsFragment listDetailsFragment;
	int selectedPageNumber = 0, total_no_of_records = 1000,selectedSortType=0, selectedSearchPageNumber , selectedSortMethod,
								totalActive=0, totalInvalid=0, totalExcluded=0, totalAll=0;
	VehicleDetailsAdapter adapter;
	boolean isAtEnd = false;
	ListView lvVehicleDetails;
	LinearLayout llFilter;
	ImageView ivArrow;
	TextView tvFilter;
	EditText edKeyword, edSordBy;
	ValueAnimator mAnimator;
	ArrayList<SortItem> sortbylist = new ArrayList<SortItem>();
	SegmentedGroup segmentedGroup;
	int selectedStatusId=1;
	RadioButton rdRetail, rdExcluded, rdInvalid, rdAll; 
	boolean fromLoadMore=false;
	String keyword;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_list_vehicle,container,false);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setHasOptionsMenu(true);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (sortbylist.size() == 0)
			putSortListItems();
		initialise(view);
		if (vehicleList == null || VehicleActivity.isVehicleUpdated) 
		{
			// load list only if needed
			VehicleActivity.isVehicleUpdated=false;
			if (vehicleList == null) 
				vehicleList = new ArrayList<Vehicle>();
			else 
				vehicleList.clear();

			if (TextUtils.isEmpty(keyword)) 
			{
				selectedPageNumber = 0;
				getVehicleList(selectedPageNumber, selectedStatusId,getSortString(selectedSortType,
						selectedSortMethod));
			} 
			else 
			{
				selectedSearchPageNumber = 0;
				searchVehicleByKeyword(keyword,selectedSearchPageNumber, selectedStatusId,
						getSortString(selectedSortType,selectedSortMethod));
			}
		}
		hideKeyboard(view);
		return view;
	}

	private void initialise(View view) 
	{
		segmentedGroup= (SegmentedGroup) view.findViewById(R.id.segmentedGroup1);
		rdRetail =(RadioButton) view.findViewById(R.id.rdRetail);
		rdExcluded= (RadioButton) view.findViewById(R.id.rdExcluded);
		rdInvalid= (RadioButton) view.findViewById(R.id.rdInvalid);
		rdAll= (RadioButton) view.findViewById(R.id.rdAll); 
		lvVehicleDetails = (ListView) view.findViewById(R.id.lvVehicleDetails);
		ivArrow = (ImageView) view.findViewById(R.id.ivArrow);
		llFilter = (LinearLayout) view.findViewById(R.id.llFilter);
		tvFilter = (TextView) view.findViewById(R.id.tvFilter);
		edKeyword = (EditText) view.findViewById(R.id.edKeyword);
		edKeyword.setHintTextColor(Color.WHITE);
		edSordBy = (EditText) view.findViewById(R.id.edSortBy);
		tvFilter.setOnClickListener(this);
		ivArrow.setOnClickListener(this);
		tvFilter.performClick();
		edSordBy.setOnClickListener(this);
		// method used to call web service on search button click of device
		edKeyword.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					vehicleList.clear();
				
					keyword=edKeyword.getText().toString().trim();
					if (!TextUtils.isEmpty(keyword)) 
					{
						selectedSearchPageNumber = 0;
						searchVehicleByKeyword(keyword,selectedSearchPageNumber, selectedStatusId,
								getSortString(selectedSortType,selectedSortMethod));
					} 
					else 
					{
						selectedSearchPageNumber = 0;
						selectedPageNumber = 0;
						getVehicleList(selectedPageNumber, selectedStatusId,
								getSortString(selectedSortType,selectedSortMethod));
					}
					return true;
				} else
					return false;
			}
		});

		if (adapter != null) 
		{
			lvVehicleDetails.setAdapter(adapter);
			updateSegmentedControl();
		}
		lvVehicleDetails.setOnItemClickListener(this);
		lvVehicleDetails.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				hideKeyboard();
				int threshold = 1;
				int count = lvVehicleDetails.getCount();
				if (scrollState == SCROLL_STATE_IDLE) {
					if (lvVehicleDetails.getLastVisiblePosition() >= count- threshold) {
						if(!vehicleList.isEmpty()){
							if(vehicleList.size()<total_no_of_records){
								
									fromLoadMore=true;
									if (TextUtils.isEmpty(keyword))
									{
										selectedPageNumber++;
										getVehicleList(selectedPageNumber,selectedStatusId,getSortString(selectedSortType,selectedSortMethod));
									}						
									else 
									{
										selectedSearchPageNumber++;
										searchVehicleByKeyword(keyword,selectedSearchPageNumber, selectedStatusId,getSortString(selectedSortType,selectedSortMethod));
									}
								} 
							}
						}
					}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {}
		});
		
		if(getArguments().getString("fromFragment").equals("PhotosAndExtras"))
		{
			segmentedGroup.setVisibility(View.GONE);
		}
		else
		{
			if(selectedStatusId==0)
				selectedStatusId=1;
			segmentedGroup.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					segmentedGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						@Override
						public void onCheckedChanged(RadioGroup group, int checkedId) 
						{
							switch (checkedId) 
							{
							case R.id.rdRetail:
								selectedStatusId=1;
								break;
							case R.id.rdExcluded:
								selectedStatusId= 4;
								break;

							case R.id.rdInvalid:
								selectedStatusId=2;
								break;
								
							case R.id.rdAll:
								selectedStatusId=-1;
								break;					
							}
							
							if(checkedId!=-1)
							{
								if (TextUtils.isEmpty(keyword)) {
									selectedPageNumber = 0;
									getVehicleList(selectedPageNumber, selectedStatusId,getSortString(selectedSortType,selectedSortMethod));
								} else {
									selectedSearchPageNumber = 0;
									searchVehicleByKeyword(keyword,selectedSearchPageNumber, selectedStatusId,getSortString(selectedSortType,selectedSortMethod));
								}
							}
						}
					});
				}
			}, 1000);
		}
	}

	/*
	 * Function to update segmented control view*/
	private void updateSegmentedControl(){
		rdRetail.setText("Retail\n("+totalActive+")");
		rdExcluded.setText("Excluded\n("+totalExcluded+")");
		rdInvalid.setText("Invalid\n("+totalInvalid+")");
		rdAll.setText("All\n("+totalAll+")"); 
	}
	
	/*
	 * Function to reset segemented control */
	private void resetSegmentedControl(){
		//if(selectedStatusId==1)
			totalActive=0;
		//if(selectedStatusId==4)
			totalExcluded=0;
		//if(selectedStatusId==2)
			totalInvalid=0;
		//if(selectedStatusId==-1)
			totalAll=0;
		
		rdRetail.setText("Retail\n("+totalActive+")");
		rdExcluded.setText("Excluded\n("+totalExcluded+")");
		rdInvalid.setText("Invalid\n("+totalInvalid+")");
		rdAll.setText("All\n("+totalAll+")"); 
			
	}
	
	// for expanding animation
	private void expand() {
		llFilter.setVisibility(View.VISIBLE);
	}
	// collapsing animation
	private void collapse() {
		llFilter.setVisibility(View.GONE);
	}

	public static void expand(final View v, final boolean expand) {
	    try {
	        Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
	        m.setAccessible(true);
	        m.invoke(  v, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
	        		MeasureSpec.makeMeasureSpec(((View)v.getParent()).getMeasuredWidth(), MeasureSpec.AT_MOST)
	        );
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    final int initialHeight = v.getMeasuredHeight();
	    if (expand) {
	    	v.getLayoutParams().height = 0;
	    }
	    else {
	    	v.getLayoutParams().height = initialHeight;
	    }
	    v.setVisibility(View.VISIBLE);
	    Animation a = new Animation() {
	        @Override
	        protected void applyTransformation(float interpolatedTime, 	android.view.animation.Transformation t) {
	            int newHeight = 0;
	            if (expand) {
	            	newHeight = (int) (initialHeight * interpolatedTime);
	            } else {
	            	newHeight = (int) (initialHeight * (1 - interpolatedTime));
	            }
	            v.getLayoutParams().height = newHeight;	            
	            v.requestLayout();
	            
	            if (interpolatedTime == 1 && !expand)
	            	v.setVisibility(View.GONE);
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };
	    a.setDuration(700);
	    v.startAnimation(a);
	}
	
	int value;
	/*
	 * go to details on clicking on item pass full object to next fragment
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
	{
		stockListDetailsFragment= new StockListDetailsFragment();
		Bundle args = new Bundle();
		args.putParcelable("selectedVehicle", vehicleList.get(position));
		stockListDetailsFragment.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.Container, stockListDetailsFragment).addToBackStack("listFragment").commit();
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.tvFilter:
			case R.id.ivArrow:
	
				if (llFilter.getVisibility() == View.GONE) {
					tvFilter.setText(getString(R.string.hidefilter));
					ivArrow.setRotation(0);
					expand();
				} else {
					tvFilter.setText(getString(R.string.showfilter));
					ivArrow.setRotation(-90);
					collapse();
					hideKeyboard();
				}
				break;
	
			case R.id.edSortBy:
				hideKeyboard(v);
				showSortPopUp(v, sortbylist);
				break;
		}
	}
	/*
	  Add sort items in sort arraylist 
	 */
	private void putSortListItems()
	{
		sortbylist.add(new SortItem(getString(R.string.none), 0));
		sortbylist.add(new SortItem("Age", 0));
		sortbylist.add(new SortItem("Comments",0));
		sortbylist.add(new SortItem("Extras", 0));
		sortbylist.add(new SortItem("Mileage", 0));
		sortbylist.add(new SortItem("Photos", 0));
		sortbylist.add(new SortItem("Price", 0));
		sortbylist.add(new SortItem("Stock#", 0));
		sortbylist.add(new SortItem("Year", 0));
	}
	/*
	 * show dropdown to show sort attributes
	 */
	private void showSortPopUp(final View v, ArrayList<SortItem> list) 
	{
		final ArrayList<SortItem> mList = list;
		final View mView = v;
		final EditText ed = (EditText) v;
		/*
		 * 0- Descending order 1- Ascending order
		 */
		final ArrayAdapter<SortItem> sortadapter = new ArrayAdapter<SortItem>(getActivity(), R.layout.listitem_sort, R.id.tvText, mList) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_sort, null);

				}
				TextView tv = (TextView) convertView.findViewById(R.id.tvText);
				tv.setText(mList.get(position).sortattribute);
				ImageView iv = (ImageView) convertView.findViewById(R.id.ivSortMethod);
				if (position == 0) // for none do not show ascending/ descending
					iv.setVisibility(View.GONE);
				if (position != selectedSortType)
					iv.setVisibility(View.GONE);
				else {
					if (mList.get(position).sortmethod == 0) {
						iv.setRotation(180);
						//mList.get(position).sortmethod = 1;
					} else {
						iv.setRotation(0);
						//mList.get(position).sortmethod = 0;
					}
				}
				return convertView;
			}
		};
		
		Helper.showDropDown(v, sortadapter, new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				if (mView.getId() == R.id.edSortBy) {
					if (selectedSortType == position) {
						if (mList.get(position).sortmethod == 0) {
							mList.get(position).sortmethod = 1;
						} else {
							mList.get(position).sortmethod = 0;
						}
					} else {
						mList.get(position).sortmethod = 0;
						selectedSortType = position;
					}
					if (position != 0) { // show order in edittext except none
											// position
						if (mList.get(position).sortmethod == 0){
							ed.setText(mList.get(position).sortattribute+ " (Ascending)");
							selectedSortMethod=0;
						}else{
							ed.setText(mList.get(position).sortattribute+ " (Descending )");
							selectedSortMethod=1;
						}
					} else {
						ed.setText(getString(R.string.none));
						vehicleList.clear();
						adapter.notifyDataSetChanged();
					}
					sortadapter.notifyDataSetChanged();
					vehicleList.clear();
					if (TextUtils.isEmpty(edKeyword.getText().toString())) {
						selectedPageNumber = 0;
						getVehicleList(selectedPageNumber, selectedStatusId,getSortString(selectedSortType,selectedSortMethod));
					
					} else {
						selectedSearchPageNumber = 0;
						searchVehicleByKeyword(edKeyword.getText().toString(),selectedSearchPageNumber, selectedStatusId,getSortString(selectedSortType,selectedSortMethod));
					}
				}
			}
		});
		
		
	}

	/*
	 * Webservice integration to call vehicle list Parameter- pageNo for index
	 * of pages
	 */
	
	private synchronized void getVehicleList(int pageNo, int statusId, String sortAttr) 
	{
		hideKeyboard();
		if(HelperHttp.isNetworkAvailable(getActivity())){
		// Add parameters to request in arraylist
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		parameterList.add(new Parameter("userHash",DataManager.getInstance().user.getUserHash(), String.class));
		parameterList.add(new Parameter("clientID",DataManager.getInstance().user.getDefaultClient().getId(),Integer.class));
		parameterList.add(new Parameter("statusID", statusId, Integer.class));
		parameterList.add(new Parameter("pageSize", 10, Integer.class));
		parameterList.add(new Parameter("pageNumber", pageNo, Integer.class));
		if(!TextUtils.isEmpty(sortAttr))
			parameterList.add(new Parameter("sort", sortAttr, String.class));
		else{
			if(getArguments().getString("fromFragment").equals("PhotosAndExtras"))
				parameterList.add(new Parameter("sort", "photos|extras", String.class));
			else
				parameterList.add(new Parameter("sort", "friendlyname", String.class));
		
		}// create web service inputs
		DataInObject inObj = new DataInObject();
		inObj.setMethodname("ListVehiclesByStatusXML");
		inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
		inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListVehiclesByStatusXML");
		inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
		inObj.setParameterList(parameterList);

		// Network call
		showLoadingProgressDialog();
		new WebServiceTask(getActivity(), inObj, false, new TaskListener() {

			@Override
			public void onTaskComplete(Object result) 
			{
				if (result != null) {
					Helper.Log("soap response", result.toString());
					try {
						
						SoapObject obj = (SoapObject) result;
						SoapObject inner = (SoapObject) obj.getPropertySafely("stockList", "default");
						Vehicle vehicle;
						
						if(!fromLoadMore)
						{
							if(getArguments().getString("fromFragment").equals("List"))
								vehicleList.clear();
						}
						for (int i = 0; i < inner.getPropertyCount(); i++) 
						{
							vehicle = new Vehicle();							
							SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
							if(vehicleObj.hasProperty("usedVehicleStockID"))
							{
								vehicle.setInternalNote(vehicleObj.getPropertySafelyAsString("internalNote", ""));
								vehicle.setID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("usedVehicleStockID", "0")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("vehicleName", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("mileage","0")));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("colour", ""));
								vehicle.setDepartment(vehicleObj.getPropertySafelyAsString("department", ""));
								vehicle.setRetailPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("price", "0.00")));
								vehicle.setTradePrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("tradePrice","0.00")));
								vehicle.setExpires(vehicleObj.getPropertySafelyAsString("age", ""));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("usedYear","0")));
								vehicle.setStockNumber(vehicleObj.getPropertySafelyAsString("stockCode",""));
								vehicle.setRegNumber(vehicleObj.getPropertySafelyAsString("registration", ""));
								vehicle.setComments(vehicleObj.getPropertySafelyAsString("comments",""));
								vehicle.setExtras(vehicleObj.getPropertySafelyAsString("extras", ""));
								vehicle.setNumOfPhotos(Integer.parseInt(vehicleObj.getPropertySafelyAsString("photos", "0")));
								vehicle.setNumOfVideos(Integer.parseInt(vehicleObj.getPropertySafelyAsString("videos", "0")));
								vehicle.setTotal(Integer.parseInt(vehicleObj.getPropertySafelyAsString("total", "0")));
								totalActive= Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalActive", "0"));
								totalInvalid= Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalInvalid", "0"));
								totalExcluded= Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalExcluded", "0"));
								totalAll= Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalAll", "0"));
								
								vehicleList.add(vehicle);
							}
							else
							{
								SoapObject totalObject =  (SoapObject) inner.getPropertySafely("Totals", "default");
								totalActive= Integer.parseInt(totalObject.getPropertySafelyAsString("totalActive", "0"));
								totalInvalid= Integer.parseInt(totalObject.getPropertySafelyAsString("totalInvalid", "0"));
								totalExcluded= Integer.parseInt(totalObject.getPropertySafelyAsString("totalExcluded", "0"));
								totalAll= Integer.parseInt(totalObject.getPropertySafelyAsString("totalAll", "0"));
							}
						}
						Helper.Log("list size", vehicleList.size()+"");
						if(!vehicleList.isEmpty()){
							total_no_of_records= vehicleList.get(0).getTotal();
							Helper.Log("total", total_no_of_records+"");
						}
						if (inner.getPropertyCount() == 1){ 
							CustomDialogManager.showOkDialog(getActivity(),	getString(R.string.no_record_found));
							vehicleList.clear();
							updateSegmentedControl();
						}
						if (adapter == null) {
							adapter = new VehicleDetailsAdapter(getActivity(),vehicleList,0);
							lvVehicleDetails.setAdapter(adapter);
						} 
						else 
						{
							adapter.notifyDataSetChanged();
						}
						updateSegmentedControl();
						hideProgressDialog();
					} catch (Exception e) 
					{
						hideProgressDialog();
						e.printStackTrace();
					}
				} else {
					hideProgressDialog();
					CustomDialogManager.showOkDialogAutoCancel(getActivity(),getString(R.string.no_record_found));
					vehicleList.clear();
					resetSegmentedControl();
					if(adapter!=null)
						adapter.notifyDataSetChanged();
					isAtEnd = true;
				}
				
				if(fromLoadMore)
					fromLoadMore=false;
			}
		}).execute();
		}else{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}

	/*
	 * web service integration to search vehicle by keyword Parameter 1- Keyword
	 * to be searched 2- Page number
	 */
	private synchronized void searchVehicleByKeyword(String keyword, int pageNumber, int statusId, String sortAttr) {
		hideKeyboard();
		if(HelperHttp.isNetworkAvailable(getActivity())){
			
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		parameterList.add(new Parameter("userHash",DataManager.getInstance().user.getUserHash(), String.class));
		parameterList.add(new Parameter("clientID",DataManager.getInstance().user.getDefaultClient().getId(),Integer.class));
		parameterList.add(new Parameter("keyword",keyword, String.class));
		parameterList.add(new Parameter("pageSize", 10, Integer.class));
		parameterList.add(new Parameter("pageNumber", pageNumber, Integer.class));
		parameterList.add(new Parameter("Status", statusId, Integer.class));
		if(!TextUtils.isEmpty(sortAttr))
			parameterList.add(new Parameter("sort",sortAttr, String.class));
		else{
			if(getArguments().getString("fromFragment").equals("PhotosAndExtras"))
				parameterList.add(new Parameter("sort", "photos|extras", String.class));
			else
				parameterList.add(new Parameter("sort", "friendlyname", String.class));
		}
		// create web service inputs
		DataInObject inObj = new DataInObject();
		inObj.setMethodname("ListVehiclesByKeywordStatusXML");
		inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
		inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListVehiclesByKeywordStatusXML");
		inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
		inObj.setParameterList(parameterList);

		// Network call
		showLoadingProgressDialog();
		new WebServiceTask(getActivity(), inObj, false, new TaskListener() {

			@Override
			public void onTaskComplete(Object result) {
				if (result != null) {
					Helper.Log("soap response", result.toString());
					try {
						SoapObject obj = (SoapObject) result;
						SoapObject inner = (SoapObject) obj.getPropertySafely("stockList", "default");
						Vehicle vehicle;
						if(!fromLoadMore)
						{
							if(getArguments().getString("fromFragment").equals("List"))
								vehicleList.clear();
						}
						for (int i = 0; i < inner.getPropertyCount(); i++) 
						{
							vehicle = new Vehicle();
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								if(vehicleObj.hasProperty("usedVehicleStockID"))
								{
									vehicle.setInternalNote(vehicleObj.getPropertySafelyAsString("internalNote", ""));
									vehicle.setID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("usedVehicleStockID", "0")));
									vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("vehicleName", ""));
									vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("mileage","0")));
									vehicle.setColour(vehicleObj.getPropertySafelyAsString("colour", ""));
									vehicle.setDepartment(vehicleObj.getPropertySafelyAsString("department", ""));
									vehicle.setRetailPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("price", "0.00")));
									vehicle.setTradePrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("tradePrice","0.00")));
									vehicle.setExpires(vehicleObj.getPropertySafelyAsString("age", ""));
									vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("usedYear","0")));
									vehicle.setStockNumber(vehicleObj.getPropertySafelyAsString("stockCode",""));
									vehicle.setRegNumber(vehicleObj.getPropertySafelyAsString("registration", ""));
									vehicle.setComments(vehicleObj.getPropertySafelyAsString("comments",""));
									vehicle.setExtras(vehicleObj.getPropertySafelyAsString("extras", ""));
									vehicle.setNumOfPhotos(Integer.parseInt(vehicleObj.getPropertySafelyAsString("photos", "0")));
									vehicle.setNumOfVideos(Integer.parseInt(vehicleObj.getPropertySafelyAsString("photos", "0")));
									vehicle.setTotal(Integer.parseInt(vehicleObj.getPropertySafelyAsString("total", "0")));
									totalActive= Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalActive", "0"));
									totalInvalid= Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalInvalid", "0"));
									totalExcluded= Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalExcluded", "0"));
									totalAll= Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalAll", "0"));
									vehicleList.add(vehicle);
								}
								else
								{
									SoapObject totalObject =  (SoapObject) inner.getPropertySafely("Totals", "default");
									totalActive= Integer.parseInt(totalObject.getPropertySafelyAsString("totalActive", "0"));
									totalInvalid= Integer.parseInt(totalObject.getPropertySafelyAsString("totalInvalid", "0"));
									totalExcluded= Integer.parseInt(totalObject.getPropertySafelyAsString("totalExcluded", "0"));
									totalAll= Integer.parseInt(totalObject.getPropertySafelyAsString("totalAll", "0"));
								}								
						}
						Helper.Log("list size", vehicleList.size()+"");
						if(!vehicleList.isEmpty()){
							total_no_of_records= vehicleList.get(0).getTotal();
							Helper.Log("total", total_no_of_records+"");
						}
						
						if (inner.getPropertyCount() == 1) {
							CustomDialogManager.showOkDialog(getActivity(),getString(R.string.no_record_found));
							vehicleList.clear();
							resetSegmentedControl();
						}

						if (adapter == null) {
							adapter = new VehicleDetailsAdapter(getActivity(),vehicleList,0);
							lvVehicleDetails.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						updateSegmentedControl();
						hideProgressDialog();
					} catch (Exception e) {
						hideProgressDialog();
						e.printStackTrace();
					}
				} else {
					hideProgressDialog();
					CustomDialogManager.showOkDialogAutoCancel(getActivity(),getString(R.string.no_record_found));
					vehicleList.clear();
					resetSegmentedControl();
					if(adapter!=null)
					adapter.notifyDataSetChanged();
					isAtEnd = true;
				}
				
				if(fromLoadMore)
					fromLoadMore=false;
			}
		}).execute();
		}else{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(listDetailsFragment!=null)
			listDetailsFragment.onActivityResult(requestCode, resultCode, data);
		
		if(stockListDetailsFragment!=null)
			stockListDetailsFragment.onActivityResult(requestCode, resultCode, data);
		
		if(photosAndExtrasDetailFragment!=null )
			photosAndExtrasDetailFragment.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			hideKeyboard();
			getActivity().finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
			//getActivity().getActionBar().setTitle(getString(R.string.stock_list));
		showActionBar(getString(R.string.stock_list));
	}

	/*
	* Model to add sort item
	 */
	private class SortItem {
		String sortattribute;
		int sortmethod;

		public SortItem(String sortattribute, int sortmethod) {
			super();
			this.sortattribute = sortattribute;
			this.sortmethod = sortmethod;
		}
	}
	
	/*
	 * Function to get sort string to be passed to web service
	 * Parameters - sort attribute selected
	 * 			  - Ascending or descending sort*/
	private String getSortString(int position, int method){
		switch (position) {
		case 0:
			return "";
		case 1:
			if(method==1)
				return "age:desc";
			else 
				return "age:asc";
		case 2:
			if(method==1)
				return "comments:desc";
			else 
				return "comments:asc";
		case 3:
			if(method==1)
				return "extras:desc";
			else 
				return "extras:asc";
		case 4:
			if(method==1)
				return "mileage:desc";
			else 
				return "mileage:asc";
		case 5:
			if(method==1)
				return "photos:desc";
			else 
				return "photos:asc";
		case 6:
			if(method==1)
				return "price:desc";
			else 
				return "price:asc";
		case 7:
			if(method==1)
				return "stockcode:desc";
			else 
				return "stockcode:asc";

		case 8:
			if(method==1)
				return "usedyear:desc";
			else 
				return "usedyear:asc";
		default:
			return "";
		}
	}

	public void onBackPressed()
	{
		if (photosAndExtrasDetailFragment!= null)
		{
			photosAndExtrasDetailFragment.onBackPressed();
			photosAndExtrasDetailFragment=null;
		}
		else {
			getFragmentManager().popBackStack();
		}
	}

	/*private void displayShowcaseView(){
		if(!ShowcaseSessions.isSessionAvailable(getActivity(), ListVehicleFragment.class.getSimpleName())){
			ArrayList<TargetView> viewList= new ArrayList<TargetView>();
			viewList.add(new TargetView(lvVehicleDetails, ShowCaseType.SwipeUpDown, getString(R.string.scroll_up_and_down)));
			ShowcaseLayout showcaseView = new ShowcaseLayout(getActivity());
			showcaseView.setShowcaseView(viewList);
			
			((ViewGroup)getActivity().getWindow().getDecorView()).addView(showcaseView);
			ShowcaseSessions.saveSession(getActivity(), ListVehicleFragment.class.getSimpleName());
		}
	}*/

	/*@Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("keyword", keyword);
    }
	private void handleSavedInstanceState(Bundle savedInstanceState)
    {
        keyword=savedInstanceState.getString("keyword");
    }*/
}
