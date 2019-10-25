package com.inved.go4lunch.firebase;


public class UserFavoriteRestaurant {


    private String restaurantPlaceId;


    UserFavoriteRestaurant(String restaurantPlaceId) {
        this.restaurantPlaceId = restaurantPlaceId;
    }

    // --- GETTERS ---

    public String getRestaurantPlaceId() {
        return restaurantPlaceId;
    }


    // --- SETTERS ---

    public void setRestaurantPlaceId(String restaurantPlaceId) {
        this.restaurantPlaceId = restaurantPlaceId;
    }

}



