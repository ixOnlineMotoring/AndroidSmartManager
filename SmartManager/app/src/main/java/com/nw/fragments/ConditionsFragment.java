package com.nw.fragments;

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

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.nw.adapters.OptionsConditionAdapter;
import com.nw.interfaces.ConditionListener;
import com.nw.interfaces.DialogListener;
import com.nw.model.Audit;
import com.nw.model.Conditioning;
import com.nw.model.DataInObject;
import com.nw.model.OptionsConditions;
import com.nw.model.Parameter;
import com.nw.model.PurchaseDetail;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomEditText;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class ConditionsFragment extends BaseFragement implements OnClickListener
{
    CustomEditText etConditions, edtComments;
    StaticListView listview;
    String[] partnertype = {"Excellent", "Very Good", "Good", "Poor", "Very Poor"};
    ArrayAdapter<String> conditionType;
    ArrayList<Conditioning> conditioningArrayList = new ArrayList<Conditioning>();;
    OptionsConditionAdapter optionsConditionAdapter;
    int intSelectedItem = 0;

    Button btnSave;

    VehicleDetails vehicleDetails;
    String ConditionId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_conditions, container, false);
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
        etConditions = (CustomEditText) view.findViewById(R.id.edtCondition);
        edtComments = (CustomEditText) view.findViewById(R.id.edtComments);
        listview = (StaticListView) view.findViewById(R.id.listview);
        btnSave = (Button) view.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(this);
        etConditions.setOnClickListener(this);

        conditionType = new ArrayAdapter<String>(getActivity(), R.layout.list_item_text2, partnertype);
        GetConditioning();
    }

    private void showConditiontypePopup(View v)
    {
        Helper.showDropDown(v, conditionType, new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
            {
                intSelectedItem = itemPosition + 1;
                etConditions.setText(conditionType.getItem(itemPosition));
                etConditions.setTag("" + conditionType.getItem(itemPosition));
                intSelectedItem = intSelectedItem -1;
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Condition");
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.edtCondition:
                showConditiontypePopup(v);
                break;
            case R.id.btnSave:
                saveCondtions();
                break;
        }
    }

    private void GetConditioning()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("UserHash", DataManager.getInstance().user.getUserHash(), String.class));
        //    parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("AppraisalId", vehicleDetails.getStr_AppraisalId(), Integer.class));
            parameterList.add(new Parameter("VinNumber", vehicleDetails.getVin(), Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadVehicleCondition");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadVehicleCondition");
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
                        SoapObject responsitems = (SoapObject) outer.getPropertySafely("Conditions");

                        SoapObject response = (SoapObject) responsitems.getPropertySafely("Items");

                        conditioningArrayList = new ArrayList<Conditioning>();
                        for (int i = 0; i < response.getPropertyCount(); i++)
                        {
                            //   SoapObject inner = (SoapObject) response.getPropertySafely("Condition", "");
                            Conditioning conditioning = new Conditioning();
                            SoapObject conditionObj = (SoapObject) response.getProperty(i);
                            conditioning.setIdentity(Integer.parseInt(conditionObj.getPropertySafelyAsString("Identity", "0")));
                            conditioning.setname(conditionObj.getPropertySafelyAsString("Name", ""));

                            SoapObject innerOptions = (SoapObject) conditionObj.getPropertySafely("Options", "");
                            ArrayList<OptionsConditions> optionsConditionsArrayList = new ArrayList<OptionsConditions>();
                            for (int j = 0; j < innerOptions.getPropertyCount(); j++)
                            {
                                OptionsConditions optionsConditions = new OptionsConditions();
                                SoapObject optionsObj = (SoapObject) innerOptions.getProperty(j);
                                optionsConditions.setIdentity(Integer.parseInt(optionsObj.getPropertySafelyAsString("Identity", "0")));
                                optionsConditions.setValue(optionsObj.getPropertySafelyAsString("Value", ""));
                                optionsConditions.setSelected(Boolean.parseBoolean(optionsObj.getPropertySafelyAsString("Selected", "false").trim()));
                                optionsConditionsArrayList.add(optionsConditions);
                            }
                            conditioning.setOptionsConditions(optionsConditionsArrayList);
                            conditioningArrayList.add(conditioning);
                        }

                        optionsConditionAdapter = new OptionsConditionAdapter(getContext(), R.layout.item_condition_options, conditioningArrayList, new ConditionListener()
                        {
                            @Override
                            public void onButtonClicked(int position, int subPosition)
                            {
                                for (int i = 0; i < conditioningArrayList.get(position).getOptionsConditions().size(); i++)
                                {
                                    if (i == subPosition)
                                    {
                                        conditioningArrayList.get(position).getOptionsConditions().get(i).setSelected(true);
                                    } else
                                    {
                                        conditioningArrayList.get(position).getOptionsConditions().get(i).setSelected(false);
                                    }
                                }
                                optionsConditionAdapter.notifyDataSetChanged();
                            }
                        });
                        listview.setAdapter(optionsConditionAdapter);
                        putValues(responsitems.getPropertySafelyAsString("ConditionId", ""),responsitems.getPropertySafelyAsString("OverallCondition", ""), responsitems.getPropertySafelyAsString("Comment", ""));
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                              //  getActivity().getFragmentManager().popBackStack();
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

    private void putValues(String ConditionId,String OverallCondition, String Comment)
    {
        if (OverallCondition.equalsIgnoreCase("anyType{}"))
            this.ConditionId = "";
        else
            this.ConditionId = ConditionId;

        if (OverallCondition.equalsIgnoreCase("anyType{}"))
            etConditions.setText(partnertype[0]);
        else
            etConditions.setText(partnertype[Integer.parseInt(OverallCondition)]);

        if (Comment.equalsIgnoreCase("anyType{}"))
            edtComments.setText("");
        else
            edtComments.setText(Comment);
    }

    private void saveCondtions()
    {
        VollyCustomRequest request;
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");

            soapMessage.append("<SaveVehicleCondition xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");

            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<VehicleCondition>");
            soapMessage.append("<ConditionSaveResults>");

            for (int i = 0; i < conditioningArrayList.size(); i++)
            {
                soapMessage.append("<Condition>");
                soapMessage.append("<ConditionTypeID>" + conditioningArrayList.get(i).getIdentity() + "</ConditionTypeID>");
                for (int j = 0; j < conditioningArrayList.get(i).getOptionsConditions().size(); j++)
                {
                    if (conditioningArrayList.get(i).getOptionsConditions().get(j).isSelected())
                        soapMessage.append("<SelectedValue>" + conditioningArrayList.get(i).getOptionsConditions().get(j).getIdentity() + "</SelectedValue>");
                }
                soapMessage.append("</Condition>");
            }

            soapMessage.append("<RootCondition>");
            soapMessage.append("<ConditionId>" + ConditionId + "</ConditionId>");
            soapMessage.append("<AppraisalId>" + vehicleDetails.getStr_AppraisalId() + "</AppraisalId>");
            soapMessage.append("<VinNumber>" +vehicleDetails.getVin()+ "</VinNumber>");
            soapMessage.append("<ClientId>" +DataManager.getInstance().user.getDefaultClient().getId()+ "</ClientId>");
        //    soapMessage.append("<ClientId>" +"1"+ "</ClientId>");
            soapMessage.append("<OverallCondition>" + intSelectedItem + "</OverallCondition>");
            soapMessage.append("<Comments>" + edtComments.getText().toString().trim() + "</Comments>");
            soapMessage.append("</RootCondition>");
            soapMessage.append("</ConditionSaveResults>");
            soapMessage.append("</VehicleCondition>");
            soapMessage.append("</SaveVehicleCondition>");
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
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_while_saving_condition_data), new DialogListener()
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
                        String PassOrFailed = ParserManager.parsetokenChecker(response, "PassOrFailed");
                        String message = "";
                        if (PassOrFailed.equalsIgnoreCase("true"))
                        {
                            message = "Conditions save successfully";
                        } else
                        {
                            message = "Error while save conditions";
                        }

                        CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {

                            }
                        });
                    }
                }
            };
            request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IStockService/SaveVehicleCondition", listener);
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
}