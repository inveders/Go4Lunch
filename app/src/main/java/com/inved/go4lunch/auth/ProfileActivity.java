package com.inved.go4lunch.auth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.controller.RestaurantActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity {

    //FOR DESIGN
    @BindView(R.id.profile_activity_imageview_profile)
    ImageView imageViewProfile;
    @BindView(R.id.profile_activity_edit_text_username)
    TextInputEditText textInputEditTextUsername;
    @BindView(R.id.profile_activity_text_view_email)
    TextView textViewEmail;
    @BindView(R.id.profile_activity_progress_bar)
    ProgressBar progressBar;


    //FOR DATA
    // 2 - Identify each Http Request

    private static final int DELETE_USER_TASK = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBAGO", "ProfileActivity : oncreate ");
      //  this.configureToolbar();
        this.updateUIWhenCreating();
    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_profile; }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.profile_activity_button_update)
    public void onClickUpdateButton() { }



    @OnClick(R.id.profile_activity_button_delete)
    public void onClickDeleteButton() {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.popup_message_confirmation_delete_account)
                    .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteUserFromFirebase();
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

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //Update views with data
            this.textInputEditTextUsername.setText(username);
            this.textViewEmail.setText(email);
            Log.d("DEBAGO", "ProfileActivity : updateUIwHENcreationg le text input est: " +this.textInputEditTextUsername);
        }
    }

        // Create OnCompleteListener called after tasks ended
        private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
            return new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (origin == DELETE_USER_TASK) {
                        finish();
                    }
                }
            };
        }
}
