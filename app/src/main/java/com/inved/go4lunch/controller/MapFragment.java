package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LONGITUDE;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;


    RestaurantActivity gps = new RestaurantActivity();

    private static final int PERMS_CALL_ID = 1234;

    private LocationManager lm;
    public SupportMapFragment mapFragment;



    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction()))
            {

                intent.getSerializableExtra(KEY_GEOLOCALISATION);

                Double myLat=intent.getDoubleExtra(KEY_LATITUDE,0);
                Double myLongi=intent.getDoubleExtra(KEY_LONGITUDE,0);
                Log.d("Debago", "MapFragment : ONReceive "+myLat);
                loadMapMapFragment(myLat,myLongi);

            }
        }
    };

    public MapFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      /*  SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapfrag);
        mapFragment.getMapAsync(this);*/
        //Launch retrofit request

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_map, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = mView.findViewById(R.id.mapfrag);

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        Log.d("Debago", "MapFragment : onMapReady ");
        mGoogleMap = googleMap;
     //   loadMapMapFragment();

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


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMS_CALL_ID) {

        }
    }


    public void loadMapMapFragment(Double lat, Double longi) {

        Log.d("Debago", "MapFragment : on loadMapMapFragment latitude /5bis " + lat);
        int mZoom = 16;
        int mBearing = 0;
        int mTilt = 45;
        if (mGoogleMap != null && lat != 0) {


            LatLng googleLocation = new LatLng(lat, longi);
            mGoogleMap.clear(); //clear old markers
            mGoogleMap.addMarker(new MarkerOptions().position(googleLocation).title("Domicile Gnimadi").snippet("ON EST AL"));
            CameraPosition Liberty = CameraPosition.builder().target(googleLocation).zoom(mZoom).bearing(mBearing).tilt(mTilt).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(googleLocation));

        }

    }


}