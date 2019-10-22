package com.inved.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ManageChangingWork {

    private static final String KEY_CHANGE_WORK = "KEY_CHANGE_WORK";
    private static final String KEY_CAHNGE_WORK_DATA = "KEY_CHANGE_WORK_DATA";

    public static void saveUserWorkDecision(Context context, boolean isChanging) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_CHANGE_WORK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_CAHNGE_WORK_DATA, isChanging);
        editor.apply();
    }

    public static boolean getUserWorkDecision(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_CHANGE_WORK, Context.MODE_PRIVATE);
        return !sharedPreferences.getBoolean(KEY_CAHNGE_WORK_DATA, false);
    }
}
