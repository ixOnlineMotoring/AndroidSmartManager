package com.smartmanager.activity;

import android.os.Bundle;
import android.app.FragmentTransaction;

import com.nw.fragments.StockAuditFragment;
import com.smartmanager.android.R;

public class StockActivity extends BaseActivity
{
	StockAuditFragment fragment;
	@Override
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		setContentView(R.layout.activity_blog);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if(getIntent().getExtras().containsKey("StockAudit"))
		{
			 fragment=new StockAuditFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.Container, fragment).commit();
		}
		/*else if(getIntent().getExtras().containsKey("Leads"))
		{
			StockAuditFragment fragment=new StockAuditFragment();
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.Container, fragment);
			fragmentTransaction.commit();
		}*/
	}
	
}
