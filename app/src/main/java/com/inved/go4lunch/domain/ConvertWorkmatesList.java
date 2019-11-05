package com.inved.go4lunch.domain;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ConvertWorkmatesList {

    public String convertListToString(@NotNull ArrayList<String> workmates){

        String workmatesList = "";

        StringBuilder sb = new StringBuilder();

        for (String s : workmates)
        {
            sb.append(workmatesList);
            sb.append(s);
            sb.append("\n");
        }

        return sb.toString();
    }
}
