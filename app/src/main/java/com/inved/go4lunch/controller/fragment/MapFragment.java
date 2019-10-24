package com.inved.go4lunch.controller.fragment;

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
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.controller.activity.RestaurantActivity;
import com.inved.go4lunch.controller.activity.ViewPlaceActivity;
import com.inved.go4lunch.firebase.Restaurant;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.firebase.RestaurantInNormalModeHelper;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageAppMode;
import com.inved.go4lunch.utils.ManageAutocompleteResponse;
import com.inved.go4lunch.utils.ManagePosition;
import com.inved.go4lunch.utils.ManageRestaurantChoiceInNormalMode;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.utils.ManagePosition.KEY_POSITION_DATA;
import static com.inved.go4lunch.utils.ManagePosition.KEY_POSITION_JOB_LAT_LNG_DATA;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String RESTAURANT_PLACE_ID = "PLACE_ID";
    private GoogleMap mGoogleMap;
    private View mView;
    private Marker mMarker;
    private double myCurrentLat;
    private double myCurrentLongi;
    //Progress bar
    private ProgressBar mProgressBar;

    private FloatingActionButton mapGeolocalisationButton;
    private Context context = App.getInstance().getApplicationContext();

    //Receive current localisation from Localisation.class
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {
                intent.getSerializableExtra(KEY_GEOLOCALISATION);
                myCurrentLat = intent.getDoubleExtra(KEY_LATITUDE, 0.0);
                myCurrentLongi = intent.getDoubleExtra(KEY_LONGITUDE, 0.0);

            }

        }
    };

    public MapFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(App.getInstance().getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));
        initializeSharedPreferences();



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        mView = inflater.inflate(R.layout.fragment_map, container, false);
        //Progress bar
        mProgressBar = mView.findViewById(R.id.progressBar);
        mapGeolocalisationButton = mView.findViewById(R.id.fragment_map_gps_geolocalisation_button);

        actionOnFloatingButton();

        if(getActivity()!=null){
            ((RestaurantActivity) getActivity()).setMapFragmentRefreshListener(this::initializeMap);
        }

        initializeMap();


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
        if(ManageAutocompleteResponse.getStringAutocomplete((context), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_ID)!=null){
            initializeMap();
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideProgressBar(){
        mProgressBar.setVisibility(View.GONE);
    }

    private void initializeMap() {

        showProgressBar();
        String sharedPreferenceRestaurantPlaceId = ManageAutocompleteResponse.getStringAutocomplete((context), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_ID);


        if (sharedPreferenceRestaurantPlaceId != null) {

            double latitude = ManageAutocompleteResponse.getDoubleAutocomplete(context, ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LATITUDE);
            double longitude = ManageAutocompleteResponse.getDoubleAutocomplete(context, ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LONGITUDE);
            customizeMarker(sharedPreferenceRestaurantPlaceId, latitude, longitude);

            moveCameraAutocompleChoice(latitude,longitude);
            //initializeSharedPreferences();



        } else {


            if (ManageAppMode.getAppMode(context).equals(getString(R.string.app_mode_work)) || ManageAppMode.getAppMode(context).equals(getString(R.string.app_mode_forced_work))) {
                RestaurantHelper.getAllRestaurants().get().addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);

                        String restaurantPlaceId = restaurant.getRestaurantPlaceId();
                        double latitude = restaurant.getLatitude();
                        double longitude = restaurant.getLongitude();

                        customizeMarker(restaurantPlaceId, latitude, longitude);

                    }

                    MoveCamera();

                }).addOnFailureListener(e -> {

                });


            } else {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    RestaurantInNormalModeHelper.getAllRestaurants(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);

                            String restaurantPlaceId = restaurant.getRestaurantPlaceId();
                            double latitude = restaurant.getLatitude();
                            double longitude = restaurant.getLongitude();
                            customizeMarker(restaurantPlaceId, latitude, longitude);

                        }

                        MoveCamera();
                    }).addOnFailureListener(e -> {

                    });
                }

            }

        }

    }

    private void initializeSharedPreferences() {
        ManageAutocompleteResponse.saveAutocompleteStringResponse(context, ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_ID, null);
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


        if (mMarker != null) {
            mMarker.remove();
        }

        LatLng latLng = new LatLng(lat, longi);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.snippet(restaurantPlaceId);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            if (ManageAppMode.getAppMode(context).equals(getString(R.string.app_mode_work)) || ManageAppMode.getAppMode(context).equals(getString(R.string.app_mode_forced_work))) {

                RestaurantHelper.getRestaurant(restaurantPlaceId).addOnCompleteListener(task -> {
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
                                }
                            }
                        }


                    }
                });


            } else {
                RestaurantInNormalModeHelper.getRestaurant(FirebaseAuth.getInstance().getCurrentUser().getUid(), restaurantPlaceId).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            Restaurant restaurant = document.toObject(Restaurant.class);

                            if (restaurant != null) {
                                if (document.exists()) {

                                    markerOptions.position(latLng);

                                    //creating and getting restaurant information

                                    if (restaurantPlaceId.equals(ManageRestaurantChoiceInNormalMode.getRestaurantChoice(App.getInstance().getApplicationContext()))) {
                                        markerOptions.icon(bitmapDescriptorFromVectorSelected(getContext()));
                                        mGoogleMap.addMarker(markerOptions);
                                    } else {
                                        markerOptions.icon(bitmapDescriptorFromVectorNotSelected(getContext()));
                                        mGoogleMap.addMarker(markerOptions);

                                    }

                                }
                            }
                        }


                    }
                });
            }
        }




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

    private void MoveCamera() {

        int mZoom = 15;
        int mBearing = 4;
        int mTilt = 35;

        LatLng latLngCurrent = new LatLng(myCurrentLat, myCurrentLongi);

        String[] latlong =  ManagePosition.getPosition(context,KEY_POSITION_DATA).split(",");
        double savedLatitude = Double.parseDouble(latlong[0]);
        double savedLongitude = Double.parseDouble(latlong[1]);
        LatLng saveLatLng = new LatLng(savedLatitude,savedLongitude);


        String[] latlongJob =  ManagePosition.getPosition(context, KEY_POSITION_JOB_LAT_LNG_DATA).split(",");
        double savedJobLatitude = Double.parseDouble(latlongJob[0]);
        double savedJobLongitude = Double.parseDouble(latlongJob[1]);
        LatLng savedLatLngJob = new LatLng(savedJobLatitude,savedJobLongitude);

        if(mGoogleMap!=null){
            if(ManageAppMode.getAppMode(context).equals(getString(R.string.app_mode_normal))){
                if (myCurrentLat == 0.0) {

                    CameraPosition Liberty = CameraPosition.builder().target(saveLatLng).zoom(mZoom).bearing(mBearing).tilt(mTilt).build();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(saveLatLng));
                } else {

                    CameraPosition Liberty = CameraPosition.builder().target(latLngCurrent).zoom(mZoom).bearing(mBearing).tilt(mTilt).build();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngCurrent));
                }
            }else{

                CameraPosition Liberty = CameraPosition.builder().target(savedLatLngJob).zoom(mZoom).bearing(mBearing).tilt(mTilt).build();
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(savedLatLngJob));



            }

        }

        hideProgressBar();

    }

    private void moveCameraAutocompleChoice(double lat,double lng) {

        int mZoom = 18;
        int mBearing = 4;
        int mTilt = 35;

        LatLng autocompleLatLng = new LatLng(lat,lng);

        if(mGoogleMap!=null){


                    CameraPosition Liberty = CameraPosition.builder().target(autocompleLatLng).zoom(mZoom).bearing(mBearing).tilt(mTilt).build();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(autocompleLatLng));

        }

        hideProgressBar();

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
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng initialPosition = new LatLng(0, 0);

        mMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(initialPosition));
    }


}