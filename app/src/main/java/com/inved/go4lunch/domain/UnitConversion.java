package com.inved.go4lunch.domain;

import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class UnitConversion {

    public double convertDegreInRadians(double degre) {
        return (degre * Math.PI) / 180;
    }

    public double convertMeterToKm(long distance) {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String decimalFormat = df.format((double) distance / 1000);
        String finalFormat = decimalFormat.replace(",", ".");

        return Double.valueOf(finalFormat);

    }

    public long calculDistanceBetweenTwoPoint(double latitude, double longitude, double
            myCurrentLat, double myCurrentLongi, String appMode, String[] latlongJob, String normalMode) {

        try {
            double lat;
            double longi;
            //DISTANCE
            if (latitude != 0 || longitude != 0) {
                double latitudeRestaurant = convertDegreInRadians(latitude);
                double longitudeRestaurant = convertDegreInRadians(longitude);

                if (!appMode.equals(normalMode)) {

                    lat = convertDegreInRadians(Double.parseDouble(latlongJob[0]));
                    longi = convertDegreInRadians(Double.parseDouble(latlongJob[1]));
                } else {
                    lat = convertDegreInRadians(myCurrentLat);
                    longi = convertDegreInRadians(myCurrentLongi);
                }

                DecimalFormat df = new DecimalFormat("#");
                df.setRoundingMode(RoundingMode.HALF_UP);


                double distanceDouble = Math.acos(Math.sin(lat) * Math.sin(latitudeRestaurant) + Math.cos(lat) * Math.cos(latitudeRestaurant) * Math.cos(longitudeRestaurant - longi)) * 6371 * 1000;
                String decimalFormat = df.format(distanceDouble);
                return Long.valueOf(decimalFormat);
            }
        } catch (NumberFormatException exception) {
            Log.d("debago", "catch: ");
            return 0;

        }

        return 0;
    }
}
