package com.inved.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ManagedNotificationEnabled {

    private static final String KEY_NOTIFICATION = "KEY_NOTIFICATION";
    private static final String KEY_NOTIFICATION_ENABLED = "KEY_NOTIFICATION_ENABLED";

    public static void saveNotificationEnabled(Context context, Boolean isNotificationEnabled) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_NOTIFICATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NOTIFICATION_ENABLED, isNotificationEnabled);
        editor.apply();
    }

    public static Boolean getNotificationStatus(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_NOTIFICATION, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED,true);
    }

}
