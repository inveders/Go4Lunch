package com.inved.go4lunch.firebase;

import android.os.Build;

import androidx.annotation.NonNull;

import java.util.Comparator;

public class Restaurant implements Comparable<Restaurant> {

    private String restaurantPlaceId;
    private int restaurantCustomers;
    private int restaurantLike;
    private String jobPlaceId;
    private String restaurantName;
    private int ratingApp;
    private boolean openForLunch;
    private String distance;
    private int openHours;
    private int closeHours;
    private String restaurantAddress;
    private double latitude;
    private double longitude;
    private String website;
    private String phoneNumber;
    private int openMinutes;
    private int closeMinutes;

    public Restaurant(){

    }


    public Restaurant(String restaurantPlaceId,
                      int restaurantCustomers,
                      int restaurantLike,
                      String jobPlaceId,
                      String restaurantName,
                      int ratingApp,
                      boolean openForLunch,
                      String distance,
                      int openHours,
                      int closeHours,
                      String restaurantAddress,
                      double latitude,
                      double longitude,
                      String website,
                      String phoneNumber,
                      int openMinutes,
                      int closeMinutes) {

        this.restaurantPlaceId = restaurantPlaceId;
        this.restaurantCustomers = restaurantCustomers;
        this.restaurantLike = restaurantLike;
        this.jobPlaceId = jobPlaceId;
        this.restaurantName = restaurantName;
        this.ratingApp = ratingApp;
        this.openForLunch = openForLunch;
        this.distance = distance;
        this.openHours = openHours;
        this.closeHours = closeHours;
        this.restaurantAddress = restaurantAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.openMinutes = openMinutes;
        this.closeMinutes = closeMinutes;

    }


    // --- GETTERS ---

    public String getRestaurantPlaceId() {
        return restaurantPlaceId;
    }

    public int getRestaurantCustomers() {
        return restaurantCustomers;
    }

    public int getRestaurantLike() {
        return restaurantLike;
    }

    public String getJobPlaceId() {
        return jobPlaceId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public int getRatingApp() {
        return ratingApp;
    }

    public boolean getOpenForLunch() {
        return openForLunch;
    }


    public String getDistance() {
        return distance;
    }


    public int getOpenHours() {
        return openHours;
    }

    public int getCloseHours() {
        return closeHours;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getOpenMinutes() {
        return openMinutes;
    }

    public int getCloseMinutes() {
        return closeMinutes;
    }

    // --- SETTERS ---
    public void setRestaurantPlaceId(String restaurantPlaceId) {
        this.restaurantPlaceId = restaurantPlaceId;
    }

    public void setJobPlaceId(String jobPlaceId) {
        this.jobPlaceId = jobPlaceId;
    }


    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }


    public void setDistance(String distance) {
        this.distance = distance;
    }


    @Override
    public int compareTo(@NonNull Restaurant o) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Comparator.comparing(Restaurant::getRatingApp)
                    .thenComparing(Restaurant::getOpenForLunch)
                    .thenComparing(Restaurant::getRestaurantCustomers)
                    .thenComparing(Restaurant::getDistance)
                    .reversed()
                    .compare(this, o);
        }

        return -1;
    }

}
