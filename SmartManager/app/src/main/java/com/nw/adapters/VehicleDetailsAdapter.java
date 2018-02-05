package com.nw.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.Vehicle;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.List;

public class VehicleDetailsAdapter extends ArrayAdapter<Vehicle>{

	Resources resources;
	int operationType;
	int FOR_MY_STOCK_USED_NEW = 2, FOR_GROUP_STOCK_USED_NEW = 4;

	public VehicleDetailsAdapter(Context context,List<Vehicle> objects,int operationType)
	{
		super(context, R.layout.list_item_vehicles_details, objects);
		resources=getContext().getResources();
		this.operationType = operationType;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		 if(convertView==null)
		 	 {
			 holder= new ViewHolder();
			 convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_vehicles_details, parent, false);
			 holder.init(convertView);
			 convertView.setTag(holder);
			 }else{
				 holder= (ViewHolder) convertView.getTag();
			 }

		if (operationType == FOR_MY_STOCK_USED_NEW || operationType == FOR_GROUP_STOCK_USED_NEW){
			//Year
			holder.tvVehicleName.setText(Html.fromHtml(getItem(position).getFriendlyName()));
			//Reg
			holder.tvRegNo.setText("");
			holder.tvRegNo.append(getItem(position).getColour());
			holder.tvRegNo.append(" | "+Helper.getSubStringBeforeString(getItem(position).getStockNumber(), "<br/>"));
			//Trade
			holder.tvTradePrice.setVisibility(View.GONE);
			holder.tvTrd.setVisibility(View.GONE);
			holder.tvLine.setVisibility(View.GONE);
		}
		else {
			//Year
			holder.tvVehicleName.setText(Html.fromHtml("<font color=#ffffff>"+getItem(position).getYear()+"</font> "+getItem(position).getFriendlyName()));

			//Reg
			if (getItem(position).getRegNumber().equals("No Registration"))
			{
				holder.tvRegNo.setText("Reg?");
			}else {
				holder.tvRegNo.setText(Helper.getSubStringBeforeString(getItem(position).getRegNumber(), "<br/>"));
			}
			holder.tvRegNo.append(" | "+getItem(position).getColour());
			holder.tvRegNo.append(" | "+Helper.getSubStringBeforeString(getItem(position).getStockNumber(), "<br/>"));

			//Trade
			holder.tvTrd.setVisibility(View.VISIBLE);
			if (Helper.formatPrice(new BigDecimal(getItem(position).getTradePrice())+"").equals("R0"))
			{
				holder.tvTradePrice.setText("R?");
			}
			else {
				holder.tvTradePrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getTradePrice())+""));
			}
			holder.tvLine.setVisibility(View.VISIBLE);
			holder.tvLine.setText(" | ");
		}


			 if(getItem(position).getInternalNote()!=null)
			 {
				 if(getItem(position).getInternalNote().trim().equalsIgnoreCase("")||getItem(position).getInternalNote().trim().equalsIgnoreCase("anyType{}"))
				 {
					 holder.tvInternalNote.setVisibility(View.GONE);
				 }else
				 {
					 holder.tvInternalNote.setVisibility(View.VISIBLE);
					 holder.tvInternalNote.setText("Notes: "+getItem(position).getInternalNote());
				 }
			}else
			 {
				 holder.tvInternalNote.setVisibility(View.GONE);
			 }




			 if (Helper.formatPrice(new BigDecimal(getItem(position).getRetailPrice())+"").equals("R0"))
			 {
				 holder.tvRetailPrice.setText("R?");
			 }
			 else {
				 holder.tvRetailPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getRetailPrice())+""));
				}
			 holder.tvDepartmentType.setText(getItem(position).getDepartment());
			 if (Helper.getFormattedDistance(getItem(position).getMileage() +"").equals("0"))
				{
				 holder.tvDepartmentType.append(" | Mileage?");
				} else
				{
					holder.tvDepartmentType.append(" | " +Helper.getFormattedDistance(getItem(position).getMileage() +"")+ " Km");
				}
				
			 holder.tvDepartmentType.append(Html.fromHtml(" | <font color=#3476BE>"+getItem(position).getExpires()+" Days</font>"));
			
			 if(getItem(position).getExtras().equals("True")){
				 holder.tvExtras.setText(Html.fromHtml("<font color=#ffffff>Extras</font> <font color="+resources.getColor(R.color.green)+">\u2713</font>"));
			 }else{
				 holder.tvExtras.setText(Html.fromHtml("<font color=#ffffff>Extras</font> <font color="+resources.getColor(R.color.red)+">\u2718</font>"));
				 }
			 if(getItem(position).getComments().equals("True"))
				 holder.tvExtras.append(Html.fromHtml("<font color=#ffffff> &emsp; &emsp; | Comments</font> <font color="+resources.getColor(R.color.green)+">\u2713</font>"));
			 else{
				 holder.tvExtras.append(Html.fromHtml("<font color=#ffffff> &emsp; &emsp; | Comments</font> <font color="+resources.getColor(R.color.red)+">\u2718</font>"));
				 }
			 holder.tvExtras.append(Html.fromHtml("<font color=#ffffff> &emsp; &emsp; | Photos </font> <font color="+resources.getColor(R.color.dark_blue)+">"+getItem(position).getNumOfPhotos()+"</font>"));
			 holder.tvExtras.append(Html.fromHtml("<font color=#ffffff> &emsp; &emsp; | Videos </font> <font color="+resources.getColor(R.color.dark_blue)+">"+getItem(position).getNumOfVideos()+"</font>"));
		return convertView;
	}

	private static class ViewHolder
	{
		TextView tvVehicleName,tvLine;
		TextView tvRegNo, tvColor, tvDaysRemain;
		TextView tvStock, tvRetailPrice,tvTradePrice, tvMileage,tvDepartmentType,tvTrd;
		TextView tvExtras, tvComments, tvPhotos, tvVideos,tvInternalNote;
		
		public void init(View convertView)
		{
			 tvVehicleName= (TextView) convertView.findViewById(R.id.tvVehicleName);
			 tvRegNo= (TextView) convertView.findViewById(R.id.tvRegNumber);
			 tvColor= (TextView) convertView.findViewById(R.id.tvColor);
			 tvStock= (TextView) convertView.findViewById(R.id.tvStock);
			 tvRetailPrice= (TextView) convertView.findViewById(R.id.tvRetailPrice);
			 tvTradePrice= (TextView) convertView.findViewById(R.id.tvTradePrice);
			 tvDepartmentType= (TextView) convertView.findViewById(R.id.tvDepartment);
			 tvMileage= (TextView) convertView.findViewById(R.id.tvMileage);
			 tvDaysRemain= (TextView) convertView.findViewById(R.id.tvRemainingDays);
			 tvExtras= (TextView) convertView.findViewById(R.id.tvExtras);
			 tvComments= (TextView) convertView.findViewById(R.id.tvComments);
			 tvPhotos= (TextView) convertView.findViewById(R.id.tvPhotos);
			 tvVideos= (TextView) convertView.findViewById(R.id.tvVideos);
			 tvLine=(TextView) convertView.findViewById(R.id.tvLine);
			 tvInternalNote=(TextView) convertView.findViewById(R.id.tvInternalNote);
			 tvTrd = (TextView)convertView.findViewById(R.id.tvTrd);
		}
	}
}
