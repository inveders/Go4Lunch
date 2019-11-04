package com.inved.go4lunch.domain;

public class RatingCalcul {

    public int ratingValueCalcul(double ratingValue) {

        int rating = 0;
        if (ratingValue > 0 && ratingValue < 4.3) {
            rating = 1;
        } else if (ratingValue >= 4.3 && ratingValue < 4.6) {
            rating = 2;
        } else if (ratingValue >= 4.6 && ratingValue <= 5) {
            rating = 3;
        }
        return rating;

    }

}
