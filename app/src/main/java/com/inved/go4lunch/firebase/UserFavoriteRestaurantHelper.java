package com.inved.go4lunch.firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageJobPlaceId;

public class UserFavoriteRestaurantHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_GENERAL = "location";

    private static final String SUB_COLLECTION_FAVORITE_RESTAURANT = "FavoritesRestaurants";

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getUsersFavoriteRestaurantCollection(String uid){
        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL).document(ManageJobPlaceId.getJobPlaceId(App.getInstance().getApplicationContext())).collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_FAVORITE_RESTAURANT);

    }


    // --- CREATE ---

    public static void createUserFavoriteRestaurants(String uid, String restaurantPlaceId, Boolean liked) {
        // 1 - Create subcollection in user

        UserFavoriteRestaurant userFavoriteRestaurantToCreate = new UserFavoriteRestaurant(restaurantPlaceId,liked);
        UserFavoriteRestaurantHelper.getUsersFavoriteRestaurantCollection(uid).document(restaurantPlaceId).set(userFavoriteRestaurantToCreate);
    }


    // --- GET ---

    public static Query getCurrentRestaurantPlaceId(String uid, String restaurantPlaceId){
        return UserFavoriteRestaurantHelper.getUsersFavoriteRestaurantCollection(uid)
                .whereEqualTo("restaurantPlaceId",restaurantPlaceId);

    }


    // --- UPDATE ---

    public static void updateFavoriteRestaurantLiked(String uid, String restaurantPlaceId, Boolean liked) {
        UserFavoriteRestaurantHelper.getUsersFavoriteRestaurantCollection(uid).document(restaurantPlaceId).update("liked", liked);
    }

    // --- DELETE ---


}
