package com.inved.go4lunch.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.inved.go4lunch.model.matrix.Row;
import com.inved.go4lunch.repository.RepositoryMatrix;

import java.util.List;

public class ResultModel extends AndroidViewModel {

    private RepositoryMatrix repositoryMatrix;


    public ResultModel(@NonNull Application application) {
        super(application);
        repositoryMatrix = new RepositoryMatrix();

    }


    public LiveData<List<Row>> getMatrixDistance(String origins, String destinations) {

        return repositoryMatrix.getMutableLiveData(origins,destinations);
    }


}
