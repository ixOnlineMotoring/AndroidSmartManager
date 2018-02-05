package com.nw.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.nw.interfaces.AuditImageClickListener;
import com.nw.model.Audit;
import com.nw.model.MyImage;
import com.nw.widget.CustomTextView;
import com.nw.widget.CustomTextViewLight;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.ArrayList;

public class AuditListAdapter extends ArrayAdapter<Audit> 
	{
		int type=0,finalPosition=0;
		ArrayList<Audit> audits;
		Context mcontext;
		ArrayList<MyImage> tImageList;
		Resources resources;
		AuditImageClickListener auditImageClickListener;

		public AuditListAdapter(Context context, int resource, ArrayList<Audit> objects, int Type,AuditImageClickListener listener)
		{
			super(context, resource, objects);
			resources=getContext().getResources();
			type = Type;
			audits = objects;
			mcontext = context;
			auditImageClickListener = listener;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			finalPosition=position;
			ViewHolder holder;
			if(convertView==null)
			{
				holder=new ViewHolder();
				convertView=LayoutInflater.from(getContext()).inflate(R.layout.list_item_stock_audit, parent,false);
				holder.tvId=(TextView) convertView.findViewById(R.id.tvId);
				holder.tvVIN=(CustomTextViewLight) convertView.findViewById(R.id.tvVINDetail);
				holder.tvVehicleName =(CustomTextView) convertView.findViewById(R.id.tvVehicleName);
				holder.tvRegNo =(CustomTextView) convertView.findViewById(R.id.tvRegNumber);
				holder.tvVehicleYear =(CustomTextView) convertView.findViewById(R.id.tvVehicleYear);
				holder.tvLocation=(TextView) convertView.findViewById(R.id.tvLocation);
				holder.tvDaysRemain=(CustomTextView) convertView.findViewById(R.id.tvRemainingDays);
				holder.tvStock=(CustomTextView) convertView.findViewById(R.id.tvStock);
				holder.tvTime=(CustomTextViewLight) convertView.findViewById(R.id.tvTime);
				holder.tvDepartmentType=(CustomTextView) convertView.findViewById(R.id.tvDepartment);	
				holder.tvMileage=(CustomTextView)convertView.findViewById(R.id.tvMileage);
				holder.tvRetailPrice =(CustomTextView)convertView.findViewById(R.id.tvRetailPrice);
				holder.tvTradePrice =(CustomTextView)convertView.findViewById(R.id.tvTradePrice);
				holder.tvColor = (CustomTextView) convertView.findViewById(R.id.tvColor);
				holder.horizontalListView=(HorizontalListView) convertView.findViewById(R.id.hlvCarImages);
	//			holder.ivSingleItem = (ImageView) convertView.findViewById(R.id.)
				convertView.setTag(holder);
			}
			else
			{
				holder=(ViewHolder) convertView.getTag();
			}
			/*if (type==1) {
				holder.tvVehicleDetails.setVisibility(View.GONE);
			} else {
				holder.tvVehicleDetails.setVisibility(View.VISIBLE);
			}*/
			
			// bind data	
			holder.tvVIN.setText("VIN: "+audits.get(position).getVIN());
			holder.tvLocation.setText("GEO: "+audits.get(position).getGEO());
	
			 holder.tvColor.setText(getItem(position).getColor());
			 if (getItem(position).getRegNumber().equals("No Registration"))
				{
					 holder.tvRegNo.setText("Reg?");
				}else {
					 holder.tvRegNo.setText(getItem(position).getRegNumber());
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
			 if (Helper.getFormattedDistance(getItem(position).getMileageKM() +"").equals(""))
				{
				 holder.tvMileage.setText("Mileage?");
				} else
				{
					holder.tvMileage.setText(Helper.getFormattedDistance(getItem(position).getMileageKM() +"")+ " Km");
				}
				
			 holder.tvDaysRemain.setText(Html.fromHtml("<font color=#3476BE>"+getItem(position).getAge()+" Days"));
			 holder.tvDepartmentType.setText(getItem(position).getVehicleType());
			holder.tvVehicleName.setText(getItem(position).getMake()+" "+getItem(position).getModel());
			 
			holder.tvStock.setText("Stock#: "+audits.get(position).getStockNumber());
			holder.tvTime.setText(Helper.getTimeHoursAndMinutes(audits.get(position).getTime()));
			
			tImageList= new ArrayList<MyImage>();
			MyImage image1 = new MyImage();
			image1.setThumb(audits.get(position).getLicenseImagePath());
			image1.setFull(audits.get(position).getLicenseImagePath());
			tImageList.add(image1);
			
			MyImage image2 = new MyImage();
			image2.setThumb(audits.get(position).getVehicleImagePath());
			image2.setFull(audits.get(position).getVehicleImagePath());
			tImageList.add(image2);
			
			
			holder.adapter=new AuditImageAdapter(getContext(),tImageList);
			holder.horizontalListView.setAdapter(holder.adapter);
			holder.horizontalListView.setOnItemClickListener(new OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int pos, long arg3)
				{
					auditImageClickListener.onImageClick(pos, tImageList,position);
				}
			});
					
			return convertView;
		}
		
		public static class ViewHolder
		{
			TextView tvId,tvLocation;
			CustomTextViewLight tvTime,tvVIN; 
			CustomTextView tvDaysRemain,tvStock,tvDepartmentType,tvMileage,tvVehicleName,
							tvRetailPrice,tvTradePrice,tvRegNo,tvColor,tvVehicleYear;
			HorizontalListView horizontalListView;
			ImageView ivSingleItem;
			AuditImageAdapter adapter;
		}
	}