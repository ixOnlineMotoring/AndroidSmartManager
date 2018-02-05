package com.nw.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.ScrollView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.nw.adapters.ClientAdapter;
import com.nw.interfaces.DateListener;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.ImageClickListener;
import com.nw.interfaces.TimeListener;
import com.nw.model.Client;
import com.nw.model.DataInObject;
import com.nw.model.Member;
import com.nw.model.Parameter;
import com.nw.model.PlannerType;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.DragableGridView;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewTaskFragment extends BaseFragement implements OnClickListener, TextWatcher
{
    EditText edtClientFilter, edtClient, edtType, edtRecipient, edtTitle,
            edtDetails, edtStartDate, edtSelectTime;
    CheckBox cbCalenderTime;

    ArrayList<Client> clients;
    ClientAdapter clientAdapter;
    ArrayList<PlannerType> plannerType;
    ArrayAdapter<PlannerType> plannerAdapter;
    ArrayList<Member> members;
    ArrayAdapter<Member> memmberAdapter;
    ListPopupWindow popupMenu;
    boolean isShown = false;
    DragableGridView imageDragableGridView;
    ScrollView scrollView;
    Button btnSave;

    int selectedClientId, selectedMemberId, selectedTypeId;
    int imageCount;
    int responseCount;
    int index = 0;
    boolean isError = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_new_task, container, false);
        setHasOptionsMenu(true);
        edtClientFilter = (EditText) view.findViewById(R.id.edtClientFilter);
        edtClient = (EditText) view.findViewById(R.id.tvClient);
        edtType = (EditText) view.findViewById(R.id.tvType);

        edtRecipient = (EditText) view.findViewById(R.id.edtRecipient);
        edtTitle = (EditText) view.findViewById(R.id.edtTitle);
        edtDetails = (EditText) view.findViewById(R.id.edtDetails);

        edtStartDate = (EditText) view.findViewById(R.id.edtStartDate);
        edtSelectTime = (EditText) view.findViewById(R.id.edtSelectTime);
        edtSelectTime.setTag("-1");
        edtRecipient.setTag("-1");
        cbCalenderTime = (CheckBox) view.findViewById(R.id.cbInternal);

        btnSave = (Button) view.findViewById(R.id.btnSave);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //this code will be executed on devices running Marshmellow or later
            imageDragableGridView = new DragableGridView();
        } else
        {
            imageDragableGridView = new DragableGridView(getActivity());
        }    //image grid view implemented in imageDragableGridView
        imageDragableGridView.init(view, new ImageClickListener()
        {

            @Override
            public void onImageClick(int position)
            {
                try
                {
                    navigateToLargeImage(position);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onImageDeleted(int position)
            {
            }
        });
        edtClient.setOnClickListener(this);
        edtType.setOnClickListener(this);
        edtRecipient.setOnClickListener(this);
        edtStartDate.setOnClickListener(this);
        edtSelectTime.setOnClickListener(this);
        edtClientFilter.addTextChangedListener(this);
        btnSave.setOnClickListener(this);

        hideKeyboard(view);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                hideKeyboard();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Navigate to large screen fragment
     * As Fragment to called belongs to other activity we call activity and pass parameters through intent
     * Parameter- position of image clicked
     * */
    private void navigateToLargeImage(int position)
    {
        try
        {
            Intent iToBuyActivity = new Intent(getActivity(), BuyActivity.class);
            iToBuyActivity.putParcelableArrayListExtra("imagelist", imageDragableGridView.getUpdatedImageListWithoutPlus());
            iToBuyActivity.putExtra("index", position);
            iToBuyActivity.putExtra("vehicleName", "New Task");
            startActivity(iToBuyActivity);
        } catch (Exception e)
        {
            e.printStackTrace();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (imageDragableGridView != null)
        {
            if (imageDragableGridView.isOptionSelected())
                imageDragableGridView.onActivityResult(requestCode, resultCode,
                        data);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("New Task");
        //getActivity().getActionBar().setSubtitle(null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void onClick(View v)
    {
        v.setEnabled(false);
        switch (v.getId())
        {
            case R.id.tvType:
                if (plannerType == null || plannerType.isEmpty())
                {
                    showProgressDialog();
                    getPlannerType();
                } else
                {
                    showTypePopup(v);
                }
                break;
            case R.id.edtRecipient:
                if (members == null || members.isEmpty())
                {
                    showProgressDialog();
                    getListMembersForClients();
                } else
                {
                    showMemberPopup(v);
                }
                break;
            case R.id.btnSave:
                postNewTaskToServer();
                break;

            case R.id.edtStartDate:

                DatePickerFragment startDate = new DatePickerFragment();
                startDate.setDateListener(new DateListener()
                {
                    @Override
                    public void onDateSet(int year, int monthOfYear, int dayOfMonth)
                    {
                        edtStartDate.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
                    }
                });
                startDate.show(getActivity().getFragmentManager(), "datePicker");
                break;

            case R.id.tvClient:
                if (clients != null)
                {
                    Helper.showDropDown(v, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, clients), new OnItemClickListener()
                    {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            edtClient.setText(clients.get(position) + "");
                            selectedClientId = clients.get(position).getId();
                        }
                    });
                } else
                {
                    Helper.showToast("No client to select", getActivity());
                }


                break;
            case R.id.edtSelectTime:

                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setTimeListener(new TimeListener()
                {
                    @Override
                    public void onTimeSet(int hourOfDay, int minute)
                    {
                        String time = "";
                        if (minute < 10)
                            time = "0" + minute;
                        else
                            time = "" + minute;
                        edtSelectTime.setText(hourOfDay + ":" + time);
                    }
                });
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                break;
        }
        v.setEnabled(true);
    }

    private void showTypePopup(View v)
    {
        if (plannerAdapter != null)
        {

            Helper.showDropDown(v, plannerAdapter, new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
                {
                    edtType.setText(plannerAdapter.getItem(itemPosition).toString());
                    selectedTypeId = plannerAdapter.getItem(itemPosition).getActivityId();
                    edtType.setTag("" + plannerAdapter.getItem(itemPosition).getActivityId());
                }
            });
            /*final ListPopupWindow popupMenu = new ListPopupWindow(
					getActivity());
			popupMenu.setAnchorView(v);
			popupMenu.setModal(true);
			popupMenu.setAdapter(plannerAdapter);
			popupMenu.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int itemPosition, long arg3) {
					popupMenu.dismiss();
					edtType.setText(plannerAdapter.getItem(itemPosition).toString());
					selectedTypeId=plannerAdapter.getItem(itemPosition).getActivityId();
					edtType.setTag(""+ plannerAdapter.getItem(itemPosition).getActivityId());
				}
			});
			popupMenu.show();
			if (popupMenu.isShowing())
				popupMenu.getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));*/
        } else
            Helper.showToast(getString(R.string.no_item_to_select), getActivity());
    }

    //don't use Helper.showDropDown() method
    private void showMemberPopup(View v)
    {
        if (memmberAdapter != null)
        {

            Helper.showDropDown(v, memmberAdapter, new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
                {
                    edtRecipient.setText(memmberAdapter.getItem(itemPosition).getName());
                    selectedMemberId = memmberAdapter.getItem(itemPosition).getId();
                    edtRecipient.setTag("" + memmberAdapter.getItem(itemPosition).getId());
                }
            });
        } else
            Helper.showToast(getString(R.string.no_item_to_select), getActivity());
    }

    @Override
    public void afterTextChanged(Editable arg0)
    {
        try
        {
            if (!TextUtils.isEmpty(arg0))
            {
                clientAdapter.getFilter().filter(arg0);
                showPopup();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
    {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
    {
    }

    //don't use Helper.showDropDown() method
    private void showPopup()
    {
        if (popupMenu == null)
        {
            popupMenu = new ListPopupWindow(getActivity());
            popupMenu.setAnchorView(edtClient);
            popupMenu.setAdapter(clientAdapter);
            popupMenu.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
                {
                    popupMenu.dismiss();
                    edtClient.setText("" + clientAdapter.getItem(itemPosition));
                    edtClient.setTag("" + clientAdapter.getItem(itemPosition).getId());
                    selectedClientId = clientAdapter.getItem(itemPosition).getId();
                    isShown = false;
                }
            });
            popupMenu.show();
        } else
        {
            clientAdapter.notifyDataSetChanged();

            if (!popupMenu.isShowing())
                popupMenu.show();
            popupMenu.show();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getClients();
    }

    private void getClients()
    {
        if (clients != null)
            clients.clear();
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            final StringBuilder soapBuilder = new StringBuilder();
            soapBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\"><Body><ListAvailableClientsXML xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapBuilder.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapBuilder.append("</ListAvailableClientsXML></Body></Envelope>");

            Helper.Log("getClients request:", soapBuilder.toString());


            VollyResponseListener listener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    VolleyLog.e("Error: ", "" + error.getMessage());
                    //getPlannerType();
                }

                @Override
                public void onResponse(String response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    Helper.Log("Response:", response);
                    clients = ParserManager.parseClientList(response);
                    if (getActivity() != null)
                    {
                        if (clients != null)
                            clientAdapter = new ClientAdapter(getActivity(), R.layout.list_item_text2, clients);
                        else
                            clientAdapter = new ClientAdapter(getActivity(), R.layout.list_item_text2, new ArrayList<Client>());
                    }

                }
            };


            VollyCustomRequest request = new VollyCustomRequest(Constants.PLANNER_WEBSERVICE_URL, soapBuilder.toString(), Constants.TEMP_URI_NAMESPACE + "IPlannerService/ListAvailableClientsXML", listener);
            try
            {
                request.init("getClients");
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getPlannerType()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<ListPlannerTypeXML xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapMessage.append("<coreClientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</coreClientID>");
            soapMessage.append("</ListPlannerTypeXML>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            Helper.Log("getPlannerType request:", soapMessage.toString());

            VollyResponseListener listener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError arg0)
                {
                    //getListMembersForClients();

                    hideProgressDialog();
                    showErrorDialog();
                    return;
                }

                @Override
                public void onResponse(String response)
                {
                    if (response == null)
                    {
                        hideProgressDialog();
                        return;
                    }
                    Helper.Log("Response:", response);
                    plannerType = ParserManager.parsePlannerTypeList(response, 1);
                    if (plannerType != null)
                    {
                        plannerAdapter = new ArrayAdapter<PlannerType>(getActivity(), R.layout.list_item_text2, plannerType);
                    }
                    showTypePopup(edtType);
                    hideProgressDialog();

                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.PLANNER_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IPlannerService/ListPlannerTypeXML", listener);
            try
            {
                request.init("getPlannerType");
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getListMembersForClients()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<ListMembersForClientXML xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapMessage.append("</ListMembersForClientXML>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            Helper.Log("getListMembersForClients request:", soapMessage.toString());

            VollyResponseListener listener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    hideProgressDialog();
                    Helper.Log("Error: ", error.getMessage());
                }

                @Override
                public void onResponse(String response)
                {
                    hideProgressDialog();
                    if (response == null)
                    {
                        return;
                    }
                    Helper.Log("Response:", response);
                    members = ParserManager.parseListMembersForClientXMLRespone(response);
                    if (members != null)
                    {
                        memmberAdapter = new ArrayAdapter<Member>(getActivity(), R.layout.list_item_text2, members);
                    }
                    hideProgressDialog();
                    showMemberPopup(edtRecipient);

                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.PLANNER_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IPlannerService/ListMembersForClientXML", listener);

            try
            {
                request.init("getListMembersForClients");
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }

        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void postNewTaskToServer()
    {
        if (TextUtils.isEmpty(edtClient.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_select_client), getActivity());
            edtClient.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtType.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_select_planner_type), getActivity());
            edtType.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtRecipient.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_select_recipient), getActivity());
            edtRecipient.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtTitle.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_enter_title), getActivity());
            edtTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtDetails.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_enter_details), getActivity());
            edtDetails.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtStartDate.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_select_date), getActivity());
            edtStartDate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtSelectTime.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_select_time), getActivity());
            edtSelectTime.requestFocus();
            return;
        }
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameters.add(new Parameter("coreClientID", selectedClientId, Integer.class));
            parameters.add(new Parameter("plannerTypeID", selectedTypeId, Integer.class));
            parameters.add(new Parameter("coreMemberID", selectedMemberId, Integer.class));
            parameters.add(new Parameter("title", edtTitle.getText().toString().trim(), String.class));
            parameters.add(new Parameter("details", edtDetails.getText().toString().trim(), String.class));
            parameters.add(new Parameter("date", formateDateTime(edtStartDate.getText().toString(), edtSelectTime.getText().toString()), String.class));
            parameters.add(new Parameter("sendCalendar", cbCalenderTime.isChecked(), Boolean.class));

            DataInObject dataInObject = new DataInObject();
            dataInObject.setMethodname("PostNewTask");
            dataInObject.setNamespace(Constants.TEMP_URI_NAMESPACE);
            dataInObject.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IPlannerService/PostNewTask");
            dataInObject.setUrl(Constants.PLANNER_WEBSERVICE_URL);
            dataInObject.setParameterList(parameters);
            new WebServiceTask(getActivity(), dataInObject, false, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object response)
                {
                    try
                    {
                        if (response != null)
                        {
                            Helper.Log("onTaskComplete ", "" + response.toString());
                            int taskId = Integer.parseInt(response.toString());
                            if (taskId > 0)
                            {
                                // check for image size
                                if (imageDragableGridView.getUpdatedImageListWithoutPlus().size() >= 1)
                                {
                                    index = 0;
                                    imageCount = imageDragableGridView.getUpdatedImageListWithoutPlus().size();
                                    Helper.Log("Image Count before saveBlogImage", "" + imageCount);
                                    addImageToTask("" + taskId);
                                } else
                                {
                                    hideProgressDialog();
                                    CustomDialogManager.showOkDialog(getActivity(), getString(R.string.new_task_saved_successfully), new DialogListener()
                                    {
                                        @Override
                                        public void onButtonClicked(int type)
                                        {
                                            getActivity().finish();
                                        }
                                    });
                                }
                            } else
                            {
                                hideProgressDialog();
                                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.could_not_save_new_task));
                            }
                        } else
                        {
                            hideProgressDialog();
                            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.could_not_save_new_task));
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                }
            }).execute();
        } else
            HelperHttp.showNoInternetDialog(getActivity());
    }

    private void addImageToTask(final String TaskId)
    {
        String base64String = null;
        base64String = Helper.convertBitmapToBase64(imageDragableGridView.getUpdatedImageListWithoutPlus().get(index).getPath());

        ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
        parameters.add(new Parameter("plannerTaskID", TaskId, Integer.class));
        parameters.add(new Parameter("imageDescription", "", String.class));
        parameters.add(new Parameter("imageFilename", getFileName(imageDragableGridView.getUpdatedImageListWithoutPlus().get(index).getPath()), String.class));
        parameters.add(new Parameter("imageBase64", base64String, String.class));

        DataInObject dataInObject = new DataInObject();
        dataInObject.setMethodname("AddImageToTask64");
        dataInObject.setNamespace(Constants.TEMP_URI_NAMESPACE);
        dataInObject.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IPlannerService/AddImageToTask64");
        dataInObject.setUrl(Constants.PLANNER_WEBSERVICE_URL);
        dataInObject.setParameterList(parameters);
        new WebServiceTask(getActivity(), dataInObject, false, new TaskListener()
        {
            @Override
            public void onTaskComplete(Object response)
            {
                try
                {
                    responseCount++;
                    index++;
                    Helper.Log("responseCount :" + responseCount, "");
                    if (response == null)
                        isError = true;

                    if (responseCount == imageCount)
                        checkResponse();
                    else
                        addImageToTask(TaskId);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    void checkResponse()
    {
        hideProgressDialog();
        if (isError == false)
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.new_task_saved_successfully), new DialogListener()
            {
                @Override
                public void onButtonClicked(int type)
                {
                    getActivity().finish();
                }
            });
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.could_not_save_new_task));
        }
    }

    private String getFileName(String path)
    {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private String formateDateTime(String dateInString, String timeInString)
    {
        String formattedDate = "";
        try
        {
            DateFormat originalFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
            Date date = originalFormat.parse(dateInString + " " + timeInString);
            String format = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
            return sdf.format(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return formattedDate;
    }
}
