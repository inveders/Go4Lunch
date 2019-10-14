package com.inved.go4lunch.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.controller.activity.RestaurantActivity;
import com.inved.go4lunch.controller.activity.ViewPlaceActivity;
import com.inved.go4lunch.firebase.Restaurant;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageAutocompleteResponse;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import java.util.Objects;

import static com.inved.go4lunch.utils.ManageJobPlaceId.KEY_JOB_PLACE_ID_DATA;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String RESTAURANT_PLACE_ID = "PLACE_ID";
    private GoogleMap mGoogleMap;
    private View mView;
    private Marker mMarker;
    private String jobPlaceId;
    private FloatingActionButton mapGeolocalisationButton;


    public MapFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jobPlaceId = ManageJobPlaceId.getJobPlaceId(Objects.requireNonNull(getActivity()));

        initializeSharedPreferences();
        initializeMap();



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.fragment_map, container, false);

        mapGeolocalisationButton = mView.findViewById(R.id.fragment_map_gps_geolocalisation_button);

        actionOnFloatingButton();

        ((RestaurantActivity) Objects.requireNonNull(getActivity())).setMapFragmentRefreshListener(this::initializeMap);

        return mView;
    }

    private void actionOnFloatingButton() {
        mapGeolocalisationButton.setOnClickListener(view ->

        initializeMap());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapView mMapView = mView.findViewById(R.id.mapfrag);


        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();


            mMapView.getMapAsync(this);


        }
    }


    @Override
    public void onResume() {
        super.onResume();

        initializeMap();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }



    private void initializeMap() {

        String sharedPreferenceRestaurantPlaceId = ManageAutocompleteResponse.getStringAutocomplete((App.getInstance().getApplicationContext()), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_ID);

        if (sharedPreferenceRestaurantPlaceId != null) {
            double latitude = ManageAutocompleteResponse.getDoubleAutocomplete(App.getInstance().getApplicationContext(), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LATITUDE);
            double longitude = ManageAutocompleteResponse.getDoubleAutocomplete(App.getInstance().getApplicationContext(), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LONGITUDE);
            //   LatLng latLngSharedPreferences = new LatLng(latitude, longitude);
            customizeMarker(sharedPreferenceRestaurantPlaceId, latitude, longitude);
            initializeSharedPreferences();


        } else {

            RestaurantHelper.getAllRestaurants(jobPlaceId).get().addOnSuccessListener(queryDocumentSnapshots -> {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);

                    String restaurantPlaceId = restaurant.getRestaurantPlaceId();
                    double latitude = restaurant.getLatitude();
                    double longitude = restaurant.getLongitude();

                    customizeMarker(restaurantPlaceId, latitude, longitude);
                }

            }).addOnFailureListener(e -> {

            });
        }


    }

    private void initializeSharedPreferences() {

        ManageAutocompleteResponse.saveAutocompleteStringResponse(Objects.requireNonNull(App.getInstance().getApplicationContext()), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_ID, null);

    }


    // Launch View Place Activity
    private void startViewPlaceActivity(String placeId) {
        Intent intent = new Intent(getContext(), ViewPlaceActivity.class);
        intent.putExtra(RESTAURANT_PLACE_ID, placeId);
        startActivity(intent);
    }


    private void customizeMarker(String restaurantPlaceId, double lat, double longi) {

        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }

        int mZoom = 18;
        int mBearing = 0;
        int mTilt = 45;
        if (mMarker != null) {
            //Mettre un log.d
            mMarker.remove();
        }

        LatLng latLng = new LatLng(lat, longi);


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.snippet(restaurantPlaceId);

        RestaurantHelper.getRestaurant(restaurantPlaceId, jobPlaceId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    Restaurant restaurant = document.toObject(Restaurant.class);

                    if (restaurant != null) {
                        if (document.exists()) {

                            markerOptions.position(latLng);

                            //creating and getting restaurant information
                            int numberCustomers = restaurant.getRestaurantCustomers();
                            if (numberCustomers > 0) {
                                markerOptions.icon(bitmapDescriptorFromVectorSelected(getContext()));
                                mGoogleMap.addMarker(markerOptions);

                            } else {
                                markerOptions.icon(bitmapDescriptorFromVectorNotSelected(getContext()));
                                mGoogleMap.addMarker(markerOptions);

                            }

                            CameraPosition Liberty = CameraPosition.builder().target(latLng).zoom(mZoom).bearing(mBearing).tilt(mTilt).build();
                            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                    }
                }


            }
        });

        if (mGoogleMap != null) {
            //Configure action on marker click
            mGoogleMap.setOnMarkerClickListener(marker -> {

                if (marker.getSnippet() != null) {

                    startViewPlaceActivity(marker.getSnippet());

                }
                return true;
            });
        }

    }

    public boolean onMarkerClick(Marker marker) {
        if (marker.getSnippet() != null) {

            startViewPlaceActivity(marker.getSnippet());

        }
        return true;
    }


    private BitmapDescriptor bitmapDescriptorFromVectorSelected(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_location_selected_24dp);
        assert background != null;
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_location_selected_24dp);

        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        if (vectorDrawable != null) {
            vectorDrawable.draw(canvas);
        }
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private BitmapDescriptor bitmapDescriptorFromVectorNotSelected(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_location_not_selected_24dp);
        if (background != null) {
            background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        }
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_location_not_selected_24dp);

        Bitmap bitmap = null;
        if (background != null) {
            bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = null;
        if (bitmap != null) {
            canvas = new Canvas(bitmap);
        }
        if (background != null) {
            if (canvas != null) {
                background.draw(canvas);
            }
        }
        assert vectorDrawable != null;
        if (canvas != null) {
            vectorDrawable.draw(canvas);
        }
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(marker -> false);
        //    mGoogleMap.setMyLocationEnabled(true);
        //   mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //    mGoogleMap.moveCamera(CameraUpdateFactory.zoomBy(11));

        LatLng initialPosition = new LatLng(0, 0);

        mMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(initialPosition));
    }


}