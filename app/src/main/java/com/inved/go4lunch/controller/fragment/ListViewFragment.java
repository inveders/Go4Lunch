package com.inved.go4lunch.controller.fragment;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.GooglePlaceCalls;
import com.inved.go4lunch.controller.activity.RecyclerViewListViewRestaurant;
import com.inved.go4lunch.model.placesearch.PlaceSearch;
import com.inved.go4lunch.utils.ManageAutocompleteResponse;

import java.util.Objects;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_SEARCH_DATA;

public class ListViewFragment extends Fragment implements GooglePlaceCalls.Callbacks {

    private RecyclerViewListViewRestaurant mRecyclerListViewAdapter;

    private String myLastGeolocalisation = null;
    private Double myCurrentLat;
    private Double myCurrentLongi;
    private FloatingActionButton filterButton;
    private String myCurrentGeolocalisation;



    //Receive current localisation from Localisation.class
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {
                intent.getSerializableExtra(KEY_GEOLOCALISATION);
                myCurrentGeolocalisation = intent.getStringExtra(KEY_GEOLOCALISATION);
                myCurrentLat = intent.getDoubleExtra(KEY_LATITUDE, 0.0);
                myCurrentLongi = intent.getDoubleExtra(KEY_LONGITUDE, 0.0);
                if (!(myCurrentGeolocalisation != null && myCurrentGeolocalisation.equals(myLastGeolocalisation))) {
                    executeHttpRequestPlaceSearchWithRetrofit(myCurrentGeolocalisation);
                    myLastGeolocalisation = myCurrentGeolocalisation;
                }

            }


        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_listview, container, false);


        //RecyclerView initialization
        RecyclerView mRecyclerListView = mView.findViewById(R.id.fragment_listview_recycler_view);
        mRecyclerListViewAdapter = new RecyclerViewListViewRestaurant(Glide.with(this));
        mRecyclerListView.setAdapter(mRecyclerListViewAdapter);

        //Choose how to display the list in the RecyclerView (vertical or horizontal)
        mRecyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerListView.addItemDecoration(new DividerItemDecoration(mRecyclerListView.getContext(), DividerItemDecoration.VERTICAL));
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_SEARCH_DATA));

        filterButton = mView.findViewById(R.id.fragment_list_view_sort_button);
        actionOnFloatingButton();
        initializeSharedPreferences();
        getRestaurantNameFromAutocomplete();


        // findCurrentPlaceRequest();
        return mView;
    }

    private void actionOnFloatingButton() {
        filterButton.setOnClickListener(view -> {
            FullScreenDialog dialog = new FullScreenDialog();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            dialog.show(ft, FullScreenDialog.TAG);
        });
    }

    private void executeHttpRequestPlaceSearchWithRetrofit(String geolocalisation) {

        if (geolocalisation != null) {
            String type = "restaurant";
            int radius = 400;
            String keyword = "restaurant";
            String key = "AIzaSyCYRQL4UOKKcszTAi6OeN8xCvZ7CuFtp8A";//context.getText(R.string.google_maps_key).toString();
            Log.d("Debago", "ListViewFragment retrofit Call ");
            GooglePlaceCalls.fetchPlaces(this, geolocalisation, radius, type, keyword, key);
        }

    }

    @Override
    public void onResponse(@Nullable PlaceSearch response) {
        assert response != null;

        String restaurantNameFromAutocomplete = ManageAutocompleteResponse.getStringAutocomplete(Objects.requireNonNull(getContext()), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_NAME);

        mRecyclerListViewAdapter.setData(response.results);

        Log.d("Debago", "ListViewFragment onResponse restaurantFromAutocomplete "+restaurantNameFromAutocomplete);
        if (restaurantNameFromAutocomplete != null) {
            mRecyclerListViewAdapter.getFilter().filter(restaurantNameFromAutocomplete);
        }

        mRecyclerListViewAdapter.setCurrentLocalisation(myCurrentLat, myCurrentLongi);
    }

    @Override
    public void onFailure() {

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("Debago", "ListViewFragment onResume ");
        getRestaurantNameFromAutocomplete();

    }

    private void getRestaurantNameFromAutocomplete() {

        String restaurantNameFromAutocomplete = ManageAutocompleteResponse.getStringAutocomplete(Objects.requireNonNull(getContext()), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_NAME);
        Log.d("Debago", "ListViewFragment getRestaurant restaurantFromAutocomplete "+restaurantNameFromAutocomplete);
        if (restaurantNameFromAutocomplete != null) {
            Log.d("Debago", "ListViewFragment getRestaurant in loop "+myCurrentGeolocalisation);
            if (myCurrentGeolocalisation != null) {
                executeHttpRequestPlaceSearchWithRetrofit(myCurrentGeolocalisation);
                myLastGeolocalisation = myCurrentGeolocalisation;
            }

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Debago", "ListViewFrqgment ondestroy ");
        //InitializeSharedPreferences
        initializeSharedPreferences();
}

    private void initializeSharedPreferences(){
        Log.d("Debago", "ListViewFrqgment initialize sharedpreferences ");
        ManageAutocompleteResponse.saveAutocompleteStringResponse(Objects.requireNonNull(getContext()), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_NAME, null);
        ManageAutocompleteResponse.saveAutocompleteStringResponse(Objects.requireNonNull(getContext()), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_ID, null);
        ManageAutocompleteResponse.saveAutocompleteLongResponseFromDouble(getContext(), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LATITUDE, 0);
        ManageAutocompleteResponse.saveAutocompleteLongResponseFromDouble(getContext(), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LONGITUDE, 0);


    }



}
