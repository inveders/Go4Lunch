package com.inved.go4lunch.controller;

import android.annotation.SuppressLint;
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

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.utils.ManageAutocompleteResponse;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.inved.go4lunch.controller.RestaurantActivity.TAG;
import static com.inved.go4lunch.utils.ManageJobPlaceId.KEY_JOB_PLACE_ID_DATA;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private View mView;
    private Marker mMarker;
    private String jobPlaceId;


    private PlaceDetailsData placeDetailsData = new PlaceDetailsData();

    public MapFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jobPlaceId = ManageJobPlaceId.getJobPlaceId(Objects.requireNonNull(getActivity()),KEY_JOB_PLACE_ID_DATA);

        ManageAutocompleteResponse.saveAutocompleteStringResponse(Objects.requireNonNull(getContext()),ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_ID,null);
        ManageAutocompleteResponse.saveAutocompleteLongResponseFromDouble(getContext(),ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LATITUDE, 0);
        ManageAutocompleteResponse.saveAutocompleteLongResponseFromDouble(getContext(),ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LONGITUDE,0);
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
        findCurrentPlaceRequest();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("MissingPermission")
    private void findCurrentPlaceRequest(){

        String sharedPreferenceRestaurantPlaceId = ManageAutocompleteResponse.getStringAutocomplete(Objects.requireNonNull(getContext()),ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_ID);
     //   Log.d(TAG, "Mapfragment " + "Avant la recherche placedId "+sharedPreferenceRestaurantPlaceId);
        if(sharedPreferenceRestaurantPlaceId!=null){
            double latitude = ManageAutocompleteResponse.getDoubleAutocomplete(getContext(),ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LATITUDE);
            double longitude = ManageAutocompleteResponse.getDoubleAutocomplete(getContext(),ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LONGITUDE);
            LatLng latLngSharedPreferences = new LatLng(latitude,longitude);
     //       Log.d(TAG, "Mapfragment " + "LatLng de la recherche " +latLngSharedPreferences+" et placedId "+sharedPreferenceRestaurantPlaceId);
            customizeMarker(sharedPreferenceRestaurantPlaceId, latLngSharedPreferences);
            createRestaurantsInFirebase(sharedPreferenceRestaurantPlaceId);

            //InitializeSharedPreferences
            ManageAutocompleteResponse.saveAutocompleteStringResponse(getContext(),ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_ID,null);
            ManageAutocompleteResponse.saveAutocompleteLongResponseFromDouble(getContext(),ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LATITUDE, 0);
            ManageAutocompleteResponse.saveAutocompleteLongResponseFromDouble(getContext(),ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LONGITUDE,0);

        }
        else{
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





    private OnFailureListener onFailureListener() {
        return e -> {
            //  Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            Log.d(TAG, "On failure creation restaurant ");
        };
    }

    private void createRestaurantsInFirebase(String restaurantPlaceId) {
            //Create restaurant in firebase if it doesn't exist

        RestaurantHelper.getRestaurant(restaurantPlaceId,jobPlaceId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {

                    //   Log.d("debago", "Document already exist");


                } else {
                    //   Log.d("debago", "No such document");
                    RestaurantHelper.createRestaurant(restaurantPlaceId, restaurantPlaceId, 0,0,jobPlaceId).addOnFailureListener(onFailureListener());


                }
            } else {
                //     Log.d("debago", "get failed with ", task.getException());
            }
        });






    }


    private void customizeMarker(String restaurantPlaceId, LatLng latLng) {

        mGoogleMap.clear();
        int mZoom = 18;
        int mBearing = 0;
        int mTilt = 45;
        if (mMarker != null) {
            mMarker.remove();
        }


            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.snippet(restaurantPlaceId);


        RestaurantHelper.getRestaurant(restaurantPlaceId,jobPlaceId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
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
        });





//Configure action on marker click
        mGoogleMap.setOnMarkerClickListener(marker -> {

            if (marker.getSnippet() != null) {

                placeDetailsData.setPlaceId(marker.getSnippet());

                startViewPlaceActivity();


            }
            return true;
        });


    }



    private BitmapDescriptor bitmapDescriptorFromVectorSelected(Context context,
                                                                @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_location_selected_24dp);
        assert background != null;
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
        assert vectorDrawable != null;
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}