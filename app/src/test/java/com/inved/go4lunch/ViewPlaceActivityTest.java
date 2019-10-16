package com.inved.go4lunch;

import android.widget.TextView;

import com.inved.go4lunch.controller.activity.ViewPlaceActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
public class ViewPlaceActivityTest {

    private ViewPlaceActivity viewPlaceActivityTest;

    @Before
    public void setUp() {
        viewPlaceActivityTest = Robolectric.buildActivity(ViewPlaceActivity.class)
                .create()
                .visible()
                .get();
    }
//ROBOLECTRIC DON't WORKS WITH THE NEW API 29 SO CHANGE THE WAY TO MAKE TEST
    @Test
    public void should_UpdateTextViewPlaceNameAndAddress_With_GoodNameAndAddress() {

        //Given
        String restaurantName = "Alibaba";
        String restaurantAddress = "4 rue de la Foire aux Lions, 57000 Metz";

        TextView viewPlaceName = viewPlaceActivityTest.findViewById(R.id.activity_view_place_name);
        TextView viewPlaceAddress = viewPlaceActivityTest.findViewById(R.id.activity_view_place_address);

        //When
        viewPlaceActivityTest.updateViewPlaceActivity(restaurantName,restaurantAddress);

        //Then

        assertEquals(viewPlaceName.getText().toString(), restaurantName);
        assertEquals(viewPlaceAddress.getText().toString(),restaurantAddress);

    }
}


