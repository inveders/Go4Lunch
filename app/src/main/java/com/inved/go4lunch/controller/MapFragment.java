package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.APIClientGoogleSearch;
import com.inved.go4lunch.api.GooglePlaceCalls;
import com.inved.go4lunch.model.placesearch.PlaceSearch;
import com.inved.go4lunch.model.placesearch.Result;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LONGITUDE;

public class MapFragment extends Fragment implements OnMapReadyCallback, GooglePlaceCalls.Callbacks, GoogleMap.OnMarkerClickListener {

    GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private PlaceSearch myPlace = new PlaceSearch();
    private Marker mMarker;


    RestaurantActivity restaurantActivity = new RestaurantActivity();

    private static final int PERMS_CALL_ID = 1234;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {

                intent.getSerializableExtra(KEY_GEOLOCALISATION);

                Double myLat = intent.getDoubleExtra(KEY_LATITUDE, 0);
                Double myLongi = intent.getDoubleExtra(KEY_LONGITUDE, 0);

                loadMapMapFragment(myLat, myLongi);
                getUrlRestaurant(myLat, myLongi);

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
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getSnippet()!=null){
            APIClientGoogleSearch.currentResult = myPlace.getResults().get(Integer.parseInt(marker.getSnippet()));
        ViewPlaceActivity viewPlaceActivity = new ViewPlaceActivity();
        viewPlaceActivity.executeHttpRequestPlaceDetailsWithRetrofit(APIClientGoogleSearch.currentResult.getPlaceId());
        Log.d("Debago","MapFragment onMarkerclick :"+APIClientGoogleSearch.currentResult.getPlaceId());
        startViewPlaceActivity();
        }
        return true;

    }

    // Launch View Place Activity
    public void startViewPlaceActivity(){
        Intent intent = new Intent(getContext(), ViewPlaceActivity.class);
        startActivity(intent);
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

    private void isNearbyRestaurantSelected(Boolean isRestaurantSelected) {

        mGoogleMap.clear();

    }


    private String getUrlRestaurant(double lat, double longi) {

//      String url = getUrlRestaurant(lat, longi);
        String type = "restaurant";
        int radius = 400;
        String keyword = "restaurant";
        String geolocalisation = "" + lat + "," + longi + "";
        String key = getText(R.string.google_maps_key).toString();

        GooglePlaceCalls.fetchPlaces(this, geolocalisation, radius, type, keyword, key);
        return "";
    }

    // Override callback methods

    @Override
    public void onResponse(@Nullable PlaceSearch response) {
        // 2.1 - When getting response, we update UI
        myPlace = response;
        if (response != null) {
            for (int i = 0; i < response.getResults().size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                Result googlePlace = response.getResults().get(i);
                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat().toString());
                double longi = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng().toString());
                String placeName = googlePlace.getName();
                String vicinity = googlePlace.getVicinity();
                LatLng latLng = new LatLng(lat, longi);
                markerOptions.position(latLng);

                markerOptions.title(placeName);
                if (i < 5) {

                    markerOptions.icon(bitmapDescriptorFromVectorNotSelected(getContext(),R.drawable.ic_location_not_selected_24dp));
                } else {
                    markerOptions.icon(bitmapDescriptorFromVectorSelected(getContext(),R.drawable.ic_location_selected_24dp));
                }
                markerOptions.snippet(String.valueOf(i));
                mGoogleMap.addMarker(markerOptions);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18 ));
            }

            mGoogleMap.setOnMarkerClickListener(this);
        }

        assert response != null;

    }

    private BitmapDescriptor bitmapDescriptorFromVectorSelected(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_location_selected_24dp);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private BitmapDescriptor bitmapDescriptorFromVectorNotSelected(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_location_not_selected_24dp);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onFailure() {
        // 2.2 - When getting error, we update UI

    }

    public void loadMapMapFragment(Double lat, Double longi) {

        int mZoom = 16;
        int mBearing = 0;
        int mTilt = 45;
        if (mMarker!=null){
            mMarker.remove();
        }

        if (mGoogleMap != null && lat != 0) {


            LatLng googleLocation = new LatLng(lat, longi);
            mGoogleMap.clear(); //clear old markers
            mMarker=mGoogleMap.addMarker(new MarkerOptions()
                    .position(googleLocation)
                    .title(getString(R.string.map_your_position))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            CameraPosition Liberty = CameraPosition.builder().target(googleLocation).zoom(mZoom).bearing(mBearing).tilt(mTilt).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(googleLocation));


        }


    }


}