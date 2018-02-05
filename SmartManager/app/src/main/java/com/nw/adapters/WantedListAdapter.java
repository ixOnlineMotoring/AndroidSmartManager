package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nw.model.Wanted;
import com.nw.webservice.VolleySingleton;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.List;

public class WantedListAdapter extends ArrayAdapter<Wanted>
{
	ImageLoader imageLoader;
	public WantedListAdapter(Context context, int resource,List<Wanted> objects) 
	{
		super(context, resource, objects);
		imageLoader= VolleySingleton.getInstance().getImageLoader();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_wanted_list, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder=(ViewHolder) convertView.getTag();
		
		viewHolder.tvVehicleNameYear.setText(Html.fromHtml("<font color=#ffffff>"+ getItem(position).getYear()
				+ "</font> <font color="+ getContext().getResources().getColor(R.color.dark_blue)+ ">"
				+ getItem(position).getFriendlyName() + "</font>"));

		viewHolder.tvVehicleMileage.setText(Helper.getFormattedDistance(getItem(position).getMileage()+"")+" Km");
		viewHolder.tvVehicleColour.setText(getItem(position).getColour());
		viewHolder.tvVehicleLocation.setText(getItem(position).getLocation());
		if(getItem(position).getTradePrice()!=0)
			viewHolder.tvAskingPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getTradePrice())+ ""));
		else
			viewHolder.tvAskingPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getPrice())+ ""));
		viewHolder.ivCar.setImageUrl(Constants.IMAGE_BASE_URL_NEW+ getItem(position).getImageId(), imageLoader);
		viewHolder.ivCar.setDefaultImageResId(R.drawable.noimage);
		viewHolder.ivCar.setErrorImageResId(R.drawable.noimage);
		return convertView;
	}
	
	public static class ViewHolder
	{
		NetworkImageView ivCar;
		TextView tvVehicleNameYear;
		TextView tvVehicleMileage;
		TextView tvVehicleColour;
		TextView tvVehicleLocation;
		TextView tvAskingPrice;
		
		public void init(View v)
		{
			 ivCar= (NetworkImageView) v.findViewById(R.id.image);
			 tvVehicleNameYear = (TextView) v.findViewById(R.id.tvVehicleNameYear);
			 tvVehicleMileage = (TextView) v.findViewById(R.id.tvVehicleMileage);
			 tvVehicleColour = (TextView) v.findViewById(R.id.tvVehicleColour);
			 tvVehicleLocation = (TextView) v.findViewById(R.id.tvLocation);
			 tvAskingPrice = (TextView) v.findViewById(R.id.tvAskingPrice);
		}
		
	}

}

