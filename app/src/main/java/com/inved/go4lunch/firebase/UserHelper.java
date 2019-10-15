package com.inved.go4lunch.firebase;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageJobPlaceId;


public class UserHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_GENERAL = "location";

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL).document(ManageJobPlaceId.getJobPlaceId(App.getInstance().getApplicationContext())).collection(COLLECTION_NAME);

    }




    // --- CREATE ---

    public static Task<Void> createUser(String uid, String firstname, String lastname,String urlPicture, String restaurantPlaceId,String restaurantName,String restaurantVicinity,String jobAddress,String jobPlaceId, String jobName,String token, boolean notificationEnabled) {
        // 1 - Create Obj

        User userToCreate = new User(uid, firstname,lastname, urlPicture,restaurantPlaceId,restaurantName,restaurantVicinity,jobAddress,jobPlaceId,jobName,token,notificationEnabled);
        Log.d("Debago", "createUser in Userhelper "+jobAddress+" "+jobName+" "+jobPlaceId);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }



    // --- GET ---

    public static Query getUserWhateverLocation(String uid){

        return FirebaseFirestore.getInstance().collectionGroup(COLLECTION_NAME)

                .whereEqualTo("uid",uid);

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

    public static void updateRestaurantPlaceId(String restaurantPlaceId, String uid) {
        UserHelper.getUsersCollection().document(uid).update("restaurantPlaceId", restaurantPlaceId);
    }

    public static void updateRestaurantName(String restaurantName, String uid) {
        UserHelper.getUsersCollection().document(uid).update("restaurantName", restaurantName);
    }

    public static void updateRestaurantVicinity(String restaurantVicinity, String uid) {
        UserHelper.getUsersCollection().document(uid).update("restaurantVicinity", restaurantVicinity);
    }

    public static void updateUserToken(String token, String uid) {
        UserHelper.getUsersCollection().document(uid).update("token", token);
    }

    public static void updateNotificationEnabled(boolean isNotificationEnabled, String uid) {
        UserHelper.getUsersCollection().document(uid).update("notificationEnabled", isNotificationEnabled);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}