package com.inved.go4lunch.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.inved.go4lunch.R;
import com.inved.go4lunch.firebase.User;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesViewHolder>  {

    private Context context;


    public interface Listener {
        void onDataChanged();
    }

    //FOR DATA
    private final RequestManager glide;


    //FOR COMMUNICATION
    private Listener callback;

    WorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide, Listener callback, Context context) {
        super(options);
        this.glide = glide;
        this.callback = callback;
        this.context=context;

    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder workmatesViewHolder, int position, @NonNull User user) {
        if(context.getClass().equals(ViewPlaceActivity.class) ){

            workmatesViewHolder.updateWithWorkmatesJoining(user, this.glide);
        }
        else if(context.getClass().equals(RestaurantActivity.class)) {

            workmatesViewHolder.updateWithUsers(user, this.glide);

        }

    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new WorkmatesViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_people_item, parent, false));
    }

    @Override
    public void onDataChanged(){
        super.onDataChanged();
        this.callback.onDataChanged();

        //Fill the Recycler View
//        notifyDataSetChanged();

    }



}
