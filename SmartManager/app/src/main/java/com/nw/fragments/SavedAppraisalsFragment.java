package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.nw.adapters.SavedAppraisalsAdapter;
import com.nw.interfaces.DateListener;
import com.nw.model.SavedAppraisals;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SavedAppraisalsFragment extends BaseFragement implements OnClickListener
{
	EditText edFromDate, edToDate;
	ArrayList<SavedAppraisals> savedAppraisalsList;
	SavedAppraisalsAdapter savedAppraisalsAdapter;
	ListView listview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_saved_appraisals, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		SavedAppraisals savedAppraisals;
		savedAppraisalsList = new ArrayList<SavedAppraisals>();
		for (int i = 0; i < 5; i++)
		{
			savedAppraisals = new SavedAppraisals();
			savedAppraisalsList.add(savedAppraisals);
		}
		edFromDate = (EditText) view.findViewById(R.id.edStartDate);
		edToDate = (EditText) view.findViewById(R.id.edEndDate);
		edFromDate.setOnClickListener(this);
		edToDate.setOnClickListener(this);

		// ListView
		listview = (ListView) view.findViewById(R.id.listview);
		savedAppraisalsAdapter = new SavedAppraisalsAdapter(getActivity(), R.layout.item_saved_appraisal, savedAppraisalsList);
		listview.setAdapter(savedAppraisalsAdapter);

		Calendar calendar = Calendar.getInstance();
		String today = new SimpleDateFormat("dd MMM yyyy").format(calendar.getTime());

		calendar.add(Calendar.MONTH, -1);
		String pastDate = new SimpleDateFormat("dd MMM yyyy").format(calendar.getTime());

		edFromDate.setText(pastDate);
		edToDate.setText(today);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				hideKeyboard();
				getActivity().finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Saved Appraisals");
		//getActivity().getActionBar().setSubtitle(null);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.edStartDate:
				DatePickerFragment startDate = new DatePickerFragment();
				startDate.setDateListener(new DateListener()
				{
					@Override
					public void onDateSet(int year, int monthOfYear, int dayOfMonth)
					{
						Calendar cal = Calendar.getInstance();
						edFromDate.setText(Helper.showDateTime(Helper.createStringDateTime(year, monthOfYear, dayOfMonth, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))));
					}
				});
				startDate.show(getActivity().getFragmentManager(), "datePicker");
				break;

			case R.id.edEndDate:
				DatePickerFragment endDate = new DatePickerFragment();
				endDate.setDateListener(new DateListener()
				{
					@Override
					public void onDateSet(int year, int monthOfYear, int dayOfMonth)
					{
						Calendar cal = Calendar.getInstance();
						edToDate.setText(Helper.showDateTime(Helper.createStringDateTime(year, monthOfYear, dayOfMonth, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))));
					}
				});
				endDate.show(getActivity().getFragmentManager(), "datePicker");
				break;

			default:
				break;
		}

	}

}
