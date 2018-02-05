package com.smartmanager.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;


import com.nw.fragments.ListDetailsFragment;
import com.nw.fragments.ListVehicleFragment;
import com.nw.fragments.LoadVehicleFragment;
import com.nw.fragments.SendBrochureFragment;
import com.nw.fragments.SpecialFragment;
import com.nw.fragments.StockAuditFragment;
import com.nw.fragments.StockSummaryFragment;
import com.nw.fragments.SupportRequestFragment;
import com.nw.fragments.VINOptionFragment;
import com.nw.model.Vehicle;
import com.smartmanager.android.R;
import com.utils.AppSession;

public class VehicleActivity extends LoacationBaseActivity {
    LoadVehicleFragment loadVehicleFragment;
    ListVehicleFragment listVehicleFragment;
    SpecialFragment specialFragment;
    VINOptionFragment vinOptionFragment;
    ListDetailsFragment listDetailsFragment;
    StockAuditFragment stockAuditFragment;
    SendBrochureFragment sendBrochureFragment;
    StockSummaryFragment stockSummaryFragment;
    public static boolean isVehicleUpdated = false;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_blog);
        if (getIntent().hasExtra("PhotosAndExtras"))
        {
            listVehicleFragment = new ListVehicleFragment();
            Bundle args = new Bundle();
            args.putString("fromFragment", "PhotosAndExtras");
            listVehicleFragment.setArguments(args);
          getSupportFragmentManager().beginTransaction().replace(R.id.Container, listVehicleFragment).commit();
        } else if (getIntent().hasExtra("LoadVehicle"))
        {
            loadVehicleFragment = new LoadVehicleFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, loadVehicleFragment).commit();
        } else if (getIntent().hasExtra("Specials"))
        {
            specialFragment = new SpecialFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, specialFragment).commit();
        } else if (getIntent().hasExtra("Edit Stock"))
        {
            listVehicleFragment = new ListVehicleFragment();
            Bundle args = new Bundle();
            args.putString("fromFragment", "Edit Stock");
            listVehicleFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, listVehicleFragment).commit();

        } else if (getIntent().hasExtra("VINLookup"))
        {

            vinOptionFragment = new VINOptionFragment();
            Bundle args = new Bundle();
            args.putString("fromFragment", "home");
            vinOptionFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, vinOptionFragment).commit();

        } else if (getIntent().hasExtra("eBrochure"))
        {
            AppSession.saveImageDetails(VehicleActivity.this,"true");
            sendBrochureFragment = new SendBrochureFragment();
            Bundle args = new Bundle();
            args.putString("fromFragment", "E-Brochure");
            sendBrochureFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, sendBrochureFragment).commit();
        } else if (getIntent().hasExtra("ActiveBidsFragment"))
        {
            listDetailsFragment = new ListDetailsFragment();
            Bundle args = new Bundle();
            args.putInt("stockID", getIntent().getExtras().getInt("stockID"));
            args.putBoolean("fromActiveBidsFragment", true);
            listDetailsFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, listDetailsFragment).commit();
        } else if (getIntent().hasExtra("StockAudit"))
        {
            stockAuditFragment = new StockAuditFragment();
            Bundle args = new Bundle();
            args.putString("fromFragment", "stockaudit");
            stockAuditFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, stockAuditFragment).commit();

        } else if (getIntent().hasExtra("Stock List"))
        {
            sendBrochureFragment = new SendBrochureFragment();
            Bundle args = new Bundle();
            args.putString("fromFragment", "Stock List");
            sendBrochureFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, sendBrochureFragment).commit();
        } else if (getIntent().hasExtra("Stock Summary"))
        {
            stockSummaryFragment = new StockSummaryFragment();
            Bundle args = new Bundle();
            args.putString("fromFragment", "Stock Summary");
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, stockSummaryFragment).commit();
        }
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }

    boolean locationCalled = false;

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra("PhotosAndExtras"))
        {
            if (listVehicleFragment != null)
            {
                if (listVehicleFragment.isVisible())
                {
                    super.onBackPressed();
                } else
                {
                    listVehicleFragment.onBackPressed();
                }
            }
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (loadVehicleFragment != null)
            loadVehicleFragment.onActivityResult(requestCode, resultCode, data);
        if (listVehicleFragment != null)
            listVehicleFragment.onActivityResult(requestCode, resultCode, data);
        if (specialFragment != null)
            specialFragment.onActivityResult(requestCode, resultCode, data);
        if (vinOptionFragment != null)
            vinOptionFragment.onActivityResult(requestCode, resultCode, data);
        if (listDetailsFragment != null)
            listDetailsFragment.onActivityResult(requestCode, resultCode, data);
        if (stockAuditFragment != null)
            stockAuditFragment.onActivityResult(requestCode, resultCode, data);
        if (sendBrochureFragment != null)
            sendBrochureFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadVehicleFragment = null;
        listVehicleFragment = null;
        specialFragment = null;
        vinOptionFragment = null;
        listDetailsFragment = null;
        stockAuditFragment = null;
        sendBrochureFragment = null;
        System.gc();
    }
}
