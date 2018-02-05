package com.nw.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.Iconify.IconValue;
import com.nw.fragments.HomeScreenFragment;
import com.nw.model.Page;
import com.nw.model.SubModule;
import com.nw.webservice.DataManager;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends BaseAdapter
{
	int count;
	List<Page> objects;
	ArrayList<SubModule> modules = null;
	Context context;
	static int width;
	
	public PageAdapter(Context context,List<Page> objects) 
	{
		this.context=context;
		if(!Helper.isTablet(context))
		{
			if(objects.size()<=12)
				count=12;
			else
				count=objects.size();
		}
		else
		{
			if(objects.size()<=20)
				count=20;
			else
				count=objects.size();
		}
		this.objects=objects;
		/*DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		width = displayMetrics.widthPixels;
		width=width-Helper.dpToPx(context, 1);*/
	}
	
	public PageAdapter(Context context,ArrayList<SubModule> subModules,boolean isSubModule)
	{
		this.context=context;
		if(!Helper.isTablet(context))
		{
			if(subModules.size()<=12)
				count=12;
			else
				count=subModules.size();
		}
		else
		{
			if(subModules.size()<=20)
				count=20;
			else
				count=subModules.size();
		}
		this.modules=subModules;
		/*DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		width = displayMetrics.widthPixels;
		width=width-Helper.dpToPx(context, 1);*/
	}

	@Override
	public int getCount()
	{
		return count;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final ViewHoldar viewHoldar;
		if(convertView==null)
		{
			viewHoldar=new ViewHoldar();
			convertView=LayoutInflater.from(context).inflate(R.layout.grid_item_page, parent, false);
			viewHoldar.init(context, convertView);
			convertView.setTag(viewHoldar);
		}
		else{
			viewHoldar=(ViewHoldar) convertView.getTag();
		}
		try
		{
			if (modules!=null)
			{

				if(position<modules.size())
				{
					viewHoldar.tvTitle.setVisibility(View.VISIBLE);
					viewHoldar.ivPicture.setVisibility(View.VISIBLE);
					IconValue imageIconValue= DataManager.getIconValue(modules.get(position).getSubModuleName());
					if (imageIconValue==null)
					{
						viewHoldar.tvTitle.setVisibility(View.GONE);
						viewHoldar.ivPicture.setVisibility(View.GONE);
					}else {
						viewHoldar.tvTitle.setText(modules.get(position).getSubModuleName());
						viewHoldar.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null,Helper.getIcon(context,imageIconValue,50), null, null);
					}
				}
				else
				{
					viewHoldar.tvTitle.setVisibility(View.INVISIBLE);
					viewHoldar.ivPicture.setVisibility(View.INVISIBLE);
				}
			}
			else
			{
				if(position<objects.size())
				{
					viewHoldar.tvTitle.setVisibility(View.VISIBLE);
					viewHoldar.ivPicture.setVisibility(View.VISIBLE);
					IconValue imageIconValue= DataManager.getIconValue(objects.get(position).getName());
					if (imageIconValue==null)
					{
						viewHoldar.tvTitle.setVisibility(View.GONE);
						viewHoldar.ivPicture.setVisibility(View.GONE);
					}else {
						viewHoldar.tvTitle.setText(objects.get(position).getName());
						viewHoldar.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null,Helper.getIcon(context,imageIconValue,50), null, null);
					}
				}
				else
				{
					viewHoldar.tvTitle.setVisibility(View.INVISIBLE);
					viewHoldar.ivPicture.setVisibility(View.INVISIBLE);
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return convertView;
	}
	
	public boolean isValid(int position)
	{
		if(objects!=null && position<objects.size())
			return true;
		else if (modules!=null && position<modules.size()) {
			return true;
		}
		else
			return false;
	}
	
	public boolean hasSubModule(int position)
	{
		if (modules!=null && modules.get(position).getSubPages().size()!= 0)
		{
			return true;
		}
		else {
			return false;
		}
	}
	
	private static class ViewHoldar
	{
		ImageView ivPicture;
		TextView tvTitle;
		
		public void init(Context context, View convertView){
			ivPicture=(ImageView) convertView.findViewById(R.id.imageView1);
			tvTitle=(TextView) convertView.findViewById(R.id.tvTitle);
			/*if(Helper.isTablet(context))
			{
				HomeScreenFragment.Height=(width/4);
				HomeScreenFragment.Width=(width/4);
				convertView.setLayoutParams(new AbsListView.LayoutParams(HomeScreenFragment.Width,HomeScreenFragment.Height));
			}				
			else
			{
				HomeScreenFragment.Height=(width/3);
				HomeScreenFragment.Width=(width/3);
				convertView.setLayoutParams(new AbsListView.LayoutParams(HomeScreenFragment.Width,HomeScreenFragment.Height));
			}*/
				
			tvTitle.setSingleLine(true);
		}
	}

	@Override
	public Page getItem(int arg0) 
	{
		if (objects!=null)
		{
			return objects.get(arg0);
		}else {
			return modules.get(arg0);
		}
		
	}

	@Override
	public long getItemId(int arg0) 
	{
		return arg0;
	}

	
}
