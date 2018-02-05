package com.nw.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nw.interfaces.BlogItemEditClickListener;
import com.nw.model.NotificationListing;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.util.ArrayList;

public class PushNotificationAdapter extends ArrayAdapter<NotificationListing>
{
    BlogItemEditClickListener blogItemEditClickListener;

    public PushNotificationAdapter(Context c, ArrayList<NotificationListing> list,BlogItemEditClickListener blogItemEditClickListener)
    {
        super(c, R.layout.list_item_push_notification, list);
        this.blogItemEditClickListener = blogItemEditClickListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_push_notification, null);
            holder.init(convertView);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvLeadNumber.setText(getItem(position).getSource() + " " + getItem(position).getIdentity());
        holder.tvDate.setText(Helper.showDateWithDay(getItem(position).getSent()));
        holder.tvDetails.setText(getItem(position).getMessage());
        if (getItem(position).getIsRead())
        {
            holder.tvNewLable.setVisibility(View.INVISIBLE);
            holder.llayoutPushNotification.setBackgroundResource(R.color.black);
        } else
        {
            holder.tvNewLable.setVisibility(View.VISIBLE);
            holder.llayoutPushNotification.setBackgroundResource(R.color.light_white_);
        }

        holder.llayoutPushNotification.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                blogItemEditClickListener.onBlogItemClicked(position);
            }
        });

        return convertView;
    }

    private static class ViewHolder
    {

        TextView tvLeadNumber, tvNewLable, tvDate, tvDetails;
        LinearLayout llayoutPushNotification;


        public void init(View convertView)
        {

            tvLeadNumber = (TextView) convertView.findViewById(R.id.tvLeadNumber);
            tvNewLable = (TextView) convertView.findViewById(R.id.tvNewLable);
            tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            tvDetails = (TextView) convertView.findViewById(R.id.tvDetails);

            llayoutPushNotification = (LinearLayout) convertView.findViewById(R.id.llayoutPushNotification);
        }
    }
}
