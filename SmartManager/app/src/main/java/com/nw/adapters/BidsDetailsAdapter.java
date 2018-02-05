package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import com.nw.model.VehicleDetails;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.List;

public class BidsDetailsAdapter extends ArrayAdapter<VehicleDetails>
{
	int type;
	Context context;

	public BidsDetailsAdapter(Context context, int resource, List<VehicleDetails> objects, String from, int type)
	{
		super(context, resource, objects);
		this.type = type;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_bids_details, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolder) convertView.getTag();

		viewHolder.tvVehicleNameYear.setText(Html.fromHtml("<font color=#ffffff>" + getItem(position).getYear() + "</font> <font color=" + getContext().getResources().getColor(R.color.dark_blue)
				+ ">" + getItem(position).getFriendlyName() + "</font>"));

		//viewHolder.tvMileage.setText(Helper.getFormattedDistance(getItem(position).getMileage() + "") + " Km");
	
		
		if (getItem(position).getRegistration().equalsIgnoreCase(""))
		{
			viewHolder.tvRegNumber.setText("reg ?");
		}
		else
		{
			viewHolder.tvRegNumber.setText(getItem(position).getRegistration());
		}
		viewHolder.tvRegNumber.append(" | "+getItem(position).getColour());
		viewHolder.tvRegNumber.append(" | "+getItem(position).getStockCode());
		viewHolder.tvtype.setText(getItem(position).getType()+" | "+Helper.getFormattedDistance(getItem(position).getMileage() + "") + " Km");

		setVisibilityViews(viewHolder, position);

		return convertView;
	}

	private void setVisibilityViews(ViewHolder viewHolder, int position)
	{
		switch (type)
		{
			case 0: // Losing Bids
				viewHolder.tablerowsoldsection.setVisibility(View.GONE);
				viewHolder.tvbidestatus.setText(" Beaten");
				viewHolder.tvbidestatus.setTextAppearance(context, R.style.SingleLineRedText);
				viewHolder.tablerowsellersection.setVisibility(View.GONE);
				viewHolder.tvWinningbidPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getHighest()) + ""));
				viewHolder.tvMybids.setText(Helper.formatPrice(new BigDecimal(getItem(position).getOfferAmt()) + ""));
				viewHolder.viewdate.setVisibility(View.GONE);
				break;
			case 1: // Winning Bids
				viewHolder.tablerowsoldsection.setVisibility(View.GONE);
				viewHolder.tvbidestatus.setText(" Winning");
				viewHolder.tvbidestatus.setTextAppearance(context, R.style.SingleLineGreenText);
				viewHolder.tablerowsellersection.setVisibility(View.GONE);
				viewHolder.viewdate.setVisibility(View.GONE);
				viewHolder.tvWinningbidPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getHighest()) + ""));
				viewHolder.tvMybids.setText(Helper.formatPrice(new BigDecimal(getItem(position).getOfferAmt()) + ""));
				break;
			/*
			 * case 2:
			 * 
			 * break;
			 */
			case 3: // Won
				viewHolder.tablerowwinningbids.setVisibility(View.GONE);
				viewHolder.tvsellerName.setText(getItem(position).getClientName());
				viewHolder.tvsellerPhno.setText(getItem(position).getClientPnno());
			//	viewHolder.tvRemainingDays.setText(getItem(position).getsolddate());
				viewHolder.tvsoldprice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getOfferAmt()) + ""));
				viewHolder.tvsoldstatus.setText(" Won");
				viewHolder.viewdate.setVisibility(View.GONE);
				viewHolder.tvsoldstatus.setTextAppearance(context, R.style.SingleLineGreenText);
				break;
			case 4: // Lost
				viewHolder.view_seller.setVisibility(View.GONE);
				viewHolder.tablerowsoldsection.setVisibility(View.GONE);
				viewHolder.tvsellerName.setText(getItem(position).getClientName());
				viewHolder.tvbidestatus.setText(" Lost");
				viewHolder.tvbidestatus.setTextAppearance(context, R.style.SingleLineRedText);
			//	viewHolder.tablerowsellersection.setVisibility(View.GONE);
				viewHolder.viewdate.setVisibility(View.GONE);
				viewHolder.tvWinningbidPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getHighest()) + ""));
				viewHolder.tvtitleMessage.setText(" Sold for");
				viewHolder.tvMybids.setText(Helper.formatPrice(new BigDecimal(getItem(position).getOfferAmt()) + ""));
				break;
			case 5: // Private Offers

				break;
			case 6: // Withdrawn
				viewHolder.tablerowsoldsection.setVisibility(View.GONE);
				viewHolder.tvbidestatus.setText(" Withdrawn");
				viewHolder.tvbidestatus.setTextAppearance(context, R.style.RedText);
				viewHolder.tablerowsellersection.setVisibility(View.GONE);
				viewHolder.tvWinningbidPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getHighest()) + ""));
				// viewHolder.tvMybids.setText(Helper.formatPrice(new
				// BigDecimal(getItem(position).getOfferAmt()) + ""));
				viewHolder.tvMybids.setVisibility(View.GONE);
				viewHolder.viewdate.setVisibility(View.GONE);
				break;
			case 7: // Cancle
				viewHolder.tablerowsoldsection.setVisibility(View.GONE);
				viewHolder.tvbidestatus.setText(" Cancelled");
				viewHolder.tvbidestatus.setTextAppearance(context, R.style.RedText);
				viewHolder.tablerowsellersection.setVisibility(View.GONE);
				viewHolder.tvWinningbidPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getHighest()) + ""));
				// viewHolder.tvMybids.setText(Helper.formatPrice(new
				// BigDecimal(getItem(position).getOfferAmt()) + ""));
				viewHolder.tvMybids.setVisibility(View.GONE);
				viewHolder.viewdate.setVisibility(View.GONE);
				break;

		}

	}

	public static class ViewHolder
	{
		TextView tvVehicleNameYear, tvVehicleColour, tvStockcode, tvtype, tvMileage, tvRemainingDays, tvWinningbidPrice, tvMybids, tvbidestatus, tvRegNumber, tvsellerName, tvsellerPhno, tvsoldprice,
				tvsoldstatus,tvtitleMessage;
		TableRow tablerowsellersection, tablerowwinningbids, tablerowsoldsection;
		View viewdate,view_seller;

		public void init(View v)
		{
			tvVehicleNameYear = (TextView) v.findViewById(R.id.tvVehicleName);
			tvVehicleColour = (TextView) v.findViewById(R.id.tvColor);
			tvStockcode = (TextView) v.findViewById(R.id.tvStockcode);
			tvtype = (TextView) v.findViewById(R.id.tvtype);
			tvMileage = (TextView) v.findViewById(R.id.tvMileage);
			tvRemainingDays = (TextView) v.findViewById(R.id.tvRemainingDays);
			tvWinningbidPrice = (TextView) v.findViewById(R.id.tvWinningbidPrice);
			tvMybids = (TextView) v.findViewById(R.id.tvMybids);
			tvbidestatus = (TextView) v.findViewById(R.id.tvbidestatus);
			tvRegNumber = (TextView) v.findViewById(R.id.tvRegNumber);
			tvsellerName = (TextView) v.findViewById(R.id.tvsellerName);
			tvsellerPhno = (TextView) v.findViewById(R.id.tvsellerPhno);
			tvsoldprice = (TextView) v.findViewById(R.id.tvsoldprice);
			tvsoldstatus = (TextView) v.findViewById(R.id.tvsoldstatus);
			tvtitleMessage = (TextView) v.findViewById(R.id.tvtitleMessage);

			tablerowsellersection = (TableRow) v.findViewById(R.id.tablerowsellersection);
			tablerowwinningbids = (TableRow) v.findViewById(R.id.tablerowwinningbids);
			tablerowsoldsection = (TableRow) v.findViewById(R.id.tablerowsoldsection);

			viewdate = (View) v.findViewById(R.id.viewdate);
			view_seller = (View) v.findViewById(R.id.view_seller);
		}
	}
}
