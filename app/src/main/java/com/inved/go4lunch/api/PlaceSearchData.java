package com.inved.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.inved.go4lunch.R;
import com.inved.go4lunch.controller.RecyclerViewListViewRestaurant;
import com.inved.go4lunch.model.placedetails.PlaceDetails;
import com.inved.go4lunch.model.placesearch.PlaceSearch;

public class PlaceSearchData implements GooglePlaceCalls.Callbacks {

    public static final String PLACE_SEARCH_DATA = "PLACE_SEARCH_DATA";
    public static final String PLACE_SEARCH_DATA_NAME = "NAME";
    public static final String PLACE_SEARCH_DATA_PHOTO_REFERENCE = "PHOTO_REFERENCE";
    public static final String PLACE_SEARCH_DATA_PHOTO_MAW_WIDTH = "PHOTO_MAX_WIDTH";

    RecyclerViewListViewRestaurant mRecyclerListViewAdapter;
    Context context;
    String geolocalisation;

    public void setGeolocalisation(String geolocalisation) {
        this.geolocalisation = geolocalisation;
        executeHttpRequestPlaceSearchWithRetrofit(geolocalisation);
    }

    private void executeHttpRequestPlaceSearchWithRetrofit(String geolocalisation) {

        if (geolocalisation != null) {
            String type = "restaurant";
            int radius = 400;
            String keyword = "restaurant";
            String key = "AIzaSyCYRQL4UOKKcszTAi6OeN8xCvZ7CuFtp8A";//context.getText(R.string.google_maps_key).toString();

            GooglePlaceCalls.fetchPlaces(this, geolocalisation, radius, type, keyword, key);
        }
    }

    @Override
    public void onResponse(@Nullable PlaceSearch response) {

        if (response != null) {

            assert response != null;
            Log.d("Debago", "PlaceSearchData onResponse, response est : " + response.results.size());
            //fragment listview
         //   mRecyclerListViewAdapter.setData(response.results);
            /**Pourquoi mon setData est nul?*/

            //VIEW PLACE ACTIVITY
            String nameRestaurant = response.getResults().get(0).getName();
            String photoReference = response.getResults().get(0).getPhotos().get(0).getPhotoReference();
            int photoMaxWidth = response.getResults().get(0).getPhotos().get(0).getWidth();
            sendRestaurantPlaceDataToOtherFragments(nameRestaurant, photoReference, photoMaxWidth);

            // viewPlaceName.setText(users.getResult().getFormattedPhoneNumber());
          /*  viewPlaceRestaurantType.setText(users.getResult().getTypes().toString());
            viewPlaceAdress.setText(users.getResult().getFormattedAddress());
         /*   if (APIClientGoogleSearch.currentResult.getPhotos() != null && APIClientGoogleSearch.currentResult.getPhotos().size() > 0) {
                glide.load(getPhotoOfPlace(APIClientGoogleSearch.currentResult.getPhotos().get(0).getPhotoReference(), 1000))
                        .placeholder(R.drawable.ic_android_blue_24dp)
                        .error(R.drawable.ic_error_red_24dp)
                        .into(viewPlacePhoto);
            }*/
        }


    }

    @Override
    public void onFailure() {

    }


    public void sendRestaurantPlaceDataToOtherFragments(String name, String photoReference, int photoMaxWidth) {

        Log.d("Debago", "PlaceSearchData send Name : " + name);
        final Intent intent = new Intent(PLACE_SEARCH_DATA);
        intent.putExtra(PLACE_SEARCH_DATA_NAME, name);
        intent.putExtra(PLACE_SEARCH_DATA_PHOTO_REFERENCE, photoReference);
        intent.putExtra(PLACE_SEARCH_DATA_PHOTO_MAW_WIDTH, photoMaxWidth);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }
}
