package com.inved.go4lunch.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.controller.activity.RestaurantActivity;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.notification.NotificationService;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;

import static com.inved.go4lunch.utils.ManageJobPlaceId.KEY_JOB_PLACE_ID;

public class FindMyJobAddressActivity extends BaseActivity {
    private static final String TAG = "Debago";
    String jobAddress;
    String jobPlaceId;
    String jobName;
    Context context;


    @BindView(R.id.activity_find_job_address_btn_validation)
    Button btnValidation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserHelper.getUserWhateverLocation(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).get().addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                if(Objects.requireNonNull(task.getResult()).getDocuments().size()==0){

                    Log.d("Debago","FindMyJob on create no result finisih inscription "+task.getResult().getDocuments().size());
                }else{
                    Log.d("Debago", "FindMyJob on create : oncreate go in restaurantActivity "+task.getResult().getDocuments().get(0).getString("jobPlaceId"));
                    ManageJobPlaceId.saveJobPlaceId(this, task.getResult().getDocuments().get(0).getString("jobPlaceId"));
                    startRestaurantActivity();
                    finish();
                }

            }


        });


      /*  if(!profileActivity.getTextViewJobPlaceId().isEmpty()){
            startRestaurantActivity();
        }*/

        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_job_address);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment.setCountry("FR");
        autocompleteFragment.setCountry("LU");


// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d(TAG, "Place: " + place.getName() + ", " + place.getId());
                jobAddress = place.getAddress();
                jobPlaceId = place.getId();
                jobName = place.getName();


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d(TAG, "An error occurred: " + status);
            }
        });

        btnValidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(jobAddress)) {
                    Log.d(TAG, "Job Address est nul " + jobAddress);
                    Toast.makeText(getApplicationContext(), "Choisissez un lieu", Toast.LENGTH_SHORT).show();
                } else {

                    String firebaseAuthUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    UserHelper.getUserWithSameUid(firebaseAuthUid, jobPlaceId).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //    Log.d("Debago", "task successful");
                            if (task.getResult().getDocuments().size() != 0) {

                                Log.d("Debago", "findMyJobAddress already exist " + task.getResult().getDocuments());
                                ManageJobPlaceId.saveJobPlaceId(getApplicationContext(), jobPlaceId);
                                startRestaurantActivity();
                            } else {
                                Log.d("Debago", "FindMyJobAddressActivity create user in firestore " + jobAddress + " " + jobName + " " + jobPlaceId);
                                createUserInFirestore(jobAddress, jobPlaceId, jobName);
                                startRestaurantActivity();
                            }

                        }


                    });


                }
            }
        });
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_find_job_address;
    }

    private void createUserInFirestore(String jobAddress, String jobPlaceId, String jobName) {

        if (this.getCurrentUser() != null) {

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String firstname = this.getCurrentUser().getDisplayName();
            String lastname = null;
            String uid = this.getCurrentUser().getUid();
            String restaurantPlaceId = null;
            String restaurantName = null;
            String restaurantType = null;
            String restaurantVicinity = null;
            String token = null ;
            boolean notificationEnabled = true;
            ManageJobPlaceId.saveJobPlaceId(this, jobPlaceId);
            UserHelper.createUser(uid, firstname, lastname, urlPicture, restaurantPlaceId, restaurantType, restaurantName, restaurantVicinity, jobAddress, jobPlaceId, jobName,token,notificationEnabled).addOnFailureListener(this.onFailureListener());



            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        if(task.getResult()!=null){
                            String token1 = task.getResult().getToken();
                            if(getCurrentUser()!=null){
                                Log.d("debago","creating token");
                                UserHelper.updateUserToken(token1,getCurrentUser().getUid(),ManageJobPlaceId.getJobPlaceId(getApplicationContext()));
                                // Log and toast
                                String msg = "my token"+ token1;
                                Log.d(TAG, msg);
                                Toast.makeText(FindMyJobAddressActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }

                        }

                    });

        }
    }




    private void startRestaurantActivity() {
        Intent intent = new Intent(this, RestaurantActivity.class);
        Log.d("Debago", "FindMyJobAddressActivity we go in restaurantActivity");
        startActivity(intent);
    }


}
