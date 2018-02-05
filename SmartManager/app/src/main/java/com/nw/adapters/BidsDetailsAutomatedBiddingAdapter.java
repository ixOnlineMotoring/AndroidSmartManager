package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.VehicleDetails;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.List;

public class BidsDetailsAutomatedBiddingAdapter extends
		ArrayAdapter<VehicleDetails> {
	public BidsDetailsAutomatedBiddingAdapter(Context context, int resource,
			List<VehicleDetails> objects, String from) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_item_automatedbidding, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();

		viewHolder.tvVehicleNameYear.setText(Html
				.fromHtml("<font color=#ffffff>"
						+ getItem(position).getYear()
						+ "</font> <font color="
						+ getContext().getResources().getColor(
								R.color.dark_blue) + ">"
						+ getItem(position).getFriendlyName() + "</font>"));

		viewHolder.tvRegNumber.setText(getItem(position).getRegistration());
		viewHolder.tvRegNumber.append(" | " + getItem(position).getColour());
		viewHolder.tvRegNumber.append(" | " + getItem(position).getStockCode());
		viewHolder.tvDepartment.setText(getItem(position).getType());
		viewHolder.tvDepartment.append(" | "
				+ Helper.getFormattedDistance(getItem(position).getMileage()
						+ "") + " Km");
		viewHolder.tvinvitedvalue.setText("Initiated by  "
				+ getItem(position).getClientName());

		viewHolder.tvCurrent.setText(Helper.formatPrice(new BigDecimal(getItem(
				position).getHighest())
				+ ""));
		
		viewHolder.tvCurrent.append(Html.fromHtml("<font color=#ffffff> |</font>"));
		viewHolder.tvcapPrice.setText(Helper.formatPrice(new BigDecimal(
				getItem(position).getCap()) + ""));
		viewHolder.tvcapPrice.append(Html.fromHtml("<font color=#ffffff> |</font>"));
		viewHolder.tvincrementprice.setText(Helper.formatPrice(new BigDecimal(
				getItem(position).getIncrement()) + ""));
		return convertView;
	}

	public static class ViewHolder {
		TextView tvVehicleNameYear, tvRegNumber, tvColor, tvStock,
				tvDepartment, tvMileage, tvCurrent, tvcapPrice,
				tvincrementprice, tvinvitedvalue, tvIncr,tvCap;

		public void init(View v) {
			tvIncr = (TextView) v.findViewById(R.id.tvIncr);
			tvVehicleNameYear = (TextView) v.findViewById(R.id.tvVehicleName);
			tvRegNumber = (TextView) v.findViewById(R.id.tvRegNumber);
			tvColor = (TextView) v.findViewById(R.id.tvColor);
			tvStock = (TextView) v.findViewById(R.id.tvStock);
			tvDepartment = (TextView) v.findViewById(R.id.tvDepartment);
			tvMileage = (TextView) v.findViewById(R.id.tvMileage);
			tvCurrent = (TextView) v.findViewById(R.id.tvCurrent);
			tvcapPrice = (TextView) v.findViewById(R.id.tvcapPrice);
			tvincrementprice = (TextView) v.findViewById(R.id.tvincrementprice);
			tvinvitedvalue = (TextView) v.findViewById(R.id.tvinvitedvalue);
			tvCap=(TextView) v.findViewById(R.id.tvCap);
		}

	}

}
