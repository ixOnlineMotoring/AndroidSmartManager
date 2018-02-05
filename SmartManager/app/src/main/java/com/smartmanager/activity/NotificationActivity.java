package com.smartmanager.activity;

import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;

import com.nw.fragments.NotificationsFragment;
import com.nw.fragments.PushNotificationsFragment;
import com.smartmanager.android.R;


public class NotificationActivity extends FragmentActivity
{
    NotificationsFragment notificationsFragment;
    PushNotificationsFragment pushNotificationsFragment;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.activity_blog);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras().containsKey("Unseen"))
        {
            notificationsFragment = new NotificationsFragment();
             getSupportFragmentManager().beginTransaction().replace(R.id.Container, notificationsFragment).commit();
        } else if (getIntent().getExtras().containsKey("pushNotification"))
        {
            pushNotificationsFragment = new PushNotificationsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, pushNotificationsFragment).commit();
        }
    }
}
