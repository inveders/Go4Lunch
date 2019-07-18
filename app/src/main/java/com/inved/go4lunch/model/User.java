package com.inved.go4lunch.model;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String firstname;
    private String lastname;
    @Nullable
    private String urlPicture;
    private String emailAdress;
    private String restaurantName;
    private String restaurantType;

    public User() { }

    public User(String uid, String firstname, String lastname, String urlPicture, String restaurantName, String restaurantType) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.urlPicture = urlPicture;
        this.restaurantName = restaurantName;
        this.restaurantType=restaurantType;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getUrlPicture() { return urlPicture; }
    public String getRestaurantName() { return restaurantName; }
    public String getRestaurantType(){return restaurantType;}

    // --- SETTERS ---
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setRestaurantType(String restaurantType){this.restaurantType=restaurantType;}
}
