package com.nw.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.nw.model.Leads;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.util.List;

public class LeadsAdapter extends ArrayAdapter<Leads> implements PinnedSectionListAdapter{

	String data="";
	public LeadsAdapter(Context context, int resource, List<Leads> objects) {
		super(context, resource, objects);
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType==Leads.SECTION;
	}

	 @Override public int getItemViewType(int position) {
         return getItem(position).getType();
     }

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_leadslist, parent, false);
			holder= new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		}else{
			holder= (ViewHolder) convertView.getTag();
		}
		/*if(getItem(position).getType()==Leads.SECTION)
		{
			holder.llChild.setVisibility(View.GONE);
			holder.tvHeader.setVisibility(View.VISIBLE);
			
			holder.tvHeader.setText(getItem(position).getHeaderTitle());
		}
		else
		{*/
			//holder.llChild.setVisibility(View.VISIBLE);
			//holder.tvHeader.setVisibility(View.GONE);
			
			holder.tvUserName.setText(getItem(position).getUsername());
			holder.tvUserNumber.setText(getItem(position).getId()+"");
			
			data="";
			
			if(TextUtils.isEmpty(Helper.getFormattedDistance(getItem(position).getPhoneNumber())))
				data=getItem(position).getUserEmail();
			else
			{
				data=Helper.getFormattedDistance(getItem(position).getPhoneNumber());
				
				if(!TextUtils.isEmpty(getItem(position).getUserEmail()))
					data=data+"|"+getItem(position).getUserEmail();
			}
				
			if(!TextUtils.isEmpty(data))
			{
				holder.tvUserPhoneNumber.setText(data);
				holder.tvUserPhoneNumber.setVisibility(View.VISIBLE);
			}				
			else
			{
				holder.tvUserPhoneNumber.setVisibility(View.GONE);
			}
				
			holder.tvVehicleDescription.setText(getItem(position).getVehicleDescription());
			//holder.tvUserEmail.setText(getItem(position).getUserEmail());
			holder.tvDaysAgo.setText(Helper.getTimeAgo(getItem(position).getDaysLeft()));
		//}
		
		
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView tvUserName ,tvUserNumber,tvUserPhoneNumber,tvVehicleDescription,tvDaysAgo;
		//TextView tvUserEmail;
		LinearLayout llChild;
		
		private void init(View convertView){
			tvUserNumber= (TextView) convertView.findViewById(R.id.tvUserNumber);
			 tvUserName= (TextView) convertView.findViewById(R.id.tvUserName);
			 tvUserPhoneNumber= (TextView) convertView.findViewById(R.id.tvPhoneNumber);
			 //tvUserEmail= (TextView) convertView.findViewById(R.id.tvUserEmail);
			 tvVehicleDescription= (TextView) convertView.findViewById(R.id.tvVehicleDescription);
			 tvDaysAgo= (TextView) convertView.findViewById(R.id.tvDaysAgo);	
			 llChild= (LinearLayout) convertView.findViewById(R.id.llChild);
			 //tvHeader= (TextView) convertView.findViewById(R.id.tvHeaderTitle);
 
		}
		 
	}
}
