package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nw.model.VehicleDetails;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ActiveBidsAdapter extends ArrayAdapter<VehicleDetails>{

	int lastCheckedPosition=0;
	public ActiveBidsAdapter(Context c, ArrayList<VehicleDetails> list) {
		super(c, R.layout.list_bid_item, list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			holder= new ViewHolder();
			convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_bid_item, null);
			holder.init(convertView);
			convertView.setTag(holder);
		}else{
			holder= (ViewHolder) convertView.getTag();
		}
		
		holder.tvBidAmount.setText(Helper.formatPrice(new BigDecimal(getItem(position).getOfferAmt())+""));
		holder.tvName.setText(getItem(position).getOfferClient());
		holder.tvVehicleName.setText(getItem(position).getOfferMember());
		holder.rbBidSelect.setTag(position);
		holder.rbBidSelect.setChecked(getItem(position).isBidChecked());
		holder.tvTime.setText(getItem(position).getOfferDate());
		holder.rbBidSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked){
					getItem(lastCheckedPosition).setBidChecked(false);
					getItem(position).setBidChecked(true);
					lastCheckedPosition=position;
					notifyDataSetChanged();
				}
			}
		});
		return convertView;
	}
	
	private static class ViewHolder{
		
		TextView tvBidAmount;
		TextView tvName;
		TextView tvVehicleName;
		TextView tvTime;
		RadioButton rbBidSelect;
		
		public void init(View convertView){
			
			tvBidAmount= (TextView) convertView.findViewById(R.id.tvBidAmount);
			tvName= (TextView) convertView.findViewById(R.id.tvName);
			tvVehicleName= (TextView) convertView.findViewById(R.id.tvVehicleName);
			tvTime= (TextView) convertView.findViewById(R.id.tvDuration);
			rbBidSelect= (RadioButton) convertView.findViewById(R.id.rbBidSelect);
		}
	}
	
	public VehicleDetails getSelectedVehicle(){
		return getItem(0);
	}
}
