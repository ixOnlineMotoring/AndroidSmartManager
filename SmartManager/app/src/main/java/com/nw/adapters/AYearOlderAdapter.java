package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.YearYounger;
import com.smartmanager.android.R;

import java.util.List;

public class AYearOlderAdapter extends ArrayAdapter<YearYounger>
{
	public AYearOlderAdapter(Context context, int resource,List<YearYounger> objects) 
	{
		super(context, resource, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_a_year_older, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tv_item_title.setText(Html.fromHtml(getItem(position).getYear()+" <font color=\"#3476BE\">"+getItem(position).getVariantName()+"</font>"+" ("+getItem(position).getFuelType()+"/"+getItem(position).getTransmission()+")"));
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tv_item_title;
		public void init(View v)
		{
			tv_item_title = (TextView) v.findViewById(R.id.tv_item_title);
			
		}
	}

}
