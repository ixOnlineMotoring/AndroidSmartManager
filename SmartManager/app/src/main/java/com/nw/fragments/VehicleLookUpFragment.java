package com.nw.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.nw.adapters.NewVariantAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.Variant;
import com.nw.model.Vehicle;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomEditText;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class VehicleLookUpFragment extends BaseFragement implements OnClickListener
{
	EditText edYear, edMake, edModel, edVariant,edtCondition,edVIN,edKilometers,edExtra;
	ArrayList<SmartObject> makeList;
	ArrayList<SmartObject> modelList;
	ArrayList<Variant> variantList;
	int selectedMakeId, selectedModelId, selectedVariantId, selectedPageNumber = 0;
	int total_no_of_records = 1000;
	Variant variant;
	CustomButton btnSynopsisSummary;
	String[] conditionType = { "Excellent", "Very Good", "Good","Poor","Very Poor"};
	SummaryFragment summaryFragment;
	VehicleDetails details;
	Button btnVINVerify;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_vehicle_look_up, container, false);
		setHasOptionsMenu(true);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		edYear = (EditText) view.findViewById(R.id.edYear);
		edMake = (EditText) view.findViewById(R.id.edMake);
		edModel = (EditText) view.findViewById(R.id.edModel);
		edVariant = (EditText) view.findViewById(R.id.edVariant);
		edtCondition = (CustomEditText) view.findViewById(R.id.edtCondition);
		btnSynopsisSummary = (CustomButton) view.findViewById(R.id.btnSynopsisSummary);
		btnSynopsisSummary.setTransformationMethod(null);
		btnSynopsisSummary.setOnClickListener(this);
		edVIN=(EditText) view.findViewById(R.id.edVIN);
		edKilometers=(EditText) view.findViewById(R.id.edKilometers);
		edExtra=(EditText) view.findViewById(R.id.edExtra);
		edMake.setOnClickListener(this);
		edModel.setOnClickListener(this);
		edVariant.setOnClickListener(this);
		edYear.setOnClickListener(this);
		edtCondition.setOnClickListener(this);
		edYear.setText("" + Calendar.getInstance().get(Calendar.YEAR));
		btnVINVerify=(Button) view.findViewById(R.id.btnVINVerify);
		btnVINVerify.setOnClickListener(this);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Vehicle Lookup");
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btnVINVerify:
			if (TextUtils.isEmpty(edMake.getText().toString()) || edMake.getText().toString().equals("Select Make"))
			{
				Helper.showToast("Select make", getActivity());
				return;
			}
			if (edModel.getText().toString().equals("")||edModel.getText().toString().equals("Select Model"))
			{
				Helper.showToast(getString(R.string.select_model1), getActivity());
				return;
			}
			if (edVariant.getText().toString().equals("")||edVariant.getText().toString().equals("Select Variant"))
			{
				Helper.showToast(getString(R.string.select_varient1), getActivity());
				return;
			}
			if (edVIN.getText().toString().equals(""))
			{
				Helper.showToast(getString(R.string.vin_warning), getActivity());
				return;
			}
            if (edKilometers.getText().toString().equals(""))
            {
                Helper.showToast(getString(R.string.kilometers_warning), getActivity());
                return;
            }
			Bundle bundle=new Bundle();
			bundle.putBoolean("fromSum", false);
            VehicleDetails vehicleDetails = new VehicleDetails();
            vehicleDetails.setYear(Integer.parseInt(edYear.getText().toString().trim()));
            vehicleDetails.setFriendlyName(edMake.getText().toString().trim()+" "+edModel.getText().toString().trim()+" "+
                    edVariant.getText().toString().trim());
            vehicleDetails.setVin(edVIN.getText().toString().trim());
            vehicleDetails.setExtras(edExtra.getText().toString().trim());
            vehicleDetails.setCondition(edtCondition.getText().toString().trim());
            vehicleDetails.setMileage(Integer.parseInt(edKilometers.getText().toString().trim()));
            bundle.putParcelable("summaryObejct",vehicleDetails);
            VerifyVINFragment verifyVINFragment=new VerifyVINFragment();
            verifyVINFragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.Container, verifyVINFragment).addToBackStack(null).commit();
			break;

		case R.id.btnSynopsisSummary:
			if (TextUtils.isEmpty(edMake.getText().toString()) || edMake.getText().toString().equals("Select Make"))
			{
				Helper.showToast("Please select make", getActivity());
				return;
			}
			if (edModel.getText().toString().equals("")||edModel.getText().toString().equals("Select Model"))
			{
				Helper.showToast(getString(R.string.select_model1), getActivity());
				return;
			}
			if (edVariant.getText().toString().equals("")||edVariant.getText().toString().equals("Select Variant"))
			{
				Helper.showToast(getString(R.string.select_varient1), getActivity());
				return;
			}
			if (edKilometers.getText().toString().equals(""))
			{
				Helper.showToast(getString(R.string.kilometers_warning), getActivity());
				return;
			}
			selectedPageNumber = 0;
			total_no_of_records = 1000;
			if (HelperHttp.isNetworkAvailable(getActivity()))
				GetSynopsisXml();
			else
				CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
			break;
		
			case R.id.edYear:
				showToPopUp(v);
				break;

			case R.id.edMake:
				if (makeList == null)
				{
					if (HelperHttp.isNetworkAvailable(getActivity()))
						getMakeList(true);
					else
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
				}
				else
				{
					if (!makeList.isEmpty())
						showListPopUp(v, makeList);
				}
				break;

			case R.id.edModel:
				if (selectedMakeId == 0)
				{
					return;
				}
				if (modelList != null)
				{
					if (!modelList.isEmpty())
						showListPopUp(v, modelList);
				}
				else
				{
					if (HelperHttp.isNetworkAvailable(getActivity()))
						getModelList();
					else
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
				}
				break;

			case R.id.edVariant:
				if (selectedMakeId == 0)
				{
					return;
				}
				if (selectedModelId == 0)
				{
					return;
				}
				if (variantList != null)
				{
					if (!variantList.isEmpty())
					{

						NewVariantAdapter variantAdapter = new NewVariantAdapter(getActivity(), R.layout.list_item_variant, variantList);
						Helper.showDropDown(v, variantAdapter, new OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id)
							{
								edVariant.setText(variantList.get(position).getVariantName() + "");
								selectedVariantId = variantList.get(position).getVariantId();
								if (selectedVariantId != 0)
									variant = variantList.get(position);
							}
						});
					}
				}
				else
				{
					if (HelperHttp.isNetworkAvailable(getActivity()))
						getVariantList();
					else
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
				}
				break;

			case R.id.edtCondition:
				Helper.showDropDown(edtCondition, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, conditionType), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						edtCondition.setText(conditionType[position]);
					}
				});
				break;	
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(summaryFragment != null){
			summaryFragment.onActivityResult(requestCode, resultCode, data);
		}
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
		
		Helper.showDropDownYear(v, new ArrayAdapter<String>(getActivity(),R.layout.list_item_text3, R.id.tvText, years), new AdapterView.OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				ed.setText(years.get(position) + "");
				if (!lastYear.equals(years.get(position) + ""))
				{
					edVariant.setText(R.string.select_varient); // set
																// defaulttext
					edModel.setText(R.string.select_model);
					edMake.setText(R.string.select_make);
					selectedVariantId = 0;
					selectedModelId = 0;
					selectedMakeId = 0;
					makeList = null;
					// getMakeList(false);
				}
					
			}
		});
		/*if (window.isShowing()) 
		{
			if (v.getId() == R.id.maxYear)
				window.getListView().setSelection(years.size() - 1); 
		}*/
	}

	private void showListPopUp(final View mView, final ArrayList list)
	{
		try
		{
			final EditText ed = (EditText) mView;
			final String edtData = ed.getText().toString().trim();
			boolean showSearch = false;
			if(mView.getId() == R.id.edMake)
				showSearch = true;
			else
				showSearch = false;

			Helper.showDropDownSearch(showSearch,mView, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, list), new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					SmartObject smartObject = (SmartObject) parent.getItemAtPosition(position);
					ed.setText(smartObject.getName() + "");
					if (edtData.equals(ed.getText().toString().trim()))
						return;
					if (mView.getId() == R.id.edMake)
					{
						// if make is clicked second
						// time
						if (modelList != null) // remove modelList and variantL
												// items
							modelList = null;
						if (variantList != null)
							variantList = null;
						edVariant.setText(R.string.select_varient); // set default text
						edModel.setText(R.string.select_model);
						selectedVariantId = 0;
						selectedModelId = 0;
						variant = null;
						// set default text
						selectedMakeId = smartObject.getId();

					}
					else if (mView.getId() == R.id.edModel)
					{ // if model is clicked remove variant list items
						if (variantList != null)
							variantList = null;
						edVariant.setText(R.string.select_varient);
						selectedVariantId = 0;
						variant = null;
						selectedModelId = smartObject.getId();
					}
					else if (mView.getId() == R.id.edVariant)
					{
						// if variant is
						selectedVariantId = variantList.get(position).getVariantId();
						if (selectedVariantId != 0)
							variant = variantList.get(position);
					}
				}
			});
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void getModelList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("year", Integer.parseInt(edYear.getText().toString().trim()), Integer.class));
			parameterList.add(new Parameter("makeID", selectedMakeId, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ListModelsXML");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListModelsXML");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			showProgressDialog();
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{

				@Override
				public void onTaskComplete(Object result)
				{
					hideProgressDialog();
					modelList = new ArrayList<SmartObject>();
					try
					{
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Models");
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject modelObj = (SoapObject) inner.getProperty(i);
							String modelid = modelObj.getPropertySafelyAsString("modelID", "0");
							String modelname = modelObj.getPropertySafelyAsString("modelName", "-");

							modelList.add(i, new SmartObject(Integer.parseInt(modelid), modelname));
						}
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						if (!modelList.isEmpty())
							showListPopUp(edModel, modelList);
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	private void getVariantList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("year", Integer.parseInt(edYear.getText().toString().trim()), Integer.class));
			parameterList.add(new Parameter("modelID", selectedModelId, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ListVariantsXML");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListVariantsXML");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			showProgressDialog();
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{

				@Override
				public void onTaskComplete(Object result)
				{
					hideProgressDialog();
					variantList = new ArrayList<Variant>();
					try
					{
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Variants");
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject variantObj = (SoapObject) inner.getProperty(i);

							variantList.add(new Variant(Integer.parseInt(variantObj.getPropertySafelyAsString("variantID", "0")), variantObj.getPropertySafelyAsString("meadCode", "0"), variantObj
									.getPropertySafelyAsString("friendlyName", "-"), variantObj.getPropertySafelyAsString("variantName", "-"), variantObj.getPropertySafelyAsString("MinDate", "-"),
									variantObj.getPropertySafelyAsString("MaxDate", "-")));

						}
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						if (!variantList.isEmpty())
						{
							NewVariantAdapter variantAdapter = new NewVariantAdapter(getActivity(), R.layout.list_item_variant, variantList);
							Helper.showDropDown(edVariant, variantAdapter, new OnItemClickListener()
							{
								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id)
								{
									edVariant.setText(variantList.get(position).getVariantName() + "");
									selectedVariantId = variantList.get(position).getVariantId();
									if (selectedVariantId != 0)
										variant = variantList.get(position);
								}
							});
						}
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	private void getMakeList(final boolean show)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("year", Integer.parseInt(edYear.getText().toString()), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ListMakesXML");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListMakesXML");
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
					makeList = new ArrayList<SmartObject>();
					try
					{
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Makes");
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject makeObj = (SoapObject) inner.getProperty(i);
							String makeid = makeObj.getPropertySafelyAsString("makeID", "0");
							String makename = makeObj.getPropertySafelyAsString("makeName", "-");

							makeList.add(i, new SmartObject(Integer.parseInt(makeid), makename));
						}
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						if (show)
						{
							if (!makeList.isEmpty())
								showListPopUp(edMake, makeList);
							else
								Helper.showToast(getString(R.string.no_make), getActivity());
						}
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
	private void GetSynopsisXml() 
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("year", Integer.parseInt(edYear.getText().toString()), Integer.class));
			parameterList.add(new Parameter("makeId", selectedMakeId, Integer.class));
			parameterList.add(new Parameter("modelId", selectedModelId, Integer.class));
			parameterList.add(new Parameter("variantId", selectedVariantId, Integer.class));
			parameterList.add(new Parameter("VIN", edVIN.getText().toString().trim(), String.class));
			parameterList.add(new Parameter("kilometers",edKilometers.getText().toString().trim(), String.class));
			parameterList.add(new Parameter("extras",edExtra.getText().toString().trim(), String.class)); 
			parameterList.add(new Parameter("condition",edtCondition.getText().toString().trim(), String.class));
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetSynopsisXml");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/GetSynopsisXml");
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
					VehicleDetails details = ParserManager.parsesSynopsisForVehicle(result);
					hideProgressDialog();
					if (details!=null)
					{
						summaryFragment = new SummaryFragment();
						Bundle bundle = new Bundle();
						bundle.putParcelable("summaryObejct", details);
						summaryFragment.setArguments(bundle);
						getFragmentManager().beginTransaction().replace(R.id.Container, summaryFragment).addToBackStack(null).commit();
					}else {
						CustomDialogManager.showOkDialog(getActivity(), "Error while loading data. Please try again later");
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
