package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nw.interfaces.OnListItemClickWithKeyListener;
import com.nw.model.ExteriorReconditioning;
import com.nw.model.Vehicle;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Exterior_ReconditioningAdapter extends ArrayAdapter<ExteriorReconditioning>
{
    OnListItemClickWithKeyListener onListItemClickWithKeyListener;

    public Exterior_ReconditioningAdapter(Context c, ArrayList<ExteriorReconditioning> list, OnListItemClickWithKeyListener onListItemClickWithKeyListener)
    {
        super(c, R.layout.list_exterior_reconditioning, list);
        this.onListItemClickWithKeyListener = onListItemClickWithKeyListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_exterior_reconditioning, null);
            holder.init(convertView);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        if (getItem(position).getRepair().equalsIgnoreCase("0"))
        {
            holder.tv_title_exterior.setText(getItem(position).getExteriorType() + " - Replace");
        } else
        {
            holder.tv_title_exterior.setText(getItem(position).getExteriorType() + " - Repair");
        }
        if (getItem(position).isActive())
        {
            holder.chk_title_price_exterior.setChecked(true);
        } else
        {
            holder.chk_title_price_exterior.setChecked(false);
        }


        holder.chk_title_price_exterior.setText(Helper.formatPrice(new BigDecimal(getItem(position).getValue()) + ""));

        holder.iv_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onListItemClickWithKeyListener.onClick(position, "delete");
            }
        });

        holder.chk_title_price_exterior.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!holder.chk_title_price_exterior.isChecked())
                {
                    onListItemClickWithKeyListener.onClick(position, "uncheck");
                } else
                {
                    onListItemClickWithKeyListener.onClick(position, "check");
                }
            }
        });


        return convertView;
    }

    private static class ViewHolder
    {

        TextView tv_title_exterior;
        ImageView iv_delete;
        CheckBox chk_title_price_exterior;

        public void init(View convertView)
        {

            tv_title_exterior = (TextView) convertView.findViewById(R.id.tv_title_exterior);
            chk_title_price_exterior = (CheckBox) convertView.findViewById(R.id.chk_title_price_exterior);
            iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
        }
    }
}
