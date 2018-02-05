package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.Availability;
import com.smartmanager.android.R;

import java.util.List;

public class AvailabilityAdapter extends ArrayAdapter<Availability>
{
	public AvailabilityAdapter(Context context, int resource,List<Availability> objects) 
	{
		super(context, resource, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_availability, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tvName.setText(getItem(position).getVariantName());
		viewHolder.tvUS.setText(""+getItem(position).getClientAvailability());
		viewHolder.tvCountFirst.setText(""+getItem(position).getGroupAvailability());
		viewHolder.tvCountSecond.setText(""+getItem(position).getProvinceAvailability());
		viewHolder.tvCountThird.setText(""+getItem(position).getNationalAvailability());
		
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvName,tvUS,tvCountFirst,tvCountSecond,tvCountThird;
		
		public void init(View v)
		{
			tvName = (TextView) v.findViewById(R.id.tvName);
			tvUS = (TextView) v.findViewById(R.id.tvUS);
			tvCountFirst = (TextView) v.findViewById(R.id.tvCountFirst);
			tvCountSecond = (TextView) v.findViewById(R.id.tvCountSecond);
			tvCountThird = (TextView) v.findViewById(R.id.tvCountThird);
			
		}
		
	}

}
