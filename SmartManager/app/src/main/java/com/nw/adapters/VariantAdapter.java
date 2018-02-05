package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.SpecialVehicle;
import com.smartmanager.android.R;

import java.util.List;

public class VariantAdapter extends ArrayAdapter<SpecialVehicle> {

	public VariantAdapter(Context context, int resource, List<SpecialVehicle> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null)
		{
			holder= new ViewHolder();
			convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_variant_details, parent, false);
			holder.init(convertView);
			convertView.setTag(holder);
		}else{
			holder= (ViewHolder) convertView.getTag();
		}
		if(!getItem(position).getFriendlyName().equals("No Vehicle Name Loaded")&&!getItem(position).getFriendlyName().equals("VehicleName?"))
			holder.tvVariantName.setText(getItem(position).getFriendlyName());
		else
			holder.tvVariantName.setText(getItem(position).getVariantName());
		holder.tvStock.setText(Html.fromHtml("<font color=\"#FF0707\">"+getItem(position).getUsedYear()+" | "
			+getItem(position).getStockCode()+" | "+getItem(position).getColour()+"</font>"));
		
		return  convertView;
	}
	
	private static class ViewHolder{
		TextView tvVariantName;
		TextView tvStock;
		
		public void init(View convertView){
			tvVariantName= (TextView) convertView.findViewById(R.id.tvVariantName);
			tvStock= (TextView) convertView.findViewById(R.id.tvStock);
		}
	}
}
