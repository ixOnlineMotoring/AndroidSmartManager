package com.nw.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.Vehicle;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class SellFragment extends BaseFragement implements	OnGroupClickListener 
{
	ExpandableListView expandableListView;
	
	SimpleExpandableAdapter adapter;
	ArrayList<Vehicle> vehicleList;
	ArrayList<String> countList;
	ArrayList<VehicleDetails> availableToTradeList;
	String[] headers;
	SellDetailsFrament sellDetailsFrament;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_todo, container, false);
		setHasOptionsMenu(true);
		headers= getResources().getStringArray(R.array.sell_options);
		expandableListView = (ExpandableListView) view;
		expandableListView.setGroupIndicator(null);
		expandableListView.setChildIndicator(null);
		expandableListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		countList = new ArrayList<String>();
		addDummy();
		getCounts();
		adapter = new SimpleExpandableAdapter();
		expandableListView.setAdapter(adapter);
		expandableListView.setOnGroupClickListener(this);
		vehicleList = new ArrayList<Vehicle>();
		availableToTradeList = new ArrayList<VehicleDetails>();
		return view;
	}

	private void addDummy() {
		countList = new ArrayList<String>();
		countList.add("0");
		countList.add("0");
		countList.add("0");
		countList.add("0");
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,int groupPosition, long id) 
	{
		if (!countList.get(groupPosition).equals("0"))
		{
			Bundle bundle=new Bundle();
			bundle.putInt("groupPosition", groupPosition);
			bundle.putString("title",""+headers[groupPosition]);		
			sellDetailsFrament=new SellDetailsFrament();
			sellDetailsFrament.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.Container, sellDetailsFrament).addToBackStack(null).commit();
			return true;
		}
		else
			return false;
	}

	public class SimpleExpandableAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return headers.length;
		}
		@Override
		public int getChildrenCount(int groupPosition) {
			int childCount = 0;
			switch (groupPosition) {
			case 2:
				childCount = availableToTradeList.size();
				break;

			case 1:
				Integer.parseInt(countList.get(groupPosition));
				break;

			default:
				break;
			}
			return childCount;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}
		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = LayoutInflater.from(getActivity()).inflate(R.layout.header_item_s, parent, false);
			}

			TextView groupName = (TextView) v.findViewById(R.id.tvHeaderTitle);
			TextView groupCount = (TextView) v.findViewById(R.id.tvHeaderCount);

			groupName.setText(headers[groupPosition]);
			groupCount.setText(countList.get(groupPosition));

			ImageView ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
			if (getChildrenCount(groupPosition) > 0) {
				if (!isExpanded)
					ivIcon.setImageResource(R.drawable.arrow_right);
				else
					ivIcon.setImageResource(R.drawable.arrow_up);
			}
			if (groupPosition == 0) {
				v.setBackgroundResource(R.color.dark_blue);
			} else {
				v.setBackgroundResource(R.color.gray);
			}
			return v;
		}
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			return null;
		}
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	private void getCounts() {

		if(HelperHttp.isNetworkAvailable(getActivity())){
			showProgressDialog();
			// Add parameters to request in array list
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash",	DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID",	DataManager.getInstance().user.getDefaultClient().getId(),	Integer.class));
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetCounts");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE+ "/ITradeService/GetCounts");
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
	
			// Network call
			new WebServiceTask(getActivity(), inObj, false, new TaskListener() {
				// Network callback
				@Override
				public void onTaskComplete(Object result) {
					try 
					{
						hideProgressDialog();
						Helper.Log("Response", result.toString());
						SoapObject obj = (SoapObject) result;
						SoapObject inner = (SoapObject) obj.getPropertySafely("Results", "default");
						countList.clear();
						countList.add(inner.getPropertySafelyAsString("BiddingEnded", "0"));
						countList.add(inner.getPropertySafelyAsString("ActiveBids","0"));
						countList.add(inner.getPropertySafelyAsString("AvailableToTrade", "0"));
						countList.add(inner.getPropertySafelyAsString("NotAvailableToTrade", "0"));
	
						adapter.notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
						hideProgressDialog();
					}
				}
			}).execute();
		}else{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(sellDetailsFrament!=null)
			sellDetailsFrament.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void onResume() {
		super.onResume();
		showActionBar(getString(R.string.sell));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
