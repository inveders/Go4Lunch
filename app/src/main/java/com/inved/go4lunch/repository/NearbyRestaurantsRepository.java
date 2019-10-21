package com.inved.go4lunch.repository;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.firebase.RestaurantInNormalModeHelper;
import com.inved.go4lunch.model.placesearch.PlaceSearch;
import com.inved.go4lunch.model.placesearch.Result;
import com.inved.go4lunch.retrofit.GoogleNearbySearchApi;
import com.inved.go4lunch.retrofit.RetrofitServiceNearbySearch;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageAppMode;
import com.inved.go4lunch.utils.ManageJobPlaceId;
import com.inved.go4lunch.utils.UnitConversion;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.MAP_API_KEY;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.TAG;

public class NearbyRestaurantsRepository {

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
    private int openHours;
    private int closeHours;
    private int openMinutes;
    private int closeMinutes;
    private boolean openForLunch;
    private boolean isOpen;
    private UnitConversion unitConversion = new UnitConversion();
    private String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    //FOR RETROFIT
    private ArrayList<Result> results = new ArrayList<>();
    private MutableLiveData<List<Result>> mutableLiveData = new MutableLiveData<>();

    public NearbyRestaurantsRepository() {

    }

    public MutableLiveData<List<Result>> getNearbySearchMutableLiveData(Double myCurrentLat, Double myCurrentLongi) {

        GoogleNearbySearchApi googleNearbySearchApi = RetrofitServiceNearbySearch.getGoogleNearbySearchApi();

        String location = "" + myCurrentLat + "," + myCurrentLongi + "";
        int radius = 400;
        String type = "restaurant";
        Call<PlaceSearch> call = googleNearbySearchApi.getNearbyRestaurants(location, radius, type, MAP_API_KEY);

        call.enqueue(new Callback<PlaceSearch>() {
            @Override
            public void onResponse(@NonNull Call<PlaceSearch> call, @NonNull Response<PlaceSearch> response) {

                PlaceSearch placeSearch = response.body();
                if (placeSearch != null) {
                    if (placeSearch.getResults() != null) {
                        results = (ArrayList<Result>) placeSearch.getResults();
                        mutableLiveData.setValue(results);
                       // Log.d("debago", "NearbyRestaurantRepository, result size " + results.size());
                        if (results.size()>0) {
                            if (appMode.equals(App.getResourses().getString(R.string.app_mode_normal))) {
                             //   Log.d("debago", "NearbyRestaurantRepository, i'm in normal mode " + results);

                                RestaurantInNormalModeHelper.getRestaurant(currentUser, results.get(0).getPlaceId()).addOnCompleteListener(task -> {
                                    if (task.getResult() != null) {
                                        String restaurantId = task.getResult().getString("restaurantPlaceId");
                                        if (task.getResult().getString("restaurantPlaceId") != null) {
                                            if (restaurantId != null) {
                                                if (restaurantId.equals(results.get(0).getPlaceId())) {
                                                    //We are in normal app mode and we are in the same place that restaurants saved in firebase, we just need to update detail as opening hours and others
                                                    updateAllRestaurantInNormalMode(results, myCurrentLat, myCurrentLongi);
                                                } else {
                                                    //We are in normal app mode and we ARE NOT in the same place that restaurant saved in firebase, we need to delete them and re-create new restaurants from current place
                                                    Log.d("debago", "NearbyRestaurantRepository livedata to delete");
                                                    deleteAllRestaurantInNormalMode(results, myCurrentLat, myCurrentLongi);
                                                }
                                            }
                                        } else {
                                            Log.d("debago", "NearbyRestaurantRepository livedata to create");
                                            setNearbyRestaurantsInFirebase(results, myCurrentLat, myCurrentLongi);
                                        }

                                    }

                                });

                            }

                            if (appMode.equals(App.getResourses().getString(R.string.app_mode_forced_work))) {
                             //   Log.d("debago", "NearbyRestaurantRepository, i'm in forced work mode");
                                updateFirebaseWithRestaurantsFromMyWorkIfExist();
                            }
                            if (appMode.equals(App.getResourses().getString(R.string.app_mode_work))) {
                                Log.d("debago", "NearbyRestaurantRepository, i'm in work mode");
                                setNearbyRestaurantsInFirebase(results, myCurrentLat, myCurrentLongi);
                            }
                        } else {

                            if (appMode.equals(App.getResourses().getString(R.string.app_mode_normal))) {
                                deleteAllRestaurantInNormalMode(results, myCurrentLat, myCurrentLongi);
                                Toast.makeText(context, App.getResourses().getString(R.string.no_restaurant_found), Toast.LENGTH_SHORT).show();
                            }

                        }


                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<PlaceSearch> call, @NonNull Throwable t) {
                Log.d("debago", "inRetrofit failure :" + t);
            }
        });

        return mutableLiveData;


    }

    private void setNearbyRestaurantsInFirebase(ArrayList<Result> results, Double myCurrentLat, Double myCurrentLongi) {

        for (Result myResult : results) {

            restaurantPlaceId = myResult.getPlaceId();
            restaurantName = myResult.getName();
            restaurantAddress = myResult.getVicinity();
            //isOpen=myResult.getOpeningHours().getOpenNow();

            if (myResult.getRating() != null) {
                rating = ratingValueCalcul(myResult.getRating());
            } else {
                rating = 0;
            }

            if (myResult.getGeometry().getLocation().getLat() != null) {
                latitude = myResult.getGeometry().getLocation().getLat();
                longitude = myResult.getGeometry().getLocation().getLng();
                distance = distanceCalcul(latitude, longitude, myCurrentLat, myCurrentLongi);
            } else {
                latitude = 0.0;
                longitude = 0.0;
                distance = null;
            }

            createRestaurantsInFirebase(restaurantPlaceId, restaurantName, rating, latitude, longitude, distance, restaurantAddress);

             fetchPlaceDetailRequest(restaurantPlaceId);
        }

    }

    private void updateFirebaseWithRestaurantsFromMyWorkIfExist() {

        RestaurantHelper.getAllRestaurants().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    if (task.getResult().size() > 0) {
                        for (DocumentSnapshot querySnapshot : task.getResult()) {
                            //We delete each existing restaurant before recreating all
                            //Log.d("debago","I place Detail");
                             fetchPlaceDetailRequest(querySnapshot.getString("restaurantPlaceId"));
                        }
                    } else {

                        Toast.makeText(context, App.getResourses().getString(R.string.app_mode_forced_mode_no_restaurant_found), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void deleteAllRestaurantInNormalMode(ArrayList<Result> results, Double
            myCurrentLat, Double myCurrentLongi) {
        RestaurantInNormalModeHelper.getAllRestaurants(currentUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if (task.getResult() != null) {
                    if (!task.getResult().getDocuments().isEmpty()) {
                        for (DocumentSnapshot querySnapshot : task.getResult()) {
                            //We delete each existing restaurant before recreating all

                            RestaurantInNormalModeHelper.deleteRestaurantsInNormalMode(currentUser, querySnapshot.getString("restaurantPlaceId"));
                            // Log.d("debago","NearbyRestaurantRepository, we delete restaurnat "+querySnapshot.getString("restaurantName"));

                        }
                        setNearbyRestaurantsInFirebase(results, myCurrentLat, myCurrentLongi);
                    } else {
                        Log.d("debago", "NearbyRestaurantRepository, no restaurant in firebase, so we create ");
                        setNearbyRestaurantsInFirebase(results, myCurrentLat, myCurrentLongi);
                    }
                }

            }
        });
    }

    private void updateAllRestaurantInNormalMode(ArrayList<Result> results, Double
            myCurrentLat, Double myCurrentLongi) {
        RestaurantInNormalModeHelper.getAllRestaurants(currentUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if (task.getResult() != null) {
                    if (!task.getResult().getDocuments().isEmpty()) {
                        for (DocumentSnapshot querySnapshot : task.getResult()) {
                            //We delete each existing restaurant before recreating all

                            fetchPlaceDetailRequest(querySnapshot.getString("restaurantPlaceId"));

                        }

                    }
                }

            }
        });
    }

    private void createRestaurantsInFirebase(String restaurantPlaceId, String restaurantName,
                                             int rating, double latitude, double longitude,
                                             String distance, String restaurantAddress) {
        //Create restaurant in firebase if it doesn't exist

        if (appMode.equals(App.getResourses().getString(R.string.app_mode_work))) {
            Log.d("debago", "NearbyRestaurant we are in work mode to create restaurant");
            RestaurantHelper.getRestaurant(restaurantPlaceId).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (!document.exists()) {

                        RestaurantHelper.createRestaurant(restaurantPlaceId, 0, 0, ManageJobPlaceId.getJobPlaceId(App.getInstance().getApplicationContext()), restaurantName, rating, false, distance, 0, 0, restaurantAddress, latitude, longitude, null, null, 0, 0);

                    }
                }
            });
        } else {

            //Log.d("debago","NearbyRestaurant we create in database restaurant in normal mode "+restaurantPlaceId);
            RestaurantInNormalModeHelper.createRestaurantsInNormalMode(currentUser, restaurantPlaceId, 0, 0, ManageJobPlaceId.getJobPlaceId(App.getInstance().getApplicationContext()), restaurantName, rating, false, distance, 0, 0, restaurantAddress, latitude, longitude, null, null, 0, 0);

        }


    }

    public int ratingValueCalcul(double ratingValue) {

        int rating = 0;
        if (ratingValue > 0 && ratingValue < 4.3) {
            rating = 1;
        } else if (ratingValue >= 4.3 && ratingValue < 4.6) {
            rating = 2;
        } else if (ratingValue >= 4.6 && ratingValue <= 5) {
            rating = 3;
        }
        return rating;

    }


    private String distanceCalcul(double latitude, double longitude, double
            myCurrentLat, double myCurrentLongi) {

        //DISTANCE
        if (latitude != 0 || longitude != 0) {
            double latitudeRestaurant = unitConversion.convertRad(latitude);
            double longitudeRestaurant = unitConversion.convertRad(longitude);
            double latCurrent = unitConversion.convertRad(myCurrentLat);
            double longiCurrent = unitConversion.convertRad(myCurrentLongi);

            DecimalFormat df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.HALF_UP);

            double distanceDouble = Math.acos(Math.sin(latCurrent) * Math.sin(latitudeRestaurant) + Math.cos(latCurrent) * Math.cos(latitudeRestaurant) * Math.cos(longitudeRestaurant - longiCurrent)) * 6371 * 1000;

            return df.format(distanceDouble);
        }

        return null;


    }


    private int openHoursCalcul(OpeningHours openingHours) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime currentTime = LocalDateTime.now();

            int current_day;
            current_day = currentTime.getDayOfWeek().getValue();
            if (openingHours != null) {


             /*   if (openingHours.getPeriods().get(current_day).getOpen() != null) {

                    return Objects.requireNonNull(openingHours.getPeriods().get(current_day).getOpen()).getTime().getHours();
                }*/
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

           /* if (openingHours.getPeriods().get(current_day).getOpen() != null) {


                return Objects.requireNonNull(openingHours.getPeriods().get(current_day).getOpen()).getTime().getMinutes();

            }*/
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
           /* if (openingHours.getPeriods().get(current_day).getClose() != null) {

                return Objects.requireNonNull(openingHours.getPeriods().get(current_day).getClose()).getTime().getHours();

            }*/
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
         /*   if (openingHours.getPeriods().get(current_day).getClose() != null) {

                return Objects.requireNonNull(openingHours.getPeriods().get(current_day).getClose()).getTime().getMinutes();

            }*/
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

            if (appMode.equals(App.getResourses().getString(R.string.app_mode_work)) || appMode.equals(App.getResourses().getString(R.string.app_mode_forced_work))) {
                RestaurantHelper.updateRestaurantPhoneNumber(phoneNumber, currentPlaceId);
                RestaurantHelper.updateRestaurantWebsite(website, currentPlaceId);
                RestaurantHelper.updateRestaurantOpenHours(openHours, currentPlaceId);
                RestaurantHelper.updateRestaurantCloseHours(closeHours, currentPlaceId);
                RestaurantHelper.updateRestaurantOpenForLunch(openForLunch, currentPlaceId);
                RestaurantHelper.updateRestaurantOpenMinutes(openMinutes, currentPlaceId);
                RestaurantHelper.updateRestaurantCloseMinutes(closeMinutes, currentPlaceId);
            } else {
                RestaurantInNormalModeHelper.updateRestaurantPhoneNumber(currentUser, phoneNumber, currentPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantWebsite(currentUser, website, currentPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantOpenHours(currentUser, openHours, currentPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantCloseHours(currentUser, closeHours, currentPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantOpenForLunch(currentUser, openForLunch, currentPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantOpenMinutes(currentUser, openMinutes, currentPlaceId);
                RestaurantInNormalModeHelper.updateRestaurantCloseMinutes(currentUser, closeMinutes, currentPlaceId);
            }

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {

                Log.e(TAG, "Place detail not found: " + exception.getMessage());
            }
        });


    }

}
