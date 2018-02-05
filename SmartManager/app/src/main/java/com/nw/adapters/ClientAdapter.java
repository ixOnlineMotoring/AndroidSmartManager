package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.Client;
import com.smartmanager.android.R;

import java.util.List;

public class ClientAdapter extends ArrayAdapter<Client> 
{
	
	Context mContext;
	public List<Client> mItems;
	public ClientAdapter(Context context, int resource, List<Client> objects) 
	{
		super(context, resource, objects);
		mItems=objects;
		mContext=context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if(convertView==null)
			convertView=LayoutInflater.from(getContext()).inflate(R.layout.list_item_text2, parent,false);
		TextView tvName=(TextView) convertView.findViewById(R.id.tvText);
		tvName.setText(""+getItem(position).getName());
		
		if(getItem(position).isCheckIn())
			tvName.setTextColor(mContext.getResources().getColor(R.color.red));
		else
			tvName.setTextColor(mContext.getResources().getColor(R.color.white));
		return convertView;
	}

	//private final Object mLock = new Object();
    // @SuppressWarnings("unused")
	//private ItemsFilter mFilter;
     
	
	/* *//**
     * Custom Filter implementation for the items adapter.
     *
     *//*
    private class ItemsFilter extends Filter 
    {
        @Override
		protected FilterResults performFiltering(CharSequence prefix) 
        {
            // Initiate our results object
            FilterResults results = new FilterResults();
            // If the adapter array is empty, check the actual items array and use it
            if (mItems == null) {
                synchronized (mLock) 
                { // Notice the declaration above
                    mItems = new ArrayList<Client>(mItems.size());
                }
            }
            // No prefix is sent to filter by so we're going to send back the original array
            if (prefix == null || prefix.length() == 0) 
            {
                synchronized (mLock) 
                {
                    results.values = mItems;
                    results.count = mItems.size();
                }
            } else {
                    // Compare lower case strings
                String prefixString = prefix.toString().toLowerCase(Locale.US);
                // Local to here so we're not changing actual array
                final List<Client> items = mItems;
                final int count = items.size();
                final ArrayList<Client> newItems = new ArrayList<Client>(count);
                for (int i = 0; i < count; i++)
                {
                    final Client item = items.get(i);
                    // First match against the whole, non-splitted value
                    if (item.getName().toString().toLowerCase(Locale.US).contains(prefixString)) 
                    {
                        newItems.add(item);
                    } 
                }
                // Set and return
                results.values = newItems;
                results.count = newItems.size();
            }
            return results;
        }
        @Override
		@SuppressWarnings("unchecked")
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            mItems = (ArrayList<Client>) results.values;
            // Let the adapter know about the updated list
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }*/
}
