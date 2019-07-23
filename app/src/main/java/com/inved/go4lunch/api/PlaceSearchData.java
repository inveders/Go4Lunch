package com.inved.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inved.go4lunch.R;
import com.inved.go4lunch.controller.RecyclerViewListViewRestaurant;
import com.inved.go4lunch.model.placesearch.PlaceSearch;
import com.inved.go4lunch.model.placesearch.Result;

import java.util.ArrayList;
import java.util.List;

import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_LIST_RESULT_PLACE_SEARCH;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_NAME;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHOTO_MAW_WIDTH;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHOTO_REFERENCE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PLACE_ID;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_RESTAURANT_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_RESTAURANT_LONGITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_SIZE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_VICINITY;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_SEARCH_DATA;

public class PlaceSearchData implements GooglePlaceCalls.Callbacks {




    Context context;
    List<Result> listResult;

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

            int resultSize = response.results.size();

            ArrayList restaurantName=new ArrayList();
            ArrayList photoReference=new ArrayList();
            ArrayList photoMaxWidth=new ArrayList();
            ArrayList restaurantLatitude=new ArrayList();
            ArrayList restaurantLongitude=new ArrayList();
            ArrayList photoResultSize=new ArrayList();
            ArrayList vicinity=new ArrayList();
            ArrayList placeId=new ArrayList();

            for (int i = 0; i < resultSize; i++) {

                restaurantName.add(response.getResults().get(i).getName());
                photoReference.add(response.getResults().get(i).getPhotos().get(0).getPhotoReference());
                photoMaxWidth.add(response.getResults().get(i).getPhotos().get(0).getWidth());
                photoResultSize.add(response.getResults().get(i).getPhotos().size());
                restaurantLatitude.add(response.getResults().get(i).getGeometry().getLocation().getLat());
                restaurantLongitude.add(response.getResults().get(i).getGeometry().getLocation().getLng());
                vicinity.add(response.getResults().get(i).getVicinity());
                placeId.add(response.getResults().get(i).getPlaceId());
            }

            sendRestaurantPlaceDataToOtherFragments(restaurantName,
                    photoReference,
                    photoMaxWidth,
                    restaurantLatitude,
                    restaurantLongitude,
                    vicinity,
                    resultSize,
                    placeId);



        }


    }

    @Override
    public void onFailure() {

    }


    public void sendRestaurantPlaceDataToOtherFragments(ArrayList restaurantName,
                                                        ArrayList photoReference,
                                                        ArrayList photoMaxWidth,
                                                        ArrayList restaurantLatitude,
                                                        ArrayList restaurantLongitude,
                                                        ArrayList vicinity,
                                                        int resultSize,
                                                        ArrayList placeId) {

      //  Log.d("Debago", "PlaceSearchData send Name : " + restaurantName);
        final Intent intent = new Intent(PLACE_SEARCH_DATA);
        intent.putExtra(PLACE_DATA_NAME, restaurantName);
        intent.putExtra(PLACE_DATA_PHOTO_REFERENCE, photoReference);
        intent.putExtra(PLACE_DATA_PHOTO_MAW_WIDTH, photoMaxWidth);
        intent.putExtra(PLACE_DATA_RESTAURANT_LATITUDE, restaurantLatitude);
        intent.putExtra(PLACE_DATA_RESTAURANT_LONGITUDE, restaurantLongitude);
        intent.putExtra(PLACE_DATA_VICINITY, vicinity);
        intent.putExtra(PLACE_DATA_SIZE, resultSize);
        intent.putExtra(PLACE_DATA_PLACE_ID, placeId);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }




}
