package com.nw.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.SettingsUser;
import com.smartmanager.android.R;

import java.util.List;

public class TradePartnersSettingAdapter extends ArrayAdapter<SettingsUser>
{
	Resources resources;
	public TradePartnersSettingAdapter(Context context, int resource,List<SettingsUser> objects) 
	{
		super(context, resource, objects);
		resources=getContext().getResources();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trade_partners, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvPartner.setText(getItem(position).getMemberName());
		if (getItem(position).isTenderAccess())
		{
			viewHolder.tvTenderAccess.setText("Yes");
		} else
		{
			viewHolder.tvTenderAccess.setText("No");
		}
		if (getItem(position).isAuctionAccess())
		{
			viewHolder.tvTradeAccess.setText("Yes"/*,after "+getItem(position).getTraderPeriod()+" days"*/);
		} else
		{
			viewHolder.tvTradeAccess.setText("No");
		}
		 
		
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvPartner,tvTradeAccess,tvTenderAccess ; 
		
		public void init(View v)
		{
			tvPartner = (TextView) v.findViewById(R.id.tvPartner);
			tvTradeAccess = (TextView) v.findViewById(R.id.tvTradeAccess);
			tvTenderAccess = (TextView) v.findViewById(R.id.tvTenderAccess);
		}
		
	}

}
