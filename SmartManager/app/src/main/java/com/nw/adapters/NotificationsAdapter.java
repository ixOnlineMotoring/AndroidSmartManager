package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmanager.android.R;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationsAdapter extends BaseExpandableListAdapter
{
	String[] groups;
	HashMap<String, ArrayList<String>>data;
	Context context;
	
	public NotificationsAdapter(Context context,String[] groups,HashMap<String,ArrayList<String>>data)
	{
		this.context=context;
		this.groups=groups;
		this.data=data;
	}
	@Override
	public String getChild(int groupPosition, int childPosition)
	{
		return data.get(groups[groupPosition]).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if(convertView==null)
		{
			convertView=LayoutInflater.from(context).inflate(R.layout.vw_notification_child, parent,false);
			holder= new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		}else{
			holder= (ViewHolder) convertView.getTag();
		}
				
		holder.tvPersonalDetails.setText("Fred Basset | 083 566 6561 | johan@ix.co.za");
		holder.tvVehicleDetails.setText("Toyota Fortuner 4.0 | White | 23 000km | cars.co.za");
		holder.tvLeadsId.setText("Lead ID 12345");
		holder.tvDaysAgo.setText("12:48 - 10 days ago");
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition){
		return data.get(groups[groupPosition]).size();
	}

	@Override
	public String getGroup(int groupPosition){
		return groups[groupPosition];
	}

	@Override
	public int getGroupCount(){
		return groups.length;
	}

	@Override
	public long getGroupId(int groupPosition){
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.vw_notification_header, parent,false);
		}
		
		TextView tvHeader=(TextView) convertView.findViewById(R.id.headerView);
		tvHeader.setText(""+getGroup(groupPosition));
		ImageView ivIcon=(ImageView) convertView.findViewById(R.id.imageView1);
		if(isExpanded)
			ivIcon.setRotation(90);
		else	
			ivIcon.setRotation(0);
		return convertView;
	}

	@Override
	public boolean hasStableIds(){
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition){
		return true;
	}
	
	private static class ViewHolder{
		TextView tvPersonalDetails;
		TextView tvVehicleDetails;
		TextView tvLeadsId;
		TextView tvDaysAgo;
		
		protected void init(View convertView){
			tvPersonalDetails= (TextView) convertView.findViewById(R.id.tvPersonalDetails);
			tvVehicleDetails= (TextView) convertView.findViewById(R.id.tvVehicleDetails);
			tvLeadsId= (TextView) convertView.findViewById(R.id.tvLeadId);
			tvDaysAgo= (TextView) convertView.findViewById(R.id.tvDaysAgo);
		}
	}
}
