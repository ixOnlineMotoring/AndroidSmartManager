package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.RattingQuestions;
import com.nw.model.SmartObject;
import com.nw.widget.CustomEditText;
import com.smartmanager.android.R;

import java.util.ArrayList;
import java.util.List;

public class RateBuyersAdapter extends ArrayAdapter<SmartObject>
{
	ArrayList<RattingQuestions> rattingQuestions;

	public RateBuyersAdapter(Context context, int resource, List<SmartObject> objects, ArrayList<RattingQuestions> rattingQuestions, String from)
	{
		super(context, resource, objects);
		this.rattingQuestions = rattingQuestions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_rate_buyers, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolder) convertView.getTag();

		for (int i = 0; i < rattingQuestions.size(); i++)
		{
			if (getItem(position).getName().equals(rattingQuestions.get(i).getName()))
			{
				viewHolder.edInputRateBuyers.setText(Integer.toString(rattingQuestions.get(i).getValue()));
				break;
			}
		}
		viewHolder.tvRateBuyers.setText(getItem(position).getName());

		return convertView;
	}

	public static class ViewHolder
	{
		TextView tvRateBuyers;
		CustomEditText edInputRateBuyers;

		public void init(View v)
		{
			tvRateBuyers = (TextView) v.findViewById(R.id.tvRateBuyers);
			edInputRateBuyers = (CustomEditText) v.findViewById(R.id.tvInputRateBuyers);
		}
	}

}
