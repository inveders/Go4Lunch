package com.inved.go4lunch.firebase;

public class Restaurant {

    private String id;
    private String restaurantPlaceId;
    private int restaurantCustomers;
    private int restaurantLike;


    public Restaurant(String id, String restaurantPlaceId, int restaurantCustomers,int restaurantLike) {
        this.id = id;
        this.restaurantPlaceId = restaurantPlaceId;
        this.restaurantCustomers = restaurantCustomers;
        this.restaurantLike = restaurantLike;

    }

    // --- GETTERS ---
    public String getId() { return id; }
    public String getRestaurantPlaceId() { return restaurantPlaceId; }
    public int getRestaurantCustomers() { return restaurantCustomers; }
    public int getRestaurantLike() { return restaurantLike; }

    // --- SETTERS ---
    public void setRestaurantPlaceId(String restaurantPlaceId) { this.restaurantPlaceId = restaurantPlaceId; }
    public void setRestaurantCustomers(int restaurantCustomers) { this.restaurantCustomers = restaurantCustomers; }
    public void setRestaurantLike(int restaurantLike) { this.restaurantLike = restaurantLike; }
    public void setUid(String id) { this.id = id; }


}
