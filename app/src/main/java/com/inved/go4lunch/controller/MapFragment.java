package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.GooglePlaceCalls;
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.firebase.Restaurant;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.model.placesearch.PlaceSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_SEARCH_DATA;

public class MapFragment extends Fragment implements OnMapReadyCallback, GooglePlaceCalls.Callbacks {

    public static final String POSITION_ARRAY_LIST = "POSITION_IN_ARRAY_LIST";

    CollectionReference restaurants = RestaurantHelper.getRestaurantsCollection();

    GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;

    private Marker mMarker;
    private Double myDoubleLat = 0.0;
    private Double myDoubleLongi;

    private String myCurrentGeolocalisation;

    PlaceDetailsData placeDetailsData = new PlaceDetailsData();


    private static final int PERMS_CALL_ID = 1234;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {
                myCurrentGeolocalisation = intent.getStringExtra(KEY_GEOLOCALISATION);
                myDoubleLat = intent.getDoubleExtra(KEY_LATITUDE, 0);
                myDoubleLongi = intent.getDoubleExtra(KEY_LONGITUDE, 0);
                executeHttpRequestPlaceSearchWithRetrofit(myCurrentGeolocalisation);

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
    public void onResume() {
        super.onResume();

        if (myDoubleLat != 0.0) {
            executeHttpRequestPlaceSearchWithRetrofit(myCurrentGeolocalisation);
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


    // Launch View Place Activity
    private void startViewPlaceActivity() {
        Intent intent = new Intent(getContext(), ViewPlaceActivity.class);
        startActivity(intent);
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

    private void executeHttpRequestPlaceSearchWithRetrofit(String geolocalisation) {

        if (geolocalisation != null) {
            String type = "restaurant";
            int radius = 400;
            String keyword = "restaurant";
            String key = "AIzaSyCYRQL4UOKKcszTAi6OeN8xCvZ7CuFtp8A";//context.getText(R.string.google_maps_key).toString();

            GooglePlaceCalls.fetchPlaces(this, geolocalisation, radius, type, keyword, key);
        }
    }

    @Override
    public void onResponse(@Nullable PlaceSearch response) {

        assert response != null;
        int resultSize = response.results.size();

        ArrayList placeId = new ArrayList();
        ArrayList restaurantName = new ArrayList();
        ArrayList latitude = new ArrayList();
        ArrayList longitude = new ArrayList();
        for (int i = 0; i < resultSize; i++) {
            placeId.add(response.results.get(i).getPlaceId());
            restaurantName.add(response.results.get(i).getName());
            latitude.add(response.results.get(i).getGeometry().getLocation().getLat());
            longitude.add(response.results.get(i).getGeometry().getLocation().getLng());
        }

        createRestaurantsInFirebase(placeId);
        customizeMarker(placeId, latitude, longitude);

        //Configure action on marker click
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getSnippet() != null) {

                    int i = Integer.parseInt(marker.getSnippet());
                    placeDetailsData.setPlaceId(response.results.get(i).getPlaceId());

                    startViewPlaceActivity();

                }
                return true;
            }
        });


    }


    //To show the current localisation
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



    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //  Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
                Log.d("Debago", "Une erreur s'est produite lors de la création de la base de odnnée restaurant ");
            }
        };
    }

    private void createRestaurantsInFirebase(ArrayList listPlaceId) {
//Create restaure in firebase if it doesn't exist

        for (int i = 0; i < listPlaceId.size(); i++) {

            String id = listPlaceId.get(i).toString();

            restaurants.document(id)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                         //   Log.d("debago", "Document already exist");


                        } else {
                         //   Log.d("debago", "No such document");
                            RestaurantHelper.createRestaurant(id, id, 0).addOnFailureListener(onFailureListener());

                            String currentPlaceId = document.getString("id");

                        }
                    } else {
                   //     Log.d("debago", "get failed with ", task.getException());
                    }
                }
            });

        }


    }


    private void customizeMarker(ArrayList listPlaceId, ArrayList lat, ArrayList longi) {

        mGoogleMap.clear();
        int mZoom = 18;
        int mBearing = 0;
        int mTilt = 45;
        if (mMarker != null) {
            mMarker.remove();
        }

        for (int i = 0; i < listPlaceId.size(); i++) {

            String id = listPlaceId.get(i).toString();
            Double latitude = (Double) lat.get(i);
            Double longitude = (Double) longi.get(i);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.snippet(String.valueOf(i));

            restaurants.document(id)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Log.d("debago", "DocumentSnapshot data: " + document.getData());

                            LatLng latLng = new LatLng(latitude, longitude);
                            markerOptions.position(latLng);

                            //creating and getting restaurant information




                            int numberCustomers = Integer.parseInt(Objects.requireNonNull(document.get("restaurantCustomers")).toString());
                            if (numberCustomers > 0) {
                                markerOptions.icon(bitmapDescriptorFromVectorSelected(getContext(), R.drawable.ic_location_selected_24dp));
                                mGoogleMap.addMarker(markerOptions);

                            } else {
                                markerOptions.icon(bitmapDescriptorFromVectorNotSelected(getContext(), R.drawable.ic_location_not_selected_24dp));
                                mGoogleMap.addMarker(markerOptions);

                            }

                            CameraPosition Liberty = CameraPosition.builder().target(latLng).zoom(mZoom).bearing(mBearing).tilt(mTilt).build();
                            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        } else {
                         //   Log.d("debago", "No such document");
                        }
                    } else {
                      //  Log.d("debago", "get failed with ", task.getException());
                    }
                }
            });


        }


    }


    @Override
    public void onFailure() {

    }

    private BitmapDescriptor bitmapDescriptorFromVectorSelected(Context context,
                                                                @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_location_selected_24dp);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);

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

        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}