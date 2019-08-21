package com.inved.go4lunch.controller;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.auth.ProfileActivity;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;

public class FindMyJobAddress extends BaseActivity {
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


      /*  if(!profileActivity.getTextViewJobPlaceId().isEmpty()){
            startRestaurantActivity();
        }*/

        Places.initialize(getApplicationContext(),getString(R.string.google_api_key));
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_job_address);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS));
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
                jobAddress=place.getAddress();
                jobPlaceId=place.getId();
                jobName=place.getName();




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

                if(TextUtils.isEmpty(jobAddress)){
                    Log.d(TAG, "Job Address est nul "+jobAddress);
                    Toast.makeText(getApplicationContext(), "Choisissez un lieu", Toast.LENGTH_SHORT).show();
                }else{

                    String firebaseAuthUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    UserHelper.getUserWithSameUid(firebaseAuthUid,jobPlaceId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                //    Log.d("Debago", "task successful");
                                if (task.getResult().getDocuments().size()!=0) {

                                       Log.d("Debago", "findMyJobAddress already exist " + task.getResult().getDocuments());
                                       startRestaurantActivity();
                                } else {
                                       Log.d("Debago", "FindMyJobAddress create user in firestore "+jobAddress+" "+jobName+" "+jobPlaceId);
                                    createUserInFirestore(jobAddress,jobPlaceId,jobName);
                                    startRestaurantActivity();
                                }

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

    private void createUserInFirestore(String jobAddress,String jobPlaceId, String jobName) {

        if (this.getCurrentUser() != null) {

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String firstname = this.getCurrentUser().getDisplayName();
            String lastname = null;
            String uid = this.getCurrentUser().getUid();
            String restaurantPlaceId = null;
            String restaurantName = null;
            String restaurantType = null;
            String restaurantVicinity = null;
            ManageJobPlaceId.saveJobPlaceId(this,jobPlaceId);
            UserHelper.createUser(uid, firstname, lastname, urlPicture, restaurantPlaceId, restaurantType, restaurantName, restaurantVicinity, jobAddress, jobPlaceId, jobName).addOnFailureListener(this.onFailureListener());


        }
    }

    private void startRestaurantActivity() {
        Intent intent = new Intent(this, RestaurantActivity.class);
        Log.d("Debago", "FindMyJobAddress we go in restaurantActivity");
        startActivity(intent);
    }


}
