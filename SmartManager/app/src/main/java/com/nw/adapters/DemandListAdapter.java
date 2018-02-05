package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.Demand;
import com.smartmanager.android.R;

import java.util.List;

public class DemandListAdapter extends ArrayAdapter<Demand>
{
	public DemandListAdapter(Context context, int resource, List<Demand> objects)
	{
		super(context, resource, objects);
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_demand_list, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.init(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		setData(position, viewHolder);

		return convertView;
	}

	public static class ViewHolder
	{
		// Client TextViews
		TextView tvClientName, tvClientNameVariant, tvClientNameVariantLeadRank, tvClientNameVariantSalesRank, tvClientNameModel, tvClientNameModeLeadRank, tvClientNameModelSalesRank;

		public void init(View view)
		{
			// Client TextView
			tvClientName = (TextView) view.findViewById(R.id.tvClientName);
			tvClientNameVariant = (TextView) view.findViewById(R.id.tvClientNameVariant);
			tvClientNameVariantLeadRank = (TextView) view.findViewById(R.id.tvClientNameVariantLeadRank);
			tvClientNameVariantSalesRank = (TextView) view.findViewById(R.id.tvClientNameVariantSalesRank);
			tvClientNameModel = (TextView) view.findViewById(R.id.tvClientNameModel);
			tvClientNameModeLeadRank = (TextView) view.findViewById(R.id.tvClientNameModeLeadRank);
			tvClientNameModelSalesRank = (TextView) view.findViewById(R.id.tvClientNameModelSalesRank);
		}
	}

	private void setData(int position, ViewHolder viewHolder)
	{
		switch (position)
		{
			case 0:
				// Client TextViews
				viewHolder.tvClientName.setText(getItem(0).getClientName());
				viewHolder.tvClientNameVariant.setText(getItem(0).getVariantName() + "(by variant)");
				viewHolder.tvClientNameVariantLeadRank.setText(getItem(0).getClientVariantLeadCount() + "/" + getItem(0).getClientVariantLeadCountRanking());
				viewHolder.tvClientNameVariantSalesRank.setText(getItem(0).getClientVariantSoldLeadCount() + "/" + getItem(0).getClientVariantSoldLeadCountRanking());
				viewHolder.tvClientNameModel.setText(getItem(0).getModelName() + "(by model)");
				viewHolder.tvClientNameModeLeadRank.setText(getItem(0).getClientModelLeadCount() + "/" + getItem(0).getClientModelLeadCountRanking());
				viewHolder.tvClientNameModelSalesRank.setText(getItem(0).getClientModelSoldLeadCount() + "/" + getItem(0).getClientModelSoldLeadCountRanking());
				break;
			case 1:
				// Client City TextViews
				viewHolder.tvClientName.setText(getItem(0).getCityName());
				viewHolder.tvClientNameVariant.setText(getItem(0).getVariantName() + "(by variant)");
				viewHolder.tvClientNameVariantLeadRank.setText(getItem(0).getCityVariantLeadCount() + "/" + getItem(0).getCityVariantLeadCountRanking());
				viewHolder.tvClientNameVariantSalesRank.setText(getItem(0).getCityVariantSoldLeadCount() + "/" + getItem(0).getCityVariantSoldLeadCountRanking());
				viewHolder.tvClientNameModel.setText(getItem(0).getModelName() + "(by model)");
				viewHolder.tvClientNameModeLeadRank.setText(getItem(0).getCityModelLeadCount() + "/" + getItem(0).getCityModelLeadCountRanking());
				viewHolder.tvClientNameModelSalesRank.setText(getItem(0).getCityModelSoldLeadCount() + "/" + getItem(0).getCityModelSoldLeadCountRanking());
				break;
			case 2:
				// Client province TextView
				viewHolder.tvClientName.setText(getItem(0).getProvinceName());
				viewHolder.tvClientNameVariant.setText(getItem(0).getVariantName() + "(by variant)");
				viewHolder.tvClientNameVariantLeadRank.setText(getItem(0).getProvinceVariantLeadCount() + "/" + getItem(0).getProvinceVariantLeadCountRanking());
				viewHolder.tvClientNameVariantSalesRank.setText(getItem(0).getProvinceVariantSoldLeadCount() + "/" + getItem(0).getProvinceVariantSoldLeadCountRanking());
				viewHolder.tvClientNameModel.setText(getItem(0).getModelName() + "(by model)");
				viewHolder.tvClientNameModeLeadRank.setText(getItem(0).getProvinceModelLeadCount() + "/" + getItem(0).getProvinceModelLeadCountRanking());
				viewHolder.tvClientNameModelSalesRank.setText(getItem(0).getProvinceModelSoldLeadCount() + "/" + getItem(0).getProvinceModelSoldLeadCountRanking());
				break;
			case 3:
				// Client Nation TextView
				// tv_client_national
				viewHolder.tvClientName.setText("National");
				viewHolder.tvClientNameVariant.setText(getItem(0).getVariantName() + "(by variant)");
				viewHolder.tvClientNameVariantLeadRank.setText(getItem(0).getNationalVariantLeadCount() + "/" + getItem(0).getNationalVariantLeadCountRanking());
				viewHolder.tvClientNameVariantSalesRank.setText(getItem(0).getNationalVariantSoldLeadCount() + "/" + getItem(0).getNationalVariantSoldLeadCountRanking());
				viewHolder.tvClientNameModel.setText(getItem(0).getModelName() + "(by model)");
				viewHolder.tvClientNameModeLeadRank.setText(getItem(0).getNationalModelLeadCount() + "/" + getItem(0).getNationalModelLeadCountRanking());
				viewHolder.tvClientNameModelSalesRank.setText(getItem(0).getNationalModelSoldLeadCount() + "/" + getItem(0).getNationalModelSoldLeadCountRanking());
				break;
			default:
				break;
		}

	}

}
