package com.inved.go4lunch.firebase;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RestaurantInNormalModeHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_GENERAL = "location";

    private static final String SUB_COLLECTION_IN_NORMAL_MODE_RESTAURANT = "RestaurantInNormalMode";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsInNormalModeCollection(String uid,String jobPlaceId) {
        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL).document(jobPlaceId).collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_IN_NORMAL_MODE_RESTAURANT);

    }

    // --- CREATE ---

    public static Task<Void> createRestaurantsInNormalMode(String uid,String restaurantPlaceId,
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

        Restaurant restaurantInNormalModeToCreate = new Restaurant(restaurantPlaceId, restaurantCustomers, restaurantLike, jobPlaceId, restaurantName, ratingApp, openForLunch, distance
                , openHours, closeHours, restaurantAddress, latitude, longitude, website, phoneNumber, openMinutes, closeMinutes);
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).set(restaurantInNormalModeToCreate);
    }

    // --- GET ---
    //id is restaurantPlaceId

    public static Task<DocumentSnapshot> getRestaurant(String uid, String restaurantPlaceId, String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).get();
    }


    public static Query sortRestaurantByRatingApp(String uid,String jobPlaceId, int ratingApp, boolean openForLunch) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId)
                .whereEqualTo("openForLunch", openForLunch)
                .whereGreaterThanOrEqualTo("ratingApp", ratingApp);


    }

    public static Query sortRestaurantByRestaurantCustomers(String uid,String jobPlaceId, int restaurantCustomers) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId)

                .whereGreaterThanOrEqualTo("restaurantCustomers", restaurantCustomers);

    }

    public static Query sortRestaurantByDistance(String uid,String jobPlaceId, Double distance) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId)

                .whereGreaterThanOrEqualTo("distance", distance);

    }

    public static Query getAllRestaurants(String uid,String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId);


    }

    public static Query getFilterRestaurant(String uid,String jobPlaceId,String queryFilter) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId)
                .whereEqualTo("restaurantName",queryFilter);

    }

    // --- UPDATE ---

    public static Task<Void> updateRestaurantLike(String uid,int restaurantLike, String restaurantPlaceId, String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).update("restaurantLike", restaurantLike);
    }

    public static Task<Void> updateRestaurantCustomers(String uid,int restaurantCustomers, String restaurantPlaceId, String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).update("restaurantCustomers", restaurantCustomers);
    }

    public static Task<Void> updateRestaurantOpenForLunch(String uid,boolean openForLunch, String restaurantPlaceId, String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).update("openForLunch", openForLunch);
    }

    public static Task<Void> updateRestaurantPhoneNumber(String uid,String phoneNumber, String restaurantPlaceId, String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).update("phoneNumber", phoneNumber);
    }

    public static Task<Void> updateRestaurantOpenHours(String uid,int openHours, String restaurantPlaceId, String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).update("openHours", openHours);
    }

    public static Task<Void> updateRestaurantCloseHours(String uid,int closeHours, String restaurantPlaceId, String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).update("closeHours", closeHours);
    }

    public static Task<Void> updateRestaurantWebsite(String uid,String website, String restaurantPlaceId, String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).update("website", website);
    }

    public static Task<Void> updateRestaurantOpenMinutes(String uid,int openMinutes, String restaurantPlaceId, String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).update("openMinutes", openMinutes);
    }

    public static Task<Void> updateRestaurantCloseMinutes(String uid,int closeMinutes, String restaurantPlaceId, String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).update("closeMinutes", closeMinutes);

    }

    // --- DELETE ---

    public static Task<Void> deleteRestaurantsInNormalMode(String uid,String restaurantPlaceId,String jobPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid,jobPlaceId).document(restaurantPlaceId).delete();
    }
}
