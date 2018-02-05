package com.nw.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.DatePicker;

import com.nw.interfaces.DateListener;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{

	DatePicker datePicker;
	DatePickerDialog datePickerDialog;
	boolean isEndDate;
	DateListener dateListener;
	//String date;
	
	public void setDateListener(DateListener dateListener) {
		this.dateListener=dateListener;
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
        // Use the current date as the default date in the picker
    	 final Calendar c = Calendar.getInstance();
    	 int day = c.get(Calendar.DAY_OF_MONTH);
    	
    	 
    	 if(getArguments()!=null)
    	 {
    		 isEndDate=Boolean.parseBoolean(getArguments().get("isEndDate").toString());
    		 if(isEndDate)
        	 {
        		c.set(Calendar.DAY_OF_MONTH, day+1);
        		day=day+1;
        	 }
    	 }
    	
    	 int year = c.get(Calendar.YEAR);
         int month = c.get(Calendar.MONTH);
         day = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog= new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Set", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) 
			{
				arg0.dismiss();
				    if(dateListener!=null)
				     dateListener.onDateSet(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
			}
		});
        
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1)
			{
				arg0.dismiss();
			}
		});
        datePicker=datePickerDialog.getDatePicker();
        if(getArguments()!=null)
        	datePicker.setMinDate(c.getTimeInMillis());
        // Create a new instance of DatePickerDialog and return it
        return datePickerDialog;
    }
    @Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {}



}