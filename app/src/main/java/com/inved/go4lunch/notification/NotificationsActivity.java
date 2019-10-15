package com.inved.go4lunch.notification;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.controller.activity.ViewPlaceActivity;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserHelper;

import java.util.ArrayList;

import butterknife.BindView;

import static com.inved.go4lunch.controller.fragment.MapFragment.RESTAURANT_PLACE_ID;

public class NotificationsActivity extends BaseActivity {

    //FOR DESIGN
    @BindView(R.id.activity_notification_message_text)
    TextView notificationMessageText;

    @BindView(R.id.activity_notification_message_workmates)
    TextView notificationMessageWorkmates;

    @BindView(R.id.activity_notification_btn_see_choice)
    Button btnSeeRestaurantChoosen;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.Notification_activity_Title);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    private void firebaseInformations(){

        if(this.getCurrentUser()!=null){
            UserHelper.getUserWhateverLocation(this.getCurrentUser().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                User currentUser = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);

                assert currentUser != null;
                String restaurantName = TextUtils.isEmpty(currentUser.getRestaurantName()) ? getString(R.string.info_no_restaurant_name_found) : currentUser.getRestaurantName();
                String restaurantVicinity = TextUtils.isEmpty(currentUser.getRestaurantVicinity()) ? getString(R.string.info_no_restaurant_adresse_found) : currentUser.getRestaurantVicinity();
                String restaurantPlaceId = currentUser.getRestaurantPlaceId();

                UserHelper.getAllWorkmatesJoining(currentUser.getRestaurantPlaceId()).get().addOnCompleteListener(task -> {
                    ArrayList<String> workmates = new ArrayList<>();

                    if (task.isSuccessful()) {
                        if(task.getResult()!=null){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                workmates.add(document.getString("firstname"));

                            }
                            for (int i = 0; i <workmates.size() ; i++) {
                                if(workmates.get(i).equals(currentUser.getFirstname())){
                                    workmates.remove(i);
                                }

                            }
                        }

                    } else {
                        Log.d("debago", "Error getting documents: ", task.getException());
                    }
                    showNotificationMessageTextworkmates(workmates);

                });

                showNotificationMessageText(restaurantName,restaurantVicinity,restaurantPlaceId);
            });
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
            btnSeeRestaurantChoosen.setOnClickListener(view -> startViewPlaceActivity(placeId));
        }


    }

    public void showNotificationMessageTextworkmates(ArrayList<String> workmates){

        if(workmates.size()==0){
            notificationMessageWorkmates.setText(getString(R.string.notification_message_no_workmates_with_you));
        }
        else{
            String workmatesList = "";
            String newligne=System.getProperty("line.separator");
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : workmates)
            {
                stringBuilder.append(workmatesList)
                        .append("+=")
                        .append(s)
                        .append("+")
                        .append("\n");
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
