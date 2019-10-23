package com.inved.go4lunch.controller.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.inved.go4lunch.BuildConfig;
import com.inved.go4lunch.R;
import com.inved.go4lunch.auth.ProfileActivity;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.controller.fragment.FragmentAdapter;
import com.inved.go4lunch.controller.fragment.ListViewFragment;
import com.inved.go4lunch.controller.fragment.MapFragment;
import com.inved.go4lunch.controller.fragment.PeopleFragment;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.firebase.RestaurantInNormalModeHelper;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.model.ResultModel;
import com.inved.go4lunch.notification.NotificationsActivity;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageAppMode;
import com.inved.go4lunch.utils.ManageAutocompleteResponse;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import java.util.List;
import java.util.Objects;

import static com.inved.go4lunch.controller.fragment.MapFragment.RESTAURANT_PLACE_ID;
import static java.lang.Math.cos;

public class RestaurantActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    public static final String MAP_API_KEY = BuildConfig.GOOGLE_MAPS_API_KEY;

    //FOR LOCAL BROADCAST MANAGER

    public static final String PLACE_SEARCH_DATA = "PLACE_SEARCH_DATA";
    private Dialog mDialog;
    public static final String TAG = "Debago";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 645;

    //FOR LOCATION
    Location location; // location
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1500; // 1000 meters for tests, after come back to 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute*/

    List<Place.Field> fields;

    double latitude; // latitude
    double longitude; // longitude

    //FOR DATA
    private ResultModel resultModel;
    MenuItem logout;
    TextView navFirstname;
    TextView navLastname;
    TextView navEmail;
    ImageView navProfileImage;


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



    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private FragmentRefreshListener fragmentRefreshListener;

    public interface FragmentRefreshListener {
        void onRefresh();
    }

    public MapFragmentRefreshListener getMapFragmentRefreshListener() {
        return mapFragmentRefreshListener;
    }

    public void setMapFragmentRefreshListener(MapFragmentRefreshListener mapFragmentRefreshListener) {
        this.mapFragmentRefreshListener = mapFragmentRefreshListener;
    }

    private MapFragmentRefreshListener mapFragmentRefreshListener;

    public interface MapFragmentRefreshListener {
        void onMapRefresh();
    }

    @Override
    public int getFragmentLayout() {

        return R.layout.activity_restaurant;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //initializeRestaurantActivity();


        this.configureToolBar();


        checkLocation();
        //Bottom Navigation View
        bottomNavigationView = findViewById(R.id.activity_restaurant_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //NavigationDrawer
        logout = findViewById(R.id.activity_main_drawer_logout);

        //Viewpager
        viewPager = findViewById(R.id.viewpager_fragment); //Init Viewpager
        setupFm(getSupportFragmentManager(), viewPager); //Setup Fragment
        viewPager.setCurrentItem(0); //Set Currrent Item When Activity Start
        viewPager.addOnPageChangeListener(new PageChange()); //Listeners For Viewpager When Page Changed

        //Configuration navigation view header
        NavigationView mNavigationView = findViewById(R.id.activity_restaurant_nav_view);
        navFirstname = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_FirstName);
        navLastname = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_LastName);
        navEmail = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_Email);
        navProfileImage = mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_profile_image);

        //All view configuration

        this.configureDrawerLayout();
        this.configureNavigationView();

        this.userInformationFromFirebase();
        //Check data
        //Assign the value to declared resultModel variable
        resultModel = ViewModelProviders.of(this).get(ResultModel.class);



    }



    @SuppressLint("MissingPermission")
    private void checkLocation() {

        //Subscribe to providers
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (lm != null) {
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

    }

    public void fillFirebase(Double lat, Double longi) {
        checkPosition();
        resultModel.setNearbyRestaurantsInFirebase(lat, longi).observe(this, result -> refreshFragment());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnMenuItemClickListener(menuItem -> {
            startAutocompleteWidgetShow();
            menu.findItem(R.id.action_search).setVisible(false);
            menu.findItem(R.id.action_clear).setVisible(true);
            return true;
        });

        final MenuItem clearItem = menu.findItem(R.id.action_clear);
        clearItem.setOnMenuItemClickListener(menuItem -> {
            ManageAutocompleteResponse.saveAutocompleteStringResponse(Objects.requireNonNull(this), ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_NAME, "");
            refreshFragment();

            menu.findItem(R.id.action_search).setVisible(true);
            menu.findItem(R.id.action_clear).setVisible(false);

            return true;
        });


        return true;
    }


    private void refreshFragment() {

        if (getFragmentRefreshListener() != null) {
            getFragmentRefreshListener().onRefresh();
        }

        if (getMapFragmentRefreshListener() != null) {
            getMapFragmentRefreshListener().onMapRefresh();
        }



    }

    //PLACE AUTOCOMPLETE

    private void startAutocompleteWidgetShow() {

        RectangularBounds boundsRect = RectangularBounds.newInstance(
                new LatLng(calculateBound("SW_LAT"), calculateBound("SW_LNG")),
                new LatLng(calculateBound("NE_LAT"), calculateBound("NE_LNG")));

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setHint(getString(R.string.Enter_restaurant_name))
                .setLocationRestriction(boundsRect)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private double calculateBound(String lat_long) {

        double eartch_circumference = 40075.04;
        double distance_in_meter = 15000;
        double distance_in_km = distance_in_meter / 1000;
        double lat_center = getLatitude();
        double longi_center = getLongitude();

        double north_south_move = (360 * distance_in_km) / eartch_circumference;
        double east_west_move = (360 * distance_in_km) / (eartch_circumference * cos(convertInRadians(lat_center)));

        double north_east_lat = lat_center + north_south_move;
        double north_east_longi = longi_center + east_west_move;
        double south_west_lat = lat_center - north_south_move;
        double south_west_longi = longi_center - east_west_move;

        switch (lat_long) {
            case "NE_LAT":
                return north_east_lat;

            case "NE_LNG":
                return north_east_longi;

            case "SW_LAT":
                return south_west_lat;

            case "SW_LNG":
                return south_west_longi;

            default:
                return 0.0;
        }

    }

    private double convertInRadians(double degre) {

        return Math.PI / (180 * degre);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);

                ManageAutocompleteResponse.saveAutocompleteStringResponse(this, ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_ID, place.getId());
                ManageAutocompleteResponse.saveAutocompleteStringResponse(this, ManageAutocompleteResponse.KEY_AUTOCOMPLETE_PLACE_NAME, place.getName());

                ManageAutocompleteResponse.saveAutocompleteLongResponseFromDouble(this, ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LATITUDE, Objects.requireNonNull(place.getLatLng()).latitude);
                ManageAutocompleteResponse.saveAutocompleteLongResponseFromDouble(this, ManageAutocompleteResponse.KEY_AUTOCOMPLETE_LONGITUDE, place.getLatLng().longitude);


                if (ManageAppMode.getAppMode(this).equals(getString(R.string.app_mode_normal))) {
                    if (getCurrentUser() != null) {
                        //We check if the autocomplete placeId is in our firebase.
                        RestaurantInNormalModeHelper.getRestaurant(this.getCurrentUser().getUid(), place.getId()).addOnSuccessListener(documentSnapshot -> {

                            if (documentSnapshot.getString("restaurantPlaceId") == null) {

                                if (place.getId() != null) {

                                    dialogToGoInViewPlaceIfAutocomplete(place.getId());
                                }
                            }

                        });
                    }

                } else {

                    //We check if the autocomplete placeId is in our firebase.
                    RestaurantHelper.getRestaurant(place.getId()).addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.getString("restaurantPlaceId") == null) {
                            if (place.getId() != null) {

                                dialogToGoInViewPlaceIfAutocomplete(place.getId());
                            }

                        }

                    });

                }


            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                if (data != null) {
                    Status status = Autocomplete.getStatusFromIntent(data);
                    if (status.getStatusMessage() != null) {
                        Log.i(TAG, status.getStatusMessage());
                    }
                }

            } else if (resultCode == RESULT_CANCELED) {

                Log.i(TAG, "User canceled the operation");
            }
        }
    }

    private void startViewPlaceActivity(String autocompletePlaceId) {

        Intent intent = new Intent(this, ViewPlaceActivity.class);
        intent.putExtra(RESTAURANT_PLACE_ID, autocompletePlaceId);
        startActivity(intent);
    }

    private void dialogToGoInViewPlaceIfAutocomplete(String autocompletePlace) {

        new AlertDialog.Builder(this)
                // Add the buttons
                .setPositiveButton(R.string.popup_message_choice_yes, (dialog, which) -> {
                    // do something like...
                    startViewPlaceActivity(autocompletePlace);
                })
                .setNegativeButton(R.string.popup_message_choice_no, (dialog, which) -> {
                    // do nothing stay on the page...
                    refreshFragment();
                    Toast.makeText(this, getString(R.string.places_autocomplete_clear_button), Toast.LENGTH_SHORT).show();
                })
                .setMessage(getString(R.string.restaurant_activity_autocomplete_no_near_you))
                .show();

    }


    @Override
    public void onResume() {
        super.onResume();
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

        switch (id) {
            case R.id.activity_main_drawer_lunch:
                startNotificationActivity();//this.detectPlaceIdForLunch();
                break;
            case R.id.activity_main_drawer_settings:
                this.startProfileActivity();
                break;
            case R.id.activity_main_drawer_logout:
                signOutUserFromFirebase();
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
                    toolbar.setTitle(getString(R.string.toolbar_title_restaurant_activity));
                    return true;
                case R.id.action_list:
                    viewPager.setCurrentItem(1);
                    toolbar.setTitle(getString(R.string.toolbar_title_restaurant_activity));
                    return true;
                case R.id.action_people:
                    viewPager.setCurrentItem(2);
                    toolbar.setTitle(getString(R.string.toolbar_title_people_fragment));
                    return true;
            }
            return false;
        }
    };


    private void checkPosition() {
        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(RestaurantActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(RestaurantActivity.this, locationSettingsResponse -> {
            // mapFragment.getDeviceLocation();
        });

        task.addOnFailureListener(RestaurantActivity.this, e -> {
            if (e instanceof ResolvableApiException) {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                try {
                    resolvable.startResolutionForResult(RestaurantActivity.this, 51);
                } catch (IntentSender.SendIntentException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }


    // Configure Toolbar
    private void configureToolBar() {
        this.toolbar = findViewById(R.id.activity_restaurant_toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.toolbar_title_restaurant_activity));
    }


    // Configure Drawer Layout
    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.activity_restaurant_drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void userInformationFromFirebase() {

        if (this.getCurrentUser() != null) {

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {

                Glide.with(this)

                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(navProfileImage);
            }

            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();

            if (email != null) {
                navEmail.setText(email);
            }
            // 7 - Get data from Firestore

            UserHelper.getUserWhateverLocation(this.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        User currentUser = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);

                        assert currentUser != null;
                        String firstname = TextUtils.isEmpty(currentUser.getFirstname()) ? getString(R.string.info_no_firstname_found) : currentUser.getFirstname();
                        String lastname = TextUtils.isEmpty(currentUser.getLastname()) ? "" : currentUser.getLastname();
                        navFirstname.setText(firstname);
                        navLastname.setText(lastname);

                    });

        }
    }

    // Configure NavigationView
    private void configureNavigationView() {
        NavigationView navigationView = findViewById(R.id.activity_restaurant_nav_view);
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


    public void sendLocationDataToFragments() {
        String currentGeolocalisation = "" + getLatitude() + "," + getLongitude() + "";
        final Intent intent = new Intent(KEY_LOCATION_CHANGED);
        intent.putExtra(KEY_GEOLOCALISATION, currentGeolocalisation);
        intent.putExtra(KEY_LATITUDE, getLatitude());
        intent.putExtra(KEY_LONGITUDE, getLongitude());
        LocalBroadcastManager.getInstance(RestaurantActivity.this).sendBroadcast(intent);

        fillFirebase(getLatitude(), getLongitude());
        this.checkDistanceFromWork(currentGeolocalisation, ManageJobPlaceId.getJobPlaceId(this), getLatitude(), getLongitude());

    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }


    public double getLongitude() {
        if (location != null) {
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

    public static void setupFm(FragmentManager fragmentManager, ViewPager viewPager) {
        FragmentAdapter Adapter = new FragmentAdapter(fragmentManager);
        //Add All Fragment To List
        Adapter.add(new MapFragment(), "Page Map");
        Adapter.add(new ListViewFragment(), "Page List View");
        Adapter.add(new PeopleFragment(), "Page People");
        viewPager.setAdapter(Adapter);
    }


    // Launch Profile Activity
    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // Launch Main Activity
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Launch Notification Activity
    private void startNotificationActivity() {
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
    }

    //Signout

    //   @OnClick(R.id.activity_main_drawer_logout)
    public void signOutUserFromFirebase() {
        AuthUI.getInstance()

                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());


    }

    // Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted() {
        return aVoid -> {
            startMainActivity();
            finish();
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


    //HANDLING MODE

    //Handle two modes for user : work mode and normal mode to be located everywhere
    public void checkDistanceFromWork(String origins, String destinations, Double lat, Double longi) {

        resultModel.getMatrixDistance(origins, destinations).observe(this, result -> {

            int distance;
            String appMode = ManageAppMode.getAppMode(this);
            try {
                distance = (result.get(0).getElements().get(0).getDistance().getValue()) / 1000;
            } catch (Exception e) {
                Log.e("error", "Error try catch " + e.getMessage());
                distance = 3;

            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    // Add the buttons
                    .setPositiveButton(R.string.alert_dialog_location_far_from_works_pos_button, (dialog, which) -> changeMode(false, lat, longi))
                    .setNegativeButton(R.string.alert_dialog_location_far_from_works_neg_button, (dialog, which) -> changeMode(true, lat, longi))
                    .setMessage(R.string.alert_dialog_location_far_from_works_text);

            // Create the AlertDialog

            if (distance > 2 && appMode.equals(App.getResourses().getString(R.string.app_mode_work))) {

                // Dismiss any old dialog.
                if (mDialog != null) {
                    mDialog.dismiss();
                }

                // Show the new dialog.
                mDialog = builder.show();

            } else if (distance < 2 && appMode.equals(App.getResourses().getString(R.string.app_mode_normal))) {
                changeMode(true, lat, longi);

            } else if (distance < 2 && appMode.equals(App.getResourses().getString(R.string.app_mode_forced_work))) {
                changeMode(true, lat, longi);

            }

        });

    }

    private void changeMode(boolean workModeDesired, Double lat, Double longi) {
        if (workModeDesired) {
            if (ManageAppMode.getAppMode(this).equals(getString(R.string.app_mode_work))) {
                ManageAppMode.saveAppMode(this, getString(R.string.app_mode_forced_work));
            } else {
                ManageAppMode.saveAppMode(this, getString(R.string.app_mode_work));
            }

            Toast.makeText(this, getString(R.string.app_mode_change_to_work_mode), Toast.LENGTH_SHORT).show();

        } else {
            ManageAppMode.saveAppMode(this, getString(R.string.app_mode_normal));
            Toast.makeText(this, getString(R.string.app_mode_change_to_normal_mode), Toast.LENGTH_SHORT).show();
        }
        fillFirebase(lat, longi);
    }


}
