package com.inved.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
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

       fetchPlaceRequest(placeId);
    }


    private void fetchPlaceRequest(String currentPlaceId) {

        // Initialize Places.
        Places.initialize(App.getInstance().getApplicationContext(), App.getResourses().getString(R.string.google_api_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(App.getInstance().getApplicationContext());

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHONE_NUMBER,
                Place.Field.ADDRESS,
                Place.Field.WEBSITE_URI,
                Place.Field.RATING);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(currentPlaceId, placeFields)
                .build();


        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            double rating=0;
            String phoneNumber = place.getPhoneNumber();
            String name = place.getName();
            String address = place.getAddress();
            String website = Objects.requireNonNull(place.getWebsiteUri()).toString();
            OpeningHours openingHours = place.getOpeningHours();
            Double ratingApi = place.getRating();
            if (ratingApi != null) {
                rating = ratingApi;
            }


            sendRestaurantDetailDataToOtherFragments(phoneNumber,name,address,placeId,website,openingHours,rating);

        //    Log.d(TAG, "currentplaceaID fetchplace address " + address);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {

                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });


    }


    private void sendRestaurantDetailDataToOtherFragments(String numberPhone, String name, String address, String placeId, String website, OpeningHours openingHours, double rating) {

        final Intent intent = new Intent(PLACE_DETAIL_DATA);
        intent.putExtra(PLACE_DATA_PHONE_NUMBER, numberPhone);
        intent.putExtra(PLACE_DATA_NAME, name);
        intent.putExtra(PLACE_DATA_ADDRESS, address);
        intent.putExtra(PLACE_DATA_PLACE_ID, placeId);
        intent.putExtra(PLACE_DATA_WEBSITE, website);
        intent.putExtra(PLACE_DATA_OPENING_HOURS,openingHours);
        intent.putExtra(PLACE_DATA_RATING,rating);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }
}
