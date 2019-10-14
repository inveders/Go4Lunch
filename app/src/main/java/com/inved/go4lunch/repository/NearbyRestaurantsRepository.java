package com.inved.go4lunch.repository;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.firebase.RestaurantInNormalModeHelper;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageAppMode;
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
import static com.inved.go4lunch.controller.activity.RestaurantActivity.MAP_API_KEY;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.TAG;

public class NearbyRestaurantsRepository {

    private String jobPlaceId = ManageJobPlaceId.getJobPlaceId(App.getInstance().getApplicationContext());
    private Context context = App.getInstance().getApplicationContext();
    private String appMode = ManageAppMode.getAppMode(context);
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
    private int openMinutes;
    private int closeMinutes;
    private boolean openForLunch;
    private boolean isOpen;
    private UnitConversion unitConversion = new UnitConversion();
    private String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    public NearbyRestaurantsRepository() {
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

        if (appMode.equals(App.getResourses().getString(R.string.app_mode_normal))) {

            deleteAllRestaurantInNormalMode();
        }

        if(appMode.equals(App.getResourses().getString(R.string.app_mode_forced_work))){
            Log.d("debago","NearbyRestaurantRepository, i'm in forced work mode, I have update restaurant near from my work");
            updateFirebaseWithRestaurantsFromMyWorkIfExist();
        }

    }

    private void updateFirebaseWithRestaurantsFromMyWorkIfExist() {

        RestaurantHelper.getAllRestaurants(jobPlaceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    for (DocumentSnapshot querySnapshot : task.getResult()) {
                        //We delete each existing restaurant before recreating all
                        fetchPlaceDetailRequest(querySnapshot.getString("restaurantPlaceId"));
                    }

                }else{
                    Toast.makeText(context, App.getResourses().getString(R.string.app_mode_forced_mode_no_restaurant_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteAllRestaurantInNormalMode() {
        RestaurantInNormalModeHelper.getAllRestaurants(currentUser, jobPlaceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    for (DocumentSnapshot querySnapshot : task.getResult()) {
                        //We delete each existing restaurant before recreating all
                        RestaurantInNormalModeHelper.deleteRestaurantsInNormalMode(currentUser, querySnapshot.getString("restaurantPlaceId"), jobPlaceId);
                        Log.d("debago","NearbyRestaurantRepository, we delete restaurnat "+querySnapshot.getString("restaurantName"));
                        setNearbyRestaurantsInFirebase();
                    }

                }
            }
        });
    }

    public void setNearbyRestaurantsInFirebase() {

        // Initialize Places.
        Places.initialize(context, MAP_API_KEY);
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(context);

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG, Place.Field.TYPES, Place.Field.RATING, Place.Field.ADDRESS);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

        placesClient.findCurrentPlace(request).addOnSuccessListener((response) ->
        {

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
                                /** distance = distanceCalcul(latitude, longitude);*/
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

        }).addOnFailureListener((exception) ->

                {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.d("Debaga", "Place not found: " + apiException.getStatusCode());
                        Toast.makeText(context, App.getResourses().getString(R.string.no_network_to_find_restaurant), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void createRestaurantsInFirebase(String restaurantPlaceId, String restaurantName, int rating, double latitude, double longitude,
                                             String distance, String restaurantAddress) {
        //Create restaurant in firebase if it doesn't exist
        Log.d("debago","NearbyRestaurant in Create Restaurant");

        if (appMode.equals(App.getResourses().getString(R.string.app_mode_work))) {
            Log.d("debago","NearbyRestaurant we are in work mode");
            RestaurantHelper.getRestaurant(restaurantPlaceId, jobPlaceId).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (!document.exists()) {

                        RestaurantHelper.createRestaurant(restaurantPlaceId, 0, 0, jobPlaceId, restaurantName, rating, false, distance, 0, 0, restaurantAddress, latitude, longitude, null, null, 0, 0);

                    }
                }
            });
        } else {

            Log.d("debago","NearbyRestaurant we create in database restaurant in normal mode "+restaurantPlaceId);
            RestaurantInNormalModeHelper.createRestaurantsInNormalMode(currentUser, restaurantPlaceId, 0, 0, jobPlaceId, restaurantName, rating, false, distance, 0, 0, restaurantAddress, latitude, longitude, null, null, 0, 0);

        }


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
        if (latitude != 0 || longitude != 0) {
            double latitudeRestaurant = unitConversion.convertRad(latitude);
            Double longitudeRestaurant = unitConversion.convertRad(longitude);
            double latCurrent = unitConversion.convertRad(myCurrentLat);
            Double longiCurrent = unitConversion.convertRad(myCurrentLongi);

            DecimalFormat df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.HALF_UP);

            double distanceDouble = Math.acos(Math.sin(latCurrent) * Math.sin(latitudeRestaurant) + Math.cos(latCurrent) * Math.cos(latitudeRestaurant) * Math.cos(longitudeRestaurant - longiCurrent)) * 6371 * 1000;

            return df.format(distanceDouble);
        }

        return null;


    }

    private int openHoursCalcul(OpeningHours openingHours) {

        LocalDateTime currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = LocalDateTime.now();
        }
        int current_day = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            current_day = currentTime.getDayOfWeek().getValue() - 1;
        }

        if (openingHours != null) {

            if (openingHours.getPeriods().get(current_day).getOpen() != null) {


                return Objects.requireNonNull(openingHours.getPeriods().get(current_day).getOpen()).getTime().getHours();

            }
        }


        return -1;

    }

    private int openMinutesCalcul(OpeningHours openingHours) {

        LocalDateTime currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = LocalDateTime.now();
        }
        int current_day = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            current_day = currentTime.getDayOfWeek().getValue() - 1;
        }

        if (openingHours != null) {

            if (openingHours.getPeriods().get(current_day).getOpen() != null) {


                return Objects.requireNonNull(openingHours.getPeriods().get(current_day).getOpen()).getTime().getMinutes();

            }
        }


        return -1;

    }

    private int closeHoursCalcul(OpeningHours openingHours) {

        LocalDateTime currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = LocalDateTime.now();
        }
        int current_day = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            current_day = currentTime.getDayOfWeek().getValue() - 1;
        }

        if (openingHours != null) {
            if (openingHours.getPeriods().get(current_day).getClose() != null) {

                return Objects.requireNonNull(openingHours.getPeriods().get(current_day).getClose()).getTime().getHours();

            }
        }

        return -1;

    }

    private int closeMinutesCalcul(OpeningHours openingHours) {

        LocalDateTime currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = LocalDateTime.now();
        }
        int current_day = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            current_day = currentTime.getDayOfWeek().getValue() - 1;
        }

        if (openingHours != null) {
            if (openingHours.getPeriods().get(current_day).getClose() != null) {

                return Objects.requireNonNull(openingHours.getPeriods().get(current_day).getClose()).getTime().getMinutes();

            }
        }

        return -1;

    }

    private void fetchPlaceDetailRequest(String currentPlaceId) {

        // Initialize Places.
        Places.initialize(App.getInstance().getApplicationContext(), MAP_API_KEY);

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
            openMinutes = openMinutesCalcul(place.getOpeningHours());
            closeMinutes = closeMinutesCalcul(place.getOpeningHours());
            closeHours = closeHoursCalcul(place.getOpeningHours());

            if (place.isOpen() == null) {
                openForLunch = false;
            } else {
                isOpen = place.isOpen();
                openForLunch = isOpen && closeHours < 15;
            }

            if(appMode.equals("work")){
                RestaurantHelper.updateRestaurantPhoneNumber(phoneNumber, currentPlaceId, jobPlaceId);
                RestaurantHelper.updateRestaurantWebsite(website, currentPlaceId, jobPlaceId);
                RestaurantHelper.updateRestaurantOpenHours(openHours, currentPlaceId, jobPlaceId);
                RestaurantHelper.updateRestaurantCloseHours(closeHours, currentPlaceId, jobPlaceId);
                RestaurantHelper.updateRestaurantOpenForLunch(openForLunch, currentPlaceId, jobPlaceId);
                RestaurantHelper.updateRestaurantOpenMinutes(openMinutes, currentPlaceId, jobPlaceId);
                RestaurantHelper.updateRestaurantCloseMinutes(closeMinutes, currentPlaceId, jobPlaceId);
            }else{
                RestaurantInNormalModeHelper.updateRestaurantPhoneNumber(currentUser,phoneNumber, currentPlaceId, jobPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantWebsite(currentUser,website, currentPlaceId, jobPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantOpenHours(currentUser,openHours, currentPlaceId, jobPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantCloseHours(currentUser,closeHours, currentPlaceId, jobPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantOpenForLunch(currentUser,openForLunch, currentPlaceId, jobPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantOpenMinutes(currentUser,openMinutes, currentPlaceId, jobPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantCloseMinutes(currentUser,closeMinutes, currentPlaceId, jobPlaceId);
            }


        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {

                Log.e(TAG, "Place detail not found: " + exception.getMessage());
            }
        });


    }

}
