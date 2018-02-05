package com.nw.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.VariantAdapter;
import com.nw.adapters.VehicleVariantAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.SpecialVehicle;
import com.nw.model.Vehicle;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.text.NumberFormat;
import java.util.ArrayList;

public class VehicleStockFragment extends BaseFragement implements OnClickListener
{
	EditText  edMake, edModel, edVariant,edSelectVehicle;
	int selectedMakeId = -1, selectedModelId = -1;
	ArrayList<SmartObject> makeList;
	ArrayList<SmartObject> modelList;
	ArrayList<SpecialVehicle> vehicleList,variantList;
    Button btnSynopsisSummary,btnVINVerify;
	SummaryFragment summaryFragment;
	VehicleDetails details;
	SpecialVehicle selectedVehicle; 
	boolean isVehicleSelected=false;
	VariantAdapter variantAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_vehicle_stock, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		vehicleList = new ArrayList<SpecialVehicle>();
		if (makeList == null)
		{
			if (HelperHttp.isNetworkAvailable(getActivity()))
				getMakeList();
			else
				CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
		hideKeyboard(view);
		return view;
	}

	private void initialise(View view)
	{
		edMake = (EditText) view.findViewById(R.id.edMake);
		edModel = (EditText) view.findViewById(R.id.edModel);
		edVariant = (EditText) view.findViewById(R.id.edVariant);
		edSelectVehicle = (EditText) view.findViewById(R.id.edSelectVehicle);
		btnSynopsisSummary = (Button) view.findViewById(R.id.btnSynopsisSummary);
		btnSynopsisSummary.setOnClickListener(this);
		btnVINVerify=(Button) view.findViewById(R.id.btnVINVerify);
		btnVINVerify.setOnClickListener(this);
		makeList = new ArrayList<SmartObject>();
		modelList = new ArrayList<SmartObject>();
		variantList = new ArrayList<SpecialVehicle>();
		edMake.setOnClickListener(this);
		edModel.setOnClickListener(this);
		edVariant.setOnClickListener(this);
		edSelectVehicle.setOnClickListener(this);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Vehicle In Stock");
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnSynopsisSummary:
				
				if (edSelectVehicle.getText().toString().equals("Select")&&edMake.getText().toString().equals("Select Make")
				&&edModel.getText().toString().equals("Select Model")&&edVariant.getText().toString().equals("Select Variant"))
				{
					Helper.showToast("Please select inputs", getActivity());
					return;
				}
				
				if (!edSelectVehicle.getText().toString().equals("Select")&&edMake.getText().toString().equals("Select Make")
						&&edModel.getText().toString().equals("Select Model")&&edVariant.getText().toString().equals("Select Variant"))
				{

					GetSynopsisByVehicle();
				}else if (!edSelectVehicle.getText().toString().equals("Select")&& !edMake.getText().toString().equals("Select Make")
						&& !edModel.getText().toString().equals("Select Model")&& !edVariant.getText().toString().equals("Select Variant"))
				{
					GetSynopsisXml();
				}
				else if(edSelectVehicle.getText().toString().equals("Select")&&!edMake.getText().toString().equals("Select Make")
				||!edModel.getText().toString().equals("Select Model")||!edVariant.getText().toString().equals("Select Variant")){ 
					if (edMake.getText().toString().equals("Select Make"))
					{
						Helper.showToast(getString(R.string.select_make1), getActivity());
						return;
					}
					else if (edModel.getText().toString().equals("Select Model"))
					{
						Helper.showToast(getString(R.string.select_model1), getActivity());
						return;
					}
					else if (edVariant.getText().toString().equals("Select Variant"))
					{
						Helper.showToast(getString(R.string.select_varient1), getActivity());
						return;
					}
					GetSynopsisXml();
				}
				
				break;

            case R.id.btnVINVerify:
				if (edSelectVehicle.getText().toString().equals("Select")&&edMake.getText().toString().equals("Select Make")
						&&edModel.getText().toString().equals("Select Model")&&edVariant.getText().toString().equals("Select Variant"))
				{
					Helper.showToast("Please select inputs", getActivity());
					return;
				}
				else if(edSelectVehicle.getText().toString().equals("Select")&&!edMake.getText().toString().equals("Select Make")
						||!edModel.getText().toString().equals("Select Model")||!edVariant.getText().toString().equals("Select Variant")){
					if (edMake.getText().toString().equals("Select Make"))
					{
						Helper.showToast(getString(R.string.select_make1), getActivity());
						return;
					}
					else if (edModel.getText().toString().equals("Select Model"))
					{
						Helper.showToast(getString(R.string.select_model1), getActivity());
						return;
					}
					else if (edVariant.getText().toString().equals("Select Variant"))
					{
						Helper.showToast(getString(R.string.select_varient1), getActivity());
						return;
					}
				}
                Bundle bundle=new Bundle();
                bundle.putBoolean("fromSum", false);
                VehicleDetails vehicleDetails = new VehicleDetails();
                if (isVehicleSelected){
                    vehicleDetails.setYear(selectedVehicle.getUsedYear());
                //   vehicleDetails.setVin(selectedVehicle.get);
                    vehicleDetails.setMileage(selectedVehicle.getMileage());
                    vehicleDetails.setFriendlyName(selectedVehicle.getFriendlyName());
                }else {
                    vehicleDetails.setFriendlyName(edMake.getText().toString().trim() + " " + edModel.getText().toString().trim() + " " +
                            edVariant.getText().toString().trim());
                }
                bundle.putParcelable("summaryObejct",vehicleDetails);
                VerifyVINFragment verifyVINFragment=new VerifyVINFragment();
                verifyVINFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, verifyVINFragment).addToBackStack(null).commit();
                break;

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
							edModel.setText("Select Model");
							edVariant.setText("Select Variant");
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
							edVariant.setText("Select Variant");
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
				if (variantList != null)
				{
					if (!variantList.isEmpty())
					{
						  Helper.showDropDown(edVariant, new VehicleVariantAdapter(getActivity(),R.layout.list_item_variant_details, variantList), new OnItemClickListener() {
						       @Override
						       public void onItemClick(AdapterView<?> parent,
						         View view, int position, long id) {
						        Helper.hidekeybord(edVariant);
						        edVariant.setText(variantList.get(position).getVariantName() + "");
						        selectedVehicle = variantList.get(position);
						        hideKeyboard();
						       }
						      });
					}
					else
					{
						getVariantList(selectedModelId);
					}
				}
				break;
				
			case R.id.edSelectVehicle:
				if (!vehicleList.isEmpty()) {
					showSearch(getActivity(),vehicleList,edSelectVehicle);
				}else{
					isVehicleSelected =false;
					getAllVehicleList();
				}
				break;
		}
		
	}
		
	public void showSearch(Context context, final ArrayList<SpecialVehicle> arrayList,final EditText editText)
	{
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_search);
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.show();

		ListView lstData = (ListView) dialog.findViewById(R.id.listView);
		lstData.setEmptyView(dialog.findViewById(R.id.emptyView));
		final EditText edtClientName = (EditText) dialog .findViewById(R.id.edtClientName);
		TextView tvCancel=(TextView) dialog.findViewById(R.id.tvCancel);
		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(edtClientName.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				dialog.dismiss();
			}
		});
		variantAdapter= new VariantAdapter(getActivity(),R.layout.list_item_variant_details, arrayList);
		lstData.setAdapter(variantAdapter);
		edtClientName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
				variantAdapter.getFilter().filter(arg0);
				if (arg0.toString().length()==0)
				{
					InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(edtClientName.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});
		lstData.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				dialog.dismiss();
				Helper.hidekeybord(edtClientName);
				editText.setText(variantAdapter.getItem(position) + "");
				makeList.clear();
				modelList.clear();
				variantList.clear();
				selectedVehicle = variantAdapter.getItem(position);
				//selectedItemId= arrayList.get(position).getItemID();
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(edtClientName.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				isVehicleSelected =true;
			}
		});
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		dialog.show();
	}
	
	private void GetSynopsisByVehicle()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("year", selectedVehicle.getUsedYear(), Integer.class));
			parameterList.add(new Parameter("makeId",selectedVehicle.getMakeId(), Integer.class));
			parameterList.add(new Parameter("modelId", selectedVehicle.getModelID(), Integer.class));
			parameterList.add(new Parameter("variantId", selectedVehicle.getVariantID(), Integer.class));
			parameterList.add(new Parameter("VIN", selectedVehicle.getVin(), String.class));
			parameterList.add(new Parameter("kilometers",selectedVehicle.getMileage(), String.class));
			parameterList.add(new Parameter("extras","", String.class)); 
			parameterList.add(new Parameter("condition","", String.class));
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
						CustomDialogManager.showOkDialog(getActivity(), "Error while loading data");
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
			parameterList.add(new Parameter("year", selectedVehicle.getUsedYear(), Integer.class));
			parameterList.add(new Parameter("makeId", selectedMakeId, Integer.class));
			parameterList.add(new Parameter("modelId", selectedModelId, Integer.class));
			parameterList.add(new Parameter("variantId", selectedVehicle.getVariantID(), Integer.class));
			parameterList.add(new Parameter("VIN", "", String.class));
			parameterList.add(new Parameter("kilometers","", Integer.class));
			parameterList.add(new Parameter("extras","", String.class)); 
			parameterList.add(new Parameter("condition","", String.class));
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

	private void getMakeList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("clientID",  DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			if (isVehicleSelected)
			{
				parameterList.add(new Parameter("year", selectedVehicle.getUsedYear(), Integer.class));
			}
			// create web service inputs
			DataInObject inObj = new DataInObject();
			if (isVehicleSelected)
			{
				inObj.setMethodname("ListDealerMakesXML");
				inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
				inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListDealerMakesXML");
			}else {
				inObj.setMethodname("ListDealerMakesOpenXML");
				inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
				inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListDealerMakesOpenXML");
			}
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
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
							String makeid = makeObj.getPropertySafelyAsString("makeID", "0");
							String makename = makeObj.getPropertySafelyAsString("makeName", "-");
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
									edModel.setText("Select Model");
									edVariant.setText("Select Variant");
								}
							});
						}else
						{
							CustomDialogManager.showOkDialog(getActivity(), "No records(s) found.");
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
			parameterList.add(new Parameter("clientID",  DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			if (isVehicleSelected)
			{
				parameterList.add(new Parameter("year", selectedVehicle.getUsedYear(), Integer.class));
			}
			parameterList.add(new Parameter("makeID",  selectedMakeId, Integer.class));
			
			// create web service inputs
			DataInObject inObj = new DataInObject();
			if (isVehicleSelected)
			{
				inObj.setMethodname("ListDealerModelsXML");
				inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
				inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListDealerModelsXML");
			}else {
				inObj.setMethodname("ListDealerModelsOpenXML");
				inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
				inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListDealerModelsOpenXML");
			}
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{

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
							String modelid = makeObj.getPropertySafelyAsString("modelID", "0");
							String modelname = makeObj.getPropertySafelyAsString("modelName", "");
							modelList.add(i, new SmartObject(Integer.parseInt(modelid), modelname));
						}
						if(!modelList.isEmpty())
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
								edVariant.setText("Select Variant");
							}
						});
						}else
						{
							CustomDialogManager.showOkDialog(getActivity(), "No records(s) found.");
						}
					} catch (Exception e)
					{
						e.printStackTrace();
						hideProgressDialog();
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data));
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
		parameterList.add(new Parameter("clientID",  DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
		parameterList.add(new Parameter("modelID",  selectedModelId, Integer.class));
		
		// create web service inputs
		DataInObject inObj = new DataInObject();
		
		inObj.setMethodname("ListDealerStockByModelXML");
		inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
		inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListDealerStockByModelXML");
		inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
		inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{
				@Override
				public void onTaskComplete(Object result)
				{
					variantList.clear();
					try
					{
						SpecialVehicle vehicleNew = null;
						Helper.Log("response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Variants");
						for(int i=0;i<inner.getPropertyCount();i++){
							vehicleNew = new SpecialVehicle();
							SoapObject variantObj= (SoapObject) inner.getProperty(i);	
							vehicleNew.setVariantID(Integer.parseInt(variantObj.getPropertySafelyAsString("variantID", "0")));
							vehicleNew.setStockCode(variantObj.getPropertySafelyAsString("meadCode", ""));
							vehicleNew.setFriendlyName(variantObj.getPropertySafelyAsString("friendlyName", ""));
							vehicleNew.setVariantName(variantObj.getPropertySafelyAsString("variantName", ""));
							vehicleNew.setUsedYear(Integer.parseInt(variantObj.getPropertySafelyAsString("year", "0")));
							vehicleNew.setColour(variantObj.getPropertySafelyAsString("colour", ""));
							variantList.add(vehicleNew);
						}
						hideProgressDialog();
						edVariant.performClick();
					} catch (Exception e)
					{
						e.printStackTrace();
						hideProgressDialog();
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data));
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
	/*Function to get all new vehicles in stock */
	private void getAllVehicleList() {
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash",DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("clientID",DataManager.getInstance().user.getDefaultClient().getId(),Integer.class));
			parameterList.add(new Parameter("statusID", 1, Integer.class));
			parameterList.add(new Parameter("pageSize", 500, Integer.class));
			parameterList.add(new Parameter("pageNumber", 0, Integer.class));	
			parameterList.add(new Parameter("sort", "friendlyname", String.class));
				
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ListVehiclesByStatusXML");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListVehiclesByStatusXML");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
	
			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener() {
	
				@Override
				public void onTaskComplete(Object result) 
				{
					vehicleList.clear();
					try {
						SpecialVehicle vehicleNew = null;
						Helper.Log("soap Response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("stockList");
						for (int i = 0; i < inner.getPropertyCount(); i++) {
							vehicleNew = new SpecialVehicle();
							SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
							vehicleNew.setItemID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("usedVehicleStockID", "0")));
							vehicleNew.setStockCode(vehicleObj.getPropertySafelyAsString("stockCode", ""));
							vehicleNew.setFriendlyName(vehicleObj.getPropertySafelyAsString("vehicleName", ""));
							vehicleNew.setUsedYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("usedYear", "0")));
							vehicleNew.setColour(vehicleObj.getPropertySafelyAsString("colour", ""));
							vehicleNew.setVin(vehicleObj.getPropertySafelyAsString("vin","VIN?"));
							vehicleNew.setReg(vehicleObj.getPropertySafelyAsString("registration","Reg?"));
							vehicleNew.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("mileage", "0")));
							vehicleNew.setMakeId(Integer.parseInt(vehicleObj.getPropertySafelyAsString("makeID", "0")));
							vehicleNew.setModelID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("modelID", "0")));
							vehicleNew.setVariantID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("variantID", "0")));
							vehicleList.add(vehicleNew);
						}
						edSelectVehicle.performClick();
						
					} catch (Exception e) {
						e.printStackTrace();
						hideProgressDialog();
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data));
					}
				}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
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

}
