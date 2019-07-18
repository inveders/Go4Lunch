package com.inved.go4lunch.api;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.inved.go4lunch.model.User;

import static com.firebase.ui.auth.AuthUI.TAG;


public class UserHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_GENERAL = "location";
    private static final String DOCUMENT_NAME = "Gnimdev";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_GENERAL).document(DOCUMENT_NAME).collection(COLLECTION_NAME);

    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String firstname, String lastname,String urlPicture, String restaurantName,String restaurantType) {
        // 1 - Create Obj

        com.inved.go4lunch.model.User userToCreate = new User(uid, firstname,lastname, urlPicture,restaurantName,restaurantType);

        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }


    public static Query getAllUsers(){
        return UserHelper.getUsersCollection();

    }
    // --- UPDATE ---

    public static Task<Void> updateFirstname(String firstname, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("firstname", firstname);
    }

    public static Task<Void> updateLastname(String lastname, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("lastname", lastname);
    }

    public static Task<Void> updateRestaurantName(String restaurantName, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("RestaurantName", restaurantName);
    }

    public static Task<Void> updateRestaurantType(String restaurantType, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("RestaurantType", restaurantType);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}