package com.nw.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joanzapata.android.iconify.Iconify.IconValue;
import com.nw.interfaces.WantedSeachDeleteListener;
import com.nw.model.Wanted;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.util.List;

public class WantedAdapter extends ArrayAdapter<Wanted>{

	WantedSeachDeleteListener mListener;
	public WantedAdapter(Context context, int resource,List<Wanted> objects, WantedSeachDeleteListener listener) {
		super(context, resource,objects);
		mListener=listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_wanted_new, parent,false);
			holder= new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.tvVehicleNameYear.setText(Helper.getFormattedYearRange(getItem(position).getYearRange())+" "+ getItem(position).getFriendlyName());
		if(getItem(position).getProviences().equals("-1")){
			holder.tvRegion.setText("Region/s: All");
		}else{
			holder.tvRegion.setText("Region/s: "+getItem(position).getProviences());
		}
		
		if(getItem(position).getSearchCount()==-1){
			if(Helper.isTablet(getContext()))
				holder.tvSearch.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(getContext(),IconValue.fa_search,40),null, null, null);
			else
				holder.tvSearch.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(getContext(),IconValue.fa_search,30),null, null, null);
		}else{
			
			SpannableString content;
			content = new SpannableString(getItem(position).getSearchCount()+"");
			content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			holder.tvSearch.setText(content);
			holder.tvSearch.setCompoundDrawablesWithIntrinsicBounds(null,null, null, null);
		}
		holder.tvSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onSearch(getItem(position).getWantedId(), position);
			}
		});
		
		holder.tvDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onDelete(getItem(position).getWantedId(),position);
			}
		});
		return convertView;
	}

	private static class ViewHolder{
		TextView tvVehicleNameYear, tvRegion, tvSearch, tvDelete;
		
		public void init(View convertView){
			tvVehicleNameYear= (TextView) convertView.findViewById(R.id.tvYearVehicleName);
			tvRegion= (TextView) convertView.findViewById(R.id.tvRegion);
			tvSearch= (TextView) convertView.findViewById(R.id.tvSearch);
			tvDelete= (TextView) convertView.findViewById(R.id.tvDelete);
		}
	}
	
	
}
