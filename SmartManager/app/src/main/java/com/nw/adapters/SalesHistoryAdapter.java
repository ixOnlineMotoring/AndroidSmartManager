package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.SmartObject;
import com.smartmanager.android.R;

import java.util.List;

public class SalesHistoryAdapter extends ArrayAdapter<SmartObject>
{
	public SalesHistoryAdapter(Context context, int resource,List<SmartObject> objects) 
	{
		super(context, resource, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_sales_history, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tvVariantname.setText(getItem(position).getName());
		viewHolder.tvSalesCount.setText(getItem(position).getID());
		
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvSalesCount,tvVariantname;
		
		public void init(View v)
		{
			tvVariantname = (TextView) v.findViewById(R.id.tvVariantname);
			tvSalesCount = (TextView) v.findViewById(R.id.tvSalesCount);
		}
	}

}
