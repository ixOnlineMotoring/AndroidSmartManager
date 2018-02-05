package com.nw.adapters;

import android.content.Context;
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

public class SaleListSalesAdapter extends ArrayAdapter<VehicleDetails>
{
	public SaleListSalesAdapter(Context context, int resource,List<VehicleDetails> objects) 
	{
		super(context, resource, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_saleslist, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder=(ViewHolder) convertView.getTag();
		
		

		viewHolder.tvsalesYear.setText(""+getItem(position).getYear());
		viewHolder.tvsalesfriendlyName.setText(""+getItem(position).getFriendlyName());
		viewHolder.tvsalesMileage.setText(Helper.getFormattedDistance(getItem(position).getMileage()+"")+" Km");
		viewHolder.tvsalesMileage.append(" | "+getItem(position).getColour());
		viewHolder.tvsalesMileage.append(" | "+getItem(position).getStockCode());
		viewHolder.tvsalesUserName.setText(getItem(position).getClientName());
		viewHolder.tvsalesTradePrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getTradeprice()) + ""));
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvsalesYear,tvsalesfriendlyName,tvsalesMileage,tvsalesColour,tvsalesStockno,tvsalesUserName,tvsalesTradePrice;
		
		
		public void init(View v)
		{
			tvsalesYear = (TextView) v.findViewById(R.id.tvsalesYear);
			tvsalesfriendlyName = (TextView) v.findViewById(R.id.tvsalesfriendlyName);
			tvsalesMileage = (TextView) v.findViewById(R.id.tvsalesMileage);
			tvsalesColour = (TextView) v.findViewById(R.id.tvsalesColour);
			tvsalesStockno = (TextView) v.findViewById(R.id.tvsalesStockno);
			tvsalesUserName = (TextView) v.findViewById(R.id.tvsalesUserName);
			tvsalesTradePrice = (TextView) v.findViewById(R.id.tvsalesTradePrice);
		}
		
	}

}
