package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.model.placesearch.OpeningHours;
import com.inved.go4lunch.model.placesearch.Result;
import com.inved.go4lunch.utils.App;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_ADDRESS;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_NAME;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_OPENING_HOURS;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PHONE_NUMBER;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_PLACE_ID;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_WEBSITE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DETAIL_DATA;

public class RecyclerViewListViewRestaurant extends RecyclerView.Adapter<RecyclerViewListViewRestaurant.ViewHolder> {

    private Double myCurrentLat;
    private Double myCurrentLongi;
    private OpeningHours openingHours;

    PlacesClient placesClient;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {
                myCurrentLat = intent.getDoubleExtra(KEY_LATITUDE, 0.0);
                myCurrentLongi = intent.getDoubleExtra(KEY_LONGITUDE, 0.0);

            }

            if (PLACE_DETAIL_DATA.equals(intent.getAction())) {
                openingHours = intent.getParcelableExtra(PLACE_DATA_OPENING_HOURS);


            }
        }
    };

    @Nullable
    //  private List<PlaceLikelihood> mData;
    private List<Result> mData;
    private int mNumberResult;
    private int mPosition;

    private String placeId;
    private final RequestManager glide;
    Context mContext;
    PlaceDetailsData placeDetailsData = new PlaceDetailsData();

    public RecyclerViewListViewRestaurant(RequestManager glide) {

        this.glide = glide;
    }


    @NonNull
    @Override
    public RecyclerViewListViewRestaurant.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_listview_item, parent, false);
        LocalBroadcastManager.getInstance(parent.getContext()).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, new IntentFilter(PLACE_DETAIL_DATA));

        // Initialize Places.
        Places.initialize(parent.getContext(), App.getResourses().getString(R.string.google_api_key));

// Create a new Places client instance.
        placesClient = Places.createClient(parent.getContext());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewListViewRestaurant.ViewHolder holder, final int position) {
        //   Log.d("Debago", "RecyclerViewRestaurant AVANT position " + position+" et mposition "+mPosition);

        // Log.d("Debago", "RecyclerViewRestaurant position " + position+" et mposition "+mPosition);

        holder.mRestaurantName.setText(mData.get(position).getName());

        //   Log.d("Debago", "RecyclerViewRestaurant getitem numberResult for restaurant "+mData.getPlace());


        holder.mRestaurantAdress.setText(mData.get(position).getVicinity());
        placeId = mData.get(position).getPlaceId();

        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth=" + 400);
        url.append("&photoreference=");
        url.append(mData.get(position).getPhotos().get(0).getPhotoReference());
        url.append("&key=");
        url.append(App.getResourses().getString(R.string.google_api_key));

        glide.load(url.toString())
                .placeholder(R.drawable.ic_android_blue_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .into(holder.mRestaurantImage);

        //Distance entre deux points
        Double latitudeRestaurant = convertRad(mData.get(position).getGeometry().getLocation().getLat());
        Double longitudeRestaurant = convertRad(mData.get(position).getGeometry().getLocation().getLng());
        Double latCurrent = convertRad(myCurrentLat);
        Double longiCurrent = convertRad(myCurrentLongi);

        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.HALF_UP);

        Double distanceDouble = Math.acos(Math.sin(latCurrent) * Math.sin(latitudeRestaurant) + Math.cos(latCurrent) * Math.cos(latitudeRestaurant) * Math.cos(longitudeRestaurant - longiCurrent)) * 6371 * 1000;
        String distance = df.format(distanceDouble);
        holder.mDistance.setText(distance + " m");

       // placeDetailsData.setPlaceId(placeId);


        //mData.get(position).getOpeningHours().getWeekdayText().get(1).toString();
        LocalDateTime currentTime = LocalDateTime.now();

        int current_day = currentTime.getDayOfWeek().getValue();

        //If it open
        if (mData.get(position).getOpeningHours().getOpenNow()) {


                    // Define a Place ID.
           // String placeId = "INSERT_PLACE_ID_HERE";

            // Specify the fields to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.OPENING_HOURS);

            // Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                    .build();

// Add a listener to handle the response.
            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                int opening_hours_close = Objects.requireNonNull(place.getOpeningHours()).getPeriods().get(current_day).getClose().getTime().getHours();

              //  Log.d("Debago", "RecyclerViewRestaurant OPENINGHOURS 2 " + place.getOpeningHours().getPeriods().get(0));
                if(opening_hours_close==0){
                    holder.mRestaurantOpenInformation.setText("Open until midnight");
                }else{
                    holder.mRestaurantOpenInformation.setText("Open until "+opening_hours_close);
                }

                Log.i("Debago", "Place found close hours: " + opening_hours_close+" current day "+current_day);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e("Debago", "Place not found: " + exception.getMessage());
                }
            });


            /*holder.mRestaurantOpenInformation.setText("true");
            Log.d("Debago", "RecyclerViewRestaurant OPENINGHOURS 2 " + mData.get(position).getOpeningHours().getWeekdayText());
         /*   int openHours =mData.get(position).getOpeningHours().;
            if (openHours<12){
                holder.mRestaurantOpenInformation.setText("Open until "+openHours+" am");
            }
            else{
                holder.mRestaurantOpenInformation.setText("Open until "+openHours+" pm");
            }*/
        }
        //if it close
        else {
            // Define a Place ID.
            // String placeId = "INSERT_PLACE_ID_HERE";

            // Specify the fields to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.OPENING_HOURS);

            // Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                    .build();

// Add a listener to handle the response.
            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                int opening_hours_open = Objects.requireNonNull(place.getOpeningHours()).getPeriods().get(current_day).getOpen().getTime().getHours();

                //  Log.d("Debago", "RecyclerViewRestaurant OPENINGHOURS 2 " + place.getOpeningHours().getPeriods().get(0));
                holder.mRestaurantOpenInformation.setText("Opening to "+opening_hours_open);
              //  Log.i("Debago", "Place found opening hours: " + Objects.requireNonNull(place.getOpeningHours()).getPeriods().get(0).getClose().getTime().getHours());
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e("Debago", "Place not found: " + exception.getMessage());
                }
            });

        }


        holder.mConstraintLayoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                placeDetailsData.setPlaceId(placeId);

                // Launch View Place Activity
                Intent intent = new Intent(view.getContext(), ViewPlaceActivity.class);
                view.getContext().startActivity(intent);

            }
        });

    }         //Photo
      /*  if (mData.getPlace().getPhotoMetadatas().get(0).zza() != null) {

            StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
            url.append("?maxwidth=" + 400);
            url.append("&photoreference=");
            url.append(mData.getPlace().getPhotoMetadatas().get(0).zza());
            url.append("&key=");
            url.append(App.getResourses().getString(R.string.google_api_key));

            glide.load(url.toString())
                    .placeholder(R.drawable.ic_android_blue_24dp)
                    .error(R.drawable.ic_error_red_24dp)
                    .into(holder.mRestaurantImage);

        } else {
            glide.load("https://previews.123rf.com/images/glebstock/glebstock1405/glebstock140501325/29470353-silhouette-m%C3%A2le-personne-inconnue-notion.jpg")
                    .into(holder.mRestaurantImage);
        }*/


    private double convertRad(double latitudeConversion) {
        return (Math.PI * latitudeConversion) / 180;
    }


    @Override
    public int getItemCount() {
        if (mData == null) return 0;

        return mData.size();
    }

    public void setData(List<Result> data) {

        mData = data;

        //Fill the Recycler View
        notifyDataSetChanged();

    }

 /*   public void setData(List<PlaceLikelihood> data, int numberResult, int pos) {

        mData = data;
        mNumberResult = numberResult;
        mPosition=pos;

        //Fill the Recycler View
        notifyDataSetChanged();

    }*/

    public void setCurrentLocalisation(Double lat, Double longi) {

        myCurrentLat = lat;
        myCurrentLongi = longi;
        //Fill the Recycler View
        //  notifyDataSetChanged();

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mRestaurantName;

        TextView mRestaurantAdress;
        TextView mRestaurantOpenInformation;
        ImageView mRestaurantImage;
        ConstraintLayout mConstraintLayoutItem;
        TextView mDistance;

        ViewHolder(View itemView) {

            super(itemView);

            mRestaurantName = itemView.findViewById(R.id.fragment_listview_item_restaurant_name);

            mRestaurantAdress = itemView.findViewById(R.id.fragment_listview_item_restaurant_adress);
            mRestaurantOpenInformation = itemView.findViewById(R.id.fragment_listview_item_restaurant_open_information);
            mRestaurantImage = itemView.findViewById(R.id.fragment_listview_item_image);
            mConstraintLayoutItem = itemView.findViewById(R.id.fragment_listview_item);
            mDistance = itemView.findViewById(R.id.fragment_listview_item_distance);

        }

    }


}
