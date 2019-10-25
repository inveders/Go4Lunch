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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.MAP_API_KEY;
import static com.inved.go4lunch.controller.activity.RestaurantActivity.TAG;
import static com.inved.go4lunch.controller.fragment.MapFragment.RESTAURANT_PLACE_ID;

public class RecyclerViewListViewFavoriteRestaurant extends FirestoreRecyclerAdapter<Restaurant, RecyclerViewListViewFavoriteRestaurant.ViewHolder> {

    private String placeId;
    private ListenerFavorite callback;

    public interface ListenerFavorite {
        void onDataChangedFavorite();
    }

    public RecyclerViewListViewFavoriteRestaurant(@NonNull FirestoreRecyclerOptions<Restaurant> options, ListenerFavorite callback) {

        super(options);
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewListViewFavoriteRestaurant.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_favorite_listview_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewListViewFavoriteRestaurant.ViewHolder holder, final int position, @NonNull Restaurant restaurant) {

        placeId = restaurant.getRestaurantPlaceId();

        // Initialize Places.
        Places.initialize(App.getInstance().getApplicationContext(), MAP_API_KEY);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(App.getInstance().getApplicationContext());

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS);


        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();


        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            holder.mRestaurantName.setText(place.getName());
            holder.mRestaurantAdress.setText(place.getAddress());

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {

                Log.e(TAG, "Place detail not found: " + exception.getMessage());
            }
        });


        //PHOTO

        // Specify the fields to return.
        List<Place.Field> fieldsPhoto = Collections.singletonList(Place.Field.PHOTO_METADATAS);

        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, fieldsPhoto).build();

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



    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mRestaurantName;
        TextView mRestaurantAdress;
        ImageView mRestaurantImage;

        ConstraintLayout mConstraintLayoutItem;


        ViewHolder(View itemView) {

            super(itemView);

            mRestaurantName = itemView.findViewById(R.id.activity_favorite_listview_item_restaurant_name);
            mRestaurantAdress = itemView.findViewById(R.id.activity_favorite_listview_item_restaurant_address);
            mRestaurantImage = itemView.findViewById(R.id.activity_favorite_listview_item_image);
            mConstraintLayoutItem = itemView.findViewById(R.id.activity_favorite_listview_item);

        }

    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChangedFavorite();

    }
}
