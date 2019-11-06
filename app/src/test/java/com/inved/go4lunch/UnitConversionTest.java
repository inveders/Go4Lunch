package com.inved.go4lunch;

import com.inved.go4lunch.domain.UnitConversion;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class UnitConversionTest {

    private UnitConversion unitConversion = Mockito.spy(new UnitConversion());

    @Test
    public void should_ConvertInRadians_With_Degre() {

        //Given
        double degre = 1000;

        //When
        double radians = unitConversion.convertDegreInRadians(degre);

        //Then

        Assert.assertEquals(17.453292519943293, radians,0.000001);

    }


    @Test
    public void should_ConvertInKm_With_Meters() {

        //Given
        long distanceMeter = 4850;

        //When
        double distanceKm = unitConversion.convertMeterToKm(distanceMeter);

        //Then

        Assert.assertEquals(4.8, distanceKm,0.01);

    }


    @Test
    public void should_CalculateDistanceBetweenTwoPoints_With_LatitudeLongitude() {

        //Given
        double latitude=49.4245568;
        double longitude=6.0153852;
        double myCurrentLat=49.4895;
        double myCurrentLongi=5.978216199999999;
        String appMode="work";
        String normalMode="normal";
        String[] latlongJob={"49.4894527","5.977546500000001"};

        //When
        long distanceMeter = unitConversion.calculDistanceBetweenTwoPoint(latitude,longitude,myCurrentLat,myCurrentLongi,appMode,latlongJob,normalMode);

        //Then
        Assert.assertEquals(7717.0, distanceMeter,0.01);

    }


}
