package com.inved.go4lunch.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.controller.activity.MainActivity;
import com.inved.go4lunch.controller.activity.RestaurantActivity;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.utils.CheckDistanceFromWork;
import com.inved.go4lunch.utils.ManageAppMode;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LONGITUDE;

public class ProfileActivity extends BaseActivity {

    //FOR DESIGN
    @BindView(R.id.profile_activity_imageview_profile)
    ImageView imageViewProfile;
    @BindView(R.id.profile_activity_edit_text_firstname)
    TextInputEditText textInputEditTextFirstname;
    @BindView(R.id.profile_activity_edit_text_lastname)
    TextInputEditText textInputEditTextLastname;
    @BindView(R.id.profile_activity_text_view_email)
    TextView textViewEmail;
    @BindView(R.id.notification_switch)
    Switch notificationSwitch;
    @BindView(R.id.profile_activity_text_view_job_name)
    TextView textViewJobName;
    @BindView(R.id.profile_activity_text_view_job_address)
    TextView textViewJobAddress;
    @BindView(R.id.profile_activity_app_mode_button)
    Button appModeButton;

    private String myCurrentGeolocalisation = null;
    private double latitude;
    private double longitude;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {
                intent.getSerializableExtra(KEY_GEOLOCALISATION);
                myCurrentGeolocalisation = intent.getStringExtra(KEY_GEOLOCALISATION);
                latitude = intent.getDoubleExtra(KEY_LATITUDE, 0.0);
                longitude = intent.getDoubleExtra(KEY_LONGITUDE, 0.0);
                Log.d("debago", "profile activity latitude :" + latitude);
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));

        this.configureToolBar();
        this.updateUIWhenCreating();


        notificationSwitch.setOnCheckedChangeListener((compoundButton, bChecked) -> {
            if (bChecked) {
                //Toast.makeText(ProfileActivity.this, getString(R.string.notification_enabling), Toast.LENGTH_SHORT).show();

                if (getCurrentUser() != null) {
                    UserHelper.updateNotificationEnabled(true, getCurrentUser().getUid());
                }

            } else {
                // Toast.makeText(ProfileActivity.this, getString(R.string.notification_desabling), Toast.LENGTH_SHORT).show();
                if (getCurrentUser() != null) {
                    UserHelper.updateNotificationEnabled(false, getCurrentUser().getUid());
                }
            }
        });


    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_profile;
    }

    // Configure Toolbar
    private void configureToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.Profile_Activity_Title);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.profile_activity_app_mode_button)
    public void onClickAppModeButton() {
        if (ManageAppMode.getAppMode(this).equals(getString(R.string.app_mode_work)) || ManageAppMode.getAppMode(this).equals(getString(R.string.app_mode_forced_work))) {
            ManageAppMode.saveAppMode(this, getString(R.string.app_mode_normal));
            appModeButton.setText(getString(R.string.app_mode_change_to_normal_mode));
        } else {
            if (latitude != 0.0) {
                RestaurantActivity restaurantActivity = new RestaurantActivity();
                Log.d("debago", "mycurrentgeoloc :" + myCurrentGeolocalisation + " latitude :" + latitude);
                restaurantActivity.checkDistanceFromWork(myCurrentGeolocalisation, ManageJobPlaceId.getJobPlaceId(this), latitude, longitude);

            } else {
                ManageAppMode.saveAppMode(this, getString(R.string.app_mode_forced_work));
            }
            appModeButton.setText(getString(R.string.app_mode_change_to_work_mode));
        }
    }

    @OnClick(R.id.profile_activity_button_update)
    public void onClickUpdateButton() {
        this.updateNameInFirebase();
    }


    @OnClick(R.id.profile_activity_button_delete)
    public void onClickDeleteButton() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) -> {
                    deleteUserFromFirebase();

                    //  startMainActivity();

                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();
    }

    // --------------------
    // REST REQUESTS
    // --------------------
    // Create http requests (Delete)


    private void deleteUserFromFirebase() {

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = GoogleAuthProvider
                .getCredential(Objects.requireNonNull(getCurrentUser()).getEmail(), null);

        // Prompt the user to re-provide their sign-in credentials
        getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    AuthUI.getInstance()
                            .delete(getApplicationContext())
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d("debago", "User account deleted.");
                                    startMainActivity();
                                    finish();
                                }
                            });

                    UserHelper.deleteUser(getCurrentUser().getUid()).addOnFailureListener(onFailureListener());

                });

    }


    // 3 - Update User Firstname and lastname
    private void updateNameInFirebase() {


        String firstname = Objects.requireNonNull(this.textInputEditTextFirstname.getText()).toString();
        String lastname = Objects.requireNonNull(this.textInputEditTextLastname.getText()).toString();
        if (this.getCurrentUser() != null) {
            if (!firstname.isEmpty() && !firstname.equals(getString(R.string.info_no_firstname_found))) {
                UserHelper.updateFirstname(firstname, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted());
            }
        }
        if (this.getCurrentUser() != null) {
            if (!lastname.isEmpty() && !lastname.equals(getString(R.string.info_no_lastname_found))) {
                UserHelper.updateLastname(lastname, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted());
            }
        }
    }

    // --------------------
    // UI
    // --------------------

    // Update UI when activity is creating
    private void updateUIWhenCreating() {

        if (ManageAppMode.getAppMode(this).equals(getString(R.string.app_mode_work)) || ManageAppMode.getAppMode(this).equals(getString(R.string.app_mode_forced_work))) {
            appModeButton.setText(getString(R.string.app_mode_change_to_work_mode));
        } else {
            appModeButton.setText(getString(R.string.app_mode_change_to_normal_mode));
        }

        if (this.getCurrentUser() != null) {

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }

            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();

            this.textViewEmail.setText(email);

            // 7 - Get data from Firestore
            UserHelper.getUserWhateverLocation(this.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        User currentUser = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);

                        //    String firstname = TextUtils.isEmpty(currentUser.getFirstname()) ? getString(R.string.info_no_firstname_found) : currentUser.getFirstname();
                        //  String lastname = TextUtils.isEmpty(currentUser.getLastname()) ? getString(R.string.info_no_lastname_found) : currentUser.getLastname();


                        assert currentUser != null;
                        String firstname = currentUser.getFirstname();
                        if (TextUtils.isEmpty(firstname) || firstname.equals(getString(R.string.info_no_firstname_found))) {
                            textInputEditTextFirstname.setHint(getString(R.string.info_no_lastname_found));
                        } else {
                            textInputEditTextFirstname.setText(firstname);
                        }

                        String lastname = currentUser.getLastname();
                        if (TextUtils.isEmpty(lastname) || lastname.equals(getString(R.string.info_no_lastname_found))) {
                            textInputEditTextLastname.setHint(getString(R.string.info_no_lastname_found));
                        } else {
                            textInputEditTextLastname.setText(lastname);
                        }

                        String jobAddress = currentUser.getJobAddress();
                        if (TextUtils.isEmpty(jobAddress) || jobAddress.equals(getString(R.string.info_no_job_address_found))) {
                            textViewJobAddress.setHint(getString(R.string.info_no_job_address_found));
                        } else {
                            textViewJobAddress.setText(jobAddress);
                        }

                        String jobName = currentUser.getJobName();
                        if (TextUtils.isEmpty(jobName) || jobName.equals(getString(R.string.info_no_job_name_found))) {
                            textViewJobName.setHint(getString(R.string.info_no_job_name_found));
                        } else {
                            textViewJobName.setText(jobName);
                        }

                        if (currentUser.isNotificationEnabled()) {
                            notificationSwitch.setChecked(currentUser.isNotificationEnabled());
                        } else {
                            notificationSwitch.setChecked(currentUser.isNotificationEnabled());
                        }


                    });


        }
    }


    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }


    // Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted() {
        return aVoid -> Toast.makeText(getApplicationContext(), getString(R.string.update_confirmation), Toast.LENGTH_LONG).show();
    }
}
