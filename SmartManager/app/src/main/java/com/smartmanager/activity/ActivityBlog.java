package com.smartmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentTransaction;

import com.nw.fragments.CustomerDeliveryFragment;
import com.nw.fragments.LeadsListFragment;
import com.nw.fragments.SearchBlogFragment;
import com.smartmanager.android.R;

public class ActivityBlog extends BaseActivity
{
    SearchBlogFragment searchBlogFragment;
    CustomerDeliveryFragment customerDeliveryFragment;
    LeadsListFragment leadsListFragment;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.activity_blog);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (!getIntent().hasExtra("CustomerDelivery"))
        {
            getActionBar().setTitle(R.string.blog);
            searchBlogFragment = new SearchBlogFragment();
           getSupportFragmentManager().beginTransaction().replace(R.id.Container, searchBlogFragment).commit();
        } else
        {
            customerDeliveryFragment = new CustomerDeliveryFragment();
            getActionBar().setTitle(R.string.create_blog);
           getSupportFragmentManager().beginTransaction().replace(R.id.Container, customerDeliveryFragment).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (searchBlogFragment != null)
            searchBlogFragment.onActivityResult(requestCode, resultCode, data);
        if (customerDeliveryFragment != null)
            customerDeliveryFragment.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        searchBlogFragment = null;
        customerDeliveryFragment = null;
        System.gc();
    }
}
