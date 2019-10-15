package com.inved.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.inved.go4lunch.model.matrix.Matrix;
import com.inved.go4lunch.model.matrix.Row;
import com.inved.go4lunch.retrofit.GoogleMatrixApi;
import com.inved.go4lunch.retrofit.RetrofitServiceMatrix;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.MATRIX_API_KEY;

public class RepositoryMatrix {

    private ArrayList<Row> results = new ArrayList<>();
    private MutableLiveData<List<Row>> mutableLiveData = new MutableLiveData<>();

    public RepositoryMatrix() {
    }

    public MutableLiveData<List<Row>> getMutableLiveData(String origins,String destinations) {

        GoogleMatrixApi googleMatrixApi = RetrofitServiceMatrix.getGoogleMatrixApi();

        String units = "metric";
        Call<Matrix> call = googleMatrixApi.getDistanceBetweenTwoPlaces(origins,"place_id:"+destinations,units, MATRIX_API_KEY);

        call.enqueue(new Callback<Matrix>() {
            @Override
            public void onResponse(@NonNull Call<Matrix> call, @NonNull Response<Matrix> response) {

                Matrix matrix = response.body();
                if(matrix!=null){
                    if (matrix.getRows() != null) {
                        results = (ArrayList<Row>) matrix.getRows();
                        mutableLiveData.setValue(results);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<Matrix> call,@NonNull Throwable t) {
                Log.d("debago","inRetrofit failure :"+t);
            }
        });


        return mutableLiveData;


    }
}
