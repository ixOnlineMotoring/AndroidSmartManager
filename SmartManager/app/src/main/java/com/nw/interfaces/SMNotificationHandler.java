package com.nw.interfaces;

import com.onesignal.OneSignal.NotificationOpenedHandler;
import com.utils.Helper;

import org.json.JSONObject;

public class SMNotificationHandler implements NotificationOpenedHandler
{

    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive)
    {

        try
        {
            if (additionalData != null)
            {
                if (additionalData.has("actionSelected"))
                    Helper.Log("OneSignalExample", "OneSignal notification button with id " + additionalData.getString("actionSelected") + " pressed");

                Helper.Log("OneSignalExample", "Full additionalData:\n" + additionalData.toString());
            }
        } catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
}


