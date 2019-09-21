package com.inved.go4lunch.firebase;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurants";
    private static final String COLLECTION_GENERAL = "location";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection(String jobPlaceId) {
        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL).document(jobPlaceId).collection(COLLECTION_NAME);

    }

    // --- CREATE ---

    public static Task<Void> createRestaurant(String restaurantPlaceId,
                                              int restaurantCustomers,
                                              int restaurantLike,
                                              String jobPlaceId,
                                              String restaurantName,
                                              int ratingApp,
                                              boolean openForLunch,
                                              String distance,
                                              int openHours,
                                              int closeHours,
                                              String restaurantAddress,
                                              double latitude,
                                              double longitude,
                                              String website,
                                              String phoneNumber,
                                              int openMinutes,
                                              int closeMinutes) {
        // 1 - Create Obj

        Restaurant restaurantToCreate = new Restaurant(restaurantPlaceId, restaurantCustomers, restaurantLike, jobPlaceId, restaurantName, ratingApp, openForLunch, distance
                , openHours, closeHours, restaurantAddress, latitude, longitude, website, phoneNumber, openMinutes, closeMinutes);

        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(restaurantPlaceId).set(restaurantToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getRestaurant(String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).get();
    }


    public static Query sortRestaurant(String jobPlaceId, int ratingApp, boolean openForLunch, int restaurantCustomers, Double distance) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId)
                .whereGreaterThanOrEqualTo("ratingApp", ratingApp)
                .whereEqualTo("openForLunch", openForLunch)
                .whereGreaterThanOrEqualTo("restaurantCustomers", restaurantCustomers)
                .whereGreaterThanOrEqualTo("distance", distance)
                //  .orderBy("restaurantName")
                .limit(30);

    }

    public static Query getAllRestaurants(String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId);


    }

    public static Query getFilterRestaurant(String jobPlaceId,String queryFilter) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId)
                .whereEqualTo("restaurantName",queryFilter);

    }

    // --- UPDATE ---

    public static Task<Void> updateRestaurant(String restaurantPlaceId, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("restaurantPlaceId", restaurantPlaceId);
    }

    public static Task<Void> updateRestaurantLike(int restaurantLike, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("restaurantLike", restaurantLike);
    }

    public static Task<Void> updateRestaurantCustomers(int restaurantCustomers, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("restaurantCustomers", restaurantCustomers);
    }

    public static Task<Void> updateRestaurantName(String restaurantName, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("restaurantName", restaurantName);
    }

    public static Task<Void> updateRestaurantRatingApp(int ratingApp, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("ratingApp", ratingApp);
    }

    public static Task<Void> updateRestaurantOpenForLunch(boolean openForLunch, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("openForLunch", openForLunch);
    }

    public static Task<Void> updateRestaurantPhoneNumber(String phoneNumber, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("phoneNumber", phoneNumber);
    }

    public static Task<Void> updateRestaurantPhotoReference(String photoReference, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("photoReference", photoReference);
    }

    public static Task<Void> updateRestaurantOpenHours(int openHours, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("openHours", openHours);
    }

    public static Task<Void> updateRestaurantCloseHours(int closeHours, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("closeHours", closeHours);
    }

    public static Task<Void> updateRestaurantWebsite(String website, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("website", website);
    }

    public static Task<Void> updateRestaurantOpenMinutes(int openMinutes, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("openMinutes", openMinutes);
    }

    public static Task<Void> updateRestaurantCloseMinutes(int closeMinutes, String id, String jobPlaceId) {
        return RestaurantHelper.getRestaurantsCollection(jobPlaceId).document(id).update("closeMinutes", closeMinutes);

    }
}
