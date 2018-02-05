package com.nw.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nw.interfaces.SectionLoadMoreListner;
import com.nw.model.Leads;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;

public class LeadsListAdapter extends BaseExpandableListAdapter
{
	Context context;
	HashMap<String, ArrayList<Leads>> leads;
	String[]groups=new String[]{"Unseen","Work in progress"};
	int[]childCount;
	
	String data;

	public LeadsListAdapter(Context context,HashMap<String, ArrayList<Leads>> leads,int[]childCount)
	{
		this.context=context;
		this.leads=leads;
		this.childCount=childCount;
	}
	
	@Override
	public Leads getChild(int groupPosition, int childPosition)
	{
		try
		{
			return leads.get(groups[groupPosition]).get(childPosition);
		}catch (Exception e) 
		{
			return null;
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return 0;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		final ViewHolder holder;
		if(convertView==null)
		{
			convertView=LayoutInflater.from(context).inflate(R.layout.list_item_leadslist, parent,false);
			holder= new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		}else{
			holder= (ViewHolder) convertView.getTag();
		}
		
		holder.viewStub.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				holder.pbLoading.setVisibility(View.VISIBLE);		
				holder.tvLoading.setText(R.string.loading_);
				
				if(loadMoreListner!=null);
					loadMoreListner.onLoadMore(groupPosition==0?false:true);
			}
		});
		
		if(!getChild(groupPosition,childPosition).isLoadMore())
		{
			holder.llChild.setVisibility(View.VISIBLE);
			holder.viewStub.setVisibility(View.GONE);
			
			holder.tvUserName.setText(getChild(groupPosition,childPosition).getUsername());
			holder.tvUserNumber.setText(getChild(groupPosition,childPosition).getId()+"");
			
			data="";
			if(TextUtils.isEmpty(Helper.formatMobileNumber(getChild(groupPosition,childPosition).getPhoneNumber()))){
				if (TextUtils.isEmpty(getChild(groupPosition,childPosition).getUserEmail()))
				{
					data= "Phone number? | Email address?";
				}else {
					data= "Phone number? | " + getChild(groupPosition,childPosition).getUserEmail();
				}
			}else{
				data=Helper.formatMobileNumber(getChild(groupPosition,childPosition).getPhoneNumber());
				
				if(!TextUtils.isEmpty(getChild(groupPosition,childPosition).getUserEmail()))
					data=data+" | "+getChild(groupPosition,childPosition).getUserEmail();
				else {
					data = data + " | Email address?";
				}
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
			if (getChild(groupPosition,childPosition).getVehicleDescription().contains("null"))
			{
					holder.tvVehicleDescription.setText("Vehicle info?");
			}else if (getChild(groupPosition,childPosition).getVehicleDescription().trim().equalsIgnoreCase("0")) 
			{
				holder.tvVehicleDescription.setText("Vehicle info?");
			}
			else{
					holder.tvVehicleDescription.setText(getChild(groupPosition,childPosition).getVehicleDescription());
			}
			if(TextUtils.isEmpty(getChild(groupPosition,childPosition).getLastUpdateDate()))
			{
				holder.tvDaysAgo.setText("No update: "+Helper.getTimeLastUpdate(getChild(groupPosition,childPosition).getDaysLeft())+"ago");
				holder.tvDaysAgo.setTextColor(Color.RED);
			}		
			else
			{
				holder.tvDaysAgo.setText("Last Update: "+Helper.getTimeAgo(getChild(groupPosition,childPosition).getLastUpdateDate())
																	+" | "+getChild(groupPosition,childPosition).getLastUpdate());
				
				if(groupPosition==0)
					holder.tvDaysAgo.setTextColor(Helper.getColorTimeAgo(getChild(groupPosition,childPosition).getLastUpdateDate()));
				else
					holder.tvDaysAgo.setTextColor(Helper.getColorTimeAgoWIP(getChild(groupPosition,childPosition).getLastUpdateDate()));
			}
			
			//holder.tvDaysAgo.setText("Last Update:"+Helper.getTimeAgo(getChild(groupPosition,childPosition).getLastUpdateDate()));
		}
		else
		{
			holder.llChild.setVisibility(View.GONE);
			holder.viewStub.setVisibility(View.VISIBLE);			
			holder.tvLoading.setText("More");	
			holder.pbLoading.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return leads.get(groups[groupPosition]).size();
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return groups[groupPosition];
	}

	@Override
	public int getGroupCount()
	{
		return groups.length;
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		if(convertView==null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.header_item_s, parent, false);
		}		
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tvHeaderTitle);
		ImageView ivIcon=(ImageView) convertView.findViewById(R.id.ivIcon);
		
		TextView tvCount = (TextView) convertView.findViewById(R.id.tvHeaderCount);
		tvTitle.setText(groups[groupPosition]);
		tvCount.setText("" + childCount[groupPosition]);
		
		if(groupPosition==0)
			convertView.setBackgroundResource(R.color.dark_blue);
		else
			convertView.setBackgroundResource(R.color.gray);
		
		if(isExpanded && getChildrenCount(groupPosition)>0)
			ivIcon.setRotation(90);
		else
			ivIcon.setRotation(0);
		
		
		return convertView;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		try{
			
			boolean result= leads.get(groups[groupPosition]).get(childPosition).isLoadMore();
			return !result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	SectionLoadMoreListner loadMoreListner;
	
	public void setLoadMoreListener(SectionLoadMoreListner loadMoreListner)
	{
		this.loadMoreListner=loadMoreListner;
	}
	
	static class ViewHolder{
		TextView tvUserName ,tvUserNumber,tvUserPhoneNumber,tvVehicleDescription,tvDaysAgo;
		//TextView tvUserEmail;
		LinearLayout llChild;
		View viewStub;
		ProgressBar pbLoading;
		TextView tvLoading;
		
		private void init(View convertView)
		{
			tvUserNumber= (TextView) convertView.findViewById(R.id.tvUserNumber);
			 tvUserName= (TextView) convertView.findViewById(R.id.tvUserName);
			 tvUserPhoneNumber= (TextView) convertView.findViewById(R.id.tvPhoneNumber);
			 //tvUserEmail= (TextView) convertView.findViewById(R.id.tvUserEmail);
			 tvVehicleDescription= (TextView) convertView.findViewById(R.id.tvVehicleDescription);
			 tvDaysAgo= (TextView) convertView.findViewById(R.id.tvDaysAgo);	
			 llChild= (LinearLayout) convertView.findViewById(R.id.llChild);			 
		
			 viewStub= convertView.findViewById(R.id.loadMore);
			 pbLoading=(ProgressBar) viewStub.findViewById(R.id.pbLoading);			 
			 tvLoading= (TextView) convertView.findViewById(R.id.tvLoading);
		}
		 
	}

}
