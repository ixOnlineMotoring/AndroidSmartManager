package com.nw.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.ActiveStockFeeds;
import com.nw.widget.CustomEditText;
import com.smartmanager.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akshay on 10-10-2017.
 */

public class ActiveStockFeedsAdapter extends ArrayAdapter<ActiveStockFeeds>

{
    Resources resources;
    List<ActiveStockFeeds> activeStockFeedsList;

    public ActiveStockFeedsAdapter(Context context, int resource, List<ActiveStockFeeds> activeStockFeedsList)
    {
        super(context, R.layout.active_stock_feeds_items, activeStockFeedsList);
        resources = getContext().getResources();
        this.activeStockFeedsList = activeStockFeedsList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.active_stock_feeds_items, parent, false);
            viewHolder = new ActiveStockFeedsAdapter.ViewHolder();
            viewHolder.init(convertView);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ActiveStockFeedsAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.tvActiveFeeds.setText(getItem(position).getActiveStocksName());
        return convertView;
    }

    public static class ViewHolder
    {
        TextView tvActiveFeeds;

        public void init(View v)
        {
            tvActiveFeeds = (TextView) v.findViewById(R.id.tvActiveFeeds);
        }
    }
}
