package com.nw.broadcast;

/**
 * Created by Swapnil on 13-03-2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.nw.service.ServiceImage;
import com.nw.webservice.DataManager;

public class NetworkChangeReceiver extends BroadcastReceiver
{
    int int_previous_id;
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        int status = NetworkUtil.getConnectivityStatusString(context);
        Log.d("Sulod sa network reciever", "Sulod sa network reciever");
        System.out.println("CONNECTIVITY CHANGE askjdfhkjsahd: " + status);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction()))
        {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()) {
                // Wifi is connected
                checkNetworkChange(context,status);
            }
        }
    }

    private void checkNetworkChange(Context context, int status)
    {
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED)
        {

        } else
        {
            if (status == NetworkUtil.NETWORK_STAUS_WIFI)
            {
                // Start Background service
                if(DataManager.getInstance().user != null)
                context.startService(new Intent(context, ServiceImage.class));
            } else if (status == NetworkUtil.NETWORK_STATUS_MOBILE)
            {
                // Stop Background service
                context.stopService(new Intent(context,ServiceImage.class));
            }
        }
    }
}
