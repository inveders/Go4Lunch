package com.inved.go4lunch.repository;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageJobPlaceId;
import com.inved.go4lunch.utils.UnitConversion;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.TAG;
import static com.inved.go4lunch.utils.ManageJobPlaceId.KEY_JOB_PLACE_ID_DATA;

public class NearbyRestaurantsRepository {

    private String jobPlaceId = ManageJobPlaceId.getJobPlaceId(App.getInstance().getApplicationContext(), KEY_JOB_PLACE_ID_DATA);
    private Context context = App.getInstance().getApplicationContext();
    private int rating;
    private double latitude;
    private double longitude;
    private String distance;
    private String restaurantAddress;
    private String restaurantPlaceId;
    private String restaurantName;
    private String website;
    private String phoneNumber;
    private Double myCurrentLat;
    private Double myCurrentLongi;
    private int openHours;
    private int closeHours;
    private boolean openForLunch;
    private boolean isOpen;
    private UnitConversion unitConversion = new UnitConversion();


    public NearbyRestaurantsRepository() {
        Log.d("debaga", "on est dans NearbyRestaurantRepository constructor");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {
                    myCurrentLat = intent.getDoubleExtra(KEY_LATITUDE, 0.0);
                    myCurrentLongi = intent.getDoubleExtra(KEY_LONGITUDE, 0.0);
                }

            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));
    }

    public void setNearbyRestaurantsInFirebase() {

        Log.d("debaga", "on est dans setrestaurantInFirebase");

        // Initialize Places.
        Places.initialize(context, App.getResourses().getString(R.string.google_api_key));
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(context);

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG, Place.Field.TYPES, Place.Field.RATING, Place.Field.ADDRESS);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

        placesClient.findCurrentPlace(request).addOnSuccessListener((response) -> {


            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {

                List typesList = placeLikelihood.getPlace().getTypes();

                if (typesList != null) {
                    for (int i = 0; i < typesList.size(); i++) {
                        if ("RESTAURANT".equals(typesList.get(i).toString())) {

                            restaurantPlaceId = placeLikelihood.getPlace().getId();
                            restaurantName = placeLikelihood.getPlace().getName();
                            restaurantAddress = placeLikelihood.getPlace().getAddress();


                            if (placeLikelihood.getPlace().getRating() != null) {
                                rating = ratingValueCalcul(placeLikelihood.getPlace().getRating());
                            } else {
                                rating = 0;
                            }

                            if (placeLikelihood.getPlace().getLatLng() != null) {
                                latitude = placeLikelihood.getPlace().getLatLng().latitude;
                                longitude = placeLikelihood.getPlace().getLatLng().longitude;
                                distance = distanceCalcul(latitude, longitude);
                            } else {
                                latitude = 0.0;
                                longitude = 0.0;
                                distance = null;
                            }


                            createRestaurantsInFirebase(restaurantPlaceId, restaurantName, rating, latitude, longitude, distance, restaurantAddress);

                            fetchPlaceDetailRequest(restaurantPlaceId);


                        }

                    }
                }


            }
        }).

                addOnFailureListener((exception) ->

                {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("Debaga", "Place not found: " + apiException.getStatusCode());
                    }
                });
    }


    private void createRestaurantsInFirebase(String restaurantPlaceId, String restaurantName, int rating, double latitude, double longitude,
                                             String distance, String restaurantAddress) {
        //Create restaurant in firebase if it doesn't exist

        RestaurantHelper.getRestaurant(restaurantPlaceId, jobPlaceId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (!document.exists()) {

                    RestaurantHelper.createRestaurant(restaurantPlaceId, 0, 0, jobPlaceId, restaurantName, rating, false, distance, 0, 0, restaurantAddress, latitude, longitude, null, null);

                }
            }
        });


    }

    private int ratingValueCalcul(double ratingValue) {

        int rating = 0;
        if (ratingValue > 0 && ratingValue < 1.665) {
            rating = 1;
        } else if (ratingValue >= 1.665 && ratingValue < 3.33) {
            rating = 2;
        } else if (ratingValue >= 3.33 && ratingValue <= 5) {
            rating = 3;
        }
        return rating;

    }


    private String distanceCalcul(double latitude, double longitude) {

        //DISTANCE
        double latitudeRestaurant = unitConversion.convertRad(latitude);
        Double longitudeRestaurant = unitConversion.convertRad(longitude);
        Log.d("debago","NearbyRestaurantRepository latCurrent est "+myCurrentLat);
        double latCurrent = unitConversion.convertRad(myCurrentLat);
        Double longiCurrent = unitConversion.convertRad(myCurrentLongi);

        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.HALF_UP);

        double distanceDouble = Math.acos(Math.sin(latCurrent) * Math.sin(latitudeRestaurant) + Math.cos(latCurrent) * Math.cos(latitudeRestaurant) * Math.cos(longitudeRestaurant - longiCurrent)) * 6371 * 1000;

        return df.format(distanceDouble);


    }

    private int openHoursCalcul(OpeningHours openingHours) {

        LocalDateTime currentTime = LocalDateTime.now();
        int current_day = currentTime.getDayOfWeek().getValue();

        if (openingHours != null) {

            if (openingHours.getPeriods().get(current_day).getOpen() != null) {


                return Objects.requireNonNull(openingHours.getPeriods().get(current_day).getOpen()).getTime().getHours();

            }
        }


        return -1;

    }

    private int closeHoursCalcul(OpeningHours openingHours) {

        LocalDateTime currentTime = LocalDateTime.now();
        int current_day = currentTime.getDayOfWeek().getValue();

        if (openingHours != null) {
            if (openingHours.getPeriods().get(current_day).getClose() != null) {

                return Objects.requireNonNull(openingHours.getPeriods().get(current_day).getClose()).getTime().getHours();

            }
        }

        return -1;

    }

    private void fetchPlaceDetailRequest(String currentPlaceId) {


        Log.d("debaga", "on est dans fetchPlaceDetailRequest");
        // Initialize Places.
        Places.initialize(App.getInstance().getApplicationContext(), App.getResourses().getString(R.string.google_api_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(App.getInstance().getApplicationContext());

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.OPENING_HOURS);


        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(currentPlaceId, placeFields)
                .build();


        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            phoneNumber = place.getPhoneNumber();

            if (place.getWebsiteUri() != null) {
                website = place.getWebsiteUri().toString();
            } else {
                website = "";
            }


            openHours = openHoursCalcul(place.getOpeningHours());
            closeHours = closeHoursCalcul(place.getOpeningHours());

            if(place.isOpen()==null){
                openForLunch = false;
            }else{
                isOpen = place.isOpen();
                openForLunch = isOpen && closeHours < 15;
            }


            RestaurantHelper.updateRestaurantPhoneNumber(phoneNumber, currentPlaceId, jobPlaceId);
            RestaurantHelper.updateRestaurantWebsite(website, currentPlaceId, jobPlaceId);
            RestaurantHelper.updateRestaurantOpenHours(openHours, currentPlaceId, jobPlaceId);
            RestaurantHelper.updateRestaurantCloseHours(closeHours, currentPlaceId, jobPlaceId);
            RestaurantHelper.updateRestaurantOpenForLunch(openForLunch, currentPlaceId, jobPlaceId);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {

                Log.e(TAG, "Place detail not found: " + exception.getMessage());
            }
        });


    }

}
