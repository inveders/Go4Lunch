package com.inved.go4lunch;

import com.inved.go4lunch.controller.activity.RestaurantActivity;
import com.inved.go4lunch.repository.NearbyRestaurantsRepository;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RestaurantActivityTest {

    private RestaurantActivity restaurantActivity = new RestaurantActivity();

    @Test
    public void calculateBound_WhenLatLng_NE_LAT() {

        double ratingValue = restaurantActivity.calculateBound("NE_LAT");
        double result = 0.1347472142261118;

        assertEquals(result, ratingValue,0.0001);
    }
}