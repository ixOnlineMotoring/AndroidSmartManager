
package com.nw.fragments;

import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.nw.adapters.AuditListAdapter;
import com.nw.interfaces.AuditImageClickListener;
import com.nw.interfaces.DialogListener;
import com.nw.model.Audit;
import com.nw.model.DataInObject;
import com.nw.model.MyImage;
import com.nw.model.Parameter;
import com.nw.model.StockAudit;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomEditText;
import com.nw.widget.CustomTextViewLight;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AuditedTodayFragment extends BaseFragement implements OnClickListener
{
	TextView tvNote;
	CustomTextViewLight tvEmailList;
	Button bSubmit;
	CustomEditText etEmailToday;
	ListView listview;
	String[] Groups=new String[]{"Audited today but no matching VIN found","These vehicles require a VIN scan"};
	RelativeLayout rlEmailOption;
	ImageView ivArrowIcon;
	HashMap<String, ArrayList<StockAudit>> data;
	LinearLayout llTopHeader;
	int firstGroup=1,secondGroup=2, lastSearchGroup, prev = -1,totalPagesScan=-1,selectedPageNumber=0,countForAuditedToday=0;
	ArrayList<Audit> audits;
	AuditListAdapter auditListAdapter;
	boolean isLoadMore =false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view=inflater.inflate(R.layout.fragment_audit_list, container,false);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		showActionBar(getString(R.string.audited_today));
		setHasOptionsMenu(true);
		audits = new ArrayList<Audit>();
		initView(view);
		return view;
	}
	
	private void initView(View view)
	{
		tvEmailList=(CustomTextViewLight) view.findViewById(R.id.tvEmailList);
		bSubmit=(Button) view.findViewById(R.id.bnSubmitToday);
		bSubmit.setOnClickListener(this);
		etEmailToday=(CustomEditText) view.findViewById(R.id.etEmailToday);
		listview=(ListView)view.findViewById(R.id.listView1);
		auditListAdapter = new AuditListAdapter(getActivity(),R.layout.list_item_stock_audit, audits,
				1,new AuditImageClickListener()
				{
					/*@Override
					public void onImageClick(int position, ArrayList<MyImage> images)
					{
						navigateToLargeImage(position, images);
					}
*/
					@Override
					public void onImageClick(int itemPosition,ArrayList<MyImage> images, int listPosition)
					{
						ArrayList<MyImage> tImageList;
						tImageList= new ArrayList<MyImage>();
						MyImage image1 = new MyImage();
						image1.setThumb(audits.get(listPosition).getLicenseImagePath());
						image1.setFull(audits.get(listPosition).getLicenseImagePath());
						tImageList.add(image1);
						
						MyImage image2 = new MyImage();
						image2.setThumb(audits.get(listPosition).getVehicleImagePath());
						image2.setFull(audits.get(listPosition).getVehicleImagePath());
						tImageList.add(image2);
						navigateToLargeImage(itemPosition, tImageList);
					}
				});
		listview.setAdapter(auditListAdapter);
		listview.setOnScrollListener(new OnScrollListener() {
			   
			   @Override
			   public void onScrollStateChanged(AbsListView view, int scrollState) {}
			   @Override
			   public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			    int lastSeenItem= firstVisibleItem+visibleItemCount;
			    if(!audits.isEmpty()){
			     if(lastSeenItem== totalItemCount && !isLoadMore){
			      if(selectedPageNumber<totalPagesScan){
			       selectedPageNumber++;
			       getAuditedToday(selectedPageNumber);
			      }
			     }
			    }
			   }
			  });
		tvNote=(TextView) view.findViewById(R.id.tvNote);
		ivArrowIcon = (ImageView) view.findViewById(R.id.ivArrowIcon);
		rlEmailOption=(RelativeLayout)view.findViewById(R.id.rlEmailOption);
		llTopHeader = (LinearLayout) view.findViewById(R.id.llTopHeader);
		tvEmailList.setOnClickListener(this);
		ivArrowIcon.setOnClickListener(this);
	//	int type= getArguments().getInt("type");
		
		showActionBar(getString(R.string.audited_today));
		showAuditTodayView();
		
		
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
			case R.id.bnSubmitToday:				
				if (!Helper.validMail(etEmailToday.getText().toString().trim()))
				{
					Helper.showToast("Please enter valid email address", getActivity());
					return;
				}
				sendEnailWithSumbit();
				break;
		}
	}
	
	protected void navigateToLargeImage(int position, ArrayList<MyImage> images)
	{
		GallaryFragment imageDetailFragment = new GallaryFragment();
		Bundle args = new Bundle();
		args.putParcelableArrayList("imagelist",images);
		args.putInt("index", position);
		args.putString("vehicleName","Stock Audit");
		args.putString("from", "image");
		imageDetailFragment.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.Container, imageDetailFragment).addToBackStack("").commit();
		/*Intent intent = new Intent(getActivity(),GalleryActivity.class);
		intent.putParcelableArrayListExtra("imagelist",images);
		intent.putExtra("index", position);
		intent.putExtra("vehicleName","Stock Audit");
		intent.putExtra("from", "image");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);*/
	}
	
	/*@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    if(isVisibleToUser) {
	        Activity a = getActivity();
	        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	}*/
	
	private void showAuditTodayView()
	{
		getAuditedToday(selectedPageNumber);
		llTopHeader.setVisibility(View.VISIBLE);
		tvNote.setVisibility(View.GONE);
		listview.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar(getString(R.string.audited_today));
		//getActivity().getActionBar().setSubtitle(null);
	}
	
	private void getAuditedToday(int PageNumber)
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
				showProgressDialog();
				//Add parameters to request in arraylist
				ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
				parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
				parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
				parameterList.add(new Parameter("Page",PageNumber , Integer.class));
				parameterList.add(new Parameter("RecordCount", 10, Integer.class));
				
				//create web service inputs
				DataInObject inObj = new DataInObject();
				inObj.setMethodname("ListDoneAudits");
				inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
				inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListDoneAudits");
				inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
				inObj.setParameterList(parameterList);
				
		//Network call
         new WebServiceTask(getActivity(), inObj, true,new TaskListener() {
 			@Override
 			public void onTaskComplete(Object result) 
 			{
	 			try{
	 	 			Helper.Log("soap response", result.toString());
	 	 			hideProgressDialog();
	 				SoapObject obj= (SoapObject) result;
	 				SoapObject response= (SoapObject) obj.getPropertySafely("Response", "");
	 				SoapObject inner= (SoapObject) response.getPropertySafely("Audits", "");
	 				Audit audit;
	 				for(int i=0;i<inner.getPropertyCount();i++){
	 					audit = new Audit();
	 					if(inner.getProperty(i) instanceof SoapObject)
	 					{
	 						SoapObject auditsObj= (SoapObject) inner.getProperty(i);
	 						if (auditsObj.getPropertySafelyAsString("Completed").equals("true"))
							{
	 						audit.setCompleted(1);
							}else {
								audit.setCompleted(0);
							}
	 						if (auditsObj.getPropertySafelyAsString("Matched").equals("true"))
							{
	 						audit.setMatched(1);
							}else {
								audit.setMatched(0);
							}
	 						audit.setTime(auditsObj.getPropertySafelyAsString("Time",""));
	 						audit.setVIN(auditsObj.getPropertySafelyAsString("VIN",""));
	 						audit.setGEO(auditsObj.getPropertySafelyAsString("GEO",""));
	 						audit.setModel(auditsObj.getPropertySafelyAsString("Model",""));
	 						audit.setMake(auditsObj.getPropertySafelyAsString("Make",""));
	 						audit.setColor(auditsObj.getPropertySafelyAsString("Colour",""));
	 						audit.setStockNumber(auditsObj.getPropertySafelyAsString("StockNumber",""));
	 						audit.setLicenseImagePath(auditsObj.getPropertySafelyAsString("LicenseImage",""));
	 						audit.setVehicleImagePath(auditsObj.getPropertySafelyAsString("VehicleImage",""));
	 						audit.setRegNumber(auditsObj.getPropertySafelyAsString("Registration",""));
	 						audit.setAge(auditsObj.getPropertySafelyAsString("Age",""));
	 						audit.setRetailPrice(Float.parseFloat(auditsObj.getPropertySafelyAsString("RetailPrice", "")));
	 						audit.setTradePrice(Float.parseFloat(auditsObj.getPropertySafelyAsString("TradePrice", "")));
	 						audit.setVehicleType(auditsObj.getPropertySafelyAsString("VehicleType",""));
	 						audit.setMileageKM(auditsObj.getPropertySafelyAsString("Mileage",""));
	 	 					audits.add(audit);
	 					}
	 				}
	 				SoapObject innerCount= (SoapObject) response.getPropertySafely("Counts", "");
	 				countForAuditedToday = Integer.parseInt(innerCount.getPropertySafelyAsString("Audits"));
	 				totalPagesScan =(int) countForAuditedToday/10;
	 				if (audits.size()!=0)
					{
							auditListAdapter.notifyDataSetChanged();
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
	
	private void sendEnailWithSumbit() 
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			StringBuilder soapMessage=new StringBuilder();
		//	soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<SendAuditHistoryItems xmlns=\""+Constants.TEMP_URI_NAMESPACE+"\">");
			soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash()+ "</userHash>");
			soapMessage.append("<clientID>" + DataManager.getInstance().user.getDefaultClient().getId()+ "</clientID>");
			Calendar calendar= Calendar.getInstance();
			soapMessage.append("<Day>" + 
			Helper.createStringDateNew(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH), 
					calendar.get(Calendar.DAY_OF_MONTH))+"</Day>");
			soapMessage.append("<EmailDestination>" +etEmailToday.getText().toString().trim()+ "</EmailDestination>");
			soapMessage.append("<Audited>" +"1"+ "</Audited>");
			soapMessage.append("<NotAudited>" +"0"+ "</NotAudited>");
			soapMessage.append("<NotMatched>" +"0"+ "</NotMatched>");
			soapMessage.append("</SendAuditHistoryItems>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");
			
			
			VollyResponseListener listener=new VollyResponseListener()
			{
				@Override
				public void onErrorResponse(VolleyError error)
				{
					hideProgressDialog();
					Helper.showToast(getString(R.string.error_getting_data),getActivity());
					VolleyLog.e("Error: ", error.toString());
				}
				
				@Override
				public void onResponse(String response)
				{
					hideProgressDialog();
					if(response==null)
					{
						return;
					}
					String message;
					Helper.Log("SendAuditHistoryItems", "" + response);
					if (response.contains("<Success"))
					{
						message = "Stock audit sent";
						
					}else {
						message = "Please try again later";
					}
					CustomDialogManager.showOkDialog(getActivity(),message , new DialogListener()
					{
						
						@Override
						public void onButtonClicked(int type)
						{
							getFragmentManager().popBackStack();
						}
					});
					
				}
			};
			
			VollyCustomRequest request=new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL,soapMessage.toString(),
							Constants.TEMP_URI_NAMESPACE+"IStockService/SendAuditHistoryItems",listener);
			try
			{
				request.init();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				hideKeyboard(tvEmailList);
				getActivity().getFragmentManager().popBackStack();;
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
