package com.inved.go4lunch.controller;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.GooglePlaceCalls;
import com.inved.go4lunch.auth.ProfileActivity;
import com.inved.go4lunch.model.pojo.Pojo;
import com.inved.go4lunch.model.pojo.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.media.CamcorderProfile.get;

public class RestaurantActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GooglePlaceCalls.Callbacks {

    //FOR DATA
    private static final int SIGN_OUT_TASK = 10;
    @BindView(R.id.activity_main_drawer_logout)
    MenuItem logout;

    //FOR DESIGN

    //Declaration for fragments
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;

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
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Viewpager
        viewPager = findViewById(R.id.viewpager_fragment); //Init Viewpager
        setupFm(getSupportFragmentManager(), viewPager); //Setup Fragment
        viewPager.setCurrentItem(0); //Set Currrent Item When Activity Start
        viewPager.setOnPageChangeListener(new PageChange()); //Listeners For Viewpager When Page Changed

        //All view configuration
        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        //Launch retrofit request
        this.executeHttpRequestWithRetrofit();
    }


    //Navigation drawer
    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    //Navigation drawer
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // 4 - Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.activity_main_drawer_lunch :this.startLocalisationActivity();
                break;
            case R.id.activity_main_drawer_settings: this.startProfileActivity();
                break;
            case R.id.activity_main_drawer_logout:signOutUserFromFirebase();
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
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_map:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.action_list:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.action_people:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

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

    //Viewpager

    public static void setupFm(FragmentManager fragmentManager, ViewPager viewPager){
        FragmentAdapter Adapter = new FragmentAdapter(fragmentManager);
        //Add All Fragment To List
        Adapter.add(new MapFragment(), "Page Map");
        Adapter.add(new ListViewFragment(), "Page List View");
        Adapter.add(new PeopleFragment(), "Page People");
        viewPager.setAdapter(Adapter);
    }




    // Launch Profile Activity
    private void startProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        Log.d("DEBAGO", "RestaurantActivity : startProfileActivity ");
        startActivity(intent);
    }

    // Launch Localisation Activity
    private void startLocalisationActivity(){
        Intent intent = new Intent(this, Localisation.class);
        Log.d("DEBAGO", "RestaurantActivity : startLocalisationActivity ");
        startActivity(intent);
    }

    //Signout

 //   @OnClick(R.id.activity_main_drawer_logout)
    public void signOutUserFromFirebase(){
        Log.d("DEBAGO", "RestaurantActivity : onclicksignout ");
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

    public class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    bottomNavigationView.setSelectedItemId(R.id.action_map);
                    break;
                case 1:
                    bottomNavigationView.setSelectedItemId(R.id.action_list);
                    break;
                case 2:
                    bottomNavigationView.setSelectedItemId(R.id.action_people);
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


    // -------------------
    // HTTP REQUEST BY RETROFIT (Retrofit Way)
    // -------------------


    // 4 - Execute HTTP request and update UI
    public void executeHttpRequestWithRetrofit(){

        String type = "Food";
        int radius = 1000;
        MapFragment geolocalisation = new MapFragment();

            Log.d("Debago","Restaurant activity latitude"+geolocalisation.getCurrentLocalisation());
            GooglePlaceCalls.fetchUserFollowing(this, type,"49.442627699999996 ,6.0247494",radius);



    }

    // 2 - Override callback methods

    @Override
    public void onResponse(@Nullable Pojo response) {
        // 2.1 - When getting response, we update UI
        if (response != null);

        assert response != null;
//        Toast.makeText(this,"Location: "+response.results.get(1).getName(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure() {
        // 2.2 - When getting error, we update UI

    }


}
