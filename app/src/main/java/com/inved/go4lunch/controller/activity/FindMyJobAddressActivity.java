package com.inved.go4lunch.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.utils.ManageChangingWork;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.MAP_API_KEY;

public class FindMyJobAddressActivity extends BaseActivity {
    private static final String TAG = "Debago";
    String jobAddress;
    String jobPlaceId;
    String jobName;


    @BindView(R.id.activity_find_job_address_btn_validation)
    Button btnValidation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ManageChangingWork.getUserWorkDecision(this)) {
            UserHelper.getUserWhateverLocation(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    if(task.getResult()!=null){
                        if (task.getResult().getDocuments().size() == 0) {
                            Log.d(TAG, "Result");
                        } else {
                            ManageJobPlaceId.saveJobPlaceId(this, task.getResult().getDocuments().get(0).getString("jobPlaceId"));
                            startRestaurantActivity();
                            finish();
                        }
                    }


                }


            });
        }


        Places.initialize(getApplicationContext(), MAP_API_KEY);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_job_address);

        // Specify the types of place data to return.
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
            autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
            autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
            autocompleteFragment.setCountry("FR");
            autocompleteFragment.setCountry("LU");

            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    Log.d(TAG, "Place: " + place.getName() + ", " + place.getId());
                    jobAddress = place.getAddress();
                    jobPlaceId = place.getId();
                    jobName = place.getName();
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.d(TAG, "An error occurred: " + status);
                }
            });
        }


        btnValidation.setOnClickListener(view -> {

            if (TextUtils.isEmpty(jobAddress)) {
                Toast.makeText(getApplicationContext(), "Choisissez un lieu", Toast.LENGTH_SHORT).show();
            } else {

                if (ManageChangingWork.getUserWorkDecision(this)) {
                    //User first work in app
                    if(getCurrentUser()!=null){
                        String firebaseAuthUid = getCurrentUser().getUid();
                        UserHelper.getUserWithSameUid(firebaseAuthUid).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                if (task.getResult() != null) {
                                    if (task.getResult().getDocuments().size() != 0) {
                                        ManageJobPlaceId.saveJobPlaceId(getApplicationContext(), jobPlaceId);
                                        startRestaurantActivity();
                                    } else {
                                        createUserInFirestore(jobAddress, jobPlaceId, jobName);
                                        startRestaurantActivity();
                                    }
                                }
                            }
                        });
                    }

                } else {
                    //User is changing work
                    if (getCurrentUser() != null) {

                        moveFirestoreDocument(UserHelper.getUsersCollection().document(getCurrentUser().getUid()), UserHelper.getUsersNewCollectionAfterChangingWork(jobPlaceId).document(getCurrentUser().getUid()), jobAddress, jobPlaceId, jobName);

                    }

                }


            }
        });
    }

    private void updateInformationAfterMoving(String jobAddress, String jobPlaceId, String jobName) {

        if (getCurrentUser() != null) {

            UserHelper.updateJobAddress(jobAddress, getCurrentUser().getUid());
            UserHelper.updateJobName(jobName, getCurrentUser().getUid());
            UserHelper.updateJobPlaceId(jobPlaceId, getCurrentUser().getUid());
        }

    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_find_job_address;
    }

    private void createUserInFirestore(String jobAddress, String jobPlaceId, String jobName) {

        if (this.getCurrentUser() != null) {

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String firstname = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            ManageJobPlaceId.saveJobPlaceId(this, jobPlaceId);
            UserHelper.createUser(uid, firstname, null, urlPicture, null, null, null, jobAddress, jobPlaceId, jobName, null, true).addOnFailureListener(this.onFailureListener());

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        if (task.getResult() != null) {
                            String token1 = task.getResult().getToken();
                            if (getCurrentUser() != null) {
                                UserHelper.updateUserToken(token1, getCurrentUser().getUid());

                            }
                        }
                    });

        }
    }


    private void startRestaurantActivity() {
        Intent intent = new Intent(this, RestaurantActivity.class);
        startActivity(intent);
    }

    public void moveFirestoreDocument(DocumentReference fromPath, final DocumentReference toPath, String jobAddress, String jobPlaceId, String jobName) {
        fromPath.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.getData() != null) {
                        toPath.set(document.getData())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    Toast.makeText(FindMyJobAddressActivity.this, getString(R.string.profile_activity_successfull_work_changement), Toast.LENGTH_SHORT).show();

                                    fromPath.delete()
                                            .addOnSuccessListener(aVoid1 -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                                            .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));

                                    ManageChangingWork.saveUserWorkDecision(this, false);
                                    ManageJobPlaceId.saveJobPlaceId(this, jobPlaceId);
                                    updateInformationAfterMoving(jobAddress, jobPlaceId, jobName);
                                    startRestaurantActivity();
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });


    }

}
