package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nw.model.Vehicle;
import com.nw.webservice.VolleySingleton;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.List;

public class CustomBuyListAdapter extends ArrayAdapter<Vehicle>{

	ImageLoader imageLoader;
	public CustomBuyListAdapter(Context context, int resource, List<Vehicle> objects)
	{
		super(context, resource, objects);
		imageLoader = VolleySingleton.getInstance().getImageLoader();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder viewHolder;
		if(convertView==null)
		{
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(getContext()).inflate(R.layout.item_buy_list, parent, false);
			viewHolder.init(getContext(),convertView);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.ivCar.setErrorImageResId(R.drawable.noimage);
		viewHolder.ivCar.setDefaultImageResId(R.drawable.noimage);
		if(getItem(position).getImageList()!=null)
		{

			if(getItem(position).getImageList()!=null && !getItem(position).getImageList().isEmpty()){
				viewHolder.ivCar.setImageUrl(getItem(position).getImageList().get(0).getThumb()+"width=200", imageLoader);
			}else{
				viewHolder.ivCar.setImageResource(R.drawable.noimage);
			}
		}else{
			viewHolder.ivCar.setImageResource(R.drawable.noimage);
		}
		viewHolder.tvDistance.setText(Helper.getFormattedDistance(getItem(position).getMileage()+"")+"Km");
		viewHolder.tvDistance.append(Html.fromHtml("<font color=#ffffff> |</font>"+getItem(position).getColour()));
		viewHolder.tvLocation.setText(getItem(position).getLocation());
		viewHolder.tvLocation.append(Html.fromHtml("<font color=#ffffff> |</font><font color=#D42E30>Exp. "+Helper.convertDateToNormal(getItem(position).getExpires())+"</font>"));
		viewHolder.tvTitle.setText(Html.fromHtml("<font color=#ffffff>"+getItem(position).getYear()+
				"</font> <font color="+getContext().getResources().getColor(R.color.dark_blue)+">"+
				getItem(position).getFriendlyName()+"</font>"));

		/*if(getItem(position).getHightestBid()>getItem(position).getRetailPrice())
			viewHolder.tvPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getHightestBid())+""));
		else
			viewHolder.tvPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getRetailPrice())+""));*/
		if (getItem(position).getMinBid()==0)
		{
			viewHolder.tvPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getTradePrice())+""));
		}else {
			viewHolder.tvPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getMinBid())+""));
		}

		if(getItem(position).getMyHighestBid()==0){
			//viewHolder.tvMyBid.setText("My Bid: N/A");
			viewHolder.tvMyBid.setText("My Bid: None Yet");
			viewHolder.tvMyBid.setTextColor(getContext().getResources().getColor(R.color.white));
			viewHolder.tvMyBidStatus.setVisibility(View.INVISIBLE);
			viewHolder.tvPrice.setVisibility(View.VISIBLE);
			viewHolder.tvTitleMinBids.setVisibility(View.VISIBLE);
		}
		else if(getItem(position).getMyHighestBid()>=getItem(position).getHightestBid()){
			viewHolder.tvMyBidStatus.setVisibility(View.VISIBLE);
			viewHolder.tvMyBid.setText("My Bid: "+Helper.formatPrice(getItem(position).getMyHighestBid()+""));
			viewHolder.tvMyBidStatus.setText("Winning");
			viewHolder.tvMyBid.setTextColor(getContext().getResources().getColor(R.color.violet));
			viewHolder.tvMyBidStatus.setTextColor(getContext().getResources().getColor(R.color.green));
			viewHolder.tvPrice.setVisibility(View.INVISIBLE);
			viewHolder.tvTitleMinBids.setVisibility(View.INVISIBLE);
			
		}else{
			viewHolder.tvMyBidStatus.setVisibility(View.VISIBLE);
			viewHolder.tvMyBid.setText("My Bid: "+Helper.formatPrice(getItem(position).getMyHighestBid()+""));
			viewHolder.tvMyBid.setTextColor(getContext().getResources().getColor(R.color.violet));
			viewHolder.tvMyBidStatus.setText("Beaten");
			viewHolder.tvMyBidStatus.setTextColor(getContext().getResources().getColor(R.color.red));
		}

		if((int) getItem(position).getBuyNow()>0){
			viewHolder.ivBuyNowFlag.setVisibility(View.VISIBLE);
		}else{
			viewHolder.ivBuyNowFlag.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	private static class ViewHolder
	{
		NetworkImageView ivCar;
		ImageView ivBuyNowFlag;
		TextView  tvTitle;
		TextView tvDistance;
		TextView tvColor;
		TextView tvLocation;
		TextView tvPrice;
		TextView tvTimeLeft;
		TextView tvMyBid;
		TextView tvMyBidStatus;
		TextView tvTitleMinBids;

		private void init(Context mContext,View convertView)
		{
			ivCar=(NetworkImageView) convertView.findViewById(R.id.ivItemBuyList);
			ivBuyNowFlag=(ImageView) convertView.findViewById(R.id.ivBuyNowFlag);
			tvTitle=(TextView) convertView.findViewById(R.id.tvTitleItemBuyList);
			tvTitle.setTextColor(mContext.getResources().getColor(R.color.dark_blue));
			tvDistance=(TextView) convertView.findViewById(R.id.tvDistanceItemBuyList);
			tvColor=(TextView) convertView.findViewById(R.id.tvColorItemBuyList);
			tvLocation=(TextView) convertView.findViewById(R.id.tvLocationItemBuyList);
			tvPrice=(TextView) convertView.findViewById(R.id.tvPriceItemBuyList);
		//	tvPrice.setTextColor(mContext.getResources().getColor(R.color.green));
			tvTimeLeft=(TextView) convertView.findViewById(R.id.tvTimeLeftItemBuyList);
			tvTimeLeft.setTextColor(mContext.getResources().getColor(R.color.red));
			tvMyBid= (TextView) convertView.findViewById(R.id.tvMyBidItemBuyList);
			tvMyBidStatus= (TextView) convertView.findViewById(R.id.tvBidStatusItemBuyList);
			tvTitleMinBids= (TextView) convertView.findViewById(R.id.tvTitleMinBids);
		}
	}
}
