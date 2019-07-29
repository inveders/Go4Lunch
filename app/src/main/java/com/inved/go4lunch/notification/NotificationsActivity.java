package com.inved.go4lunch.notification;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserHelper;

import java.util.ArrayList;

import butterknife.BindView;

public class NotificationsActivity extends BaseActivity {

    //FOR DESIGN
    @BindView(R.id.activity_notification_message_text)
    TextView notificationMessageText;

    @BindView(R.id.activity_notification_message_workmates)
    TextView notificationMessageWorkmates;

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_notification;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);

                assert currentUser != null;
                String restaurantName = TextUtils.isEmpty(currentUser.getRestaurantName()) ? getString(R.string.info_no_restaurant_name_found) : currentUser.getRestaurantName();
                String restaurantVicinity = TextUtils.isEmpty(currentUser.getRestaurantVicinity()) ? getString(R.string.info_no_restaurant_adresse_found) : currentUser.getRestaurantVicinity();


                UserHelper.getAllWorkmatesJoining(currentUser.getRestaurantPlaceId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> workmates = new ArrayList<String>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("debago", document.getId() + " => " + document.getData());
                                workmates.add(document.get("firstname").toString());
                            }
                        } else {
                            Log.d("debago", "Error getting documents: ", task.getException());
                        }
                        showNotificationMessageTextworkmates(workmates);

                    }
                });


                Log.d("debago","NotificationActivity notifidationMessage "+getString(R.string.notification_message_text,restaurantName,restaurantVicinity));
            showNotificationMessageText(restaurantName,restaurantVicinity);
        }
        });

    }

    public void showNotificationMessageText(String restaurantName,String restaurantVicinity) {
        notificationMessageText.setText(getString(R.string.notification_message_text,restaurantName,restaurantVicinity));

    }

    public void showNotificationMessageTextworkmates(ArrayList<String> workmates){

        String workmatesList = "";
        String newligne=System.getProperty("line.separator");
        for (String s : workmates)
        {
            workmatesList += s + " \n";
        }
        notificationMessageWorkmates.setText(getString(R.string.notification_message_workmates,newligne,workmatesList));
    }



}
