package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.APIClientGoogleSearch;

import butterknife.BindView;

import static com.inved.go4lunch.controller.MapFragment.POSITION_ARRAY_LIST;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_NAME;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHONE_NUMBER;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHOTO_REFERENCE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_VICINITY;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DETAIL_DATA;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_SEARCH_DATA;

public class ViewPlaceActivity extends AppCompatActivity {


    ImageView viewPlacePhoto;
    TextView viewPlaceName;
    TextView viewPlaceAdress;
    @BindView(R.id.activity_view_place_restaurant_type)
    TextView viewPlaceRestaurantType;


    @BindView(R.id.activity_view_place_call_image)
    ImageView viewPlaceCallImage;
    @BindView(R.id.activity_view_place_like_image)
    ImageView viewPlaceLikeImage;
    @BindView(R.id.activity_view_place_website_image)
    ImageView viewPlaceWebsiteImage;


    private String photoreference;
    private String restaurantName;
    private String vicinity;
    private String phoneNumber;

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

            }

            if (PLACE_SEARCH_DATA.equals(intent.getAction())) {


            }

            updateViewPlaceActivity(restaurantName, vicinity, phoneNumber, photoreference);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);

        viewPlaceName = findViewById(R.id.activity_view_place_name);
        viewPlaceAdress = findViewById(R.id.activity_view_place_adress);
        viewPlacePhoto = findViewById(R.id.activity_view_place_photo);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_DETAIL_DATA));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_SEARCH_DATA));


    }


    public void updateViewPlaceActivity(String restaurantName,
                                        String vicinity,
                                        String phoneNumber,
                                        String photoreference) {

        Log.d("Debago", "ViewPlaceActivity updtaeViewPlaceActivity positionInArrayList est: " + restaurantName);
        Log.d("Debago", "ViewPlaceActivity updtaeViewPlaceActivity photoreference est: " + phoneNumber);

        //Photo
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth=" + 1000);
        url.append("&photoreference=");
        url.append(photoreference);
        url.append("&key=");
        url.append(getResources().getString(R.string.google_api_key));

        Log.d("Debago", "ViewPlaceActivity updtaeViewPlaceActivity url: " + url.toString());

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


}
