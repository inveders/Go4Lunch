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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.QuerySnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.model.placesearch.OpeningHours;
import com.inved.go4lunch.model.placesearch.Result;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_JOB_PLACE_ID_DATA;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DATA_OPENING_HOURS;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DETAIL_DATA;

public class RecyclerViewListViewRestaurant extends RecyclerView.Adapter<RecyclerViewListViewRestaurant.ViewHolder> {

    private Double myCurrentLat;
    private Double myCurrentLongi;
    private OpeningHours openingHours;
    private String jobPlaceId;

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

        jobPlaceId = ManageJobPlaceId.getJobPlaceId(mContext, KEY_JOB_PLACE_ID_DATA);
        Log.d("DEBAGO", "RecyclerViewListViewRestaurant oncreate jobplaceId: " + jobPlaceId);

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


        UserHelper.getAllWorkmatesJoining(placeId, jobPlaceId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                int numberWorkmatesInRestaurant = task.getResult().size();
                holder.mNumberRates.setText("(" + numberWorkmatesInRestaurant + ")");

            }
        });

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

        //OPENING HOURS
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
                if (opening_hours_close == 0) {
                    holder.mRestaurantOpenInformation.setText("Open until midnight");
                } else {
                    holder.mRestaurantOpenInformation.setText("Open until " + opening_hours_close);
                }

                Log.i("Debago", "Place found close hours: " + opening_hours_close + " current day " + current_day);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e("Debago", "Place not found: " + exception.getMessage());
                }
            });


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
                holder.mRestaurantOpenInformation.setText("Opening to " + opening_hours_open);
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

        //RATING

        double ratingValue = mData.get(mPosition).getRating();

        if (ratingValue > 0 && ratingValue < 1.665) {
            holder.mStarFirst.setVisibility(View.VISIBLE);
            holder.mStarSecond.setVisibility(View.INVISIBLE);
            holder.mStarThird.setVisibility(View.INVISIBLE);
        } else if (ratingValue >= 1.665 && ratingValue < 3.33) {
            holder.mStarFirst.setVisibility(View.VISIBLE);
            holder.mStarSecond.setVisibility(View.VISIBLE);
            holder.mStarThird.setVisibility(View.INVISIBLE);
        } else if (ratingValue >= 3.33 && ratingValue <= 5) {
            holder.mStarFirst.setVisibility(View.VISIBLE);
            holder.mStarSecond.setVisibility(View.VISIBLE);
            holder.mStarThird.setVisibility(View.VISIBLE);
        } else if (ratingValue == 0.0) {
            holder.mStarFirst.setVisibility(View.INVISIBLE);
            holder.mStarSecond.setVisibility(View.INVISIBLE);
            holder.mStarThird.setVisibility(View.INVISIBLE);
        }

    }


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
        TextView mNumberRates;
        TextView mRestaurantAdress;
        TextView mRestaurantOpenInformation;
        ImageView mRestaurantImage;
        ImageView mStarFirst;
        ImageView mStarSecond;
        ImageView mStarThird;
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
            mNumberRates = itemView.findViewById(R.id.fragment_listview_item_restaurant_number_rates);
            mStarFirst = itemView.findViewById(R.id.fragment_listview_item_like_start_first);
            mStarSecond = itemView.findViewById(R.id.fragment_listview_item_like_start_second);
            mStarThird = itemView.findViewById(R.id.fragment_listview_item_like_start_third);

        }

    }


}
