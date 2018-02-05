package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.VehicleDetails;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.List;

public class TraderVehiclesAdapter extends ArrayAdapter<VehicleDetails>
{
	public TraderVehiclesAdapter(Context context, int resource,List<VehicleDetails> objects) 
	{
		super(context, resource, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_tradevehicles, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder=(ViewHolder) convertView.getTag();
		
		viewHolder.tvVehicleNameYear.setText(Html.fromHtml(
				"<font color=#ffffff>"+ getItem(position).getYear()
				+ "</font> <font color="+ getContext().getResources().getColor(R.color.dark_blue)+ ">"
				+ getItem(position).getFriendlyName() + "</font>"));
		viewHolder.tvDepartment.setText(getItem(position).getType());
		viewHolder.tvDepartment.append(" | "+Helper.getFormattedDistance(getItem(position).getMileage()+"")+" Km");
		viewHolder.tvDepartment.append(Html.fromHtml(" | <font color=#3476BE>"+getItem(position).getAge()+" Days"));
		viewHolder.tvRegNumber.setText(""+getItem(position).getRegistration());
		viewHolder.tvRegNumber.append(" | "+getItem(position).getColour());
		viewHolder.tvRegNumber.append(" | "+getItem(position).getStockCode());
		viewHolder.tvretPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getRetailPrice()) + ""));
		viewHolder.tvTrdbids.setText(Helper.formatPrice(new BigDecimal(getItem(position).getPrice()) + ""));
		viewHolder.tvBidPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getOfferAmt()) + ""));
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvVehicleNameYear,tvColor,tvStock,tvDepartment,tvMileage,tvCurrent,tvcapPrice,tvincrementprice,tvinvitedvalue,tvRegNumber,tvRemainingDays,tvretPrice,tvTrdbids,tvBidPrice;
		
		
		public void init(View v)
		{
			 tvVehicleNameYear = (TextView) v.findViewById(R.id.tvVehicleName);
			 tvColor = (TextView) v.findViewById(R.id.tvColor);
			 tvStock = (TextView) v.findViewById(R.id.tvStock);
			 tvDepartment = (TextView) v.findViewById(R.id.tvDepartment);
			 tvMileage = (TextView) v.findViewById(R.id.tvMileage);
			 tvCurrent = (TextView) v.findViewById(R.id.tvCurrent);
			 tvRegNumber = (TextView) v.findViewById(R.id.tvRegNumber);
			 tvRemainingDays = (TextView) v.findViewById(R.id.tvRemainingDays);
			 tvretPrice = (TextView) v.findViewById(R.id.tvretPrice);
			 tvTrdbids = (TextView) v.findViewById(R.id.tvTrdbids);
			 tvBidPrice = (TextView) v.findViewById(R.id.tvBidPrice);
		}
	}

}
