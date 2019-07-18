package com.inved.go4lunch.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.RequestManager;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.APIClientGoogleSearch;
import com.inved.go4lunch.api.GooglePlaceDetailsCalls;
import com.inved.go4lunch.model.placedetails.PlaceDetails;

import butterknife.BindView;

public class ViewPlaceActivity extends AppCompatActivity implements GooglePlaceDetailsCalls.CallbacksDetail {

    @BindView(R.id.activity_view_place_photo)
    ImageView viewPlacePhoto;
    @BindView(R.id.activity_view_place_name)
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);



    }

  /*  private String getPhotoOfPlace(String photo_reference,int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth="+maxWidth);
        url.append("&photoreference"+photo_reference);
        url.append("&key="+getResources().getString(R.string.google_api_key));
        return url.toString();
    }*/

    void executeHttpRequestPlaceDetailsWithRetrofit(String placeid){

        String key = "AIzaSyCYRQL4UOKKcszTAi6OeN8xCvZ7CuFtp8A";// getText(R.string.google_maps_key).toString();
        String fields="photo,type,formatted_phone_number,opening_hours";
        Log.d("Debago","ViewPlaceActivity onexecuteretrofit : "+placeid);
        GooglePlaceDetailsCalls.fetchPlaceDetail(this,placeid,key,fields);

    }

    @Override
    public void onResponse(@Nullable PlaceDetails users) {

        assert users != null;
        if(users.getResult()!=null) {
            Log.d("Debago", "ViewPlaceActivity onResponse hum : " + users.getResult().getFormattedPhoneNumber());
            viewPlaceName.setText(users.getResult().getFormattedPhoneNumber());
          /*  viewPlaceRestaurantType.setText(users.getResult().getTypes().toString());
            viewPlaceAdress.setText(users.getResult().getFormattedAddress());
         /*   if (APIClientGoogleSearch.currentResult.getPhotos() != null && APIClientGoogleSearch.currentResult.getPhotos().size() > 0) {
                glide.load(getPhotoOfPlace(APIClientGoogleSearch.currentResult.getPhotos().get(0).getPhotoReference(), 1000))
                        .placeholder(R.drawable.ic_android_blue_24dp)
                        .error(R.drawable.ic_error_red_24dp)
                        .into(viewPlacePhoto);
            }*/

/*            viewPlaceCallImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /**ICI LANCER UN APPEL TELEPHONIQUE DEPUIS L'APPAREIL*/
                }
       //    });
       // }
        Log.d("Debago", "ViewPlaceActivity onResponse : users est null");
    }

    @Override
    public void onFailure() {

    }
}
