package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nw.interfaces.ConditionListener;
import com.nw.model.Conditioning;
import com.smartmanager.android.R;

import java.util.List;

public class OptionsConditionAdapter extends ArrayAdapter<Conditioning>
{
    ConditionListener conditionListener;

    public OptionsConditionAdapter(Context context, int resource, List<Conditioning> objects, ConditionListener conditionListener)
    {
        super(context, resource, objects);
        this.conditionListener = conditionListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_condition_options, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.init(convertView);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_title_name.setText(getItem(position).getname());

        if (getItem(position).getOptionsConditions().size() < 3)
        {
            viewHolder.radiobutton_low.setVisibility(View.INVISIBLE);
            viewHolder.radiobutton_yes.setChecked(getItem(position).getOptionsConditions().get(0).isSelected());
            viewHolder.radiobutton_no.setChecked(getItem(position).getOptionsConditions().get(1).isSelected());
        } else
        {
            viewHolder.radiobutton_low.setVisibility(View.VISIBLE);
            viewHolder.radiobutton_yes.setChecked(getItem(position).getOptionsConditions().get(0).isSelected());
            viewHolder.radiobutton_no.setChecked(getItem(position).getOptionsConditions().get(1).isSelected());
            viewHolder.radiobutton_low.setChecked(getItem(position).getOptionsConditions().get(2).isSelected());
        }

        final ViewHolder viewHolderfinal = viewHolder;
        viewHolder.radiobutton_yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewHolderfinal.radiobutton_yes.setChecked(true);
                viewHolderfinal.radiobutton_no.setChecked(false);
                viewHolderfinal.radiobutton_low.setChecked(false);
                conditionListener.onButtonClicked(position, 0);
            }
        });
        viewHolder.radiobutton_no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewHolderfinal.radiobutton_yes.setChecked(false);
                viewHolderfinal.radiobutton_no.setChecked(true);
                viewHolderfinal.radiobutton_low.setChecked(false);
                conditionListener.onButtonClicked(position, 1);
            }
        });
        viewHolder.radiobutton_low.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewHolderfinal.radiobutton_yes.setChecked(false);
                viewHolderfinal.radiobutton_no.setChecked(false);
                viewHolderfinal.radiobutton_low.setChecked(true);
                conditionListener.onButtonClicked(position, 2);
            }
        });

        return convertView;
    }

    public static class ViewHolder
    {
        TextView tv_title_name;
        //   RadioGroup radiogroupselection;
        RadioButton radiobutton_yes, radiobutton_no, radiobutton_low;

        public void init(View v)
        {
            tv_title_name = (TextView) v.findViewById(R.id.tv_title_name);

            //    radiogroupselection = (RadioGroup) v.findViewById(R.id.radiogroupselection);

            radiobutton_yes = (RadioButton) v.findViewById(R.id.radiobutton_yes);
            radiobutton_no = (RadioButton) v.findViewById(R.id.radiobutton_no);
            radiobutton_low = (RadioButton) v.findViewById(R.id.radiobutton_low);
        }
    }
}
