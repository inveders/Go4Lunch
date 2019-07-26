package com.inved.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.inved.go4lunch.api.GooglePlaceDetailsCalls;
import com.inved.go4lunch.controller.ViewPlaceActivity;
import com.inved.go4lunch.model.placedetails.PlaceDetails;

import static com.inved.go4lunch.controller.MapFragment.POSITION_ARRAY_LIST;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_NAME;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHONE_NUMBER;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHOTO_REFERENCE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PLACE_ID;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_VICINITY;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DETAIL_DATA;

public class PlaceDetailsData implements GooglePlaceDetailsCalls.CallbacksDetail {



    Context context;
    private String placeId;

    public void setPlaceId(String placeId) {
        this.placeId = placeId;

        executeHttpRequestPlaceDetailsWithRetrofit(placeId);
    }


    private void executeHttpRequestPlaceDetailsWithRetrofit(String currentPlaceId) {

        if (currentPlaceId != null) {
            String key = "AIzaSyCYRQL4UOKKcszTAi6OeN8xCvZ7CuFtp8A";// getText(R.string.google_maps_key).toString();
            String fields = "type,formatted_phone_number,opening_hours,photo,name,vicinity,place_id";

            GooglePlaceDetailsCalls.fetchPlaceDetail(this, currentPlaceId, key, fields);
        }
    }

    @Override
    public void onResponse(@Nullable PlaceDetails response) {

        assert response != null;
        if (response.getResult() != null) {


            String numberPhone = response.getResult().getFormattedPhoneNumber();
            String photoReference = response.getResult().getPhotos().get(0).getPhotoReference();
            String name = response.getResult().getName();
            String vicinity = response.getResult().getVicinity();
            String placeId = response.getResult().getPlaceId();
            sendRestaurantDetailDataToOtherFragments(numberPhone,photoReference,name,vicinity,placeId);


        } else {
            Log.d("Debago", "ViewPlaceActivity onResponse : users est null");
        }

    }

    @Override
    public void onFailure() {

    }


    public void sendRestaurantDetailDataToOtherFragments(String numberPhone,String photoreference,String name, String vicinity,String placeId) {

        final Intent intent = new Intent(PLACE_DETAIL_DATA);
        intent.putExtra(PLACE_DATA_PHONE_NUMBER, numberPhone);
        intent.putExtra(PLACE_DATA_PHOTO_REFERENCE, photoreference);
        intent.putExtra(PLACE_DATA_NAME, name);
        intent.putExtra(PLACE_DATA_VICINITY, vicinity);
        intent.putExtra(PLACE_DATA_PLACE_ID, placeId);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }
}
