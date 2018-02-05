
package com.smartmanager.activity;

import android.os.Bundle;


import com.nw.fragments.LeadListNew;
import com.nw.fragments.ScanLicenseFragment;
import com.smartmanager.android.R;

public class CustomerActivity extends BaseActivity
{

    public static boolean UnseenleadUpdated = false;
    public static boolean WipleadUpdated = false;
    ScanLicenseFragment scanLicenseFragment;
    LeadListNew leadsListFragment;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.activity_blog);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getExtras().containsKey("ScanLicense"))
        {
            scanLicenseFragment = new ScanLicenseFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, scanLicenseFragment).commit();
        } else if (getIntent().getExtras().containsKey("Leads"))
        {
            leadsListFragment = new LeadListNew();
            Bundle bundle = new Bundle();
            bundle.putString("fromFragment", "Leads");
            leadsListFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container,
                    leadsListFragment).commit();

        } else if (getIntent().getExtras().containsKey("My Leads"))
        {
            leadsListFragment = new LeadListNew();
            Bundle bundle = new Bundle();
            bundle.getString("fromFragment", "My Leads");
            leadsListFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container,
                    leadsListFragment).commit();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        scanLicenseFragment = null;
        leadsListFragment = null;
        System.gc();
    }

    @Override
    public void onBackPressed()
    {
        if (scanLicenseFragment != null)
        {
            if (scanLicenseFragment.isVisible())
            {
                super.onBackPressed();
            } else
            {
                scanLicenseFragment.onBackPressed();
            }
        } else
        {
            super.onBackPressed();
        }
    }
}
