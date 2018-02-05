package com.nw.fragments;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.nw.model.ScanVIN;
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

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class LoadVehicleFragment extends BaseFragement implements OnClickListener
{
    EditText edMinYear, edVINType, edMake, edModel, edVariant;
    ArrayList<SmartObject> makeList;
    ArrayList<SmartObject> modelList;
    ArrayList<Variant> variantList;
    ArrayList<Vehicle> vehicleList;
    int selectedMakeId, selectedModelId, selectedVariantId, selectedPageNumber = 0;
    int total_no_of_records = 1000;
    Button btnClear, btnNext;
    VariantDetails variantDetails;
    VINOptionFragment vinOptionFragment;
    Variant variant;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_load_vehicle, container, false);
        setHasOptionsMenu(true);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        edMinYear = (EditText) view.findViewById(R.id.edYear);
        edVINType = (EditText) view.findViewById(R.id.edVINType);
        edMake = (EditText) view.findViewById(R.id.edMake);
        edModel = (EditText) view.findViewById(R.id.edModel);
        edVariant = (EditText) view.findViewById(R.id.edVariant);
        btnClear = (Button) view.findViewById(R.id.btnClear);
        btnNext = (Button) view.findViewById(R.id.btnNext);

        edMake.setOnClickListener(this);
        edModel.setOnClickListener(this);
        edVariant.setOnClickListener(this);
        edMinYear.setOnClickListener(this);
        edVINType.setOnClickListener(this);

        btnClear.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        // show the default year here
        edMinYear.setText("" + Calendar.getInstance().get(Calendar.YEAR));
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Load Vehicle");
        //getActivity().getActionBar().setSubtitle(null);
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

            // Edittext of maximum year
            case R.id.maxYear:
                showToPopUp(v);
                break;

            // Edittext of make
            case R.id.edMake:
                if (makeList == null)
                {
                    if (HelperHttp.isNetworkAvailable(getActivity()))
                        getMakeList(true);
                    else
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                } else
                {
                    if (!makeList.isEmpty())
                        showListPopUp(v, makeList);
                }

                break;
            // Edittext of model
            case R.id.edModel:

                if (selectedMakeId == 0)
                {
                    return;
                }

                if (modelList != null)
                {
                    if (!modelList.isEmpty())
                        showListPopUp(v, modelList);
                } else
                {

                    if (HelperHttp.isNetworkAvailable(getActivity()))
                        getModelList();
                    else
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                }

                break;

            // Edittext of variant
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
                } else
                {
                    if (HelperHttp.isNetworkAvailable(getActivity()))
                        getVariantList();
                    else
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                }

                break;

            // Search button
            case R.id.bSearch:
                selectedPageNumber = 0;
                total_no_of_records = 1000;
                if (vehicleList != null)
                    vehicleList.clear();
                if (HelperHttp.isNetworkAvailable(getActivity()))
                {
                } else
                    CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                break;

            case R.id.edVINType:
                vinOptionFragment = new VINOptionFragment();
                Bundle args = new Bundle();
                args.putString("fromFragment", "load");
                vinOptionFragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.Container, vinOptionFragment).addToBackStack(null).commit();
                break;

            case R.id.btnClear:
                makeList = null;
                modelList = null;
                variantList = null;
                // set default text
                edMinYear.setText("" + Calendar.getInstance().get(Calendar.YEAR));
                edMake.setText(R.string.select_make);
                edModel.setText(R.string.select_model);
                edVariant.setText(R.string.select_varient);
                selectedVariantId = 0;
                selectedModelId = 0;
                selectedMakeId = 0;
                variant = null;
                break;
            case R.id.btnNext:
                getVatientDetails();
                break;
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
        final String lastYear = ed.getText().toString().trim();

        Helper.showDropDownYear(v, new ArrayAdapter<String>(getActivity(), R.layout.list_item_text3, R.id.tvText, years), new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ed.setText(years.get(position) + "");
                if (!lastYear.equals(years.get(position) + ""))
                {
                    edVariant.setText(R.string.select_varient); // set defaulttext
                    edModel.setText(R.string.select_model);
                    edMake.setText(R.string.select_make);
                    selectedVariantId = 0;
                    selectedModelId = 0;
                    selectedMakeId = 0;
                    makeList = null;
                    //getMakeList(false);
                }

            }
        });
        /*if (window.isShowing())
		{
			if (v.getId() == R.id.maxYear)
				window.getListView().setSelection(years.size() - 1); 
		}*/
    }

    /**
     * Displays list popup for make, model and variant Function gets general
     * arraylist as parameter, it can be makeList, modelList or variantList
     *
     * @param
     * @param list
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void showListPopUp(final View mView, final ArrayList list)
    {
        try
        {
            final EditText ed = (EditText) mView;
            final String edtData = ed.getText().toString().trim();

            boolean showSearch = false;
            if (mView.getId() == R.id.edMake)
                showSearch = true;
            else
                showSearch = false;

            Helper.showDropDownSearch(showSearch, mView, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, list), new OnItemClickListener()
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
                        if (modelList != null) // remove modelList and variantL items
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
						
						/*if (HelperHttp.isNetworkAvailable(getActivity()))
							getModelList(position);
						else
							CustomDialogManager.showOkDialog(getActivity(),	getString(R.string.no_internet_connection));*/
                    } else if (mView.getId() == R.id.edModel)
                    { // if model is clicked remove variant list items
                        if (variantList != null)
                            variantList = null;
                        edVariant.setText(R.string.select_varient);
                        selectedVariantId = 0;
                        variant = null;

                        selectedModelId = smartObject.getId();
						/*if (HelperHttp.isNetworkAvailable(getActivity()))
							getVariantList(position);
						else
							CustomDialogManager.showOkDialog(getActivity(),	getString(R.string.no_internet_connection));*/

                    } else if (mView.getId() == R.id.edVariant)
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
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("year", Integer.parseInt(edMinYear.getText().toString().trim()), Integer.class));
            parameterList.add(new Parameter("makeID", selectedMakeId, Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListModelsXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListModelsXML");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            //Network call
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
                    } finally
                    {
                        if (!modelList.isEmpty())
                            showListPopUp(edModel, modelList);
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getVariantList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("year", Integer.parseInt(edMinYear.getText().toString().trim()), Integer.class));
            parameterList.add(new Parameter("modelID", selectedModelId, Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListVariantsXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListVariantsXML");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            //Network call
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

                            variantList.add(new Variant(Integer.parseInt(variantObj.getPropertySafelyAsString("variantID", "0")), variantObj.getPropertySafelyAsString("meadCode", "0"),
                                    variantObj.getPropertySafelyAsString("friendlyName", "-"), variantObj.getPropertySafelyAsString("variantName", "-"),
                                    variantObj.getPropertySafelyAsString("MinDate", "-"), variantObj.getPropertySafelyAsString("MaxDate", "-")
                            ));

                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    } finally
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
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getMakeList(final boolean show)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("year", Integer.parseInt(edMinYear.getText().toString()), Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListMakesXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListMakesXML");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            //Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                //Network callback
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
                    } finally
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
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getVatientDetails()
    {
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

        if (variant == null)
        {
            // select variant
            Helper.showToast(getString(R.string.select_varient1), getActivity());
            return;
        }


        //go to next screen
        variantDetails = new VariantDetails();
        variant.setDetails("");
        variant.setYear(edMinYear.getText().toString());
        Bundle bundle = new Bundle();
        ScanVIN scanVIN = new ScanVIN(variant);
        scanVIN.setYear(edMinYear.getText().toString());
        bundle.putParcelable("data", scanVIN);
        bundle.putBoolean("update", false);
        bundle.putString("fromFragment", "Add To Stock");
        variantDetails.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.Container, variantDetails).addToBackStack(null).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (variantDetails != null)
            variantDetails.onActivityResult(requestCode, resultCode, data);
        if (vinOptionFragment != null)
            vinOptionFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
		/*if(!ShowcaseSessions.isSessionAvailable(getActivity(), LoadVehicleFragment.class.getSimpleName())){
			ArrayList<TargetView> viewList= new ArrayList<TargetView>();
			viewList.add(new TargetView(getActivity().findViewById(R.id.btnNext),  ShowCaseType.Right,getString(R.string.press_here_to_add_vehicle_to_stock)));
			viewList.add(new TargetView(getActivity().findViewById(R.id.edVINType),ShowCaseType.Left,getString(R.string.scan_vehicle_barcode_by_tapping_here)));
			
			ShowcaseLayout showcaseView = new ShowcaseLayout(getActivity());
			showcaseView.setShowcaseView(viewList);
			showcaseView.setClickable(true);
			((ViewGroup)getActivity().getWindow().getDecorView()).addView(showcaseView);
			ShowcaseSessions.saveSession(getActivity(), LoadVehicleFragment.class.getSimpleName());
		}*/
    }
}
