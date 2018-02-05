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

public class SellDetailsAdapter extends ArrayAdapter<VehicleDetails>
{
	String type;

	public SellDetailsAdapter(Context context, int resource, List<VehicleDetails> objects, String from)
	{
		super(context, resource, objects);
		this.type = from;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_available_to_trade, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolder) convertView.getTag();

		viewHolder.tvVehicleNameYear.setText(Html.fromHtml("<font color=#ffffff>" + getItem(position).getYear() + "</font> <font color=" + getContext().getResources().getColor(R.color.dark_blue)
				+ ">" + getItem(position).getFriendlyName() + "</font>"));
	
		viewHolder.tvRegistration.setText(getItem(position).getRegistration());
		viewHolder.tvRegistration.append(" | "+getItem(position).getColour());
		viewHolder.tvRegistration.append(" | "+ getItem(position).getStockCode());
		viewHolder.tvDepartment.setText("" + getItem(position).getDepartment());
		viewHolder.tvDepartment.append(" | "+Helper.getFormattedDistance(getItem(position).getMileage() + "") + " Km");
		viewHolder.tvDepartment.append(Html.fromHtml( "| <font color=#3476BE> " + getItem(position).getAgeValue()+"</font>"));
		viewHolder.tvAskingPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getRetailPrice()) + ""));
		
		if (type.equalsIgnoreCase("BiddingEnded"))
		{
			viewHolder.tvBidPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getOfferAmt()) + ""));
			viewHolder.tvtradeprice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getPrice()) + ""));
		}
		else if (type.equalsIgnoreCase("BidsRecevied"))
		{
			viewHolder.tvBidPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getHighest()) + ""));
			viewHolder.tvtradeprice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getTradeprice()) + ""));
		}
		
		return convertView;
	}

	public static class ViewHolder
	{
		TextView tvVehicleNameYear;
		TextView tvVehicleMileage;
		TextView tvVehicleColour;
		TextView tvVehicleLocation;
		TextView tvAskingPrice;
		TextView tvtradeprice;
		TextView tvDepartment;
		TextView tvAge;
		TextView tvRegistration;
		TextView tvBidPrice;

		public void init(View v)
		{
			tvVehicleNameYear = (TextView) v.findViewById(R.id.tvVehicleNameYear);
			tvRegistration = (TextView) v.findViewById(R.id.tvRegistration);
			tvBidPrice = (TextView) v.findViewById(R.id.tvBidPrice);
			tvVehicleMileage = (TextView) v.findViewById(R.id.tvVehicleMileage);
			tvVehicleColour = (TextView) v.findViewById(R.id.tvVehicleColour);
			tvVehicleLocation = (TextView) v.findViewById(R.id.tvLocation);
			tvAskingPrice = (TextView) v.findViewById(R.id.tvAskingPrice);
			tvtradeprice = (TextView) v.findViewById(R.id.tvtradeprice);
			tvDepartment = (TextView) v.findViewById(R.id.tvDepartment);
			tvAge = (TextView) v.findViewById(R.id.tvAge);
			tvRegistration = (TextView) v.findViewById(R.id.tvRegistration);
		}

	}

}
