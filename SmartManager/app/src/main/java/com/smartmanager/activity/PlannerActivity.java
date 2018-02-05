package com.smartmanager.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.app.FragmentTransaction;

import com.nw.fragments.LogFragment;
import com.nw.fragments.NewTaskFragment;
import com.nw.fragments.NewTodoFragment;
import com.nw.fragments.TaskByMeFragment;
import com.smartmanager.android.R;

public class PlannerActivity extends LoacationBaseActivity 
{
	NewTodoFragment todoFragment;
	NewTaskFragment newTaskFragment;
	/**
	 * No of web service request.
	 */
	public int RequestCount=0;
	@Override
	protected void onCreate(Bundle arg0) 
	{
		super.onCreate(arg0);
		setContentView(R.layout.activity_blog);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if(getIntent().hasExtra("fromLogFragment"))
		{
			getSupportFragmentManager().beginTransaction().replace(R.id.Container, new LogFragment()).commit();
		}
		else if(getIntent().hasExtra("NewTask"))
		{
			newTaskFragment=  new NewTaskFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.Container,newTaskFragment).commit();
		}
		else if(getIntent().hasExtra("todo"))
		{
			todoFragment=new NewTodoFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.Container, todoFragment).commit();
		}
		else if(getIntent().hasExtra("tasksByMe"))
		{
			getSupportFragmentManager().beginTransaction().replace(R.id.Container, new TaskByMeFragment()).commit();
		}
	}
	
	boolean locationCalled=false;
	@Override
	public void onLocationChanged(Location location) 
	{
		super.onLocationChanged(location);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(newTaskFragment!=null)
			newTaskFragment.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		todoFragment=null;
		newTaskFragment=null;
		System.gc();
		
	}
}
