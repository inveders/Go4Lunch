package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.RequestManager;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.APIClientGoogleSearch;
import com.inved.go4lunch.api.GooglePlaceDetailsCalls;
import com.inved.go4lunch.model.placedetails.PlaceDetails;

import butterknife.BindView;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.utils.PlaceDetailsData.PLACE_DETAIL_DATA;
import static com.inved.go4lunch.utils.PlaceDetailsData.PLACE_DETAIL_DATA_PHONE_NUMBER;

public class ViewPlaceActivity extends AppCompatActivity {

    @BindView(R.id.activity_view_place_photo)
    ImageView viewPlacePhoto;
  //  @BindView(R.id.activity_view_place_name)
    TextView viewPlaceName;
    @BindView(R.id.activity_view_place_restaurant_type)
    TextView viewPlaceRestaurantType;
    @BindView(R.id.activity_view_place_adress)
    TextView viewPlaceAdress;
    @BindView(R.id.activity_view_place_call_image)
    ImageView viewPlaceCallImage;
    @BindView(R.id.activity_view_place_like_image)
    ImageView viewPlaceLikeImage;
    @BindView(R.id.activity_view_place_website_image)
    ImageView viewPlaceWebsiteImage;

    private RecyclerViewListViewRestaurant mRecyclerListViewAdapter;

    //FOR DATA
    private RequestManager glide;
    APIClientGoogleSearch mService;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PLACE_DETAIL_DATA.equals(intent.getAction())) {

                String phoneNumber = intent.getStringExtra(PLACE_DETAIL_DATA_PHONE_NUMBER);
                Log.d("Debago", "ViewPlaceActivity onReceive phoneNumber: "+phoneNumber);
                updateViewPlaceActivity(phoneNumber);


            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);

        viewPlaceName = findViewById(R.id.activity_view_place_name);

       /* BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (PLACE_DETAIL_DATA.equals(intent.getAction())) {

                    String phoneNumber = intent.getStringExtra(PLACE_DETAIL_DATA_PHONE_NUMBER);
                    Log.d("Debago", "ViewPlaceActivity onReceive phoneNumber: "+phoneNumber);
                    updateViewPlaceActivity(phoneNumber);


                }
            }
        };*/

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_DETAIL_DATA));

    }

  /*  private String getPhotoOfPlace(String photo_reference,int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth="+maxWidth);
        url.append("&photoreference"+photo_reference);
        url.append("&key="+getResources().getString(R.string.google_api_key));
        return url.toString();
    }*/

    public void updateViewPlaceActivity(String numberPhone) {
        Log.d("Debago", "ViewPlaceActivity updateViewPlaceActivity : " + numberPhone);

        if (numberPhone != null) {
                viewPlaceName.setText(numberPhone);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }




}
