package com.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppSession {
    public static final String TAG = AppSession.class.getSimpleName();
    public static final String LOGIN_STATUS = "LOGIN";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static final String CLIENT_IMAGE = "CLIENT_IMAGE";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String LOG_COUNT = "log_count";
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String IMAGE_DETAILS = "IMAGE_DETAILS";
    public static final String IS_SUBSCRIBE = "SUBSCRIBE";

    public void saveLoginStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGIN_STATUS, true);
        editor.commit();
    }

    public static void saveLoginStatus(Context context, boolean status) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGIN_STATUS, status);
        editor.commit();
    }

    public static boolean isLoginAvailabe(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(LOGIN_STATUS, false);
    }

    public static void saveLogin(Context context, String userName, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, userName);
        editor.putString(USER_PASSWORD, password);
        editor.putBoolean(LOGIN_STATUS, true);
        editor.commit();
    }

    public static String[] getLogin(Context context) {
        String[] arr = new String[2];
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        arr[0] = sharedPreferences.getString(USER_NAME, "");
        arr[1] = sharedPreferences.getString(USER_PASSWORD, "");
        return arr;
    }

    public static void saveClientImage(Context context, String clientImageUrl, int clientId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(CLIENT_IMAGE, clientImageUrl);
        editor.putInt(CLIENT_ID, clientId);
        editor.commit();
    }

    public static void saveDeviceID(Context context, String deviceID) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(DEVICE_ID, deviceID);
        editor.commit();
    }

    public static String getDeviceID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DEVICE_ID, "");
    }

    public static String getClientImage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CLIENT_IMAGE, null);
    }

    public static int getClientID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(CLIENT_ID, 0);
    }

    public static void addLogCount(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        int count = sharedPreferences.getInt(LOG_COUNT, 0);
        if (count >= 5)
        {
            count = 0;
            editor.putInt(LOG_COUNT, 0);
        } else
        {
            editor.putInt(LOG_COUNT, (count + 1));
        }
        editor.commit();
    }

    public static int getLogCount(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(LOG_COUNT, 0);
    }

    public static void saveImageDetails(Context context, String imageDetails) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE_DETAILS, imageDetails);
        editor.commit();
    }

    public static String getImageDetails(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(IMAGE_DETAILS, "");
    }


    public static void saveSubscription(Context context, String subscription) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(IS_SUBSCRIBE, subscription);
        editor.commit();
    }

    public static String getSubscription(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(IS_SUBSCRIBE, "");
    }
}
