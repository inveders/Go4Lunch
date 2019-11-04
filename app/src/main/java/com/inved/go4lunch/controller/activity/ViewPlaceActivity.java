package com.inved.go4lunch.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.domain.RatingCalcul;
import com.inved.go4lunch.firebase.Restaurant;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.firebase.RestaurantInNormalModeHelper;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserFavoriteRestaurantHelper;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageAppMode;
import com.inved.go4lunch.utils.ManageRestaurantChoiceInNormalMode;
import com.inved.go4lunch.view.WorkmatesAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.MAP_API_KEY;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.TAG;
import static com.inved.go4lunch.controller.fragment.MapFragment.RESTAURANT_PLACE_ID;

public class ViewPlaceActivity extends BaseActivity implements WorkmatesAdapter.Listener {

    @BindView(R.id.activity_view_place_photo)
    ImageView viewPlacePhoto;
    @BindView(R.id.activity_view_place_name)
    TextView viewPlaceName;
    @BindView(R.id.activity_view_place_address)
    TextView viewPlaceAddress;
    @BindView(R.id.activity_view_place_no_workmates_text)
    TextView textViewRecyclerViewEmpty;

    Context context;


    @BindView(R.id.activity_view_place_call_image)
    ImageView viewPlaceCallImage;
    @BindView(R.id.activity_view_place_call_image_text)
    TextView viewPlaceCallImageText;
    @BindView(R.id.activity_view_place_like_image)
    ImageView viewPlaceLikeImage;
    @BindView(R.id.activity_view_place_like_image_text)
    TextView viewPlaceLikeText;
    @BindView(R.id.activity_view_place_website_image)
    ImageView viewPlaceWebsiteImage;
    @BindView(R.id.activity_view_place_website_image_text)
    TextView viewPlaceWebsiteImageText;
    @BindView(R.id.activity_view_place_floating_button)
    FloatingActionButton isChoosenRestaurantImage;
    @BindView(R.id.activity_view_place_like_start_first)
    ImageView likeStarFirst;
    @BindView(R.id.activity_view_place_like_start_second)
    ImageView likeStarSecond;
    @BindView(R.id.activity_view_place_like_start_third)
    ImageView likeStarThird;

    //FOR DATA
    private String restaurantName;
    private String restaurantAddress;
    private String phoneNumber;
    private String currentPlaceId;
    private String website;
    private int rating;

    private WorkmatesAdapter mRecyclerWorkmatesAdapter;
    private RecyclerView mRecyclerWorkmates;
    private RatingCalcul ratingCalcul = new RatingCalcul();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        currentPlaceId = getIntent().getStringExtra(RESTAURANT_PLACE_ID);
        initializeViewPlace();


        //RecyclerView initialization
        mRecyclerWorkmates = findViewById(R.id.activity_view_place_recycler_view);

    }

    private void initializeViewPlace() {


        if (ManageAppMode.getAppMode(this).equals(getString(R.string.app_mode_work)) || ManageAppMode.getAppMode(this).equals(getString(R.string.app_mode_forced_work))) {
            RestaurantHelper.getRestaurant(currentPlaceId).addOnSuccessListener(documentSnapshot -> {
                Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);

                if (restaurant != null) {

                    restaurantName = restaurant.getRestaurantName();
                    restaurantAddress = restaurant.getRestaurantAddress();
                    phoneNumber = restaurant.getPhoneNumber();
                    website = restaurant.getWebsite();
                    rating = restaurant.getRatingApp();
                    initializationChoosenRestaurants(currentPlaceId, restaurantName, restaurantAddress);
                    updateViewPlaceActivity(restaurantName, restaurantAddress);
                    updatePhotoViewPlace(currentPlaceId);
                    displayAllWorkmatesJoining(currentPlaceId);
                    actionOnButton(phoneNumber, website);
                    initializationLikedRestaurants(currentPlaceId);
                    actionOnLikeButton(currentPlaceId);
                    showingLikeStars(rating);
                } else {

                    initializeViewPlaceWithFetchDetailDirectly();
                }


            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {

                    // Handle error with given status code.
                    Log.e("debago", "Error " + exception.getMessage());
                }
            });
        } else {
            if (this.getCurrentUser() != null) {
                RestaurantInNormalModeHelper.getRestaurant(this.getCurrentUser().getUid(), currentPlaceId).addOnSuccessListener(documentSnapshot -> {
                    Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);

                    if (restaurant != null) {

                        restaurantName = restaurant.getRestaurantName();
                        restaurantAddress = restaurant.getRestaurantAddress();
                        phoneNumber = restaurant.getPhoneNumber();
                        website = restaurant.getWebsite();
                        rating = restaurant.getRatingApp();
                        initializationChoosenRestaurants(currentPlaceId, restaurantName, restaurantAddress);
                        updateViewPlaceActivity(restaurantName, restaurantAddress);
                        updatePhotoViewPlace(currentPlaceId);
                        displayAllWorkmatesJoining(currentPlaceId);
                        actionOnButton(phoneNumber, website);
                        initializationLikedRestaurants(currentPlaceId);
                        actionOnLikeButton(currentPlaceId);
                        showingLikeStars(rating);
                    } else {
                        initializeViewPlaceWithFetchDetailDirectly();
                    }


                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {

                        // Handle error with given status code.
                        Log.e("debago", "This restaurant is not found in database: " + exception.getMessage());

                    }
                });
            }
        }


    }

    private void initializeViewPlaceWithFetchDetailDirectly() {

        // Initialize Places.
        Places.initialize(App.getInstance().getApplicationContext(), MAP_API_KEY);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(App.getInstance().getApplicationContext());

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.RATING);


        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(currentPlaceId, placeFields)
                .build();


        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            restaurantName = place.getName();
            restaurantAddress = place.getAddress();
            phoneNumber = place.getPhoneNumber();

            if (place.getWebsiteUri() != null) {
                website = place.getWebsiteUri().toString();
            } else {
                website = "";
            }

            if (place.getRating() != null) {
                rating = ratingCalcul.ratingValueCalcul(place.getRating().intValue());
            } else {
                rating = 1;
            }

            isChoosenRestaurantImage.hide();
            initializationChoosenRestaurants(currentPlaceId, restaurantName, restaurantAddress);
            updateViewPlaceActivity(restaurantName, restaurantAddress);
            updatePhotoViewPlace(currentPlaceId);
            displayAllWorkmatesJoining(currentPlaceId);
            actionOnButton(phoneNumber, website);
            initializationLikedRestaurants(currentPlaceId);
            actionOnLikeButton(currentPlaceId);
            showingLikeStars(rating);


        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {

                Log.e(TAG, "Place detail not found: " + exception.getMessage());
            }
        });


    }

    public void showingLikeStars(int ratingValue) {

        switch (ratingValue) {
            case 1:
                likeStarFirst.setVisibility(View.VISIBLE);
                likeStarSecond.setVisibility(View.INVISIBLE);
                likeStarThird.setVisibility(View.INVISIBLE);
                break;
            case 2:
                likeStarFirst.setVisibility(View.VISIBLE);
                likeStarSecond.setVisibility(View.VISIBLE);
                likeStarThird.setVisibility(View.INVISIBLE);
                break;
            case 3:
                likeStarFirst.setVisibility(View.VISIBLE);
                likeStarSecond.setVisibility(View.VISIBLE);
                likeStarThird.setVisibility(View.VISIBLE);
                break;
            default:
                likeStarFirst.setVisibility(View.INVISIBLE);
                likeStarSecond.setVisibility(View.INVISIBLE);
                likeStarThird.setVisibility(View.INVISIBLE);
        }


    }


    private void initializationChoosenRestaurants(String mCurrentPlaceId, String myRestaurantName, String myRestaurantVicinity) {

        if (getCurrentUser() != null) {
            UserHelper.getUserWhateverLocation(getCurrentUser().getUid()).get().addOnCompleteListener(task -> {

                QuerySnapshot document = task.getResult();
                if (document != null) {
                    String restaurantPlaceIdInFirebase = document.getDocuments().get(0).getString("restaurantPlaceId");

                    if (ManageAppMode.getAppMode(this).equals(getString(R.string.app_mode_normal))) {
                        if (ManageRestaurantChoiceInNormalMode.getRestaurantChoice(this) == null || !ManageRestaurantChoiceInNormalMode.getRestaurantChoice(this).equals(mCurrentPlaceId)) {

                            isChoosenRestaurantImage.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B70400")));//red color

                        } else {

                            isChoosenRestaurantImage.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));//green color

                        }
                    } else {
                        if (TextUtils.isEmpty(restaurantPlaceIdInFirebase) || !restaurantPlaceIdInFirebase.equals(mCurrentPlaceId)) {
                            isChoosenRestaurantImage.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B70400")));//red color
                        } else {
                            isChoosenRestaurantImage.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));//green color

                        }
                    }

                }


            });
        }


        isChoosenRestaurantImage.setOnClickListener(view -> UserHelper.getUserWhateverLocation(getCurrentUser().getUid()).get().addOnCompleteListener(task -> {

            QuerySnapshot document = task.getResult();

            if (document != null) {
                if (ManageAppMode.getAppMode(this).equals(getString(R.string.app_mode_normal))) {

                    if (ManageRestaurantChoiceInNormalMode.getRestaurantChoice(this) == null) {
                        ManageRestaurantChoiceInNormalMode.saveRestaurantChoice(this, currentPlaceId);
                        ManageRestaurantChoiceInNormalMode.saveRestaurantName(this, myRestaurantName);
                        ManageRestaurantChoiceInNormalMode.saveRestaurantAddress(this, myRestaurantVicinity);
                        changeButtonColor("#4CAF50");//green color

                    } else if (!ManageRestaurantChoiceInNormalMode.getRestaurantChoice(this).equals(mCurrentPlaceId)) {


                        new android.app.AlertDialog.Builder(this)
                                // Add the buttons
                                .setPositiveButton(R.string.popup_message_choice_yes, (dialog, which) -> {
                                    ManageRestaurantChoiceInNormalMode.saveRestaurantChoice(this, currentPlaceId);
                                    ManageRestaurantChoiceInNormalMode.saveRestaurantName(this, myRestaurantName);
                                    ManageRestaurantChoiceInNormalMode.saveRestaurantAddress(this, myRestaurantVicinity);
                                    changeButtonColor("#4CAF50");//green color
                                })
                                .setNegativeButton(R.string.popup_message_choice_no, (dialog, which) -> {

                                })
                                .setMessage(context.getString(R.string.alert_dialog_view_activity, restaurantName))
                                .show();
                    } else {

                        new android.app.AlertDialog.Builder(this)
                                // Add the buttons
                                .setPositiveButton(R.string.popup_message_choice_yes, (dialog, which) -> {
                                    ManageRestaurantChoiceInNormalMode.saveRestaurantChoice(this, null);
                                    ManageRestaurantChoiceInNormalMode.saveRestaurantName(this, null);
                                    ManageRestaurantChoiceInNormalMode.saveRestaurantAddress(this, null);
                                    changeButtonColor("#B70400");//red color
                                })
                                .setNegativeButton(R.string.popup_message_choice_no, (dialog, which) -> {

                                })
                                .setMessage(context.getString(R.string.alert_dialog_view_activity_no_choice_yet))
                                .show();


                    }


                } else {
                    String restaurantPlaceIdInFirebase = document.getDocuments().get(0).getString("restaurantPlaceId");
                    clickOnButton(currentPlaceId, restaurantPlaceIdInFirebase, myRestaurantName, myRestaurantVicinity);
                }

            }


        }));


    }

    private void clickOnButton(String mCurrentPlaceId, String restaurantPlaceIdInFirebase, String myRestaurantName, String myRestaurantVicinity) {


        if (TextUtils.isEmpty(restaurantPlaceIdInFirebase)) { //if there is no restaurant in my firebase
            //We retrieve the new restaurant to increment customers's number

            RestaurantHelper.getRestaurant(mCurrentPlaceId).addOnCompleteListener(task -> {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    Restaurant restaurant = document.toObject(Restaurant.class);

                    if (restaurant != null) {

                        int currentNewRestaurantCustomersFirebase = restaurant.getRestaurantCustomers();

                        //update new restaurant
                        int updateCustomersNewRestaurant = currentNewRestaurantCustomersFirebase + 1;
                        RestaurantHelper.updateRestaurantCustomers(updateCustomersNewRestaurant, mCurrentPlaceId);
                        if (getCurrentUser() != null) {
                            UserHelper.updateRestaurantPlaceId(mCurrentPlaceId, getCurrentUser().getUid());
                            UserHelper.updateRestaurantName(myRestaurantName, getCurrentUser().getUid());
                            UserHelper.updateRestaurantVicinity(myRestaurantVicinity, getCurrentUser().getUid());
                        }

                    }

                }


            });

            changeButtonColor("#4CAF50");//green color

        } else if (!restaurantPlaceIdInFirebase.equals(mCurrentPlaceId)) { //if there is one restaurant in my firebase but different of actual view place

            new AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.alert_dialog_view_activity, restaurantName))
                    .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) -> {

                        //We retrieve the old restaurant to decrease customers's number

                        RestaurantHelper.getRestaurant(restaurantPlaceIdInFirebase).addOnCompleteListener(task -> {
                            DocumentSnapshot document = task.getResult();

                            if (document != null) {
                                Restaurant restaurant = document.toObject(Restaurant.class);

                                if (restaurant != null) {

                                    int currentCustomersFirebase = restaurant.getRestaurantCustomers();

                                    int updateCustomersOldRestaurant;
                                    //update Old restaurant
                                    if (currentCustomersFirebase != 0) {
                                        updateCustomersOldRestaurant = currentCustomersFirebase - 1;
                                        RestaurantHelper.updateRestaurantCustomers(updateCustomersOldRestaurant, restaurantPlaceIdInFirebase);
                                    }
                                }
                            }

                        });


                        //We retrieve the new restaurant customers's to increase customers's number

                        RestaurantHelper.getRestaurant(mCurrentPlaceId).addOnCompleteListener(task -> {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                Restaurant restaurant = document.toObject(Restaurant.class);

                                if (restaurant != null) {

                                    int currentNewRestaurantCustomersFirebase = restaurant.getRestaurantCustomers();
                                    //update new restaurant
                                    int updateCustomersNewRestaurant = currentNewRestaurantCustomersFirebase + 1;
                                    RestaurantHelper.updateRestaurantCustomers(updateCustomersNewRestaurant, mCurrentPlaceId);
                                    if (getCurrentUser() != null) {
                                        UserHelper.updateRestaurantPlaceId(mCurrentPlaceId, getCurrentUser().getUid());
                                        UserHelper.updateRestaurantName(myRestaurantName, getCurrentUser().getUid());
                                        UserHelper.updateRestaurantVicinity(myRestaurantVicinity, getCurrentUser().getUid());
                                    }

                                }

                            }

                        });


                        changeButtonColor("#4CAF50");//green color


                    })
                    .setNegativeButton(R.string.popup_message_choice_no, null)
                    .show();


        } else { //if there is one restaurant in my firebase and he is the same than actual view place

            new AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.alert_dialog_view_activity_no_choice_yet))
                    .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) -> {

                        //We retrieve the old restaurant customers's number
                        RestaurantHelper.getRestaurant(restaurantPlaceIdInFirebase).addOnCompleteListener(task -> {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                Restaurant restaurant = document.toObject(Restaurant.class);

                                if (restaurant != null) {

                                    int currentCustomersFirebase = restaurant.getRestaurantCustomers();
                                    int updateCustomersOldRestaurant;
                                    //update Old restaurant
                                    if (currentCustomersFirebase != 0) {
                                        updateCustomersOldRestaurant = currentCustomersFirebase - 1;
                                        RestaurantHelper.updateRestaurantCustomers(updateCustomersOldRestaurant, restaurantPlaceIdInFirebase);
                                        if (getCurrentUser() != null) {
                                            UserHelper.updateRestaurantPlaceId(null, getCurrentUser().getUid());
                                            UserHelper.updateRestaurantName(null, getCurrentUser().getUid());
                                            UserHelper.updateRestaurantVicinity(null, getCurrentUser().getUid());
                                        }

                                    }

                                }
                            }
                        });
                        changeButtonColor("#B70400");//red color
                    })
                    .setNegativeButton(R.string.popup_message_choice_no, null)
                    .show();


        }

    }

    public void changeButtonColor(String newColor) {

        isChoosenRestaurantImage.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(newColor)));

    }

    private void initializationLikedRestaurants(String currentPlaceId) {

        if (getCurrentUser() != null) {
            UserFavoriteRestaurantHelper.getCurrentRestaurantPlaceId(getCurrentUser().getUid(), currentPlaceId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    if (task.getResult() != null) {

                        if (!task.getResult().getDocuments().isEmpty()) {
                            changeLikeButtonColor(getString(R.string.changeButtonColor_Yellow));
                        } else {
                            changeLikeButtonColor(getString(R.string.changeButtonColor_Orange));
                        }


                    }
                }

            });
        }
    }




    private void actionOnLikeButton(String currentPlaceId) {

        viewPlaceLikeImage.setOnClickListener(v -> {
            if (getCurrentUser() != null) {
                UserFavoriteRestaurantHelper.getCurrentRestaurantPlaceId(getCurrentUser().getUid(), currentPlaceId).get().addOnCompleteListener(task -> {

                    if(task.getResult()!=null){
                        if (!task.getResult().getDocuments().isEmpty()) {

                            changeLikeButtonColor(getString(R.string.changeButtonColor_Orange));
                            UserFavoriteRestaurantHelper.deleteFavoriteRestaurant(getCurrentUser().getUid(), currentPlaceId);
                        } else {

                            changeLikeButtonColor(getString(R.string.changeButtonColor_Yellow));
                            UserFavoriteRestaurantHelper.createUserFavoriteRestaurants(getCurrentUser().getUid(), currentPlaceId);
                        }
                    }


                }).addOnFailureListener(e -> Log.e("debago", "Problem during the sort in work mode"));
            }
        });

        viewPlaceLikeText.setOnClickListener(v -> {
            if (getCurrentUser() != null) {
                UserFavoriteRestaurantHelper.getCurrentRestaurantPlaceId(getCurrentUser().getUid(), currentPlaceId).get().addOnCompleteListener(task -> {

                    if(task.getResult()!=null){
                        if (!task.getResult().getDocuments().isEmpty()) {

                            changeLikeButtonColor(getString(R.string.changeButtonColor_Orange));
                            UserFavoriteRestaurantHelper.deleteFavoriteRestaurant(getCurrentUser().getUid(), currentPlaceId);
                        } else {

                            changeLikeButtonColor(getString(R.string.changeButtonColor_Yellow));
                            UserFavoriteRestaurantHelper.createUserFavoriteRestaurants(getCurrentUser().getUid(), currentPlaceId);
                        }
                    }

                }).addOnFailureListener(e -> Log.e("debago", "Problem during the sort in work mode"));
            }
        });


    }

    private void actionOnButton(String phoneNumber, String website) {

        viewPlaceCallImage.setOnClickListener(view -> {

            //Launch the call
            if (!TextUtils.isEmpty(phoneNumber)) {

                Intent appel = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(appel);

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_phone_number), Toast.LENGTH_SHORT).show();
            }

        });

        viewPlaceCallImageText.setOnClickListener(view -> {

            //Launch the call
            if (!TextUtils.isEmpty(phoneNumber)) {

                Intent appel = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(appel);

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_phone_number), Toast.LENGTH_SHORT).show();
            }

        });


        viewPlaceWebsiteImage.setOnClickListener(view -> {

            //Creation of the Chrome Custom Tabs
            if (!TextUtils.isEmpty(website)) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(context, Uri.parse(website));
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_website), Toast.LENGTH_SHORT).show();
            }


        });

        viewPlaceWebsiteImageText.setOnClickListener(view -> {

            //Creation of the Chrome Custom Tabs
            if (!TextUtils.isEmpty(website)) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(context, Uri.parse(website));
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_website), Toast.LENGTH_SHORT).show();
            }


        });


    }


    private void changeLikeButtonColor(String newColor) {

        viewPlaceLikeImage.setColorFilter(Color.parseColor(newColor));

    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_view_place;
    }


    public void updateViewPlaceActivity(String restaurantName, String restaurantAddress) {

        //Textes
        viewPlaceName.setText(restaurantName);
        viewPlaceAddress.setText(restaurantAddress);

    }

    private void updatePhotoViewPlace(String placeId) {

        // Initialize Places.
        Places.initialize(App.getInstance().getApplicationContext(), MAP_API_KEY);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(App.getInstance().getApplicationContext());

        // Specify the fields to return.
        List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);

        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, fields).build();

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            // Get the photo metadata.
            PhotoMetadata photoMetadata;

            if (place.getPhotoMetadatas() != null) {
                photoMetadata = place.getPhotoMetadatas().get(0);
            } else {
                photoMetadata = null;
            }

            // Get the attribution text.
            String attributions;
            if (photoMetadata != null) {
                attributions = photoMetadata.getAttributions();
            } else {
                attributions = getString(R.string.image_content_description);
            }
            viewPlacePhoto.setContentDescription(attributions);

            if (photoMetadata != null) {
                // Create a FetchPhotoRequest.
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500) // Optional.
                        .setMaxHeight(300) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    viewPlacePhoto.setImageBitmap(bitmap);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {

                        // Handle error with given status code.
                        Log.e("debago", "Place not found: " + exception.getMessage());
                    }
                });
            } else {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_connexion_activity_flou_ok);
                viewPlacePhoto.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void displayAllWorkmatesJoining(String currentPlaceId) {

        this.mRecyclerWorkmatesAdapter = new WorkmatesAdapter(generateOptionsForAdapter(UserHelper.getAllWorkmatesJoining(currentPlaceId)), Glide.with(this), this, this);
        //Choose how to display the list in the RecyclerView (vertical or horizontal)
        mRecyclerWorkmates.setHasFixedSize(true); //REVOIR CELA
        mRecyclerWorkmates.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerWorkmates.addItemDecoration(new DividerItemDecoration(mRecyclerWorkmates.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerWorkmates.setAdapter(mRecyclerWorkmatesAdapter);
    }

    // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onDataChanged() {

        // Show TextView in case RecyclerView is empty
        if (!ManageAppMode.getAppMode(this).equals(getString(R.string.app_mode_normal))) {
            textViewRecyclerViewEmpty.setVisibility(this.mRecyclerWorkmatesAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }


    }


}

