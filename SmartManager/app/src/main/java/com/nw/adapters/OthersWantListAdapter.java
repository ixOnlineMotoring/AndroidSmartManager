package com.nw.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.nw.model.MessageListing;
import com.nw.model.OthersWants;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;

import java.util.ArrayList;
import java.util.List;

public class OthersWantListAdapter extends ArrayAdapter<OthersWants>
{
	Activity activity;
	MessageListingAdapter messageListingAdapter;
	ArrayList<MessageListing> messageList;

	public OthersWantListAdapter(Context context, Activity activity, int resource, List<OthersWants> objects)
	{
		super(context, resource, objects);
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_others_want, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolder) convertView.getTag();

		/*
		 * viewHolder.tvsalesYear.setText(""+getItem(position).getYear());
		 * viewHolder
		 * .tvsalesfriendlyName.setText(""+getItem(position).getFriendlyName());
		 * viewHolder
		 * .tvsalesMileage.setText(Helper.getFormattedDistance(getItem(
		 * position).getMileage()+"")+" Km");
		 * viewHolder.tvsalesColour.setText(""+getItem(position).getColour());
		 * viewHolder.tvsalesStockno.setText(getItem(position).getStockCode());
		 * viewHolder
		 * .tvsalesUserName.setText(getItem(position).getClientName());
		 * viewHolder.tvsalesTradePrice.setText(Helper.formatPrice(new
		 * BigDecimal(getItem(position).getTradeprice()) + ""));
		 */
		viewHolder.btMessage.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				messageList = new ArrayList<MessageListing>();
				for (int i = 0; i < 10; i++)
				{
					messageList.add(new MessageListing());
				}
				messageListingAdapter = new MessageListingAdapter(activity, R.layout.single_item_seller_message, messageList, "Message");
				viewHolder.lvMessage.setAdapter(messageListingAdapter);
			}
		});
		return convertView;
	}

	public static class ViewHolder
	{
		// TextView
		// tvsalesYear,tvsalesfriendlyName,tvsalesMileage,tvsalesColour,tvsalesStockno,tvsalesUserName,tvsalesTradePrice;
		Button btMessage;
		StaticListView lvMessage;

		public void init(View v)
		{
			// tvsalesYear = (TextView) v.findViewById(R.id.tvsalesYear);
			btMessage = (Button) v.findViewById(R.id.btMessage);
			lvMessage = (StaticListView) v.findViewById(R.id.lvMessage);

		}

	}

}
