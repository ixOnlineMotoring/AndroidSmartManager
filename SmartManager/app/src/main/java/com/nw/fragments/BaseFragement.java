package com.nw.fragments;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Helper;

public class BaseFragement extends Fragment
{
    private Dialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void showProgressDialog()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = CustomDialogManager.showProgressDialog(getActivity(), getString(R.string.pleasewait));
        progressDialog.show();
    }

    protected void resetActionBar()
    {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void showActionBar(String titletobeset)
    {
        getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActivity().getActionBar().setDisplayShowHomeEnabled(false);
        getActivity().getActionBar().setDisplayShowCustomEnabled(true);
        getActivity().getActionBar().setDisplayShowTitleEnabled(false);
        View view = View.inflate(getActivity(), R.layout.custom_action_bar_general, null);
        getActivity().getActionBar().setCustomView(view);
        /*Toolbar parent =(Toolbar) view.getParent();
        parent.setPadding(12,15,0,20);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0,0);*/

        TextView title = (TextView) view.findViewById(R.id.tvScreenTitle);
        title.setText(titletobeset);
        TextView tvBack = (TextView) view.findViewById(R.id.tvBack);
        Rect bounds = new Rect();
        Paint textPaint = tvBack.getPaint();
        textPaint.getTextBounds(tvBack.getText().toString(), 0, tvBack.getText().toString().length(), bounds);
        if (title.getText().toString().trim().length() > 30)
            title.setPadding((int) (bounds.width() * 1.5), 0, 0, 0);
        tvBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideKeyboard();
                if (getFragmentManager().getBackStackEntryCount() != 0)
                {
                    getFragmentManager().popBackStack();
                } else
                {
                    getActivity().finish();
                }
                getActivity().getActionBar().setDisplayShowCustomEnabled(false);
            }
        });
    }

    protected void showProgressDialog(String title)
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = CustomDialogManager.showProgressDialog(getActivity(),title);
    }

    protected void showLoadingProgressDialog()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = CustomDialogManager.showProgressDialog(getActivity(),getString(R.string.loading));
    }

    protected void hideProgressDialog()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    protected void showErrorDialog()
    {
        CustomDialogManager.showOkDialog(getActivity(),getString(R.string.error_occured_try_later));
    }

    protected void showErrorDialog(String message)
    {
        CustomDialogManager.showOkDialog(getActivity(), message);
    }

    protected void showErrorDialog(String title, String message)
    {
        CustomDialogManager.showOkDialog(getActivity(), message);
    }

    protected void hideKeyboard()
    {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null)
        {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected void hideKeyboard(View view)
    {
        if (view != null)
        {
            view.setOnTouchListener(new OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    Helper.hidekeybord(v);
                    return false;
                }
            });
        }
    }
}
