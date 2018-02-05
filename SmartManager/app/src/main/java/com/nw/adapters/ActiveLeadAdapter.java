package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.Leads;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.util.List;

public class ActiveLeadAdapter extends ArrayAdapter<Leads>
{
	String data;
	
	public ActiveLeadAdapter(Context context, int resource,List<Leads> objects) 
	{
		super(context, resource, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_active_leads, parent,false);
			holder=new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.tvUserName.setText(getItem(position).getUsername());
		holder.tvUserNumber.setText(getItem(position).getId()+"");
		data="";
		if(TextUtils.isEmpty(Helper.formatMobileNumber(getItem(position).getPhoneNumber()))){
			if (TextUtils.isEmpty(getItem(position).getUserEmail()))
			{
				data= "Phone number? | Email address?";
			}else {
				data= "Phone number? | " + getItem(position).getUserEmail();
			}
		}else{
			data=Helper.formatMobileNumber(getItem(position).getPhoneNumber());
			if(!TextUtils.isEmpty(getItem(position).getUserEmail()))
				data=data+" | "+getItem(position).getUserEmail();
			else {
				data = data + " | Email address?";
			}
		}
		if(!TextUtils.isEmpty(data))
		{
			holder.tvUserPhoneNumber.setText(data);
			holder.tvUserPhoneNumber.setVisibility(View.VISIBLE);
		}				
		else
		{
			holder.tvUserPhoneNumber.setVisibility(View.GONE);
		}
		/*if (getItem(position).getVehicleDescription().contains("null"))
		{
			holder.tvVehicleDescription.setText("Vehicle info?");
		}else if (getItem(position).getVehicleDescription().trim().equalsIgnoreCase("0")) 
		{
			holder.tvVehicleDescription.setText("Vehicle info?");
		}
		else{*/
			holder.tvVehicleDescription.setText(Html.fromHtml("<font color=#ffffff>" + getItem(position).getYear() + "</font> <font color=" + getContext().getResources().getColor(R.color.dark_blue) + ">"
				+ getItem(position).getMakeAsked()+" "+getItem(position).getModelAsked()+" "+getItem(position).getVariant()+ "</font>"));//}
	
		if(TextUtils.isEmpty(getItem(position).getDaysLeft())||getItem(position).getDaysLeft().equals("0"))
		{
			holder.tvDaysAgo.setText("No update info");
		}		
		else
		{
			holder.tvDaysAgo.setText(Html.fromHtml("<font color="+getContext().getResources().getColor(R.color.red)+">Last Update: " + getItem(position).getDaysLeft() + " days ago</font> <font color=" + getContext().getResources().getColor(R.color.white) + "> by "
					+ getItem(position).getFriendlyName()+ "</font>"));
		}
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvUserName ,tvUserNumber,tvUserPhoneNumber,tvVehicleDescription,tvDaysAgo;
		
		public void init(View convertView)
		{
			tvUserNumber= (TextView) convertView.findViewById(R.id.tvUserNumber);
			tvUserName= (TextView) convertView.findViewById(R.id.tvUserName);
			tvUserPhoneNumber= (TextView) convertView.findViewById(R.id.tvPhoneNumber);
			tvVehicleDescription= (TextView) convertView.findViewById(R.id.tvVehicleDescription);
			tvDaysAgo= (TextView) convertView.findViewById(R.id.tvDaysAgo);	
		}
	}

}
