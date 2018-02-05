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

public class BidsDetailsPrivateOffersAdapter extends ArrayAdapter<VehicleDetails>
{
	public BidsDetailsPrivateOffersAdapter(Context context, int resource,List<VehicleDetails> objects,String from) 
	{
		super(context, resource, objects);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_privateoffers, parent,false);
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

		viewHolder.tvprivateseller.setText(getItem(position).getClientName());
		viewHolder.tvlocation.setText("");
		viewHolder.tvlocation.append(" | "+getItem(position).getColour());
		viewHolder.tvlocation.append(" | "+getItem(position).getStockCode());
		viewHolder.tvAskingbidPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getTradeprice()) + ""));
		viewHolder.tvOffered.setText(Helper.formatPrice(new BigDecimal(getItem(position).getAmount()) + ""));
		viewHolder.tvdatetime.setText(getItem(position).getUserName()+" at "+getItem(position).getDate());
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvVehicleNameYear,tvprivateseller,tvlocation,tvColor,tvStock,tvAskingbidPrice,tvOffered,tvdatetime;
		
		public void init(View v)
		{
			 tvVehicleNameYear = (TextView) v.findViewById(R.id.tvprivateofferVehicleName);
			 tvprivateseller = (TextView) v.findViewById(R.id.tvprivateseller);
			 tvlocation = (TextView) v.findViewById(R.id.tvlocation);
			 tvColor = (TextView) v.findViewById(R.id.tvColor);
			 tvStock = (TextView) v.findViewById(R.id.tvStock);
			 tvAskingbidPrice = (TextView) v.findViewById(R.id.tvAskingbidPrice);
			 tvOffered = (TextView) v.findViewById(R.id.tvOffered);
			 tvdatetime = (TextView) v.findViewById(R.id.tvdatetime);
			
		}
	}

}
