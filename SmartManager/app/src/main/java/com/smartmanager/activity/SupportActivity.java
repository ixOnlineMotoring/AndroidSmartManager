package com.smartmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import com.nw.fragments.SupportRequestFragment;
import com.smartmanager.android.R;

import java.io.IOException;

/**
 * Created by Akshay on 14-11-2017.
 */

public class SupportActivity extends LoacationBaseActivity
{
    SupportRequestFragment supportRequestFragment;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.activity_support);
        if (getIntent().hasExtra("Support Request"))
        {
            try
            {
                supportRequestFragment = new SupportRequestFragment();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, supportRequestFragment).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (supportRequestFragment != null)
            supportRequestFragment.onActivityResult(requestCode, resultCode, data);
    }
}
