package com.nw.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nw.adapters.AuditHistoryAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.Audit;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomTextView;
import com.nw.widget.CustomTextViewLight;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class AuditHistoryFragment extends BaseFragement implements OnClickListener
{
	ListView lvAuditHistory;
	AuditHistoryAdapter auditHistoryAdapter;
	CustomTextViewLight tvEmailList;
	CustomTextView tvNoteHistory;
	Button bSubmit;
	ImageView ivArrowIcon;
	LinearLayout llTopHeader ;
	RelativeLayout rlEmailOption;
	ArrayList<Audit> audits;
	int totalPagesScan=-1,selectedPageNumber=0,countForAuditHistory=0;
	boolean isLoadMore=false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_audit_history, container, false);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		showActionBar(getString(R.string.audit_history));
		if (audits == null)
		{
			audits = new ArrayList<Audit>();
		}
		setHasOptionsMenu(true);
		init(view);
		if(audits.size()==0)
		{
		getAuditedHistory(selectedPageNumber);
		}
		return view;
	}

	private void init(View view)
	{
		tvEmailList=(CustomTextViewLight) view.findViewById(R.id.tvEmailList);
		tvNoteHistory =(CustomTextView) view.findViewById(R.id.tvNoteHistory);
		bSubmit=(Button) view.findViewById(R.id.bSubmit);
		ivArrowIcon = (ImageView) view.findViewById(R.id.ivArrowIcon);
		rlEmailOption=(RelativeLayout)view.findViewById(R.id.rlEmailOption);
		llTopHeader = (LinearLayout) view.findViewById(R.id.llTopHeader);
		llTopHeader.setVisibility(View.GONE);
		tvEmailList.setOnClickListener(this);
		ivArrowIcon.setOnClickListener(this);
		lvAuditHistory = (ListView) view.findViewById(R.id.lvAuditHistory);
		auditHistoryAdapter = new AuditHistoryAdapter(getActivity(),0,audits);
		lvAuditHistory.setAdapter(auditHistoryAdapter);
		lvAuditHistory.setOnScrollListener(new OnScrollListener() {
			   
			   @Override
			   public void onScrollStateChanged(AbsListView view, int scrollState) {}
			   @Override
			   public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			    int lastSeenItem= firstVisibleItem+visibleItemCount;
			    if(!audits.isEmpty()){
			     if(lastSeenItem== totalItemCount && !isLoadMore){
			      if(selectedPageNumber<totalPagesScan){
			       selectedPageNumber++;
			      getAuditedHistory(selectedPageNumber);
			      }
			     }
			    }
			   }
			  });
		lvAuditHistory.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position ,long arg3)
			{
				AuditHistoryItemsFragment historyFragment = new AuditHistoryItemsFragment();
				Bundle bundle = new Bundle();
				bundle.putString("date", audits.get(position).getAuditDate());
				historyFragment.setArguments(bundle);
				getFragmentManager().beginTransaction().replace(R.id.Container,historyFragment).addToBackStack(null).commit();
			}
		});		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar(getString(R.string.audit_history));
		//getActivity().getActionBar().setSubtitle(null);
	}
	
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.tvEmailList:
			case R.id.ivArrowIcon:	
				if(rlEmailOption.getVisibility()!=View.GONE){
					rlEmailOption.setVisibility(View.GONE);
					ivArrowIcon.setRotation(-90);}
				else{
					rlEmailOption.setVisibility(View.VISIBLE);
					ivArrowIcon.setRotation(0);
					hideKeyboard();
				}
				break;				
			case R.id.bSubmit:				
				
				break;
		}
	}
	
	private void getAuditedHistory(int pageNUmber)
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
				showProgressDialog();
		//Add parameters to request in arraylist
				ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
				parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
				parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
				parameterList.add(new Parameter("Page",pageNUmber , Integer.class));
				parameterList.add(new Parameter("RecordCount", 13, Integer.class));
				
		//create web service inputs
				DataInObject inObj = new DataInObject();
				inObj.setMethodname("AuditHistory");
				inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
				inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/AuditHistory");
				inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
				inObj.setParameterList(parameterList);
				
		//Network call
         new WebServiceTask(getActivity(), inObj, true,new TaskListener()
         {
 			@Override
 			public void onTaskComplete(Object result) 
 			{
	 			try{
	 	 			Helper.Log("soap response", result.toString());
	 	 			hideProgressDialog();
	 				SoapObject obj= (SoapObject) result;
	 				SoapObject response= (SoapObject) obj.getPropertySafely("Response", "");
	 				SoapObject inner= (SoapObject) response.getPropertySafely("AuditHistories", "");
	 				Audit audit;
	 				for(int i=0;i<inner.getPropertyCount();i++){
	 					audit = new Audit();
	 					if(inner.getProperty(i) instanceof SoapObject)
	 					{
	 						SoapObject auditsObj= (SoapObject) inner.getProperty(i);
	 						audit.setAuditDate(auditsObj.getPropertySafelyAsString("Date",""));
	 						audit.setAuditedVehicles(auditsObj.getPropertySafelyAsString("Count",""));
	 	 					audits.add(audit);
	 					}
	 				}
	 				countForAuditHistory = Integer.parseInt(response.getPropertySafelyAsString("Count"));
	 				totalPagesScan =(int) countForAuditHistory/10;
	 				if (audits.size()!=0)
					{
						auditHistoryAdapter.notifyDataSetChanged();
					}
	 				else {
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found),new DialogListener()
						{
							@Override
							public void onButtonClicked(int type)
							{
								getActivity().getFragmentManager().popBackStack();
							}
						});
					}
	 			}
 				catch (Exception e) 
 				{
 					e.printStackTrace();
 					CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found),new DialogListener()
					{
						@Override
						public void onButtonClicked(int type)
						{
							getActivity().getFragmentManager().popBackStack();
						}
					});
 				}
 			}
 		}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				getActivity().getFragmentManager().popBackStack();;
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
