package com.inved.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.inved.go4lunch.R;
import com.inved.go4lunch.model.matrix.Element;
import com.inved.go4lunch.model.matrix.Matrix;
import com.inved.go4lunch.model.matrix.Row;
import com.inved.go4lunch.retrofit.GoogleMatrixApi;
import com.inved.go4lunch.retrofit.RetrofitServiceMatrix;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositoryMatrix {

    private ArrayList<Row> results = new ArrayList<>();
    private MutableLiveData<List<Row>> mutableLiveData = new MutableLiveData<>();

    public RepositoryMatrix() {
    }

    public MutableLiveData<List<Row>> getMutableLiveData(String origins,String destinations) {

        GoogleMatrixApi googleMatrixApi = RetrofitServiceMatrix.getGoogleMatrixApi();

        String units = "metric";
        String google_key = "AIzaSyDGlWlfgLFt-CyCh9m8B7mQU8HMc6VT1xw";
        Call<Matrix> call = googleMatrixApi.getDistanceBetweenTwoPlaces(origins,"place_id:"+destinations,units, google_key);

        call.enqueue(new Callback<Matrix>() {
            @Override
            public void onResponse(@NonNull Call<Matrix> call, @NonNull Response<Matrix> response) {

                Matrix matrix = response.body();
                if(matrix!=null){
                    Log.d("debago","getmutablelivedata row not null "+matrix.getRows());
                    if (matrix.getRows() != null) {
                        results = (ArrayList<Row>) matrix.getRows();
                        Log.d("debago","getmutablelivedata result distance"+(results.get(0).getElements().get(0).getDistance().getValue())/1000);
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
