package com.inved.go4lunch.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurants";
    private static final String COLLECTION_GENERAL = "location";
    private static final String DOCUMENT_NAME = "Gnimdev";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL).document(DOCUMENT_NAME).collection(COLLECTION_NAME);

    }

    // --- CREATE ---

    public static Task<Void> createRestaurant(String id, String restaurantPlaceId, int restaurantCustomers, int restaurantLike) {
        // 1 - Create Obj

        Restaurant restaurantToCreate = new Restaurant(id, restaurantPlaceId,restaurantCustomers,restaurantLike);

        return RestaurantHelper.getRestaurantsCollection().document(id).set(restaurantToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getRestaurant(String id){
        return RestaurantHelper.getRestaurantsCollection().document(id).get();
    }


    public static Query getAllRestaurant(){
        return RestaurantHelper.getRestaurantsCollection();

    }

    // --- UPDATE ---

    public static Task<Void> updateRestaurant(String restaurantPlaceId, String id) {
        return RestaurantHelper.getRestaurantsCollection().document(id).update("restaurantPlaceId", restaurantPlaceId);
    }

    public static Task<Void> updateRestaurantCustomers(int restaurantCustomers, String id) {
        return RestaurantHelper.getRestaurantsCollection().document(id).update("restaurantCustomers", restaurantCustomers);
    }

    public static Task<Void> updateRestaurantLike(int restaurantLike, String id) {
        return RestaurantHelper.getRestaurantsCollection().document(id).update("restaurantLike", restaurantLike);
    }

}
