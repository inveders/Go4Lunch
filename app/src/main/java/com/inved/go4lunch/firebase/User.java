package com.inved.go4lunch.firebase;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String firstname;
    private String lastname;
    @Nullable
    private String urlPicture;
    private String emailAdress;
    private String restaurantPlaceId;
    private String restaurantType;
    private String restaurantName;
    private String restaurantVicinity;
    public User() { }

    public User(String uid, String firstname, String lastname, String urlPicture, String restaurantPlaceId, String restaurantType,String restaurantName,String restaurantVicinity) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.urlPicture = urlPicture;
        this.restaurantPlaceId = restaurantPlaceId;
        this.restaurantType=restaurantType;
        this.restaurantName = restaurantName;
        this.restaurantVicinity = restaurantVicinity;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getUrlPicture() { return urlPicture; }
    public String getRestaurantPlaceId() { return restaurantPlaceId; }
    public String getRestaurantType(){return restaurantType;}
    public String getRestaurantName() { return restaurantName; }
    public String getRestaurantVicinity() { return restaurantVicinity; }

    // --- SETTERS ---
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setRestaurantPlaceId(String restaurantPlaceId) { this.restaurantPlaceId = restaurantPlaceId; }
    public void setRestaurantType(String restaurantType){this.restaurantType=restaurantType;}
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setRestaurantVicinity(String restaurantVicinity) { this.restaurantVicinity = restaurantVicinity; }
}
