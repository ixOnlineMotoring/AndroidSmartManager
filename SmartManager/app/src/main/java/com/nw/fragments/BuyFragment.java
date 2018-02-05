package com.nw.fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.nw.adapters.CustomBuyListAdapter;
import com.nw.model.DataInObject;
import com.nw.model.MyImage;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.Variant;
import com.nw.model.Vehicle;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomCheckBox;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class BuyFragment extends BaseFragement implements OnItemClickListener, OnClickListener
{

	ListView lvBuy; // vehicle list
	LinearLayout llFilter, llSort;
	TextView tvFilter, tvVehicleCount,tvFilterrecords;
	CustomBuyListAdapter adapter;
	TableRow trSort;
	boolean  isNeedLoading = false, isLoadMore = false, isFromNextPage = false, isSearched = false;
	EditText edMinYear, edMaxYear, edMake, edModel, edVariant, edSortby;
	int selectedMakeId = -1, selectedModelId = -1, selectedVariantId = -1, selectedPageNumber = 0, selectedSortType = 0, selectedVehiclePosition = -1, selectedSortPosition = 0, selectedSortMethod;
	// for getting positions of make, model, variant selected
	ImageView ivArrow;
	ListPopupWindow window;
	ArrayList<SmartObject> makeList;
	ArrayList<SmartObject> modelList;
	ArrayList<Variant> variantList;
	ArrayList<Vehicle> vehicleList;
	ArrayList<SortItem> sortbylist = new ArrayList<SortItem>();
	Button bSearch, bClear;
	ValueAnimator mAnimator;
	int total_no_of_records = 1000;
	CustomCheckBox cbAll, cbPrivateVehicle, cbFactoryDemo, cbTendors, cbTradeVehicle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_buy, container, false);
		setHasOptionsMenu(true);

		if (sortbylist.size() == 0)
			putSortListItems();
		if (vehicleList == null)
		{
			vehicleList = new ArrayList<Vehicle>();
		}
		else
		{
			if (isNeedLoading)
			{
				vehicleList.clear();
				searchVehicles(selectedSortPosition, selectedSortType);
			}
		}
		// vehicles list view initialization
		initialise(view);
		if (makeList == null)
		{
			if (HelperHttp.isNetworkAvailable(getActivity()))
				getMakeList();
			else
				CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
		if (adapter != null)
		{
			lvBuy.setAdapter(adapter);
			isLoadMore = true;
			tvVehicleCount.setText("Vehicles Found: " + total_no_of_records);
			llSort.setVisibility(View.VISIBLE);
			llFilter.setVisibility(View.GONE);
		}
		return view;
	}

	private void putSortListItems()
	{
		sortbylist.add(new SortItem(getString(R.string.none), 0));
	//	sortbylist.add(new SortItem("Age", 0));
		sortbylist.add(new SortItem("Mileage", 0));
		sortbylist.add(new SortItem("Price", 0));
		sortbylist.add(new SortItem("Time left", 0));
		sortbylist.add(new SortItem("Year", 0));
	}

	private void initialise(View view)
	{

		lvBuy = (ListView) view.findViewById(R.id.lvBuy);
		lvBuy.setOnItemClickListener(this);
		ivArrow = (ImageView) view.findViewById(R.id.ivArrow);
		tvFilterrecords= (TextView) view.findViewById(R.id.tvFilterrecords);
		tvVehicleCount = (TextView) view.findViewById(R.id.tvVehicleCount);
		llSort = (LinearLayout) view.findViewById(R.id.llSort);
		llSort.setVisibility(View.GONE);
		trSort = (TableRow) view.findViewById(R.id.trSort);
		llFilter = (LinearLayout) view.findViewById(R.id.llFilter);
		tvFilter = (TextView) view.findViewById(R.id.tvFilter);
		tvFilter.setOnClickListener(this);
		ivArrow.setOnClickListener(this);
		edMinYear = (EditText) view.findViewById(R.id.minYear);
		edMaxYear = (EditText) view.findViewById(R.id.maxYear);
		edMake = (EditText) view.findViewById(R.id.edMake);
		edModel = (EditText) view.findViewById(R.id.edModel);
		edVariant = (EditText) view.findViewById(R.id.edVariant);
		edSortby = (EditText) view.findViewById(R.id.edSortBy);
		edMake.setOnClickListener(this);
		edModel.setOnClickListener(this);
		edVariant.setOnClickListener(this);
		edMinYear.setOnClickListener(this);
		edMaxYear.setOnClickListener(this);
		edSortby.setOnClickListener(this);
		bSearch = (Button) view.findViewById(R.id.bSearch);
		bSearch.setOnClickListener(this);
		bClear = (Button) view.findViewById(R.id.bClear);
		bClear.setOnClickListener(this);
		cbAll = (CustomCheckBox) view.findViewById(R.id.cbAll);
		cbPrivateVehicle = (CustomCheckBox) view.findViewById(R.id.cbPrivateVehicle);
		cbFactoryDemo = (CustomCheckBox) view.findViewById(R.id.cbFactoryDemo);
		cbPrivateVehicle = (CustomCheckBox) view.findViewById(R.id.cbPrivateVehicle);
		cbTendors = (CustomCheckBox) view.findViewById(R.id.cbTendors);
		cbTradeVehicle = (CustomCheckBox) view.findViewById(R.id.cbDealerTrade);
		cbAll.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				cbPrivateVehicle.setChecked(isChecked);
				cbFactoryDemo.setChecked(isChecked);
				cbPrivateVehicle.setChecked(isChecked);
				cbTradeVehicle.setChecked(isChecked);
				cbTendors.setChecked(isChecked);
			}
		});
		makeList = new ArrayList<SmartObject>();
		modelList = new ArrayList<SmartObject>();
		variantList = new ArrayList<Variant>();

		// Set ellipsize
		edVariant.setSelected(true);
		edVariant.setEllipsize(TruncateAt.END);
		edVariant.setSingleLine(true);

		Calendar cal = Calendar.getInstance();
		edMaxYear.setText(cal.get(Calendar.YEAR) + "");
		edMinYear.setText("2006");
		// animation

		// Add onPreDrawListener
		ivArrow.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
		{

			@Override
			public boolean onPreDraw()
			{
				llFilter.getViewTreeObserver().removeOnPreDrawListener(this);
				//llFilter.setVisibility(View.GONE);

				final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				llFilter.measure(widthSpec, heightSpec);

				mAnimator = slideAnimator(0, llFilter.getMeasuredHeight());
				//expand();
				return true;
			}
		});

		lvBuy.setOnScrollListener(new OnScrollListener()
		{

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				int threshold = 1;
				int count = lvBuy.getCount();
				if (scrollState == SCROLL_STATE_IDLE)
				{
					if (lvBuy.getLastVisiblePosition() >= count - threshold)
					{
						if (!vehicleList.isEmpty())
						{
							if (vehicleList.size() < total_no_of_records)
							{
								selectedPageNumber++;
								isLoadMore =true;
								if (HelperHttp.isNetworkAvailable(getActivity()))
									searchVehicles(selectedSortPosition, selectedSortType);
								else
									CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
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

	private void expand()
	{
		// set Visible
		trSort.setVisibility(View.VISIBLE);
		llFilter.setVisibility(View.VISIBLE);
		llSort.setVisibility(View.GONE);
		mAnimator.start();
	}

	private void collapse()
	{
		int finalHeight = llFilter.getHeight();
		ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
		mAnimator.addListener(new Animator.AnimatorListener()
		{
			@Override
			public void onAnimationEnd(Animator animator)
			{
				llFilter.setVisibility(View.GONE);
				// trSort.setVisibility(View.GONE);
				if (!vehicleList.isEmpty()){
					llSort.setVisibility(View.VISIBLE);
				}else {
					llSort.setVisibility(View.GONE);
				}
			}

			@Override
			public void onAnimationStart(Animator animator)
			{
			}

			@Override
			public void onAnimationCancel(Animator animator)
			{
			}

			@Override
			public void onAnimationRepeat(Animator animator)
			{
			}
		});
		mAnimator.start();
	}

	private ValueAnimator slideAnimator(int start, int end)
	{
		ValueAnimator animator = ValueAnimator.ofInt(start, end);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator)
			{
				// Update Height
				int value = (Integer) valueAnimator.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = llFilter.getLayoutParams();
				layoutParams.height = value;
				llFilter.setLayoutParams(layoutParams);
			}
		});
		return animator;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Fragment f = new BuyDetailFragment();
		Bundle args = new Bundle();
		args.putInt("selectedVehicleid", vehicleList.get(position).getID());
		f.setArguments(args);
		selectedVehiclePosition = position;
		getFragmentManager().beginTransaction().replace(this.getId(), f).addToBackStack("listFragment").commit();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		// Edittext of minimum year
			case R.id.minYear:
				showToPopUp(v);
				break;

			// Edittext of maximum year
			case R.id.maxYear:
				showToPopUp(v);
				break;

			// Edittext of make
			case R.id.edMake:
				if (!makeList.isEmpty())
				{
					Helper.showDropDownSearch(true,edMake, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, makeList), new OnItemClickListener()
					{
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id)
						{
							SmartObject smartObject = (SmartObject) parent.getItemAtPosition(position);
							edMake.setText(smartObject.getName() + "");
							selectedMakeId = smartObject.getId();

							if (modelList != null)
								modelList.clear();
							if (variantList != null)
								variantList.clear();

							edModel.setText("");
							edVariant.setText("");
						}
					});
				}
				else
				{
					getMakeList();
				}
				break;

			// Edittext of model
			case R.id.edModel:
				if (TextUtils.isEmpty(edMake.getText().toString()) || edMake.getText().toString().equals("Select Make*"))
				{
					Helper.showToast("Select make", getActivity());
					return;
				}
				if (!modelList.isEmpty())
				{
					Helper.showDropDown(edModel, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, modelList), new OnItemClickListener()
					{
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id)
						{
							edModel.setText(modelList.get(position).toString());
							selectedModelId = modelList.get(position).getId();
							if (variantList != null)
								variantList.clear();
							edVariant.setText("");
						}
					});
				}
				else
				{
					getModelList(selectedMakeId);
				}
				break;

			// Edittext of variant
			case R.id.edVariant:

				if (edMake.getText().toString().equals(""))
				{
					Helper.showToast(getString(R.string.select_make1), getActivity());
					return;
				}
				if (edModel.getText().toString().equals(""))
				{
					Helper.showToast(getString(R.string.select_model1), getActivity());
					return;
				}
				if (variantList != null)
				{
					if (!variantList.isEmpty())
					{
						Helper.showDropDown(edVariant, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, variantList), new OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id)
							{
								edVariant.setText(variantList.get(position).toString());
								selectedVariantId = variantList.get(position).getVariantId();
							}
						});
					}
					else
					{
						if (edMake.getText().equals("") || makeList.isEmpty())
						{
							Helper.showToast(getString(R.string.select_make1), getActivity());
							return;
						}
						if (edModel.getText().equals("") || modelList.isEmpty())
						{
							Helper.showToast(getString(R.string.select_model1), getActivity());
							return;
						}
						getVariantList(selectedModelId);
					}

				}

				break;

			// Search button
			case R.id.bSearch:
				selectedPageNumber = 0;
				total_no_of_records = 1000;
				ivArrow.performClick();
				if (HelperHttp.isNetworkAvailable(getActivity()))
					searchVehicles(selectedSortPosition, selectedSortType);
				else
					CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
				break;

			case R.id.bClear:
				if (variantList != null)
					variantList.clear();
				if (modelList != null)
					modelList.clear();
				edMake.setText("");
				edModel.setText("");
				edVariant.setText("");
				edSortby.setText(getString(R.string.none));

				cbPrivateVehicle.setChecked(false);
				cbFactoryDemo.setChecked(false);
				cbPrivateVehicle.setChecked(false);
				cbTradeVehicle.setChecked(false);
				cbTendors.setChecked(false);
				cbAll.setChecked(false);

				Calendar cal = Calendar.getInstance();
				int nowYear = cal.get(Calendar.YEAR);
				edMaxYear.setText(nowYear + "");
				edMinYear.setText("2006");
				selectedSortType = 0;

				selectedMakeId = 0;
				selectedModelId = 0;
				selectedVariantId = 0;

				break;

			case R.id.tvFilter:
			case R.id.ivArrow:
				if (llFilter.getVisibility() == View.GONE)
				{
					tvFilter.setText(getString(R.string.hidefilter));
					ivArrow.setRotation(0);
					expand();
				}
				else
				{
					if (isSearched)
					{
						tvFilter.setText(getString(R.string.showsearch));
					}
					else
					{
						tvFilter.setText(getString(R.string.showfilter));
						llSort.setVisibility(View.VISIBLE);
					}
					ivArrow.setRotation(-90);
					collapse();
				}
				break;

			case R.id.edSortBy:
				showSortPopUp(v, sortbylist);
				break;
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		// display action bar title and subtitle
		showActionBar("Search Trade");
		// to hide the filter section when again open of fragment
		if (adapter != null)
		{
			llFilter.setVisibility(View.GONE);
		}
	}

	private void showSortPopUp(final View v, ArrayList<SortItem> list)
	{
		final ArrayList<SortItem> mList = list;
		final View mView = v;
		//window = new ListPopupWindow(getActivity());
		final EditText ed = (EditText) v;
		/*
		 * 0- Descending order 1- Ascending order
		 */
		final ArrayAdapter<SortItem> sortadapter = new ArrayAdapter<SortItem>(getActivity(), R.layout.listitem_sort, R.id.tvText, mList)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				if (convertView == null)
				{
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_sort, null);
				}
				TextView tv = (TextView) convertView.findViewById(R.id.tvText);
				tv.setText(mList.get(position).sortattribute);
				ImageView iv = (ImageView) convertView.findViewById(R.id.ivSortMethod);
				if (position == 0)
					iv.setVisibility(View.GONE);
				if (position != selectedSortType)
					iv.setVisibility(View.GONE);
				else
				{
					if (mList.get(position).sortmethod == 0)
					{
						iv.setRotation(180);
					}
					else
					{
						iv.setRotation(0);
					}
				}
				return convertView;
			}
		};
		Helper.showDropDown(v, sortadapter,new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				//window.dismiss();

				if (mView.getId() == R.id.edSortBy)
				{
					if (selectedSortType == position)
					{
						if (mList.get(position).sortmethod == 0)
							mList.get(position).sortmethod = 1;
						else
							mList.get(position).sortmethod = 0;
					}
					else
						selectedSortType = position;
					sortadapter.notifyDataSetChanged();
					if (position != 0)
					{
						if (mList.get(position).sortmethod == 0)
						{
							selectedSortMethod = 0;
							ed.setText(mList.get(position).sortattribute + " (Ascending)");
						}
						else
						{
							selectedSortMethod = 1;
							ed.setText(mList.get(position).sortattribute + " (Descending)");
						}
					}
					else
					{
						ed.setText(getString(R.string.none));
					}
					selectedPageNumber = 0;
					selectedSortPosition = position;
					vehicleList.clear();
					searchVehicles(selectedSortPosition, selectedSortMethod);
					if (adapter != null)
						adapter.notifyDataSetChanged();
				}
			}
		});
	}

	private void showToPopUp(View v)
	{
		int defaultYear = 1990;
		Calendar cal = Calendar.getInstance();
		int nowYear = cal.get(Calendar.YEAR);
		cal.set(Calendar.YEAR, defaultYear);

		int subtraction = nowYear - defaultYear;
		final List<String> years = new ArrayList<String>();
		int i = 0;
		cal.set(Calendar.YEAR, defaultYear);
		while (i <= subtraction) 
		{
			years.add(cal.get(Calendar.YEAR) + i + "");
			i++;
		}
		Collections.reverse(years);
		final EditText ed = (EditText) v;
		final String lastYear=ed.getText().toString().trim();
		//Collections.sort(years, String.CASE_INSENSITIVE_ORDER);
		Helper.showDropDownYear(v, new ArrayAdapter<String>(getActivity(),R.layout.list_item_text3, R.id.tvText, years), new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				if(Integer.parseInt(years.get(position).trim())<Integer.parseInt(edMinYear.getText().toString().trim()))
				{
					CustomDialogManager.showOkDialog(getActivity(), "Please select a valid year range");
					return;
				}
				ed.setText(years.get(position) + "");
				edMake.setText("");
				edModel.setText("");
				edVariant.setText("");

				selectedMakeId = 0;
				selectedModelId = 0;
				selectedVariantId = 0;

				makeList.clear();
				modelList.clear();
				variantList.clear();
			//	window.dismiss();
					
			}
		});
		/*if (window.isShowing()) 
		{
			if (v.getId() == R.id.maxYear)
				window.getListView().setSelection(years.size() - 1); 
		}*/
	}
	// Function provides search functionality
	private void searchVehicles(int position, int method)
	{

		Helper.Log("total no of record", total_no_of_records + "");
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("MinYear", Integer.parseInt(edMinYear.getText().toString()), Integer.class));
			parameterList.add(new Parameter("MaxYear", Integer.parseInt(edMaxYear.getText().toString()), Integer.class));
			parameterList.add(new Parameter("Make", selectedMakeId, Integer.class));
			parameterList.add(new Parameter("Model", selectedModelId, Integer.class));
			parameterList.add(new Parameter("Variant", selectedVariantId, Integer.class));
			parameterList.add(new Parameter("Province", -1, Integer.class));
			parameterList.add(new Parameter("Count", 10, Integer.class));
			parameterList.add(new Parameter("Page", selectedPageNumber, Integer.class));
			// parameterList.add(new Parameter("SeperateTotal", true,
			// Boolean.class));
			if (cbAll.isChecked())
			{
				parameterList.add(new Parameter("bTrade", true, Boolean.class));
				parameterList.add(new Parameter("bTender", true, Boolean.class));
				parameterList.add(new Parameter("bPrivate", true, Boolean.class));
				parameterList.add(new Parameter("bFactory", true, Boolean.class));
			}
			else
			{
				parameterList.add(new Parameter("bTrade", cbTradeVehicle.isChecked(), Boolean.class));
				parameterList.add(new Parameter("bTender", cbTendors.isChecked(), Boolean.class));
				parameterList.add(new Parameter("bPrivate", cbPrivateVehicle.isChecked(), Boolean.class));
				parameterList.add(new Parameter("bFactory", cbFactoryDemo.isChecked(), Boolean.class));
			}
			parameterList.add(new Parameter("SeperateTotal", 1, Integer.class));
			parameterList.add(new Parameter("Sort", getSortString(position, method), String.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("Search");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/Search");
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{

				@Override
				public void onTaskComplete(Object result)
				{
					try
					{

						Active:
						Helper.Log("soap response", result.toString());
						SoapObject obj = (SoapObject) result;
						tvFilter.setText(R.string.showsearch);
						isSearched = true;
						if (!isLoadMore)
						{
							if (vehicleList != null){
								if(selectedPageNumber == 0)
								vehicleList.clear();
							}
						}
						SoapObject inner = (SoapObject) obj.getPropertySafely("Vehicles", "default");
						Vehicle vehicle;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							vehicle = new Vehicle();
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								vehicle.setID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("ID", "")));
								vehicle.setOwnerID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("OwnerID", "")));
								vehicle.setOwnerName(vehicleObj.getPropertySafelyAsString("OwnerName", ""));
								vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Year", "")));
								vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
								vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
								vehicle.setMileageType(vehicleObj.getPropertySafelyAsString("MileageType", ""));
								vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
								vehicle.setLocation(vehicleObj.getPropertySafelyAsString("Location", ""));
								vehicle.setRetailPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "")));
								vehicle.setCount(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Count", "")));
								if (i==0)
								{
									total_no_of_records = vehicle.getCount();
								}
								vehicle.setExpires(vehicleObj.getPropertySafelyAsString("Expires", ""));
								vehicle.setTimeLeft(vehicleObj.getPropertySafelyAsString("TimeLeft", ""));
								vehicle.setBuyNow(Float.parseFloat(vehicleObj.getPropertySafelyAsString("BuyNow", "")));
								vehicle.setMinBid(Float.parseFloat(vehicleObj.getPropertySafelyAsString("MinBid", "")));
								vehicle.setMyHighestBid(Float.parseFloat(vehicleObj.getPropertySafelyAsString("MyHighestBid", "")));
								vehicle.setHightestBid(Float.parseFloat(vehicleObj.getPropertySafelyAsString("HightestBid", "")));
								vehicle.setIncrement(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Increment", "")));
								vehicle.setStockNumber(vehicleObj.getPropertySafelyAsString("StockNumber", ""));
								vehicle.setRegNumber(vehicleObj.getPropertySafelyAsString("RegNumber", ""));
								vehicle.setVIN(vehicleObj.getPropertySafelyAsString("VIN", ""));
								vehicle.setComments(vehicleObj.getPropertySafelyAsString("Comments", ""));
								vehicle.setExtras(vehicleObj.getPropertySafelyAsString("Extras", ""));
								vehicle.setBought(false);

								SoapObject imageObj = (SoapObject) vehicleObj.getPropertySafely("Images");
								MyImage image;
								ArrayList<MyImage> tImageList = new ArrayList<MyImage>();
								for (int j = 0; j < imageObj.getPropertyCount(); j++)
								{

									if (imageObj.getProperty(j) instanceof SoapObject)
									{
										image = new MyImage();
										SoapObject object = (SoapObject) imageObj.getProperty(j);
										image.setFull(object.getPropertySafelyAsString("Full", ""));
										image.setThumb(object.getPropertySafelyAsString("Thumb", ""));
										tImageList.add(j, image);
									}
								}
								vehicle.setImageList(tImageList);
								vehicleList.add(vehicle);
								llSort.setVisibility(View.VISIBLE);
								tvVehicleCount.setText("Vehicles Found: " + total_no_of_records);
							}
							else
							{
								SoapPrimitive p = (SoapPrimitive) inner.getProperty(i);
								String total = p.getValue().toString();
								total_no_of_records = Integer.parseInt(total);
								llSort.setVisibility(View.VISIBLE);
								tvVehicleCount.setText("Vehicles Found: " + total_no_of_records);
							}
						}
						LinkedHashSet<Vehicle> hashSet = new LinkedHashSet<Vehicle>();
						hashSet.addAll(vehicleList);
						if(vehicleList.isEmpty())
						{
							tvFilterrecords.setVisibility(View.VISIBLE);
							tvFilterrecords.setText("No results found. Try adjusting your filters");
						}else
						{
							tvFilterrecords.setVisibility(View.GONE);
						}
						vehicleList.clear();
						vehicleList.addAll(hashSet);
						if (inner.getPropertyCount() == 0)
						{
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.try_adjusting_filters));
						}

						if (adapter == null)
						{
							adapter = new CustomBuyListAdapter(getActivity(), R.layout.item_buy_list, vehicleList);
							lvBuy.setAdapter(adapter);
						}
						else
						{
							adapter.notifyDataSetChanged();
						}
						isLoadMore = false;
					} catch (Exception e)
					{
						e.printStackTrace();
						CustomDialogManager.showOkDialogAutoCancel(getActivity(), getString(R.string.try_adjusting_filters));
					}
					finally
					{
						llSort.setVisibility(View.VISIBLE);
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	private void getMakeList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("fromYear", Integer.parseInt(edMinYear.getText().toString()), Integer.class));
			parameterList.add(new Parameter("toYear", Integer.parseInt(edMaxYear.getText().toString()), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ListMakesXML");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ListMakesXML");
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{

				// Network callback

				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public void onTaskComplete(Object result)
				{
					makeList.clear();
					try
					{
						Helper.Log("response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Makes");
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject makeObj = (SoapObject) inner.getProperty(i);
							String makeid = makeObj.getPropertySafelyAsString("id", "0");
							String makename = makeObj.getPropertySafelyAsString("name", "-");
							makeList.add(i, new SmartObject(Integer.parseInt(makeid), makename));
						}

						if (!makeList.isEmpty())
						{
							Helper.showDropDownSearch(true,edMake, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, makeList), new OnItemClickListener()
							{
								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id)
								{
									SmartObject smartObject = (SmartObject) parent.getItemAtPosition(position);
									edMake.setText(smartObject.getName() + "");
									selectedMakeId = smartObject.getId();
									if (modelList != null)
										modelList.clear();
									if (variantList != null)
										variantList.clear();
									edModel.setText("");
									edVariant.setText("");
								}
							});
						}else
						{
							CustomDialogManager.showOkDialog(getActivity(), "No record(s) found");
						}

					} catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
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

	private void getModelList(int makeId)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("makeID", makeId, Integer.class));
			parameterList.add(new Parameter("fromYear", edMinYear.getText().toString(), Integer.class));
			parameterList.add(new Parameter("toYear", edMaxYear.getText().toString(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ListModelsXML");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ListModelsXML");
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{

				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public void onTaskComplete(Object result)
				{
					modelList.clear();
					try
					{
						Helper.Log("response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Models");
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject makeObj = (SoapObject) inner.getProperty(i);
							String modelid = makeObj.getPropertySafelyAsString("id", "0");
							String modelname = makeObj.getPropertySafelyAsString("name", "");
							modelList.add(i, new SmartObject(Integer.parseInt(modelid), modelname));
						}
						if(modelList.isEmpty())
						{
								CustomDialogManager.showOkDialog(getActivity(), "No record(s) found");
								return;
						}
						Helper.showDropDown(edModel, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, modelList), new OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id)
							{
								edModel.setText(modelList.get(position).toString());
								selectedModelId = modelList.get(position).getId();
								if (variantList != null)
									variantList.clear();
								edVariant.setText("");
							}
						});
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
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

	// Function fetches variant list and adds to arraylist- variantList
	// Function gets position as parameter used for getting which model is
	// selected
	private void getVariantList(int modelId)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("modelID", modelId, Integer.class));
			parameterList.add(new Parameter("fromYear", edMinYear.getText().toString(), Integer.class));
			parameterList.add(new Parameter("toYear", edMaxYear.getText().toString(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ListVariantsXML");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ListVariantsXML");
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{

				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public void onTaskComplete(Object result)
				{
					variantList.clear();
					try
					{
						Helper.Log("response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Variants");
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject variantObj = (SoapObject) inner.getProperty(i);
							variantList.add(
									i,
									new Variant(Integer.parseInt(variantObj.getPropertySafelyAsString("id", "0")), "", variantObj.getPropertySafelyAsString("name", ""), variantObj
											.getPropertySafelyAsString("name", "")));
						}
						if(variantList.isEmpty())
						{
								CustomDialogManager.showOkDialog(getActivity(), "No record(s) found");
								return;
						}

						Helper.showDropDown(edVariant, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, variantList), new OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id)
							{
								edVariant.setText(variantList.get(position).toString());
								selectedVariantId = variantList.get(position).getVariantId();
							}
						});
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
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

	private class SortItem
	{
		String sortattribute;
		int sortmethod;

		public SortItem(String sortattribute, int sortmethod)
		{
			super();
			this.sortattribute = sortattribute;
			this.sortmethod = sortmethod;
		}

	}

	public void isNeedToUpdate(boolean flag)
	{
		isNeedLoading = flag;
	}

	private String getSortString(int position, int method)
	{
		switch (position)
		{
			case 0:
				return "mileage:asc";
			/*case 1:
				if (method == 1)
					return "age:desc";
				else
					return "age:asc";*/
			case 1:
				if (method == 1)
					return "mileage:desc";
				else
					return "mileage:asc";
			case 2:
				if (method == 1)
					return "price:desc";
				else
					return "price:asc";
			case 3:
				if (method == 1)
					return "time:desc";
				else
					return "time:asc";
			case 4:
				if (method == 1)
					return "year:desc";
				else
					return "year:asc";
			default:
				return "";
		}
	}

	/*
	 * ShowcaseLayout showcaseView; private void displayShowcaseView(){
	 * if(!ShowcaseSessions.isSessionAvailable(getActivity(),
	 * BuyFragment.class.getSimpleName())){
	 * 
	 * ArrayList<TargetView> viewList= new ArrayList<TargetView>();
	 * viewList.add(new TargetView(bSearch,ShowCaseType.Right,
	 * getString(R.string.tap_here_to_search_vehicle))); showcaseView= new
	 * ShowcaseLayout(getActivity()); showcaseView.setShowcaseView(viewList);
	 * 
	 * ((ViewGroup)getActivity().getWindow().getDecorView()).addView(showcaseView
	 * ); ShowcaseSessions.saveSession(getActivity(),
	 * BuyFragment.class.getSimpleName()); } }
	 */
}
