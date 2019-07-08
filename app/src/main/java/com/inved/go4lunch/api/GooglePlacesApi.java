package com.inved.go4lunch.api;

import com.inved.go4lunch.model.pojo.Pojo;
import com.inved.go4lunch.model.pojo.Result;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesApi {

    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyCYRQL4UOKKcszTAi6OeN8xCvZ7CuFtp8A")
    Call<Pojo> getNearbyRestaurant(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);


    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

    String url = "https://maps.googleapis.com/maps/";

   /* public static final */ Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();

}


