package com.inved.go4lunch.controller;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.inved.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

 //   private LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    private ArrayList<LocationProvider> providers;
    private Criteria critere;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.localisationMethod();

        return inflater.inflate(R.layout.fragment_map, container, false);


    }

    private void localisationMethod() {
        providers = new ArrayList<LocationProvider>();

    //    List<String> names = locationManager.getProviders(true);

     //   for (String name : names)
      //      providers.add(locationManager.getProvider(name));


        //----------Critère du fournisseur

        Criteria critere = new Criteria();

        // Pour indiquer la précision voulue
        // On peut mettre ACCURACY_FINE pour une haute précision ou ACCURACY_COARSE pour une moins bonne précision
        critere.setAccuracy(Criteria.ACCURACY_FINE);

        // Est-ce que le fournisseur doit être capable de donner une altitude ?
        critere.setAltitudeRequired(true);

        // Est-ce que le fournisseur doit être capable de donner une direction ?
        critere.setBearingRequired(true);

        // Est-ce que le fournisseur peut être payant ?
        critere.setCostAllowed(false);

        // Pour indiquer la consommation d'énergie demandée
        // Criteria.POWER_HIGH pour une haute consommation, Criteria.POWER_MEDIUM pour une consommation moyenne et Criteria.POWER_LOW pour une basse consommation
        critere.setPowerRequirement(Criteria.POWER_HIGH);

        // Est-ce que le fournisseur doit être capable de donner une vitesse ?
        critere.setSpeedRequired(true);


        //---------recup données

        /*locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150, new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onLocationChanged(Location location) {
                Log.d("GPS", "Latitude " + location.getLatitude() + " et longitude " + location.getLongitude());
            }
        });*/

    }


}
