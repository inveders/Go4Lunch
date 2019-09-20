package com.inved.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.inved.go4lunch.R;
import com.inved.go4lunch.utils.App;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_DATA_ADDRESS;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_DATA_NAME;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_DATA_OPENING_HOURS;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_DATA_PHONE_NUMBER;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_DATA_PLACE_ID;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_DATA_RATING;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_DATA_WEBSITE;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_DETAIL_DATA;

public class PlaceDetailsData{

    private static final String TAG = "Debago";
    protected Context context;
    private String placeId;

    public void setPlaceId(String placeId) {
        this.placeId = placeId;

    }




}
