package com.nw.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import com.nw.model.Audit;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.List;

public class StillAuditAdapter extends ArrayAdapter<Audit>{

	Resources resources;
	public StillAuditAdapter(Context context,List<Audit> objects) 
	{
		super(context, R.layout.list_item_vehicles_details, objects);
		resources=getContext().getResources();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		 if(convertView==null)
		 	 {
			 holder= new ViewHolder();
			 convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_still_to_audit, parent, false);
			 holder.init(convertView);
			 convertView.setTag(holder);
			 }else{
				 holder= (ViewHolder) convertView.getTag();
			 }
			 holder.tvVehicleName.setText(getItem(position).getMake()+" "+getItem(position).getModel());
			 holder.tvYear.setText(getItem(position).getYear());
			 if (getItem(position).getRegNumber().equals("No Registration"))
				{
					 holder.tvRegNumber.setText("Reg?");
				}else {
					 holder.tvRegNumber.setText(getItem(position).getRegNumber());
				}
			 holder.tvRegNumber.append(" | "+getItem(position).getColor());
			 holder.tvRegNumber.append(" | "+Helper.getSubStringBeforeString(getItem(position).getStockNumber(), "<br/>"));
			 if (Helper.getFormattedDistance(getItem(position).getMileageKM()).equals(""))
			 {
				holder.tvMileage.setText("Mileage?");
			 } 
			 else
			 {
				holder.tvMileage.setText(Helper.getFormattedDistance(getItem(position).getMileageKM() +"")+ " Km");
			 }
			 
			
			 if (Helper.formatPrice(new BigDecimal(getItem(position).getTradePrice())+"").equals("R0"))
			 {
				 holder.tvTradePrice.setText("R?");
			 }
			 else {
				holder.tvTradePrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getTradePrice())+""));
				}
			 if (Helper.formatPrice(new BigDecimal(getItem(position).getRetailPrice())+"").equals("R0"))
			 {
				 holder.tvRetailPrice.setText("R?");
			 }
			 else {
				 holder.tvRetailPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getRetailPrice())+""));
				}
			 holder.tvDepartment.setText("Used");
			 if (Helper.getFormattedDistance(getItem(position).getMileageKM() +"").equals(""))
				{
				 holder.tvDepartment.append(" | Mileage?");
				} else
				{
					holder.tvDepartment.append(" | "+Helper.getFormattedDistance(getItem(position).getMileageKM() +"")+ " Km");
				}
				
			 holder.tvDepartment.append(Html.fromHtml(" | <font color=#3476BE>"+getItem(position).getAge()+" Days"));
			 holder.tvLine.setText(" |");
			 holder.tablerow_extra.setVisibility(View.GONE);
				
		return convertView;
	}

	private static class ViewHolder
	{
		TextView tvVehicleName,tvYear,tvColor,tvStock,tvMileage,tvRegNumber,tvTradePrice,tvRetailPrice,tvRemainingDays,tvDepartment,tvLine;
		TableRow tablerow_extra;
		
		public void init(View convertView)
		{
			 tvVehicleName= (TextView) convertView.findViewById(R.id.tvVehicleName);
			 tvColor= (TextView) convertView.findViewById(R.id.tvColor);
			 tvStock= (TextView) convertView.findViewById(R.id.tvStock);
			 tvMileage= (TextView) convertView.findViewById(R.id.tvMileage);
			 tvYear=(TextView) convertView.findViewById(R.id.tvYear);
			 tvRegNumber=(TextView) convertView.findViewById(R.id.tvRegNumber);
			 tvTradePrice=(TextView) convertView.findViewById(R.id.tvTradePrice);
			 tvRetailPrice=(TextView) convertView.findViewById(R.id.tvRetailPrice);
			 tvRemainingDays=(TextView) convertView.findViewById(R.id.tvRemainingDays);
			 tvDepartment=(TextView) convertView.findViewById(R.id.tvDepartment);
			 tvLine=(TextView) convertView.findViewById(R.id.tvLine);
			 tablerow_extra = (TableRow) convertView.findViewById(R.id.tablerow_extra);
		}
	}
}
