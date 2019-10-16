package com.inved.go4lunch.retrofit;

import com.inved.go4lunch.model.placesearch.PlaceSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleNearbySearchApi {

    @GET("place/nearbysearch/json")
    Call<PlaceSearch> getNearbyRestaurants(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("key") String api_key
    );
}
