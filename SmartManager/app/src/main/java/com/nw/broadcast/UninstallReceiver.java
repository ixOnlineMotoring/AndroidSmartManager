package com.nw.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.onesignal.OneSignal;

/**
 * Created by Akshay on 06-11-2017.
 */

public class UninstallReceiver extends BroadcastReceiver
{
    Context context;
    @Override
    public void onReceive(Context context, Intent intent)
    {

        this.context = context;

        // when package removed
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            Log.e(" BroadcastReceiver ", "onReceive called "
                    + " PACKAGE_REMOVED ");
           OneSignal.setSubscription(false);
        }

    }
}
