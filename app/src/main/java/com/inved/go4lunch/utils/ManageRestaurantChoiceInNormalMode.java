package com.inved.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ManageRestaurantChoiceInNormalMode {

    private static final String KEY_RESTAURANT_CHOICE = "KEY_RESTAURANT_CHOICE";
    private static final String KEY_RESTAURANT_CHOICE_DATA = "KEY_RESTAURANT_CHOICE_DATA";
    private static final String KEY_RESTAURANT_NAME_DATA = "KEY_RESTAURANT_NAME_DATA";
    private static final String KEY_RESTAURANT_ADDRESS_DATA = "KEY_RESTAURANT_ADDRESS_DATA";

    public static void saveRestaurantChoice(Context context, String restaurantChoice) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_RESTAURANT_CHOICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_RESTAURANT_CHOICE_DATA, restaurantChoice);
        editor.apply();
    }

    public static void saveRestaurantName(Context context, String restaurantName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_RESTAURANT_CHOICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_RESTAURANT_NAME_DATA, restaurantName);
        editor.apply();
    }

    public static void saveRestaurantAddress(Context context, String restaurantAddress) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_RESTAURANT_CHOICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_RESTAURANT_ADDRESS_DATA, restaurantAddress);
        editor.apply();
    }

    public static String getRestaurantChoice(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_RESTAURANT_CHOICE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_RESTAURANT_CHOICE_DATA, null);
    }

    public static String getRestaurantChoiceName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_RESTAURANT_CHOICE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_RESTAURANT_NAME_DATA, null);
    }

    public static String getRestaurantChoiceAddress(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_RESTAURANT_CHOICE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_RESTAURANT_ADDRESS_DATA, null);
    }

}
