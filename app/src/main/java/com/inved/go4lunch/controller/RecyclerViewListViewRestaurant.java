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
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.model.placesearch.Result;
import com.inved.go4lunch.utils.App;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LATITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LONGITUDE;
import static com.inved.go4lunch.controller.RestaurantActivity.PLACE_DETAIL_DATA;

public class RecyclerViewListViewRestaurant extends RecyclerView.Adapter<RecyclerViewListViewRestaurant.ViewHolder> {

    private Double myCurrentLat;
    private Double myCurrentLongi;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (KEY_LOCATION_CHANGED.equals(intent.getAction())) {
                //  myCurrentLat = intent.getDoubleExtra(KEY_LATITUDE, 0.0);
                //  myCurrentLongi = intent.getDoubleExtra(KEY_LONGITUDE, 0.0);
                //   Log.d("Debago", "RecyclerViewRestaurant onBroadcastReceiver myCurrentLat"+myCurrentLat);
            }
        }
    };

    @Nullable
    private List<Result> mData;
    private List<com.inved.go4lunch.model.placedetails.Result> mDataDetail;

    private String placeId;
    private final RequestManager glide;
    Context mContext;
    PlaceDetailsData placeDetailsData = new PlaceDetailsData();
    ViewPlaceActivity viewPlaceActivity = new ViewPlaceActivity();

    public RecyclerViewListViewRestaurant(RequestManager glide) {

        this.glide = glide;
    }


    @NonNull
    @Override
    public RecyclerViewListViewRestaurant.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_listview_item, parent, false);
        LocalBroadcastManager.getInstance(parent.getContext()).registerReceiver(broadcastReceiver, new IntentFilter(KEY_LOCATION_CHANGED));

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewListViewRestaurant.ViewHolder holder, final int position) {

        holder.mRestaurantName.setText(mData.get(position).getName());

        holder.mRestaurantType.setText(mData.get(position).getTypes().get(0));

        holder.mRestaurantAdress.setText(mData.get(position).getVicinity());
        placeId = mData.get(position).getPlaceId();

        //Distance entre deux points
        Double latitudeRestaurant = convertRad(mData.get(position).getGeometry().getLocation().getLat());
        Double longitudeRestaurant = convertRad(mData.get(position).getGeometry().getLocation().getLng());
        Double latCurrent = convertRad(myCurrentLat);
        Double longiCurrent = convertRad(myCurrentLongi);

        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.HALF_UP);

        Double distanceDouble = Math.acos(Math.sin(latCurrent) * Math.sin(latitudeRestaurant) + Math.cos(latCurrent) * Math.cos(latitudeRestaurant) * Math.cos(longitudeRestaurant - longiCurrent)) * 6371*1000;
        String distance = df.format(distanceDouble);
        holder.mDistance.setText(distance+" m");

     /*   if (mData.get(position).getOpeningHours().getOpenNow()) {
            int openHours =mData.get(position).getOpeningHours().;
            if (openHours<12){
                holder.mRestaurantOpenInformation.setText("Open until "+openHours+" am");
            }
            else{
                holder.mRestaurantOpenInformation.setText("Open until "+openHours+" pm");
            }
        }*/

        holder.mConstraintLayoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                placeDetailsData.setPlaceId(placeId);

                // Launch View Place Activity
                Intent intent = new Intent(view.getContext(), ViewPlaceActivity.class);
                view.getContext().startActivity(intent);

            }
        });

        //Photo
        if (mData.get(position).getPhotos().get(0).getPhotoReference() != null) {

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

        } else {
            glide.load("https://previews.123rf.com/images/glebstock/glebstock1405/glebstock140501325/29470353-silhouette-m%C3%A2le-personne-inconnue-notion.jpg")
                    .into(holder.mRestaurantImage);
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

    public void setCurrentLocalisation(Double lat, Double longi) {

        myCurrentLat = lat;
        myCurrentLongi = longi;
        //Fill the Recycler View
        //  notifyDataSetChanged();

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mRestaurantName;
        TextView mRestaurantType;
        TextView mRestaurantAdress;
        TextView mRestaurantOpenInformation;
        ImageView mRestaurantImage;
        ConstraintLayout mConstraintLayoutItem;
        TextView mDistance;

        ViewHolder(View itemView) {

            super(itemView);

            mRestaurantName = itemView.findViewById(R.id.fragment_listview_item_restaurant_name);
            mRestaurantType = itemView.findViewById(R.id.fragment_listview_item_restaurant_type);
            mRestaurantAdress = itemView.findViewById(R.id.fragment_listview_item_restaurant_adress);
            mRestaurantOpenInformation = itemView.findViewById(R.id.fragment_listview_item_restaurant_open_information);
            mRestaurantImage = itemView.findViewById(R.id.fragment_listview_item_image);
            mConstraintLayoutItem = itemView.findViewById(R.id.fragment_listview_item);
            mDistance = itemView.findViewById(R.id.fragment_listview_item_distance);

        }

    }


}
