package com.inved.go4lunch.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inved.go4lunch.R;
import com.inved.go4lunch.api.GooglePlaceCalls;
import com.inved.go4lunch.model.pojo.Pojo;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListViewFragment extends Fragment implements GooglePlaceCalls.Callbacks {

    private View mView;
    private RecyclerViewListViewRestaurant mRecyclerListViewAdapter;

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
        executeHttpRequestWithRetrofit();
        return mView;
    }

// -------------------
    // HTTP REQUEST BY RETROFIT (Retrofit Way)
    // -------------------


    // 4 - Execute HTTP request and update UI
    void executeHttpRequestWithRetrofit(){
        MapFragment geolocalisation = new MapFragment();
        String type = "bank";
        int radius = 4000;
        String currentLocalisation = geolocalisation.getCurrentLocalisation();

        Log.d("Debago","ListViewFragment currentLocalisation"+currentLocalisation);
        GooglePlaceCalls.fetchUserFollowing(this, type,"49.442627699999996 ,6.0247494",radius);



    }

    // 2 - Override callback methods

    @Override
    public void onResponse(@Nullable Pojo response) {
        // 2.1 - When getting response, we update UI
        if (response != null);

        assert response != null;
        Log.d("Debago","ListViewFragment result size :"+response.results.size());
     //   mRecyclerListViewAdapter.setData(response.results);
//        Toast.makeText(this,"Location: "+response.results.get(1).getName(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure() {
        // 2.2 - When getting error, we update UI

    }


}
