package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.VehicleDetails;
import com.nw.widget.CustomCheckBox;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.List;

public class BidReceivedAdapter extends ArrayAdapter<VehicleDetails>
{

	ListClickListener listClickListener;

	public BidReceivedAdapter(Context context, int resource, List<VehicleDetails> objects)
	{

		super(context, resource, objects);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{

		final ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_bid_received, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvBidderName.setText(getItem(position).getOfferClient());
		viewHolder.tvBidDate.setText("" + getItem(position).getOfferDate());
		viewHolder.tvBidPerson.setText("" + getItem(position).getOfferMember());
		viewHolder.tvBidPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getOfferAmt())+""));

		viewHolder.chkBidResponse.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (listClickListener != null)
					listClickListener.ListClick(position);
			}
		});
		viewHolder.chkBidResponse.setChecked(getItem(position).isBidChecked());

		return convertView;
	}

	public static class ViewHolder
	{
		TextView tvBidderName, tvBidDate, tvBidPerson, tvBidPrice;
		CustomCheckBox chkBidResponse;

		public void init(View v)
		{

			tvBidderName = (TextView) v.findViewById(R.id.tvBiderName);
			tvBidDate = (TextView) v.findViewById(R.id.tvBidDateTime);
			tvBidPerson = (TextView) v.findViewById(R.id.tvBidPerson);
			tvBidPrice = (TextView) v.findViewById(R.id.tvBidPrice);
			chkBidResponse = (CustomCheckBox) v.findViewById(R.id.chkBidResponse);
		}

	}

	/*
	 * public VehicleDetails getSelectedVehicle() { return getItem(0); }
	 */

	public interface ListClickListener
	{
		public void ListClick(int postion);
	}

	public void setListClickListener(ListClickListener lstListClickListener)
	{
		this.listClickListener = lstListClickListener;
	}

}