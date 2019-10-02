package com.inved.go4lunch.notification;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.controller.activity.ViewPlaceActivity;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserHelper;

import java.util.ArrayList;

import butterknife.BindView;

import static com.inved.go4lunch.controller.fragment.MapFragment.RESTAURANT_PLACE_ID;

public class NotificationsActivity extends BaseActivity {

    public static final String NOTIFICATION_RESTAURANT_NAME = "RESTAURANT_NAME";
    public static final String NOTIFICATION_RESTAURANT_ADDRESS = "RESTAURANT_ADDRESS";

    //FOR DESIGN
    @BindView(R.id.activity_notification_message_text)
    TextView notificationMessageText;

    @BindView(R.id.activity_notification_message_workmates)
    TextView notificationMessageWorkmates;

    @BindView(R.id.activity_notification_btn_see_choice)
    Button btnSeeRestaurantChoosen;

    PlaceDetailsData placeDetailsData = new PlaceDetailsData();
    private Toolbar toolbar;

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_notification;
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseInformations();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolBar();
        firebaseInformations();


    }

    // Configure Toolbar
    private void configureToolBar(){
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.Notification_activity_Title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void firebaseInformations(){

        UserHelper.getUserWhateverLocation(this.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                User currentUser = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);

                assert currentUser != null;
                String restaurantName = TextUtils.isEmpty(currentUser.getRestaurantName()) ? getString(R.string.info_no_restaurant_name_found) : currentUser.getRestaurantName();
                String restaurantVicinity = TextUtils.isEmpty(currentUser.getRestaurantVicinity()) ? getString(R.string.info_no_restaurant_adresse_found) : currentUser.getRestaurantVicinity();
                String restaurantPlaceId = currentUser.getRestaurantPlaceId();
             //   sendDataInService(restaurantName,restaurantVicinity);

                UserHelper.getAllWorkmatesJoining(currentUser.getRestaurantPlaceId(),currentUser.getJobPlaceId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> workmates = new ArrayList<String>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                            //    Log.d("debago", document.getId() + " => " + document.getData());
                                workmates.add(document.get("firstname").toString());

                            }
                            for (int i = 0; i <workmates.size() ; i++) {
                                if(workmates.get(i).equals(currentUser.getFirstname())){
                                    workmates.remove(i);
                                }

                            }
                        } else {
                            Log.d("debago", "Error getting documents: ", task.getException());
                        }
                        showNotificationMessageTextworkmates(workmates);

                    }
                });


              //  Log.d("debago","NotificationActivity notifidationMessage "+getString(R.string.notification_message_text,restaurantName,restaurantVicinity));
                showNotificationMessageText(restaurantName,restaurantVicinity,restaurantPlaceId);
            }
        });

    }

    private void sendDataInService(String restaurantName, String restaurantVicinity) {

        Intent mIntent = new Intent(this, NotificationService.class);
        Bundle extras = mIntent.getExtras();
        if (extras != null) {
            extras.putString(NOTIFICATION_RESTAURANT_NAME, restaurantName);
            extras.putString(NOTIFICATION_RESTAURANT_ADDRESS, restaurantVicinity);
        }

    }

    public void showNotificationMessageText(String restaurantName,String restaurantVicinity,String placeId) {

        if(restaurantName.equals(getString(R.string.info_no_restaurant_name_found))){
            notificationMessageText.setText(getString(R.string.restaurant_no_choosen));
            notificationMessageWorkmates.setVisibility(View.INVISIBLE);
        }
        else{
            notificationMessageText.setText(getString(R.string.notification_message_text,restaurantName,restaurantVicinity));
            btnSeeRestaurantChoosen.setVisibility(View.VISIBLE);
            btnSeeRestaurantChoosen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startViewPlaceActivity(placeId);
                }
            });
        }


    }

    public void showNotificationMessageTextworkmates(ArrayList<String> workmates){

        if(workmates.size()==0){
            notificationMessageWorkmates.setText(getString(R.string.notification_message_no_workmates_with_you));
        }
        else{
            String workmatesList = "";
            String newligne=System.getProperty("line.separator");
            for (String s : workmates)
            {
                workmatesList += s + " \n";
            }
            notificationMessageWorkmates.setText(getString(R.string.notification_message_workmates,newligne,workmatesList));
        }


    }

    // Launch View Place Activity
    private void startViewPlaceActivity(String placeId) {
        Intent intent = new Intent(this, ViewPlaceActivity.class);
        intent.putExtra(RESTAURANT_PLACE_ID,placeId);
        startActivity(intent);
    }



}
