package com.inved.go4lunch.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.controller.activity.ViewPlaceActivity;
import com.inved.go4lunch.firebase.RestaurantHelper;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.controller.fragment.MapFragment.RESTAURANT_PLACE_ID;
import static com.inved.go4lunch.utils.ManageJobPlaceId.KEY_JOB_PLACE_ID_DATA;

public class RecyclerViewListViewRestaurant extends RecyclerView.Adapter<RecyclerViewListViewRestaurant.ViewHolder> implements Filterable {


    private String jobPlaceId;

    private PlacesClient placesClient;



    @Nullable
    private List<Result> mData;
    private List<Result> mDataFiltered;


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


        jobPlaceId = ManageJobPlaceId.getJobPlaceId(parent.getContext(), KEY_JOB_PLACE_ID_DATA);


        // Initialize Places.
        Places.initialize(parent.getContext(), App.getResourses().getString(R.string.google_api_key));

        // Create a new Places client instance.
        placesClient = Places.createClient(parent.getContext());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewListViewRestaurant.ViewHolder holder, final int position) {

        //Query wich serve filter
     /*   Result result = mDataFiltered.get(position);

        holder.mRestaurantName.setText(result.getName());*/
        if(mData!=null){
            String name = mData.get(position).getName();
            holder.mRestaurantName.setText(name);
            Log.d("Debago","restaurantName in bindview holder is "+name);
            placeId = mData.get(position).getPlaceId();




        }

      //  holder.mDistance.setText(App.getResourses().getString(R.string.distance_text, distance));

        if (mData != null) {
            holder.mRestaurantAdress.setText(mData.get(position).getVicinity());

        }

        //NUMBER WORKMATES IN RESTAURANT
        UserHelper.getAllWorkmatesJoining(placeId, jobPlaceId).get().addOnCompleteListener(task -> {

            if (task.getResult() != null) {
                int numberWorkmatesInRestaurant = task.getResult().size();
                holder.mNumberRates.setText(App.getResourses().getString(R.string.workmates_in_restaurant, numberWorkmatesInRestaurant));
            }


        });

        //PHOTO
        if(mData!=null){
            if(mData.get(position).getPhotos().get(0).getPhotoReference()!=null){
                String url = "https://maps.googleapis.com/maps/api/place/photo" + "?maxwidth=" + 400 +
                        "&photoreference=" +
                        mData.get(position).getPhotos().get(0).getPhotoReference() +
                        "&key=" +
                        App.getResourses().getString(R.string.google_api_key);
                glide.load(url)
                        .placeholder(R.drawable.ic_android_blue_24dp)
                        .error(R.drawable.ic_error_red_24dp)
                        .into(holder.mRestaurantImage);
            }else{
                String url = "https://images.unsplash.com/photo-1498837167922-ddd27525d352?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80";
                glide.load(url)
                        .placeholder(R.drawable.ic_android_blue_24dp)
                        .error(R.drawable.ic_error_red_24dp)
                        .into(holder.mRestaurantImage);
            }
        }









        //openinh hours

        //  Log.d("Debago", "RecyclerViewRestaurant OPENINGHOURS 2 " + place.getOpeningHours().getPeriods().get(0));
     /*   holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.open_hours_text, opening_hours_open));

        if(opening_hours_close<15){
            RestaurantHelper.updateRestaurantOpenForLunch(true,placeId,jobPlaceId);
        }

        //  Log.d("Debago", "RecyclerViewRestaurant OPENINGHOURS 2 " + place.getOpeningHours().getPeriods().get(0));
        if (opening_hours_close == 0) {
            holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.opening_hours_midnight));
        } else {
            holder.mRestaurantOpenInformation.setText(App.getResourses().getString(R.string.open_hours_until, opening_hours_close));

        }*/


        //CLICK ON A RESTAURANT
        holder.mConstraintLayoutItem.setOnClickListener(view -> {

            if(mData.get(position).getPlaceId()!=null){
                placeId = mData.get(position).getPlaceId();

                // Launch View Place Activity
                Intent intent = new Intent(view.getContext(), ViewPlaceActivity.class);
                intent.putExtra(RESTAURANT_PLACE_ID,placeId);
                view.getContext().startActivity(intent);
            }
            else{
                Toast.makeText(view.getContext(), "Aucun restaurant trouvé", Toast.LENGTH_SHORT).show();
            }

        });

        //RATING
        double ratingValue = mData.get(position).getRating();

        if (ratingValue > 0 && ratingValue < 1.665) {
            holder.mStarFirst.setVisibility(View.VISIBLE);
            holder.mStarSecond.setVisibility(View.INVISIBLE);
            holder.mStarThird.setVisibility(View.INVISIBLE);
            RestaurantHelper.updateRestaurantRatingApp(1,placeId,jobPlaceId);
        } else if (ratingValue >= 1.665 && ratingValue < 3.33) {
            holder.mStarFirst.setVisibility(View.VISIBLE);
            holder.mStarSecond.setVisibility(View.VISIBLE);
            holder.mStarThird.setVisibility(View.INVISIBLE);
            RestaurantHelper.updateRestaurantRatingApp(2,placeId,jobPlaceId);
        } else if (ratingValue >= 3.33 && ratingValue <= 5) {
            holder.mStarFirst.setVisibility(View.VISIBLE);
            holder.mStarSecond.setVisibility(View.VISIBLE);
            holder.mStarThird.setVisibility(View.VISIBLE);
            RestaurantHelper.updateRestaurantRatingApp(3,placeId,jobPlaceId);
        } else if (ratingValue <= 0 || ratingValue > 5) {
            holder.mStarFirst.setVisibility(View.INVISIBLE);
            holder.mStarSecond.setVisibility(View.INVISIBLE);
            holder.mStarThird.setVisibility(View.INVISIBLE);
            RestaurantHelper.updateRestaurantRatingApp(0,placeId,jobPlaceId);
        }

        //PHOTO
        // Initialize Places.
        Places.initialize(App.getInstance().getApplicationContext(), App.getResourses().getString(R.string.google_api_key));

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
            String attributions = photoMetadata.getAttributions();
            holder.mRestaurantImage.setContentDescription(attributions);
            // Create a FetchPhotoRequest.
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
        });

    }


    @Override
    public int getItemCount() {

        if (mDataFiltered != null) {

           return mDataFiltered.size();
        }else{
            return 0;
        }

    }


    public void setData(List<Result> mData) {

        Log.d("Debago","mData dans le setData "+mData);
        this.mData = mData;
        this.mDataFiltered = mData;
        //Fill the Recycler View
        notifyDataSetChanged();

    }


    public void setCurrentLocalisation(Double lat, Double longi) {

        //myCurrentLat = lat;
       // myCurrentLongi = longi;
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

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString().toLowerCase();


                if (charString.isEmpty()) {
                    mDataFiltered = mData;

                } else {
                    //   Log.d("Debago", "RecyclerViewRestaurant GETfILTER : charString is not empty result "+   mData.get(0).getName()+" contains charString "+ charString);
                    List<Result> filteredList = new ArrayList<>();


                    if (mData != null) {
                        for (Result result : mData) {

                            if (result.getName().toLowerCase().contains(charString)) {
                                filteredList.add(result);
                            }
                        }
                    }

                    mDataFiltered = filteredList;


                }

                FilterResults filterResults = new FilterResults();

                filterResults.values = mDataFiltered;
                Log.d("Debago","filteredresult after loop "+filterResults);
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataFiltered = (List<Result>) filterResults.values;
               // RecyclerViewListViewRestaurant.this.setData(mDataFiltered);
                notifyDataSetChanged();
            }
        };
    }

}
