package com.nw.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.nw.adapters.LeadsListAdapter;
import com.nw.interfaces.SectionLoadMoreListner;
import com.nw.model.DataInObject;
import com.nw.model.Leads;
import com.nw.model.Parameter;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.activity.CustomerActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LeadListNew extends BaseFragement implements SectionLoadMoreListner, OnClickListener, OnChildClickListener
{
	// Declare variable 
	private final String UN_SEEN = "Unseen";
	private final String WIP = "Work in progress";
	ExpandableListView listView;
	HashMap<String, ArrayList<Leads>> data;
	boolean loadMore = false;
	int totalUnseenLeads , totalWIPLeads,unseenPageCount , wipPageCount;
	ArrayList<Leads> workInProgressList;
	ArrayList<Leads> unseenList;
	LeadsListAdapter adapter;
	int[] childCount = new int[] { 0, 0 };
	EditText edSortBy,edKeyword;
	private int prev = -1;
	boolean defaultSortOrder=true;
	String lastUnseenSort="Date Received",lastWIPSort="Update Urgency",previousKeyword,previousUnseenKeyword="",previousWIPKeyword="";
	int lastSearchGroup;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_leads_list, container, false);
		setHasOptionsMenu(true);
		edKeyword=(EditText) view.findViewById(R.id.edKeyword);
		edSortBy=(EditText) view.findViewById(R.id.edSortBy);
		edSortBy.setOnClickListener(this);
		listView = (ExpandableListView) view.findViewById(R.id.expandableListView1);
		
		// for unseen part
		listView.setOnGroupExpandListener(new OnGroupExpandListener() {

	            @Override
	            public void onGroupExpand(int groupPosition) 
	            {
	            	if(lastSearchGroup!=groupPosition)
	            //		edKeyword.setText("");
	            	 if(prev!=-1 && groupPosition!=prev)
		                {
		                	listView.collapseGroup(prev); 
		                }          	
	            	if(groupPosition==0)
	            	{
						edSortBy.setText(""+lastUnseenSort);
						edKeyword.setText(""+previousUnseenKeyword);	
	            	}
					else if(groupPosition==1)
					{
						edSortBy.setText(""+lastWIPSort);
						edKeyword.setText(""+previousWIPKeyword);
	            	}
	            	prev=groupPosition;        	
	            }
	        });
		
		// for WIP part
		listView.setOnGroupCollapseListener(new OnGroupCollapseListener()
			{
				
				@Override
				public void onGroupCollapse(int groupPosition)
				{
					if(groupPosition==0)
					{
						prev=0;
						lastUnseenSort=edSortBy.getText().toString();
					}else if(groupPosition==1)
						prev=1;
						lastWIPSort=edSortBy.getText().toString();
						
	                if(prev==groupPosition)
	                {
	                	prev=-1;
	                //	edSortBy.setText("");    
	                }
				}
			});
		
		/*listView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,int groupPosition, long id)
			{
				if (groupPosition==0) {
					if (previousKeyword.equals("")) 
            		{
            			edKeyword.setText("");
            			previousKeyword ="";
					}
				}else {
					edKeyword.setText(previousKeyword);
				}
				return true;
			}
		});*/
		listView.setGroupIndicator(null);
		listView.setOnChildClickListener(this);
		edKeyword.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) 
				{
					hideKeyboard();
					if(prev==0)
					{
						lastSearchGroup=0;
						unseenPageCount=0;
						totalUnseenLeads=0;
						unseenList.clear();
						adapter.notifyDataSetChanged();
						// unseen
						previousUnseenKeyword = edKeyword.getText().toString();
						getLeadsList(false, false,defaultSortOrder);
					}
					else if(prev==1)
					{
						lastSearchGroup=1;
						wipPageCount=0;
						totalWIPLeads=0;
						workInProgressList.clear();
						previousWIPKeyword =edKeyword.getText().toString();
						adapter.notifyDataSetChanged();
						// wip
						getLeadsList(false, true,defaultSortOrder);
					}
					return true;
				} else
					return false;
			}
		});
		edKeyword.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if (TextUtils.isEmpty(s)) 
				{
					if (prev==0)
					{
						previousUnseenKeyword="";
						if (unseenList.isEmpty())
						{
						getLeadsList(false, false,defaultSortOrder);
						}
					} else {
						previousWIPKeyword="";
						if (workInProgressList.isEmpty())
						{
							getLeadsList(false, true,defaultSortOrder);
						}
					}
					listView.collapseGroup(prev); 
					adapter.notifyDataSetChanged();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				
			}
		});
	/*	listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE 
			            && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() -
			            listView.getFooterViewsCount()) >= (data.size() - 1)) {

			        onLoadMore(false);
			    }
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});*/
		getdata();
		return view;
	}

	private void getdata()
	{
		if(workInProgressList==null)
			workInProgressList = new ArrayList<Leads>();
		if(unseenList==null)
			unseenList = new ArrayList<Leads>();
		if(data==null)
		{
			data = new HashMap<String, ArrayList<Leads>>();
			data.put(UN_SEEN, unseenList);
			data.put(WIP, workInProgressList);
		}
		adapter = new LeadsListAdapter(getActivity(), data, childCount);
		adapter.setLoadMoreListener(this);
		listView.setAdapter(adapter);
		if (unseenList == null || unseenList.isEmpty()||CustomerActivity.UnseenleadUpdated==true)
		{
			unseenList.clear();
			CustomerActivity.UnseenleadUpdated=false;	
			getLeadsList(false, false,defaultSortOrder);
		}			
		if (workInProgressList == null || workInProgressList.isEmpty()||CustomerActivity.WipleadUpdated==true)
		{
			workInProgressList.clear();
			CustomerActivity.WipleadUpdated=false;
			getLeadsList(false, true,defaultSortOrder);
		}
		
	}

	private  void getLeadsList(boolean increaseCount, final boolean seen,boolean defaultSort)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			listView.setEnabled(false);
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
			parameterList.add(new Parameter("keyword", edKeyword.getText().toString(), String.class));
			if(defaultSort)
				parameterList.add(new Parameter("order", getDefaultSortOrder(seen), Integer.class));
			else
				parameterList.add(new Parameter("order", getSortOrder(edSortBy.getText().toString()), Integer.class));

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
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{
				// Network callback
				@Override
				public void onTaskComplete(Object result)
				{
					if (result == null)
						return;
					try
					{
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
								if (leadsObj.hasProperty("LastUpdateDate"))
									leads.setLastUpdateDate(leadsObj.getPrimitivePropertySafelyAsString("LastUpdateDate"));
								if (leadsObj.hasProperty("LastUpdate"))
									leads.setLastUpdate(leadsObj.getPrimitivePropertySafelyAsString("LastUpdate"));
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
									leads.setVehicleDescription(leads.getYearAsked()+" "+leads.getModelAsked()+" "+leads.getMakeAsked());
								}else if (TextUtils.isEmpty(leadsObj.getPropertySafelyAsString("Vehicle", ""))){
									leads.setVehicleDescription("Vehicle info?");
								}
								else {
									leads.setVehicleDescription(leadsObj.getPropertySafelyAsString("Vehicle", ""));
									}
								leads.setType(Leads.ITEM);
								if (leadsObj.hasProperty("TradeIn"))
									leads.setTradeIn(dataObj.getPrimitivePropertySafelyAsString("TradeIn"));
								else
									leads.setTradeIn("");
								if (seen==false){
									boolean isInList = isInList(leads,unseenList);
									if (!isInList)
									{
										unseenList.add(leads);
									}
								}else{
									boolean isInList = isInList(leads,workInProgressList);
									if (!isInList)
									{
										workInProgressList.add(leads);
									}
								}
							}
						}
					}
					catch (Exception e){
						e.printStackTrace();
						hideProgressDialog();
						Toast.makeText(getActivity(),getString(R.string.no_record_found),Toast.LENGTH_SHORT).show();
					}
					finally
					{
						loadMore = false;
						childCount[0] = totalUnseenLeads;
						childCount[1] = totalWIPLeads;
						addloadMoreView(seen);
						if (seen == false)
						{
							data.put(UN_SEEN, unseenList);
							adapter.notifyDataSetChanged();
						}
						else{
							data.put(WIP, workInProgressList);
							adapter.notifyDataSetChanged();
						}
						hideProgressDialog();
						listView.setEnabled(true);
					}
				}
			}).execute();
		}
		else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	protected boolean isInList(Leads leads, ArrayList<Leads> list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).getId()==leads.getId()){
				return true;
			}
		}
		return false;
	}

	private void addloadMoreView(boolean seen)
	{
		if (seen == false)
		{
			// unseen list
			if (!unseenList.isEmpty())
			{
				if (hasLoadMore(false))
				{
					removeLoadMore(false);
				}
				addLoadMore(false);
			}
		}
		else
		{
			if (!workInProgressList.isEmpty())
			{
				if (hasLoadMore(true))
				{
					removeLoadMore(true);
				}
				addLoadMore(true);
			}
		}
	}

	private boolean hasLoadMore(boolean seen)
	{
		boolean result = false;
		if (seen == false)
		{
			for (Leads lead : unseenList)
			{
				if (lead.isLoadMore())
				{
					result = true;
					break;
				}
			}
		}
		else
		{
			for (Leads lead : workInProgressList)
			{
				if (lead.isLoadMore())
				{
					result = true;
					break;
				}
			}
		}
		return result;
	}

	private void removeLoadMore(boolean seen)
	{
		if (seen == false)
		{
			int postition = -1;
			for (int i = 0; i < unseenList.size(); i++)
			{
				if (unseenList.get(i).isLoadMore())
					postition = i;
			}
			if (postition != -1)
				unseenList.remove(postition);
		}
		else
		{
			int postition = -1;
			for (int i = 0; i < workInProgressList.size(); i++)
			{
				if (workInProgressList.get(i).isLoadMore())
					postition = i;
			}
			if (postition != -1)
				workInProgressList.remove(postition);
		}
	}

	private void addLoadMore(boolean seen)
	{
		if (seen == false)
		{
			if (unseenList.size() < totalUnseenLeads)
			{
				Leads lead = new Leads();
				lead.setLoadMore(true);
				unseenList.add(lead);
			}
		}
		else
		{
			if (workInProgressList.size() < totalWIPLeads)
			{
				Leads lead = new Leads();
				lead.setLoadMore(true);
				workInProgressList.add(lead);
			}
		}
	}
	
	@Override
	public void onLoadMore(boolean seen)
	{
		getLeadsList(true, seen,defaultSortOrder);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("My Leads");
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.edSortBy:
				showSortOption(v);				
				break;
		}
	}
	
	private void showSortOption(final View v)
	{
		final String [] sort_Options;
		if(prev==0)
		{
			if(unseenList.isEmpty())
				return;
			sort_Options=new String[] {"Date Received","Alphabetical"};
			showDropdown(v, sort_Options);
		}
		else if(prev==1)
		{
			if(workInProgressList.isEmpty())
				return;
			sort_Options=new String[] {"Update Urgency","Alphabetical"};
			showDropdown(v, sort_Options);
		}else
		{
			return;
		}
	}
	
	private void showDropdown(final View v,final String [] sort_Options)
	{
		ArrayAdapter<String>adapter=new ArrayAdapter<String>(getActivity(), R.layout.list_item_text2, R.id.tvText, sort_Options);
		Helper.showDropDown(v, adapter, new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)
			{
				defaultSortOrder=false;
				if(v.getTag()==null)
				{
					edSortBy.setText(""+sort_Options[position]);
					//sortOrder=position+1;
					v.setTag(""+sort_Options[position]);
				}
				else
				{
					edSortBy.setText(""+sort_Options[position]);
					v.setTag(""+sort_Options[position]);
					if(position==0)
					{
						//sortOrder=position+1;
						v.setTag(null);
					}
					else
					{
						v.setTag(""+sort_Options[position]);
						//sortOrder=position;
					}
				}
				if(prev==0)
				{
					unseenPageCount=0;
					totalUnseenLeads=0;
					unseenList.clear();
					// unseen
					getLeadsList(false, false,defaultSortOrder);
				}
				else if(prev==1)
				{
					wipPageCount=0;
					totalWIPLeads=0;
					workInProgressList.clear();
					// wip
					getLeadsList(false, true,defaultSortOrder);
				}
			}					
		});
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
	{
		hideProgressDialog();
		Bundle bundle = new Bundle();
		bundle.putInt("leadID", adapter.getChild(groupPosition, childPosition).getId());
		bundle.putInt("groupPosition", groupPosition);
		LeadViewFragment leadViewFragment = new LeadViewFragment();
		leadViewFragment.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(R.id.Container, leadViewFragment).addToBackStack(null).commit();
		return true;
	}
	
	private int getSortOrder(String order)
	{
		int returnValue=0;
		if(prev==0){
			if(order.equals("Date Received"))
				returnValue= 1;
			if(order.equals("Alphabetical"))
				returnValue=  2;
		}else if(prev==1){
			if(order.equals("Update Urgency"))
				returnValue= 3;
			if(order.equals("Alphabetical"))
				returnValue= 2;
		}
		return returnValue;
	}
	
	private int getDefaultSortOrder(boolean seen)
	{
		if(seen)
			return 3;
		else
			return 1;
	}

}
