package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.GooglePlaceCalls;
import com.inved.go4lunch.api.PlaceSearchData;
import com.inved.go4lunch.model.placesearch.PlaceSearch;
import com.inved.go4lunch.model.placesearch.Result;

import java.util.List;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_LIST_RESULT_PLACE_SEARCH;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_NAME;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PLACE_ID;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_RESTAURANT_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_RESTAURANT_LONGITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_SIZE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_SEARCH_DATA;

public class ListViewFragment extends Fragment implements GooglePlaceCalls.Callbacks {

    private View mView;
    private List<Result> listResultPlaceSearch;
    private RecyclerViewListViewRestaurant mRecyclerListViewAdapter;
    RestaurantActivity gps = new RestaurantActivity();
    String myLastGeolocalisation=null;
    
    PlaceSearchData placeSearchData = new PlaceSearchData();

    //Receive current localisation from Localisation.class
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction()))
            {
                intent.getSerializableExtra(KEY_GEOLOCALISATION);
                String myCurrentGeolocalisation=intent.getStringExtra(KEY_GEOLOCALISATION);

                if(myCurrentGeolocalisation.equals(myLastGeolocalisation)){

                }
                else{

                //    placeSearchData.setGeolocalisation(myCurrentGeolocalisation);
        executeHttpRequestPlaceSearchWithRetrofit(myCurrentGeolocalisation);
                    myLastGeolocalisation=myCurrentGeolocalisation;

                }

            }

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView =inflater.inflate(R.layout.fragment_listview,container,false);

        //RecyclerView initialization
        RecyclerView mRecyclerListView = mView.findViewById(R.id.fragment_listview_recycler_view);
        mRecyclerListViewAdapter = new RecyclerViewListViewRestaurant(Glide.with(this));
        mRecyclerListView.setAdapter(mRecyclerListViewAdapter);

        //Choose how to display the list in the RecyclerView (vertical or horizontal)
        mRecyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_SEARCH_DATA));


        return mView;
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
        mRecyclerListViewAdapter.setData(response.results);
    }

    @Override
    public void onFailure() {

    }

// -------------------
    // HTTP REQUEST BY RETROFIT (Retrofit Way)
    // -------------------


}
