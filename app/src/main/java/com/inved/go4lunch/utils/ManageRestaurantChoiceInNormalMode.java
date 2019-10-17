package com.inved.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ManageRestaurantChoiceInNormalMode {

    private static final String KEY_RESTAURANT_CHOICE = "KEY_RESTAURANT_CHOICE";
    private static final String KEY_RESTAURANT_CHOICE_DATA = "KEY_RESTAURANT_CHOICE_DATA";

    public static void saveRestaurantChoice(Context context, String restaurantChoice) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_RESTAURANT_CHOICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_RESTAURANT_CHOICE_DATA, restaurantChoice);
        editor.apply();
    }

    public static String getRestaurantChoice(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_RESTAURANT_CHOICE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_RESTAURANT_CHOICE_DATA, null);
    }

}
