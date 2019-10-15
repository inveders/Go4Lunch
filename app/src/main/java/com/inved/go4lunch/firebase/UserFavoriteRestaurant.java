package com.inved.go4lunch.firebase;


public class UserFavoriteRestaurant {


    private String restaurantPlaceId;
    private Boolean liked;


    UserFavoriteRestaurant(String restaurantPlaceId, Boolean liked) {
        this.restaurantPlaceId = restaurantPlaceId;
        this.liked = liked;
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



