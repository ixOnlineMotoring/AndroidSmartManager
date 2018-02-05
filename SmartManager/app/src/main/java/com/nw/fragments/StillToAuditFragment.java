package com.nw.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.nw.adapters.AuditListAdapter;
import com.nw.adapters.StillAuditAdapter;
import com.nw.interfaces.AuditImageClickListener;
import com.nw.interfaces.DialogListener;
import com.nw.model.Audit;
import com.nw.model.DataInObject;
import com.nw.model.MyImage;
import com.nw.model.Parameter;
import com.nw.model.ScanVIN;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomEditText;
import com.nw.widget.CustomTextViewLight;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import net.photopay.barcode.BarcodeDetailedData;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;

import mobi.pdf417.Pdf417MobiScanData;
import mobi.pdf417.Pdf417MobiSettings;
import mobi.pdf417.activity.Pdf417ScanActivity;

public class StillToAuditFragment extends BaseFragement implements OnClickListener
{
	TextView tvNote,tvHeaderCount1,tvHeaderCount2;
	CustomTextViewLight tvEmailList;
	CustomButton btnStillSubmit;
	CustomEditText etStilEmailId;
	ListView listview1,listview2;
	LinearLayout llListView1;
	//String[] Groups=new String[]{"Audited today but no matching VIN","These vehicles requires a VIN scan"};
	LinearLayout rlEmailOption;
	ImageView ivArrowIcon,ivIconGroup1,ivIconGroup2;
	LinearLayout llTopHeader;
	RelativeLayout textHeader1,textHeader2;
	boolean isGroup1=false,isGroup2=false,isLoadMore1=false,isLoadMore2=false;
	ArrayList<Audit> audits;
	ArrayList<Audit> auditsNotDone;
	AuditListAdapter auditListAdapter;
	StillAuditAdapter stillAuditAdapter;
	int AUDITED_NO_VIN=1,REQUIRES_SCAN=2,selectedPageNumber=0,totalPagesNoVIN=-1,totalPagesScan=-1,selectedPageNumber1=0
			,countForNotAudited=0,countForUnmatchedAudits=0,selectedGroupNumber=0;
	private static final int MY_REQUEST_CODE = 1337;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_still_to_audit, container, false);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		showActionBar(getString(R.string.still_to_audit));
		audits = new ArrayList<>();
		auditsNotDone = new ArrayList<>();
		initView(view);
		setHasOptionsMenu(true);
		return view;
	}

	private void initView(View view)
	{
		llListView1=(LinearLayout) view.findViewById(R.id.llListView1);
		tvEmailList=(CustomTextViewLight) view.findViewById(R.id.tvEmailList);
		tvHeaderCount1=(TextView) view.findViewById(R.id.tvHeaderCount1);
		tvHeaderCount2=(TextView) view.findViewById(R.id.tvHeaderCount2);
		etStilEmailId = (CustomEditText) view.findViewById(R.id.etStilEmailId);
		btnStillSubmit=(CustomButton) view.findViewById(R.id.btStillSubmit);
		btnStillSubmit.setOnClickListener(this);
		listview1=(ListView)view.findViewById(R.id.listView1);
		listview2=(ListView)view.findViewById(R.id.listView2);
		llListView1.setVisibility(View.GONE);
		listview2.setVisibility(View.GONE);
		auditListAdapter = new AuditListAdapter(getActivity(),R.layout.list_item_stock_audit, audits,
				2,new AuditImageClickListener()
				{
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
		listview1.setAdapter(auditListAdapter);
		if (audits.size()==0)
		{
			getStillToAudit(AUDITED_NO_VIN,selectedPageNumber);
		}
		listview1.setOnScrollListener(new OnScrollListener() {
			   
			   @Override
			   public void onScrollStateChanged(AbsListView view, int scrollState) {}
			   @Override
			   public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			    int lastSeenItem= firstVisibleItem+visibleItemCount;
			    if(!audits.isEmpty()){
			     if(lastSeenItem== totalItemCount && !isLoadMore1){
			      if(selectedPageNumber<=totalPagesScan){
			       selectedPageNumber++;
			       getStillToAudit(AUDITED_NO_VIN,selectedPageNumber);
			      }
			     }
			    }
			   }
			  });
		stillAuditAdapter = new StillAuditAdapter(getActivity(), auditsNotDone);
		listview2.setAdapter(stillAuditAdapter);
		listview2.setOnScrollListener(new OnScrollListener()
		{
			   @Override
			   public void onScrollStateChanged(AbsListView view, int scrollState) {}
			   @Override
			   public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			   {
			    int lastSeenItem= firstVisibleItem+visibleItemCount;
			    if(!auditsNotDone.isEmpty())
			    {
			     if(lastSeenItem== totalItemCount && !isLoadMore2)
			     {
			      if(selectedPageNumber1<=totalPagesNoVIN)
			      {
			       selectedPageNumber1++;
			       getStillToAudit(REQUIRES_SCAN,selectedPageNumber1);
			      }
			     }
			    }
			   }
			  });
		listview2.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
			{
				scanVIN();
			}
		});
		textHeader1 =(RelativeLayout) view.findViewById(R.id.rltextHeader1);
		textHeader2 =(RelativeLayout) view.findViewById(R.id.rltextHeader2);
		textHeader1.setOnClickListener(this);
		textHeader2.setOnClickListener(this);
		tvNote=(TextView) view.findViewById(R.id.tvNote);
		ivArrowIcon = (ImageView) view.findViewById(R.id.ivArrowIcon);
		ivIconGroup1 = (ImageView) view.findViewById(R.id.ivIconGroup1);
		ivIconGroup2 = (ImageView) view.findViewById(R.id.ivIconGroup2);
		rlEmailOption=(LinearLayout)view.findViewById(R.id.rlEmailOption);
		llTopHeader = (LinearLayout) view.findViewById(R.id.llTopHeader);
		tvEmailList.setOnClickListener(this);
		ivArrowIcon.setOnClickListener(this);
	}
	
	public void scanVIN()
	{
		Helper.Log(getTag(), "scan will be performed");
		// Intent for ScanActivity
		Intent intent = new Intent(getActivity(), Pdf417ScanActivity.class);
		// If you want sound to be played after the scanning process ends,
		// put here the resource ID of your sound file. (optional)
		intent.putExtra(Pdf417ScanActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);
		// set EXTRAS_ALWAYS_USE_HIGH_RES to true if you want to always use
		// highest
		// possible camera resolution (enabled by default for all devices that
		// support
		// at least 720p camera preview frame size)
		// intent.putExtra(Pdf417ScanActivity.EXTRAS_ALWAYS_USE_HIGH_RES, true);
		// set the license key (for commercial versions only) - obtain your key
		// at
		// http://pdf417.mobi
		intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, Constants.SCAN_LICENSE_KEY); 
		intent.putExtra(Pdf417ScanActivity.EXTRAS_ALWAYS_USE_HIGH_RES, true);
		// intent.putExtra(Pdf417ScanActivity.EXTRAS_OPTIMIZE_CAMERA_FOR_NEAR_SCANNING,
		// true);
		// If you want to open front facing camera, uncomment the following
		// line.
		// Note that front facing cameras do not have autofocus support, so it
		// will not
		// be possible to scan denser and smaller codes.
		// intent.putExtra(Pdf417ScanActivity.EXTRAS_CAMERA_TYPE,
		// (Parcelable)CameraType.CAMERA_FRONTFACE);
		// You can use Pdf417MobiSettings object to tweak additional scanning
		// parameters.
		// This is entirely optional. If you don't send this object via intent,
		// default
		// scanning parameters will be used - this means both QR and PDF417
		// codes will
		// be scanned and default camera overlay will be shown.
		Pdf417MobiSettings sett = new Pdf417MobiSettings();
		// set this to true to enable PDF417 scanning
		sett.setPdf417Enabled(true);
		// Set this to true to scan even barcode not compliant with standards
		// For example, malformed PDF417 barcodes which were incorrectly encoded
		// Use only if necessary because it slows down the recognition process
		// sett.setUncertainScanning(true);
		// Set this to true to scan barcodes which don't have quiet zone (white
		// area) around it
		// Use only if necessary because it drastically slows down the
		// recognition process
		sett.setNullQuietZoneAllowed(true);
		// Set this to true to enable parsing of data from US Driver's License
		// barcodes
		// This feature is available only if license key permits it.
		sett.setDecodeUSDriverLicenseData(true);
		// set this to true to enable QR code scanning
		sett.setQrCodeEnabled(true);
		// set this to true to prevent showing dialog after successful scan
		sett.setDontShowDialog(true);
		// if license permits this, remove Pdf417.mobi logo overlay on scan
		// activity
		// if license forbids this, this option has no effect
		sett.setRemoveOverlayEnabled(true);
		// set this to false if you want to receive at most one scan result
		// sett.setAllowMultipleScanResultsOnSingleImage(false);
		// put settings as intent extra
		intent.putExtra(Pdf417ScanActivity.EXTRAS_SETTINGS, sett);
		// Start Activity
		startActivityForResult(intent, MY_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		
		if (requestCode == MY_REQUEST_CODE && resultCode == Pdf417ScanActivity.RESULT_OK)
		{
			// obtain scan results
			ArrayList<Pdf417MobiScanData> scanDataList = data.getParcelableArrayListExtra(Pdf417ScanActivity.EXTRAS_RESULT_LIST);
			// NOTE: if you are interested in only single scan result, you can
			// obtain the first element of the array list
			// or you can use the old key EXTRAS_RESULT
			// If you have set allowing of multiple scan results on single image
			// to false
			// (Pdf417MobiSettings.setAllowMultipleScanResultsOnSingleImage
			// method)
			// scanDataList will contain at most one element.
			String barcodeData = null;
			@SuppressWarnings("unused")
			String barcodeType = null;
			@SuppressWarnings("unused")
			BarcodeDetailedData rawData = null;
			for (Pdf417MobiScanData scanData : scanDataList)
			{
				// read scanned barcode type (PDF417 or QR code)
				barcodeType = scanData.getBarcodeType();
				// read the data contained in barcode
				barcodeData = scanData.getBarcodeData();
				// read raw barcode data
				rawData = scanData.getBarcodeRawData();
				// determine if returned scan data is certain
			}
			String[] dataIn = barcodeData.split("%");
			Helper.Log("orignal:", barcodeData);
			Helper.Log("data Length", "Size:" + dataIn.length);
			for (int i = 0; i < dataIn.length; i++)
			{
				Helper.Log("data" + i, dataIn[i]);
			}
			if (dataIn.length >= 14)
			{
				ScanVIN scanVIN = new ScanVIN();
				scanVIN.setId(0);
				scanVIN.setDate(dataIn[14]);
				scanVIN.setLicence(dataIn[5]);
				scanVIN.setVIN(dataIn[12]);
				scanVIN.setRegistration(dataIn[6]);
				scanVIN.setShape(dataIn[8]);
				scanVIN.setMake(dataIn[9]);
				scanVIN.setModel(dataIn[10]);
				scanVIN.setColour(dataIn[11]);
				scanVIN.setEngineNumber(dataIn[13]);
				ScanVINAuditFragment fragment = new ScanVINAuditFragment();
				Bundle bundle = new Bundle();
				bundle.putParcelable("scannedVIN", scanVIN);
				fragment.setArguments(bundle);
				getFragmentManager().beginTransaction().replace(R.id.Container, fragment).addToBackStack(null).commit();
			}
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
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		hideKeyboard(tvEmailList);
		showActionBar(getString(R.string.still_to_audit));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				Helper.hidekeybord(tvEmailList);
				getActivity().getFragmentManager().popBackStack();;
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
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
				
			case R.id.rltextHeader1:
				if(rlEmailOption.getVisibility()!=View.GONE){
					rlEmailOption.setVisibility(View.GONE);
					ivArrowIcon.setRotation(-90);}
				if(isGroup1)
				{
					listview2.setVisibility(View.VISIBLE);
					llListView1.setVisibility(View.GONE);
				}else{
					llListView1.setVisibility(View.VISIBLE);
					listview2.setVisibility(View.GONE);
				}
				rotateImage();
				break;
				
			case R.id.rltextHeader2:	
				
				if(rlEmailOption.getVisibility()!=View.GONE){
					rlEmailOption.setVisibility(View.GONE);
					ivArrowIcon.setRotation(-90);}
				
				if(isGroup2)
				{
					llListView1.setVisibility(View.VISIBLE);
					listview2.setVisibility(View.GONE);
				}
				else{
					listview2.setVisibility(View.VISIBLE);
					llListView1.setVisibility(View.GONE);
					}
				rotateImage();
				if (auditsNotDone.size()==0)
				{
					getStillToAudit(REQUIRES_SCAN,selectedPageNumber);
				}
				break;
			case R.id.btStillSubmit:
				if (!Helper.validMail(etStilEmailId.getText().toString().trim()))
				{
					Helper.showToast("Please enter valid email address", getActivity());
					return;
				}
				sendEnailWithSumbit();
				break;
		}
	}
	
	private void rotateImage()
	{
		if (llListView1.getVisibility()== View.GONE)
		{
			isGroup1= false;
			ivIconGroup1.setRotation(0);
		}else {
			isGroup1= true;
			ivIconGroup1.setRotation(90);
		}
		if (listview2.getVisibility()== View.GONE)
		{
			isGroup2= false;
			ivIconGroup2.setRotation(0);
		}else {
			isGroup2= true;
			ivIconGroup2.setRotation(90);
		}
	}
	
	private void sendEnailWithSumbit() 
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			StringBuilder soapMessage=new StringBuilder();
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<SendAuditHistoryItems xmlns=\""+Constants.TEMP_URI_NAMESPACE+"\">");
			soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash()+ "</userHash>");
			soapMessage.append("<clientID>" + DataManager.getInstance().user.getDefaultClient().getId()+ "</clientID>");
			Calendar calendar= Calendar.getInstance();
			soapMessage.append("<Day>" + 
			Helper.createStringDateNew(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH))+"</Day>");
			soapMessage.append("<EmailDestination>" +etStilEmailId.getText().toString().trim()+ "</EmailDestination>");
			if (selectedGroupNumber==1)
			{
				soapMessage.append("<Audited>" +"0"+ "</Audited>");
				soapMessage.append("<NotAudited>" +"0"+ "</NotAudited>");
				soapMessage.append("<NotMatched>" +"1"+ "</NotMatched>");
			} else
			{
				soapMessage.append("<Audited>" +"0"+ "</Audited>");
				soapMessage.append("<NotAudited>" +"1"+ "</NotAudited>");
				soapMessage.append("<NotMatched>" +"0"+ "</NotMatched>");
			}
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

	/*	Pass value 1 for No matching VIN part in expandablelist
	 * 
	 * 	Pass value 2 for Required VIN part in expandablelist
	 * 
	 */
	
	private void getStillToAudit(final int groupNumber,int selectedPageNumber)   
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
				showProgressDialog();
				//Add parameters to request in arraylist
				ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
				parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
				parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
				parameterList.add(new Parameter("Page",selectedPageNumber , Integer.class));
				parameterList.add(new Parameter("RecordCount", 10, Integer.class));
				
				selectedGroupNumber= groupNumber;
				//create web service inputs
				DataInObject inObj = new DataInObject();
				if (groupNumber==AUDITED_NO_VIN)
				{
					inObj.setMethodname("ListUnmatchedAudits");
					inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListUnmatchedAudits");
				}else {
					inObj.setMethodname("ListNotDoneAudits");
					inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListNotDoneAudits");
				}
				inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
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
	 						audit.setRegNumber(auditsObj.getPropertySafelyAsString("Registration",""));
	 						audit.setAge(auditsObj.getPropertySafelyAsString("Age",""));
	 						audit.setRetailPrice(Float.parseFloat(auditsObj.getPropertySafelyAsString("RetailPrice", "")));
	 						audit.setTradePrice(Float.parseFloat(auditsObj.getPropertySafelyAsString("TradePrice", "")));
	 						audit.setVehicleType(auditsObj.getPropertySafelyAsString("VehicleType",""));
	 						audit.setColor(auditsObj.getPropertySafelyAsString("Colour",""));
	 						audit.setMileageKM(auditsObj.getPropertySafelyAsString("Mileage",""));
	 						if (groupNumber==REQUIRES_SCAN)
	 						{
	 		 					audit.setYear(auditsObj.getPropertySafelyAsString("Year",""));
	 						}
	 						audit.setStockNumber(auditsObj.getPropertySafelyAsString("StockNumber",""));
	 						audit.setLicenseImagePath(auditsObj.getPropertySafelyAsString("LicenseImage",""));
	 						audit.setVehicleImagePath(auditsObj.getPropertySafelyAsString("VehicleImage",""));
	 	 					if (groupNumber==1)
							{
	 	 						audits.add(audit);
							}else {
								auditsNotDone.add(audit);
							}
	 					}
	 				}
	 				SoapObject innerCount= (SoapObject) response.getPropertySafely("Counts", "");
	 				countForNotAudited = Integer.parseInt(innerCount.getPropertySafelyAsString("NotAudited"));
	 				countForUnmatchedAudits =Integer.parseInt(innerCount.getPropertySafelyAsString("UnmatchedAudits"));
	 				
	 				if (groupNumber==AUDITED_NO_VIN)
					{
						totalPagesNoVIN = (int) countForNotAudited / 10;
						tvHeaderCount1.setText(""+countForUnmatchedAudits);
						tvHeaderCount2.setText(""+countForNotAudited);
					}else {
						totalPagesScan = (int) countForUnmatchedAudits / 10;
					}
					if (groupNumber==1)
					{
						auditListAdapter.notifyDataSetChanged();
					}else {
						stillAuditAdapter.notifyDataSetChanged();
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
		}
		else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

}
