package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.Vehicle;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.ArrayList;

public class VINHistoryAdapter extends ArrayAdapter<Vehicle>
{

	public VINHistoryAdapter(Context c, ArrayList<Vehicle> list)
	{
		super(c, R.layout.list_bid_item, list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_vin_history, null);
			holder.init(convertView);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvHistoryDetails.setText(getItem(position).getOwnerName()+" | "+getItem(position).getLocation()+" | "+
				getItem(position).getExpires()+" | "+Helper.getFormattedDistance(""+getItem(position).getMileage())+" | "+Helper.formatPrice(new BigDecimal(getItem(position).getRetailPrice())+""));
		
		return convertView;
	}

	private static class ViewHolder
	{

		TextView tvHistoryDetails;

		public void init(View convertView)
		{

			tvHistoryDetails = (TextView) convertView.findViewById(R.id.tvHistoryDetails);
		}
	}
}
