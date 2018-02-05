package com.nw.fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.OthersWantListAdapter;
import com.nw.adapters.WantedAdapter;
import com.nw.model.DataInObject;
import com.nw.model.OthersWants;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.Wanted;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OthersWantFragment extends BaseFragement implements OnClickListener
{
	ListView lvListOthersWant;

	OthersWantListAdapter othersWantListAdapter;
	ArrayList<OthersWants> othersWants;
	Context context;

	EditText edMinYear, edMaxYear, edMake, edModel;
	Button bClear;
	ListView lvVariant, lvRegion;
	ImageView ivArrow;
	TextView tvFilter, tvPlaceHolder;
	LinearLayout llFilter;
	int selectedMakeId = 0, selectedModelId = 0;
	ArrayList<SmartObject> makeList;
	ArrayList<SmartObject> modelList;
	ArrayList<SmartObject> variantList;
	ArrayList<SmartObject> regionList;
	ArrayList<Wanted> wantedList;
	RegionAdapter regionAdapter, variant_adapter;
	WantedAdapter wantedAdapter;
	boolean isSearchAllDisplayed = false;
	ValueAnimator mAnimator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_others_want, container, false);
		setHasOptionsMenu(true);
		context = getActivity();

		if (othersWants == null)
		{
			othersWants = new ArrayList<OthersWants>();
			for (int i = 0; i < 10; i++)
			{
				othersWants.add(new OthersWants());
			}
		}
		initialise(view);

		if (regionList.isEmpty())
			getRegionList();

		return view;
	}

	@SuppressLint("SimpleDateFormat")
	private void initialise(View view)
	{
		lvListOthersWant = (ListView) view.findViewById(R.id.lvListOthersWant);

		LayoutInflater inflater = getActivity().getLayoutInflater();
		LinearLayout listHeaderView = (LinearLayout) inflater.inflate(R.layout.list_item_header_others_want, null);
		lvListOthersWant.addHeaderView(listHeaderView);

		othersWantListAdapter = new OthersWantListAdapter(context, getActivity(), R.layout.list_item_others_want, othersWants);
		lvListOthersWant.setAdapter(othersWantListAdapter);

		edMinYear = (EditText) view.findViewById(R.id.minYear);
		edMaxYear = (EditText) view.findViewById(R.id.maxYear);
		edMake = (EditText) view.findViewById(R.id.edMake);
		edModel = (EditText) view.findViewById(R.id.edModel);
		tvFilter = (TextView) view.findViewById(R.id.tvFilter);
		tvPlaceHolder = (TextView) view.findViewById(R.id.tvPlaceHolder);
		ivArrow = (ImageView) view.findViewById(R.id.ivArrow);
		llFilter = (LinearLayout) view.findViewById(R.id.llFilter);
		lvVariant = (ListView) view.findViewById(R.id.lvVariant);
		lvRegion = (ListView) view.findViewById(R.id.lvRegion);
		bClear = (Button) view.findViewById(R.id.bClear);

		if (makeList == null)
			makeList = new ArrayList<SmartObject>();
		if (modelList == null)
			modelList = new ArrayList<SmartObject>();
		if (regionList == null)
			regionList = new ArrayList<SmartObject>();
		if (variantList == null)
			variantList = new ArrayList<SmartObject>();
		if (wantedList == null)
			wantedList = new ArrayList<Wanted>();

		edMinYear.setOnClickListener(this);
		edMaxYear.setOnClickListener(this);
		edMake.setOnClickListener(this);
		edModel.setOnClickListener(this);
		tvFilter.setOnClickListener(this);
		ivArrow.setOnClickListener(this);
		bClear.setOnClickListener(this);

		regionAdapter = new RegionAdapter(getActivity(), R.layout.list_item_checked_variant, regionList);
		lvRegion.setAdapter(regionAdapter);

		lvRegion.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				if (regionList.get(position).isChecked())
				{
					regionList.get(position).setChecked(false);
					if (position == 0)
						uncheckAll(regionList);
					else
					{
						if (isOtherUnChecked(regionList))
						{
							regionList.get(0).setChecked(false);
						}
					}
				}
				else
				{
					regionList.get(position).setChecked(true);
					if (position == 0)
						checkAll(regionList);
					else
					{
						if (isOtherChecked(regionList))
						{
							regionList.get(0).setChecked(true);
						}
					}
				}
				regionAdapter.notifyDataSetChanged();
			}
		});

		variant_adapter = new RegionAdapter(getActivity(), R.layout.list_item_checked_variant, variantList);
		lvVariant.setAdapter(variant_adapter);
		checkForPlaceHolder();
		lvVariant.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if (variantList.get(position).isChecked())
				{
					variantList.get(position).setChecked(false);
					if (position == 0)
						uncheckAll(variantList);
					else
					{
						if (isOtherUnChecked(variantList))
						{
							variantList.get(0).setChecked(false);
						}
					}

				}
				else
				{
					variantList.get(position).setChecked(true);
					if (position == 0)
						checkAll(variantList);
					else
					{
						if (isOtherChecked(variantList))
						{
							variantList.get(0).setChecked(true);
						}
					}
				}
				variant_adapter.notifyDataSetChanged();
				checkForPlaceHolder();
			}
		});

		lvRegion.setOnTouchListener(new View.OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				lvRegion.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		lvVariant.setOnTouchListener(new View.OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				lvVariant.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		// Add onPreDrawListener
		ivArrow.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
		{

			@Override
			public boolean onPreDraw()
			{
				llFilter.getViewTreeObserver().removeOnPreDrawListener(this);
				llFilter.setVisibility(View.GONE);

				final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				llFilter.measure(widthSpec, heightSpec);

				mAnimator = slideAnimator(0, llFilter.getMeasuredHeight());
				expand();
				return true;
			}
		});

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
	public void onResume()
	{
		super.onResume();
		showActionBar("Others Want");
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

	private void checkForPlaceHolder()
	{
		if (variantList.isEmpty())
		{
			lvVariant.setVisibility(View.GONE);
			tvPlaceHolder.setVisibility(View.VISIBLE);
		}
		else
		{
			lvVariant.setVisibility(View.VISIBLE);
			tvPlaceHolder.setVisibility(View.GONE);
		}
	}

	private void checkAll(ArrayList<SmartObject> mList)
	{
		for (int i = 1; i < mList.size(); i++)
		{
			mList.get(i).setChecked(true);
		}
	}

	private void uncheckAll(ArrayList<SmartObject> mList)
	{
		for (int i = 1; i < mList.size(); i++)
		{
			mList.get(i).setChecked(false);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.minYear:
				showYearPopup(edMinYear);
				break;

			case R.id.maxYear:
				if (TextUtils.isEmpty(edMinYear.getText().toString()))
				{
					Helper.showToast(getString(R.string.please_select_min_year), getActivity());
					return;
				}
				showYearPopup(edMaxYear);
				break;

			case R.id.edMake:
				if (TextUtils.isEmpty(edMinYear.getText().toString()))
				{
					Helper.showToast(getString(R.string.please_select_min_year), getActivity());
					return;
				}
				if (TextUtils.isEmpty(edMaxYear.getText().toString()))
				{
					Helper.showToast(getString(R.string.please_select_max_year), getActivity());
					return;
				}
				if (!makeList.isEmpty())
				{
					Helper.showDropDown(edMake, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, makeList), new OnItemClickListener()
					{
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id)
						{
							selectedMakeId = makeList.get(position).getId();
							edMake.setText(makeList.get(position).toString());
							if (modelList != null)
								modelList.clear();
							edModel.setText("");
							if (variantList != null)
							{
								variantList.clear();
								variant_adapter.notifyDataSetChanged();
								checkForPlaceHolder();
							}
						}
					});
				}
				else
				{
					getMakeList();
				}
				break;

			case R.id.edModel:
				if (TextUtils.isEmpty(edMinYear.getText().toString()))
				{
					Helper.showToast(getString(R.string.please_select_min_year), getActivity());
					return;
				}
				if (TextUtils.isEmpty(edMaxYear.getText().toString()))
				{
					Helper.showToast(getString(R.string.please_select_max_year), getActivity());
					return;
				}
				if (TextUtils.isEmpty(edMake.getText().toString()))
				{
					Helper.showToast(getString(R.string.please_select_make), getActivity());
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
							{
								variantList.clear();
								variant_adapter.notifyDataSetChanged();
								checkForPlaceHolder();
							}
							getVariantList(position);
						}
					});
				}
				else
				{
					getModelList(selectedMakeId);
				}
				break;

			case R.id.bClear:
				resetViews();
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
					tvFilter.setText(getString(R.string.showfilter));
					ivArrow.setRotation(-90);
					collapse();
				}
			default:
				break;
		}
	}

	// for expanding animation
	private void expand()
	{
		llFilter.setVisibility(View.VISIBLE);
		mAnimator.start();
	}

	// collapsing animation
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getMakeList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			// showActionbarProgress();
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
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{
				// Network callback
				@Override
				public void onTaskComplete(Object result)
				{
					makeList.clear();
					try
					{
						// hideActionbarProgress();
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
							Helper.showDropDown(edMake, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, makeList), new OnItemClickListener()
							{
								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id)
								{
									edMake.setText(makeList.get(position).toString());
									selectedMakeId = makeList.get(position).getId();
									if (modelList != null)
										modelList.clear();
									edModel.setText("");
									if (variantList != null)
									{
										variantList.clear();
										variant_adapter.notifyDataSetChanged();
										checkForPlaceHolder();
									}
								}
							});
						}
					} catch (Exception e)
					{
						e.printStackTrace();
						// hideActionbarProgress();
					}
				}
			}).execute();
		}
		else
		{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}

	// Function fetches Model list and adds to Arraylist - modelList
	// Function gets position as parameter used to get which make is selected
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getModelList(int makeId)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// showActionbarProgress();
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
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{
				@Override
				public void onTaskComplete(Object result)
				{
					// hideActionbarProgress();
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

						Helper.showDropDown(edModel, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, modelList), new OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id)
							{
								edModel.setText(modelList.get(position).toString());
								selectedModelId = modelList.get(position).getId();
								if (variantList != null)
								{
									variantList.clear();
									variant_adapter.notifyDataSetChanged();
									checkForPlaceHolder();
								}
								getVariantList(position);
							}
						});
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}).execute();
		}
		else
		{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}

	// Function fetches variant list and adds to arraylist- variantList
	// Function gets position as parameter used for getting which model is
	// selected
	private void getVariantList(int position)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// showActionbarProgress();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("modelID", modelList.get(position).getId(), Integer.class));
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
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{

				@Override
				public void onTaskComplete(Object result)
				{
					// hideActionbarProgress();
					variantList.clear();
					variantList.add(new SmartObject(0, "All"));
					try
					{
						Helper.Log("response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Variants");
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject variantObj = (SoapObject) inner.getProperty(i);
							SmartObject object = new SmartObject(Integer.parseInt(variantObj.getPropertySafelyAsString("id", "0")), variantObj.getPropertySafelyAsString("name", ""));
							object.setChecked(false);
							variantList.add(i + 1, object);
						}
						variant_adapter.notifyDataSetChanged();
						checkForPlaceHolder();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}).execute();
		}
		else
		{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}

	private void getRegionList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("RegionList");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/RegionList");
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{
				@Override
				public void onTaskComplete(Object result)
				{
					hideProgressDialog();
					regionList.clear();
					regionList.add(new SmartObject(0, "All"));
					try
					{
						Helper.Log("response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Regions");
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject variantObj = (SoapObject) inner.getProperty(i);
							SmartObject object = new SmartObject(Integer.parseInt(variantObj.getPropertySafelyAsString("ID", "0")), variantObj.getPropertySafelyAsString("Name", ""));
							object.setChecked(false);
							regionList.add(i + 1, object);
						}
						sort(regionList);
						regionAdapter.notifyDataSetChanged();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}).execute();
		}
		else
		{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}

	private void resetViews()
	{
		edMinYear.setText("");
		edMaxYear.setText("");
		edMake.setText("");
		edModel.setText("");

		makeList.clear();
		modelList.clear();
		variantList.clear();
		variant_adapter.notifyDataSetChanged();
		uncheckAll(regionList);
		regionList.get(0).setChecked(false);
		regionAdapter.notifyDataSetChanged();
		checkForPlaceHolder();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void showYearPopup(final EditText edYear)
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
		int selPosition;
		if (edYear.getId() == R.id.minYear)
		{
			selPosition = 16;
		}
		else
			selPosition = years.size() - 1;
		Helper.showDropDown(edYear, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, years), new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				edYear.setText(years.get(position));
				if (makeList != null)
					makeList.clear();
				if (modelList != null)
					modelList.clear();
				if (edYear.getId() == R.id.minYear)
				{
					edMaxYear.setText("");
				}
				else if (edYear.getId() == R.id.maxYear)
				{
					if (Integer.parseInt(edMaxYear.getText().toString()) < Integer.parseInt(edMinYear.getText().toString()))
					{
						Helper.showToast(getString(R.string.please_select_year_properly), getActivity());
						edMaxYear.setText("");
						return;
					}
				}
				if (variantList != null)
					variantList.clear();
				if (variant_adapter != null)
					variant_adapter.notifyDataSetChanged();
				checkForPlaceHolder();
				edMake.setText("");
				edModel.setText("");
			}
		});
	}

	private static class RegionAdapter extends ArrayAdapter<SmartObject>
	{

		public RegionAdapter(Context context, int resource, List<SmartObject> objects)
		{
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_checked_variant, parent, false);

			final CheckedTextView tvRegion = (CheckedTextView) convertView.findViewById(android.R.id.text1);
			tvRegion.setText(getItem(position).toString());
			tvRegion.setChecked(getItem(position).isChecked());

			return convertView;
		}
	}

	private void sort(ArrayList<SmartObject> mList)
	{
		Collections.sort(mList, new Comparator<SmartObject>()
		{

			@Override
			public int compare(SmartObject lhs, SmartObject rhs)
			{
				return lhs.getName().compareTo(rhs.getName());
			}
		});
	}

	private boolean isOtherChecked(ArrayList<SmartObject> mList)
	{
		boolean flag = true;
		for (int i = 1; i < mList.size(); i++)
		{
			if (!mList.get(i).isChecked())
			{ // atleast one unchecked
				flag = false;
				break;
			}
		}
		return flag;
	}

	private boolean isOtherUnChecked(ArrayList<SmartObject> mList)
	{
		boolean flag = false;
		for (int i = 1; i < mList.size(); i++)
		{
			if (!mList.get(i).isChecked())
			{ // atleast one checked
				flag = true;
				break;
			}
		}
		return flag;
	}
}
