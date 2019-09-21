package com.inved.go4lunch.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.inved.go4lunch.controller.activity.ViewPlaceActivity;
import com.inved.go4lunch.firebase.Restaurant;
import com.inved.go4lunch.utils.App;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.inved.go4lunch.controller.fragment.MapFragment.RESTAURANT_PLACE_ID;
import static com.inved.go4lunch.utils.ManageJobPlaceId.KEY_JOB_PLACE_ID_DATA;

public class RecyclerViewListViewRestaurant extends RecyclerView.Adapter<RecyclerViewListViewRestaurant.ViewHolder> implements Filterable {


    private String jobPlaceId;

    private PlacesClient placesClient;


    @Nullable
    private ArrayList<Restaurant> restaurantArrayList;
    private ArrayList<Restaurant> restaurantArrayListFiltered;

    private String placeId;
    private final RequestManager glide;

    public RecyclerViewListViewRestaurant(RequestManager glide, @Nullable ArrayList<Restaurant> restaurantArrayList) {

        this.glide = glide;
        this.restaurantArrayList = restaurantArrayList;

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

        if (restaurantArrayList != null) {
            String name = restaurantArrayList.get(position).getRestaurantName();
            holder.mRestaurantName.setText(name);
            placeId = restaurantArrayList.get(position).getRestaurantPlaceId();
            holder.mDistance.setText(App.getResourses().getString(R.string.distance_text, restaurantArrayList.get(position).getDistance()));

        }


        /**N'EST PAS ACTUALISE RAPIDEMENT*/
        if (restaurantArrayList != null) {
            holder.mRestaurantAdress.setText(restaurantArrayList.get(position).getRestaurantAddress());
            holder.mNumberRates.setText(String.valueOf(restaurantArrayList.get(position).getRestaurantCustomers()));
            int numberWorkmatesInRestaurant = restaurantArrayList.get(position).getRestaurantCustomers();
            holder.mNumberRates.setText(App.getResourses().getString(R.string.workmates_in_restaurant, numberWorkmatesInRestaurant));

        }


        //OPENING HOURS
        if (restaurantArrayList != null) {
            int opening_open_hours = restaurantArrayList.get(position).getOpenHours();
            int opening_open_minutes = restaurantArrayList.get(position).getOpenMinutes();
            int opening_close_hours = restaurantArrayList.get(position).getCloseHours();
            int opening_close_minutes = restaurantArrayList.get(position).getCloseMinutes();

            LocalDateTime currentTime = LocalDateTime.now();
            int current_hours = currentTime.getHour();


            if (current_hours < opening_open_hours) {
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
                    } else {
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
        }


        //CLICK ON A RESTAURANT
        holder.mConstraintLayoutItem.setOnClickListener(view -> {

            if (restaurantArrayList.get(position).getRestaurantPlaceId() != null) {
                placeId = restaurantArrayList.get(position).getRestaurantPlaceId();

                // Launch View Place Activity
                Intent intent = new Intent(view.getContext(), ViewPlaceActivity.class);
                intent.putExtra(RESTAURANT_PLACE_ID, placeId);
                view.getContext().startActivity(intent);
            } else {
                Toast.makeText(view.getContext(), "Aucun restaurant trouvé", Toast.LENGTH_SHORT).show();
            }

        });

        //RATING
        int ratingValue = restaurantArrayList.get(position).getRatingApp();

        switch (ratingValue) {
            case 1:
                holder.mStarFirst.setVisibility(View.VISIBLE);
                holder.mStarSecond.setVisibility(View.INVISIBLE);
                holder.mStarThird.setVisibility(View.INVISIBLE);
                break;
            case 2:
                holder.mStarFirst.setVisibility(View.VISIBLE);
                holder.mStarSecond.setVisibility(View.VISIBLE);
                holder.mStarThird.setVisibility(View.INVISIBLE);
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

    }


    @Override
    public int getItemCount() {

        if (restaurantArrayListFiltered != null) {

            return restaurantArrayListFiltered.size();
        } else {
            return 0;
        }

    }


    public void setData(ArrayList<Restaurant> restaurantArrayList) {

        Log.d("Debago", "mData dans le setData " + restaurantArrayList);
        this.restaurantArrayList = restaurantArrayList;
        this.restaurantArrayListFiltered = restaurantArrayList;
        //Fill the Recycler View
        notifyDataSetChanged();

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
                    restaurantArrayListFiltered = restaurantArrayList;

                } else {
                    //   Log.d("Debago", "RecyclerViewRestaurant GETfILTER : charString is not empty result "+   mData.get(0).getName()+" contains charString "+ charString);
                    ArrayList<Restaurant> filteredList = new ArrayList<>();


                    if (restaurantArrayList != null) {
                        for (Restaurant restaurant : restaurantArrayList) {

                            if (restaurant.getRestaurantName().toLowerCase().contains(charString)) {
                                filteredList.add(restaurant);
                            }
                        }
                    }

                    restaurantArrayListFiltered = filteredList;


                }

                FilterResults filterResults = new FilterResults();

                filterResults.values = restaurantArrayListFiltered;
                Log.d("Debago", "filteredresult after loop " + filterResults);
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                restaurantArrayListFiltered = (ArrayList<Restaurant>) filterResults.values;
                // RecyclerViewListViewRestaurant.this.setData(mDataFiltered);
                notifyDataSetChanged();
            }
        };
    }

}
