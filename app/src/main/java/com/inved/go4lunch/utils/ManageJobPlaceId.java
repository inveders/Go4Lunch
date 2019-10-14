package com.inved.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ManageJobPlaceId {

    public static final String KEY_JOB_PLACE_ID = "KEY_JOB_PLACE_ID";
    public static final String KEY_JOB_PLACE_ID_DATA = "KEY_JOB_PLACE_ID_DATA";

    public static void saveJobPlaceId(Context context,String jobPlaceId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_JOB_PLACE_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_JOB_PLACE_ID_DATA, jobPlaceId);
                editor.apply();
    }

    public static String getJobPlaceId(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_JOB_PLACE_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_JOB_PLACE_ID_DATA,null);
    }



}
