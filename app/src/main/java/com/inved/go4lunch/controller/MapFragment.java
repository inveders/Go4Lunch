package com.inved.go4lunch.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inved.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback,LocationListener {

    GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    public double latitude;
    public double longitude;
    private int i=0;

    private static final int PERMS_CALL_ID = 1234;

    private LocationManager lm;
    public SupportMapFragment mapFragment;

    public MapFragment(){
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      /*  SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapfrag);
        mapFragment.getMapAsync(this);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = mView.findViewById(R.id.mapfrag);

        if(mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;


      //  MapFragment.this.googleMap = googleMap;
   /*     Log.d("Debago","MapFragment onMapReady : latitude "+latitude);
        googleMap.moveCamera(CameraUpdateFactory.zoomBy(15));

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longitude))
        );*/
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("DEBAGO", "MapFragment : in resume");
        checkPermissions();

    }

    private void checkPermissions(){
        //We check permission to know if they are granted
        if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((RestaurantActivity)getContext(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },PERMS_CALL_ID);

            return;
        }


        //Subscribe to providers
        lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }

        if (lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 0, this);
        }

        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        }

        //Charging carto
        // loadMap();
        Log.d("DEBAGO", "MapFragment : in checkpermissions googlemap value "+mGoogleMap);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMS_CALL_ID){

        }
    }



    @Override
    public void onLocationChanged(Location location) {
        RestaurantActivity launchRetrofitRequest=new RestaurantActivity();
                latitude = location.getLatitude();
        longitude = location.getLongitude();
        int mZoom=16;
        int mBearing=0;
        int mTilt=45;

       // Log.d("Debago","MapFragment onlocationchanged : latitude "+latitude);
       // Toast.makeText(getContext(),"Location: "+latitude+"/"+longitude,Toast.LENGTH_LONG).show();
      //  Log.d("DEBAGO", "MapFragment : valeur mMap "+mGoogleMap);

        if (mGoogleMap != null && latitude!=0){
        //    Log.d("DEBAGO", "MapFragment : in onLocationChanged ");

            LatLng googleLocation = new LatLng(latitude,longitude);
            mGoogleMap.clear(); //clear old markers
            mGoogleMap.addMarker(new MarkerOptions().position(googleLocation).title("Domicile Gnimadi").snippet("ON EST AL"));
            CameraPosition Liberty = CameraPosition.builder().target(googleLocation).zoom(mZoom).bearing(mBearing).tilt(mTilt).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(googleLocation));
            getCurrentLocalisation();

            if(latitude!=0 &&  i==2){
                launchRetrofitRequest.executeHttpRequestWithRetrofit();
                Log.d("Debago","MapFragment onlocationchanged  on exàcute retrofit ");

            }

            i++;/**TROUVER UNE SOLUTION POUR NE PASINCREMENTER LES i indéfiniment*/
            Log.d("Debago","MapFragment onlocationchanged  i value "+i);
        }





    }

    public String getCurrentLocalisation(){
      //  Log.d("Debago","MapFragment getcurrentlocalisation: latitude "+latitude);
        return ""+latitude+","+longitude+"";
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}