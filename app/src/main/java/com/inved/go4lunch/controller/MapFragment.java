package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.api.PlaceSearchData;
import com.inved.go4lunch.model.placesearch.PlaceSearch;

import java.util.ArrayList;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_NAME;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PLACE_ID;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_RESTAURANT_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_RESTAURANT_LONGITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_SIZE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_SEARCH_DATA;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String POSITION_ARRAY_LIST = "POSITION_IN_ARRAY_LIST";


    GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private PlaceSearch myPlace = new PlaceSearch();
    private Marker mMarker;
    private Double myDoubleLat;
    private Double myDoubleLongi;
    private ArrayList restaurantName;
    private ArrayList restaurantLatitude;
    private ArrayList restaurantLongitude;
    private int resultSizeDataPlaceSearch;
    private ArrayList placeId;
    private String myCurrentGeolocalisation;
    Context context;
    PlaceDetailsData placeDetailsData = new PlaceDetailsData();
    PlaceSearchData placeSearchData = new PlaceSearchData();
    ViewPlaceActivity viewPlaceActivity = new ViewPlaceActivity();

    private static final int PERMS_CALL_ID = 1234;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {
                myCurrentGeolocalisation = intent.getStringExtra(KEY_GEOLOCALISATION);
                myDoubleLat = intent.getDoubleExtra(KEY_LATITUDE, 0);
                myDoubleLongi = intent.getDoubleExtra(KEY_LONGITUDE, 0);

            }

            if (PLACE_SEARCH_DATA.equals(intent.getAction())) {

                restaurantName = intent.getStringArrayListExtra(PLACE_DATA_NAME);
                restaurantLatitude = intent.getStringArrayListExtra(PLACE_DATA_RESTAURANT_LATITUDE);
                restaurantLongitude = intent.getStringArrayListExtra(PLACE_DATA_RESTAURANT_LONGITUDE);
                resultSizeDataPlaceSearch = intent.getIntExtra(PLACE_DATA_SIZE, 0);
                placeId = intent.getStringArrayListExtra(PLACE_DATA_PLACE_ID);
            }

            loadMapMapFragment(myDoubleLat, myDoubleLongi, restaurantName, restaurantLatitude, restaurantLongitude, resultSizeDataPlaceSearch);
        }
    };

    public MapFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_SEARCH_DATA));

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
        if (marker.getSnippet() != null) {

            int i = Integer.parseInt(marker.getSnippet());
            placeDetailsData.setPlaceId((String) placeId.get(i));

            startViewPlaceActivity();

        }

        return true;

    }


    // Launch View Place Activity
    private void startViewPlaceActivity() {
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

    private void loadMapMapFragment(Double lat,
                                    Double longi,
                                    ArrayList restaurantName,
                                    ArrayList restaurantLatitude,
                                    ArrayList restaurantLongitude,
                                    int resultSize) {


        int mZoom = 18;
        int mBearing = 0;
        int mTilt = 45;
        if (mMarker != null) {
            mMarker.remove();
        }

        // 2.1 - When getting response, we update UI

        if (lat != null) {
            //  mGoogleMap.clear();
            for (int i = 0; i < resultSize; i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng((Double) restaurantLatitude.get(i), (Double) restaurantLongitude.get(i));
                markerOptions.position(latLng);

                markerOptions.title((String) restaurantName.get(i));
                // Log.d("Debago", "MapFragment restaurantName: " + restaurantName.get(i));
                if (i < 5) {

                    markerOptions.icon(bitmapDescriptorFromVectorNotSelected(getContext(), R.drawable.ic_location_not_selected_24dp));
                } else {
                    markerOptions.icon(bitmapDescriptorFromVectorSelected(getContext(), R.drawable.ic_location_selected_24dp));
                }
                markerOptions.snippet(String.valueOf(i));
                mGoogleMap.addMarker(markerOptions);
                CameraPosition Liberty = CameraPosition.builder().target(latLng).zoom(mZoom).bearing(mBearing).tilt(mTilt).build();
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                //   mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //  mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(mZoom));
            }

            mGoogleMap.setOnMarkerClickListener(this);
        }

     /*   if (mGoogleMap != null && lat != 0) {
            LatLng googleLocation = new LatLng(lat, longi);
            mMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(googleLocation)
                    .title(getString(R.string.map_your_position))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                 CameraPosition Liberty = CameraPosition.builder().target(googleLocation).zoom(mZoom).bearing(mBearing).tilt(mTilt).build();
                 mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
                 mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(googleLocation));
        }*/


    }

    private BitmapDescriptor bitmapDescriptorFromVectorSelected(Context context,
                                                                @DrawableRes int vectorDrawableResourceId) {
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

    private BitmapDescriptor bitmapDescriptorFromVectorNotSelected(Context context,
                                                                   @DrawableRes int vectorDrawableResourceId) {
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


}