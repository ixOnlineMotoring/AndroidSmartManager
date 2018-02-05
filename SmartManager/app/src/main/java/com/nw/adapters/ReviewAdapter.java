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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nw.model.Review;
import com.nw.webservice.VolleySingleton;
import com.smartmanager.android.R;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review>{

	ImageLoader imageLoader;
	DisplayImageOptions options;
	
	public ReviewAdapter(Context context, int resource, List<Review> reviews)
	{
		super(context, resource, reviews);
		imageLoader = VolleySingleton.getInstance().getImageLoader();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder viewHolder;
		if(convertView==null)
		{
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, parent, false);
			viewHolder.init(getContext(),convertView);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.ivCar.setErrorImageResId(R.drawable.noimage);
		viewHolder.ivCar.setDefaultImageResId(R.drawable.noimage);
		if(getItem(position).getImages()!=null)
		{
			if(getItem(position).getImages()!=null && !getItem(position).getImages().isEmpty()){
				viewHolder.ivCar.setImageUrl(getItem(position).getImages().get(0).getThumb(), imageLoader);
			}else{
				viewHolder.ivCar.setImageResource(R.drawable.noimage);
			}
		}else{
			viewHolder.ivCar.setImageResource(R.drawable.noimage);
		}
	//	SmartManagerApplication.getImageLoader().displayImage(getItem(position).getImages().get(0).getLink()/*.replace("width=350", "width=200")*/, viewHolder.ivCar, options);
		viewHolder.tvVehicleName.setText(getItem(position).getTitle());
		viewHolder.tvRoadTest.setText(getItem(position).getType()+" "+ getItem(position).getDate());
		viewHolder.tvReview.setText(Html.fromHtml(getItem(position).getBody()));
		
		return convertView;
	}

	private static class ViewHolder
	{
		NetworkImageView ivCar;
		TextView  tvVehicleName;
		TextView tvRoadTest;
		TextView tvReview;

		private void init(Context mContext,View convertView)
		{
			ivCar=(NetworkImageView) convertView.findViewById(R.id.ivVehicleImage);
			tvVehicleName=(TextView) convertView.findViewById(R.id.tvVehicleName);
			tvRoadTest=(TextView) convertView.findViewById(R.id.tvRoadTest);
			tvReview=(TextView) convertView.findViewById(R.id.tvReview);
		}
	}
}
