package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.APIClientGoogleSearch;
import com.inved.go4lunch.firebase.RestaurantHelper;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.base.BaseActivity;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.utils.App;

import java.util.Objects;

import butterknife.BindView;

import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_NAME;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHONE_NUMBER;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHOTO_REFERENCE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PLACE_ID;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_VICINITY;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DETAIL_DATA;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_SEARCH_DATA;

public class ViewPlaceActivity extends BaseActivity {


    ImageView viewPlacePhoto;
    TextView viewPlaceName;
    TextView viewPlaceAdress;
    @BindView(R.id.activity_view_place_restaurant_type)
    TextView viewPlaceRestaurantType;

    CollectionReference restaurants = RestaurantHelper.getRestaurantsCollection();

    private static final int UPDATE_RESTAURANT_PLACE_ID = 40;

    @BindView(R.id.activity_view_place_call_image)
    ImageView viewPlaceCallImage;
    @BindView(R.id.activity_view_place_like_image)
    ImageView viewPlaceLikeImage;
    @BindView(R.id.activity_view_place_website_image)
    ImageView viewPlaceWebsiteImage;
    @BindView(R.id.activity_view_place_button_choose_restaurant)
    ImageView isChoosenRestaurantImage;


    private String photoreference;
    private String restaurantName;
    private String vicinity;
    private String phoneNumber;
    private String cuurentPlaceId;
    private String restaurantPlaceIdInFirebase;

    //FOR DATA

    APIClientGoogleSearch mService;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PLACE_DETAIL_DATA.equals(intent.getAction())) {
                phoneNumber = intent.getStringExtra(PLACE_DATA_PHONE_NUMBER);
                photoreference = intent.getStringExtra(PLACE_DATA_PHOTO_REFERENCE);
                restaurantName = intent.getStringExtra(PLACE_DATA_NAME);
                vicinity = intent.getStringExtra(PLACE_DATA_VICINITY);
                cuurentPlaceId = intent.getStringExtra(PLACE_DATA_PLACE_ID);

            }

            if (PLACE_SEARCH_DATA.equals(intent.getAction())) {


            }
            initializationChoosenRestaurants(cuurentPlaceId);
            updateViewPlaceActivity(restaurantName, vicinity, phoneNumber, photoreference);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.activity_view_place);

        viewPlaceName = findViewById(R.id.activity_view_place_name);
        viewPlaceAdress = findViewById(R.id.activity_view_place_adress);
        viewPlacePhoto = findViewById(R.id.activity_view_place_photo);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_DETAIL_DATA));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_SEARCH_DATA));


    }

    private void initializationChoosenRestaurants(String mCurrentPlaceId) {

        // 7 - Get data from Firestore to initialize page
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                assert currentUser != null;
                Log.d("Debago", "ViewPlaceActivity choose restaurant: restaurantqInFirebase "+currentUser.getRestaurantPlaceId()+ " et currentIdpage "+mCurrentPlaceId);
                restaurantPlaceIdInFirebase = currentUser.getRestaurantPlaceId();
                if (TextUtils.isEmpty(restaurantPlaceIdInFirebase) || !restaurantPlaceIdInFirebase.equals(mCurrentPlaceId)) {
                    isChoosenRestaurantImage.setColorFilter(Color.parseColor("#92FFA3"));//green color
                } else {
                    isChoosenRestaurantImage.setColorFilter(Color.parseColor("#B70400"));//red color
                }


            }
        });

        isChoosenRestaurantImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(restaurantPlaceIdInFirebase)) { //if there is no restaurant in my firebase

                    //We retrieve the new restaurant to increment customers's number
                    RestaurantHelper.getRestaurant(mCurrentPlaceId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;

                            int currentNewRestaurantCustomersFirebase = document.getLong("restaurantCustomers").intValue();

                            //update new restaurant
                            int updateCustomersNewRestaurant = currentNewRestaurantCustomersFirebase + 1;
                            RestaurantHelper.updateRestaurantCustomers(updateCustomersNewRestaurant,mCurrentPlaceId);
                            UserHelper.updateRestaurantPlaceId(mCurrentPlaceId, Objects.requireNonNull(getCurrentUser()).getUid());

                        }
                    });

                    isChoosenRestaurantImage.setColorFilter(Color.parseColor("#92FFA3"));//red color
                    Log.d("Debago", "ViewPlaceActivity choose restaurant: je fais un nouveau choix");

                } else if (!restaurantPlaceIdInFirebase.equals(mCurrentPlaceId)) { //if there is one restaurant in my firebase but different of actual view place


                    new AlertDialog.Builder(getApplicationContext())
                            .setMessage(getApplicationContext().getString(R.string.alert_dialog_view_activity, restaurantName))
                            .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //We retrieve the old restaurant to decrease customers's number
                                    RestaurantHelper.getRestaurant(restaurantPlaceIdInFirebase).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();
                                            assert document != null;

                                            int currentCustomersFirebase = document.getLong("restaurantCustomers").intValue();

                                            int updateCustomersOldRestaurant;
                                            //update Old restaurant
                                            if (currentCustomersFirebase != 0) {
                                                updateCustomersOldRestaurant = currentCustomersFirebase - 1;
                                                RestaurantHelper.updateRestaurantCustomers(updateCustomersOldRestaurant,restaurantPlaceIdInFirebase);
                                            }



                                        }
                                    });

                                    //We retrieve the new restaurant customers's to increase customers's number
                                    RestaurantHelper.getRestaurant(mCurrentPlaceId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();
                                            assert document != null;

                                            int currentNewRestaurantCustomersFirebase = document.getLong("restaurantCustomers").intValue();

                                            //update new restaurant
                                            int updateCustomersNewRestaurant = currentNewRestaurantCustomersFirebase + 1;
                                            RestaurantHelper.updateRestaurantCustomers(updateCustomersNewRestaurant,mCurrentPlaceId);
                                            UserHelper.updateRestaurantPlaceId(mCurrentPlaceId, Objects.requireNonNull(getCurrentUser()).getUid());

                                        }
                                    });

                                    isChoosenRestaurantImage.setColorFilter(Color.parseColor("#B70400"));//green color
                                    Log.d("Debago", "ViewPlaceActivity choose restaurant: je change de choix");

                                }
                            })
                            .setNegativeButton(R.string.popup_message_choice_no, null)
                            .show();





                }

                else { //if there is one restaurant in my firebase and he is the same than actual view place

                    new AlertDialog.Builder(getApplicationContext())
                            .setMessage(getApplicationContext().getString(R.string.alert_dialog_view_activity))
                            .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //We retrieve the old restaurant customers's number
                                    RestaurantHelper.getRestaurant(restaurantPlaceIdInFirebase).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();
                                            assert document != null;

                                            int currentCustomersFirebase = document.getLong("restaurantCustomers").intValue();
                                            int updateCustomersOldRestaurant;
                                            //update Old restaurant
                                            if (currentCustomersFirebase != 0) {
                                                updateCustomersOldRestaurant = currentCustomersFirebase - 1;
                                                RestaurantHelper.updateRestaurantCustomers(updateCustomersOldRestaurant,restaurantPlaceIdInFirebase);
                                                UserHelper.updateRestaurantPlaceId(null, Objects.requireNonNull(getCurrentUser()).getUid());
                                            }



                                        }
                                    });

                                    isChoosenRestaurantImage.setColorFilter(Color.parseColor("#B70400"));//green color
                                    Log.d("Debago", "ViewPlaceActivity choose restaurant: je désélectionne mon choix");
                                }
                            })
                            .setNegativeButton(R.string.popup_message_choice_no, null)
                            .show();




                }


            }
        });

    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_view_place;
    }


    public void updateViewPlaceActivity(String restaurantName,
                                        String vicinity,
                                        String phoneNumber,
                                        String photoreference) {


        //Photo
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth=" + 1000);
        url.append("&photoreference=");
        url.append(photoreference);
        url.append("&key=");
        url.append(getResources().getString(R.string.google_api_key));

        Glide.with(this)
                .load(url.toString())
                .placeholder(R.drawable.ic_android_blue_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .into(viewPlacePhoto);

        //Textes
        viewPlaceName.setText(restaurantName);
        viewPlaceAdress.setText(vicinity);

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

}
