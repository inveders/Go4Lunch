package com.inved.go4lunch.controller;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.inved.go4lunch.R;
import com.inved.go4lunch.model.User;
import com.inved.go4lunch.utils.App;

public class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    private ImageView mWorkmatesImage;
    private TextView mWorkmatesText;


    public WorkmatesViewHolder(@NonNull View itemView) {

        super(itemView);

        mWorkmatesText = itemView.findViewById(R.id.fragment_people_item_text);
        mWorkmatesImage = itemView.findViewById(R.id.fragment_people_item_image);
    }

    public void updateWithUsers(User user, RequestManager glide){

        String firstname = user.getFirstname();
        String restaurantName =user.getRestaurantName();
        String restaurantType=user.getRestaurantType();

        // Update user TextView

        if(restaurantName!=null){
            this.mWorkmatesText.setText(App.getResourses().getString(R.string.workmates_text_choice, firstname, restaurantType, restaurantName));

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


}

