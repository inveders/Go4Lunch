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
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.GooglePlaceCalls;
import com.inved.go4lunch.model.placesearch.Result;
import com.inved.go4lunch.view.RecyclerViewListViewRestaurant;
import com.inved.go4lunch.controller.activity.RestaurantActivity;
import com.inved.go4lunch.model.placesearch.PlaceSearch;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageAutocompleteResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_SEARCH_DATA;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.TAG;

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

        ((RestaurantActivity) Objects.requireNonNull(getActivity())).setFragmentRefreshListener(this::getRestaurantNameFromAutocomplete);

        return mView;
    }

    private void actionOnFloatingButton() {
        filterButton.setOnClickListener(view -> {
            FullScreenDialog dialog = new FullScreenDialog();
            if(getFragmentManager()!=null){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, FullScreenDialog.TAG);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getRestaurantNameFromAutocomplete();

    }

    private void getRestaurantNameFromAutocomplete() {

        String restaurantNameFromAutocomplete = ManageAutocompleteResponse.getStringAutocomplete((App.getInstance().getApplicationContext()), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_NAME);

        if (restaurantNameFromAutocomplete != null) {

            double latitude = ManageAutocompleteResponse.getDoubleAutocomplete(App.getInstance().getApplicationContext(), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LATITUDE);

            double longitude = ManageAutocompleteResponse.getDoubleAutocomplete(App.getInstance().getApplicationContext(), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LONGITUDE);
            String lat = String.valueOf(latitude);
            String longi=String.valueOf(longitude);
            myCurrentGeolocalisation=""+lat+","+longi+"";

            executeHttpRequestPlaceSearchWithRetrofit(myCurrentGeolocalisation);

        }
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
        assert response != null;


       /* Collections.sort(response.results, (one, two) -> {
            if(Integer.valueOf(one.getName()) > Integer.valueOf(two.getName())) {
                return -1;
            } else {
                return 1;
            }

            public int compareTo(Result o){

            }
        });*/
/** BEGINNING TO SORT MY LIST. NOW HOW TO PUT IT IN THE ADAPTER? IN WHAT CONDITION, HOW TO LINK IT WITH THE FULLSCREEN DIALOG?
        Collections.sort(response.results, Comparator.comparing(Result::getRating)
                .thenComparing(Result::getOpeningHours)
                .thenComparing(Result::getOpeningHours)
                .thenComparing(Result::getGeometry)
                .thenComparing(Result::getPriceLevel));
*/


        String restaurantNameFromAutocomplete = ManageAutocompleteResponse.getStringAutocomplete((App.getInstance().getApplicationContext()), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_NAME);


        mRecyclerListViewAdapter.setData(response.results);


        Log.d(TAG,"ListViewFragment restaurantName "+restaurantNameFromAutocomplete);
        if (restaurantNameFromAutocomplete != null) {
            mRecyclerListViewAdapter.getFilter().filter(restaurantNameFromAutocomplete);
        }else{

            mRecyclerListViewAdapter.getFilter().filter("");
        }

        mRecyclerListViewAdapter.setCurrentLocalisation(myCurrentLat, myCurrentLongi);


        initializeSharedPreferences();
    }

    @Override
    public void onFailure() {

    }






    @Override
    public void onDetach() {
        super.onDetach();

        initializeSharedPreferences();
}

    private void initializeSharedPreferences(){

        ManageAutocompleteResponse.saveAutocompleteStringResponse(Objects.requireNonNull(getContext()), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_NAME, null);


    }



}
