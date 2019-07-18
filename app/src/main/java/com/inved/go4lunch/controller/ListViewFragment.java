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

import com.inved.go4lunch.R;
import com.inved.go4lunch.api.GooglePlaceCalls;
import com.inved.go4lunch.model.placesearch.PlaceSearch;
import com.inved.go4lunch.model.placesearch.Result;

import java.util.List;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;

public class ListViewFragment extends Fragment implements GooglePlaceCalls.Callbacks {

    private View mView;
    private RecyclerViewListViewRestaurant mRecyclerListViewAdapter;
    RestaurantActivity gps = new RestaurantActivity();
    String myLastGeolocalisation=null;
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
                    executeHttpRequestWithRetrofit(myCurrentGeolocalisation);
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
        mRecyclerListViewAdapter = new RecyclerViewListViewRestaurant();
        mRecyclerListView.setAdapter(mRecyclerListViewAdapter);

        //Choose how to display the list in the RecyclerView (vertical or horizontal)
        mRecyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));

        return mView;
    }

// -------------------
    // HTTP REQUEST BY RETROFIT (Retrofit Way)
    // -------------------


    // 4 - Execute HTTP request and update UI
    void executeHttpRequestWithRetrofit(String geolocalisation){

        String type = "restaurant";
        int radius = 400;
        String keyword = "restaurant";
        String key = getText(R.string.google_maps_key).toString();

        GooglePlaceCalls.fetchPlaces(this,geolocalisation,radius, type,keyword,key);

    }


    // Override callback methods

    @Override
    public void onResponse(@Nullable PlaceSearch response) {
        // 2.1 - When getting response, we update UI
        if (response != null);

        assert response != null;
        mRecyclerListViewAdapter.setData(response.results);



    }


    @Override
    public void onFailure() {
        // 2.2 - When getting error, we update UI

    }


}
