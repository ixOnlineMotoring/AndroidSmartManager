package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.Variant;
import com.smartmanager.android.R;

import java.util.List;

public class NewVariantAdapter extends ArrayAdapter<Variant>
{

	public NewVariantAdapter(Context context, int resource, List<Variant> objects)
	{
		super(context, resource, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if(convertView==null)
		{
			convertView=LayoutInflater.from(getContext()).inflate(R.layout.list_item_variant,parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder=(ViewHolder) convertView.getTag();
		
		viewHolder.tvTitle.setText(Html.fromHtml(""+getItem(position).getVariantName()+""));
		viewHolder.tvSubTitle.setText(Html.fromHtml("<font color=\"#FF0707\">"+getItem(position).getMeadCode()+" ("+getItem(position).getMinYear()+" to "+getItem(position).getMaxYear()+")</font>"));
		return convertView;
	}
	
	private static class ViewHolder
	{
		TextView tvTitle,tvSubTitle;
		
		public void init(View convertView){
			tvTitle=(TextView) convertView.findViewById(R.id.tvTitle);
			tvSubTitle=(TextView) convertView.findViewById(R.id.tvSubTitle);
		}
	}
}
