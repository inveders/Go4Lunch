package com.inved.go4lunch.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserFavoriteRestaurantHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_GENERAL = "location";

    private static final String SUB_COLLECTION_FAVORITE_RESTAURANT = "FavoritesRestaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersFavoriteRestaurantCollection(String uid,String jobPlaceId){
        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL).document(jobPlaceId).collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_FAVORITE_RESTAURANT);

    }


    // --- CREATE ---

    public static Task<Void> createUserFavoriteRestaurants(String uid, String restaurantPlaceId,Boolean isLiked,String jobPlaceId) {
        // 1 - Create subcollection in user

        UserFavoriteRestaurant userFavoriteRestaurantToCreate = new UserFavoriteRestaurant(restaurantPlaceId,isLiked);
        return UserFavoriteRestaurantHelper.getUsersFavoriteRestaurantCollection(uid,jobPlaceId).document(restaurantPlaceId).set(userFavoriteRestaurantToCreate);
    }


    // --- GET ---

    public static Query getCurrentRestaurantPlaceId(String uid, String restaurantPlaceId,String jobPlaceId){
        return UserFavoriteRestaurantHelper.getUsersFavoriteRestaurantCollection(uid,jobPlaceId)
                .whereEqualTo("restaurantPlaceId",restaurantPlaceId);

    }


    // --- UPDATE ---

    public static Task<Void> updateFavoriteRestaurantLiked(String uid, String restaurantPlaceId,Boolean isLiked,String jobPlaceId) {
        return UserFavoriteRestaurantHelper.getUsersFavoriteRestaurantCollection(uid,jobPlaceId).document(restaurantPlaceId).update("isLiked", isLiked);
    }

    // --- DELETE ---


}
