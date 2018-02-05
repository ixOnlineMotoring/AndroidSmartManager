package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.nw.model.Vehicle;
import com.smartmanager.android.R;

import java.util.List;

public class FinanceHistoryAdapter extends ArrayAdapter<Vehicle>
{
	public FinanceHistoryAdapter(Context context, int resource,List<Vehicle> objects) 
	{
		super(context, resource, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_finance_history, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		return convertView;
	}
	
	public static class ViewHolder
	{
		
		public void init(View v)
		{
		//	tvPartner = (CustomTextViewLight) v.findViewById(R.id.tvPartner);
			
		}
		
	}

}
