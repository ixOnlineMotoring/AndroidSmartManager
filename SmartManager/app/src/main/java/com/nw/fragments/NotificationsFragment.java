package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.nw.adapters.NotificationsAdapter;
import com.smartmanager.android.R;

import java.util.ArrayList;
import java.util.HashMap;


public class NotificationsFragment extends BaseFragement
{
	ExpandableListView list;
	private static final String[] headers={
		"Leads Received",
		"Lead Updates Due",
		"Messages"};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view=inflater.inflate(R.layout.fragment_todo, container,false);
		setHasOptionsMenu(true);
		list=(ExpandableListView) view;
		list.setGroupIndicator(null);
		list.setOnChildClickListener(new OnChildClickListener()
		{
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2, int arg3, long arg4)
			{
				/*LeadViewFragment fragment=new LeadViewFragment();
				Bundle bundle= new Bundle();
				bundle.putInt("leadID", 0);
				fragment.setArguments(bundle);
				FragmentManager manager=getActivity().getSupportFragmentManager();
				manager.beginTransaction().replace(R.id.Container, fragment).addToBackStack("").commit();
				return true;*/
				return true;
			}
		});
		
		HashMap<String, ArrayList<String>>data=new HashMap<String, ArrayList<String>>();
		
		for(String obj:headers)
		{
			ArrayList<String>values=new ArrayList<String>();
			for(int i=0;i<5;i++)
				values.add("data "+i);
			data.put(obj, values);
		}
		NotificationsAdapter adapter=new NotificationsAdapter(getActivity(),headers, data);
		list.setAdapter(adapter);
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Notifications");
		//getActivity().getActionBar().setSubtitle(null);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	hideKeyboard();
        	getActivity().finish();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
