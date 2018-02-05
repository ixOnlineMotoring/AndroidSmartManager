package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.AverageDays;
import com.smartmanager.android.R;

import java.util.List;

public class AverageDaysAdapter extends ArrayAdapter<AverageDays>
{
	public AverageDaysAdapter(Context context, int resource,List<AverageDays> arrayList) 
	{
		super(context, resource, arrayList);
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_average_days, parent,false);
			viewHolder=new ViewHolder();



			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tvVariantName.setText(getItem(position).getVariantName());
		viewHolder.tvUs.setText(getItem(position).getClientAverageDays()+"");
		viewHolder.tvCity.setText(getItem(position).getCityAverageDays()+"");
		viewHolder.tvNational.setText(getItem(position).getNationalAverageDays()+"");
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvVariantName,tvUs,tvCity,tvNational;
		public void init(View v)
		{
			tvVariantName = (TextView) v.findViewById(R.id.tvVariantName);
			tvUs = (TextView) v.findViewById(R.id.tvUs);
			tvCity = (TextView) v.findViewById(R.id.tvCity);
			tvNational = (TextView) v.findViewById(R.id.tvNational);
		}
		
	}

}
