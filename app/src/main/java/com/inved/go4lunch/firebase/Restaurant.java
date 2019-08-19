package com.inved.go4lunch.firebase;

public class Restaurant {

    private String id;
    private String restaurantPlaceId;
    private int restaurantCustomers;
    private int restaurantLike;
    private String jobPlaceId;


    public Restaurant(String id, String restaurantPlaceId, int restaurantCustomers,int restaurantLike,String jobPlaceId) {
        this.id = id;
        this.restaurantPlaceId = restaurantPlaceId;
        this.restaurantCustomers = restaurantCustomers;
        this.restaurantLike = restaurantLike;
        this.jobPlaceId = jobPlaceId;

    }

    // --- GETTERS ---
    public String getId() { return id; }
    public String getRestaurantPlaceId() { return restaurantPlaceId; }
    public int getRestaurantCustomers() { return restaurantCustomers; }
    public int getRestaurantLike() { return restaurantLike; }
    public String getJobPlaceId() { return jobPlaceId; }

    // --- SETTERS ---
    public void setRestaurantPlaceId(String restaurantPlaceId) { this.restaurantPlaceId = restaurantPlaceId; }
    public void setRestaurantCustomers(int restaurantCustomers) { this.restaurantCustomers = restaurantCustomers; }
    public void setRestaurantLike(int restaurantLike) { this.restaurantLike = restaurantLike; }
    public void setUid(String id) { this.id = id; }
    public void setJobPlaceId(String jobPlaceId) { this.jobPlaceId = jobPlaceId; }

}
