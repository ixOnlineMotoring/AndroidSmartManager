package com.smartmanager.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.nw.fragments.HomeScreenFragment;
import com.nw.interfaces.DialogListener;
import com.nw.webservice.DataManager;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.AppSession;
import com.utils.Helper;

public class BaseActivity extends FragmentActivity
{
    private Dialog progressDialog;


    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        /*Typeface font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
		int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
	    TextView yourTextView = (TextView) findViewById(titleId);
	    yourTextView.setTextColor(BaseActivity.this.getResources().getColor(R.color.white));
	    yourTextView.setTypeface(font);	*/
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (DataManager.getInstance().user == null)
        {
            CustomDialogManager.showOkDialog(this, "Session expired", new DialogListener()
            {

                @Override
                public void onButtonClicked(int type)
                {
                    AppSession.saveLoginStatus(BaseActivity.this, false);
                    finish();
                    DataManager.getInstance().user = null;

                    HomeScreenFragment.homeScreenFragment = null;
                    HomeScreenFragment.pageModule = null;
                    Helper.flushFileContents();
                    System.gc();
                    // start login screen
                    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    protected void showProgressDialog()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = CustomDialogManager.showProgressDialog(BaseActivity.this, getString(R.string.pleasewait));
        progressDialog.show();
    }

    protected void hideProgressDialog()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
