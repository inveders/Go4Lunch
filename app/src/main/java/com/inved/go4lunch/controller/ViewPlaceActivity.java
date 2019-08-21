package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.auth.ProfileActivity;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserFavoriteRestaurantHelper;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;


import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_ADDRESS;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_NAME;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHONE_NUMBER;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PLACE_ID;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_RATING;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_WEBSITE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DETAIL_DATA;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_SEARCH_DATA;
import static com.inved.go4lunch.utils.ManageJobPlaceId.KEY_JOB_PLACE_ID_DATA;

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


    private static final int UPDATE_RESTAURANT_PLACE_ID = 40;

    @BindView(R.id.activity_view_place_call_image)
    ImageView viewPlaceCallImage;
    @BindView(R.id.activity_view_place_like_image)
    ImageView viewPlaceLikeImage;
    @BindView(R.id.activity_view_place_website_image)
    ImageView viewPlaceWebsiteImage;
    @BindView(R.id.activity_view_place_button_choose_restaurant)
    ImageView isChoosenRestaurantImage;
    @BindView(R.id.activity_view_place_like_start_first)
    ImageView likeStarFirst;
    @BindView(R.id.activity_view_place_like_start_second)
    ImageView likeStarSecond;
    @BindView(R.id.activity_view_place_like_start_third)
    ImageView likeStarThird;

    private String restaurantName;
    private String restaurantAddress;
    private String phoneNumber;
    private String currentPlaceId;
    private String website;
    private double rating;
    private ProfileActivity profileActivity;
    private String jobPlaceId;

    private WorkmatesAdapter mRecyclerWorkmatesAdapter;
    private RecyclerView mRecyclerWorkmates;

    //FOR DATA

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PLACE_DETAIL_DATA.equals(intent.getAction())) {
                phoneNumber = intent.getStringExtra(PLACE_DATA_PHONE_NUMBER);
                restaurantName = intent.getStringExtra(PLACE_DATA_NAME);
                restaurantAddress = intent.getStringExtra(PLACE_DATA_ADDRESS);
                currentPlaceId = intent.getStringExtra(PLACE_DATA_PLACE_ID);
                website = intent.getStringExtra(PLACE_DATA_WEBSITE);
                rating = intent.getDoubleExtra(PLACE_DATA_RATING,0.0);

            }

            if (PLACE_SEARCH_DATA.equals(intent.getAction())) {


            }
            initializationChoosenRestaurants(currentPlaceId, restaurantName, restaurantAddress);
            updateViewPlaceActivity(restaurantName, restaurantAddress);
            updatePhotoViewPlace(currentPlaceId);
            displayAllWorkmatesJoining(currentPlaceId);
            actionOnButton(phoneNumber,website,currentPlaceId);
            initializationLikedRestaurants(currentPlaceId);
            actionOnLikeButton(currentPlaceId);
            //calculateStarSum(currentPlaceId);
            showingLikeStars(rating);



        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.activity_view_place);
        context = this;


        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_DETAIL_DATA));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_SEARCH_DATA));

        jobPlaceId = ManageJobPlaceId.getJobPlaceId(this,KEY_JOB_PLACE_ID_DATA);

        //RecyclerView initialization
        mRecyclerWorkmates = findViewById(R.id.activity_view_place_recycler_view);





    }

    private void calculateStarSum(String currentPlaceId) {
        RestaurantHelper.getAllRestaurantsLike(jobPlaceId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                QuerySnapshot querySnapshot = task.getResult();

                assert querySnapshot != null;

                long count = 0;
                long number=0;

                for (int i = 0; i < querySnapshot.size(); i++) {
                    long rating = querySnapshot.getDocuments().get(i).getLong("restaurantLike");
                    count = count + rating;
                    number = i;
                }

                long finalRating = count/number;



            }

        });
    }

    private void showingLikeStars(double ratingValue) {

        Log.d("TAG", "View Place Activity Rating Value is "+ratingValue);
        if(ratingValue>0 && ratingValue<1.665){
            likeStarFirst.setVisibility(View.VISIBLE);
            likeStarSecond.setVisibility(View.INVISIBLE);
            likeStarThird.setVisibility(View.INVISIBLE);
        }
        else if(ratingValue>=1.665 && ratingValue<3.33){
            likeStarFirst.setVisibility(View.VISIBLE);
            likeStarSecond.setVisibility(View.VISIBLE);
            likeStarThird.setVisibility(View.INVISIBLE);
        }
        else if(ratingValue>=3.33 && ratingValue<=5){
            likeStarFirst.setVisibility(View.VISIBLE);
            likeStarSecond.setVisibility(View.VISIBLE);
            likeStarThird.setVisibility(View.VISIBLE);
        }
        else if(ratingValue==0.0){
            likeStarFirst.setVisibility(View.INVISIBLE);
            likeStarSecond.setVisibility(View.INVISIBLE);
            likeStarThird.setVisibility(View.INVISIBLE);
        }


    }


    private void initializationChoosenRestaurants(String mCurrentPlaceId, String myRestaurantName, String myRestaurantVicinity) {

        UserHelper.getUserWhateverLocation(getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                QuerySnapshot document = task.getResult();
                assert document != null;

                String restaurantPlaceIdInFirebase = document.getDocuments().get(0).getString("restaurantPlaceId");

                //  Log.d("Debago", "ViewPlaceActivity initialization: restaurantqInFirebase " + restaurantPlaceIdInFirebase + " et mCurrentPLace " + mCurrentPlaceId);
                if (TextUtils.isEmpty(restaurantPlaceIdInFirebase) || !restaurantPlaceIdInFirebase.equals(mCurrentPlaceId)) {
                    //      Log.d("Debagoo", "ViewPlaceActivity initialization: couleur rouge");
                    isChoosenRestaurantImage.setColorFilter(Color.parseColor("#B70400"));//red color

                } else {
                    //    Log.d("Debagoo", "ViewPlaceActivity initialization: couleur verte");
                    isChoosenRestaurantImage.setColorFilter(Color.parseColor("#4CAF50"));//green color
                }

            }
        });

        isChoosenRestaurantImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                UserHelper.getUserWhateverLocation(getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        QuerySnapshot document = task.getResult();
                        assert document != null;

                        String restaurantPlaceIdInFirebase = document.getDocuments().get(0).getString("restaurantPlaceId");
                        clickOnButton(currentPlaceId, restaurantPlaceIdInFirebase, myRestaurantName, myRestaurantVicinity);


                    }
                });


            }
        });


    }

    private void clickOnButton(String mCurrentPlaceId, String restaurantPlaceIdInFirebase, String myRestaurantName, String myRestaurantVicinity) {


        if (TextUtils.isEmpty(restaurantPlaceIdInFirebase)) { //if there is no restaurant in my firebase
            //  Log.d("Debagoo", "ViewPlaceActivity clickbutton cas1 if there is no restaurant in my firebase: restaurantqInFirebase " + restaurantPlaceIdInFirebase);
            //We retrieve the new restaurant to increment customers's number
            RestaurantHelper.getRestaurant(mCurrentPlaceId,jobPlaceId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;

                    int currentNewRestaurantCustomersFirebase = document.getLong("restaurantCustomers").intValue();

                    //update new restaurant
                    int updateCustomersNewRestaurant = currentNewRestaurantCustomersFirebase + 1;
                    RestaurantHelper.updateRestaurantCustomers(updateCustomersNewRestaurant, mCurrentPlaceId,jobPlaceId);
                    UserHelper.updateRestaurantPlaceId(mCurrentPlaceId, Objects.requireNonNull(getCurrentUser()).getUid(),jobPlaceId);
                    UserHelper.updateRestaurantName(myRestaurantName, Objects.requireNonNull(getCurrentUser()).getUid(),jobPlaceId);
                    UserHelper.updateRestaurantVicinity(myRestaurantVicinity, Objects.requireNonNull(getCurrentUser()).getUid(),jobPlaceId);

                }
            });



            changeButtonColor("#4CAF50");//green color
            //  Log.d("Debagoo", "ViewPlaceActivity choose restaurant cas1 bis: je fais un nouveau choix");

        } else if (!restaurantPlaceIdInFirebase.equals(mCurrentPlaceId)) { //if there is one restaurant in my firebase but different of actual view place
            //  Log.d("Debagoo", "ViewPlaceActivity click button cas 2, if there is one restaurant in my firebase but different of actual view place restaurantInFirebase :" + restaurantPlaceIdInFirebase + " et currentplaceID " + mCurrentPlaceId);

            new AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.alert_dialog_view_activity, restaurantName))
                    .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //We retrieve the old restaurant to decrease customers's number
                            //  Log.d("Debagoo", "ViewPlaceActivity click button retrieve, restaurantInFirebase :" + restaurantPlaceIdInFirebase);
                            RestaurantHelper.getRestaurant(restaurantPlaceIdInFirebase,jobPlaceId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    assert document != null;

                                    int currentCustomersFirebase = document.getLong("restaurantCustomers").intValue();

                                    int updateCustomersOldRestaurant;
                                    //update Old restaurant
                                    if (currentCustomersFirebase != 0) {
                                        updateCustomersOldRestaurant = currentCustomersFirebase - 1;
                                        RestaurantHelper.updateRestaurantCustomers(updateCustomersOldRestaurant, restaurantPlaceIdInFirebase,jobPlaceId);
                                    }


                                }
                            });

                            //We retrieve the new restaurant customers's to increase customers's number
                            RestaurantHelper.getRestaurant(mCurrentPlaceId,jobPlaceId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    assert document != null;

                                    int currentNewRestaurantCustomersFirebase = document.getLong("restaurantCustomers").intValue();

                                    //update new restaurant
                                    int updateCustomersNewRestaurant = currentNewRestaurantCustomersFirebase + 1;
                                    RestaurantHelper.updateRestaurantCustomers(updateCustomersNewRestaurant, mCurrentPlaceId,jobPlaceId);
                                    UserHelper.updateRestaurantPlaceId(mCurrentPlaceId, Objects.requireNonNull(getCurrentUser()).getUid(),jobPlaceId);
                                    UserHelper.updateRestaurantName(myRestaurantName, Objects.requireNonNull(getCurrentUser()).getUid(),jobPlaceId);/**Faut aller chercher le nom du nouveau restaurant*/
                                    UserHelper.updateRestaurantVicinity(myRestaurantVicinity, Objects.requireNonNull(getCurrentUser()).getUid(),jobPlaceId);
                                }
                            });

                            changeButtonColor("#4CAF50");//green color


                        }
                    })
                    .setNegativeButton(R.string.popup_message_choice_no, null)
                    .show();


        } else { //if there is one restaurant in my firebase and he is the same than actual view place
            //   Log.d("Debagoo", "ViewPlaceActivity click button cas 3,if there is one restaurant in my firebase and he is the same than actual view place restaurantInFirebase :" + restaurantPlaceIdInFirebase + " et currentplaceID " + mCurrentPlaceId);
            new AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.alert_dialog_view_activity_no_choice_yet))
                    .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //We retrieve the old restaurant customers's number
                            RestaurantHelper.getRestaurant(restaurantPlaceIdInFirebase,jobPlaceId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    assert document != null;

                                    int currentCustomersFirebase = document.getLong("restaurantCustomers").intValue();
                                    int updateCustomersOldRestaurant;
                                    //update Old restaurant
                                    if (currentCustomersFirebase != 0) {
                                        updateCustomersOldRestaurant = currentCustomersFirebase - 1;
                                        RestaurantHelper.updateRestaurantCustomers(updateCustomersOldRestaurant, restaurantPlaceIdInFirebase,jobPlaceId);
                                        UserHelper.updateRestaurantPlaceId(null, Objects.requireNonNull(getCurrentUser()).getUid(),jobPlaceId);
                                        UserHelper.updateRestaurantName(null, Objects.requireNonNull(getCurrentUser()).getUid(),jobPlaceId);
                                        UserHelper.updateRestaurantVicinity(myRestaurantVicinity, Objects.requireNonNull(getCurrentUser()).getUid(),jobPlaceId);
                                    }


                                }
                            });



                            changeButtonColor("#B70400");//red color

                            //     Log.d("Debagoo", "ViewPlaceActivity choose restaurant cas3 bis: je désélectionne mon choix");
                        }
                    })
                    .setNegativeButton(R.string.popup_message_choice_no, null)
                    .show();


        }

    }

    private void changeButtonColor(String newColor) {

        isChoosenRestaurantImage.setColorFilter(Color.parseColor(newColor));

    }

    private void initializationLikedRestaurants(String currentPlaceId) {

        UserFavoriteRestaurantHelper.getCurrentRestaurantPlaceId(getCurrentUser().getUid(),currentPlaceId,jobPlaceId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    if (!task.getResult().getDocuments().isEmpty()) {


                        Boolean isRestaurantLiked = task.getResult().getDocuments().get(0).getBoolean("liked");
                        if(isRestaurantLiked){
                            changeLikeButtonColor("#FFFF00");//yellow
                        }
                        else{
                            changeLikeButtonColor("#FF5722");//orange

                        }


                    } else {

                        changeLikeButtonColor("#FF5722");//orange
                    }

                }


            }
        });


    }

    private void actionOnLikeButton(String currentPlaceId) {

        viewPlaceLikeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                RestaurantHelper.getRestaurant(currentPlaceId,jobPlaceId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;

                        int currentRestaurantLike = document.getLong("restaurantLike").intValue();
                        Log.d("Debago", "ViewPlaceActivity onLikeButton currentRestaurantLike: "+currentRestaurantLike);
                        UserFavoriteRestaurantHelper.getCurrentRestaurantPlaceId(getCurrentUser().getUid(),currentPlaceId,jobPlaceId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Debago", "View Place task successful");
                                    if (!task.getResult().getDocuments().isEmpty()) {

                                        int newRestaurantLike;



                                        Boolean isRestaurantLiked = task.getResult().getDocuments().get(0).getBoolean("liked");
                                        Log.d("Debago", "ViewPlaceActivity onLikeButton isRestaurantLiked "+isRestaurantLiked);
                                        //We want to decrement
                                        if(isRestaurantLiked){
                                            changeLikeButtonColor("#FF5722");//orange
                                            newRestaurantLike = currentRestaurantLike - 1;
                                            Log.d("Debago", "ViewPlaceActivity onLikeButton : we decrement");
                                            UserFavoriteRestaurantHelper.updateFavoriteRestaurantLiked(getCurrentUser().getUid(),currentPlaceId,false,jobPlaceId);

                                        }
                                        //We want to increment
                                        else{
                                            changeLikeButtonColor("#FFFF00");//yellow
                                            newRestaurantLike = currentRestaurantLike + 1;
                                            Log.d("Debago", "ViewPlaceActivity onLikeButton : we increment");
                                            UserFavoriteRestaurantHelper.updateFavoriteRestaurantLiked(getCurrentUser().getUid(),currentPlaceId,true,jobPlaceId);
                                        }

                                        RestaurantHelper.updateRestaurantLike(newRestaurantLike,currentPlaceId,jobPlaceId);

                                        actionOnLikeButton(currentPlaceId);

                                    }
                                    //First time we have select this restaurant to favorite, we create it in the database
                                    else {

                                        UserFavoriteRestaurantHelper.createUserFavoriteRestaurants(getCurrentUser().getUid(),currentPlaceId,true,jobPlaceId);
                                        Log.d("Debago", "ViewPlaceActivity onLikeButton : we create favorite restaurant in Database");
                                        changeLikeButtonColor("#FFFF00");//yellow
                                        RestaurantHelper.updateRestaurantLike(1,currentPlaceId,jobPlaceId);
                                        actionOnLikeButton(currentPlaceId);
                                    }

                                }
                                else{
                                    Log.d("Debago", "View Place task is not successful");
                                }


                            }
                        });


                    }
                });


            }
        });
    }

    private void actionOnButton(String phoneNumber, String website, String currentPlaceId) {

        viewPlaceCallImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Launch the call
                if(!TextUtils.isEmpty(phoneNumber)){

                    Intent appel = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumber));
                    startActivity(appel);

                }
                else{
                    Toast.makeText(getApplicationContext(), getString(R.string.no_phone_number), Toast.LENGTH_SHORT).show();
                }

            }
        });

        viewPlaceWebsiteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Creation of the Chrome Custom Tabs
                if(!TextUtils.isEmpty(website)){
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent intent = builder.build();
                    intent.launchUrl(context, Uri.parse(website));
                }
                else{
                    Toast.makeText(getApplicationContext(), getString(R.string.no_website), Toast.LENGTH_SHORT).show();
                }


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

        /*   Glide.with(this)
                .load(url.toString())
                .placeholder(R.drawable.ic_android_blue_24dp)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_error_red_24dp)
                .into(viewPlacePhoto);*/


    }

    private void updatePhotoViewPlace(String placeId) {

        // Initialize Places.
        Places.initialize(App.getInstance().getApplicationContext(), App.getResourses().getString(R.string.google_api_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(App.getInstance().getApplicationContext());

        // Specify the fields to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);

        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, fields).build();

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            // Get the photo metadata.
            PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

            // Get the attribution text.
            String attributions = photoMetadata.getAttributions();
            viewPlacePhoto.setContentDescription(attributions);
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
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e("debago", "Place not found: " + exception.getMessage());
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }


    // Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> makeToastAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {

                    case UPDATE_RESTAURANT_PLACE_ID:

                        Toast.makeText(getApplicationContext(), getString(R.string.restaurant_choosen), Toast.LENGTH_LONG).show();
                        break;

                }
            }
        };
    }

    private void displayAllWorkmatesJoining(String currentPlaceId) {

        this.mRecyclerWorkmatesAdapter = new WorkmatesAdapter(generateOptionsForAdapter(UserHelper.getAllWorkmatesJoining(currentPlaceId,jobPlaceId)), Glide.with(this), this, this);
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

       // textViewRecyclerViewEmpty.setVisibility(View.VISIBLE);
        textViewRecyclerViewEmpty.setVisibility(this.mRecyclerWorkmatesAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

}
