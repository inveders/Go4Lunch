package com.inved.go4lunch.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.auth.ProfileActivity;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.firebase.User;

public class RestaurantActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,LocationListener {

    //FOR LOCAL BROADCAST MANAGER
    public static final String PLACE_DETAIL_DATA = "PLACE_DETAIL_DATA";

    public static final String PLACE_DATA_PHONE_NUMBER = "PLACE_DETAIL_DATA_PHONE_NUMBER";
    public static final String PLACE_DATA_PHOTO_REFERENCE = "PLACE_DETAIL_DATA_PHOTO_REFERENCE";
    public static final String PLACE_DATA_PHOTO_MAW_WIDTH = "PHOTO_MAX_WIDTH";

    public static final String PLACE_SEARCH_DATA = "PLACE_SEARCH_DATA";

    public static final String PLACE_DATA_RESTAURANT_LATITUDE = "RESTAURANT_LONGITUDE";
    public static final String PLACE_DATA_RESTAURANT_LONGITUDE = "RESTAURANT_LATITUDE";


    public static final String PLACE_DATA_NAME = "RESTAURANT_NAME";
    public static final String PLACE_DATA_VICINITY = "VICINITY";
    public static final String PLACE_DATA_SIZE = "RESULT_SIZE";
    public static final String PLACE_DATA_PLACE_ID = "PLACE_ID";
    public static final String PLACE_DATA_LIST_RESULT_PLACE_SEARCH = "LIST_RESULT_PLACE_SEARCH";



    //FOR DATA
    private static final int SIGN_OUT_TASK = 10;

    MenuItem logout;
    TextView navFirstname;
    TextView navLastname;
    TextView navEmail;
    ImageView navProfileImage;
    private NavigationView mNavigationView;
    //FOR DESIGN

    //Declaration for fragments
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;

    //Localisation

    private LocationManager lm;
    public static final String KEY_LOCATION_CHANGED = "DATA_ACTION";
    public static final String KEY_GEOLOCALISATION = "LAT_LONG";
    public static final String KEY_LATITUDE = "LAT";
    public static final String KEY_LONGITUDE = "LONGI";
    private static final int PERMS_CALL_ID = 1234;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000; // 1000 meters for tests, after come back to 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute*/

    //Declaration for Navigation Drawer
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    @Override
    public int getFragmentLayout() {return R.layout.activity_restaurant;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_restaurant);

        //Bottom Navigation View
        bottomNavigationView = findViewById(R.id.activity_restaurant_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //NavigationDrawer
        logout=findViewById(R.id.activity_main_drawer_logout);


        //Viewpager
        viewPager = findViewById(R.id.viewpager_fragment); //Init Viewpager
        setupFm(getSupportFragmentManager(), viewPager); //Setup Fragment
        viewPager.setCurrentItem(0); //Set Currrent Item When Activity Start
        viewPager.setOnPageChangeListener(new PageChange()); //Listeners For Viewpager When Page Changed

        //Configuration navigation view header
        mNavigationView=findViewById(R.id.activity_restaurant_nav_view);
        navFirstname = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_FirstName);
        navLastname = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_LastName);
        navEmail = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_Email);
        navProfileImage = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_profile_image);

     /*   navFirstname=findViewById(R.id.nav_header_FirstName);
        navLastname=findViewById(R.id.nav_header_LastName);
        navEmail=findViewById(R.id.nav_header_Email);
        navProfileImage=findViewById(R.id.nav_header_profile_image);*/

        //All view configuration
        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();
        this.userInformationFromFirebase();

        //Localisation
        checkPermissions();


    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermissions();
        userInformationFromFirebase();
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
            case R.id.activity_main_drawer_lunch :
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
  /*  private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.activity_restaurant_frame_layout, fragment);
        fragmentTransaction.commit();
    }*/


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
       // userInformationFromFirebase();



    }

    private void userInformationFromFirebase() {


        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {

                Glide.with(this)

                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(navProfileImage);
            }

            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();

            if(email!=null) {
                navEmail.setText(email);
            }
            // 7 - Get data from Firestore
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);

                    String firstname = TextUtils.isEmpty(currentUser.getFirstname()) ? getString(R.string.info_no_firstname_found) : currentUser.getFirstname();
                    String lastname = TextUtils.isEmpty(currentUser.getLastname()) ? getString(R.string.info_no_lastname_found) : currentUser.getLastname();
                    navFirstname.setText(firstname);
                    navLastname.setText(lastname);
                }
            });

        }
    }

    // Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = findViewById(R.id.activity_restaurant_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // -------------------
    // LOCALISATION
    // -------------------

    private void checkPermissions(){
        //We check permission to know if they are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },PERMS_CALL_ID);

            return;
        }


        //Subscribe to providers
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }

        if (lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }

        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMS_CALL_ID){

        }
    }

    @Override
    public void onLocationChanged(Location location) {


        latitude = location.getLatitude();
        longitude = location.getLongitude();
        sendLocationDataToFragments();


        }

    public void stopUsingGPS(){
        if(lm != null){
            lm.removeUpdates(RestaurantActivity.this);
        }
    }

    public void sendLocationDataToFragments (){
        String currentGeolocalisation = ""+getLatitude()+","+getLongitude()+"";
                final Intent intent = new Intent(KEY_LOCATION_CHANGED);
        intent.putExtra(KEY_GEOLOCALISATION, currentGeolocalisation);
        intent.putExtra(KEY_LATITUDE, getLatitude());
        intent.putExtra(KEY_LONGITUDE, getLongitude());
        LocalBroadcastManager.getInstance(RestaurantActivity.this).sendBroadcast(intent);

    }


    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        return latitude;
    }


    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        return longitude;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

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
        startActivity(intent);
    }


    //Signout

 //   @OnClick(R.id.activity_main_drawer_logout)
    public void signOutUserFromFirebase(){
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





}
