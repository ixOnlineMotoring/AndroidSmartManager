package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.nw.interfaces.DateListener;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.Person;
import com.nw.model.PurchaseDetail;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class PurchaseDetailsFragment extends BaseFragement implements View.OnClickListener
{
    EditText edBoughtFrom, edDate, edFinanceHouse, edSettlement, edAccountNo, edDetails, edtComments;
    Button btnSave;

    VehicleDetails vehicleDetails;
    private PurchaseDetail purchaseDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_purchase_details, container, false);
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
        edBoughtFrom = (EditText) view.findViewById(R.id.edBoughtFrom);
        edDate = (EditText) view.findViewById(R.id.edDate);
        edFinanceHouse = (EditText) view.findViewById(R.id.edFinanceHouse);
        edSettlement = (EditText) view.findViewById(R.id.edSettlement);
        edAccountNo = (EditText) view.findViewById(R.id.edAccountNo);
        edDetails = (EditText) view.findViewById(R.id.edDetails);
        edtComments = (EditText) view.findViewById(R.id.edtComments);
        btnSave = (Button) view.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(this);
        edDate.setOnClickListener(this);
       getPurchaseData();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Purchase Details");
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

    private void getPurchaseData()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("UserHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("AppraisalId", vehicleDetails.getStr_AppraisalId(), Integer.class));
         //   parameterList.add(new Parameter("ClientID", 1, Integer.class));
            parameterList.add(new Parameter("VinNumber", vehicleDetails.getVin(), Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadPurchaseDetails");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadPurchaseDetails");
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
                        SoapObject response = (SoapObject) outer.getPropertySafely("PurchaseDetail");

                        purchaseDetail = new PurchaseDetail();

                        purchaseDetail.setAppraisalId(response.getPropertySafelyAsString("AppraisalId", ""));
                        purchaseDetail.setAccountNo(response.getPropertySafelyAsString("AccountNo", ""));
                        purchaseDetail.setBoughtFrom(response.getPropertySafelyAsString("BoughtFrom", ""));
                        purchaseDetail.setComments(response.getPropertySafelyAsString("Comments", ""));
                        purchaseDetail.setDate(response.getPropertySafelyAsString("Date", ""));
                        purchaseDetail.setDetails(response.getPropertySafelyAsString("Details", ""));
                        purchaseDetail.setFinanceHouse(response.getPropertySafelyAsString("FinanceHouse", ""));
                        purchaseDetail.setPurchaseDetailsId(response.getPropertySafelyAsString("PurchaseDetailsId", ""));
                        purchaseDetail.setSettlementR(response.getPropertySafelyAsString("SettlementR", ""));
                        putValues(purchaseDetail);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                              //  getActivity().getSupportFragmentManager().popBackStack();
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

    private void putValues(PurchaseDetail purchaseDetail)
    {
        edBoughtFrom.setText(purchaseDetail.getBoughtFrom());
        edDate.setText(Helper.showDateWithDash(purchaseDetail.getDate()));
        edFinanceHouse.setText(purchaseDetail.getFinanceHouse());
        edSettlement.setText(Helper.formatPrice(purchaseDetail.getSettlementR()));
        edAccountNo.setText(purchaseDetail.getAccountNo());
        edDetails.setText(purchaseDetail.getDetails());
        edtComments.setText(purchaseDetail.getComments());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.edDate:
                DatePickerFragment endDate = new DatePickerFragment();
                endDate.setDateListener(new DateListener()
                {
                    @Override
                    public void onDateSet(int year, int monthOfYear, int dayOfMonth)
                    {
                        edDate.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
                    }
                });
                endDate.show(getActivity().getFragmentManager(), "datePicker");
                break;
            case R.id.btnSave:
                savePurchaseDetails();
                break;
        }
    }

    private void savePurchaseDetails()
    {
        VollyCustomRequest request;
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");

            soapMessage.append("<SavePurchaseDetails xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");

            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<PurchaseDetailsXML>");
            soapMessage.append("<PurchaseDetails>");
            if(purchaseDetail!=null)
                soapMessage.append("<PurchaseDetailsId>" + purchaseDetail.getPurchaseDetailsId() + "</PurchaseDetailsId>");
                else
                soapMessage.append("<PurchaseDetailsId>" + "" + "</PurchaseDetailsId>");

            soapMessage.append("<AccountNo>" + edAccountNo.getText().toString().trim() + "</AccountNo>");
            soapMessage.append("<AppraisalId>" + vehicleDetails.getStr_AppraisalId() + "</AppraisalId>");
            soapMessage.append("<BoughtFrom>" + edBoughtFrom.getText().toString().trim() + "</BoughtFrom>");
            soapMessage.append("<Comments>" + edtComments.getText().toString().trim() + "</Comments>");
            soapMessage.append("<Date>" + Helper.convertStringToDatewithMONTHS(edDate.getText().toString().trim()) + "</Date>");
            soapMessage.append("<Details>" + edDetails.getText().toString().trim() + "</Details>");
            soapMessage.append("<FinanceHouse>" + edFinanceHouse.getText().toString().trim() + "</FinanceHouse>");
            soapMessage.append("<VinNumber>" +vehicleDetails.getVin()+ "</VinNumber>");
            soapMessage.append("<ClientId>" + DataManager.getInstance().user.getDefaultClient().getId()+ "</ClientId>");
        //    soapMessage.append("<ClientId>" + 1 + "</ClientId>");
            soapMessage.append("<SettlementR>" + edSettlement.getText().toString().trim().replaceAll("R", "").replaceAll("\\s","") + "</SettlementR>");
            soapMessage.append("</PurchaseDetails>");
            soapMessage.append("</PurchaseDetailsXML>");
            soapMessage.append("</SavePurchaseDetails>");
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
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_while_saving_purchase_details_data), new DialogListener()
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
                            message = "Purchase details save successfully";
                        } else
                        {
                            message = "Error while save purchase details";
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
                    Constants.TEMP_URI_NAMESPACE + "IStockService/SavePurchaseDetails", listener);
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