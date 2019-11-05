package com.inved.go4lunch.domain;

import android.util.Log;

import com.google.android.libraries.places.api.model.OpeningHours;

public class OpeningHoursCalculation {

    @SuppressWarnings("ConstantConditions")
    public int openHoursCalcul(OpeningHours openingHours, int day, int currentHour,String stringCurrentDay) {

        Log.d("debaga","opening hours : "+openingHours);
        if (openingHours != null) {
            //if getpriod.size equal to 7
            if (openingHours.getPeriods().size() == 7) {

                if (openingHours.getPeriods().get(day).getOpen() != null) {
                    return openingHours.getPeriods().get(day).getOpen().getTime().getHours();
                }
            } else {
                //if getpriod.size is different from 7
                if (openingHours.getPeriods().size() != 0) {

                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {

                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {


                            //Current hour is less than open hour
                            if (currentHour < openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                int myOccurrence = 0;
                                //We check if there is other same day in the list
                                for (int y = 0; y < openingHours.getPeriods().size(); y++) {
                                    if ((openingHours.getPeriods().get(y).getOpen().getDay().toString()).equals(stringCurrentDay)) {

                                        if (y != i) {
                                            myOccurrence = y;
                                        }

                                    }
                                }

                                //if I have a second even day I check wich is near from my current hour
                                if (myOccurrence != 0) {
                                    int hourOne = openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    int hourTwo = openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getHours();
                                    if (hourOne - currentHour >= 0 && Math.abs(hourOne - currentHour) <= Math.abs(hourTwo - currentHour)) {

                                        return openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    } else {
                                        return openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getHours();
                                    }
                                }
                                //Else I take only the one
                                else {
                                    return openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                }
                            } else if (currentHour > openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {

                                if (openingHours.getPeriods().get(i).getClose() != null) {
                                    if (currentHour < openingHours.getPeriods().get(i).getClose().getTime().getHours()) {

                                        return openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    }
                                }


                            }

                        }

                    }

                    int noResultFound = 0;
                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {
                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                            noResultFound = i;

                        }
                    }
                    if (noResultFound == 0) {
                        return -1;
                    }


                }
                //if getpriod.size is 0
                else {
                    return -1;
                }

            }

        }
        return -1;

    }

    @SuppressWarnings("ConstantConditions")
    public int openMinutesCalcul(OpeningHours openingHours,int day,int currentHour,String stringCurrentDay) {

        if (openingHours != null) {

            //if getpriod.size equal to 7
            if (openingHours.getPeriods().size() == 7) {


                if (openingHours.getPeriods().get(day).getOpen() != null) {
                    return openingHours.getPeriods().get(day).getOpen().getTime().getMinutes();
                }
            } else {
                //if getpriod.size is different from 7
                if (openingHours.getPeriods().size() != 0) {

                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {

                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {

                            //Current hour is less than open hour
                            if (currentHour < openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                int myOccurrence = 0;
                                //We check if there is other same day in the list
                                for (int y = 0; y < openingHours.getPeriods().size(); y++) {
                                    if ((openingHours.getPeriods().get(y).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                                        if (y != i) {
                                            myOccurrence = y;
                                        }

                                    }
                                }
                                //if I have a second even day I check wich is near from my current hour
                                if (myOccurrence != 0) {
                                    int hourOne = openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    int hourTwo = openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getHours();
                                    if (hourOne - currentHour >= 0 && Math.abs(hourOne - currentHour) <= Math.abs(hourTwo - currentHour)) {
                                        return openingHours.getPeriods().get(i).getOpen().getTime().getMinutes();
                                    } else {

                                        return openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getMinutes();
                                    }
                                }
                                //Else I take only the one
                                else {
                                    return openingHours.getPeriods().get(i).getOpen().getTime().getMinutes();
                                }
                            } else if (currentHour > openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                if (openingHours.getPeriods().get(i).getClose() != null) {
                                    if (currentHour < openingHours.getPeriods().get(i).getClose().getTime().getHours()) {
                                        return openingHours.getPeriods().get(i).getOpen().getTime().getMinutes();
                                    }
                                }


                            }

                        }

                    }

                    int noResultFound = 0;
                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {
                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                            noResultFound = i;

                        }
                    }
                    if (noResultFound == 0) {
                        return -1;
                    }


                }
                //if getpriod.size is 0
                else {
                    return -1;
                }

            }

        }
        return -1;

    }

    @SuppressWarnings("ConstantConditions")
    public int closeHoursCalcul(OpeningHours openingHours, int day, int currentHour,String stringCurrentDay) {

        if (openingHours != null) {

            //if getpriod.size equal to 7
            if (openingHours.getPeriods().size() == 7) {

                if (openingHours.getPeriods().get(day).getOpen() != null) {
                    return openingHours.getPeriods().get(day).getClose().getTime().getHours();
                }
            } else {
                //if getpriod.size is different from 7
                if (openingHours.getPeriods().size() != 0) {

                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {

                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {

                            //Current hour is less than open hour
                            if (currentHour < openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                int myOccurrence = 0;
                                //We check if there is other same day in the list
                                for (int y = 0; y < openingHours.getPeriods().size(); y++) {
                                    if ((openingHours.getPeriods().get(y).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                                        if (y != i) {
                                            myOccurrence = y;
                                        }

                                    }
                                }
                                //if I have a second even day I check wich is near from my current hour
                                if (myOccurrence != 0) {
                                    int hourOne = openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    int hourTwo = openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getHours();
                                    if (hourOne - currentHour >= 0 && Math.abs(hourOne - currentHour) <= Math.abs(hourTwo - currentHour)) {
                                        return openingHours.getPeriods().get(i).getClose().getTime().getHours();
                                    } else {

                                        return openingHours.getPeriods().get(myOccurrence).getClose().getTime().getHours();
                                    }
                                }
                                //Else I take only the one
                                else {
                                    return openingHours.getPeriods().get(i).getClose().getTime().getHours();
                                }
                            } else if (currentHour > openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                if (openingHours.getPeriods().get(i).getClose() != null) {
                                    if (currentHour < openingHours.getPeriods().get(i).getClose().getTime().getHours()) {
                                        return openingHours.getPeriods().get(i).getClose().getTime().getHours();
                                    }
                                }


                            }

                        }

                    }

                    int noResultFound = 0;
                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {
                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                            noResultFound = i;

                        }
                    }
                    if (noResultFound == 0) {
                        return -1;
                    }


                }
                //if getpriod.size is 0
                else {
                    return -1;
                }

            }

        }
        return -1;

    }

    @SuppressWarnings("ConstantConditions")
    public int closeMinutesCalcul(OpeningHours openingHours,int day, int currentHour,String stringCurrentDay) {

        if (openingHours != null) {

            //if getpriod.size equal to 7
            if (openingHours.getPeriods().size() == 7) {

                if (openingHours.getPeriods().get(day).getOpen() != null) {
                    return openingHours.getPeriods().get(day).getClose().getTime().getMinutes();
                }
            } else {
                //if getpriod.size is different from 7
                if (openingHours.getPeriods().size() != 0) {

                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {

                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {

                            //Current hour is less than open hour
                            if (currentHour < openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                int myOccurrence = 0;
                                //We check if there is other same day in the list
                                for (int y = 0; y < openingHours.getPeriods().size(); y++) {
                                    if ((openingHours.getPeriods().get(y).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                                        if (y != i) {
                                            myOccurrence = y;
                                        }

                                    }
                                }
                                //if I have a second even day I check wich is near from my current hour
                                if (myOccurrence != 0) {
                                    int hourOne = openingHours.getPeriods().get(i).getOpen().getTime().getHours();
                                    int hourTwo = openingHours.getPeriods().get(myOccurrence).getOpen().getTime().getHours();
                                    if (hourOne - currentHour >= 0 && Math.abs(hourOne - currentHour) <= Math.abs(hourTwo - currentHour)) {
                                        return openingHours.getPeriods().get(i).getClose().getTime().getMinutes();
                                    } else {

                                        return openingHours.getPeriods().get(myOccurrence).getClose().getTime().getMinutes();
                                    }
                                }
                                //Else I take only the one
                                else {
                                    return openingHours.getPeriods().get(i).getClose().getTime().getMinutes();
                                }
                            } else if (currentHour > openingHours.getPeriods().get(i).getOpen().getTime().getHours()) {
                                if (openingHours.getPeriods().get(i).getClose() != null) {
                                    if (currentHour < openingHours.getPeriods().get(i).getClose().getTime().getHours()) {
                                        return openingHours.getPeriods().get(i).getClose().getTime().getMinutes();
                                    }
                                }


                            }

                        }

                    }

                    int noResultFound = 0;
                    for (int i = 0; i < openingHours.getPeriods().size(); i++) {
                        if ((openingHours.getPeriods().get(i).getOpen().getDay().toString()).equals(stringCurrentDay)) {
                            noResultFound = i;

                        }
                    }
                    if (noResultFound == 0) {
                        return -1;
                    }


                }
                //if getpriod.size is 0
                else {
                    return -1;
                }

            }

        }
        return -1;

    }

}
