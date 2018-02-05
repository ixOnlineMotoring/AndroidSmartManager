package com.nw.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.nw.adapters.NewVariantAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Model;
import com.nw.model.Parameter;
import com.nw.model.ScanVIN;
import com.nw.model.SmartObject;
import com.nw.model.Variant;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class VINLookupFragment extends BaseFragement implements OnClickListener
{

    EditText edMake, edModel, edYear, edVariant, edStock, edColor, edRegistration, edengine, edvin, edExpireDate;
    ScanVIN scanVIN;
    LinearLayout addLayout, updateLayout;
    Button btnDiscard, btnSaveLater, btnAddToStock, btnUpdate;
    TableRow tblStockNo;
    String errorMessage = "", message = "";
    ArrayList<Variant> variants;
    int minYear, maxYear;
    Variant variant;
    VariantDetails variantDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_vin_lookup, container, false);
        setHasOptionsMenu(true);
        init(view);
        hideKeyboard(view);
        return view;
    }

    private void init(View view)
    {
        edMake = (EditText) view.findViewById(R.id.edMake);
        edModel = (EditText) view.findViewById(R.id.edModel);
        edYear = (EditText) view.findViewById(R.id.edYear);
        edVariant = (EditText) view.findViewById(R.id.edVariant);
        edStock = (EditText) view.findViewById(R.id.edStock);
        edColor = (EditText) view.findViewById(R.id.edColor);
        edRegistration = (EditText) view.findViewById(R.id.edRegistration);
        edengine = (EditText) view.findViewById(R.id.edengine);
        edvin = (EditText) view.findViewById(R.id.edvin);
        edExpireDate = (EditText) view.findViewById(R.id.edExpireDate);

        tblStockNo = (TableRow) view.findViewById(R.id.tblStockNo);

        addLayout = (LinearLayout) view.findViewById(R.id.addLayout);
        updateLayout = (LinearLayout) view.findViewById(R.id.updateLayout);

        btnDiscard = (Button) view.findViewById(R.id.btnDiscard);
        btnSaveLater = (Button) view.findViewById(R.id.btnSaveLater);
        btnAddToStock = (Button) view.findViewById(R.id.btnAddToStock);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);

        if (getArguments() != null)
            scanVIN = getArguments().getParcelable("data");

        variants = new ArrayList<Variant>();
        // set data
        if (scanVIN != null)
        {
            edMake.setText("" + scanVIN.getMake());
            edColor.setText("" + scanVIN.getColour());
            edRegistration.setText("" + scanVIN.getRegistration());
            edengine.setText("" + scanVIN.getEngineNumber());
            edExpireDate.setText("" + scanVIN.getDate());
            edvin.setText("" + scanVIN.getVIN());
            if(scanVIN.getVariantstr() == null)
            {
                edVariant.setText(getResources().getString(R.string.select_varient_));
            }else
            {
                edVariant.setText("" + scanVIN.getVariantstr());
            }

            if (scanVIN.getYear() != null)
            {
                if (!scanVIN.getYear().equalsIgnoreCase("Year?"))
                {
                    edYear.setText("" + scanVIN.getYear());
                    getVariantList(Integer.parseInt(scanVIN.getYear()));
                }
            }

            if (scanVIN.isExisting())
            {
                updateLayout.setVisibility(View.VISIBLE);
                addLayout.setVisibility(View.GONE);
                message = getString(R.string.stock_updated_successfully);
                errorMessage = getString(R.string.stock_details_not_saved);
            } else
            {
                addLayout.setVisibility(View.VISIBLE);
                updateLayout.setVisibility(View.GONE);
                message = getString(R.string.stock_saved_successfully);
                errorMessage = getString(R.string.stock_details_not_saved);
            }
            if (scanVIN.getStocks().isEmpty())
                tblStockNo.setVisibility(View.GONE);

            if (scanVIN.isHasModel())
                edModel.setText("" + scanVIN.getModel());
            else
            {
                edModel.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.register_arrow), null);
                edModel.setOnClickListener(this);
            }
        }
        btnDiscard.setOnClickListener(this);
        btnAddToStock.setOnClickListener(this);
        btnSaveLater.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDiscard.setOnClickListener(this);

        edVariant.setOnClickListener(this);
        edYear.setOnClickListener(this);
        edStock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ivAddImage:
                if (DataManager.getInstance().isClientSetUptoUploadVideo)
                {
                    Helper.getVideoFromGalleryOrCamera(getActivity());
                } else
                {
                    CustomDialogManager.showErrorDialogEmail(getActivity(),
                            "Sorry, you have not been activated for this service yet. Please contact support@ix.co.za to get setup.");
                }

                break;

            case R.id.edYear:
                if (!scanVIN.isHasModel())
                {
                    if (minYear != 0 && maxYear != 0)
                        showSelectYearPopup(v, minYear, maxYear);
                    else
                        Helper.showToast(getString(R.string.select_model1), getActivity());
                } else
                    showSelectYearPopup(v, scanVIN.getMinYear(), scanVIN.getMaxYear());
                break;
            case R.id.edVariant:
                if (TextUtils.isEmpty(edModel.getText()))
                {
                    Helper.showToast(getString(R.string.select_model1), getActivity());
                    return;
                } else if (TextUtils.isEmpty(edYear.getText()))
                {
                    Helper.showToast(getString(R.string.select_year1), getActivity());
                    return;
                }
                showListPopUp(v);
                break;
            case R.id.edStock:
                showStockListPopUp(v);
                break;

            case R.id.edModel:
                showModelListPopUp(v);
                break;

            case R.id.btnUpdate:
                updateVINViaObj();
                break;
            case R.id.btnAddToStock:
                if (TextUtils.isEmpty(edModel.getText()))
                {
                    Helper.showToast(getString(R.string.select_model1), getActivity());
                    return;
                }
                if (TextUtils.isEmpty(edYear.getText()))
                {
                    Helper.showToast(getString(R.string.select_year1), getActivity());
                    return;
                } else if (TextUtils.isEmpty(edVariant.getText()))
                {
                    Helper.showToast(getString(R.string.select_varient1), getActivity());
                    return;
                }
                variantDetails = new VariantDetails();
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", scanVIN);
                bundle.putBoolean("update", false);
                bundle.putString("fromFragment", "Add To Stock");
                variantDetails.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, variantDetails).addToBackStack(null).commit();
                break;
            case R.id.btnSaveLater:

                if (TextUtils.isEmpty(edModel.getText().toString().trim()))
                {
                    Helper.showToast(getString(R.string.select_model1), getActivity());
                    return;
                }

                if (TextUtils.isEmpty(edYear.getText().toString().trim()))
                {
                    Helper.showToast(getString(R.string.select_year1), getActivity());
                    return;
                }

                if (TextUtils.isEmpty(edVariant.getText().toString().trim()))
                {
                    Helper.showToast(getString(R.string.select_varient1), getActivity());
                    return;
                }
                if (tblStockNo.getVisibility() == View.VISIBLE)
                {
                    if (TextUtils.isEmpty(edStock.getText().toString().trim()))
                    {
                        Helper.showToast(getString(R.string.select_stock1), getActivity());
                        return;
                    }
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
            case R.id.btnDiscard:
                if (getArguments().containsKey("fromScan"))
                {
                    if (getArguments().getBoolean("fromScan"))
                        getFragmentManager().popBackStack();
                    else
                        showDiscardAlert();
                } else
                    showDiscardAlert();
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("VIN Lookup");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                hideKeyboard();
                getActivity().getFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showListPopUp(View view)
    {
        if (!variants.isEmpty())
        {

            final EditText ed = (EditText) view;
            final NewVariantAdapter variAdapter = new NewVariantAdapter(getActivity(), R.layout.list_item_variant, variants);
            Helper.showDropDown(view, variAdapter, new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    variant = variAdapter.getItem(position);
                    variant.setYear(edYear.getText().toString());
                    variant.setDetails("");
                    scanVIN.setVariant(variant);
                    ed.setText(variAdapter.getItem(position).getVariantName());
                }
            });
        } else
        {
            Helper.showToast(getString(R.string.no_variant), getActivity());
        }
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
                    edModel.setText(adapter.getItem(position).toString());
                    scanVIN.setModelId(adapter.getItem(position).getId());
                    minYear = adapter.getItem(position).getMinYear();
                    maxYear = adapter.getItem(position).getMaxYear();

                    if (maxYear > Calendar.getInstance().get(Calendar.YEAR))
                        maxYear = Calendar.getInstance().get(Calendar.YEAR);

                    // reset year and variant
                    edYear.setText("");
                    edVariant.setText("");

                }
            });
        } else
        {
            Helper.showToast(getString(R.string.no_model), getActivity());
        }
    }

    private void getVariantList(int year)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("year", year, Integer.class));
            parameterList.add(new Parameter("modelID", scanVIN.getModelId(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListVariantsXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListVariantsXML");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);
            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {
                    if (result != null)
                    {
                        Helper.Log("onTaskComplete", "" + result);
                        if (variants != null)
                            variants.clear();
                        variants = new ArrayList<Variant>();
                        try
                        {
                            SoapObject outer = (SoapObject) result;
                            SoapObject inner = (SoapObject) outer.getPropertySafely("Variants");
                            for (int i = 0; i < inner.getPropertyCount(); i++)
                            {
                                SoapObject variantObj = (SoapObject) inner.getProperty(i);
                                variants.add(new Variant(Integer.parseInt(variantObj.getPropertySafelyAsString("variantID", "0")), variantObj.getPropertySafelyAsString("meadCode", "0"),
                                        variantObj.getPropertySafelyAsString("friendlyName", "-"), variantObj.getPropertySafelyAsString("variantName", "-"),
                                        variantObj.getPropertySafelyAsString("MinDate", "-"), variantObj.getPropertySafelyAsString("MaxDate", "-")));

                            }

                            for (int j = 0; j < variants.size(); j++)
                            {
                                if (variants.get(j).getVariantName().equalsIgnoreCase(scanVIN.getVariantstr()))
                                {
                                    variant = variants.get(j);
                                    variant.setYear(edYear.getText().toString());
                                    variant.setDetails("");
                                    scanVIN.setVariant(variant);
                                }
                            }

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    } else
                    {
                        if (variants != null)
                            variants.clear();
                        variants = new ArrayList<Variant>();
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    private void showStockListPopUp(View view)
    {
        if (!scanVIN.getStocks().isEmpty())
        {
            final EditText ed = (EditText) view;
            final ArrayAdapter<SmartObject> adapter = new ArrayAdapter<SmartObject>(getActivity(), R.layout.list_item_text2, scanVIN.getStocks());

            Helper.showDropDown(view, adapter, new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    scanVIN.setStock(adapter.getItem(position));
                    ed.setText(adapter.getItem(position).toString());
                }
            });
        } else
            Helper.showToast(getString(R.string.no_stock), getActivity());
    }

    private void showSelectYearPopup(View view, int minYear, int maxYear)
    {
        Helper.Log("YEAR", "MIN" + minYear + "MAX" + maxYear);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, minYear);
        final List<String> years = new ArrayList<String>();

        if (maxYear == 0)
            maxYear = Calendar.getInstance().get(Calendar.YEAR);

        int differentYear = (maxYear - minYear);
        for (int i = 0; i <= differentYear; i++)
        {
            years.add(cal.get(Calendar.YEAR) + "");
            cal.set(Calendar.YEAR, (minYear + i + 1));
        }
        final EditText ed = (EditText) view;
        Collections.sort(years, Collections.reverseOrder());

        Helper.showDropDown(view, new ArrayAdapter<String>(getActivity(), R.layout.list_item_text2, years), new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ed.setText(years.get(position) + "");
                edVariant.setText("");
                if (variant != null)
                    variant.setYear(years.get(position));

                scanVIN.setYear(years.get(position));
                getVariantList(Integer.parseInt(ed.getText().toString()));
            }
        });

    }

    private void updateVINViaObj()
    {

        if (TextUtils.isEmpty(edModel.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.select_model1), getActivity());
            return;
        }

        if (TextUtils.isEmpty(edYear.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.select_year1), getActivity());
            return;
        }

        if (TextUtils.isEmpty(edVariant.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.select_varient1), getActivity());
            return;
        }

        if (tblStockNo.getVisibility() == View.VISIBLE)
        {
            if (TextUtils.isEmpty(edStock.getText().toString().trim()))
            {
                Helper.showToast(getString(R.string.select_stock1), getActivity());
                return;
            }
        }

        CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.this_vehicle_already_in_stock_would_you_like_to_update_it), new DialogListener()
        {
            @Override
            public void onButtonClicked(int type)
            {
                if (type == Dialog.BUTTON_POSITIVE)
                {
                    variantDetails = new VariantDetails();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data", scanVIN);
                    bundle.putBoolean("update", true);
                    bundle.putString("fromFragment", "VINLookup");
                    variantDetails.setArguments(bundle);
                   getFragmentManager().beginTransaction().replace(R.id.Container, variantDetails).addToBackStack(null).commit();
                }
            }
        });
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
            parameters.add(new Parameter("year", edYear.getText().toString().trim(), String.class));
            parameters.add(new Parameter("make", scanVIN.getMake(), String.class));
            parameters.add(new Parameter("model", scanVIN.getModel(), String.class));
            parameters.add(new Parameter("variant", scanVIN.getVariant().getVariantName(), String.class));
            parameters.add(new Parameter("variantID", scanVIN.getVariant().getVariantId(), String.class));
            parameters.add(new Parameter("colour", scanVIN.getColour(), String.class));
            parameters.add(new Parameter("engineNo", scanVIN.getEngineNumber(), String.class));

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
                                    showErrorDialog(jsonObject.getString("message"));
                                else
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
                                    CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
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
                        CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                    }
                }
            }).execute();
        } else
            HelperHttp.showNoInternetDialog(getActivity());
    }

    private void showDiscardAlert()
    {
        CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.discard_warning), new DialogListener()
        {
            @Override
            public void onButtonClicked(int type)
            {
                if (type == Dialog.BUTTON_POSITIVE)
                {
                    discard();
                }
            }
        });
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
                        CustomDialogManager.showOkDialog(getActivity(), errorMessage);
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
        if (variantDetails != null)
            variantDetails.onActivityResult(requestCode, resultCode, data);
    }
}
