package com.smartmanager.android.live;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class SmartManagerApplication extends Application 
{
	public static final String TAG = SmartManagerApplication.class .getSimpleName();
	 
    private RequestQueue mRequestQueue;
    private static SmartManagerApplication mInstance;
    private static Context mAppContext;
    private static ImageLoader imageLoader;
	@Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        this.setAppContext(getApplicationContext());       
        initImageLoader();
    }
   
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context AppContext) {
        mAppContext = AppContext;
    }
    
    public static synchronized SmartManagerApplication getInstance() {
        return mInstance;
    }
 
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
 
        return mRequestQueue;
    }
 
    public static ImageLoader getImageLoader() {
		return imageLoader;
	}
 
    
    @SuppressWarnings("deprecation")
    private void initImageLoader() 
	{
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)	
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this).
				defaultDisplayImageOptions(defaultOptions)						
				.memoryCache(new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

    
    
}