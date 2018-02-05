package com.nw.showcase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ShowcaseSessions {
	
	public static boolean isSessionAvailable(Context context,String key) 
	{
		SharedPreferences preferences;
		preferences= PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(key, false);
	}
	
	public static void saveSession(Context context,String key) 
	{
		SharedPreferences.Editor editor;
		SharedPreferences preferences;
		preferences= PreferenceManager.getDefaultSharedPreferences(context);
		editor= preferences.edit();
		editor.putBoolean(key, true).commit();
	}
	
	
	
}
