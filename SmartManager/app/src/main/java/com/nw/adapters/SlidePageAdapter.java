package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joanzapata.android.iconify.Iconify.IconValue;
import com.nw.model.Page;
import com.nw.model.SubModule;
import com.nw.webservice.DataManager;
import com.smartmanager.android.R;

import java.util.ArrayList;
import java.util.List;

public class SlidePageAdapter extends BaseAdapter
{
	/*List<Page> objects;
	List<SubModule> modules;*/
	List<Page> objects;
	ArrayList<SubModule> modules = null;
	Context context;

	public SlidePageAdapter(Context context, int resource, List<Page> objects) 
	{
		this.objects =objects;
		this.context = context;
	}
	
	public SlidePageAdapter(Context context, int resource, ArrayList<SubModule> subModules,boolean isSubModule) 
	{
		this.modules = subModules;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if(convertView==null)
			convertView=LayoutInflater.from(context).inflate(R.layout.list_item_text2, parent,false);
		TextView tvName=(TextView) convertView.findViewById(R.id.tvText);
		if (objects!=null)
		{
			tvName.setText(""+objects.get(position).getName());
		} else
		{
			tvName.setText(""+modules.get(position).getName());
		}
		

		if (modules!=null)
		{

			if(position<modules.size())
			{
				tvName.setVisibility(View.VISIBLE);
				IconValue imageIconValue= DataManager.getIconValue(modules.get(position).getSubModuleName());
				if (imageIconValue==null)
				{
					tvName.setVisibility(View.GONE);
				}else {
					tvName.setText(modules.get(position).getSubModuleName());
				}
			}
			else
			{
				tvName.setVisibility(View.INVISIBLE);
			}
		}
		else
		{
			if(position<objects.size())
			{
				tvName.setVisibility(View.VISIBLE);
				IconValue imageIconValue= DataManager.getIconValue(objects.get(position).getName());
				if (imageIconValue==null)
				{
					tvName.setVisibility(View.GONE);
				}else {
					tvName.setText(objects.get(position).getName());
				}
			}
			else
			{
				tvName.setVisibility(View.INVISIBLE);
			}
		}
	
		return convertView;
	}

	@Override
	public int getCount()
	{

		// TODO Auto-generated method stub
		if (objects!=null)
		{
			return objects.size();
		} else
		{
			return modules.size();
		}
	}

	@Override
	public Object getItem(int position)
	{

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position)
	{

		// TODO Auto-generated method stub
		return 0;
	}

}
