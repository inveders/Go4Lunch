package com.inved.go4lunch.controller.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.firebase.Restaurant;
import com.inved.go4lunch.firebase.UserFavoriteRestaurantHelper;
import com.inved.go4lunch.view.RecyclerViewListViewFavoriteRestaurant;


public class FavoritesActivity extends BaseActivity implements RecyclerViewListViewFavoriteRestaurant.ListenerFavorite {

    private RecyclerViewListViewFavoriteRestaurant mRecyclerListViewFavoriteAdapter;
    private RecyclerView mRecyclerListView;
    private TextView textViewNoRestaurantFound;
    //Progress bar
    private ProgressBar mProgressBar;

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_favorites;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolBar();
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

        //RecyclerView initialization
        mRecyclerListView = findViewById(R.id.activity_favorite_recycler_view);
        mRecyclerListView.addItemDecoration(new DividerItemDecoration(mRecyclerListView.getContext(), DividerItemDecoration.VERTICAL));
        textViewNoRestaurantFound = findViewById(R.id.activity_favorite_textview_no_restaurant);

        displayAllFavoritesRestaurants();

    }

    // Configure Toolbar
    private void configureToolBar() {
        Toolbar toolbar = findViewById(R.id.favorite_toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_title_favorites_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    private void displayAllFavoritesRestaurants() {

        if (getCurrentUser() != null) {
            mRecyclerListViewFavoriteAdapter = new RecyclerViewListViewFavoriteRestaurant(generateOptionsForAdapter(UserFavoriteRestaurantHelper.getAllRestaurants(getCurrentUser().getUid())), this);
            //Choose how to display the list in the RecyclerView (vertical or horizontal)
            mRecyclerListView.setHasFixedSize(true);
            mRecyclerListView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mRecyclerListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mRecyclerListView.setAdapter(mRecyclerListViewFavoriteAdapter);
        }


    }

    // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Restaurant> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setQuery(query, Restaurant.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChangedFavorite() {
        // 7 - Show TextView in case RecyclerView is empty

        if (getCurrentUser() != null) {
            UserFavoriteRestaurantHelper.getAllRestaurants(getCurrentUser().getUid()).get().addOnCompleteListener(task -> {

                if (task.getResult() != null) {

                    if (task.getResult() != null) {
                        if (task.getResult().getDocuments().isEmpty()) {
                            textViewNoRestaurantFound.setVisibility(this.mRecyclerListViewFavoriteAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                            hideProgressBar();
                        }
                    }
                }

            }).addOnFailureListener(e -> Log.e("debago", "Problem during the sort in work mode"));
        }

        if(mRecyclerListViewFavoriteAdapter.getItemCount()!=0){
            hideProgressBar();
        }else{
            showProgressBar();
        }

    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);

    }
}
