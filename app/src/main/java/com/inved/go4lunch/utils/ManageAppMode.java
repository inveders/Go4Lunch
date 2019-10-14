package com.inved.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ManageAppMode {

    private static final String KEY_APP_MODE = "KEY_APP_MODE";
    private static final String KEY_APP_MODE_DATA = "KEY_APP_MODE_DATA";

    public static void saveAppMode(Context context, String appMode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_APP_MODE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_APP_MODE_DATA, appMode);
        editor.apply();
    }

    public static String getAppMode(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_APP_MODE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_APP_MODE_DATA,"work");
    }
}
