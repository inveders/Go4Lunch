package com.inved.go4lunch.repository;

import android.content.Context;
import android.util.Log;

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
import com.inved.go4lunch.utils.ListDay;
import com.inved.go4lunch.utils.ManageAppMode;
import com.inved.go4lunch.utils.ManageJobPlaceId;
import com.inved.go4lunch.utils.UnitConversion;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
    private String website;
    private String phoneNumber;
    private int openHours;
    private int closeHours;
    private int openMinutes;
    private int closeMinutes;
    private boolean openForLunch;
    private UnitConversion unitConversion = new UnitConversion();
    private String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private Calendar calendar = Calendar.getInstance();
    private int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    private int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
    private String stringCurrentDay = ListDay.values()[day].toString();

    //FOR RETROFIT
    private ArrayList<Result> results = new ArrayList<>();
    private MutableLiveData<List<Result>> mutableLiveData = new MutableLiveData<>();

    public NearbyRestaurantsRepository() {

    }

    public MutableLiveData<List<Result>> getNearbySearchMutableLiveData(Double myCurrentLat, Double myCurrentLongi) {

        GoogleNearbySearchApi googleNearbySearchApi = RetrofitServiceNearbySearch.getGoogleNearbySearchApi();

        String location = "" + myCurrentLat + "," + myCurrentLongi + "";
        int radius = 1500;
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
                        if (results.size() > 0) {
                            if (appMode.equals(App.getResourses().getString(R.string.app_mode_normal))) {

                                RestaurantInNormalModeHelper.getAllRestaurants(currentUser).get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        if (task.getResult() != null) {
                                            if (!task.getResult().getDocuments().isEmpty()) {

                                                if (Objects.equals(task.getResult().getDocuments().get(0).getString("restaurantPlaceId"), results.get(0).getPlaceId())) {
                                                    updateAllRestaurantInNormalMode();
                                                } else {
                                                    deleteAllRestaurantInNormalMode(results, myCurrentLat, myCurrentLongi);
                                                }


                                            } else {
                                                setNearbyRestaurantsInFirebase(results, myCurrentLat, myCurrentLongi);
                                            }
                                        }
                                    }
                                });

                            }

                            if (appMode.equals(App.getResourses().getString(R.string.app_mode_forced_work))) {
                                updateFirebaseWithRestaurantsFromMyWorkIfExist();
                            }
                            if (appMode.equals(App.getResourses().getString(R.string.app_mode_work))) {
                                setNearbyRestaurantsInFirebase(results, myCurrentLat, myCurrentLongi);
                            }
                        } else {

                            if (appMode.equals(App.getResourses().getString(R.string.app_mode_normal))) {
                                deleteAllRestaurantInNormalMode(results, myCurrentLat, myCurrentLongi);
                            }

                        }


                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<PlaceSearch> call, @NonNull Throwable t) {
                Log.d("error", "inRetrofit failure :" + t);
            }
        });

        return mutableLiveData;


    }

    private void setNearbyRestaurantsInFirebase(ArrayList<Result> results, Double myCurrentLat, Double myCurrentLongi) {

        for (Result myResult : results) {

            String restaurantPlaceId = myResult.getPlaceId();
            String restaurantName = myResult.getName();
            String restaurantAddress = myResult.getVicinity();
            //isOpen=myResult.getOpeningHours().getOpenNow();

            int rating;
            if (myResult.getRating() != null) {
                rating = ratingValueCalcul(myResult.getRating());
            } else {
                rating = 0;
            }

            double longitude;
            String distance;
            double latitude;
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
                            fetchPlaceDetailRequest(querySnapshot.getString("restaurantPlaceId"));
                        }
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
                        }
                        setNearbyRestaurantsInFirebase(results, myCurrentLat, myCurrentLongi);
                    } else {
                        setNearbyRestaurantsInFirebase(results, myCurrentLat, myCurrentLongi);
                    }
                }

            }
        });
    }

    private void updateAllRestaurantInNormalMode() {
        RestaurantInNormalModeHelper.getAllRestaurants(currentUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if (task.getResult() != null) {
                    if (!task.getResult().getDocuments().isEmpty()) {
                        for (DocumentSnapshot querySnapshot : task.getResult()) {

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

    @SuppressWarnings("ConstantConditions")
    private int openHoursCalcul(OpeningHours openingHours) {

        if (openingHours != null) {

            //if getpriod.size equal to 7
            if (openingHours.getPeriods().size() == 7) {


                if (openingHours.getPeriods().get(day).getOpen() != null) {
                    return openingHours.getPeriods().get(day).getOpen().getTime().getHours();
                }
            } else {
                //if getpriod.size is different from 7
                if (openingHours.getPeriods().size() != 0) {

                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {

                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {

                            //Current hour is less than open hour
                            if (currentHour < openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                int myOccurrence = 0;
                                //We check if there is other same day in the list
                                for (int y = 0; y < openingHours.getPeriods().size(); y++) {
                                    if ((openingHours.getPeriods().get(y).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                                        if (y != i) {
                                            myOccurrence = y;
                                        }

                                    }
                                }
                                //if I have a second even day I check wich is near from my current hour
                                if (myOccurrence != 0) {
                                    int hourOne = openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    int hourTwo = openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getHours();
                                    if (hourOne - currentHour >= 0 && hourOne - currentHour <= hourTwo - currentHour) {
                                        return openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    } else {

                                        return openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getHours();
                                    }
                                }
                                //Else I take only the one
                                else {
                                    return openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                }
                            } else if (currentHour > openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                if (openingHours.getPeriods().get(i).getClose() != null) {
                                    if (currentHour < openingHours.getPeriods().get(i).getClose().getTime().getHours()) {
                                        return openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    }
                                }


                            }

                        }

                    }

                    int noResultFound = 0;
                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {
                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                            noResultFound = i;

                        }
                    }
                    if (noResultFound == 0) {
                        return -1;
                    }


                }
                //if getpriod.size is 0
                else {
                    return -1;
                }

            }

        }
        return -1;

    }

    @SuppressWarnings("ConstantConditions")
    private int openMinutesCalcul(OpeningHours openingHours) {

        if (openingHours != null) {

            //if getpriod.size equal to 7
            if (openingHours.getPeriods().size() == 7) {


                if (openingHours.getPeriods().get(day).getOpen() != null) {
                    return openingHours.getPeriods().get(day).getOpen().getTime().getMinutes();
                }
            } else {
                //if getpriod.size is different from 7
                if (openingHours.getPeriods().size() != 0) {

                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {

                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {

                            //Current hour is less than open hour
                            if (currentHour < openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                int myOccurrence = 0;
                                //We check if there is other same day in the list
                                for (int y = 0; y < openingHours.getPeriods().size(); y++) {
                                    if ((openingHours.getPeriods().get(y).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                                        if (y != i) {
                                            myOccurrence = y;
                                        }

                                    }
                                }
                                //if I have a second even day I check wich is near from my current hour
                                if (myOccurrence != 0) {
                                    int hourOne = openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    int hourTwo = openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getHours();
                                    if (hourOne - currentHour >= 0 && hourOne - currentHour <= hourTwo - currentHour) {
                                        return openingHours.getPeriods().get(i).getOpen().getTime().getMinutes();
                                    } else {

                                        return openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getMinutes();
                                    }
                                }
                                //Else I take only the one
                                else {
                                    return openingHours.getPeriods().get(i).getOpen().getTime().getMinutes();
                                }
                            } else if (currentHour > openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                if (openingHours.getPeriods().get(i).getClose() != null) {
                                    if (currentHour < openingHours.getPeriods().get(i).getClose().getTime().getHours()) {
                                        return openingHours.getPeriods().get(i).getOpen().getTime().getMinutes();
                                    }
                                }


                            }

                        }

                    }

                    int noResultFound = 0;
                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {
                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                            noResultFound = i;

                        }
                    }
                    if (noResultFound == 0) {
                        return -1;
                    }


                }
                //if getpriod.size is 0
                else {
                    return -1;
                }

            }

        }
        return -1;

    }

    @SuppressWarnings("ConstantConditions")
    private int closeHoursCalcul(OpeningHours openingHours) {

        if (openingHours != null) {

            //if getpriod.size equal to 7
            if (openingHours.getPeriods().size() == 7) {

                if (openingHours.getPeriods().get(day).getOpen() != null) {
                    return openingHours.getPeriods().get(day).getClose().getTime().getHours();
                }
            } else {
                //if getpriod.size is different from 7
                if (openingHours.getPeriods().size() != 0) {

                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {

                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {

                            //Current hour is less than open hour
                            if (currentHour < openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                int myOccurrence = 0;
                                //We check if there is other same day in the list
                                for (int y = 0; y < openingHours.getPeriods().size(); y++) {
                                    if ((openingHours.getPeriods().get(y).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                                        if (y != i) {
                                            myOccurrence = y;
                                        }

                                    }
                                }
                                //if I have a second even day I check wich is near from my current hour
                                if (myOccurrence != 0) {
                                    int hourOne = openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    int hourTwo = openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getHours();
                                    if (hourOne - currentHour >= 0 && hourOne - currentHour <= hourTwo - currentHour) {
                                        return openingHours.getPeriods().get(i).getClose().getTime().getHours();
                                    } else {

                                        return openingHours.getPeriods().get(myOccurrence).getClose().getTime().getHours();
                                    }
                                }
                                //Else I take only the one
                                else {
                                    return openingHours.getPeriods().get(i).getClose().getTime().getHours();
                                }
                            } else if (currentHour > openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                if (openingHours.getPeriods().get(i).getClose() != null) {
                                    if (currentHour < openingHours.getPeriods().get(i).getClose().getTime().getHours()) {
                                        return openingHours.getPeriods().get(i).getClose().getTime().getHours();
                                    }
                                }


                            }

                        }

                    }

                    int noResultFound = 0;
                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {
                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                            noResultFound = i;

                        }
                    }
                    if (noResultFound == 0) {
                        return -1;
                    }


                }
                //if getpriod.size is 0
                else {
                    return -1;
                }

            }

        }
        return -1;

    }

    @SuppressWarnings("ConstantConditions")
    private int closeMinutesCalcul(OpeningHours openingHours) {

        if (openingHours != null) {

            //if getpriod.size equal to 7
            if (openingHours.getPeriods().size() == 7) {


                if (openingHours.getPeriods().get(day).getOpen() != null) {
                    return openingHours.getPeriods().get(day).getClose().getTime().getMinutes();
                }
            } else {
                //if getpriod.size is different from 7
                if (openingHours.getPeriods().size() != 0) {

                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {

                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {

                            //Current hour is less than open hour
                            if (currentHour < openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                int myOccurrence = 0;
                                //We check if there is other same day in the list
                                for (int y = 0; y < openingHours.getPeriods().size(); y++) {
                                    if ((openingHours.getPeriods().get(y).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                                        if (y != i) {
                                            myOccurrence = y;
                                        }

                                    }
                                }
                                //if I have a second even day I check wich is near from my current hour
                                if (myOccurrence != 0) {
                                    int hourOne = openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    int hourTwo = openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getHours();
                                    if (hourOne - currentHour >= 0 && hourOne - currentHour <= hourTwo - currentHour) {
                                        return openingHours.getPeriods().get(i).getClose().getTime().getMinutes();
                                    } else {

                                        return openingHours.getPeriods().get(myOccurrence).getClose().getTime().getMinutes();
                                    }
                                }
                                //Else I take only the one
                                else {
                                    return openingHours.getPeriods().get(i).getClose().getTime().getMinutes();
                                }
                            } else if (currentHour > openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                if (openingHours.getPeriods().get(i).getClose() != null) {
                                    if (currentHour < openingHours.getPeriods().get(i).getClose().getTime().getHours()) {
                                        return openingHours.getPeriods().get(i).getClose().getTime().getMinutes();
                                    }
                                }


                            }

                        }

                    }

                    int noResultFound = 0;
                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {
                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                            noResultFound = i;

                        }
                    }
                    if (noResultFound == 0) {
                        return -1;
                    }


                }
                //if getpriod.size is 0
                else {
                    return -1;
                }

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

            openForLunch = openHours <= 12 && closeHours >= 13;

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
