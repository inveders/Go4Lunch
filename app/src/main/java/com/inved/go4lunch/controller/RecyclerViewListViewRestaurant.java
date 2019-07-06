package com.inved.go4lunch.controller;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.inved.go4lunch.R;

import butterknife.BindView;

public class RecyclerViewListViewRestaurant /*extends RecyclerView.Adapter<RecyclerViewListViewRestaurant.ViewHolder> */{
/*
    @Nullable
    private List<Result> mData;

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

        holder.mRestaurantName.setText(mData.get(position).section);
        holder.mRestaurantType.setText(mData.get(position).subsection);
        holder.mRestaurantAdress.setText(convertedPublishedDate);
        holder.mRestaurantOpenInformation.setText(convertedPublishedDate);

        if (mData.get(position).getImageUrl() != null) {
            Picasso.get().load(mData.get(position).getImageUrl()).into(holder.mRestaurantImage);
        } else {
            Picasso.get().load("https://pmcdeadline2.files.wordpress.com/2016/10/the-new-york-times-logo-featured.jpg?w=446&h=299&crop=1").into(holder.mImageItem);
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

    }*/


}
