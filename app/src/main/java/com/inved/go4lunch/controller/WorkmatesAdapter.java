package com.inved.go4lunch.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inved.go4lunch.R;
import com.inved.go4lunch.api.UserHelper;
import com.inved.go4lunch.model.User;
import com.inved.go4lunch.model.placesearch.Result;

import java.util.List;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesViewHolder>  {


    public interface Listener {
        void onDataChanged();
    }

    //FOR DATA
    private final RequestManager glide;


    //FOR COMMUNICATION
    private Listener callback;

    public WorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide, Listener callback) {
        super(options);
        this.glide = glide;
        this.callback = callback;

    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder workmatesViewHolder, int position, @NonNull User user) {
        workmatesViewHolder.updateWithUsers(user, this.glide);

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
