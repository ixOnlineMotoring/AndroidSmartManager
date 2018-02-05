package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.nw.model.Audit;
import com.nw.widget.CustomTextViewLight;
import com.smartmanager.android.R;

import java.util.ArrayList;

public class AuditHistoryAdapter extends ArrayAdapter<Audit>
{
	ArrayList<Audit> audits;
	Context mContext;
	
	public AuditHistoryAdapter(Context context, int resource, ArrayList<Audit> audit)
	{
		super(context, 0, audit);
		mContext = context;
		audits = audit;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if(convertView==null)
		{
			holder=new ViewHolder();
			convertView=LayoutInflater.from(mContext).inflate(R.layout.single_item, parent,false);
			holder.tvDate = (CustomTextViewLight) convertView.findViewById(R.id.tvDate);
			holder.tvVehicleNum = (CustomTextViewLight) convertView.findViewById(R.id.tvNoOfVehicles);
			convertView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.tvDate.setText(audits.get(position).getAuditDate());
		if (audits.get(position).getAuditedVehicles().equals("1"))
		{
			holder.tvVehicleNum.setText(audits.get(position).getAuditedVehicles()+" Vehicle");
		}else {
			holder.tvVehicleNum.setText(audits.get(position).getAuditedVehicles()+" Vehicles");
		}
		
		return convertView;
	}
	
	public static class ViewHolder
	{
		CustomTextViewLight tvDate,tvVehicleNum; 
	}
}