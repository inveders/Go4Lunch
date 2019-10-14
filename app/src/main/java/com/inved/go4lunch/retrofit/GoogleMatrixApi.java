package com.inved.go4lunch.retrofit;

import com.inved.go4lunch.model.matrix.Matrix;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMatrixApi {

    @GET("distancematrix/json")
    Call<Matrix> getDistanceBetweenTwoPlaces(
            @Query("origins") String origins,
            @Query("destinations") String destinations,
            @Query("units") String units,
            @Query("key") String api_key
    );

}


