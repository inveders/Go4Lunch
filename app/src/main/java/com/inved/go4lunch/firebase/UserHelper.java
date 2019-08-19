package com.inved.go4lunch.firebase;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class UserHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_GENERAL = "location";

    private static final String SUB_COLLECTION_FAVORITE_RESTAURANT = "FavoritesRestaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(String jobPlaceId){
        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL).document(jobPlaceId).collection(COLLECTION_NAME);

    }

    public static CollectionReference getUsersWhateverLocation(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL).document().collection(COLLECTION_NAME);

    }




    // --- CREATE ---

    public static Task<Void> createUser(String uid, String firstname, String lastname,String urlPicture, String restaurantPlaceId,String restaurantType,String restaurantName,String restaurantVicinity,String jobAddress,String jobPlaceId, String jobName) {
        // 1 - Create Obj

        User userToCreate = new User(uid, firstname,lastname, urlPicture,restaurantPlaceId,restaurantType,restaurantName,restaurantVicinity,jobAddress,jobPlaceId,jobName);
        Log.d("Debago", "createUser in Userhelper "+jobAddress+" "+jobName+" "+jobPlaceId);
        return UserHelper.getUsersCollection(jobPlaceId).document(uid).set(userToCreate);
    }



    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid,String jobPlaceId){
        return UserHelper.getUsersCollection(jobPlaceId).document(uid).get();
    }

    public static Query getUserWhateverLocation(String uid){

        return FirebaseFirestore.getInstance().collectionGroup(COLLECTION_NAME)

                .whereEqualTo("uid","J3wgqJ67umfacBCGWHyvPAnmkzk2");

    }

    public static Query getAllUsers(String jobPlaceId){
        return UserHelper.getUsersCollection(jobPlaceId);


    }

    public static Query getUserWithSameUid(String uid,String jobPlaceId){
        return UserHelper.getUsersCollection(jobPlaceId)
                .whereEqualTo("uid",uid);

    }

    public static Query getAllWorkmatesJoining(String currentPlacedId,String jobPlaceId){
        return UserHelper.getUsersCollection(jobPlaceId)
                .whereEqualTo("restaurantPlaceId",currentPlacedId)
                .limit(15);


    }

    // --- UPDATE ---

    public static Task<Void> updateFirstname(String firstname, String uid) {
        return UserHelper.getUsersWhateverLocation().document(uid).update("firstname", firstname);
    }

    public static Task<Void> updateLastname(String lastname, String uid) {
        return UserHelper.getUsersWhateverLocation().document(uid).update("lastname", lastname);
    }

    public static Task<Void> updateRestaurantPlaceId(String restaurantPlaceId, String uid) {
        return UserHelper.getUsersWhateverLocation().document(uid).update("restaurantPlaceId", restaurantPlaceId);
    }

    public static Task<Void> updateRestaurantName(String restaurantName, String uid) {
        return UserHelper.getUsersWhateverLocation().document(uid).update("restaurantName", restaurantName);
    }

    public static Task<Void> updateRestaurantVicinity(String restaurantVicinity, String uid) {
        return UserHelper.getUsersWhateverLocation().document(uid).update("restaurantVicinity", restaurantVicinity);
    }

    public static Task<Void> updateRestaurantType(String restaurantType, String uid,String jobPlaceId) {
        return UserHelper.getUsersCollection(jobPlaceId).document(uid).update("restaurantType", restaurantType);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersWhateverLocation().document(uid).delete();
    }

}