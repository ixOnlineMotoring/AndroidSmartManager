package com.smartmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.nw.service.ServiceImage;
import com.smartmanager.android.R;
import com.utils.AppSession;
import com.utils.Helper;

import java.text.DateFormat;
import java.util.Date;


public class SplashActivity extends Activity
{

    Handler mHideHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (AppSession.getLogCount(SplashActivity.this) >= 5)
        {
            AppSession.addLogCount(SplashActivity.this);
            Helper.flushFileContents();
        } else
        {
            String s = "Debug-infos:";
            s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
            s += "\n OS API Level: " + android.os.Build.VERSION.RELEASE + "(" + android.os.Build.VERSION.SDK_INT + ")";
            s += "\n Device: " + android.os.Build.DEVICE;
            s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";
            Helper.Log(getString(R.string.app_name), "----------Application Start ===>" + DateFormat.getDateTimeInstance().format(new Date()) + "\n\n" + s);

            AppSession.addLogCount(SplashActivity.this);
        }

		/*PushManager pushManager= new PushManager(SplashActivity.this);
        pushManager.registerDevice();*/

        mHideHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // start login screen
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("fromsplash", false);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        }, 2000);
    }
}
