package com.nw.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.nw.model.ScanVIN;
import com.nw.webservice.DataManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;

import net.photopay.barcode.BarcodeDetailedData;

import java.util.ArrayList;

import mobi.pdf417.Pdf417MobiScanData;
import mobi.pdf417.Pdf417MobiSettings;
import mobi.pdf417.activity.Pdf417ScanActivity;

public class StockAuditFragment extends BaseFragement implements OnClickListener
{
	Button btnScanVIN, btnAuditedToday, btnStillAudit, btnAuditHistory;
	private static final int MY_REQUEST_CODE = 1337;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_stock_audit, container, false);
		setHasOptionsMenu(true);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initView(view);
		return view;
	}

	private void initView(View view)
	{
		btnScanVIN = (Button) view.findViewById(R.id.btnScanVIN);
		btnAuditedToday = (Button) view.findViewById(R.id.btnAuditedToday);
		btnStillAudit = (Button) view.findViewById(R.id.btnStillAudit);
		btnAuditHistory = (Button) view.findViewById(R.id.btnAuditHistory);
		btnScanVIN.setOnClickListener(this);
		btnAuditedToday.setOnClickListener(this);
		btnStillAudit.setOnClickListener(this);
		btnAuditHistory.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnScanVIN:
				scanVIN();
				break;
			case R.id.btnAuditedToday:
				showFragment(1);
				break;
			case R.id.btnStillAudit:
				showStillToAuditFragment();
				break;
			case R.id.btnAuditHistory:
				showAuditHistoryFragment();
				break;
		}
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
		sett.setDecodeUSDriverLicenseData(false);
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

	private void showFragment(int type)
	{
		AuditedTodayFragment fragment = new AuditedTodayFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("type", type);
		DataManager.getInstance().AuditType=type;
		fragment.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(R.id.Container, fragment).addToBackStack(null).commit();
	}
	
	private void showStillToAuditFragment()
	{
		StillToAuditFragment fragment = new StillToAuditFragment();
		getFragmentManager().beginTransaction().replace(R.id.Container, fragment).addToBackStack(null).commit();
	}
	
	private void showAuditHistoryFragment()
	{
		AuditHistoryFragment fragment = new AuditHistoryFragment();
		getFragmentManager().beginTransaction().replace(R.id.Container, fragment).addToBackStack(null).commit();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar(getString(R.string.stock_audit));
		//getActivity().getActionBar().setSubtitle(null);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
			case android.R.id.home:
				getActivity().finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
