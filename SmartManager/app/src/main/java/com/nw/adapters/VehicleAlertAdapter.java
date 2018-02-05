package com.nw.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nw.interfaces.ListClickListener;
import com.nw.model.Vehicle;
import com.nw.widget.CustomButton;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.List;

public class VehicleAlertAdapter extends ArrayAdapter<Vehicle>
{
	int type = 0;
	Resources resources;
	ListClickListener listener;

	public VehicleAlertAdapter(Context context, List<Vehicle> objects, int type, ListClickListener listClickListener)
	{
		super(context, R.layout.list_item_vehicle_alert, objects);
		resources = getContext().getResources();
		this.type = type;
		this.listener = listClickListener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		final ViewHolder holder;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_vehicle_alert, parent, false);
			holder.init(convertView);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.btnActiveTrade.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (TextUtils.isEmpty(holder.edTradePrice.getText().toString().trim()))
				{
					Helper.showToast("Please enter the trade price", getContext());
					return;
				}
				listener.onActivateClick(position, holder.cbIsTrade.isChecked(), Integer.parseInt(holder.edTradePrice.getText().toString().trim()));
			}
		});
		holder.btnEdit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				listener.onEditClick(position);
			}
		});
		holder.tvVehicleName.setText(""+getItem(position).getFriendlyName());
		holder.tvYear.setText(""+getItem(position).getYear());
		if (getItem(position).getRegNumber().equals("No Registration"))
		{
			holder.tvRegNo.setText("Reg?");
		}
		else
		{
			holder.tvRegNo.setText(Helper.getSubStringBeforeString(getItem(position).getRegNumber(), "<br/>"));
		}
		holder.tvRegNo.append(" | "+getItem(position).getColour());
		holder.tvRegNo.append(" | "+Helper.getSubStringBeforeString(getItem(position).getStockNumber(), "<br/>"));
		if (Helper.formatPrice(new BigDecimal(getItem(position).getTradePrice()) + "").equals("R0"))
		{
			holder.tvTradePrice.setText("R?");
		}
		else
		{
			holder.tvTradePrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getTradePrice()) + ""));
		}
		if (Helper.formatPrice(new BigDecimal(getItem(position).getRetailPrice()) + "").equals("R0"))
		{
			holder.tvRetailPrice.setText("R?");
		}
		else
		{
			holder.tvRetailPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getRetailPrice()) + ""));
		}
		
		if (type == 0)
		{
			holder.viewRemainingDays.setVisibility(View.INVISIBLE);
			holder.tvDaysRemain.setVisibility(View.INVISIBLE);
			holder.llTradePrice.setVisibility(View.VISIBLE);
			holder.btnActiveTrade.setVisibility(View.VISIBLE);
			holder.btnEdit.setVisibility(View.VISIBLE);
		}
		else if (type == 1)
		{
			holder.llTradePrice.setVisibility(View.VISIBLE);
			holder.btnActiveTrade.setVisibility(View.VISIBLE);
			holder.btnEdit.setVisibility(View.VISIBLE);
		//	holder.llayoutAleart.setVisibility(View.GONE);
		}
		else if (type == 2)
		{
			holder.viewRemainingDays.setVisibility(View.INVISIBLE);
			holder.tvDaysRemain.setVisibility(View.INVISIBLE);
			holder.llTradePrice.setVisibility(View.GONE);
			holder.btnActiveTrade.setVisibility(View.GONE);
			holder.btnEdit.setVisibility(View.VISIBLE);
			
		}
		holder.tvDepartmentType.setText(getItem(position).getDepartment());
		if (Helper.getFormattedDistance(getItem(position).getMileage() + "").equals("0"))
		{
			holder.tvDepartmentType.append(" | Mileage?");
		}
		else
		{
			holder.tvDepartmentType.append(" | "+Helper.getFormattedDistance(getItem(position).getMileage() + "") + " Km");
		}
		if (getItem(position).getExpires() != null)
		{
			holder.tvDepartmentType.append(Html.fromHtml(" | <font color=#3476BE>"+getItem(position).getExpires() + " Days"));
		}
		else
		{
			holder.tvDepartmentType.append(" | Days?");
		}

		
		if (!getItem(position).getExtras().equals(""))
		{
			holder.tvExtras.setText(Html.fromHtml("<font color=#ffffff>Extras</font> <font color=" + resources.getColor(R.color.green) + ">\u2713</font>"));
		}
		else
			holder.tvExtras.setText(Html.fromHtml("<font color=#ffffff>Extras</font> <font color=" + resources.getColor(R.color.red) + ">\u2718</font>"));
		if (!getItem(position).getComments().equals(""))
			holder.tvComments.setText(Html.fromHtml("<font color=#ffffff>Comments</font> <font color=" + resources.getColor(R.color.green) + ">\u2713</font>"));
		else
			holder.tvComments.setText(Html.fromHtml("<font color=#ffffff>Comments</font> <font color=" + resources.getColor(R.color.red) + ">\u2718</font>"));
		holder.tvPhotos.setText(Html.fromHtml("<font color=#ffffff>Photos </font> <font color=" + resources.getColor(R.color.dark_blue) + ">" + getItem(position).getNumOfPhotos() + "</font>"));
		holder.tvVideos.setText(Html.fromHtml("<font color=#ffffff>Videos </font> <font color=" + resources.getColor(R.color.dark_blue) + ">" + getItem(position).getNumOfVideos() + "</font>"));
		return convertView;
	}

	private static class ViewHolder
	{
		TextView tvVehicleName,tvYear;
		TextView tvRegNo, tvColor, tvDaysRemain;
		TextView tvStock, tvRetailPrice, tvTradePrice, tvMileage, tvDepartmentType;
		TextView tvExtras, tvComments, tvPhotos, tvVideos;
		LinearLayout llTradePrice;
		CustomButton btnActiveTrade, btnEdit;
		CheckBox cbIsTrade;
		EditText edTradePrice;
		View viewRemainingDays;

		public void init(View convertView)
		{
			
			tvVehicleName = (TextView) convertView.findViewById(R.id.tvVehicleName);
			tvYear = (TextView) convertView.findViewById(R.id.tvYear);
			tvRegNo = (TextView) convertView.findViewById(R.id.tvRegNumber);
			tvColor = (TextView) convertView.findViewById(R.id.tvColor);
			tvStock = (TextView) convertView.findViewById(R.id.tvStock);
			tvRetailPrice = (TextView) convertView.findViewById(R.id.tvRetailPrice);
			llTradePrice = (LinearLayout) convertView.findViewById(R.id.llTradePrice);
	//		llayoutAleart = (LinearLayout) convertView.findViewById(R.id.llayoutAleart);
			tvTradePrice = (TextView) convertView.findViewById(R.id.tvTradePrice);
			tvDepartmentType = (TextView) convertView.findViewById(R.id.tvDepartment);
			tvMileage = (TextView) convertView.findViewById(R.id.tvMileage);
			tvDaysRemain = (TextView) convertView.findViewById(R.id.tvRemainingDays);
			tvExtras = (TextView) convertView.findViewById(R.id.tvExtras);
			tvComments = (TextView) convertView.findViewById(R.id.tvComments);
			tvPhotos = (TextView) convertView.findViewById(R.id.tvPhotos);
			tvVideos = (TextView) convertView.findViewById(R.id.tvVideos);
			btnActiveTrade = (CustomButton) convertView.findViewById(R.id.btnActiveTrade);
			btnEdit = (CustomButton) convertView.findViewById(R.id.btnEdit);
			cbIsTrade = (CheckBox) convertView.findViewById(R.id.cbIsTrade);
			edTradePrice = (EditText) convertView.findViewById(R.id.edTradePrice);
			
			viewRemainingDays = (View)convertView.findViewById(R.id.viewRemainingDays);
		}
	}

}
