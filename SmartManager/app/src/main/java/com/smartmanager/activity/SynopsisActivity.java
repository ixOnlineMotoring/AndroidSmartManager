package com.smartmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentTransaction;

import com.nw.fragments.SavedAppraisalsFragment;
import com.nw.fragments.ScanVINSynosis;
import com.nw.fragments.VehicleCodeFragment;
import com.nw.fragments.VehicleLookUpFragment;
import com.nw.fragments.VehicleStockFragment;
import com.smartmanager.android.R;

public class SynopsisActivity extends BaseActivity
{
	ScanVINSynosis scanVINSynosis;
	VehicleLookUpFragment vehicleLookUpFragment;
	VehicleCodeFragment vehicleCodeFragment;
	VehicleStockFragment vehicleStockFragment;
	SavedAppraisalsFragment savedAppraisalsFragment;

	@Override
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		setContentView(R.layout.activity_blog);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if (getIntent().getExtras().containsKey("Scan VIN"))
		{
			scanVINSynosis = new ScanVINSynosis();
		getSupportFragmentManager().beginTransaction().replace(R.id.Container, scanVINSynosis).commit();
		}
		else if (getIntent().getExtras().containsKey("Vehicle Lookup"))
		{
			vehicleLookUpFragment = new VehicleLookUpFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.Container, vehicleLookUpFragment).commit();
		}
		else if (getIntent().getExtras().containsKey("Vehicle Code"))
		{
			vehicleCodeFragment = new VehicleCodeFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.Container, vehicleCodeFragment).commit();
		}
		else if (getIntent().getExtras().containsKey("Vehicle in Stock"))
		{
			vehicleStockFragment = new VehicleStockFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.Container, vehicleStockFragment).commit();
		}
		else if (getIntent().getExtras().containsKey("Saved Appraisals"))
		{
			savedAppraisalsFragment = new SavedAppraisalsFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.Container, savedAppraisalsFragment).commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
		if (vehicleLookUpFragment != null)
			vehicleLookUpFragment.onActivityResult(requestCode, resultCode, data);
		if (vehicleCodeFragment != null)
			vehicleCodeFragment.onActivityResult(requestCode, resultCode, data);
		if (vehicleStockFragment != null)
			vehicleStockFragment.onActivityResult(requestCode, resultCode, data);
		if (scanVINSynosis != null)
			scanVINSynosis.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		scanVINSynosis = null;
		vehicleLookUpFragment = null;
		vehicleCodeFragment = null;
		vehicleStockFragment = null;
		savedAppraisalsFragment = null;
		System.gc();
	}

}
