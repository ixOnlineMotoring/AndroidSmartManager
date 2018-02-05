package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.SavedAppraisals;
import com.smartmanager.android.R;

import java.util.List;

public class SavedAppraisalsAdapter extends ArrayAdapter<SavedAppraisals>
{

	public SavedAppraisalsAdapter(Context context, int resource, List<SavedAppraisals> objects)
	{
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Viewholder holder;
		if (convertView == null)
		{
			holder = new Viewholder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_saved_appraisal, parent, false);

			holder.tvVehicleName = (TextView) convertView.findViewById(R.id.tvVehicleName);
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (Viewholder) convertView.getTag();
		}
		
		
		holder.tvVehicleName.setText(Html.fromHtml("<font color=#ffffff>" + "2010" + "</font> <font color=" + getContext().getResources().getColor(R.color.dark_blue)
				+ ">" + "Volkswagen Polo 1.4 Comfortline" + "</font>"));

		return convertView;
	}

	public static class Viewholder
	{
		TextView tvVehicleName;
	}

}
