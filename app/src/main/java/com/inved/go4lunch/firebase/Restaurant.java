package com.inved.go4lunch.firebase;

public class Restaurant {

    private String id;
    private String restaurantPlaceId;
    private int restaurantCustomers;

    public Restaurant(String id, String restaurantPlaceId, int restaurantCustomers) {
        this.id = id;
        this.restaurantPlaceId = restaurantPlaceId;
        this.restaurantCustomers = restaurantCustomers;

    }

    // --- GETTERS ---
    public String getId() { return id; }
    public String getRestaurantPlaceId() { return restaurantPlaceId; }
    public int getRestaurantCustomers() { return restaurantCustomers; }

    // --- SETTERS ---
    public void setRestaurantPlaceId(String restaurantPlaceId) { this.restaurantPlaceId = restaurantPlaceId; }
    public void setRestaurantCustomers(int restaurantCustomers) { this.restaurantCustomers = restaurantCustomers; }
    public void setUid(String id) { this.id = id; }

}
