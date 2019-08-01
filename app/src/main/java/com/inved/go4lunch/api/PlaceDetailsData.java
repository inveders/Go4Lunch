package com.inved.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.inved.go4lunch.R;
import com.inved.go4lunch.controller.ViewPlaceActivity;
import com.inved.go4lunch.utils.App;

import java.util.Arrays;
import java.util.List;

import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_ADDRESS;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_NAME;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHONE_NUMBER;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHOTO_BITMAP;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PLACE_ID;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_WEBSITE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DETAIL_DATA;

public class PlaceDetailsData{

    public static final String TAG = "Debago";
    Context context;
    private String placeId;

    public void setPlaceId(String placeId) {
        this.placeId = placeId;

       fetchPlaceRequest(placeId);
    }


    public void fetchPlaceRequest(String currentPlaceId) {

        // Initialize Places.
        Places.initialize(App.getInstance().getApplicationContext(), App.getResourses().getString(R.string.google_api_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(App.getInstance().getApplicationContext());

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHONE_NUMBER,
                Place.Field.ADDRESS,
                Place.Field.WEBSITE_URI);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(currentPlaceId, placeFields)
                .build();


        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            String phoneNumber = place.getPhoneNumber();
            String name = place.getName();
            String address = place.getAddress();
            String website = place.getWebsiteUri().toString();

            sendRestaurantDetailDataToOtherFragments(phoneNumber,name,address,placeId,website);

            Log.d(TAG, "currentplaceaID fetchplace address " + address);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });


    }

    private void sendRestaurantDetailPhotoToOtherFragments(Bitmap bitmap) {
        Intent intent = new Intent(App.getInstance().getApplicationContext(), ViewPlaceActivity.class);
        intent.putExtra(PLACE_DATA_PHOTO_BITMAP, bitmap);
    }


    public void sendRestaurantDetailDataToOtherFragments(String numberPhone,String name, String address,String placeId,String website) {

        final Intent intent = new Intent(PLACE_DETAIL_DATA);
        intent.putExtra(PLACE_DATA_PHONE_NUMBER, numberPhone);
        intent.putExtra(PLACE_DATA_NAME, name);
        intent.putExtra(PLACE_DATA_ADDRESS, address);
        intent.putExtra(PLACE_DATA_PLACE_ID, placeId);
        intent.putExtra(PLACE_DATA_WEBSITE, website);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }
}
