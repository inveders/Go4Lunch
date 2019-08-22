package com.inved.go4lunch.api;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inved.go4lunch.model.placedetails.PlaceDetails;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GooglePlaceDetailsCalls {

    // 1 - Creating a callback
    public interface CallbacksDetail {
        void onResponse(@Nullable PlaceDetails users);
        void onFailure();
    }

    // 2 - Public method to start fetching users following by Jake Wharton
    public static void fetchPlaceDetail(CallbacksDetail callbacks, String placeid, String api_key, String fields){

        // 2.1 - Create a weak reference to callback (avoid memory leaks)
        final WeakReference<CallbacksDetail> callbacksWeakReference = new WeakReference<CallbacksDetail>(callbacks);

        // 2.2 - Get a Retrofit instance and the related endpoints
        GooglePlacesApi service = APIClientGoogleSearch.getClient().create(GooglePlacesApi.class);

        // 2.3 - Create the call on Github API
        Call<PlaceDetails> call = service.getDetailPlace(placeid,api_key,fields);

        // 2.4 - Start the call
        call.enqueue(new Callback<PlaceDetails>() {


            @Override
            public void onResponse(Call<PlaceDetails> call, @NonNull Response<PlaceDetails> response) {
                // 2.5 - Call the proper callback used in controller (MainFragment)
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponse(response.body());
                if (response.isSuccessful()) {

                    if (response.body() != null) {

                    } else {
               //         Log.d("Debago", "detailcall on response est null ohhhh ");
                    }
                }

            }


            @Override
            public void onFailure(Call<PlaceDetails> call, Throwable t) {
                // 2.5 - Call the proper callback used in controller (MainFragment)
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();

            }
        });
    }
}
