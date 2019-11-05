package com.inved.go4lunch;

import com.inved.go4lunch.controller.activity.RestaurantActivity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RestaurantActivityTest {

    private RestaurantActivity restaurantActivity = new RestaurantActivity();
    private double lat_center=49.424738;
    private double longi_center=6.015377;

    @Test
    public void calculateBound_WhenLatLng_NE_LAT() {

        double ratingValue = restaurantActivity.calculateBound("NE_LAT",lat_center,longi_center);
        double result = 49.55948521;

        assertEquals(result, ratingValue,0.00000001);
    }

    @Test
    public void calculateBound_WhenLatLng_NE_LNG() {

        double ratingValue = restaurantActivity.calculateBound("NE_LNG",lat_center,longi_center);
        double result = 6.222538155635307;

        assertEquals(result, ratingValue,0.0000001);
    }

    @Test
    public void calculateBound_WhenLatLng_SW_LAT() {

        double ratingValue = restaurantActivity.calculateBound("SW_LAT",lat_center,longi_center);
        double result = 49.28999079;

        assertEquals(result, ratingValue,0.00000001);
    }

    @Test
    public void calculateBound_WhenLatLng_SW_LNG() {

        double ratingValue = restaurantActivity.calculateBound("SW_LNG",lat_center,longi_center);
        double result = 5.8082158443646925;

        assertEquals(result, ratingValue,0.0000001);
    }
}