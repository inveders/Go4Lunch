package com.inved.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_JOB_PLACE_ID;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_JOB_PLACE_ID_DATA;

public class ManageJobPlaceId {


    public static void saveJobPlaceId(Context context,String key,String jobPlaceId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_JOB_PLACE_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, jobPlaceId);
                editor.apply();
    }

    public static String getJobPlaceId(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_JOB_PLACE_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }



}
