package com.nw.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.nw.database.SMDatabase;
import com.nw.interfaces.BackgroundWebServiceListener;
import com.nw.webservice.BackgroundWebServices;
import com.nw.webservice.DataManager;
import com.smartmanager.android.R;
import com.utils.Constants;

import java.util.List;

/**
 * Created by Swapnil on 16-03-2017.
 */

public class ServiceImage extends Service
{
    public Runnable mRunnable = null;
    int count = 0;
    int countVideos = 0;
    SMDatabase smDatabase;
    List<Integer> idslist,idsVideoList;
    boolean showNotification = false;
    boolean showVideoNotification = false;

    public ServiceImage()
    {

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Get all the recods id from database
        smDatabase = new SMDatabase(getApplicationContext());
        idslist = smDatabase.getAllRecordIds();
        idsVideoList = smDatabase.getAllVideoRecordIds();

        callWebService();
        return super.onStartCommand(intent, flags, startId);
    }

    private void callWebService()
    {
        // Stop Web-Service call if user is Logout
        if (DataManager.getInstance().user != null)
        {
            if (count < idslist.size())
            {
                showNotification = true;
                // Remove this Toast
                //  Toast.makeText(getApplicationContext(), "Webservice Call : " + idslist.get(count), Toast.LENGTH_SHORT).show();
                BackgroundWebServices backgroundWebServices = new BackgroundWebServices(new BackgroundWebServiceListener()
                {
                    @Override
                    public void statusService(boolean PassOrFail)
                    {
                        if (PassOrFail)
                            smDatabase.deleteRecords(idslist.get(count));
                        count++;
                        callWebService();
                    }
                }, smDatabase.getRequiestType(idslist.get(count)), smDatabase.getRequiestData(idslist.get(count)),null);
            } else
            {
                // Remove this Toast
                // Toast.makeText(getApplicationContext(), "Webservice Stoped", Toast.LENGTH_SHORT).show();
                if (showNotification == true)
                {
                    if(count > 1){
                        generateNotification(1,getApplicationContext(), count+" Images uploaded successfully");
                    }else
                    {
                        generateNotification(1,getApplicationContext(), "Image uploaded successfully");
                    }
                }

                if(countVideos < idsVideoList.size())
                {
                    showVideoNotification = true;
                    BackgroundWebServices backgroundWebServices = new BackgroundWebServices(new BackgroundWebServiceListener()
                    {
                        @Override
                        public void statusService(boolean PassOrFail)
                        {
                            if (PassOrFail)
                                smDatabase.deleteVideoRecords(idsVideoList.get(countVideos));
                            countVideos++;
                            callWebService();
                        }
                    }, Constants.SaveVideos, null,smDatabase.getVideoRequiestData(idsVideoList.get(countVideos)));
                }else
                {
                    if (showVideoNotification == true)
                    {
                        if(countVideos > 1){
                            generateNotification(2,getApplicationContext(), countVideos+" Videos uploaded successfully");
                        }else
                        {
                            generateNotification(2,getApplicationContext(), "Video uploaded successfully");
                        }
                    }
                    stopSelf();
                }
            }
        } else
        {
            // Remove this Toast
            // Toast.makeText(getApplicationContext(), "Webservice Stoped", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
    }

    /**
     * Create a notification to inform the user that server has sent a message.
     */
    private void generateNotification(int notificationid,Context context, String message)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        //NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setAutoCancel(true).setWhen(System.currentTimeMillis()).setContentText(message);
       /* Intent notificationIntent;
        notificationIntent = new Intent(context, SplashActivity.class);
        notificationIntent.putExtra("title", "My Notifications");
        notificationIntent.putExtra("message", message.toLowerCase(Locale.getDefault()));

        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, notificationIntent, 0);
        builder.setContentIntent(contentIntent);*/

        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationid, builder.build());
    }
}
