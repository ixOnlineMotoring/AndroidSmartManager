package com.smartmanager.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


import com.nw.fragments.GallaryFragment;
import com.nw.model.MyImage;
import com.smartmanager.android.R;

import java.util.ArrayList;

public class GalleryActivity extends FragmentActivity
{
	GallaryFragment imageDetailFragment;
	Bundle bundle;
	ArrayList<MyImage> imagelist;
	String vehicleName,from;
	int pos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		if (getIntent().getParcelableArrayListExtra("imagelist")!=null)
			imagelist = getIntent().getParcelableArrayListExtra("imagelist");
		if (getIntent().getExtras().getString("vehicleName")!=null)
			vehicleName = getIntent().getExtras().getString("vehicleName");
		if (getIntent().getExtras().getString("from")!=null)
			from = getIntent().getExtras().getString("from");
		pos= getIntent().getExtras().getInt("index");
		if (imageDetailFragment==null)
		{
			imageDetailFragment = new GallaryFragment();
		}
		
		bundle.putParcelableArrayList("imagelist",imagelist);
		bundle.putInt("index", pos);
		bundle.putString("vehicleName",vehicleName);
		bundle.putString("from", from);
		imageDetailFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().add(R.id.gallaryContainer,imageDetailFragment).commit();
	}

}
