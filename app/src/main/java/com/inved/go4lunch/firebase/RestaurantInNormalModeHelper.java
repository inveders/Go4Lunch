package com.inved.go4lunch.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageJobPlaceId;

public class RestaurantInNormalModeHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_GENERAL = "location";

    private static final String SUB_COLLECTION_IN_NORMAL_MODE_RESTAURANT = "RestaurantInNormalMode";
    // --- COLLECTION REFERENCE ---

    private static CollectionReference getRestaurantsInNormalModeCollection(String uid) {

        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL).document(ManageJobPlaceId.getJobPlaceId(App.getInstance().getApplicationContext())).collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_IN_NORMAL_MODE_RESTAURANT);

    }

    // --- CREATE ---

    public static void createRestaurantsInNormalMode(String uid, String restaurantPlaceId,
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
        RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(restaurantPlaceId).set(restaurantInNormalModeToCreate);
    }

    // --- GET ---
    //id is restaurantPlaceId

    public static Task<DocumentSnapshot> getRestaurant(String uid, String restaurantPlaceId) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(restaurantPlaceId).get();
    }

    public static Query getAllRestaurants(String uid) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid);


    }

    public static Query getFilterRestaurant(String uid,String queryFilter) {
        return RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid)
                .whereEqualTo("restaurantName",queryFilter);

    }

    // --- UPDATE ---

    public static void updateRestaurantOpenForLunch(String uid, boolean openForLunch, String restaurantPlaceId) {
        RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(restaurantPlaceId).update("openForLunch", openForLunch);
    }

    public static void updateRestaurantPhoneNumber(String uid, String phoneNumber, String restaurantPlaceId) {
        RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(restaurantPlaceId).update("phoneNumber", phoneNumber);
    }

    public static void updateRestaurantOpenHours(String uid, int openHours, String restaurantPlaceId) {
        RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(restaurantPlaceId).update("openHours", openHours);
    }

    public static void updateRestaurantCloseHours(String uid, int closeHours, String restaurantPlaceId) {
        RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(restaurantPlaceId).update("closeHours", closeHours);
    }

    public static void updateRestaurantWebsite(String uid, String website, String restaurantPlaceId) {
        RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(restaurantPlaceId).update("website", website);
    }

    public static void updateRestaurantOpenMinutes(String uid, int openMinutes, String restaurantPlaceId) {
        RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(restaurantPlaceId).update("openMinutes", openMinutes);
    }

    public static void updateRestaurantCloseMinutes(String uid, int closeMinutes, String restaurantPlaceId) {
        RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(restaurantPlaceId).update("closeMinutes", closeMinutes);

    }

    public static void updateRestaurantCustomers(String uid, int restaurantCustomers, String id) {
        RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(id).update("restaurantCustomers", restaurantCustomers);
    }

    public static void updateRestaurantLike(String uid,int restaurantLike, String id) {
        RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(id).update("restaurantLike", restaurantLike);
    }

    // --- DELETE ---

    public static void deleteRestaurantsInNormalMode(String uid, String restaurantPlaceId) {
        RestaurantInNormalModeHelper.getRestaurantsInNormalModeCollection(uid).document(restaurantPlaceId).delete();
    }
}
