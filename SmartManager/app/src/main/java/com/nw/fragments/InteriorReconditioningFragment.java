package com.nw.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.nw.adapters.InteriorReconditioningAdapter;
import com.nw.adapters.OptionsConditionAdapter;
import com.nw.interfaces.DialogInputListener;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.DoAppraisalInputListener;
import com.nw.model.Conditioning;
import com.nw.model.DataInObject;
import com.nw.model.InteriorReconditioning;
import com.nw.model.OptionsConditions;
import com.nw.model.Parameter;
import com.nw.model.VehicleDetails;
import com.nw.model.VehicleExtras;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class InteriorReconditioningFragment extends BaseFragement implements View.OnClickListener
{
    StaticListView listview;
    TextView tv_total_value;
    EditText edtComments;
    Button btnSave, btnAdd;

    ArrayList<InteriorReconditioning> interiorReconditioningArrayList = new ArrayList<InteriorReconditioning>();
    InteriorReconditioningAdapter interiorReconditioningAdapter;

    VehicleDetails vehicleDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_interior_conditioning, container, false);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            vehicleDetails = getArguments().getParcelable("vehicleDetails");
        }
        initialise(view);
        return view;
    }

    private void initialise(View view)
    {
        if (interiorReconditioningArrayList == null)
        {
            interiorReconditioningArrayList = new ArrayList<InteriorReconditioning>();
        }

        // Static LIstview
        listview = (StaticListView) view.findViewById(R.id.listview);

        // TextView
        tv_total_value = (TextView) view.findViewById(R.id.tv_total_value);

        // EditText
        edtComments = (EditText) view.findViewById(R.id.edtComments);

        //Button
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        //ImageView
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

       GetInteriorReconditioning();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Interior Reconditioning");
        //getActivity().getActionBar().setSubtitle(null);
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

    private void GetInteriorReconditioning()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("UserHash", DataManager.getInstance().user.getUserHash(), String.class));
            //   parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("AppraisalId", vehicleDetails.getStr_AppraisalId(), Integer.class));
            parameterList.add(new Parameter("VinNumber", vehicleDetails.getVin(), Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadInteriorReconditioning");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadInteriorReconditioning");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            //Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("soap response", result.toString());
                        hideProgressDialog();
                        Helper.Log("Response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        SoapObject response = (SoapObject) outer.getPropertySafely("InteriorReconditioning");
                        SoapObject responsitems = (SoapObject) response.getPropertySafely("Items");
                        if (response.getPropertySafelyAsString("Comments", "").equalsIgnoreCase("anyType{}"))
                        {
                            edtComments.setText("");
                        } else
                        {
                            edtComments.setText(response.getPropertySafelyAsString("Comments", ""));
                        }

                        interiorReconditioningArrayList = new ArrayList<InteriorReconditioning>();
                        for (int i = 0; i < responsitems.getPropertyCount(); i++)
                        {
                            SoapObject conditionObj = (SoapObject) responsitems.getProperty(i);
                            InteriorReconditioning interiorReconditioning = new InteriorReconditioning();
                            interiorReconditioning.setInteriorReconditioningValueID(conditionObj.getPropertySafelyAsString("InteriorReconditioningValueID", ""));
                            interiorReconditioning.setReconditioningType(conditionObj.getPropertySafelyAsString("ReconditioningType", ""));
                            interiorReconditioning.setReconditioningTypeID(conditionObj.getPropertySafelyAsString("ReconditioningTypeID", ""));
                            interiorReconditioning.setCustomType(conditionObj.getPropertySafelyAsString("CustomType", ""));
                            double d = Double.parseDouble(conditionObj.getPropertySafelyAsString("Value", "0.0"));
                            interiorReconditioning.setValue((int) d);

                            interiorReconditioning.setActive(Boolean.parseBoolean(conditionObj.getPropertySafelyAsString("IsActive", "true")));
                            interiorReconditioning.setExtraValue(false);
                            interiorReconditioningArrayList.add(interiorReconditioning);
                        }
                        for (int i = 0; i < 1; i++)
                        {
                            InteriorReconditioning interiorReconditioning = new InteriorReconditioning();
                            interiorReconditioning.setInteriorReconditioningValueID("");
                            interiorReconditioning.setReconditioningType("");
                            interiorReconditioning.setReconditioningTypeID("");
                            interiorReconditioning.setCustomType("");
                            interiorReconditioning.setValue(0);
                            interiorReconditioning.setActive(false);
                            interiorReconditioning.setExtraValue(true);
                            interiorReconditioningArrayList.add(interiorReconditioning);
                        }
                        interiorReconditioningAdapter = new InteriorReconditioningAdapter(getContext(), R.layout.item_condition_options, interiorReconditioningArrayList, new DoAppraisalInputListener()
                        {
                            @Override
                            public void onButtonClicked(boolean isTitle, int position, String message)
                            {
                                if (isTitle)
                                {
                                    // To change the title if selected
                                    interiorReconditioningArrayList.get(position).setReconditioningType(message);
                                    interiorReconditioningArrayList.get(position).setCustomType(message);
                                } else
                                {
                                    if (!message.equalsIgnoreCase("checkbox") && !message.equalsIgnoreCase("") && !message.equalsIgnoreCase("delete"))
                                    {
                                        interiorReconditioningArrayList.get(position).setValue((int) Double.parseDouble(message.replaceAll("R", "")));
                                    } else if (message.equalsIgnoreCase("checkbox"))
                                    {
                                        if (interiorReconditioningArrayList.get(position).isActive())
                                        {
                                            interiorReconditioningArrayList.get(position).setActive(false);
                                        } else
                                        {
                                            interiorReconditioningArrayList.get(position).setActive(true);
                                        }
                                    } else if (message.equalsIgnoreCase("delete"))
                                    {
                                        final int temp_postion = position;
                                        CustomDialogManager.showOkCancelDialog(getContext(), getContext().getString(R.string.are_you_sure_you_want_delete), new DialogListener()
                                        {

                                            @Override
                                            public void onButtonClicked(int type)
                                            {
                                                if (type == Dialog.BUTTON_POSITIVE)
                                                {
                                                    interiorReconditioningArrayList.remove(temp_postion);
                                                    interiorReconditioningAdapter.notifyDataSetChanged();
                                                    setTotalValue();
                                                }
                                            }
                                        });
                                    } else
                                    {
                                        interiorReconditioningArrayList.get(position).setValue(0);

                                        // To set the check box un-tick if value is empty
                                        if (interiorReconditioningArrayList.get(position).isActive())
                                        {
                                            interiorReconditioningArrayList.get(position).setActive(false);
                                            interiorReconditioningAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                setTotalValue();
                            }
                        });
                        listview.setAdapter(interiorReconditioningAdapter);
                        setTotalValue();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                // getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void setTotalValue()
    {
        if (interiorReconditioningArrayList != null)
        {
            int total_value = 0;
            for (int i = 0; i < interiorReconditioningArrayList.size(); i++)
            {
                if (interiorReconditioningArrayList.get(i).isActive())
                    total_value += interiorReconditioningArrayList.get(i).getValue();
            }
            tv_total_value.setText(Helper.formatPrice("" + total_value));
        }
    }

    private void saveInteriorRecondtioning()
    {
        VollyCustomRequest request;
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");

            soapMessage.append("<SaveInteriorReconditioning xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<InteriorReconditioningXml>");
            soapMessage.append("<InteriorReconditioning>");
            soapMessage.append("<Items>");

            for (int i = 0; i < interiorReconditioningArrayList.size(); i++)
            {
                if (interiorReconditioningArrayList.get(i).getExtraValue())
                {
                    if (!interiorReconditioningArrayList.get(i).getReconditioningType().equalsIgnoreCase(""))
                    {
                        soapMessage.append("<Interior>");
                        soapMessage.append("<InteriorReconditioningValueID>" + interiorReconditioningArrayList.get(i).getInteriorReconditioningValueID() + "</InteriorReconditioningValueID>");
                        soapMessage.append("<ReconditioningTypeID>" + interiorReconditioningArrayList.get(i).getReconditioningTypeID() + "</ReconditioningTypeID>");
                        soapMessage.append("<CustomType>" + interiorReconditioningArrayList.get(i).getCustomType() + "</CustomType>");
                        soapMessage.append("<Value>" + interiorReconditioningArrayList.get(i).getValue() + "</Value>");
                        soapMessage.append("<IsActive>" + interiorReconditioningArrayList.get(i).isActive() + "</IsActive>");
                        soapMessage.append("</Interior>");
                    }
                } else
                {
                    soapMessage.append("<Interior>");
                    soapMessage.append("<InteriorReconditioningValueID>" + interiorReconditioningArrayList.get(i).getInteriorReconditioningValueID() + "</InteriorReconditioningValueID>");
                    soapMessage.append("<ReconditioningTypeID>" + interiorReconditioningArrayList.get(i).getReconditioningTypeID() + "</ReconditioningTypeID>");
                    soapMessage.append("<CustomType>" + interiorReconditioningArrayList.get(i).getCustomType() + "</CustomType>");
                    soapMessage.append("<Value>" + interiorReconditioningArrayList.get(i).getValue() + "</Value>");
                    soapMessage.append("<IsActive>" + interiorReconditioningArrayList.get(i).isActive() + "</IsActive>");
                    soapMessage.append("</Interior>");
                }
            }

            soapMessage.append("</Items>");
            soapMessage.append("<RootInfo>");
            soapMessage.append("<InteriorReconditioningID>" + "1" + "</InteriorReconditioningID>");
            soapMessage.append("<AppraisalId>" + vehicleDetails.getStr_AppraisalId() + "</AppraisalId>");
            soapMessage.append("<VinNumber>" + vehicleDetails.getVin() + "</VinNumber>");
            soapMessage.append("<ClientId>" + DataManager.getInstance().user.getDefaultClient().getId()+ "</ClientId>");
          //  soapMessage.append("<ClientId>" + 1 + "</ClientId>");
            soapMessage.append("<Comments>" + edtComments.getText().toString().trim() + "</Comments>");
            soapMessage.append("</RootInfo>");


            soapMessage.append("</InteriorReconditioning>");
            soapMessage.append("</InteriorReconditioningXml>");
            soapMessage.append("</SaveInteriorReconditioning>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            Helper.Log("SaveVehicle request", "" + soapMessage);

            VollyResponseListener listener = new VollyResponseListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    hideProgressDialog();
                    VolleyLog.e("Error: ", error.toString());
                }

                @Override
                public void onResponse(String response)
                {
                    hideProgressDialog();
                    if (response == null)
                    {
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_while_saving_Incondition_data), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                //getFragmentManager().popBackStack();
                            }
                        });
                        return;
                    } else
                    {
                        final String PassOrFailed = ParserManager.parsetokenChecker(response, "PassOrFailed");
                        String message = "";
                        if (PassOrFailed.equalsIgnoreCase("true"))
                        {
                            message = "InteriorReCondition save successfully";
                        } else
                        {
                            message = "Error while saveing InteriorReconditions";
                        }

                        CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                if (PassOrFailed.equalsIgnoreCase("true"))
                                    GetInteriorReconditioning();
                            }
                        });
                    }
                }
            };
            request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IStockService/SaveInteriorReconditioning", listener);
            try
            {
                request.init();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnSave:
                saveInteriorRecondtioning();
                break;
            case R.id.btnAdd:
                InteriorReconditioning interiorReconditioning = new InteriorReconditioning();
                interiorReconditioning.setInteriorReconditioningValueID("");
                interiorReconditioning.setReconditioningType("");
                interiorReconditioning.setReconditioningTypeID("");
                interiorReconditioning.setCustomType("");
                interiorReconditioning.setValue(0);
                interiorReconditioning.setActive(false);
                interiorReconditioning.setExtraValue(true);
                interiorReconditioningArrayList.add(interiorReconditioning);
                interiorReconditioningAdapter.notifyDataSetChanged();
                break;
        }
    }
}