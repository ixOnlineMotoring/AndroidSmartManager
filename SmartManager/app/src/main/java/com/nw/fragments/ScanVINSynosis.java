package com.nw.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.greysonparrelli.permiso.Permiso;
import com.nw.model.DataInObject;
import com.nw.model.Model;
import com.nw.model.Parameter;
import com.nw.model.ScanVIN;
import com.nw.model.SmartObject;
import com.nw.model.Variant;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import net.photopay.barcode.BarcodeDetailedData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.ArrayList;

import mobi.pdf417.Pdf417MobiScanData;
import mobi.pdf417.Pdf417MobiSettings;
import mobi.pdf417.activity.Pdf417ScanActivity;

public class ScanVINSynosis extends BaseFragement implements OnClickListener
{
	Button btnScanVIN,btnSavedVIN;
	private static final int MY_REQUEST_CODE = 1337;
	private static final String TAG = "Pdf417MobiDemo";
	VehicleDetails details;
	ScanVINDetailsSynopsis scanVINDetailsSynopsis;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Permiso.getInstance().setActivity(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View view=inflater.inflate(R.layout.fragment_vin_option, container,false);
		setHasOptionsMenu(true);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		btnScanVIN=(Button) view.findViewById(R.id.btnScanVIN);
		btnSavedVIN=(Button) view.findViewById(R.id.btnSavedVIN);
		btnScanVIN.setOnClickListener(this);
		btnSavedVIN.setOnClickListener(this);
		hideKeyboard(view);
		return view;
	}

	@Override
	public void onClick(final View v)
	{
		switch (v.getId()) 
		{
		case R.id.btnScanVIN:
			// scan for VIN
			//scanVIN(v);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
						&&getActivity().checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
					getActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.CAMERA},100);
					return;
				}
			}
			Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
				@Override
				public void onPermissionResult(Permiso.ResultSet resultSet) {
					if (resultSet.isPermissionGranted(Manifest.permission.CAMERA)){
						scanVIN(v);
					}else {
                        Helper.showToast("Please accept permission for scanning VIN",getActivity());
                    }
				}

				@Override
				public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {

				}
			}, Manifest.permission.CAMERA);

			break;
		case R.id.btnSavedVIN:

			SavedVINListFragment savedVINListFragment = new SavedVINListFragment();
			Bundle bundle = new Bundle();
			bundle.putBoolean("fromVehicleModule", false);
			savedVINListFragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.Container, savedVINListFragment).addToBackStack(null).commit();
			
			break;
		}
	}
	
	public void scanVIN(View v)
	{
	        Helper.Log(TAG, "scan will be performed");
	        // Intent for ScanActivity
	        Intent intent = new Intent(getActivity(), Pdf417ScanActivity.class);

	        // If you want sound to be played after the scanning process ends, 
	        // put here the resource ID of your sound file. (optional)
	        intent.putExtra(Pdf417ScanActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);

	        // set EXTRAS_ALWAYS_USE_HIGH_RES to true if you want to always use highest 
	        // possible camera resolution (enabled by default for all devices that support
	        // at least 720p camera preview frame size)
	        //		intent.putExtra(Pdf417ScanActivity.EXTRAS_ALWAYS_USE_HIGH_RES, true);

	        // set the license key (for commercial versions only) - obtain your key at
	        // http://pdf417.mobi
	        intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, Constants.SCAN_LICENSE_KEY); // demo license key for package mobi.pdf417.demo

	        intent.putExtra(Pdf417ScanActivity.EXTRAS_ALWAYS_USE_HIGH_RES, true);
	        
	        //intent.putExtra(Pdf417ScanActivity.EXTRAS_OPTIMIZE_CAMERA_FOR_NEAR_SCANNING, true);
	        
	        // If you want to open front facing camera, uncomment the following line.
	        // Note that front facing cameras do not have autofocus support, so it will not
	        // be possible to scan denser and smaller codes.
	        //		intent.putExtra(Pdf417ScanActivity.EXTRAS_CAMERA_TYPE, (Parcelable)CameraType.CAMERA_FRONTFACE);

	        // You can use Pdf417MobiSettings object to tweak additional scanning parameters.
	        // This is entirely optional. If you don't send this object via intent, default
	        // scanning parameters will be used - this means both QR and PDF417 codes will
	        // be scanned and default camera overlay will be shown.

	        Pdf417MobiSettings sett = new Pdf417MobiSettings();
	        
	        // set this to true to enable PDF417 scanning
	        sett.setPdf417Enabled(true);
	        // Set this to true to scan even barcode not compliant with standards
	        // For example, malformed PDF417 barcodes which were incorrectly encoded
	        // Use only if necessary because it slows down the recognition process
	        //		sett.setUncertainScanning(true);
	        // Set this to true to scan barcodes which don't have quiet zone (white area) around it
	        // Use only if necessary because it drastically slows down the recognition process 
	        sett.setNullQuietZoneAllowed(true);
	        // Set this to true to enable parsing of data from US Driver's License barcodes
	        // This feature is available only if license key permits it.
	        sett.setDecodeUSDriverLicenseData(true);
	        // set this to true to enable QR code scanning
	        sett.setQrCodeEnabled(true);
	        // set this to true to prevent showing dialog after successful scan
	        sett.setDontShowDialog(true);
	        // if license permits this, remove Pdf417.mobi logo overlay on scan activity
	        // if license forbids this, this option has no effect
	        sett.setRemoveOverlayEnabled(true);
	        // set this to false if you want to receive at most one scan result
//	        sett.setAllowMultipleScanResultsOnSingleImage(false);
	        // put settings as intent extra
	        intent.putExtra(Pdf417ScanActivity.EXTRAS_SETTINGS, sett);
	        // Start Activity
	        startActivityForResult(intent, MY_REQUEST_CODE);
	    }
	
	public void onResume() 
	{
		super.onResume();
		Permiso.getInstance().setActivity(getActivity());
		showActionBar("Scan VIN ");
	}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode,permissions,grantResults);
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	 {
	        if (requestCode == MY_REQUEST_CODE && resultCode == Pdf417ScanActivity.RESULT_OK)
	        {
	            // obtain scan results
	            ArrayList<Pdf417MobiScanData> scanDataList = data.getParcelableArrayListExtra(Pdf417ScanActivity.EXTRAS_RESULT_LIST);
	            // NOTE: if you are interested in only single scan result, you can obtain the first element of the array list
	            //       or you can use the old key EXTRAS_RESULT
	            // If you have set allowing of multiple scan results on single image to false (Pdf417MobiSettings.setAllowMultipleScanResultsOnSingleImage method)
	            // scanDataList will contain at most one element.
	            //Pdf417MobiScanData scanData = data.getParcelableExtra(Pdf417ScanActivity.EXTRAS_RESULT);	            
	            String barcodeData=null;
	            @SuppressWarnings("unused")
				String barcodeType=null;
	            @SuppressWarnings("unused")
				BarcodeDetailedData rawData=null;
	            for(Pdf417MobiScanData scanData : scanDataList)
	            {
	                // read scanned barcode type (PDF417 or QR code)
	                barcodeType = scanData.getBarcodeType();
	                // read the data contained in barcode
	                barcodeData = scanData.getBarcodeData();
	                // read raw barcode data
	                rawData = scanData.getBarcodeRawData();
	                // determine if returned scan data is certain
	            }
	            
	            String[]dataIn=barcodeData.split("%");
	            Helper.Log("orignal:", barcodeData);
	            Helper.Log("data Length","Size:"+dataIn.length);
	            for(int i=0;i<dataIn.length;i++)
	            {
	            	Helper.Log("data"+i, dataIn[i]);
	            }
	         
	            if(dataIn.length>=14)
	            {
	            	ScanVIN scanVIN=new ScanVIN();
	 				scanVIN.setId(0);
	 				scanVIN.setDate(dataIn[14]);
	 				scanVIN.setVIN(dataIn[12]);
	 				scanVIN.setRegistration(dataIn[6]);
	 				scanVIN.setShape(dataIn[8]);
	 				scanVIN.setMake(dataIn[9]);
	 				scanVIN.setModel(dataIn[10]);
	 				scanVIN.setColour(dataIn[11]);
	 				scanVIN.setEngineNumber(dataIn[13]);
	 				checkScannedVINJson(scanVIN);
	            }
	        }
	    }
	
	private void checkScannedVINJson(final ScanVIN scanVIN)
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
 		{
			ArrayList<Parameter>parameters=new ArrayList<Parameter>();
			parameters.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameters.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameters.add(new Parameter("vin", scanVIN.getVIN(), String.class));
			parameters.add(new Parameter("registration", scanVIN.getRegistration(), String.class));
			parameters.add(new Parameter("make",scanVIN.getMake(), String.class));
			parameters.add(new Parameter("model", scanVIN.getModel(), String.class));
			
			DataInObject dataInObject=new DataInObject();
			dataInObject.setMethodname("CheckScannedVinJSON");
			dataInObject.setNamespace(Constants.TEMP_URI_NAMESPACE);
			dataInObject.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/CheckScannedVinJSON");
			dataInObject.setUrl(Constants.STOCK_WEBSERVICE_URL);
			dataInObject.setParameterList(parameters);
			new WebServiceTask(getActivity(), dataInObject, true, new TaskListener() 
			{
				@Override
				public void onTaskComplete(Object response) 
				{
					if(response!=null)
					{
						Helper.Log("checkScannedVINJson onTaskComplete ", ""+response.toString());
							if(response instanceof SoapFault)
							{
								// error
								showErrorDialog();
							}
							else
							{
								// not fault
								try 
								{
									String value=(String) ((SoapPrimitive)response).getValue();
									if(!TextUtils.isEmpty(value))
									{
										JSONObject jsonObject=new JSONObject(value);
										if(jsonObject.optString("status", "").equals("ok"))
										{
											if(jsonObject.optString("existing", "no").equals("yes"))
												scanVIN.setExisting(true);
											if(jsonObject.has("stock"))
											{
												JSONArray stockArray=jsonObject.getJSONArray("stock");
												for(int index=0;index<stockArray.length();index++)
												{
													JSONObject stockObject=stockArray.getJSONObject(index);
													SmartObject smartObject=new SmartObject(stockObject.optInt("id"),
													stockObject.optString("code"));
													scanVIN.getStocks().add(smartObject);
												}
											}
											scanVIN.setHasModel(jsonObject.optBoolean("hasModel", false));
											scanVIN.setModelId(jsonObject.optInt("ModelID", 0));
											scanVIN.setMakeId(jsonObject.optInt("makeID", 0));
											scanVIN.setMaxYear(jsonObject.optInt("maxYear", 0));
											scanVIN.setMinYear(jsonObject.optInt("minYear", 0));
											
											if(jsonObject.has("models"))
											{
												JSONArray modelArray=jsonObject.getJSONArray("models");
												for(int index=0;index<modelArray.length();index++)
												{
													JSONObject modelObject=modelArray.getJSONObject(index);
													Model smartObject=new Model(modelObject.optInt("modelID"),modelObject.optString("modelName"),modelObject.optInt("minYear"),modelObject.optInt("maxYear"));
													scanVIN.getModels().add(smartObject);
												}
											}
											if(jsonObject.has("variants"))
											{
												JSONArray variantsArray=jsonObject.optJSONArray("variants");
												for(int index=0;index<variantsArray.length();index++)
												{
													Variant variant=new Variant();
													JSONObject variantObject=variantsArray.getJSONObject(index);
													variant.setVariantId(variantObject.optInt("id"));
													variant.setVariantName(variantObject.optString("name"));
													variant.setFriendlyName(variantObject.optString("friendly"));
													variant.setMeadCode(variantObject.optString("code"));
													variant.setMinYear(variantObject.optString("minYear"));
													variant.setMaxYear(variantObject.optString("maxYear"));
													scanVIN.getVariants().add(variant);
												}
											}
											// got to next screen
											scanVINDetailsSynopsis=new ScanVINDetailsSynopsis();
											Bundle bundle=new Bundle();
											bundle.putParcelable("scannedVIN", scanVIN);
											bundle.putBoolean("fromScan", true);
											scanVINDetailsSynopsis.setArguments(bundle);
											getFragmentManager().beginTransaction().replace(R.id.Container,scanVINDetailsSynopsis).addToBackStack(null).commit();
										}
									}
								}
								catch(Exception e)
								{
									e.printStackTrace();
									showErrorDialog();
								}
							}
					}
				else
					{
						showErrorDialog();
					}
				}
			}).execute();
 		}
		else
			HelperHttp.showNoInternetDialog(getActivity());
	}

}
