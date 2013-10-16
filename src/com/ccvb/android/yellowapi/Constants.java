package com.ccvb.android.yellowapi;

import android.content.Context;
import android.provider.Settings.Secure;

public class Constants
{
	public static final String YELLOW_API_KEY = "vf9g25p2348ewxwtky962s22";
	public static String YELLOW_API_UID;
	
	public static void init(Context context)
	{
		Constants.YELLOW_API_UID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}
}