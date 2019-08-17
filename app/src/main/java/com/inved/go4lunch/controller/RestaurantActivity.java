package com.inved.go4lunch.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.auth.ProfileActivity;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.notification.NotificationsActivity;

import java.util.Arrays;
import java.util.List;

public class RestaurantActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    //FOR LOCAL BROADCAST MANAGER
    public static final String PLACE_DETAIL_DATA = "PLACE_DETAIL_DATA";

    public static final String PLACE_DATA_PHONE_NUMBER = "PLACE_DETAIL_DATA_PHONE_NUMBER";
    public static final String PLACE_DATA_PHOTO_BITMAP = "PLACE_DETAIL_DATA_PHOTO_BITMAP";


    public static final String PLACE_SEARCH_DATA = "PLACE_SEARCH_DATA";

    public static final String PLACE_DATA_RESTAURANT_LATITUDE = "RESTAURANT_LONGITUDE";
    public static final String PLACE_DATA_RESTAURANT_LONGITUDE = "RESTAURANT_LATITUDE";


    public static final String PLACE_DATA_NAME = "RESTAURANT_NAME";
    public static final String PLACE_DATA_ADDRESS = "ADDRESS";
    public static final String PLACE_DATA_SIZE = "RESULT_SIZE";
    public static final String PLACE_DATA_PLACE_ID = "PLACE_ID";
    public static final String PLACE_DATA_WEBSITE = "WEBSITE";
    public static final String PLACE_DATA_RATING = "RATING";
    public static final String PLACE_DATA_OPENING_HOURS = "LIST_RESULT_PLACE_SEARCH";


    public static final String TAG = "Debago";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 645;

    //FOR LOCATION
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    Location location; // location
    private LocationCallback locationCallback;
    private Location mLastKnownLocation;
    private LocationManager lm;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000; // 1000 meters for tests, after come back to 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute*/

    //AUTOCOMPLETE
    private List<AutocompletePrediction> predictionList;
    AutocompleteSessionToken token;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    List<Place.Field> fields;

    double latitude; // latitude
    double longitude; // longitude

    //Pour SEARCH ACTIVITY


    private View mapView;


 //   @BindView(R.id.action_search)
    Button btnFindSearch;

    //FOR DATA
    private static final int SIGN_OUT_TASK = 10;
    PlaceDetailsData placeDetailsData = new PlaceDetailsData();
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


    public static final String KEY_LOCATION_CHANGED = "DATA_ACTION";
    public static final String KEY_GEOLOCALISATION = "LAT_LONG";
    public static final String KEY_LATITUDE = "LAT";
    public static final String KEY_LONGITUDE = "LONGI";

    //Declaration for Navigation Drawer
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    @Override
    public int getFragmentLayout() {return R.layout.activity_restaurant;}

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        //Location
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



        //All view configuration
        this.configureToolBar();

        this.configureDrawerLayout();

      //  this.checkLocation();
        // Initialize Places.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        }
        fields = Arrays.asList(Place.Field.NAME);

        // Create a new Places client instance.
        placesClient = Places.createClient(this);
       token = AutocompleteSessionToken.newInstance();

        /*final AutocompleteSupportFragment autocompleteSupportFragment=
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.searautocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });*/

        this.configureNavigationView();
        this.userInformationFromFirebase();

        //Localisation



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startAutocompleteWidgetShow();
                return true;
            }
        });



        return true;
    }

    private void startAutocompleteWidgetShow() {
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }




    @Override
    public void onResume() {
        super.onResume();

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
            case R.id.activity_main_drawer_lunch :startNotificationActivity();//this.detectPlaceIdForLunch();
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


 /* private void checkLocation(){
      //check if gps is enabled or not and then request user to enable it
      LocationRequest locationRequest = LocationRequest.create();
      locationRequest.setInterval(10000);
      locationRequest.setFastestInterval(5000);
      locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

      LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

      SettingsClient settingsClient = LocationServices.getSettingsClient(RestaurantActivity.this);
      Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

      task.addOnSuccessListener(RestaurantActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
          @Override
          public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
             // mapFragment.getDeviceLocation();
          }
      });

      task.addOnFailureListener(RestaurantActivity.this, new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              if (e instanceof ResolvableApiException) {
                  ResolvableApiException resolvable = (ResolvableApiException) e;
                  try {
                      resolvable.startResolutionForResult(RestaurantActivity.this, 51);
                  } catch (IntentSender.SendIntentException e1) {
                      e1.printStackTrace();
                  }
              }
          }
      });
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

                    assert currentUser != null;
                    String firstname = TextUtils.isEmpty(currentUser.getFirstname()) ? getString(R.string.info_no_firstname_found) : currentUser.getFirstname();
                    String lastname = TextUtils.isEmpty(currentUser.getLastname()) ? "" : currentUser.getLastname();
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

    @Override
    public void onLocationChanged(Location location) {


        latitude = location.getLatitude();
        longitude = location.getLongitude();
        sendLocationDataToFragments();


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

    // Launch Main Activity
    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Launch View Place Activity
    private void startViewPlaceActivity(){
        Intent intent = new Intent(this, ViewPlaceActivity.class);
        startActivity(intent);
    }

    // Launch Notification Activity
    private void startNotificationActivity(){
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
    }

    //
    private void detectPlaceIdForLunch(){

        UserHelper.getUser(getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                assert document != null;

                String restaurantPlaceIdInFirebase = document.getString("restaurantPlaceId");
                if(!TextUtils.isEmpty(restaurantPlaceIdInFirebase)){
                    placeDetailsData.setPlaceId(restaurantPlaceIdInFirebase);
                    startViewPlaceActivity();
                }
                else{
                    Toast.makeText(getApplicationContext(), getString(R.string.restaurant_no_choosen), Toast.LENGTH_LONG).show();
                }




            }
        });

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
                    startMainActivity();
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
