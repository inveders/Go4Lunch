package com.inved.go4lunch.controller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.inved.go4lunch.R;

import butterknife.BindView;

public class RestaurantActivity extends AppCompatActivity {

    //1 - FOR DESIGN
    @BindView(R.id.activity_restaurant_bottom_navigation) BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.configureBottomView();
    }

    // -------------------
    // CONFIGURATION
    // -------------------


    // 2 - Configure BottomNavigationView Listener
    private void configureBottomView(){
      //  bottomNavigationView.setOnNavigationItemSelectedListener(item -> updateAbsBseFragment(item.getItemId()));
    }

    // -------------------
    // UI
    // -------------------

    // Update Base Fragment design
  /*  private Boolean updateAbsBseFragment(Integer integer){
        switch (integer) {
            case R.id.action_map:
                this.MapFragment.updateDesignWhenUserClickedBottomView(MapFragment.REQUEST_MAP);
                break;
            case R.id.action_list:
                this.ListViewFragment.updateDesignWhenUserClickedBottomView(ListViewFragment.REQUEST_LIST);
                break;
            case R.id.action_people:
                this.PeopleFragment.updateDesignWhenUserClickedBottomView(PeopleFragment.REQUEST_PEOPLE);
                break;
        }
        return true;
    }*/ /**Pas besoin pour l'instant*/
}
