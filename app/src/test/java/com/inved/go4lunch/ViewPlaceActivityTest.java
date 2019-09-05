package com.inved.go4lunch;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inved.go4lunch.controller.ViewPlaceActivity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;


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

    @Test
    public void should_Show_Two_Stars_With_RatingValue_Two() {

        //Given
        double ratingValue = 2;
        ImageView likeStarSecond = viewPlaceActivityTest.findViewById(R.id.activity_view_place_like_start_second);

        //When
        viewPlaceActivityTest.showingLikeStars(ratingValue);

        //Then

        Assert.assertEquals(View.VISIBLE, likeStarSecond.getVisibility());

    }


    @Test
    public void should_Show_Three_Stars_With_RatingValue_Four() {

        //Given
        double ratingValue = 4;
        ImageView likeStarThird = viewPlaceActivityTest.findViewById(R.id.activity_view_place_like_start_third);

        //When
        viewPlaceActivityTest.showingLikeStars(ratingValue);

        //Then

        Assert.assertEquals(View.VISIBLE, likeStarThird.getVisibility());

    }

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

        Assert.assertEquals(viewPlaceName.getText().toString(), restaurantName);
        Assert.assertEquals(viewPlaceAddress.getText().toString(),restaurantAddress);

    }
}


