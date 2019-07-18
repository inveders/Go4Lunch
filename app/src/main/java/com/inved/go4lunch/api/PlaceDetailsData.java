package com.inved.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.inved.go4lunch.api.GooglePlaceDetailsCalls;
import com.inved.go4lunch.controller.ViewPlaceActivity;
import com.inved.go4lunch.model.placedetails.PlaceDetails;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LONGITUDE;

public class PlaceDetailsData implements GooglePlaceDetailsCalls.CallbacksDetail {

    public static final String PLACE_DETAIL_DATA = "PLACE_DETAIL_DATA";
    public static final String PLACE_DETAIL_DATA_PHONE_NUMBER = "PLACE_DETAIL_DATA_PHONE_NUMBER";
    Context context;
    String placeId;

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
        executeHttpRequestPlaceDetailsWithRetrofit(placeId);
    }

    private void executeHttpRequestPlaceDetailsWithRetrofit(String currentPlaceId) {

        if (currentPlaceId != null) {
            String key = "AIzaSyCYRQL4UOKKcszTAi6OeN8xCvZ7CuFtp8A";// getText(R.string.google_maps_key).toString();
            String fields = "photo,type,formatted_phone_number,opening_hours";
            Log.d("Debago", "ViewPlaceActivity onexecuteretrofit : " + currentPlaceId);
            GooglePlaceDetailsCalls.fetchPlaceDetail(this, currentPlaceId, key, fields);
        }
    }

    @Override
    public void onResponse(@Nullable PlaceDetails response) {

        assert response != null;
        if (response.getResult() != null) {


            String numberPhone = response.getResult().getFormattedPhoneNumber();
           // String photoReference = users.getResult()
            sendRestaurantDetailDataToOtherFragments(numberPhone);

            // viewPlaceName.setText(users.getResult().getFormattedPhoneNumber());
          /*  viewPlaceRestaurantType.setText(users.getResult().getTypes().toString());
            viewPlaceAdress.setText(users.getResult().getFormattedAddress());
         /*   if (APIClientGoogleSearch.currentResult.getPhotos() != null && APIClientGoogleSearch.currentResult.getPhotos().size() > 0) {
                glide.load(getPhotoOfPlace(APIClientGoogleSearch.currentResult.getPhotos().get(0).getPhotoReference(), 1000))
                        .placeholder(R.drawable.ic_android_blue_24dp)
                        .error(R.drawable.ic_error_red_24dp)
                        .into(viewPlacePhoto);
            }*/

       /*     viewPlaceCallImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /**ICI LANCER UN APPEL TELEPHONIQUE DEPUIS L'APPAREIL*/


            //      }
            //     });

            //  startViewPlaceActivity();
        } else {
            Log.d("Debago", "ViewPlaceActivity onResponse : users est null");
        }

    }

    @Override
    public void onFailure() {

    }


    public void sendRestaurantDetailDataToOtherFragments(String numberPhone) {

        final Intent intent = new Intent(PLACE_DETAIL_DATA);
        intent.putExtra(PLACE_DETAIL_DATA_PHONE_NUMBER, numberPhone);
      /*  intent.putExtra(KEY_LATITUDE, getLatitude());
        intent.putExtra(KEY_LONGITUDE, getLongitude());*/
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }
}
