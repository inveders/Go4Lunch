package com.inved.go4lunch;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inved.go4lunch.controller.activity.ViewPlaceActivity;
import com.inved.go4lunch.repository.NearbyRestaurantsRepository;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class NearbyRestaurantsRepositoryTest {

    private NearbyRestaurantsRepository nearbyRestaurantsRepositoryTest=new NearbyRestaurantsRepository();
    private int firebaseValue;

   /* @Before
    public void setUp() {
        /*nearbyRestaurantsRepositoryTest = Robolectric.buildbuildActivity(NearbyRestaurantsRepository.class)
                .create()
                .visible()
                .get();*/
     /*   nearbyRestaurantsRepositoryTest= new NearbyRestaurantsRepository();

    }*/

    @Test
    public void should_Save_In_Firebase_One_Star_With_RatingValue_4_1() {

        //Given
        double ratingValue = 4.1;



        //When
        firebaseValue = nearbyRestaurantsRepositoryTest.ratingValueCalcul(ratingValue);

        //Then

        assertEquals(1, firebaseValue);

    }

    @Test
    public void should_Save_In_Firebase_Two_Stars_With_RatingValue_4_3() {

        //Given
        double ratingValue = 4.3;

        //When
        firebaseValue = nearbyRestaurantsRepositoryTest.ratingValueCalcul(ratingValue);

        //Then

        assertEquals(2, firebaseValue);

    }


    @Test
    public void should_Show_In_Firebase_Three_Stars_With_RatingValue_4_8() {

        //Given
        double ratingValue = 4.8;

        //When
        firebaseValue = nearbyRestaurantsRepositoryTest.ratingValueCalcul(ratingValue);

        //Then

        assertEquals(3, firebaseValue);

    }


}
