package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.MyBuyers;
import com.smartmanager.android.R;

import java.util.List;

public class MyBuyersAdapter extends ArrayAdapter<MyBuyers>
{
	public MyBuyersAdapter(Context context, int resource,List<MyBuyers> objects,String from) 
	{
		super(context, resource, objects);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_my_buyers, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder=(ViewHolder) convertView.getTag();
		
		viewHolder.tvBuyersName.setText(getItem(position).getName());
		viewHolder.tvBuyersLocation.setText(getItem(position).getType());
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvBuyersName,tvBuyersLocation;
		
		public void init(View v)
		{
			tvBuyersName = (TextView) v.findViewById(R.id.tvBuyersName);
			tvBuyersLocation = (TextView) v.findViewById(R.id.tvBuyersLocation);
		}
		
	}

}
