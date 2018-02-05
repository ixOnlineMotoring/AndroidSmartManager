package com.nw.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nw.interfaces.DialogInputListener;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.DoAppraisalInputListener;
import com.nw.model.InteriorReconditioning;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Helper;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class InteriorReconditioningAdapter extends ArrayAdapter<InteriorReconditioning>
{
    DoAppraisalInputListener doAppraisalInputListener;

    public InteriorReconditioningAdapter(Context context, int resource, List<InteriorReconditioning> objects, DoAppraisalInputListener doAppraisalInputListener)
    {
        super(context, resource, objects);
        this.doAppraisalInputListener = doAppraisalInputListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_interiorrecondition_options, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.init(convertView);
            viewHolder.mWatcher = new MutableWatcher();
            viewHolder.mutableWatcherTitle = new MutableWatcherTitle();
            viewHolder.et_value.addTextChangedListener(viewHolder.mWatcher);
            viewHolder.et_extra_title.addTextChangedListener(viewHolder.mutableWatcherTitle);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (!getItem(position).getCustomType().equalsIgnoreCase("Null"))
            viewHolder.tv_ReconditioningType.setText(getItem(position).getReconditioningType() + getItem(position).getCustomType());
        else
            viewHolder.tv_ReconditioningType.setText(getItem(position).getReconditioningType());

        if (getItem(position).isActive())
        {
            viewHolder.chkValue.setChecked(true);
        } else
        {
            viewHolder.chkValue.setChecked(false);
        }

        viewHolder.mWatcher.setActive(false, viewHolder.et_value);
        viewHolder.mutableWatcherTitle.setActive(false);
        //    viewHolder.et_value.setText(Helper.formatPrice("" + getItem(position).getValue()), TextView.BufferType.EDITABLE);
        viewHolder.et_value.setText("R" + getItem(position).getValue(), TextView.BufferType.EDITABLE);
        viewHolder.et_extra_title.setText(getItem(position).getReconditioningType(), TextView.BufferType.EDITABLE);
        viewHolder.mWatcher.setPosition(position);
        viewHolder.mutableWatcherTitle.setPosition(position);
        viewHolder.mWatcher.setActive(true, viewHolder.et_value);
        viewHolder.mutableWatcherTitle.setActive(true);

        viewHolder.chkValue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!viewHolder.chkValue.isChecked())
                {
                    doAppraisalInputListener.onButtonClicked(false, position, "checkbox");
                } else
                {
                    if ((!viewHolder.et_value.getText().toString().trim().equalsIgnoreCase("")) && (!viewHolder.et_value.getText().toString().trim().equalsIgnoreCase("R0")))
                    {
                        if (getItem(position).getExtraValue())
                        {
                            if ((!viewHolder.et_extra_title.getText().toString().trim().equalsIgnoreCase("")))
                            {
                                doAppraisalInputListener.onButtonClicked(false, position, "checkbox");
                            } else
                            {
                                viewHolder.chkValue.setChecked(false);
                                CustomDialogManager.showOkDialog(getContext(), getContext().getResources().getString(R.string.enter_name));
                            }
                        } else
                        {
                            doAppraisalInputListener.onButtonClicked(false, position, "checkbox");
                        }

                    } else
                    {
                        notifyDataSetChanged();
                        CustomDialogManager.showOkDialog(getContext(), getContext().getResources().getString(R.string.enter_value), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                // getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
                    }
                }
            }
        });

        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                doAppraisalInputListener.onButtonClicked(false, position, "delete");
            }
        });

        if (getItem(position).getExtraValue())
        {
            viewHolder.et_extra_title.setVisibility(View.VISIBLE);
            viewHolder.iv_delete.setVisibility(View.VISIBLE);
            viewHolder.tv_ReconditioningType.setVisibility(View.GONE);
        } else
        {
            viewHolder.et_extra_title.setVisibility(View.GONE);
            viewHolder.iv_delete.setVisibility(View.GONE);
            viewHolder.tv_ReconditioningType.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public static class ViewHolder
    {
        TextView tv_ReconditioningType;
        EditText et_value, et_extra_title;
        CheckBox chkValue;
        ImageView iv_delete;
        public MutableWatcher mWatcher;
        public MutableWatcherTitle mutableWatcherTitle;

        public void init(View v)
        {
            tv_ReconditioningType = (TextView) v.findViewById(R.id.tv_ReconditioningType);
            et_value = (EditText) v.findViewById(R.id.et_value);
            et_extra_title = (EditText) v.findViewById(R.id.et_extra_title);
            iv_delete = (ImageView) v.findViewById(R.id.iv_delete);
            chkValue = (CheckBox) v.findViewById(R.id.chkValue);
        }
    }


    class MutableWatcher implements TextWatcher
    {
        private int mPosition;
        private boolean mActive;
        private EditText editText;

        void setPosition(int position)
        {
            mPosition = position;
        }

        void setActive(boolean active, EditText editText)
        {
            mActive = active;
            this.editText = editText;
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
                if (s.toString().trim().length() <= 7)
                {
                    String value = s.toString().replaceAll("R", "");
                    // To remove all white space
                    value = value.replaceAll("\\s+", "");
                    doAppraisalInputListener.onButtonClicked(false, mPosition, value);
                } else
                {
                    editText.setText(s.toString().substring(0, s.toString().length() - 1));
                    editText.setSelection(s.toString().length() - 1);
                }
            }
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
