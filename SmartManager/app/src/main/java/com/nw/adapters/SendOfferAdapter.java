package com.nw.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nw.interfaces.DoAppraisalInputListener;
import com.nw.widget.CustomEditText;
import com.smartmanager.android.R;

import java.util.ArrayList;

public class SendOfferAdapter extends ArrayAdapter<String>
{
    DoAppraisalInputListener doAppraisalInputListener;
    public SendOfferAdapter(Context context, ArrayList<String> arrayList, DoAppraisalInputListener doAppraisalInputListener)
    {
        super(context, R.layout.single_item_vehicle_extra, arrayList);
        this.doAppraisalInputListener = doAppraisalInputListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_item_send_offer, parent, false);
            holder.init(convertView);
            holder.mutableWatcherTitle = new MutableWatcherTitle();
            holder.etKey.addTextChangedListener(holder.mutableWatcherTitle);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mutableWatcherTitle.setActive(false);

        holder.etKey.setText(getItem(position),TextView.BufferType.EDITABLE);
     //   holder.etValue.setText(Helper.formatPrice(""+getItem(position).getPrice()),TextView.BufferType.EDITABLE);

        holder.mutableWatcherTitle.setPosition(position);
        holder.mutableWatcherTitle.setActive(true);

        holder.iv_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                doAppraisalInputListener.onButtonClicked(false, position, "delete");
            }
        });

        return convertView;
    }


    private static class ViewHolder
    {
        EditText etKey;
        ImageView iv_delete;
        public MutableWatcherTitle mutableWatcherTitle;

        public void init(View convertView)
        {
            etKey = (CustomEditText) convertView.findViewById(R.id.edtKey);
            iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
        }
    }


    class MutableWatcherTitle implements TextWatcher
    {
        private int mPosition;
        private boolean mActive;

        void setPosition(int position)
        {
            mPosition = position;
        }

        void setActive(boolean active)
        {
            mActive = active;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override
        public void afterTextChanged(Editable s)

        {
            if (mActive)
            {
                doAppraisalInputListener.onButtonClicked(true, mPosition, s.toString().trim());
            }
        }
    }
}
