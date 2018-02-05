package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.LeadsAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Leads;
import com.nw.model.Parameter;
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

public class LeadsListFragment extends BaseFragement implements OnItemClickListener
{
	ArrayList<Leads> workInProgressList;
	ArrayList<Leads> unseenList;
	Leads leads;
	LeadsAdapter adapter, adapter2;
	TextView tvCount1;
	TextView tvCount;
	boolean loadMore = false;
	ListView listView1, listView2;
	int totalUnseenLeads = 0, totalWIPLeads;
	int unseenPageCount = 0, wipPageCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_leads_list, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		if (unseenList == null|| unseenList.isEmpty())
			getLeadsList(false, false);
		if (workInProgressList == null||workInProgressList.isEmpty())
			getLeadsList(false, true);
		return view;
	}

	private void initialise(View view)
	{
		listView1 = (ListView) view.findViewById(R.id.listView1);
		listView2 = (ListView) view.findViewById(R.id.listView2);
		setupHeaders();
		workInProgressList = new ArrayList<Leads>();
		unseenList = new ArrayList<Leads>();
		adapter = new LeadsAdapter(getActivity(), R.layout.list_item_leadslist, unseenList);
		adapter2 = new LeadsAdapter(getActivity(), R.layout.list_item_leadslist, workInProgressList);
		listView1.setAdapter(adapter);
		listView2.setAdapter(adapter2);
		listView1.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1){}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				int lastSeenItem = firstVisibleItem + visibleItemCount;
				if (!unseenList.isEmpty())
				{
					if (lastSeenItem == totalItemCount && !loadMore)
					{
						if (unseenList.size() < totalUnseenLeads)
							getLeadsList(true, false);
					}
				}
			}
		});
		listView2.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1){}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				int lastSeenItem = firstVisibleItem + visibleItemCount;
				if (!workInProgressList.isEmpty())
				{
					if (lastSeenItem == totalItemCount && !loadMore)
					{
						if (workInProgressList.size() < totalWIPLeads)
							getLeadsList(true, true);
					}
				}
			}
		});
		listView1.setOnItemClickListener(this);
		listView2.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		if (position != 0)
		{
			Bundle bundle = new Bundle();
			if (parent == listView1)
				bundle.putInt("leadID", adapter.getItem(position - 1).getId());
			else
				bundle.putInt("leadID", adapter2.getItem(position - 1).getId());
			LeadViewFragment leadViewFragment = new LeadViewFragment();
			leadViewFragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.Container, leadViewFragment).addToBackStack(null).commit();
		}
	}

	private void getLeadsList(boolean increaseCount, final boolean seen)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			loadMore = true;
			if (increaseCount)
			{
				if (seen)
					wipPageCount = wipPageCount + 1;
				else
					unseenPageCount = unseenPageCount + 1;
			}
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("keyword", "", String.class));
			parameterList.add(new Parameter("order", 1, Integer.class));
			if (seen)
				parameterList.add(new Parameter("page", wipPageCount, Integer.class));
			else
				parameterList.add(new Parameter("page", unseenPageCount, Integer.class));
			parameterList.add(new Parameter("size", 10, Integer.class));
			parameterList.add(new Parameter("Seen", seen, Boolean.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("List");
			inObj.setNamespace(Constants.LEADS_NAMESPACE);
			inObj.setSoapAction(Constants.LEADS_NAMESPACE + "/ILeadService/List");
			inObj.setUrl(Constants.LEADS_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{
				// Network callback
				@Override
				public void onTaskComplete(Object result)
				{
					if (result == null)
						return;
					try
					{
						Helper.Log("Response", "" + result.toString());
						SoapObject obj = (SoapObject) result;
						SoapObject dataObj = (SoapObject) obj.getPropertySafely("Data", "default");
						SoapObject inner = (SoapObject) dataObj.getPropertySafely("Leads", "default");
						if (seen)
							totalWIPLeads = Integer.parseInt(dataObj.getPrimitivePropertySafelyAsString("Total"));
						else
							totalUnseenLeads = Integer.parseInt(dataObj.getPrimitivePropertySafelyAsString("Total"));

						Leads leads;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							leads = new Leads();
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject leadsObj = (SoapObject) inner.getProperty(i);
								if (leadsObj.hasProperty("ID"))
									leads.setId(Integer.parseInt(leadsObj.getPrimitivePropertySafelyAsString("ID")));
								else
									leads.setId(0);

								if (leadsObj.hasProperty("Name"))
									leads.setUsername(leadsObj.getPrimitivePropertySafelyAsString("Name"));
								else
									leads.setUsername("");

								if (leadsObj.hasProperty("Mobile"))
									leads.setPhoneNumber(leadsObj.getPrimitivePropertySafelyAsString("Mobile"));
								else
									leads.setPhoneNumber("");

								if (leadsObj.hasProperty("Email"))
									leads.setUserEmail(leadsObj.getPrimitivePropertySafelyAsString("Email"));
								else
									leads.setUserEmail("");

								if (leadsObj.hasProperty("Age"))
									leads.setDaysLeft(leadsObj.getPrimitivePropertySafelyAsString("Age"));
								else
									leads.setDaysLeft("");

								if (leadsObj.getPropertySafely("Vehicle") instanceof SoapObject)
								{
									SoapObject vehicleObject = (SoapObject) leadsObj.getPropertySafely("Vehicle");
									if (vehicleObject.hasProperty("Type"))
										leads.setVehicleType(Helper.getVehicleType(vehicleObject.getPrimitivePropertySafelyAsString("Type")));
									if (vehicleObject.hasProperty("Matched"))
										leads.setMatched(Boolean.parseBoolean(vehicleObject.getPrimitivePropertySafelyAsString("Matched")));
									if (vehicleObject.hasProperty("MakeAsked"))
										leads.setMakeAsked(vehicleObject.getPrimitivePropertySafelyAsString("MakeAsked"));
									if (vehicleObject.hasProperty("ModelAsked"))
										leads.setModelAsked(vehicleObject.getPrimitivePropertySafelyAsString("ModelAsked"));
									if (vehicleObject.hasProperty("YearAsked"))
										leads.setYearAsked(vehicleObject.getPrimitivePropertySafelyAsString("YearAsked"));
									if (vehicleObject.hasProperty("MileageAsked"))
										leads.setMileageAsked(vehicleObject.getPrimitivePropertySafelyAsString("MileageAsked"));
									if (vehicleObject.hasProperty("ColourAsked"))
										leads.setColourAsked(vehicleObject.getPrimitivePropertySafelyAsString("ColourAsked"));
									if (vehicleObject.hasProperty("PriceAsked"))
										leads.setPriceAsked(vehicleObject.getPrimitivePropertySafelyAsString("PriceAsked"));

									leads.setVehicleDescription("No vehicle info.");
								}
								else
									leads.setVehicleDescription(leadsObj.getPropertySafelyAsString("Vehicle", ""));

								leads.setType(Leads.ITEM);

								if (leadsObj.getPropertySafelyAsString("Seen", "false").equals("false"))
									unseenList.add(leads);
								else
									workInProgressList.add(leads);
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						mergeLists();
						loadMore = false;
						if (seen == false)
							adapter.notifyDataSetChanged();
						else
							adapter2.notifyDataSetChanged();
						tvCount.setText("" + totalUnseenLeads);
						tvCount1.setText("" + totalWIPLeads);
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	private void mergeLists()
	{
		leads = new Leads();
		leads.setType(Leads.SECTION);
		leads.setHeaderTitle("Unseen");
		leads.setCount(unseenList.size());
		leads = new Leads();
		leads.setType(Leads.SECTION);
		leads.setHeaderTitle("Work In Progress");
		leads.setCount(workInProgressList.size());
	}

	private void setupHeaders()
	{
		View header1 = getActivity().getLayoutInflater().inflate(R.layout.header_item_s, listView1, false);
		TextView tvTitle = (TextView) header1.findViewById(R.id.tvHeaderTitle);
		tvCount = (TextView) header1.findViewById(R.id.tvHeaderCount);
		tvTitle.setText("Unseen");
		tvCount.setText("" + totalUnseenLeads);
		listView1.addHeaderView(header1);
		View header2 = getActivity().getLayoutInflater().inflate(R.layout.header_item_s, listView2, false);
		TextView tvTitle1 = (TextView) header2.findViewById(R.id.tvHeaderTitle);
		tvCount1 = (TextView) header2.findViewById(R.id.tvHeaderCount);
		tvTitle1.setText("Work in progress");
		tvCount1.setText("" + totalWIPLeads);
		listView2.addHeaderView(header2);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("My Leads");
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
}
