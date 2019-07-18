package com.inved.go4lunch.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.model.LatLng;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.GooglePlaceDetailsCalls;
import com.inved.go4lunch.model.placesearch.Result;
import com.inved.go4lunch.utils.App;

import java.util.List;

import static com.inved.go4lunch.controller.RestaurantActivity.KEY_GEOLOCALISATION;
import static com.inved.go4lunch.controller.RestaurantActivity.KEY_LOCATION_CHANGED;

public class RecyclerViewListViewRestaurant extends RecyclerView.Adapter<RecyclerViewListViewRestaurant.ViewHolder> {

    @Nullable
    private List<Result> mData;
    private List<com.inved.go4lunch.model.placedetails.Result> mDataDetail;
    private String placeId;
    private RequestManager glide;
    ViewPlaceActivity viewPlaceActivity = new ViewPlaceActivity();

    RecyclerViewListViewRestaurant() {

    }


    @NonNull
    @Override
    public RecyclerViewListViewRestaurant.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_listview_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewListViewRestaurant.ViewHolder holder, final int position) {

        holder.mRestaurantName.setText(mData.get(position).getName());

        holder.mRestaurantType.setText(mData.get(position).getTypes().toString());
        holder.mRestaurantAdress.setText(mData.get(position).getVicinity());
        placeId= mData.get(position).getPlaceId();


        if(mData.get(position).getOpeningHours().getOpenNow()){
       /*     int openHours =mData.get(position).getOpeningHours().getWeekdayText().indexOf(1);
            if (openHours<12){
                holder.mRestaurantOpenInformation.setText("Open until "+openHours+" am");
            }
            else{
                holder.mRestaurantOpenInformation.setText("Open until "+openHours+" pm");
            }*/
        }

        holder.mRestaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
         //       viewPlaceActivity.executeHttpRequestPlaceDetailsWithRetrofit(mData.get(position).getPlaceId());

            }
        });

        //Pour ajouter les marqueurs des restaurants
        Double lat = mData.get(position).getGeometry().getLocation().getLat();
        Double lng = mData.get(position).getGeometry().getLocation().getLng();
        LatLng latLng = new LatLng(lat, lng);


        if (mData.get(position).getPhotos().get(0).getHtmlAttributions() != null) {

         /*   glide.load(mData.get(position).getPhotos().get(0).getHtmlAttributions())
                    .into(holder.mRestaurantImage);*/

            } else {
            glide.load("https://previews.123rf.com/images/glebstock/glebstock1405/glebstock140501325/29470353-silhouette-m%C3%A2le-personne-inconnue-notion.jpg")
                    .into(holder.mRestaurantImage);
        }

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

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mRestaurantName;
        TextView mRestaurantType;
        TextView mRestaurantAdress;
        TextView mRestaurantOpenInformation;
        ImageView mRestaurantImage;

        ViewHolder(View itemView) {

            super(itemView);

            mRestaurantName= itemView.findViewById(R.id.fragment_listview_item_restaurant_name);
            mRestaurantType = itemView.findViewById(R.id.fragment_listview_item_restaurant_type);
            mRestaurantAdress = itemView.findViewById(R.id.fragment_listview_item_restaurant_adress);
            mRestaurantOpenInformation = itemView.findViewById(R.id.fragment_listview_item_restaurant_open_information);
            mRestaurantImage = itemView.findViewById(R.id.fragment_listview_item_image);

        }

    }


}
