package com.inved.go4lunch.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class UserHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_GENERAL = "location";
    private static final String DOCUMENT_NAME = "Gnimdev";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL).document(DOCUMENT_NAME).collection(COLLECTION_NAME);

    }




    // --- CREATE ---

    public static Task<Void> createUser(String uid, String firstname, String lastname,String urlPicture, String restaurantPlaceId,String restaurantType,String restaurantName,String restaurantVicinity) {
        // 1 - Create Obj

        User userToCreate = new User(uid, firstname,lastname, urlPicture,restaurantPlaceId,restaurantType,restaurantName,restaurantVicinity);

        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }


    public static Query getAllUsers(){
        return UserHelper.getUsersCollection();


    }

    public static Query getUserWithSameUid(String uid){
        return UserHelper.getUsersCollection()
                .whereEqualTo("uid",uid);

    }

    public static Query getAllWorkmatesJoining(String currentPlacedId){
        return UserHelper.getUsersCollection()
                .whereEqualTo("restaurantPlaceId",currentPlacedId)
                .limit(15);


    }

    // --- UPDATE ---

    public static Task<Void> updateFirstname(String firstname, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("firstname", firstname);
    }

    public static Task<Void> updateLastname(String lastname, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("lastname", lastname);
    }

    public static Task<Void> updateRestaurantPlaceId(String restaurantPlaceId, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("restaurantPlaceId", restaurantPlaceId);
    }

    public static Task<Void> updateRestaurantName(String restaurantName, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("restaurantName", restaurantName);
    }

    public static Task<Void> updateRestaurantVicinity(String restaurantVicinity, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("restaurantVicinity", restaurantVicinity);
    }

    public static Task<Void> updateRestaurantType(String restaurantType, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("restaurantType", restaurantType);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}