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

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.controller.MainActivity;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserHelper;

import butterknife.BindView;
import butterknife.OnClick;

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



    //FOR DATA
    // 2 - Identify each Http Request

    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_NAME = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBAGO", "ProfileActivity : oncreate ");
      //  this.configureToolbar();
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

                            startMainActivity();

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
            if (this.getCurrentUser() != null) {
                AuthUI.getInstance()
                        .delete(this)
                        .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));

                UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
            }
        }



    // 3 - Update User Firstname and lastname
    private void updateNameInFirebase(){


        String firstname = this.textInputEditTextFirstname.getText().toString();
        String lastname = this.textInputEditTextLastname.getText().toString();
        if (this.getCurrentUser() != null){
            if (!firstname.isEmpty() &&  !firstname.equals(getString(R.string.info_no_firstname_found))){
                UserHelper.updateFirstname(firstname, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_NAME));
            }
        }
        if (this.getCurrentUser() != null){
            if (!lastname.isEmpty() &&  !lastname.equals(getString(R.string.info_no_lastname_found))){
                UserHelper.updateLastname(lastname, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_NAME));
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
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);

                    String firstname = TextUtils.isEmpty(currentUser.getFirstname()) ? getString(R.string.info_no_firstname_found) : currentUser.getFirstname();
                    String lastname = TextUtils.isEmpty(currentUser.getLastname()) ? getString(R.string.info_no_lastname_found) : currentUser.getLastname();
                    textInputEditTextFirstname.setText(firstname);
                    textInputEditTextLastname.setText(lastname);
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

                            finish();


                            break;
                    }
                }
            };
        }
}
