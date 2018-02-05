package com.smartmanager.android.staging;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nw.interfaces.SMNotificationHandler;
import com.onesignal.OneSignal;
import com.onesignal.OneSignal.IdsAvailableHandler;
import com.smartmanager.android.R;
import com.utils.AppSession;
import com.utils.Helper;


public class SmartManagerApplication extends Application
{
    public static final String TAG = SmartManagerApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private static SmartManagerApplication mInstance;
    private static Context mAppContext;
    private static ImageLoader imageLoader;

    @Override
    public void onCreate()
    {
        super.onCreate();
        OneSignal.init(this, "790494337781", "06a1817b-6dad-4d2a-9e0d-98d5ba2e2055", new SMNotificationHandler());


        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new SMNotificationHandler())
                .init();
        OneSignal.enableInAppAlertNotification(true);

        OneSignal.idsAvailable(new IdsAvailableHandler()
        {
            @Override
            public void idsAvailable(String userId, String registrationId)
            {

                Helper.Log("debug", "User:" + userId);
                AppSession.saveDeviceID(getApplicationContext(), userId);
                if (registrationId != null)
                    Helper.Log("debug", "registrationId:" + registrationId);
            }
        });

        mInstance = this;
        this.setAppContext(getApplicationContext());
        initImageLoader();
    }


    public static Context getAppContext()
    {
        return mAppContext;
    }

    public void setAppContext(Context AppContext)
    {
        mAppContext = AppContext;
    }

    public static synchronized SmartManagerApplication getInstance()
    {
        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public static ImageLoader getImageLoader()
    {
        return imageLoader;
    }

    @SuppressWarnings("deprecation")
    private void initImageLoader()
    {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .considerExifParams(true)
                .showImageForEmptyUri(R.drawable.noimage)
                .showImageOnFail(R.drawable.noimage)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this).
                defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
