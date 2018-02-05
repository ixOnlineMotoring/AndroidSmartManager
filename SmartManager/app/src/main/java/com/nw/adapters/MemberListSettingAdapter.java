package com.nw.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.SettingsUser;
import com.smartmanager.android.R;

import java.util.List;

public class MemberListSettingAdapter extends ArrayAdapter<SettingsUser>
{
	Resources resources;

	public MemberListSettingAdapter(Context context, int resource, List<SettingsUser> objects)
	{
		super(context, resource, objects);
		resources = getContext().getResources();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_members, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolder) convertView.getTag();

		if (getItem(position).isTradeBuy())
		{
			viewHolder.tvTradebuy.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.green) + ">\u2713</font>"));
		}
		else
		{
			viewHolder.tvTradebuy.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.red) + ">\u2718</font>"));
		}
		if (getItem(position).isTradeSell())
		{
			viewHolder.tvTradesell.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.green) + ">\u2713</font>"));
		}
		else
		{
			viewHolder.tvTradesell.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.red) + ">\u2718</font>"));
		}
		if (getItem(position).isTenderAccept())
		{
			viewHolder.tvTenderAccept.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.green) + ">\u2713</font>"));
		}
		else
		{
			viewHolder.tvTenderAccept.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.red) + ">\u2718</font>"));
		}
		if (getItem(position).isTenderDecline())
		{
			viewHolder.tvTenderDecline.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.green) + ">\u2713</font>"));
		}
		else
		{
			viewHolder.tvTenderDecline.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.red) + ">\u2718</font>"));
		}
		if (getItem(position).isTenderManager())
		{
			viewHolder.tvTenderMgr.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.green) + ">\u2713</font>"));
		}
		else
		{
			viewHolder.tvTenderMgr.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.red) + ">\u2718</font>"));
		}
		if (getItem(position).isTenderAuditor())
		{
			viewHolder.tvTenderAuditor.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.green) + ">\u2713</font>"));
		}
		else
		{
			viewHolder.tvTenderAuditor.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.red) + ">\u2718</font>"));
		}
		if (getItem(position).isTenderAuditor())
		{
			viewHolder.tvTenderAuditor.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.green) + ">\u2713</font>"));
		}
		else
		{
			viewHolder.tvTenderAuditor.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.red) + ">\u2718</font>"));
		}
		viewHolder.tvMemberName.setText(getItem(position).getMemberName());

		return convertView;
	}

	public static class ViewHolder
	{
		TextView tvMemberName, tvTradebuy, tvTradesell, tvTenderAccept, tvTenderDecline, tvTenderMgr, tvTenderAuditor;

		public void init(View v)
		{
			tvMemberName = (TextView) v.findViewById(R.id.tvMemberName);
			tvTradebuy = (TextView) v.findViewById(R.id.tvTradebuy);
			tvTradesell = (TextView) v.findViewById(R.id.tvTradesell);
			tvTenderAccept = (TextView) v.findViewById(R.id.tvTenderAccept);
			tvTenderDecline = (TextView) v.findViewById(R.id.tvTenderDecline);
			tvTenderMgr = (TextView) v.findViewById(R.id.tvTenderMgr);
			tvTenderAuditor = (TextView) v.findViewById(R.id.tvTenderAuditor);
		}

	}

}
