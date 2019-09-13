package com.inved.go4lunch.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.model.placesearch.Result;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageJobPlaceId;
import com.inved.go4lunch.utils.UnitConversion;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.utils.ManageJobPlaceId.KEY_JOB_PLACE_ID_DATA;

public class RecyclerViewListViewRestaurant extends RecyclerView.Adapter<RecyclerViewListViewRestaurant.ViewHolder> implements Filterable {

    private Double myCurrentLat;
    private Double myCurrentLongi;
    private String jobPlaceId;

    private PlacesClient placesClient;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {
                myCurrentLat = intent.getDoubleExtra(KEY_LATITUDE, 0.0);
                myCurrentLongi = intent.getDoubleExtra(KEY_LONGITUDE, 0.0);

            }


        }
    };

    @Nullable
    private List<Result> mData;
    private List<Result> mDataFiltered;

    private UnitConversion unitConversion = new UnitConversion();
    private String placeId;
    private final RequestManager glide;
    private PlaceDetailsData placeDetailsData = new PlaceDetailsData();

    public RecyclerViewListViewRestaurant(RequestManager glide) {

        this.glide = glide;
    }


    @NonNull
    @Override
    public RecyclerViewListViewRestaurant.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_listview_item, parent, false);
        LocalBroadcastManager.getInstance(parent.getContext()).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));

        jobPlaceId = ManageJobPlaceId.getJobPlaceId(parent.getContext(), KEY_JOB_PLACE_ID_DATA);


        // Initialize Places.
        Places.initialize(parent.getContext(), App.getResourses().getString(R.string.google_api_key));

        // Create a new Places client instance.
        placesClient = Places.createClient(parent.getContext());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewListViewRestaurant.ViewHolder holder, final int position) {

        //Query wich servent to filter
        final Result result = mDataFiltered.get(position);
        holder.mRestaurantName.setText(result.getName());

        //  Log.d("Debago", "RecyclerViewRestaurant placeId "+mData.get(position).getPlaceId());

        assert mData != null;
        holder.mRestaurantAdress.setText(mData.get(position).getVicinity());
        placeId = mData.get(position).getPlaceId();

            UserHelper.getAllWorkmatesJoining(placeId, jobPlaceId).get().addOnCompleteListener(task -> {

                int numberWorkmatesInRestaurant = Objects.requireNonNull(task.getResult()).size();
                holder.mNumberRates.setText(App.getResourses().getString(R.string.workmates_in_restaurant,numberWorkmatesInRestaurant));

            });

        String url = "https://maps.googleapis.com/maps/api/place/photo" + "?maxwidth=" + 400 +
                "&photoreference=" +
                mData.get(position).getPhotos().get(0).getPhotoReference() +
                "&key=" +
                App.getResourses().getString(R.string.google_api_key);
        glide.load(url)
                .placeholder(R.drawable.ic_android_blue_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .into(holder.mRestaurantImage);

        //Distance entre deux points
        double latitudeRestaurant = unitConversion.convertRad(mData.get(position).getGeometry().getLocation().getLat());
        Double longitudeRestaurant = unitConversion.convertRad(mData.get(position).getGeometry().getLocation().getLng());
        double latCurrent = unitConversion.convertRad(myCurrentLat);
        Double longiCurrent = unitConversion.convertRad(myCurrentLongi);

        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.HALF_UP);

        Double distanceDouble = Math.acos(Math.sin(latCurrent) * Math.sin(latitudeRestaurant) + Math.cos(latCurrent) * Math.cos(latitudeRestaurant) * Math.cos(longitudeRestaurant - longiCurrent)) * 6371 * 1000;
        String distance = df.format(distanceDouble);
        holder.mDistance.setText(App.getResourses().getString(R.string.distance_text,distance));

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
                    holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.opening_hours_midnight));
                } else {
                    holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.open_hours_until,opening_hours_close));

                }

                //   Log.i("Debago", "Place found close hours: " + opening_hours_close + " current day " + current_day);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {

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
                int opening_hours_open = Objects.requireNonNull(Objects.requireNonNull(place.getOpeningHours()).getPeriods().get(current_day).getOpen()).getTime().getHours();

                //  Log.d("Debago", "RecyclerViewRestaurant OPENINGHOURS 2 " + place.getOpeningHours().getPeriods().get(0));
                holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.open_hours_text,opening_hours_open));
                //  Log.i("Debago", "Place found opening hours: " + Objects.requireNonNull(place.getOpeningHours()).getPeriods().get(0).getClose().getTime().getHours());
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {

                    // Handle error with given status code.
                    Log.e("Debago", "Place not found: " + exception.getMessage());
                }
            });

        }


        holder.mConstraintLayoutItem.setOnClickListener(view -> {

            placeId = mData.get(position).getPlaceId();
            Log.e("Debago", "RecyclerViewList placeId: " + placeId);
            placeDetailsData.setPlaceId(placeId);

            // Launch View Place Activity
            Intent intent = new Intent(view.getContext(), ViewPlaceActivity.class);
            view.getContext().startActivity(intent);

        });

        //RATING

        double ratingValue = mData.get(position).getRating();

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
        } else if (ratingValue <= 0 || ratingValue > 5) {
            holder.mStarFirst.setVisibility(View.INVISIBLE);
            holder.mStarSecond.setVisibility(View.INVISIBLE);
            holder.mStarThird.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public int getItemCount() {

        if (mData != null) {
            return mDataFiltered.size();
        } else {
            return 0;
        }

    }


    public void setData(List<Result> mData) {
        this.mData = mData;
        this.mDataFiltered = mData;
        //Fill the Recycler View
        notifyDataSetChanged();

    }


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

            mRestaurantAdress = itemView.findViewById(R.id.fragment_listview_item_restaurant_address);
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


    @Override
    public Filter getFilter() {
        //   Log.d("Debago", "RecyclerViewRestaurant GETfILTER in getFilter");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                Log.d("Debago", "RecyclerViewRestaurant GETfILTER : charString " + charString);
                if (charString.isEmpty()) {
                    mDataFiltered = mData;
                    Log.d("Debago", "RecyclerViewRestaurant GETfILTER : charString is empty " + mDataFiltered);
                } else {
                    //   Log.d("Debago", "RecyclerViewRestaurant GETfILTER : charString is not empty result "+   mData.get(0).getName()+" contains charString "+ charString);
                    List<Result> filteredList = new ArrayList<>();
                    Log.d("Debago", "RecyclerViewRestaurant GETfILTER : charString is not empty filteredList " + mData);

                    /**ICI MON mData est null ce qui est normal vu que je ne fais pas de set Data avant d'entrer ici. Comment
                     * Faire pour transmettre des données depuis un searchview placé dans l'activity et l'afficher dans le fragment?*/
                    for (Result result : mData) {
                        Log.d("Debago", "RecyclerViewRestaurant GETfILTER : charString is not empty result " + result.getName() + " contains charString " + charString);
                        if (result.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(result);
                        }
                    }

                    mDataFiltered = filteredList;
                    Log.d("Debago", "RecyclerViewRestaurant GETfILTER : charString is not empty mDataFilteredList " + mDataFiltered);
                }

                FilterResults filterResults = new FilterResults();
                Log.d("Debago", "RecyclerViewRestaurant GETfILTER : filteredResult " + mDataFiltered);
                filterResults.values = mDataFiltered;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataFiltered = (List<Result>) filterResults.values;
                Log.d("Debago", "RecyclerViewRestaurant GETfILTER : publishResukt mDatafilteredlist " + mDataFiltered);

                notifyDataSetChanged();
            }
        };
    }

}
