package com.inved.go4lunch.model;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.inved.go4lunch.controller.fragment.MapFragment;
import com.inved.go4lunch.model.matrix.Row;
import com.inved.go4lunch.model.placesearch.Result;
import com.inved.go4lunch.repository.NearbyRestaurantsRepository;
import com.inved.go4lunch.repository.RepositoryMatrix;

import java.util.List;

public class ResultModel extends AndroidViewModel {

    private RepositoryMatrix repositoryMatrix;
    private NearbyRestaurantsRepository nearbyRestaurantsRepository;



    public ResultModel(@NonNull Application application) {
        super(application);
        repositoryMatrix = new RepositoryMatrix();
        nearbyRestaurantsRepository = new NearbyRestaurantsRepository();

    }


    public LiveData<List<Row>> getMatrixDistance(String origins, String destinations) {

        return repositoryMatrix.getMutableLiveData(origins,destinations);
    }

    public LiveData<List<Result>> setNearbyRestaurantsInFirebase(Double lat,Double longi) {

        return nearbyRestaurantsRepository.getNearbySearchMutableLiveData(lat,longi);
    }



}
