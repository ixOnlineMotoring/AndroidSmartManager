package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.interfaces.OnListItemClickListener;
import com.nw.model.SmartObject;
import com.smartmanager.android.R;

import java.util.List;

public class LeadPoolAdapter extends ArrayAdapter<SmartObject>
{

	OnListItemClickListener onActiveClickListener,onCloseClickListener;
	public LeadPoolAdapter(Context context, int resource,List<SmartObject> objects,OnListItemClickListener onListItemClickListener,OnListItemClickListener oncloseitemclick) 
	{
		super(context, resource, objects);
		this.onActiveClickListener=onListItemClickListener;
		this.onCloseClickListener = oncloseitemclick;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_lead_pool, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tvName.setText(getItem(position).getName());
		switch (position)
		{
			case 0:
				viewHolder.tvActive.setText(Html.fromHtml("<u>"+getItem(position).getId()+"</u>"));
				viewHolder.tvClose.setText(Html.fromHtml("<u>"+getItem(position).getType()+"</u>"));
				break;
				
			case 1:
				viewHolder.tvActive.setText(getItem(position).getId()+"");
				viewHolder.tvClose.setText(Html.fromHtml("<u>"+getItem(position).getType()+"</u>"));
				break;
		}
		viewHolder.tvActive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onActiveClickListener.onClick(position);
			}
		});
		viewHolder.tvClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onCloseClickListener.onClick(position);
			}
		});
		return convertView;
	}
	
	public static class ViewHolder
	{
		TextView tvActive,tvClose,tvName;
		public void init(View v)
		{
			tvName = (TextView) v.findViewById(R.id.tvName);
			tvActive=(TextView) v.findViewById(R.id.tvActive);
			tvClose=(TextView) v.findViewById(R.id.tvClose);
		}
	}

}
