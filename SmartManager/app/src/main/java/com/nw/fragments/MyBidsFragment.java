package com.nw.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.interfaces.DateListener;
import com.nw.model.DataInObject;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyBidsFragment extends BaseFragement implements OnItemClickListener, OnClickListener{

	String[] headers;
	ListView lvMyBids;
	MyBidsAdapter adapter;
	EditText edFromDate, edToDate;
	Button btnSearch;
	boolean isSearchDone=false;
	int losingBidsCount=0, winningBidsCount=0, wonCount=0, lostCount=0;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_my_bids, container,false);
		setHasOptionsMenu(true);
		headers= getResources().getStringArray(R.array.my_bids_options);
		initialise(view);
		
		return view;
	}
	
	@SuppressLint("SimpleDateFormat")
	private void initialise(View view){
		edFromDate= (EditText) view.findViewById(R.id.edStartDate);
		edToDate= (EditText) view.findViewById(R.id.edEndDate);
		btnSearch= (Button) view.findViewById(R.id.bSearch);
		
		edFromDate.setOnClickListener(this);
		edToDate.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		
		Calendar calendar= Calendar.getInstance();
		String today= new SimpleDateFormat("dd MMM yyyy HH:mm").format(calendar.getTime());
		
		calendar.add(Calendar.MONTH, -1);
		String pastDate=new SimpleDateFormat("dd MMM yyyy HH:mm").format(calendar.getTime());
		
		edFromDate.setText(pastDate);
		edToDate.setText(today);
		
		btnSearchClicked(false);
		
		lvMyBids= (ListView) view.findViewById(R.id.listview);
		adapter= new MyBidsAdapter(getActivity(), 0, headers);
		lvMyBids.setAdapter(adapter);
		lvMyBids.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		if(isSearchDone){
			
			switch (position) {
			case 0:
				if(losingBidsCount!=0)
					navigateToDetails(position);
				break;
			case 1:
				if(winningBidsCount!=0)
					navigateToDetails(position);
				break;
			case 2:
				if(wonCount!=0)
					navigateToDetails(position);
				break;
				
			case 3:
				if(lostCount!=0)
					navigateToDetails(position);
				break;

			default:
				break;
			}
		}
	}
	
	private void navigateToDetails(int position){
		Bundle bundle=new Bundle();
		bundle.putInt("Position", position);
		bundle.putString("title",""+headers[position]);	
		bundle.putString("start_date", edFromDate.getText().toString());
		bundle.putString("end_date", edToDate.getText().toString());
		MyBidsDetailsFragment detailFragment=new MyBidsDetailsFragment();
		detailFragment.setArguments(bundle);
		getFragmentManager()
					.beginTransaction()
					.replace(R.id.Container, detailFragment)
					.addToBackStack(null)
					.commit();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edStartDate:
			DatePickerFragment startDate= new DatePickerFragment();
			startDate.setDateListener(new DateListener() 
			{
				@Override
				public void onDateSet(int year, int monthOfYear,int dayOfMonth) 
				{
					Calendar cal= Calendar.getInstance();
					edFromDate.setText(Helper.showDateTime(Helper.createStringDateTime(year, monthOfYear, dayOfMonth, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))));
				}
			});
			startDate.show(getActivity().getFragmentManager(), "datePicker");
			break;

		case R.id.edEndDate:
			DatePickerFragment endDate= new DatePickerFragment();
			endDate.setDateListener(new DateListener() 
			{
				@Override
				public void onDateSet(int year, int monthOfYear,int dayOfMonth) 
				{
					Calendar cal= Calendar.getInstance();
					edToDate.setText(Helper.showDateTime(Helper.createStringDateTime(year, monthOfYear, dayOfMonth, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))));
				}
			});
			endDate.show(getActivity().getFragmentManager(), "datePicker");
			break;
			
		case R.id.bSearch:
			btnSearchClicked(true);
			
			break;
		default:
			break;
		}
	}
	
	private void btnSearchClicked(boolean flag){
		if(TextUtils.isEmpty(edFromDate.getText().toString())){
			Helper.showToast(getString(R.string.please_select_start_date), getActivity());
			return;
		}else if(TextUtils.isEmpty(edToDate.getText().toString())){
			Helper.showToast(getString(R.string.please_select_end_date), getActivity());
			return;
		}else{
			getBuyingCount(flag);
		}
		isSearchDone=true;
	}
	private class MyBidsAdapter extends ArrayAdapter<String>{

		public MyBidsAdapter(Context context, int resource, String[] objects) {
			super(context, resource, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView= LayoutInflater.from(getActivity()).inflate(R.layout.header_item_s, parent, false);
			}
			TextView tvTitle= (TextView) convertView.findViewById(R.id.tvHeaderTitle);
			TextView tvCount= (TextView) convertView.findViewById(R.id.tvHeaderCount);
			
			tvTitle.setText(headers[position]);
			switch (position) {
			case 0:
				convertView.setBackgroundColor(getResources().getColor(R.color.dark_blue));
				tvCount.setText(losingBidsCount+"");
				break;

			case 1:
				convertView.setBackgroundColor(getResources().getColor(R.color.gray));
				tvCount.setText(winningBidsCount+"");
				break;
				
			case 2:
				convertView.setBackgroundColor(getResources().getColor(R.color.gray));
				tvCount.setText(wonCount+"");
				break;
				
			case 3:
				convertView.setBackgroundColor(getResources().getColor(R.color.gray));
				tvCount.setText(lostCount+"");
				break;
			default:
				break;
			}
			return convertView;
		}
	}
	
	private void getBuyingCount(boolean flag) {
		if(HelperHttp.isNetworkAvailable(getActivity())){
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash",DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID",DataManager.getInstance().user.getDefaultClient().getId(),Integer.class));
			parameterList.add(new Parameter("DateTimeFrom",edFromDate.getText().toString(),String.class));
			parameterList.add(new Parameter("DateTimeTo",edToDate.getText().toString(),String.class));
			
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetBuyingCounts");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE+ "/ITradeService/GetBuyingCounts");
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
	
			// Network call
			new WebServiceTask(getActivity(), inObj, flag, new TaskListener() {
	
				// Network callback
				@Override
				public void onTaskComplete(Object result) {
					try {
						hideProgressDialog();
						Helper.Log("Response", result.toString());
						SoapObject outer= (SoapObject) result;
						SoapObject inner= (SoapObject) outer.getProperty("Results");
						losingBidsCount= Integer.parseInt(inner.getPropertySafelyAsString("LosingBids", ""));
						winningBidsCount= Integer.parseInt(inner.getPropertySafelyAsString("WinningBids", ""));
						wonCount= Integer.parseInt(inner.getPropertySafelyAsString("BidsWon", ""));
						lostCount= Integer.parseInt(inner.getPropertySafelyAsString("BidsLost", ""));
						adapter.notifyDataSetChanged();
						
					} catch (Exception e) {
						e.printStackTrace();
						hideProgressDialog();
					}
				}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().getActionBar().setTitle("My Bids");
		//getActivity().getActionBar().setSubtitle(null);
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
