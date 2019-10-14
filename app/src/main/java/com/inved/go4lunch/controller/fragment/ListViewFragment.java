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
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.controller.activity.RestaurantActivity;
import com.inved.go4lunch.firebase.Restaurant;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageAutocompleteResponse;
import com.inved.go4lunch.utils.ManageJobPlaceId;
import com.inved.go4lunch.view.RecyclerViewListViewRestaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_SEARCH_DATA;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.TAG;

public class ListViewFragment extends Fragment {

    private RecyclerViewListViewRestaurant mRecyclerListViewAdapter;
    private RecyclerView mRecyclerListView;

    private String myLastGeolocalisation = null;
    private FloatingActionButton filterButton;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantPlaceId;
    private int restaurantCustomers;
    private int rating;
    private int openHours;
    private int closeHours;
    private int openMinutes;
    private int closeMinutes;
    private boolean isOpenForLunch;
    private String distance;
    private ArrayList<Restaurant> restaurantArrayList;
    private String jobPlaceId;


    //Receive current localisation from Localisation.class
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {
                intent.getSerializableExtra(KEY_GEOLOCALISATION);
                String myCurrentGeolocalisation = intent.getStringExtra(KEY_GEOLOCALISATION);

                if (!(myCurrentGeolocalisation != null && myCurrentGeolocalisation.equals(myLastGeolocalisation))) {
                    myLastGeolocalisation = myCurrentGeolocalisation;
                }

            }


        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_listview, container, false);

        jobPlaceId = ManageJobPlaceId.getJobPlaceId(App.getInstance().getApplicationContext());

        restaurantArrayList = new ArrayList<>();

        //RecyclerView initialization
        mRecyclerListView = mView.findViewById(R.id.fragment_listview_recycler_view);
        mRecyclerListView.setHasFixedSize(true);

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


    private void loadDataFromFirebase() {

        if (restaurantArrayList.size() > 0) {
            restaurantArrayList.clear();
        }

        RestaurantHelper.getAllRestaurants(jobPlaceId).get().addOnCompleteListener(task -> {

            if (task.getResult() != null) {
                for (DocumentSnapshot querySnapshot : task.getResult()) {
                    Restaurant restaurant = querySnapshot.toObject(Restaurant.class);

                    if (restaurant != null) {
                        restaurantPlaceId = restaurant.getRestaurantPlaceId();
                        restaurantCustomers = restaurant.getRestaurantCustomers();
                        restaurantName = restaurant.getRestaurantName();
                        rating = restaurant.getRatingApp();
                        isOpenForLunch = restaurant.getOpenForLunch();
                        distance = restaurant.getDistance();
                        openHours = restaurant.getOpenHours();
                        closeHours = restaurant.getCloseHours();
                        restaurantAddress = restaurant.getRestaurantAddress();
                        openMinutes = restaurant.getOpenMinutes();
                        closeMinutes = restaurant.getCloseMinutes();


                        Restaurant restaurantObject = new Restaurant(restaurantPlaceId, restaurantCustomers, 0, jobPlaceId,
                                restaurantName, rating, isOpenForLunch, distance, openHours, closeHours, restaurantAddress, 0.0, 0.0,
                                null, null, openMinutes, closeMinutes);

                        restaurantArrayList.add(restaurantObject);
                    }

                }

            }


            mRecyclerListViewAdapter = new RecyclerViewListViewRestaurant(Glide.with(App.getInstance().getApplicationContext()), restaurantArrayList);
            mRecyclerListView.setAdapter(mRecyclerListViewAdapter);
            mRecyclerListViewAdapter.setData(restaurantArrayList);
        }).addOnFailureListener(e -> Log.e("debago", "Problem during the load data"));
    }


    private void loadDataFromFirebaseFilter(String mQuery) {

        if (restaurantArrayList.size() > 0) {
            restaurantArrayList.clear();
        }

        RestaurantHelper.getFilterRestaurant(jobPlaceId, mQuery).get().addOnCompleteListener(task -> {

            if (task.getResult() != null) {
                for (DocumentSnapshot querySnapshot : task.getResult()) {
                    Restaurant restaurant = querySnapshot.toObject(Restaurant.class);

                    if (restaurant != null) {
                        restaurantPlaceId = restaurant.getRestaurantPlaceId();
                        restaurantCustomers = restaurant.getRestaurantCustomers();
                        restaurantName = restaurant.getRestaurantName();
                        rating = restaurant.getRatingApp();
                        isOpenForLunch = restaurant.getOpenForLunch();
                        distance = restaurant.getDistance();
                        openHours = restaurant.getOpenHours();
                        closeHours = restaurant.getCloseHours();
                        restaurantAddress = restaurant.getRestaurantAddress();
                        openMinutes = restaurant.getOpenMinutes();
                        closeMinutes = restaurant.getCloseMinutes();


                        Restaurant restaurantObject = new Restaurant(restaurantPlaceId, restaurantCustomers, 0, jobPlaceId,
                                restaurantName, rating, isOpenForLunch, distance, openHours, closeHours, restaurantAddress, 0.0, 0.0,
                                null, null, openMinutes, closeMinutes);

                        restaurantArrayList.add(restaurantObject);
                    }

                }

            }


            mRecyclerListViewAdapter = new RecyclerViewListViewRestaurant(Glide.with(App.getInstance().getApplicationContext()), restaurantArrayList);
            mRecyclerListView.setAdapter(mRecyclerListViewAdapter);
            mRecyclerListViewAdapter.setData(restaurantArrayList);
        }).addOnFailureListener(e -> Log.e("debago", "Problem during the filter"));
    }


    //COLLECTION SORT HERE
    private void actionOnFloatingButton() {
        filterButton.setOnClickListener(view -> {

            FullScreenDialog dialog = FullScreenDialog.newInstance();

            dialog.setCallback((ratingChoosen, openForLunchChoosen, customersNumberChoosen, distanceChoosen) -> {

                Log.d(TAG, "ListViewFragment restaurant size sort " + restaurantArrayList.size());
                if (restaurantArrayList.size() > 0) {
                    restaurantArrayList.clear();
                }

                RestaurantHelper.getAllRestaurants(jobPlaceId).get().addOnCompleteListener(task -> {

                    if (task.getResult() != null) {
                        for (DocumentSnapshot querySnapshot : task.getResult()) {
                            Restaurant restaurant = querySnapshot.toObject(Restaurant.class);

                            if (restaurant != null) {
                                restaurantPlaceId = restaurant.getRestaurantPlaceId();
                                restaurantCustomers = restaurant.getRestaurantCustomers();
                                restaurantName = restaurant.getRestaurantName();
                                rating = restaurant.getRatingApp();
                                isOpenForLunch = restaurant.getOpenForLunch();
                                distance = restaurant.getDistance();
                                openHours = restaurant.getOpenHours();
                                closeHours = restaurant.getCloseHours();
                                restaurantAddress = restaurant.getRestaurantAddress();
                                openMinutes = restaurant.getOpenMinutes();
                                closeMinutes = restaurant.getCloseMinutes();

                                Restaurant restaurantObject = new Restaurant(restaurantPlaceId, restaurantCustomers, 0, jobPlaceId,
                                        restaurantName, rating, isOpenForLunch, distance, openHours, closeHours, restaurantAddress, 0.0, 0.0,
                                        null, null, openMinutes, closeMinutes);

                                restaurantArrayList.add(restaurantObject);
                            }

                        }

                        Collections.sort(restaurantArrayList, Restaurant::compareTo);
                        mRecyclerListViewAdapter = new RecyclerViewListViewRestaurant(Glide.with(App.getInstance().getApplicationContext()), restaurantArrayList);
                        mRecyclerListView.setAdapter(mRecyclerListViewAdapter);
                        mRecyclerListViewAdapter.setData(restaurantArrayList);

                    } else {
                        Toast.makeText(getContext(), "No result found", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Log.e("debago", "Problem during the sort"));
            });

            if (getFragmentManager() != null) {
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

            if (restaurantNameFromAutocomplete.isEmpty()) {
                loadDataFromFirebase();
            } else {
                loadDataFromFirebaseFilter(restaurantNameFromAutocomplete);
                initializeSharedPreferences();
            }
        }else{
            loadDataFromFirebase();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();

        initializeSharedPreferences();
    }

    private void initializeSharedPreferences() {

        ManageAutocompleteResponse.saveAutocompleteStringResponse(Objects.requireNonNull(getContext()), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_NAME, null);


    }


}
