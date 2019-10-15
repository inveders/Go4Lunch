package com.inved.go4lunch.firebase;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String firstname;
    private String lastname;
    @Nullable
    private String urlPicture;
    private String restaurantPlaceId;
    private String restaurantName;
    private String restaurantVicinity;
    private String jobAddress;
    private String jobPlaceId;
    private String jobName;
    private boolean notificationEnabled;

    public User() { }

    User(String uid, String firstname, String lastname, @org.jetbrains.annotations.Nullable String urlPicture, String restaurantPlaceId,String restaurantName, String restaurantVicinity, String jobAddress, String jobPlaceId, String jobName, String token, boolean notificationEnabled) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.urlPicture = urlPicture;
        this.restaurantPlaceId = restaurantPlaceId;
        this.restaurantName = restaurantName;
        this.restaurantVicinity = restaurantVicinity;
        this.jobAddress=jobAddress;
        this.jobPlaceId=jobPlaceId;
        this.jobName=jobName;
        this.notificationEnabled=notificationEnabled;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    @org.jetbrains.annotations.Nullable
    public String getUrlPicture() { return urlPicture; }
    public String getRestaurantPlaceId() { return restaurantPlaceId; }

    public String getRestaurantName() { return restaurantName; }
    public String getRestaurantVicinity() { return restaurantVicinity; }
    public String getJobAddress() { return jobAddress; }
    public String getJobPlaceId() { return jobPlaceId; }
    public String getJobName() { return jobName; }

    public boolean isNotificationEnabled() {return notificationEnabled;}

    // --- SETTERS ---

    public void setRestaurantPlaceId(String restaurantPlaceId) { this.restaurantPlaceId = restaurantPlaceId; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setJobPlaceId(String jobPlaceId) { this.jobPlaceId = jobPlaceId; }
}
