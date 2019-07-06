package com.inved.go4lunch.api;

import com.inved.go4lunch.pojo.Pojo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesApi {

    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyDN7RJFmImYAca96elyZlE5s_fhX-MMuhk")
    Call<Pojo> getNearbyRestaurant(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);

}
