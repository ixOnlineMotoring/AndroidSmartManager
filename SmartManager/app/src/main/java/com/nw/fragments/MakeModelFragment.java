package com.nw.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MakeModelFragment extends DialogFragment implements OnClickListener
{
	EditText edMinYear, edMake, edModel, edVariant;
	ArrayList<SmartObject> makeList;
	ArrayList<SmartObject> modelList;
	ArrayList<Variant> variantList;
	ArrayList<Vehicle> vehicleList;
	int selectedMakeId, selectedModelId, selectedVariantId, selectedPageNumber = 0;
	Button btnClear, btnNext;
	Variant variant;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		getDialog().setTitle("Edit ");
		View view = inflater.inflate(R.layout.vw_make_model, container, false);
		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		setupView();
	}

	void setupView()
	{
		edMinYear = (EditText) getView().findViewById(R.id.edYear);
		edMake = (EditText) getView().findViewById(R.id.edMake);
		edModel = (EditText) getView().findViewById(R.id.edModel);
		edVariant = (EditText) getView().findViewById(R.id.edVariant);
		btnClear = (Button) getView().findViewById(R.id.btnClear);
		btnNext = (Button) getView().findViewById(R.id.btnNext);

		edMake.setOnClickListener(this);
		edModel.setOnClickListener(this);
		edVariant.setOnClickListener(this);
		edMinYear.setOnClickListener(this);

		btnClear.setOnClickListener(this);
		btnNext.setOnClickListener(this);

		// show the default year here
		edMinYear.setText("" + Calendar.getInstance().get(Calendar.YEAR));
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		// Edittext of minimum year
			case R.id.edYear:
				showToPopUp(v);
				break;

			case R.id.edMake:
				if (makeList == null)
				{
						getMakeList(true);
				}
				else
				{
					if (makeList.size() > 0)
						showListPopUp(v, makeList);
				}

				break;

			// Edittext of model
			case R.id.edModel:

				if (selectedMakeId == 0)
				{
					Helper.showToast(getString(R.string.select_make1), getActivity());
					return;
				}

				if (modelList != null)
					if (modelList.size() > 0)
						showListPopUp(v, modelList);
				
				break;

			// Edittext of variant
			case R.id.edVariant:

				if (selectedMakeId == 0)
				{
					Helper.showToast(getString(R.string.select_make1), getActivity());
					return;
				}
				if (selectedModelId == 0)
				{
					Helper.showToast(getString(R.string.select_model1), getActivity());
					return;
				}

				if (variantList != null)
				{
					if (variantList.size() > 0)
					{
						final NewVariantAdapter variAdapter=new NewVariantAdapter(getActivity(), R.layout.list_item_variant, variantList);
						
						Helper.showDropDown(v, variAdapter, new OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> parent, View view,	int position, long id) 
							{
								edVariant.setText(variantList.get(position).getVariantName() + "");
								selectedVariantId = variantList.get(position).getVariantId();
								if (selectedVariantId != 0)
									variant = variantList.get(position);
							}
						});
						
					}
					else
						Helper.showToast(getString(R.string.no_variant), getActivity());
				}
				else
				{
					Helper.showToast(getString(R.string.no_variant), getActivity());
				}
						
						
						
				break;

			case R.id.btnNext:
				getVatientDetails();

				break;
			case R.id.btnClear:
				dismiss();
				break;

		}
	}

	// Function fetches Make list and adds to Arraylist - makeList
	private void getMakeList(final boolean show)
	{
		if(HelperHttp.isNetworkAvailable(getActivity())){
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash",DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("year", Integer.parseInt(edMinYear.getText().toString()), Integer.class));
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ListMakesXML");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+ "IStockService/ListMakesXML");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
	
			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{
				// Network callback
				@Override
				public void onTaskComplete(Object result)
				{
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
							makeList.add(new SmartObject(Integer.parseInt(makeid),makename));
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}finally
					{
						if (show)
						{
							if (!makeList.isEmpty())
								showListPopUp(edMake, makeList);
							else
								Helper.showToast(getString(R.string.no_make),getActivity());
						}
					}
				}
			}).execute();
		}
	}
	
	private void getModelList(int position){
		if(HelperHttp.isNetworkAvailable(getActivity())){
			//Add parameters to request in arraylist
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("year", Integer.parseInt(edMinYear.getText().toString()),Integer.class));
			parameterList.add(new Parameter("makeID", makeList.get(position).getId(),Integer.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("ListModelsXML");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListModelsXML");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			new WebServiceTask(getActivity(),inObj, true,new TaskListener() {
			
			@Override
			public void onTaskComplete(Object result) {
				modelList= new ArrayList<SmartObject>();
					try{
					SoapObject outer= (SoapObject) result;
					SoapObject inner= (SoapObject) outer.getPropertySafely("Models");
					for(int i=0;i<inner.getPropertyCount();i++){
						SoapObject modelObj= (SoapObject) inner.getProperty(i);
						String modelid=modelObj.getPropertySafelyAsString("modelID", "0");
						String modelname= modelObj.getPropertySafelyAsString("modelName","-");
						
						modelList.add(i, new SmartObject(Integer.parseInt(modelid),modelname));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			}).execute();
		}else {
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	/**
	 * Displays list popup for make, model and variant Function gets general
	 * arraylist as parameter, it can be makeList, modelList or variantList
	 * 
	 * @param
	 * @param list
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void showListPopUp(final View mView, final ArrayList list)
	{
		final EditText ed = (EditText) mView;
		final String edtData = ed.getText().toString().trim();
		Helper.showDropDown(mView, new ArrayAdapter(getActivity(),
				R.layout.list_item_text2, R.id.tvText, list),
				new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view,int position, long id)
					{
						ed.setText(list.get(position) + "");
						if (edtData.equals(ed.getText().toString()))
							return;
						if (mView.getId() == R.id.edMake)
						{
							// if make is clicked second
							// time
							if (modelList != null) // remove modelList and
													// variantL items
								modelList.clear();
							if (variantList != null)
								variantList.clear();
							edVariant.setText(R.string.select_varient); // set
																		// default
																		// text
							edModel.setText(R.string.select_model);
							selectedVariantId = 0;
							selectedModelId = 0;
							variant = null;
							// set default text
							selectedMakeId = makeList.get(position).getId();
							getModelList(position);
						}
						else if (mView.getId() == R.id.edModel)
						{ 
							if (variantList != null)
								variantList.clear();
							edVariant.setText(R.string.select_varient);
							selectedVariantId = 0;
							variant = null;

							selectedModelId = modelList.get(position).getId();
							getVariantList(position);

						}
						else if (mView.getId() == R.id.edVariant)
						{
							selectedVariantId = variantList.get(position).getVariantId();
							if (selectedVariantId != 0)
								variant = variantList.get(position);
						}
					}
				});
	}

	
	private void getVariantList(int position){
		
		if(HelperHttp.isNetworkAvailable(getActivity())){
			//Add parameters to request in arraylist
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("year", Integer.parseInt(edMinYear.getText().toString()),Integer.class));
			parameterList.add(new Parameter("modelID", modelList.get(position).getId(),Integer.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("ListVariantsXML");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListVariantsXML");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			new WebServiceTask(getActivity(),inObj, true,new TaskListener() {
			
			@Override
			public void onTaskComplete(Object result) {
					variantList= new ArrayList<Variant>();
					try{
						SoapObject outer= (SoapObject) result;
						SoapObject inner= (SoapObject) outer.getPropertySafely("Variants");
						for(int i=0;i<inner.getPropertyCount();i++){
							SoapObject variantObj= (SoapObject) inner.getProperty(i);
							
							variantList.add(new Variant(Integer.parseInt(variantObj.getPropertySafelyAsString("variantID", "0")), 
									variantObj.getPropertySafelyAsString("meadCode", "0"),
									variantObj.getPropertySafelyAsString("friendlyName", "-"), 
									variantObj.getPropertySafelyAsString("variantName", "-"),
									variantObj.getPropertySafelyAsString("MinDate", "-"), 
									variantObj.getPropertySafelyAsString("MaxDate", "-")));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
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
		
		Helper.showDropDownYear(v, new ArrayAdapter<String>(getActivity(),R.layout.list_item_text3, R.id.tvText, years), new OnItemClickListener()
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
					getMakeList(false);
				}
					
			}
		});
		/*if (window.isShowing()) 
		{
			if (v.getId() == R.id.maxYear)
				window.getListView().setSelection(years.size() - 1); 
		}*/
	}
/*	private void showToPopUp(View v)
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
		final String lastYear = ed.getText().toString().trim();

		ListPopupWindow window = Helper.getDropDown(v, new ArrayAdapter<String>(getActivity(), R.layout.list_item_text2, R.id.tvText, years), new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
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
					selectedRequestId = 0;
					getMakeList(false);
				}

			}
		});
		if (window.isShowing())
		{
			if (v.getId() == R.id.maxYear)
				window.getListView().setSelection(years.size() - 1);
		}
	}*/

	public void getVatientDetails()
	{
		if (!validateMakeModel())
			return;
		if(HelperHttp.isNetworkAvailable(getActivity())){
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("variantID", selectedVariantId, Integer.class));
	
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("VariantDetails");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/VariantDetails");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{
				@Override
				public void onTaskComplete(Object response)
				{
					if (response != null)
					{
						Helper.Log(getTag(), response.toString());
						if (response instanceof SoapFault || response instanceof SoapObject)
						{
							String result = getString(R.string.no_information);
							if (variatEditListener != null)
							variatEditListener.onVariantEdited(edMinYear.getText().toString(),
									""+selectedMakeId,edMake.getText().toString(),
									""+selectedModelId,edModel.getText().toString(),
									""+selectedVariantId,variant.getVariantName(),
									result,variant.getMeadCode());
							dismiss();
						}
						else
						{
							// not fault
							// no error
							try
							{
								if (variant != null)
								{
									String result = "" + ((SoapPrimitive) response).getValue();
									if (variatEditListener != null)
										variatEditListener.onVariantEdited(edMinYear.getText().toString(),""+selectedMakeId,edMake.getText().toString(),""+selectedModelId,edModel.getText().toString(),""+selectedVariantId,variant.getVariantName(),result,variant.getMeadCode());
									dismiss();
								}
								else
								{
									Helper.showToast(getString(R.string.select), getActivity());
								}
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
					else
					{
						dismiss();
					}
				}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	private boolean validateMakeModel()
	{
		if (selectedMakeId == 0)
		{
			Helper.showToast(getString(R.string.select_make1), getActivity());
			return false;
		}
		if (selectedModelId == 0)
		{
			Helper.showToast(getString(R.string.select_model1), getActivity());
			return false;
		}

		if (variant == null)
		{
			Helper.showToast(getString(R.string.select_varient1), getActivity());
			return false;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				getActivity().getFragmentManager().popBackStack();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	VariatEditListener variatEditListener;

	public void setVariatEditListener(VariatEditListener variatEditListener)
	{
		this.variatEditListener = variatEditListener;
	}

	public interface VariatEditListener
	{
		void onVariantEdited(String year, String makeID, String make, String modelId, String model, String variantId, String variant, String details, String meadcode);
		
	}
}
