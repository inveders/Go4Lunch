package com.inved.go4lunch.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.inved.go4lunch.R;
import com.inved.go4lunch.controller.activity.RestaurantActivity;
import com.inved.go4lunch.firebase.Restaurant;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.firebase.RestaurantInNormalModeHelper;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageAppMode;
import com.inved.go4lunch.utils.ManageAutocompleteResponse;
import com.inved.go4lunch.utils.ManageJobPlaceId;
import com.inved.go4lunch.view.RecyclerViewListViewRestaurant;
import com.inved.go4lunch.view.RecyclerViewListViewRestaurantSorted;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.PLACE_SEARCH_DATA;

public class ListViewFragment extends Fragment implements RecyclerViewListViewRestaurant.Listener {

    private RecyclerViewListViewRestaurant mRecyclerListViewAdapter;
    private RecyclerViewListViewRestaurantSorted mRecyclerListViewSortedAdapter;
    private RecyclerView mRecyclerListView;
    private TextView textViewNoRestaurantFound;
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
    private long distance;
    private ArrayList<Restaurant> restaurantArrayList;
    private Context context = App.getInstance().getApplicationContext();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String appMode=ManageAppMode.getAppMode(App.getInstance().getApplicationContext());

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

        restaurantArrayList = new ArrayList<>();

        //DECLARE ET IITIALIZE TEXTVIEW
        textViewNoRestaurantFound = mView.findViewById(R.id.fragment_list_view_textview_no_restaurant);
        mSwipeRefreshLayout = mView.findViewById(R.id.swipeRefreshLayout);

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

        mSwipeRefreshLayout.setOnRefreshListener(() -> mRecyclerListViewAdapter.refresh());

        if(getActivity()!=null){
            ((RestaurantActivity)getActivity()).setFragmentRefreshListener(this::getRestaurantNameFromAutocomplete);
        }


        return mView;
    }


    private void loadDataFromFirebase() {

        if (ManageAppMode.getAppMode(context).equals(getString(R.string.app_mode_work)) || ManageAppMode.getAppMode(context).equals(getString(R.string.app_mode_forced_work))) {
            displayAllRestaurantsInWorkMode();
        } else {
            displayAllRestaurantsInNormalMode();
        }

    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem searchitem = menu.findItem(R.id.action_search);
        MenuItem clearItem = menu.findItem(R.id.action_clear);
        if (searchitem != null) {
            searchitem.setVisible(false);
        }
        if (clearItem != null) {
            clearItem.setVisible(true);
            clearItem.setOnMenuItemClickListener(menuItem -> {

              //  ((RestaurantActivity) Objects.requireNonNull(getActivity())).setFragmentRefreshListenerInterfaceCallback(this::getRestaurantNameFromAutocomplete);
                menu.findItem(R.id.action_search).setVisible(true);
                menu.findItem(R.id.action_clear).setVisible(false);

                return true;
            });
        }

    }


    private void loadDataFromFirebaseFilter(String mQuery) {

        if (appMode.equals(getString(R.string.app_mode_work)) || appMode.equals(getString(R.string.app_mode_forced_work))) {
            displayFilterRestaurantsInWorkMode(mQuery);
        } else {
            displayFilterRestaurantInNormalMode(mQuery);
        }

    }


    //COLLECTION SORT HERE
    private void actionOnFloatingButton() {
        filterButton.setOnClickListener(view -> {

            FullScreenDialog dialog = FullScreenDialog.newInstance();
            setHasOptionsMenu(true);
            dialog.setCallback((ratingChoosen, openForLunchChoosen, customersNumberChoosen, distanceChoosen) -> {

                if (restaurantArrayList.size() > 0) {
                    restaurantArrayList.clear();
                }

                if (!ManageAppMode.getAppMode(context).equals(getString(R.string.app_mode_normal))) {
                     RestaurantHelper.getAllRestaurants().get().addOnCompleteListener(task -> {

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

                                    Restaurant restaurantObject = new Restaurant(restaurantPlaceId, restaurantCustomers, 0, ManageJobPlaceId.getJobPlaceId(App.getInstance().getApplicationContext()),
                                            restaurantName, rating, isOpenForLunch, distance, openHours, closeHours, restaurantAddress, 0.0, 0.0,
                                            null, null, openMinutes, closeMinutes);

                                    restaurantArrayList.add(restaurantObject);
                                }

                            }

                            if (ratingChoosen == 1 && !openForLunchChoosen) {
                                Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByDistance);
                            } else if (distanceChoosen == 1500.0 && !openForLunchChoosen) {
                                Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByRating);
                            } else if (distanceChoosen == 1500.0 && ratingChoosen == 1) {
                                Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByOpenForLunch);
                            } else if (openForLunchChoosen && ratingChoosen != 1) {
                                Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByOpenForLunch);
                                Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByRating);
                                Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByDistance);
                            } else {
                                Collections.sort(restaurantArrayList, Restaurant::compareTo);
                            }

                            mRecyclerListViewSortedAdapter = new RecyclerViewListViewRestaurantSorted(restaurantArrayList);
                            mRecyclerListView.setAdapter(mRecyclerListViewSortedAdapter);
                            mRecyclerListViewSortedAdapter.setData(restaurantArrayList);

                        } else {
                            Toast.makeText(getContext(), "No result found", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Log.e("debago", "Problem during the sort in work mode"));
                } else {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        RestaurantInNormalModeHelper.getAllRestaurants(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {

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

                                        Restaurant restaurantObject = new Restaurant(restaurantPlaceId, restaurantCustomers, 0, ManageJobPlaceId.getJobPlaceId(context),
                                                restaurantName, rating, isOpenForLunch, distance, openHours, closeHours, restaurantAddress, 0.0, 0.0,
                                                null, null, openMinutes, closeMinutes);

                                        restaurantArrayList.add(restaurantObject);
                                    }

                                }


                                if (ratingChoosen == 1 && !openForLunchChoosen) {
                                    Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByDistance);
                                } else if (distanceChoosen == 1500.0 && !openForLunchChoosen) {
                                    Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByRating);
                                } else if (distanceChoosen == 1500.0 && ratingChoosen == 1) {
                                    Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByOpenForLunch);
                                } else if (openForLunchChoosen && ratingChoosen != 1) {
                                    Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByOpenForLunch);
                                    Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByRating);
                                    Collections.sort(restaurantArrayList, Restaurant.compareRestaurantByDistance);
                                } else {
                                    Collections.sort(restaurantArrayList, Restaurant::compareTo);
                                }
                                mRecyclerListViewSortedAdapter = new RecyclerViewListViewRestaurantSorted(restaurantArrayList);
                                mRecyclerListView.setAdapter(mRecyclerListViewSortedAdapter);
                                mRecyclerListViewSortedAdapter.setData(restaurantArrayList);

                            } else {
                                Toast.makeText(getContext(), "No result found", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> Log.e("debago", "Problem during the sort in normal mode"));
                    }


                }

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

        String restaurantNameFromAutocomplete = ManageAutocompleteResponse.getStringAutocomplete((context), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_NAME);

        if (restaurantNameFromAutocomplete != null) {

            if (restaurantNameFromAutocomplete.isEmpty()) {
                loadDataFromFirebase();
            } else {

                loadDataFromFirebaseFilter(restaurantNameFromAutocomplete);
                //  initializeSharedPreferences();
            }
        } else {
            loadDataFromFirebase();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        initializeSharedPreferences();
    }

    private void initializeSharedPreferences() {
        ManageAutocompleteResponse.saveAutocompleteStringResponse(context, ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_NAME, null);
    }

    //FIRESTORE RECYCLER VIEW

    private void displayAllRestaurantsInWorkMode() {

        mRecyclerListViewAdapter = new RecyclerViewListViewRestaurant(generateOptionsForAdapterPaging(RestaurantHelper.getAllRestaurants()), this);
        //Choose how to display the list in the RecyclerView (vertical or horizontal)
        mRecyclerListView.setHasFixedSize(true);
        mRecyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerListView.addItemDecoration(new DividerItemDecoration(mRecyclerListView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerListView.setAdapter(mRecyclerListViewAdapter);
    }

    private void displayAllRestaurantsInNormalMode() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mRecyclerListViewAdapter = new RecyclerViewListViewRestaurant(generateOptionsForAdapterPaging(RestaurantInNormalModeHelper.getAllRestaurants(FirebaseAuth.getInstance().getCurrentUser().getUid())), this);
            //Choose how to display the list in the RecyclerView (vertical or horizontal)
            mRecyclerListView.setHasFixedSize(true);
            mRecyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            mRecyclerListView.addItemDecoration(new DividerItemDecoration(mRecyclerListView.getContext(), DividerItemDecoration.VERTICAL));
            mRecyclerListView.setAdapter(mRecyclerListViewAdapter);
        }

    }

    private void displayFilterRestaurantsInWorkMode(String mQuery) {

        mRecyclerListViewAdapter = new RecyclerViewListViewRestaurant(generateOptionsForAdapterPaging(RestaurantHelper.getFilterRestaurant(mQuery)), this);
        //Choose how to display the list in the RecyclerView (vertical or horizontal)
        mRecyclerListView.setHasFixedSize(true);
        mRecyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerListView.addItemDecoration(new DividerItemDecoration(mRecyclerListView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerListView.setAdapter(mRecyclerListViewAdapter);
    }

    private void displayFilterRestaurantInNormalMode(String mQuery) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mRecyclerListViewAdapter = new RecyclerViewListViewRestaurant(generateOptionsForAdapterPaging(RestaurantInNormalModeHelper.getFilterRestaurant(FirebaseAuth.getInstance().getCurrentUser().getUid(), mQuery)), this);
            //Choose how to display the list in the RecyclerView (vertical or horizontal)
            mRecyclerListView.setHasFixedSize(true);
            mRecyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            mRecyclerListView.addItemDecoration(new DividerItemDecoration(mRecyclerListView.getContext(), DividerItemDecoration.VERTICAL));
            mRecyclerListView.setAdapter(mRecyclerListViewAdapter);
        }

    }


    // Init Paging Configuration
    private PagedList.Config config = new PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(10)
            .build();

    // Create options for RecyclerView from a Query pagination
    private FirestorePagingOptions<Restaurant> generateOptionsForAdapterPaging(Query query) {
        return new FirestorePagingOptions.Builder<Restaurant>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Restaurant.class)
                .build();
    }



   /* // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Restaurant> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setQuery(query, Restaurant.class)
                .setLifecycleOwner(this)
                .build();
    }*/
    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onSwipeBoolean(boolean b) {
        textViewNoRestaurantFound.setVisibility(this.mRecyclerListViewAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        if (this.mRecyclerListViewAdapter.getItemCount() == 0) filterButton.hide();
        else filterButton.show();

        mSwipeRefreshLayout.setRefreshing(b);

    }

}
