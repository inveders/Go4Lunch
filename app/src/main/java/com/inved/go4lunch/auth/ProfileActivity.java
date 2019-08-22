package com.inved.go4lunch.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.QuerySnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.controller.MainActivity;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import butterknife.BindView;
import butterknife.OnClick;

import static com.inved.go4lunch.utils.ManageJobPlaceId.KEY_JOB_PLACE_ID_DATA;

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

    private String jobPlaceId;
    private Toolbar toolbar;

    //FOR DATA
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_NAME = 30;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jobPlaceId = ManageJobPlaceId.getJobPlaceId(this,KEY_JOB_PLACE_ID_DATA);
        Log.d("DEBAGO", "ViewPlaceActivity oncreate jobplaceid: "+jobPlaceId);

        this.configureToolBar();
        this.updateUIWhenCreating();

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    Toast.makeText(ProfileActivity.this, "Notifications actives", Toast.LENGTH_SHORT).show();
                   // notificationActionIfEnabled();
                } else {
                    Toast.makeText(ProfileActivity.this, "Désactivation des notifications", Toast.LENGTH_SHORT).show();
                 //   notificationActionIfIsNotEnabled();
                }
            }
        });


    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_profile; }

    // Configure Toolbar
    private void configureToolBar(){
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.Profile_Activity_Title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.profile_activity_button_update)
    public void onClickUpdateButton() {this.updateNameInFirebase(); }



    @OnClick(R.id.profile_activity_button_delete)
    public void onClickDeleteButton() {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.popup_message_confirmation_delete_account)
                    .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteUserFromFirebase();

                          //  startMainActivity();

                        }
                    })
                    .setNegativeButton(R.string.popup_message_choice_no, null)
                    .show();
        }

        // --------------------
        // REST REQUESTS
        // --------------------
        // Create http requests (Delete)



        private void deleteUserFromFirebase(){

            // Get auth credentials from the user for re-authentication. The example below shows
            // email and password credentials but there are multiple possible providers,
            // such as GoogleAuthProvider or FacebookAuthProvider.
            AuthCredential credential = GoogleAuthProvider
                    .getCredential(getCurrentUser().getEmail(),null);

            // Prompt the user to re-provide their sign-in credentials
            getCurrentUser().reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            AuthUI.getInstance()
                                    .delete(getApplicationContext())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("debago", "User account deleted.");
                                                startMainActivity();
                                                finish();
                                            }
                                        }
                                    });

                            UserHelper.deleteUser(getCurrentUser().getUid(),jobPlaceId).addOnFailureListener(onFailureListener());

                        }
                    });






        /*    if (this.getCurrentUser() != null) {
                AuthUI.getInstance()
                        .delete(this)
                        .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));

                UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
            }*/
        }



    // 3 - Update User Firstname and lastname
    private void updateNameInFirebase(){


        String firstname = this.textInputEditTextFirstname.getText().toString();
        String lastname = this.textInputEditTextLastname.getText().toString();
        if (this.getCurrentUser() != null){
            if (!firstname.isEmpty() &&  !firstname.equals(getString(R.string.info_no_firstname_found))){
                UserHelper.updateFirstname(firstname, this.getCurrentUser().getUid(),jobPlaceId).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_NAME));
            }
        }
        if (this.getCurrentUser() != null){
            if (!lastname.isEmpty() &&  !lastname.equals(getString(R.string.info_no_lastname_found))){
                UserHelper.updateLastname(lastname, this.getCurrentUser().getUid(),jobPlaceId).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_NAME));
            }
        }
    }

        // --------------------
        // UI
        // --------------------

    // Update UI when activity is creating
    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){

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
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            User currentUser = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);

                            //    String firstname = TextUtils.isEmpty(currentUser.getFirstname()) ? getString(R.string.info_no_firstname_found) : currentUser.getFirstname();
                            //  String lastname = TextUtils.isEmpty(currentUser.getLastname()) ? getString(R.string.info_no_lastname_found) : currentUser.getLastname();


                            String firstname = currentUser.getFirstname();
                            if(TextUtils.isEmpty(firstname)||firstname.equals(getString(R.string.info_no_firstname_found))){
                                textInputEditTextFirstname.setHint(getString(R.string.info_no_lastname_found));
                            }
                            else{
                                textInputEditTextFirstname.setText(firstname);
                            }

                            String lastname = currentUser.getLastname();
                            if(TextUtils.isEmpty(lastname)||lastname.equals(getString(R.string.info_no_lastname_found))){
                                textInputEditTextLastname.setHint(getString(R.string.info_no_lastname_found));
                            }
                            else{
                                textInputEditTextLastname.setText(lastname);
                            }

                            String jobAddress = currentUser.getJobAddress();
                            if(TextUtils.isEmpty(jobAddress)||jobAddress.equals(getString(R.string.info_no_job_address_found))){
                                textViewJobAddress.setHint(getString(R.string.info_no_job_address_found));
                            }
                            else{
                                textViewJobAddress.setText(jobAddress);
                            }

                            String jobName = currentUser.getJobName();
                            if(TextUtils.isEmpty(jobName)||jobName.equals(getString(R.string.info_no_job_name_found))){
                                textViewJobName.setHint(getString(R.string.info_no_job_name_found));
                            }
                            else{
                                textViewJobName.setText(jobName);
                            }


                        }
                    });



        }
    }


    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }



        // Create OnCompleteListener called after tasks ended
        private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
            return new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    switch (origin){

                        case UPDATE_NAME:

                            Toast.makeText(getApplicationContext(), getString(R.string.update_confirmation), Toast.LENGTH_LONG).show();
                            break;
                        case DELETE_USER_TASK:
                            Log.d("debago", "We should never arrived here");

                            finish();
                            break;

                    }
                }
            };
        }
}
