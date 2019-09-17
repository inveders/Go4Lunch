package com.inved.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ManageAutocompleteResponse {

    private static final String KEY_AUTOCOMPLETE_RESPONSE = "KEY_AUTOCOMPLETE_RESPONSE";
    public static final String KEY_AUTOCOMPLETE_PLACE_ID = "KEY_AUTOCOMPLETE_PLACE_ID";
    public static final String KEY_AUTOCOMPLETE_PLACE_NAME = "KEY_AUTOCOMPLETE_PLACE_NAME";
    public static final String KEY_AUTOCOMPLETE_LATITUDE = "KEY_AUTOCOMPLETE_LATITUDE";
    public static final String KEY_AUTOCOMPLETE_LONGITUDE = "KEY_AUTOCOMPLETE_LONGITUDE";

    public static void saveAutocompleteStringResponse(Context context, String key, String autocompleteData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_AUTOCOMPLETE_RESPONSE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, autocompleteData);
        editor.apply();
    }

    public static void saveAutocompleteLongResponseFromDouble(Context context,String key, double autocompleteData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_AUTOCOMPLETE_RESPONSE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, Double.doubleToRawLongBits(autocompleteData));
        editor.apply();
    }



    public static String getStringAutocomplete(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_AUTOCOMPLETE_RESPONSE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }

    public static double getDoubleAutocomplete(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_AUTOCOMPLETE_RESPONSE, Context.MODE_PRIVATE);
        return Double.longBitsToDouble(sharedPreferences.getLong(key,0));
    }
}
