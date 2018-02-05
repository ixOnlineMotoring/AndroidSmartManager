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

public class OmeSpecsAdapter extends ArrayAdapter<SmartObject>
{
	List<SmartObject> list;
	
	public OmeSpecsAdapter(Context context, int resource,List<SmartObject> objects) 
	{
		super(context, resource, objects);
		list = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_oem_space, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tvKeySpecs.setText(list.get(position).getName());
		viewHolder.tvValueSpecs.setText(list.get(position).getType());
		
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvValueSpecs,tvKeySpecs;
		public void init(View v)
		{
			tvKeySpecs = (TextView) v.findViewById(R.id.tvKeySpecs);
			tvValueSpecs = (TextView) v.findViewById(R.id.tvValueSpecs);
		}
	}

}
