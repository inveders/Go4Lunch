package com.inved.go4lunch.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.inved.go4lunch.R;
import com.inved.go4lunch.controller.activity.ViewPlaceActivity;
import com.inved.go4lunch.domain.UnitConversion;
import com.inved.go4lunch.firebase.Restaurant;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageAppMode;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.MAP_API_KEY;
import static com.inved.go4lunch.controller.fragment.MapFragment.RESTAURANT_PLACE_ID;

public class RecyclerViewListViewRestaurant extends FirestorePagingAdapter<Restaurant, RecyclerViewListViewRestaurant.ViewHolder> {

    private String placeId;
    private Listener callback;
    private PlacesClient placesClient;
    private UnitConversion unitConversion = new UnitConversion();


    public interface Listener {

        void onSwipeBoolean(boolean b);
    }



    public RecyclerViewListViewRestaurant(@NonNull FirestorePagingOptions<Restaurant> options, Listener callback) {

        super(options);
        this.callback = callback;
        // Initialize Places.
        Places.initialize(App.getInstance().getApplicationContext(), MAP_API_KEY);
        // Create a new Places client instance.
        placesClient = Places.createClient(App.getInstance().getApplicationContext());

    }

    @NonNull
    @Override
    public RecyclerViewListViewRestaurant.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_listview_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewListViewRestaurant.ViewHolder holder, final int position, @NonNull Restaurant restaurant) {

        placeId = restaurant.getRestaurantPlaceId();

        //PHOTO

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
                attributions = App.getResourses().getString(R.string.image_content_description);
            }
            holder.mRestaurantImage.setContentDescription(attributions);
            // Create a FetchPhotoRequest.

            if (photoMetadata != null) {
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500) // Optional.
                        .setMaxHeight(300) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    holder.mRestaurantImage.setImageBitmap(bitmap);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {

                        // Handle error with given status code.
                        Log.e("debago", "Place not found: " + exception.getMessage());
                    }
                });
            } else {
                Bitmap bitmap = BitmapFactory.decodeResource(App.getResourses(), R.drawable.background_connexion_activity_flou_ok);
                holder.mRestaurantImage.setImageBitmap(bitmap);
            }
        });

        String name = restaurant.getRestaurantName();
        holder.mRestaurantName.setText(name);

        if(restaurant.getDistance()>=1000){
            double distanceKm=unitConversion.convertMeterToKm(restaurant.getDistance());
            holder.mDistance.setText(App.getResourses().getString(R.string.distance_text_km, String.valueOf(distanceKm)));

        }else{
            holder.mDistance.setText(App.getResourses().getString(R.string.distance_text_meter, String.valueOf(restaurant.getDistance())));

        }

        holder.mRestaurantAdress.setText(restaurant.getRestaurantAddress());

        holder.mNumberRates.setText(String.valueOf(restaurant.getRestaurantCustomers()));
        int numberWorkmatesInRestaurant = restaurant.getRestaurantCustomers();
        holder.mNumberRates.setText(App.getResourses().getString(R.string.workmates_in_restaurant, numberWorkmatesInRestaurant));

        //OPENING HOURS
        int opening_open_hours = restaurant.getOpenHours();
        int opening_open_minutes = restaurant.getOpenMinutes();
        int opening_close_hours = restaurant.getCloseHours();
        int opening_close_minutes = restaurant.getCloseMinutes();

        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (opening_open_hours == 0 && opening_close_hours == 0) {
            holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.no_opened_hours));
        } else if (hourOfDay < opening_open_hours) {
            if (opening_close_hours != -1) {
                if (opening_open_minutes == 0) {
                    holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.open_hours_text_no_minutes, opening_open_hours));
                } else {
                    holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.open_hours_text, opening_open_hours, opening_open_minutes));
                }

            } else {
                holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.no_opened_hours));

            }
        } else {
            if (opening_close_hours != -1) {
                if (opening_close_hours == 0) {
                    holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.opening_hours_midnight));
                }
                else if(opening_close_hours<hourOfDay){
                    holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.closed_restaurant));
                }
                else {
                    if (opening_close_minutes == 0) {
                        holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.open_hours_until_no_minutes, opening_close_hours));
                    } else {
                        holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.open_hours_until, opening_close_hours, opening_close_minutes));
                    }
                }
            } else {
                holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.no_opened_hours));
            }

        }


        //CLICK ON A RESTAURANT
        holder.mConstraintLayoutItem.setOnClickListener(view -> {

            if (restaurant.getRestaurantPlaceId() != null) {
                placeId = restaurant.getRestaurantPlaceId();

                // Launch View Place Activity
                Intent intent = new Intent(view.getContext(), ViewPlaceActivity.class);
                intent.putExtra(RESTAURANT_PLACE_ID, placeId);
                view.getContext().startActivity(intent);
            } else {
                Toast.makeText(view.getContext(), App.getResourses().getString(R.string.no_restaurant_found), Toast.LENGTH_SHORT).show();
            }

        });

        //RATING
        int ratingValue = restaurant.getRatingApp();

        switch (ratingValue) {
            case 1:
                holder.mStarFirst.setVisibility(View.INVISIBLE);
                holder.mStarSecond.setVisibility(View.INVISIBLE);
                holder.mStarThird.setVisibility(View.VISIBLE);
                break;
            case 2:
                holder.mStarFirst.setVisibility(View.INVISIBLE);
                holder.mStarSecond.setVisibility(View.VISIBLE);
                holder.mStarThird.setVisibility(View.VISIBLE);
                break;
            case 3:
                holder.mStarFirst.setVisibility(View.VISIBLE);
                holder.mStarSecond.setVisibility(View.VISIBLE);
                holder.mStarThird.setVisibility(View.VISIBLE);
                break;
            default:
                holder.mStarFirst.setVisibility(View.INVISIBLE);
                holder.mStarSecond.setVisibility(View.INVISIBLE);
                holder.mStarThird.setVisibility(View.INVISIBLE);
        }


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
        ImageView mNumberRatesIcon;


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
            mNumberRatesIcon = itemView.findViewById(R.id.fragment_listview_item_person_icon_rate);


            if (ManageAppMode.getAppMode(App.getInstance().getApplicationContext()).equals(App.getResourses().getString(R.string.app_mode_normal))) {
                mNumberRates.setVisibility(View.INVISIBLE);
                mNumberRatesIcon.setVisibility(View.INVISIBLE);
            }

        }

    }

    @Override
    protected void onError(@NonNull Exception e) {
        super.onError(e);
        if(e.getMessage()!=null){
            Log.e("debago", e.getMessage());
        }

       // retry();

    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state) {
            case LOADING_INITIAL:
            case LOADING_MORE:
                callback.onSwipeBoolean(true);

                break;

            case LOADED:

            case FINISHED:
                callback.onSwipeBoolean(false);
                break;

            case ERROR:
                Toast.makeText(
                        App.getInstance().getApplicationContext(),
                        "Error Occurred!",
                        Toast.LENGTH_SHORT
                ).show();
                retry();
                callback.onSwipeBoolean(true);
                break;
        }
    }




}
