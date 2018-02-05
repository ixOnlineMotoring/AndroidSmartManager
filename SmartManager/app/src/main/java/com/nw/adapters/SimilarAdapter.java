package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.nw.model.SettingsUser;
import com.nw.widget.CustomTextViewLight;
import com.smartmanager.android.R;

import java.util.List;

public class SimilarAdapter extends ArrayAdapter<SettingsUser>
{
	Context context;
	public SimilarAdapter(Context context, int resource, List<SettingsUser> objects)
	{
		super(context, resource, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_similar, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvTitleCarName.setText(Html.fromHtml("<font color=#ffffff>" + "2010" + "</font> <font color=" + context.getResources().getColor(R.color.dark_blue) + ">"
				+ "Volkswagen Polo Hatch 1.4 Comfortline" + "</font>"+" (Man, Petrol)"));

		return convertView;
	}

	public static class ViewHolder
	{
		CustomTextViewLight tvTitleCarName;

		public void init(View v)
		{
			tvTitleCarName = (CustomTextViewLight) v.findViewById(R.id.tvTitleCarName);

		}

	}

}
