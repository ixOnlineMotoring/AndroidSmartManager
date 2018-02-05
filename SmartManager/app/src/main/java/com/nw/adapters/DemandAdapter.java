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

public class DemandAdapter extends ArrayAdapter<SmartObject>
{
	public DemandAdapter(Context context, int resource,List<SmartObject> objects) 
	{
		super(context, resource, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_demand, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tvName.setText(getItem(position).getName());
		viewHolder.tvCountFirst.setText(getItem(position).getId()+"");
		viewHolder.tvCountSecond.setText(getItem(position).getType());
		
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvName,tvRanking,tvCountSecond,tvCountFirst;
		
		public void init(View v)
		{
			tvName = (TextView) v.findViewById(R.id.tvName);
			tvCountFirst=(TextView) v.findViewById(R.id.tvCountFirst);
			tvCountSecond = (TextView) v.findViewById(R.id.tvCountSecond);
		}
	}

}
