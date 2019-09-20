package com.inved.go4lunch.firebase;

import com.google.android.gms.maps.model.LatLng;

public class Restaurant {

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
                      String phoneNumber) {

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

    // --- SETTERS ---
    public void setRestaurantPlaceId(String restaurantPlaceId) {
        this.restaurantPlaceId = restaurantPlaceId;
    }

    public void setRestaurantCustomers(int restaurantCustomers) {
        this.restaurantCustomers = restaurantCustomers;
    }

    public void setRestaurantLike(int restaurantLike) {
        this.restaurantLike = restaurantLike;
    }

    public void setJobPlaceId(String jobPlaceId) {
        this.jobPlaceId = jobPlaceId;
    }


    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setRatingApp(int ratingApp) {
        this.ratingApp = ratingApp;
    }

    public void setOpenForLunch(boolean openForLunch) {
        this.openForLunch = openForLunch;
    }


    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setOpenHours(int openHours) {
        this.openHours = openHours;
    }

    public void setCloseHours(int closeHours) {
        this.closeHours = closeHours;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
