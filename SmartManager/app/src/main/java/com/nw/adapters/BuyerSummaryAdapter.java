package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.VehicleDetails;
import com.smartmanager.android.R;

import java.util.List;

public class BuyerSummaryAdapter extends ArrayAdapter<VehicleDetails>
{
	public BuyerSummaryAdapter(Context context, int resource,List<VehicleDetails> objects) 
	{
		super(context, resource, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_buyersummary, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tvbuyersSummeryClientName.setText(getItem(position).getClientName());
		viewHolder.tvbuyersSummerySalesCount.setText(Html.fromHtml("<u>"+getItem(position).getSales()+"</u>"));
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvbuyersSummeryClientName,tvbuyersSummerySalesCount;
		public void init(View v)
		{
			tvbuyersSummeryClientName = (TextView) v.findViewById(R.id.tvbuyersSummeryClientName);
			tvbuyersSummerySalesCount = (TextView) v.findViewById(R.id.tvbuyersSummerySalesCount);
		}
	}

}