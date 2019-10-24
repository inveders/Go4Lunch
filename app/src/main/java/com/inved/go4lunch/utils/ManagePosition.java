package com.inved.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ManagePosition {

    private static final String KEY_POSITION = "KEY_POSITION";
    public static final String KEY_POSITION_DATA = "KEY_POSITION_DATA";
    public static final String KEY_POSITION_JOB_LAT_LNG_DATA = "KEY_POSITION_JOB_LAT_LNG_DATA";

    public static void savePosition(Context context, String position,String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_POSITION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, position);
        editor.apply();
    }

    public static String getPosition(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_POSITION, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"0.0,0.0");
    }

}
