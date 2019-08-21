package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.GooglePlaceCalls;
import com.inved.go4lunch.model.placesearch.PlaceSearch;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DETAIL_DATA;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_SEARCH_DATA;

public class ListViewFragment extends Fragment implements GooglePlaceCalls.Callbacks{

    private View mView;

    private RecyclerViewListViewRestaurant mRecyclerListViewAdapter;
    RestaurantActivity gps = new RestaurantActivity();
    String myLastGeolocalisation=null;
    private Double myCurrentLat;
    private Double myCurrentLongi;
    RestaurantActivity restaurantActivity = new RestaurantActivity();
    private int numberResult;
    private int z;
    private PlaceLikelihood placeLikelihood;
    SearchView searchView;
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
                myCurrentLat = intent.getDoubleExtra(KEY_LATITUDE, 0.0);
                myCurrentLongi = intent.getDoubleExtra(KEY_LONGITUDE, 0.0);
                if(myCurrentGeolocalisation.equals(myLastGeolocalisation)){

                }
                else{

                //    placeSearchData.setGeolocalisation(myCurrentGeolocalisation);
                 executeHttpRequestPlaceSearchWithRetrofit(myCurrentGeolocalisation);
                    myLastGeolocalisation=myCurrentGeolocalisation;

                }

            }

            if (PLACE_DETAIL_DATA.equals(intent.getAction())) {
           /*     phoneNumber = intent.getStringExtra(PLACE_DATA_PHONE_NUMBER);
                photoreference = intent.getStringExtra(PLACE_DATA_PHOTO_METADATA);
                restaurantName = intent.getStringExtra(PLACE_DATA_NAME);
                vicinity = intent.getStringExtra(PLACE_DATA_VICINITY);*/

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
        mRecyclerListView.addItemDecoration(new DividerItemDecoration(mRecyclerListView.getContext(), DividerItemDecoration.VERTICAL));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_SEARCH_DATA));



       // findCurrentPlaceRequest();
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

  /*  @SuppressLint("MissingPermission")
    private void findCurrentPlaceRequest(){

        // Initialize Places.
        Places.initialize(getContext(), getString(R.string.google_api_key));
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getContext());

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,Place.Field.ID,Place.Field.LAT_LNG,Place.Field.TYPES);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

        placesClient.findCurrentPlace(request).addOnSuccessListener(((response) -> {
         //   for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {

                for (int y = 0; y < response.getPlaceLikelihoods().size(); y++) {
                    List typesList = response.getPlaceLikelihoods().get(y).getPlace().getTypes();


                    assert typesList != null;
                    for (int i = 0; i<typesList.size(); i++){
                        if("RESTAURANT".equals(typesList.get(i).toString())){
                            numberResult++;
                          //  Log.d("Debago", "RecyclerViewRestaurant taille list1 "+response.getPlaceLikelihoods().get(y).getPlace().getName()+" et y "+y);
                            HashMap<String,Object> data = new HashMap<>();
                            data.put("name",placeLikelihood.getPlace().getName());

                            mRecyclerListViewAdapter.setData(data,numberResult);

                        }

                    }

                }
          //  launchRecyclerView(response.getPlaceLikelihoods(),numberResult);


          //  }





        })).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });

    }

  /*  private void launchRecyclerView(List<PlaceLikelihood> placeLikelihood,int numberResult,int position) {
   //     mRecyclerListViewAdapter.setData(placeLikelihood,numberResult,position);
        mRecyclerListViewAdapter.setCurrentLocalisation(myCurrentLat,myCurrentLongi);
    }*/




    @Override
    public void onResponse(@Nullable PlaceSearch response) {
        mRecyclerListViewAdapter.setData(response.results);
        mRecyclerListViewAdapter.setCurrentLocalisation(myCurrentLat,myCurrentLongi);
}

    @Override
    public void onFailure() {

    }

// -------------------
    // HTTP REQUEST BY RETROFIT (Retrofit Way)
    // -------------------


}
