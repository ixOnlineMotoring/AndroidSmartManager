package com.nw.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.NotificationsAdapter;
import com.nw.adapters.PushNotificationAdapter;
import com.nw.adapters.VehicleDetailsAdapter;
import com.nw.interfaces.BlogItemEditClickListener;
import com.nw.model.DataInObject;
import com.nw.model.NotificationListing;
import com.nw.model.Parameter;
import com.nw.model.Vehicle;
import com.nw.model.VehicleDetails;
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
import java.util.HashMap;


public class PushNotificationsFragment extends BaseFragement
{
    ListView lvNotificationList;
    PushNotificationAdapter pushNotificationAdapter;
    ArrayList<NotificationListing> arrayList;
    int selectedPageNumber = 0, total_no_of_records = 1000;
    boolean fromLoadMore = false;

    EditText edKeyword;
    String keyword_editValue = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_push_notification, container, false);
        setHasOptionsMenu(true);
        initialise(view);
        return view;
    }

    private void initialise(View view)
    {
        lvNotificationList = (ListView) view.findViewById(R.id.lvNotificationList);

        edKeyword = (EditText) view.findViewById(R.id.edKeyword);
        edKeyword.setHintTextColor(Color.WHITE);

        arrayList = new ArrayList<>();

        getNotificationList(keyword_editValue, selectedPageNumber);

        lvNotificationList.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                hideKeyboard();
                int threshold = 1;
                int count = lvNotificationList.getCount();
                if (scrollState == SCROLL_STATE_IDLE)
                {
                    if (lvNotificationList.getLastVisiblePosition() >= count - threshold)
                    {
                        if (!arrayList.isEmpty())
                        {
                            if (arrayList.size() < total_no_of_records)
                            {
                                selectedPageNumber++;
                                getNotificationList(keyword_editValue, selectedPageNumber);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
            }
        });

        // method used to call web service on search button click of device
        // keyboard
        edKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    arrayList.clear();
                    keyword_editValue = edKeyword.getText().toString().trim();
                    if (!TextUtils.isEmpty(keyword_editValue))
                    {
                        selectedPageNumber = 0;
                        getNotificationList(keyword_editValue, selectedPageNumber);
                    } else
                    {
                        selectedPageNumber = 0;
                        getNotificationList(keyword_editValue, selectedPageNumber);
                    }
                    return true;
                } else
                    return false;
            }
        });

    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Messaging");
        //getActivity().getActionBar().setSubtitle(null);
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
     * Webservice integration to call vehicle list Parameter- pageNo for index
	 * of pages
	 */

    private void getNotificationList(String keyword, final int pageNo)
    {
        hideKeyboard();
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist

            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();


            DataInObject inObj = new DataInObject();
            if (keyword.equals(""))
            {
                parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
                parameterList.add(new Parameter("pageSize", 10, Integer.class));
                parameterList.add(new Parameter("page", pageNo, Integer.class));

                inObj.setMethodname("GetNotifications");
                inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IAuthenticate/GetNotifications");
                inObj.setUrl(Constants.WEBSERVICE_URL);
                inObj.setParameterList(parameterList);
            } else
            {
                parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
                parameterList.add(new Parameter("key", keyword, String.class));
                parameterList.add(new Parameter("pageSize", 10, Integer.class));
                parameterList.add(new Parameter("page", pageNo, Integer.class));

                inObj.setMethodname("SearchNotifications");
                inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IAuthenticate/SearchNotifications");
                inObj.setUrl(Constants.WEBSERVICE_URL);
                inObj.setParameterList(parameterList);
            }


            // Network call
            showLoadingProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {
                    if (result != null)
                    {
                        Helper.Log("soap response", result.toString());
                        try
                        {

                            SoapObject obj = (SoapObject) result;
                            SoapObject inner = (SoapObject) obj.getPropertySafely("Notifications", "default");
                            SoapObject items = (SoapObject) inner.getPropertySafely("items", "default");
                            total_no_of_records = Integer.parseInt(inner.getPropertySafelyAsString("totalMessages", "0"));
                            NotificationListing notificationListing;
                            if (pageNo == 0)
                            {
                                arrayList = new ArrayList<NotificationListing>();
                            }
                            for (int i = 0; i < items.getPropertyCount(); i++)
                            {
                                notificationListing = new NotificationListing();
                                SoapObject notificationObj = (SoapObject) items.getProperty(i);

                                notificationListing.setMessageLogID(notificationObj.getPropertySafelyAsString("MessageLogID", ""));
                                notificationListing.setMessage(notificationObj.getPropertySafelyAsString("message", ""));
                                notificationListing.setHeading(notificationObj.getPropertySafelyAsString("heading", ""));
                                notificationListing.setSent(notificationObj.getPropertySafelyAsString("sent", ""));
                                notificationListing.setSource(notificationObj.getPropertySafelyAsString("source", ""));
                                notificationListing.setIdentity(notificationObj.getPropertySafelyAsString("identity", ""));
                                notificationListing.setIsRead(Boolean.parseBoolean(notificationObj.getPropertySafelyAsString("isRead", "false")));
                                notificationListing.setReadDate(notificationObj.getPropertySafelyAsString("readDate", ""));
                                arrayList.add(notificationListing);
                            }
                            if (pageNo == 0)
                            {
                                pushNotificationAdapter = new PushNotificationAdapter(getContext(), arrayList, new BlogItemEditClickListener()
                                {
                                    @Override
                                    public void onBlogItemClicked(int position)
                                    {
                                        if (arrayList.get(position).getIsRead())
                                        {
                                            callLeadDetailsFragment(position);
                                        } else
                                        {
                                            markNotificationRead(position, arrayList.get(position).getMessageLogID());
                                        }
                                    }
                                });
                                lvNotificationList.setAdapter(pushNotificationAdapter);
                            } else
                            {
                                pushNotificationAdapter.notifyDataSetChanged();
                            }
                            Helper.Log("list size", arrayList.size() + "");
                            hideProgressDialog();
                            if(arrayList != null && arrayList.size() == 0)
                                CustomDialogManager.showOkDialogAutoCancel(getActivity(), getString(R.string.no_record_found));
                        } catch (Exception e)
                        {
                            hideProgressDialog();
                            e.printStackTrace();
                        }
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialogAutoCancel(getActivity(), getString(R.string.no_record_found));
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    private void callLeadDetailsFragment(int position)
    {
        Bundle bundle = new Bundle();
        //  bundle.putInt("leadID", Integer.parseInt(arrayList.get(position).getIdentity()));
        bundle.putInt("leadID", 2728751);
        //   bundle.putInt("leadID", 123456789);
        LeadViewFragment leadViewFragment = new LeadViewFragment();
        leadViewFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.Container, leadViewFragment).addToBackStack(null).commit();
    }

    private void markNotificationRead(final int position, String notificationID)
    {
        hideKeyboard();
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("notificationID", notificationID, String.class));

            DataInObject inObj = new DataInObject();
            inObj.setMethodname("MarkNotificationAsRead");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IAuthenticate/MarkNotificationAsRead");
            inObj.setUrl(Constants.WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showLoadingProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {
                    if (result != null)
                    {
                        Helper.Log("soap response", result.toString());
                        try
                        {
                            SoapObject obj = (SoapObject) result;
                            SoapObject inner = (SoapObject) obj.getPropertySafely("Success", "default");
                            hideProgressDialog();
                            callLeadDetailsFragment(position);
                        } catch (Exception e)
                        {
                            hideProgressDialog();
                            e.printStackTrace();
                        }
                    } else
                    {
                        hideProgressDialog();
                        // CustomDialogManager.showOkDialogAutoCancel(getActivity(), getString(R.string.no_record_found));
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }
}
