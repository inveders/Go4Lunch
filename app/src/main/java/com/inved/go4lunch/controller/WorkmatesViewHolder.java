package com.inved.go4lunch.controller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.PlaceDetailsData;
import com.inved.go4lunch.firebase.Restaurant;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.utils.App;

public class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    private ImageView mWorkmatesImage;
    private TextView mWorkmatesText;
    ConstraintLayout mConstraintLayoutItem;
    PlaceDetailsData placeDetailsData = new PlaceDetailsData();


    public WorkmatesViewHolder(@NonNull View itemView) {

        super(itemView);

        mWorkmatesText = itemView.findViewById(R.id.fragment_people_item_text);
        mWorkmatesImage = itemView.findViewById(R.id.fragment_people_item_image);
        mConstraintLayoutItem = itemView.findViewById(R.id.fragment_people_item);
    }

    public void updateWithUsers(User user, RequestManager glide){

        String firstname = user.getFirstname();
        String restaurantPlaceId =user.getRestaurantPlaceId();
        String restaurantType=user.getRestaurantType();
        String restaurantName=user.getRestaurantName();

        // Update user TextView

        if(restaurantPlaceId!=null){
            this.mWorkmatesText.setText(App.getResourses().getString(R.string.workmates_text_choice, firstname, restaurantType, restaurantName));

            mConstraintLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    placeDetailsData.setPlaceId(restaurantPlaceId);

                    // Launch View Place Activity
                    Intent intent = new Intent(view.getContext(), ViewPlaceActivity.class);
                    view.getContext().startActivity(intent);

                }
            });

        } else {

            this.mWorkmatesText.setText(App.getResourses().getString(R.string.workmates_text_no_choice, firstname));
            mWorkmatesText.setTextColor(Color.parseColor("#D3D3D3"));
            mWorkmatesText.setTypeface(null, Typeface.ITALIC);
        }


        // Update image sent ImageView
        if (user.getUrlPicture() != null){
            glide.load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mWorkmatesImage);

        } else{
            glide.load("https://previews.123rf.com/images/glebstock/glebstock1405/glebstock140501325/29470353-silhouette-m%C3%A2le-personne-inconnue-notion.jpg")
                    .apply(RequestOptions.circleCropTransform())
                    .into(mWorkmatesImage);
        }



    }


    public void updateWithWorkmatesJoining(User user, RequestManager glide){

        String firstname = user.getFirstname();
        String restaurantPlaceId =user.getRestaurantPlaceId();


        // Update user TextView

        if(restaurantPlaceId!=null){
            this.mWorkmatesText.setText(App.getResourses().getString(R.string.workmates_text_joining, firstname));

        }


        // Update image sent ImageView
        if (user.getUrlPicture() != null){
            glide.load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mWorkmatesImage);

        } else{
            glide.load("https://previews.123rf.com/images/glebstock/glebstock1405/glebstock140501325/29470353-silhouette-m%C3%A2le-personne-inconnue-notion.jpg")
                    .apply(RequestOptions.circleCropTransform())
                    .into(mWorkmatesImage);
        }

    }


}

