package com.nw.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.nw.adapters.OptionsConditionAdapter;
import com.nw.adapters.VehicleExtrasAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.DoAppraisalInputListener;
import com.nw.model.Conditioning;
import com.nw.model.DataInObject;
import com.nw.model.OptionsConditions;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.VehicleDetails;
import com.nw.model.VehicleExtras;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class VehicleExtrasFragment extends BaseFragement implements OnClickListener
{
    VehicleExtrasAdapter extrasAdapter;
    ArrayList<VehicleExtras> arrayList;

    ImageView ivAddExtra;
    StaticListView lvVehicleExtras;
    CustomButton btnSave;
    EditText edtComments;
    TextView tv_total_value;

    VehicleDetails vehicleDetails;
    private String VehicleExtraID = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_vehicle_extras, container, false);
        setHasOptionsMenu(true);
        if (arrayList == null)
        {
            arrayList = new ArrayList<VehicleExtras>();
        }
        if (getArguments() != null)
        {
            vehicleDetails = getArguments().getParcelable("vehicleDetails");
        }
        initialise(view);

        return view;
    }

    private void initialise(View view)
    {
        arrayList = new ArrayList<VehicleExtras>();

        lvVehicleExtras = (StaticListView) view.findViewById(R.id.listview);

        //	Helper.setListViewHeightBasedOnChildren(lvVehicleExtras);
        ivAddExtra = (ImageView) view.findViewById(R.id.ivAddExtra);
        ivAddExtra.setOnClickListener(this);

        btnSave = (CustomButton) view.findViewById(R.id.btnSave);
        edtComments = (EditText) view.findViewById(R.id.edtComments);
        btnSave.setOnClickListener(this);

        tv_total_value = (TextView) view.findViewById(R.id.tv_total_value);

        GetExtra();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Vehicle Extras");
        //getActivity().getActionBar().setSubtitle(null);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ivAddExtra:
                VehicleExtras vehicleExtras = new VehicleExtras();
                vehicleExtras.setExtraID("");
                vehicleExtras.setName("");
                vehicleExtras.setPrice(0);
                vehicleExtras.setNewField(true);
                arrayList.add(vehicleExtras);
                extrasAdapter.notifyDataSetChanged();
                //	Helper.setListViewHeightBasedOnChildren(lvVehicleExtras);
                vehicleExtras = null;
                break;

            case R.id.btnSave:
                saveVehicleExtras();
                break;
        }

    }

    private void saveVehicleExtras()
    {
        VollyCustomRequest request;
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");

            soapMessage.append("<SaveVehicleExtras xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<VehicleExtrasXML>");
            soapMessage.append("<VehicleExtras>");

            soapMessage.append("<Items>");

            for (int i = 0; i < arrayList.size(); i++)
            {
                if (!arrayList.get(i).getName().equalsIgnoreCase(""))
                {
                    soapMessage.append("<Extra>");
                    soapMessage.append("<VehicleExtraItemId>" + arrayList.get(i).getExtraID() + "</VehicleExtraItemId>");
                    soapMessage.append("<IsActive>1</IsActive>");
                    soapMessage.append("<Name>" + arrayList.get(i).getName() + "</Name>");
                    soapMessage.append("<Price>" + arrayList.get(i).getPrice() + "</Price>");
                    soapMessage.append("</Extra>");
                }
            }
            soapMessage.append("</Items>");
            soapMessage.append("<RootInfo>");
            soapMessage.append("<AppraisalId>" + vehicleDetails.getStr_AppraisalId() + "</AppraisalId>");

            if (VehicleExtraID.equalsIgnoreCase("anyType{}") || VehicleExtraID.equalsIgnoreCase(""))
                soapMessage.append("<VehicleExtraID>" + "" + "</VehicleExtraID>");
            else
                soapMessage.append("<VehicleExtraID>" + VehicleExtraID + "</VehicleExtraID>");

            soapMessage.append("<ClientId>" + DataManager.getInstance().user.getDefaultClient().getId() + "</ClientId>");
            soapMessage.append("<VinNumber>" + vehicleDetails.getVin() + "</VinNumber>");
            soapMessage.append("<Comments>" + edtComments.getText().toString().trim() + "</Comments>");
            soapMessage.append("</RootInfo>");

            soapMessage.append("</VehicleExtras>");
            soapMessage.append("</VehicleExtrasXML>");
            soapMessage.append("</SaveVehicleExtras>");
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
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_while_saving_VehicleExtras), new DialogListener()
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
                            message = "VehicleExtras save successfully";
                        } else
                        {
                            message = "Error while saving VehicleExtras";
                        }

                        CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                if (PassOrFailed.equalsIgnoreCase("true"))
                                {
                                    arrayList.clear();
                                    GetExtra();
                                }
                            }
                        });
                    }
                }
            };
            request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IStockService/SaveVehicleExtras", listener);
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

    private void GetExtra()
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
            inObj.setMethodname("LoadVehicleExtras");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadVehicleExtras");
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
                        SoapObject responsitems = (SoapObject) outer.getPropertySafely("VehicleExtras");

                        SoapObject response = (SoapObject) responsitems.getPropertySafely("Items");

                        putValues(responsitems.getPropertySafelyAsString("Comments", ""));

                        VehicleExtraID = responsitems.getPropertySafelyAsString("VehicleExtraID", "");

                        for (int i = 0; i < response.getPropertyCount(); i++)
                        {
                            //   SoapObject inner = (SoapObject) response.getPropertySafely("Condition", "");
                            VehicleExtras vehicleExtras = new VehicleExtras();
                            SoapObject conditionObj = (SoapObject) response.getProperty(i);
                            vehicleExtras.setExtraID(conditionObj.getPropertySafelyAsString("ExtraID", ""));
                            vehicleExtras.setName(conditionObj.getPropertySafelyAsString("Name", ""));
                            double d = Double.parseDouble(conditionObj.getPropertySafelyAsString("Price", "0.0"));
                            vehicleExtras.setPrice((int) d);
                            vehicleExtras.setNewField(false);
                            arrayList.add(vehicleExtras);
                        }

                        SetAdaptor();
                        setTotalValue();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
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

    private void SetAdaptor()
    {
        extrasAdapter = new VehicleExtrasAdapter(getActivity(), arrayList, new DoAppraisalInputListener()
        {
            @Override
            public void onButtonClicked(boolean isTitle, final int position, String message)
            {
                if (message.equalsIgnoreCase("delete"))
                {
                    CustomDialogManager.showOkCancelDialog(getContext(), getContext().getString(R.string.are_you_sure_you_want_delete), new DialogListener()
                    {

                        @Override
                        public void onButtonClicked(int type)
                        {
                            if (type == Dialog.BUTTON_POSITIVE)
                            {
                                arrayList.remove(position);
                                extrasAdapter.notifyDataSetChanged();
                                setTotalValue();
                            }
                        }
                    });
                } else
                {
                    if (isTitle)
                    {
                        arrayList.get(position).setName(message);
                    } else
                    {
                        if (!message.equalsIgnoreCase(""))
                        {
                            arrayList.get(position).setPrice((int) Double.parseDouble(message.replaceAll("R", "")));
                        } else
                            arrayList.get(position).setPrice(0);
                    }
                    setTotalValue();
                }
            }
        });
        lvVehicleExtras.setAdapter(extrasAdapter);
    }

    private void putValues(String comment)
    {
        if (comment.trim().equals("anyType{}"))
        {
            edtComments.setText("");
        } else
        {
            edtComments.setText(comment);
        }

    }

    private void setTotalValue()
    {
        if (arrayList != null)
        {
            int total_value = 0;
            for (int i = 0; i < arrayList.size(); i++)
            {
                total_value += arrayList.get(i).getPrice();
            }
            tv_total_value.setText(Helper.formatPrice("" + total_value));
        }
    }

}
