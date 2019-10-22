package com.inved.go4lunch.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.firebase.Restaurant;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.firebase.UserHelper;

public class MyAlarmService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Null restaurant in work mode name for user
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserHelper.updateRestaurantName(null, FirebaseAuth.getInstance().getCurrentUser().getUid());
            UserHelper.updateRestaurantPlaceId(null, FirebaseAuth.getInstance().getCurrentUser().getUid());
            UserHelper.updateRestaurantVicinity(null, FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        //Null restaurant in normal mode for user
        ManageRestaurantChoiceInNormalMode.saveRestaurantChoice(this, null);
        ManageRestaurantChoiceInNormalMode.saveRestaurantName(this, null);
        ManageRestaurantChoiceInNormalMode.saveRestaurantAddress(this, null);

        //Delete all customers for each restaurant
        RestaurantHelper.getAllRestaurants().get().addOnCompleteListener(task -> {

            if (task.getResult() != null) {
                if(task.getResult().size()>0){
                    for (DocumentSnapshot querySnapshot : task.getResult()) {
                        Restaurant restaurant = querySnapshot.toObject(Restaurant.class);

                        if (restaurant != null) {
                            String restaurantPlaceId = restaurant.getRestaurantPlaceId();
                            RestaurantHelper.updateRestaurantCustomers(0,restaurantPlaceId);

                        }

                    }
                }

            }
        }).addOnFailureListener(e -> Log.e("debago", "Problem during the load data"));
        return super.onStartCommand(intent, flags, startId);
    }

}
