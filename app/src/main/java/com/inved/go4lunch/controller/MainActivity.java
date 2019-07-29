package com.inved.go4lunch.controller;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.inved.go4lunch.R;

import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.base.BaseActivity;

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
            this.startRestaurantActivity();

        }
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
        if (this.isCurrentUserLogged()) {
            this.startRestaurantActivity();
        } else {
            this.startFacebookSignInActivity();
        }
    }

    @OnClick(R.id.login_google_button)
    public void onClickGoogleLoginButton() {

        if (this.isCurrentUserLogged()) {

            this.startRestaurantActivity();
        } else {
            this.startGoogleSignInActivity();
        }
    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void createUserInFirestore() {

        if (this.getCurrentUser() != null) {

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String firstname = this.getCurrentUser().getDisplayName();
            String lastname = null;
            String uid = this.getCurrentUser().getUid();
            String restaurantPlaceId = null;
            String restaurantName = null;
            String restaurantType = null;
            String restaurantVicinity = null;
            Log.d("Debago", "MainActivity : createUserInFirestore");
            UserHelper.createUser(uid, firstname, lastname, urlPicture, restaurantPlaceId, restaurantType,restaurantName,restaurantVicinity).addOnFailureListener(this.onFailureListener());


        }
    }

 /*   private void createRestaurantInFirestore() {

        if (this.getCurrentUser() != null) {

            String restaurantPlaceId = null;
            String uid = this.getCurrentUser().getUid();
            int restaurantCustomers = 0;
            RestaurantHelper.createRestaurant(uid,restaurantPlaceId,restaurantCustomers).addOnFailureListener(this.onFailureListener());
        }
    }*/

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
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));
                this.createUserInFirestore(); /**C'est ici le probléme il faudrait qu'on crée l'utilisateur s'il n'existe pas déjà dans la base de données*/
                //this.createRestaurantInFirestore();
                this.startRestaurantActivity();
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
