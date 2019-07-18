package com.inved.go4lunch.api;

import com.inved.go4lunch.model.placedetails.PlaceDetails;
import com.inved.go4lunch.model.placesearch.PlaceSearch;
import com.inved.go4lunch.model.placesearch.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesApi {

    @GET("nearbysearch/json")
    Call<PlaceSearch> getNearbyRestaurant(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("keyword") String keyword,
            @Query("key") String api_key
    );

    @GET("details/json")
    Call<PlaceDetails> getDetailPlace(
            @Query("placeid") String placeId,
            @Query("key") String api_key,
            @Query("fields") String fields
    );


}


