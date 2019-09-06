package com.inved.go4lunch.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inved.go4lunch.model.placesearch.PlaceSearch;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GooglePlaceCalls {

    // 1 - Creating a callback
    public interface Callbacks {
        void onResponse(@Nullable PlaceSearch users);
        void onFailure();
    }

    // 2 - Public method to start fetching users following by Jake Wharton
    public static void fetchPlaces(Callbacks callbacks,String location,int radius, String type, String keyword, String api_key){

        // 2.1 - Create a weak reference to callback (avoid memory leaks)
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<Callbacks>(callbacks);

        // 2.2 - Get a Retrofit instance and the related endpoints
        GooglePlacesApi service = APIClientGoogleSearch.getClient().create(GooglePlacesApi.class);

        // 2.3 - Create the call on Github API
        Call<PlaceSearch> call = service.getNearbyRestaurant(location,radius,type,keyword,api_key);

        // 2.4 - Start the call
        call.enqueue(new Callback<PlaceSearch>() {


            @Override
            public void onResponse(@NonNull Call<PlaceSearch> call, @NonNull Response<PlaceSearch> response) {
                // 2.5 - Call the proper callback used in controller (MainFragment)
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponse(response.body());


            }


            @Override
            public void onFailure(@NonNull Call<PlaceSearch> call, @NonNull Throwable t) {
                // 2.5 - Call the proper callback used in controller (MainFragment)
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();

            }
        });
    }
}
