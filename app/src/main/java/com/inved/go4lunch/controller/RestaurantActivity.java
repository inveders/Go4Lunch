package com.inved.go4lunch.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.inved.go4lunch.R;
import com.inved.go4lunch.auth.ProfileActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class RestaurantActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //FOR DATA
    private static final int SIGN_OUT_TASK = 10;

    //FOR DESIGN

    //Declaration for fragments
    MapFragment mapFragment = new MapFragment();
    ListViewFragment listViewFragment = new ListViewFragment();
    PeopleFragment peopleFragment = new PeopleFragment();
    BottomNavigationView bottomNavigationView;

    //Declaration for Navigation Drawer
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        //Bottom Navigation View
        bottomNavigationView = findViewById(R.id.activity_restaurant_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> updateMainFragment(item.getItemId()));
        bottomNavigationView.setSelectedItemId(R.id.action_map); //The first page is map Fragment


        //All view configuration
        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // 4 - Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.activity_main_drawer_lunch :
                break;
            case R.id.activity_main_drawer_settings: this.startProfileActivity();
                break;
            case R.id.activity_main_drawer_logout:
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    //Configuration of the Bottom Navigation View on click
    private Boolean updateMainFragment(Integer integer) {
        switch (integer) {
            case R.id.action_map:
                setFragment(mapFragment);
                break;
            case R.id.action_list:
                setFragment(listViewFragment);
                break;
            case R.id.action_people:
                setFragment(peopleFragment);
                break;
        }
        return true;
    }

    //Configuration to go on the good fragment after click on the Bottom Navigation View button
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.activity_restaurant_frame_layout, fragment);
        fragmentTransaction.commit();
    }


    // Configure Toolbar
    private void configureToolBar(){
        this.toolbar = findViewById(R.id.activity_restaurant_toolbar);
        setSupportActionBar(toolbar);
    }

    // Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = findViewById(R.id.activity_restaurant_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    // Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = findViewById(R.id.activity_restaurant_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // -------------------
    // UI
    // -------------------



    // -------------------
    // NAVIGATION
    // -------------------

    // Launch Profile Activity
    private void startProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        Log.d("DEBAGO", "MainActivity : satrtProfileActivity ");
        startActivity(intent);
    }

    //Signout

    @OnClick(R.id.activity_main_drawer_logout)
    public void onClickSignOutButton() {
        this.signOutUserFromFirebase();
    }

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    // Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (origin == SIGN_OUT_TASK) {
                    finish();
                }
            }
        };
    }
}
