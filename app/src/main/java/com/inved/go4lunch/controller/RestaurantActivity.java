package com.inved.go4lunch.controller;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.inved.go4lunch.R;

import butterknife.BindView;

public class RestaurantActivity extends AppCompatActivity {

    //1 - FOR DESIGN
 //   @BindView(R.id.activity_restaurant_bottom_navigation) BottomNavigationView bottomNavigationView;
    MapFragment mapFragment = new MapFragment();
    ListViewFragment listViewFragment = new ListViewFragment();
    PeopleFragment peopleFragment = new PeopleFragment();
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        bottomNavigationView = findViewById(R.id.activity_restaurant_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> updateMainFragment(item.getItemId()));
        bottomNavigationView.setSelectedItemId(R.id.action_map); //The first page is map Fragment


    }

    // -------------------
    // CONFIGURATION
    // -------------------


    private Boolean updateMainFragment(Integer integer){
        switch (integer) {
            case R.id.action_map:
                setFragment(mapFragment);
                break;
            case R.id.action_list:
                setFragment(listViewFragment);
                break;
            case R.id.action_people:
                setFragment(peopleFragment);
                break;
        }
        return true;
    }

    private void setFragment (Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.activity_restaurant_frame_layout, fragment);
        fragmentTransaction.commit();
    }


    // -------------------
    // UI
    // -------------------


}
