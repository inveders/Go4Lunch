package com.inved.go4lunch.firebase;


public class UserFavoriteRestaurant {


    private String restaurantPlaceId;
    private Boolean isLiked;


    public UserFavoriteRestaurant(String restaurantPlaceId, Boolean isLiked) {
        this.restaurantPlaceId = restaurantPlaceId;
        this.isLiked = isLiked;
    }

    // --- GETTERS ---

    public String getRestaurantPlaceId() {
        return restaurantPlaceId;
    }

    public Boolean getLiked() {
        return isLiked;
    }


    // --- SETTERS ---

    public void setRestaurantPlaceId(String restaurantPlaceId) {
        this.restaurantPlaceId = restaurantPlaceId;
    }

    public void setLiked(Boolean isLiked) {
        isLiked = isLiked;
    }
}



