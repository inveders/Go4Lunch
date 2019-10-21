package com.inved.go4lunch.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageJobPlaceId;

public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurants";
    private static final String COLLECTION_GENERAL = "location";

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL)
                .document(ManageJobPlaceId.getJobPlaceId(App.getInstance()
                .getApplicationContext())).collection(COLLECTION_NAME);

    }

    // --- CREATE ---

    public static void createRestaurant(String restaurantPlaceId,
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

        RestaurantHelper.getRestaurantsCollection().document(restaurantPlaceId).set(restaurantToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getRestaurant(String id) {
        return RestaurantHelper.getRestaurantsCollection().document(id).get();
    }

    public static Query getAllRestaurants() {
        return RestaurantHelper.getRestaurantsCollection();


    }

    public static Query getFilterRestaurant(String queryFilter) {
        return RestaurantHelper.getRestaurantsCollection()
                .whereEqualTo("restaurantName",queryFilter);

    }

    // --- UPDATE ---

    public static void updateRestaurantLike(int restaurantLike, String id) {
        RestaurantHelper.getRestaurantsCollection().document(id).update("restaurantLike", restaurantLike);
    }

    public static void updateRestaurantCustomers(int restaurantCustomers, String id) {
        RestaurantHelper.getRestaurantsCollection()
                .document(id)
                .update("restaurantCustomers", restaurantCustomers);
    }

    public static void updateRestaurantOpenForLunch(boolean openForLunch, String id) {
        RestaurantHelper.getRestaurantsCollection().document(id).update("openForLunch", openForLunch);
    }

    public static void updateRestaurantPhoneNumber(String phoneNumber, String id) {
        RestaurantHelper.getRestaurantsCollection().document(id).update("phoneNumber", phoneNumber);
    }

    public static void updateRestaurantOpenHours(int openHours, String id) {
        RestaurantHelper.getRestaurantsCollection().document(id).update("openHours", openHours);
    }

    public static void updateRestaurantCloseHours(int closeHours, String id) {
        RestaurantHelper.getRestaurantsCollection().document(id).update("closeHours", closeHours);
    }

    public static void updateRestaurantWebsite(String website, String id) {
        RestaurantHelper.getRestaurantsCollection().document(id).update("website", website);
    }

    public static void updateRestaurantOpenMinutes(int openMinutes, String id) {
        RestaurantHelper.getRestaurantsCollection().document(id).update("openMinutes", openMinutes);
    }

    public static void updateRestaurantCloseMinutes(int closeMinutes, String id) {
        RestaurantHelper.getRestaurantsCollection().document(id).update("closeMinutes", closeMinutes);

    }
}
