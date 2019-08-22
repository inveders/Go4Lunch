package com.inved.go4lunch.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QuerySnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.firebase.UserHelper;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    //FOR DESIGN
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.login_facebook_button)
    Button facebookLogin;
    @BindView(R.id.login_google_button)
    Button googleLogin;

    Animation animation;
    //FOR DATA
    private static final int RC_SIGN_IN = 123;

    @Override
    public int getFragmentLayout() {

        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start appropriate activity
        if (this.isCurrentUserLogged()) {
        //    Log.d("Debago", "MainActivity : user is already log");

            UserHelper.getUserWhateverLocation(getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.isSuccessful()){
                        if(task.getResult().getDocuments().size()==0){
                            startFindMyJobAddressActivity();
                       //     Log.d("Debago","no result finisih inscription "+task.getResult().getDocuments().size());
                        }else{
                        //    Log.d("Debago", "MainActivity : oncreate go in restaurantActivity "+task.getResult().getDocuments().get(0).getString("jobPlaceId"));

                            startRestaurantActivity();
                            finish();
                        }

                    }else {
                        startFindMyJobAddressActivity();
                      //  Log.d("Debago","no result finisih inscription");
                    }


                }
            });


        }
        animation = AnimationUtils.loadAnimation(this, R.anim.fadein);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // this.updateUIWhenGoogleResuming();
    }


    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.login_facebook_button)
    public void onClickFacebookLoginButton() {
        facebookLogin.startAnimation(animation);
        if (this.isCurrentUserLogged()) {
            this.startRestaurantActivity();
        } else {
            this.startFacebookSignInActivity();
        }
    }

    @OnClick(R.id.login_google_button)
    public void onClickGoogleLoginButton() {
        googleLogin.startAnimation(animation);
        if (this.isCurrentUserLogged()) {

            this.startRestaurantActivity();
        } else {
            this.startGoogleSignInActivity();
        }
    }

    // --------------------
    // REST REQUEST
    // --------------------



    // --------------------
    // NAVIGATION
    // --------------------

    private void startFacebookSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build())) // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_appli)
                        .build(),
                RC_SIGN_IN);
    }

    private void startGoogleSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build()))//GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_appli)
                        .build(),
                RC_SIGN_IN);
    }

    private void startRestaurantActivity() {
        Intent intent = new Intent(this, RestaurantActivity.class);

        startActivity(intent);
    }

    private void startFindMyJobAddressActivity() {
        Intent intent = new Intent(this, FindMyJobAddress.class);
        startActivity(intent);
    }

    private void startPermissionActivity() {
        Intent intent = new Intent(this, PermissionActivity.class);

        startActivity(intent);
    }
    // --------------------
    // UI
    // --------------------

    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // Update UI when activity is resuming
   /* private void updateUIWhenGoogleResuming(){
        //this.buttonLogin.setText(this.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
    }*/

    private void updateUIWhenFacebookResuming() {
        this.facebookLogin.setText(this.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
    }

    // --------------------
    // UTILS
    // --------------------


    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
          //  Log.d("Debago", "MainActivity : SIGNIN");
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));



                this.startPermissionActivity();
                finish();
                //this.startRestaurantActivity();
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }


}
