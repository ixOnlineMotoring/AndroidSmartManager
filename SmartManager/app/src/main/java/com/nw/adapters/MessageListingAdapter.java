package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.MessageListing;
import com.nw.widget.CustomEditText;
import com.smartmanager.android.R;

import java.util.List;

public class MessageListingAdapter extends ArrayAdapter<MessageListing>
{
	public MessageListingAdapter(Context context, int resource, List<MessageListing> objects, String from)
	{
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_item_seller_message, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolder) convertView.getTag();

		viewHolder.tvUserName.setText(getItem(position).getName());
		viewHolder.tvUserMessage.setText(getItem(position).getMessage());
		viewHolder.tvUserDate.setText(getItem(position).getDate());
		return convertView;
	}

	public static class ViewHolder
	{
		TextView tvUserName, tvUserMessage, tvUserDate;
		CustomEditText edInputRateBuyers;

		public void init(View v)
		{
			tvUserName = (TextView) v.findViewById(R.id.tvUserName);
			tvUserMessage = (TextView) v.findViewById(R.id.tvUserMessage);
			tvUserDate = (TextView) v.findViewById(R.id.tvUserDate);
		}
	}

}
