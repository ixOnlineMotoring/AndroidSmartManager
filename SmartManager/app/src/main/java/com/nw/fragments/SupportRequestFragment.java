package com.nw.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.ImageClickListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomDragableGridView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Akshay on 10-11-2017.
 */

public class SupportRequestFragment extends BaseFragement implements View.OnClickListener
{

    //Added by Asmita on 11/11/2017
   /* private final String SELECT_TEAM[] = {"Google PPC", "Social Media", "Websites",
            "Email & SMS Campaigns", "Stock", "Leads", "Webmaster", "Accounts", "User Access"};*/

    //Added by Asmita on 16/11/2018
    //Made changes in the list of menu items
    /*private final String SELECT_TEAM[] = {"Website Development", "Website Support", "Stock",
            "Leads", "Google PPC", "Social Media", "Email and SMS Marketing", "Webmasters",
            "Accounts", "Access, Users and Lead Recipients", "Cancellations"};
*/
    EditText etSelectTeam, edTitle, edDescription;
    ImageView iv_add;
    CustomDragableGridView imageDragableGridView;
    FrameLayout flAddFiles;
    RelativeLayout rlGridImage;
    ArrayList<SmartObject> requestList;
    int selectedRequestId = 0;
    Button btnSendEmailList;
    String successMessage = "";
    String filePath;
    private int fileCount = 0;
    String base64String = "";
    int planTaskId = 0;

    public SupportRequestFragment() throws IOException
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_support_request, container, false);
        setHasOptionsMenu(true);
        initialise(view);
        return view;
    }

    private void initialise(View view)
    {
        if (requestList == null)
        {
            requestList = new ArrayList<>();
        }

        etSelectTeam = (EditText) view.findViewById(R.id.etSelectTeam);
        etSelectTeam.setOnClickListener(this);
        edTitle = (EditText) view.findViewById(R.id.edTitle);
        edDescription = (EditText) view.findViewById(R.id.edDescription);

        btnSendEmailList = (Button) view.findViewById(R.id.btnSendEmailList);
        btnSendEmailList.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //this code will be executed on devices running Marshmellow or later
            imageDragableGridView = new CustomDragableGridView(getActivity());
        } else
        {
            imageDragableGridView = new CustomDragableGridView();
        }    //image grid view implemented in imageDragableGridView
        imageDragableGridView.init(view, new ImageClickListener()
        {
            @Override
            public void onImageClick(int position)
            {

            }

            @Override
            public void onImageDeleted(int position)
            {
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (imageDragableGridView != null)
        {
            if (imageDragableGridView.isOptionSelected())
                imageDragableGridView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar(getString(R.string.support_request));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.etSelectTeam:

                if (requestList.size() == 0)
                {
                    if (HelperHttp.isNetworkAvailable(getActivity()))
                        getRequestList();
                    else
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                } else
                {
                    if (!requestList.isEmpty())
                        showListPopUp(v, requestList);
                }

                break;

            case R.id.btnSendEmailList:
                validation();

                break;
        }

    }

    //Webservice implementation to pass request on Submit button click
    //Added by Asmita on 18/01/2018

    private void sendSupportRequest()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ticketType", selectedRequestId, Integer.class));
            parameterList.add(new Parameter("ticketTitle", edTitle.getText().toString(), String.class));
            parameterList.add(new Parameter("ticketDetails", edDescription.getText().toString(), String.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LogNewSupportRequest");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IPlannerService/LogNewSupportRequest");
            inObj.setUrl(Constants.PLANNER_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            //Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object response)
                {
                    //Added by Asmita on 24th Jan 2017 as client asked to temporary hide the functionality due to server side error
                    try
                    {
                        SoapObject outer = (SoapObject) response;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Success");
                        successMessage = inner.getPropertySafelyAsString("Message", "0");
                        planTaskId = Integer.parseInt(inner.getPropertySafelyAsString("PlanTaskID", "0"));

                        if (imageDragableGridView.getUpdatedImageListWithoutPlus().size() >= 1)
                        {
                            for (int i = 0; i < imageDragableGridView.getUpdatedImageListWithoutPlus().size(); i++)
                            {
                                uploadDocFiles();
                            }

                        } else
                        {
                            hideProgressDialog();
                            CustomDialogManager.showOkDialog(getActivity(), successMessage, new DialogListener()
                            {
                                @Override
                                public void onButtonClicked(int type)
                                {
                                    hideProgressDialog();
                                    if (getFragmentManager().getBackStackEntryCount() != 0)
                                    {
                                        getFragmentManager().popBackStack();
                                    } else
                                    {
                                        getActivity().finish();
                                    }
                                }
                            });
                        }

                    } catch (Exception e)
                    {
                        hideProgressDialog();
                        e.printStackTrace();
                    }

                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    //Webservice implementation to get Request Type Listing
    private void getRequestList()
    {
        StringBuilder soapMessage = new StringBuilder();
        soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        soapMessage.append("<Body>");
        soapMessage.append("<ListRequestType xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
        soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
        soapMessage.append("</ListRequestType>");
        soapMessage.append("</Body>");
        soapMessage.append("</Envelope>");
        showProgressDialog();
        VollyResponseListener vollyResponseListener = new VollyResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();
            }

            @Override
            public void onResponse(String response)
            {
                hideProgressDialog();
                requestList = new ArrayList<SmartObject>();
                Helper.Log("Response:%n %s", response);
                DataManager.getInstance().requestUser = ParserManager.parseRequestResponse(response);
                for (int i = 0; i < ParserManager.parseRequestResponse(response).getRequests().size(); i++)
                {
                    int requestId = Integer.parseInt(ParserManager.parseRequestResponse(response).getRequests().get(i).getRequestId());
                    String requestName = ParserManager.parseRequestResponse(response).getRequests().get(i).getRequestName();
                    requestList.add(i, new SmartObject(requestId, requestName));
                }

                {
                    if (!requestList.isEmpty())
                        showListPopUp(etSelectTeam, requestList);
                }

            }
        };
        try
        {
            VollyCustomRequest vollyCustomRequest = new VollyCustomRequest(Constants.PLANNER_WEBSERVICE_URL,
                    soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IPlannerService/ListRequestType", vollyResponseListener);
            vollyCustomRequest.init("validateUser");


        } catch (Exception e1)
        {
            e1.printStackTrace();
        }

    }

    //Check validation
    private void validation()
    {
        if (TextUtils.isEmpty(etSelectTeam.getText().toString().trim()))
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.please_select_request));
            return;
        }
        if (TextUtils.isEmpty(edTitle.getText().toString().trim()))
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.please_enter_request_title));
            edTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edDescription.getText().toString().trim()))
        {
            CustomDialogManager.showOkDialog(getActivity(),getString(R.string.please_enter_request_description));
            edDescription.requestFocus();
            return;
        }
        sendSupportRequest();

    }

    private void showListPopUp(final View mView, final ArrayList list)
    {
        try
        {
            final EditText ed = (EditText) mView;
            final String edtData = ed.getText().toString().trim();

            Helper.showDropDownSearch(false, mView, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, list), new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    SmartObject smartObject = (SmartObject) parent.getItemAtPosition(position);
                    ed.setText(smartObject.getName() + "");
                    if (edtData.equals(ed.getText().toString().trim()))
                        return;
                    if (mView.getId() == R.id.etSelectTeam)
                    {
                        if (requestList != null) // remove modelList and variantL items
                            requestList = null;
                        etSelectTeam.setText(smartObject.getName());
                        selectedRequestId = smartObject.getId();

                    }
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Webservice implementation to upload Files on success response of send Support Request
    private void uploadDocFiles()
    {
        fileCount = imageDragableGridView.getUpdatedImageListWithoutPlus().size() - 1;
        base64String = Helper.convertFileToByteArray(imageDragableGridView.getUpdatedImageListWithoutPlus().get(fileCount).getPath());

        String filePath = "";
        if (imageDragableGridView.getUpdatedImageListWithoutPlus().get(fileCount).getDocPath().isEmpty())
        {
            filePath = imageDragableGridView.getUpdatedImageListWithoutPlus().get(fileCount).getPath();
        } else
        {
            filePath = imageDragableGridView.getUpdatedImageListWithoutPlus().get(fileCount).getDocPath();
        }

        if (HelperHttp.isNetworkAvailable(getActivity()))

        {
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("bas64Doc", base64String, String.class));
            parameterList.add(new Parameter("fileName", filePath, String.class));
            parameterList.add(new Parameter("planTaskID", planTaskId, String.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("NewSupportRequestDoc");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IPlannerService/NewSupportRequestDoc");
            inObj.setUrl(Constants.PLANNER_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            //Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    CustomDialogManager.showOkDialog(getActivity(), successMessage, new DialogListener()
                    {
                        @Override
                        public void onButtonClicked(int type)
                        {
                            hideProgressDialog();
                            if (getFragmentManager().getBackStackEntryCount() != 0)
                            {
                                getFragmentManager().popBackStack();
                            } else

                            {
                                getActivity().finish();
                            }
                        }
                    });
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }


}
