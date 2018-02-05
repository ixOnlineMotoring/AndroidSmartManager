package com.nw.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.nw.adapters.NewVariantAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Model;
import com.nw.model.Parameter;
import com.nw.model.ScanVIN;
import com.nw.model.SmartObject;
import com.nw.model.Variant;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomEditText;
import com.nw.widget.CustomTextViewLight;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ScanVINDetailsSynopsis extends BaseFragement implements OnClickListener
{
    TextView tvVinMake, tvVinColor, tvVinRegistration, tvVinEngine, tvVinNumber, tvVinLicense,
            tvVinExpires;
    EditText edtYear, edtVariant, edtCondition, etVinModel, edtMilage, edtExtrasCost;
    ScanVIN scanVIN;
    int selectedVariantId = -1;
    ListPopupWindow window;
    CustomButton btnSynopsisSummary, btnDiscard, btnSaveForLater;
    Button btnVINVerify;
    int selectedMakeId, selectedModelId, selectedPosition = 0;
    ArrayList<SmartObject> modelList;
    String[] conditionType = {"Excellent", "Very Good", "Good", "Poor", "Very Poor"};
    VehicleDetails details;
    ArrayList<Variant> variantList;
    Variant variant;
    SummaryFragment summaryFragment;
    LinearLayout llKilometers, llExtras;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_scan_details_synopsis, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getArguments() != null)
        {
            scanVIN = getArguments().getParcelable("scannedVIN");
        }
        setHasOptionsMenu(true);
        initialise(view);
        setDetails(scanVIN);
        return view;
    }

    private void initialise(View view)
    {
        tvVinMake = (CustomTextViewLight) view.findViewById(R.id.tvVinMake);
        edtCondition = (CustomEditText) view.findViewById(R.id.edtCondition);
        edtYear = (CustomEditText) view.findViewById(R.id.edtYear);
        edtVariant = (CustomEditText) view.findViewById(R.id.edtVariant);
        etVinModel = (EditText) view.findViewById(R.id.etVinModel);
        llKilometers = (LinearLayout) view.findViewById(R.id.tbrKilometers);
        llExtras = (LinearLayout) view.findViewById(R.id.tbrExtras);
        variantList = new ArrayList<Variant>();
        edtVariant.setOnClickListener(this);
        edtYear.setOnClickListener(this);
        edtCondition.setOnClickListener(this);
        tvVinColor = (CustomTextViewLight) view.findViewById(R.id.tvVinColor);
        tvVinEngine = (CustomTextViewLight) view.findViewById(R.id.tvVinEngine);
        tvVinExpires = (CustomTextViewLight) view.findViewById(R.id.tvVinExpires);
        tvVinNumber = (CustomTextViewLight) view.findViewById(R.id.tvVinNumber);
        tvVinLicense = (CustomTextViewLight) view.findViewById(R.id.tvVinLicense);
        tvVinRegistration = (CustomTextViewLight) view.findViewById(R.id.tvVinRegistration);
        btnSynopsisSummary = (CustomButton) view.findViewById(R.id.btnSynopsisSummary);
        btnDiscard = (CustomButton) view.findViewById(R.id.btnDiscard);
        btnSaveForLater = (CustomButton) view.findViewById(R.id.btnSaveForLater);
        btnVINVerify = (Button) view.findViewById(R.id.btnVINVerify);
        edtMilage = (EditText) view.findViewById(R.id.edtMilage);
        edtExtrasCost = (EditText) view.findViewById(R.id.edtExtrasCost);
        btnSynopsisSummary.setOnClickListener(this);
        btnDiscard.setOnClickListener(this);
        btnSaveForLater.setOnClickListener(this);
        btnVINVerify.setOnClickListener(this);
    }

    private void setDetails(ScanVIN scanVIN)
    {
        if (scanVIN.getMilage() != null)
        {
            if (!scanVIN.getMilage().trim().equalsIgnoreCase("") && !scanVIN.getMilage().trim().equalsIgnoreCase("anyType{}"))
            {
                llKilometers.setVisibility(View.VISIBLE);
                edtMilage.setText(scanVIN.getMilage());
                Helper.nonEditableEditText(edtMilage);
            } else
            {
                llKilometers.setVisibility(View.GONE);
            }

        }
        if (scanVIN.getExtras() != null)
        {
            if (!scanVIN.getExtras().trim().equalsIgnoreCase("") && !scanVIN.getExtras().trim().equalsIgnoreCase("anyType{}"))
            {
                llExtras.setVisibility(View.VISIBLE);
                edtExtrasCost.setText(scanVIN.getExtras());
                Helper.nonEditableEditText(edtExtrasCost);

            } else
            {
                llExtras.setVisibility(View.GONE);
            }
        }
        if (scanVIN.getYear() != null)
        {
            if (!scanVIN.getYear().trim().equalsIgnoreCase("") && !scanVIN.getYear().trim().equalsIgnoreCase("anyType{}"))
            {
                edtYear.setText(scanVIN.getYear());
                Helper.nonEditableEditText(edtYear);
            }
        }
        if (scanVIN.getVariantstr() != null)
        {
            if (!scanVIN.getVariantstr().trim().equalsIgnoreCase("") && !scanVIN.getVariantstr().trim().equalsIgnoreCase("anyType{}"))
            {
                edtVariant.setText(scanVIN.getVariantstr());
                selectedVariantId = scanVIN.getVariantID();
                Helper.nonEditableEditText(edtVariant);

            }
        }
        if (scanVIN.getCondition() != null)
        {
            if (!scanVIN.getCondition().trim().equalsIgnoreCase("") && !scanVIN.getCondition().trim().equalsIgnoreCase("anyType{}"))
            {
                edtCondition.setText(scanVIN.getCondition());
                Helper.nonEditableEditText(edtCondition);
            }
        }

        if (scanVIN.getCondition() != null || scanVIN.getVariantstr() != null || scanVIN.getYear() != null ||
                scanVIN.getExtras() != null || scanVIN.getMilage() != null)
        {
            btnSaveForLater.setVisibility(View.GONE);
        } else
        {
            btnSaveForLater.setVisibility(View.VISIBLE);
        }

        tvVinMake.setText(scanVIN.getMake());
        tvVinColor.setText(scanVIN.getColour());
        if (scanVIN.isHasModel())
        {
            etVinModel.setText(scanVIN.getModel());
            selectedModelId = scanVIN.getModelId();
            etVinModel.setBackground(null);
            etVinModel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            etVinModel.setPadding(0, 0, 0, 0);
            etVinModel.setOnClickListener(null);
        } else
        {
            etVinModel.setOnClickListener(this);
        }
        tvVinExpires.setText(scanVIN.getDate());
        tvVinLicense.setText(scanVIN.getLicence());
        tvVinRegistration.setText(scanVIN.getRegistration());
        tvVinEngine.setText(scanVIN.getEngineNumber());
        tvVinNumber.setText(scanVIN.getVIN());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.etVinModel:
                showModelListPopUp(v);
                break;

            case R.id.edtYear:
                if (etVinModel.getText().toString().trim().equals(getString(R.string.select_model)))
                {
                    Helper.showToast("Please select model", getActivity());
                    return;
                }
                if (scanVIN.isHasModel())
                {
                    showToPopUp(v, scanVIN.getMinYear(), scanVIN.getMaxYear());
                } else
                {
                    showToPopUp(v, scanVIN.getModels().get(selectedPosition).getMinYear(),
                            scanVIN.getModels().get(selectedPosition).getMaxYear());
                }
                break;

            case R.id.edtVariant:
                if (edtYear.getText().toString().trim().equalsIgnoreCase(getString(R.string.select_year)))
                {
                    Helper.showToast("Please select year", getActivity());
                    return;
                }
                if (variantList != null)
                {
                    getVariantList();
                }
                break;

            case R.id.edtCondition:
                Helper.showDropDown(edtCondition, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, conditionType), new OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        edtCondition.setText(conditionType[position]);
                        //selectedVariantId = position;
                    }
                });
                break;

            case R.id.btnSynopsisSummary:
                if (etVinModel.getText().toString().equals("") || etVinModel.getText().toString().equals("Select Model"))
                {
                    Helper.showToast("Please select model", getActivity());
                    return;
                }
                if (edtYear.getText().toString().equals("") || edtYear.getText().toString().equals("Select Year"))
                {
                    Helper.showToast("Please select year", getActivity());
                    return;
                }
                if (edtVariant.getText().toString().equals("") || edtVariant.getText().toString().equals("Select Variant"))
                {
                    Helper.showToast("Please select variant", getActivity());
                    return;
                }
                if (edtMilage.getText().toString().equals(""))
                {
                    Helper.showToast(getString(R.string.kilometers_warning), getActivity());
                    return;
                }
                GetSynopsisXml();
                break;

            case R.id.btnVINVerify:
                if (TextUtils.isEmpty(etVinModel.getText().toString().trim()))
                {
                    Helper.showToast(getString(R.string.select_model1), getActivity());
                    return;
                }
                if (TextUtils.isEmpty(edtYear.getText().toString().trim()))
                {
                    Helper.showToast(getString(R.string.select_year1), getActivity());
                    return;
                }
                if (TextUtils.isEmpty(edtVariant.getText().toString().trim()))
                {
                    Helper.showToast(getString(R.string.select_varient1), getActivity());
                    return;
                }
                if (edtMilage.getText().toString().equals(""))
                {
                    Helper.showToast(getString(R.string.kilometers_warning), getActivity());
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromSum", false);
                VehicleDetails vehicleDetails = new VehicleDetails();
                vehicleDetails.setYear(Integer.parseInt(edtYear.getText().toString().trim()));
                vehicleDetails.setFriendlyName(tvVinMake.getText().toString().trim() + " " + etVinModel.getText().toString().trim() + " " +
                        edtVariant.getText().toString().trim());
                vehicleDetails.setVin(tvVinNumber.getText().toString().trim());
                vehicleDetails.setRegistration(scanVIN.getRegistration());
                vehicleDetails.setExtras(edtExtrasCost.getText().toString().trim());
                vehicleDetails.setCondition(edtCondition.getText().toString().trim());
                vehicleDetails.setMileage(TextUtils.isEmpty(edtMilage.getText().toString().trim()) == true ? 0 : Integer.parseInt(edtMilage.getText().toString().trim()));
                bundle.putParcelable("summaryObejct", vehicleDetails);
                VerifyVINFragment verifyVINFragment = new VerifyVINFragment();
                verifyVINFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Container, verifyVINFragment).addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.btnDiscard:
                CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.discard_warning), new DialogListener()
                {
                    @Override
                    public void onButtonClicked(int type)
                    {
                        if (type == Dialog.BUTTON_POSITIVE)
                        {
                            if (scanVIN != null)
                            {
                                if (scanVIN.getId() == 0)
                                {
                                    getFragmentManager().popBackStack();
                                } else
                                {
                                    discard();
                                }
                            }
                        }
                    }
                });

                break;

            case R.id.btnSaveForLater:
                if (TextUtils.isEmpty(etVinModel.getText().toString().trim()))
                {
                    Helper.showToast(getString(R.string.select_model1), getActivity());
                    return;
                }

                if (TextUtils.isEmpty(edtYear.getText().toString().trim()))
                {
                    Helper.showToast(getString(R.string.select_year1), getActivity());
                    return;
                }

                if (TextUtils.isEmpty(edtVariant.getText().toString().trim()))
                {
                    Helper.showToast(getString(R.string.select_varient1), getActivity());
                    return;
                }

                if (edtMilage.getText().toString().equals(""))
                {
                    Helper.showToast(getString(R.string.kilometers_warning), getActivity());
                    return;
                }

                CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.do_you_want_to_save_this_scan), new DialogListener()
                {
                    @Override
                    public void onButtonClicked(int type)
                    {
                        if (type == Dialog.BUTTON_POSITIVE)
                        {
                            saveVINScan();
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Scan VIN");
    }

    private void showToPopUp(View v, int minYear, int maxYear)
    {
        //int defaultYear = 1990;

        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        cal.set(Calendar.YEAR, minYear);
        if (maxYear > nowYear)
            maxYear = nowYear;
        int subtraction = maxYear - minYear;
        final List<String> years = new ArrayList<String>();
        int i = 0;
        cal.set(Calendar.YEAR, minYear);
        while (i <= subtraction)
        {
            years.add(cal.get(Calendar.YEAR) + i + "");
            i++;
        }
        Collections.reverse(years);
        final EditText ed = (EditText) v;
        final String lastYear = ed.getText().toString().trim();

        Helper.showDropDownYear(v, new ArrayAdapter<String>(getActivity(), R.layout.list_item_text3, R.id.tvText, years), new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ed.setText(years.get(position) + "");
                if (!lastYear.equals(years.get(position) + ""))
                {
                    ed.setText(years.get(position) + "");
                    edtVariant.setText("");
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

    private void showModelListPopUp(View view)
    {
        if (!scanVIN.getModels().isEmpty())
        {
            final ArrayAdapter<Model> adapter = new ArrayAdapter<Model>(getActivity(), R.layout.list_item_text2, scanVIN.getModels());
            Helper.showDropDown(view, adapter, new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    scanVIN.setModel(adapter.getItem(position).toString());
                    etVinModel.setText(adapter.getItem(position).toString());
                    selectedPosition = position;
                    selectedModelId = adapter.getItem(position).getId();
                }
            });
        } else
        {
            Helper.showToast(getString(R.string.no_model), getActivity());
        }
    }

    private void getVariantList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("year", Integer.parseInt(edtYear.getText().toString().trim()), Integer.class));
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
                            Helper.showDropDown(edtVariant, variantAdapter, new OnItemClickListener()
                            {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                                {
                                    edtVariant.setText(variantList.get(position).getVariantName() + "");
                                    selectedVariantId = variantList.get(position).getVariantId();
                                    if (selectedVariantId != 0)
                                        variant = variantList.get(position);
                                }
                            });
                        } else
                        {
                            CustomDialogManager.showOkDialog(getActivity(), "No Variant(s) found");
                        }
                    }
                }
            }).execute();
        } else
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
            parameterList.add(new Parameter("year", Integer.parseInt(edtYear.getText().toString().trim()), Integer.class));
            parameterList.add(new Parameter("makeId", scanVIN.getMakeId() == 0 ? selectedMakeId : scanVIN.getMakeId(), Integer.class));
            parameterList.add(new Parameter("modelId", scanVIN.getModelId() == 0 ? selectedModelId : scanVIN.getModelId(), Integer.class));
            parameterList.add(new Parameter("variantId", selectedVariantId, Integer.class));
            parameterList.add(new Parameter("VIN", tvVinNumber.getText().toString().trim(), String.class));
            parameterList.add(new Parameter("kilometers", edtMilage.getText().toString().trim(), String.class));
            parameterList.add(new Parameter("extras", edtExtrasCost.getText().toString().trim(), String.class));
            parameterList.add(new Parameter("condition", edtCondition.getText().toString().trim(), String.class));
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
                    if (details != null)
                    {
                        summaryFragment = new SummaryFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("summaryObejct", details);
                        summaryFragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.Container, summaryFragment).addToBackStack(null);
                        fragmentTransaction.commit();
                    } else
                    {
                        CustomDialogManager.showOkDialog(getActivity(), "Error while loading data. Please try again later");
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void discard()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameters.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameters.add(new Parameter("SavedScanID", scanVIN.getId(), String.class));
            DataInObject dataInObject = new DataInObject();
            dataInObject.setMethodname("RemoveVINScan");
            dataInObject.setNamespace(Constants.TEMP_URI_NAMESPACE);
            dataInObject.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/RemoveVINScan");
            dataInObject.setUrl(Constants.STOCK_WEBSERVICE_URL);
            dataInObject.setParameterList(parameters);
            new WebServiceTask(getActivity(), dataInObject, true, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object response)
                {
                    if (response != null)
                    {
                        Helper.Log("onTaskComplete ", "" + response.toString());

                        if (response instanceof SoapFault)
                        {
                            // error
                        } else
                        {
                            // not fault
                            try
                            {
                                CustomDialogManager.showOkDialog(getActivity(), "Vehicle is discarded successfully", new DialogListener()
                                {
                                    @Override
                                    public void onButtonClicked(int type)
                                    {
                                        getFragmentManager().popBackStack();
                                    }
                                });
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    } else
                    {
                        CustomDialogManager.showOkDialog(getActivity(), "Unable to remove saved vin scan");
                    }
                }
            }).execute();
        } else
            HelperHttp.showNoInternetDialog(getActivity());
    }

    private void saveVINScan()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameters.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameters.add(new Parameter("vin", scanVIN.getVIN(), String.class));
            parameters.add(new Parameter("registration", scanVIN.getRegistration(), String.class));
            parameters.add(new Parameter("shape", scanVIN.getShape(), String.class));
            parameters.add(new Parameter("year", edtYear.getText().toString().trim(), String.class));
            parameters.add(new Parameter("make", scanVIN.getMake(), String.class));
            parameters.add(new Parameter("model", scanVIN.getModel(), String.class));
            parameters.add(new Parameter("variant", edtVariant.getText().toString().trim(), String.class));
            parameters.add(new Parameter("variantID", selectedVariantId, Integer.class));
            parameters.add(new Parameter("colour", scanVIN.getColour(), String.class));
            parameters.add(new Parameter("engineNo", scanVIN.getEngineNumber(), String.class));
            parameters.add(new Parameter("kilometers", edtMilage.getText().toString().trim(), String.class));
            parameters.add(new Parameter("extras", edtExtrasCost.getText().toString().trim(), String.class));
            parameters.add(new Parameter("condition", edtCondition.getText().toString().trim(), String.class));
            parameters.add(new Parameter("licenseExpiry", scanVIN.getDate(), String.class));

            DataInObject dataInObject = new DataInObject();
            dataInObject.setMethodname("SaveVINScan");
            dataInObject.setNamespace(Constants.TEMP_URI_NAMESPACE);
            dataInObject.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/SaveVINScan");
            dataInObject.setUrl(Constants.STOCK_WEBSERVICE_URL);
            dataInObject.setParameterList(parameters);
            new WebServiceTask(getActivity(), dataInObject, false, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object response)
                {
                    hideProgressDialog();
                    if (response != null)
                    {
                        Helper.Log("onTaskComplete ", "" + response);

                        if (response instanceof SoapFault)
                        {
                        } else
                        {
                            // not fault
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.getString("status").equals("error"))
                                {
                                    CustomDialogManager.showOkDialog(getActivity(), jsonObject.getString("message"), new DialogListener()
                                    {
                                        @Override
                                        public void onButtonClicked(int type)
                                        {
                                            getFragmentManager().popBackStack();
                                        }
                                    });
                                } else
                                {
                                    CustomDialogManager.showOkDialog(getActivity(), jsonObject.getString("message"), new DialogListener()
                                    {
                                        @Override
                                        public void onButtonClicked(int type)
                                        {
                                            getFragmentManager().popBackStack();
                                        }
                                    });
                                }

                            } catch (Exception e)
                            {
                                e.printStackTrace();
                                hideProgressDialog();
                                if (response.toString().contains("OK"))
                                {
                                    CustomDialogManager.showOkDialog(getActivity(), "Scan saved successfully", new DialogListener()
                                    {
                                        @Override
                                        public void onButtonClicked(int type)
                                        {
                                            getFragmentManager().popBackStack();
                                        }
                                    });
                                }
                            }
                        }
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data));
                    }
                }
            }).execute();
        } else
            HelperHttp.showNoInternetDialog(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (summaryFragment != null)
        {
            summaryFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
