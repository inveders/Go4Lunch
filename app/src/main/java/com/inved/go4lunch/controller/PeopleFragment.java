package com.inved.go4lunch.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.inved.go4lunch.R;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserHelper;
import com.inved.go4lunch.utils.ManageJobPlaceId;

import static com.inved.go4lunch.utils.ManageJobPlaceId.KEY_JOB_PLACE_ID_DATA;

public class PeopleFragment extends Fragment implements WorkmatesAdapter.Listener{

    private View mView;
    private WorkmatesAdapter mRecyclerWorkmatesAdapter;
    private RecyclerView mRecyclerWorkmates;
    private String jobPlaceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        jobPlaceId = ManageJobPlaceId.getJobPlaceId(getActivity(),KEY_JOB_PLACE_ID_DATA);


        mView =inflater.inflate(R.layout.fragment_people,container,false);

        //RecyclerView initialization
        mRecyclerWorkmates = mView.findViewById(R.id.fragment_people_recycler_view);

        displayAllWorkmates();

        return mView;

    }

    private void displayAllWorkmates() {

        this.mRecyclerWorkmatesAdapter = new WorkmatesAdapter(generateOptionsForAdapter(UserHelper.getAllUsers(jobPlaceId)), Glide.with(this),this,getContext());
        //Choose how to display the list in the RecyclerView (vertical or horizontal)
        mRecyclerWorkmates.setHasFixedSize(true); //REVOIR CELA
        mRecyclerWorkmates.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerWorkmates.addItemDecoration(new DividerItemDecoration(mRecyclerWorkmates.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerWorkmates.setAdapter(mRecyclerWorkmatesAdapter);
    }

    // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        // 7 - Show TextView in case RecyclerView is empty
      //  textViewRecyclerViewEmpty.setVisibility(this.mentorChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
