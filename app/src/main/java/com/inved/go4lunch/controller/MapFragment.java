package com.inved.go4lunch.controller;

import android.annotation.SuppressLint;
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

import com.google.android.gms.common.api.ApiException;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.utils.ManageJobPlaceId;
import com.inved.go4lunch.utils.MyFirebaseCallback;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_JOB_PLACE_ID_DATA;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_SEARCH_DATA;
import static com.inved.go4lunch.controller.RestaurantActivity.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback {



    GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private Marker mMarker;
    private String jobPlaceId;
    private final float DEFAULT_ZOOM =18;


    PlaceDetailsData placeDetailsData = new PlaceDetailsData();
    RestaurantActivity restaurantActivity = new RestaurantActivity();


    private static final int PERMS_CALL_ID = 1234;

    /**OLD*/
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {
             //   myCurrentGeolocalisation = intent.getStringExtra(KEY_GEOLOCALISATION);

            }



        }
    };


    public MapFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jobPlaceId = ManageJobPlaceId.getJobPlaceId(getActivity(),KEY_JOB_PLACE_ID_DATA);
        Log.d("DEBAGO", "MapFragment oncreate jobplaceid: "+jobPlaceId);
        /**OLD*/
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_SEARCH_DATA));
        findCurrentPlaceRequest();





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
        findCurrentPlaceRequest();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**OLD*/
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    @SuppressLint("MissingPermission")
    private void findCurrentPlaceRequest(){

        // Initialize Places.
        Places.initialize(getContext(), getString(R.string.google_api_key));
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getContext());

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,Place.Field.ID,Place.Field.LAT_LNG,Place.Field.TYPES);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

        placesClient.findCurrentPlace(request).addOnSuccessListener(((response) -> {
            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {

                List typesList = placeLikelihood.getPlace().getTypes();

                assert typesList != null;
                for (int i = 0; i<typesList.size(); i++){
                    if("RESTAURANT".equals(typesList.get(i).toString())){
                        String restaurantPlaceId = placeLikelihood.getPlace().getId();
                        LatLng latLng = placeLikelihood.getPlace().getLatLng();

                        customizeMarker(restaurantPlaceId, latLng);
                        createRestaurantsInFirebase(restaurantPlaceId);
                    }

                }


            }
        })).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

       // MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
    //    mGoogleMap.setMyLocationEnabled(true);
     //   mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    //    mGoogleMap.moveCamera(CameraUpdateFactory.zoomBy(11));





    }



    // Launch View Place Activity
    private void startViewPlaceActivity() {
        Intent intent = new Intent(getContext(), ViewPlaceActivity.class);
        startActivity(intent);
    }





    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //  Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
                Log.d(TAG, "On failure creation restaurant ");
            }
        };
    }

    private void createRestaurantsInFirebase(String restaurantPlaceId) {
            //Create restaurant in firebase if it doesn't exist

            String id = restaurantPlaceId;

        RestaurantHelper.getRestaurant(id,jobPlaceId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        //   Log.d("debago", "Document already exist");


                    } else {
                        //   Log.d("debago", "No such document");
                        RestaurantHelper.createRestaurant(id, id, 0,0,jobPlaceId).addOnFailureListener(onFailureListener());

                        String currentPlaceId = document.getString("id");

                    }
                } else {
                    //     Log.d("debago", "get failed with ", task.getException());
                }
            }
        });






    }


    private void customizeMarker(String restaurantPlaceId, LatLng latLng) {

//        mGoogleMap.clear();
        int mZoom = 18;
        int mBearing = 0;
        int mTilt = 45;
        if (mMarker != null) {
            mMarker.remove();
        }

            String id = restaurantPlaceId;


            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.snippet(restaurantPlaceId);


        RestaurantHelper.getRestaurant(restaurantPlaceId,jobPlaceId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

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





//Configure action on marker click
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getSnippet() != null) {

                    placeDetailsData.setPlaceId(marker.getSnippet());

                    startViewPlaceActivity();


                }
                return true;
            }
        });


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


    public void autocompleteMarker(String restaurantPlaceId, LatLng latLng) {

        clearMap();
        mGoogleMap.addMarker(new MarkerOptions().position(latLng));
     /*   mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {



                    placeDetailsData.setPlaceId(restaurantPlaceId);

                    startViewPlaceActivity();



                return true;
            }
        });*/

//        mGoogleMap.clear();
    /*    int mZoom = 18;
        int mBearing = 0;
        int mTilt = 45;
        if (mMarker != null) {
            mMarker.remove();
        }

        String id = restaurantPlaceId;


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.snippet(restaurantPlaceId);

        restaurants.document(id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

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


//Configure action on marker click
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getSnippet() != null) {

                    placeDetailsData.setPlaceId(marker.getSnippet());

                    startViewPlaceActivity();


                }
                return true;
            }
        });*/


    }

    public void clearMap() {

        GoogleMap gm = createMap();
        if (gm == null) {
            Log.e("clear", "The map is null"); //for testing purposes
            return;

        } else {
            Log.e("clear", "The map already exists"); //for testing purposes
            mGoogleMap = gm;
            mGoogleMap.clear();
        }


     /*  startLocation = null;
        endLocation = null;
        txtFrom.setText("");
        txtTo.setText("");*/

    }

    private GoogleMap createMap() {

        if (mGoogleMap != null) {
            Log.e("create", "The map already exists"); // for testing purposes
            return mGoogleMap;
        }

      //  mMapView = mView.findViewById(R.id.mapfrag);

        try {
            mMapView.getMapAsync(this);
        } catch (Exception ex) {
            Log.e("Error", ex.getLocalizedMessage());
        }

        return null;

    }


}