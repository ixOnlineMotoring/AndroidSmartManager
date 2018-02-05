package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.LeadPoolAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.OnListItemClickListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
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

import java.text.NumberFormat;
import java.util.ArrayList;

public class LeadPoolFragment extends BaseFragement
{
    ListView lvLeadPool;
    TextView tvNoteForModel;
    LeadPoolAdapter leadPoolAdapter;
    ArrayList<SmartObject> leadPoolList, groupList;
    VehicleDetails vehicleDetails;
    ArrayList<String> values = new ArrayList<String>();
    String[] listItems = new String[10];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_lead_pool, container, false);
        setHasOptionsMenu(true);
        if (groupList == null)
        {
            getGroupList();
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
        lvLeadPool = (ListView) view.findViewById(R.id.lvLeadPool);
        tvNoteForModel = (TextView) view.findViewById(R.id.tvNoteForModel);
        tvNoteForModel.setText("The following lead prospects are/were interested in a new or used " + vehicleDetails.getModelName());
        if (leadPoolList != null)
        {
            leadPoolAdapter = new LeadPoolAdapter(getActivity(), R.layout.list_item_lead_pool, leadPoolList, new OnListItemClickListener()
            {
                @Override
                public void onClick(int position)
                {
                    if (position != 1 && leadPoolList.get(position).getId() != 0)
                    {
                        ActiveLeadFragment activeLeadFragment = new ActiveLeadFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isActiveClicked", true);
                        bundle.putBoolean("isGroup", false);
                        bundle.putString("pageName", listItems[0] + " Active Leads");
                        bundle.putParcelable("vehicleDetails", vehicleDetails);
                        activeLeadFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.Container, activeLeadFragment).addToBackStack("").commit();
                    }
                }
            }, new OnListItemClickListener()
            {
                @Override
                public void onClick(int position)
                {
                    if (Integer.parseInt(leadPoolList.get(position).getType()) != 0)
                    {
                        ActiveLeadFragment activeLeadFragment = new ActiveLeadFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isActiveClicked", false);
                        if (position == 0)
                        {
                            bundle.putBoolean("isGroup", false);
                            bundle.putString("pageName", listItems[0] + " Lost Leads");
                        } else
                        {
                            bundle.putBoolean("isGroup", true);
                            bundle.putString("pageName", listItems[1] + " Lost Leads");
                        }
                        bundle.putParcelable("vehicleDetails", vehicleDetails);
                        activeLeadFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.Container, activeLeadFragment).addToBackStack("").commit();
                    }
                }
            });
            lvLeadPool.setAdapter(leadPoolAdapter);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Lead Pool");
    }

    private void getGroupList()
    {
        groupList = new ArrayList<SmartObject>();
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("GetClientCorprateGroups");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IAuthenticate/GetClientCorprateGroups");
            inObj.setUrl(Constants.WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    groupList.clear();
                    try
                    {
                        Helper.Log("GroupList response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Results");
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject groupObj = (SoapObject) inner.getProperty(i);
                            String groupid = groupObj.getPropertySafelyAsString("ID", "0");
                            String groupname = groupObj.getPropertySafelyAsString("Name", "-");
                            groupList.add(i, new SmartObject(Integer.parseInt(groupid), groupname));
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                    } finally
                    {
                        hideProgressDialog();
                        if (leadPoolList == null && !groupList.isEmpty())
                        {
                            getLeadPoolData();
                        } else
                        {
                            CustomDialogManager.showOkDialog(getActivity(), "No associated group found", new DialogListener()
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
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getLeadPoolData()
    {
        leadPoolList = new ArrayList<SmartObject>();
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("modelID", vehicleDetails.getModelId(), Integer.class));
            parameterList.add(new Parameter("year", vehicleDetails.getYear(), Integer.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("GroupID", groupList.get(0).getId(), Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadLeadPoolSummary");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadLeadPoolSummary");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            //Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("LeadPoolSummary");
                        SmartObject object;
                        listItems[0] = inner.getPropertySafelyAsString("ClientName", "ClientName?");
                        listItems[1] = inner.getPropertySafelyAsString("GroupName", "GroupName");
                        values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("ClientActiveLeads")).intValue());
                        values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("ClientLostLeads")).intValue());
                        values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("GroupActiveLeads")).intValue());
                        values.add("" + NumberFormat.getInstance().parse(inner.getPropertySafelyAsString("GroupLostLeads")).intValue());
                        for (int i = 0; i < 2; i++)
                        {
                            object = new SmartObject();
                            object.setName(listItems[i]);
                            object.setId(Integer.parseInt((values.get(i))));
                            object.setType(values.get(i + 1));
                            leadPoolList.add(object);
                        }
                        leadPoolAdapter = new LeadPoolAdapter(getActivity(), R.layout.list_item_lead_pool, leadPoolList, new OnListItemClickListener()
                        {
                            @Override
                            public void onClick(int position)
                            {
                                if (position != 1 && leadPoolList.get(position).getId() != 0)
                                {
                                    ActiveLeadFragment activeLeadFragment = new ActiveLeadFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("isActiveClicked", true);
                                    bundle.putBoolean("isGroup", false);
                                    bundle.putString("pageName", listItems[0] + " Active Leads");
                                    bundle.putParcelable("vehicleDetails", vehicleDetails);
                                    activeLeadFragment.setArguments(bundle);
                                    getFragmentManager().beginTransaction().replace(R.id.Container, activeLeadFragment).addToBackStack("").commit();
                                }
                            }
                        }, new OnListItemClickListener()
                        {
                            @Override
                            public void onClick(int position)
                            {
                                if (leadPoolList.get(position).getType() != null)
                                {
                                    if (Integer.parseInt(leadPoolList.get(position).getType()) != 0)
                                    {
                                        ActiveLeadFragment activeLeadFragment = new ActiveLeadFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("isActiveClicked", false);
                                        if (position == 0)
                                        {
                                            bundle.putBoolean("isGroup", false);

                                            bundle.putString("pageName", listItems[0] + " Lost Leads");
                                        } else
                                        {
                                            bundle.putBoolean("isGroup", true);
                                            bundle.putString("pageName", listItems[1] + " Lost Leads");
                                        }
                                        bundle.putParcelable("vehicleDetails", vehicleDetails);
                                        activeLeadFragment.setArguments(bundle);
                                        getFragmentManager().beginTransaction().replace(R.id.Container, activeLeadFragment).addToBackStack("").commit();
                                    }
                                }
                            }
                        });
                        lvLeadPool.setAdapter(leadPoolAdapter);
                        hideProgressDialog();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

}
