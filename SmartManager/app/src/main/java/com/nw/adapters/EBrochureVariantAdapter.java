package com.nw.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nw.model.Variant;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.math.BigDecimal;
import java.util.List;

public class EBrochureVariantAdapter extends ArrayAdapter<Variant>
{

    public EBrochureVariantAdapter(Context context, int resource, List<Variant> objects)
    {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_ebroucher_variant, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.init(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.tvTitle.setText(Html.fromHtml("" + getItem(position).getFriendlyName() + ""));

        viewHolder.tvSubTitle.setText("New | "+getItem(position).getMeadCode() + " | ");

        if (Helper.formatPrice(new BigDecimal(getItem(position).getPrice()) + "").equals("R0"))
        {
            viewHolder.tvRetailPrice.setText("R?");
        } else
        {
            viewHolder.tvRetailPrice.setText(Helper.formatPrice(new BigDecimal(getItem(position).getPrice()) + ""));
        }
        return convertView;
    }

    private static class ViewHolder
    {
        TextView tvTitle, tvSubTitle,tvRetailPrice;

        public void init(View convertView)
        {
            tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            tvSubTitle = (TextView) convertView.findViewById(R.id.tvSubTitle);
            tvRetailPrice = (TextView) convertView.findViewById(R.id.tvRetailPrice);
        }
    }
}
