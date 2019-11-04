package com.inved.go4lunch;

import com.inved.go4lunch.domain.RatingCalcul;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class RatingCalculTest {


    private RatingCalcul ratingCalcul = Mockito.spy(new RatingCalcul());

    @Test
    public void should_Retrieve_One_When_RatingIs_4_2() {

        //Given
        double ratingValue = 4.2;

        //When
        int result = ratingCalcul.ratingValueCalcul(ratingValue);

        //Then
        Assert.assertEquals(1, result);

    }

    @Test
    public void should_Retrieve_Two_When_RatingIs_4_5() {

        //Given
        double ratingValue = 4.5;

        //When
        int result = ratingCalcul.ratingValueCalcul(ratingValue);

        //Then
        Assert.assertEquals(2, result);

    }

    @Test
    public void should_Retrieve_Three_When_RatingIs_4_6() {

        //Given
        double ratingValue = 4.6;

        //When
        int result = ratingCalcul.ratingValueCalcul(ratingValue);

        //Then
        Assert.assertEquals(3, result);

    }

}
