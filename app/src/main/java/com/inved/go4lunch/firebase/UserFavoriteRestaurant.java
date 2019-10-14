package com.inved.go4lunch.firebase;


public class UserFavoriteRestaurant {


    private String restaurantPlaceId;
    private Boolean liked;


    public UserFavoriteRestaurant(String restaurantPlaceId, Boolean liked) {
        this.restaurantPlaceId = restaurantPlaceId;
        this.liked = liked;
    }

    // --- GETTERS ---

    public String getRestaurantPlaceId() {
        return restaurantPlaceId;
    }

    public Boolean getLiked() {
        return liked;
    }


    // --- SETTERS ---

    public void setRestaurantPlaceId(String restaurantPlaceId) {
        this.restaurantPlaceId = restaurantPlaceId;
    }

    public void setLiked(Boolean liked) {
        liked = liked;
    }
}



