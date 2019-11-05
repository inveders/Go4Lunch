package com.inved.go4lunch;

import com.inved.go4lunch.domain.ConvertWorkmatesList;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Objects;

public class ConvertWorkmatesListTest {

    private ConvertWorkmatesList convertWorkmatesList = Mockito.spy(new ConvertWorkmatesList());

    @Test
    public void should_Retrieve_List_Workmates_InGoodFormat() {

        //Given
        ArrayList<String> workmates = new ArrayList<>();
        workmates.add("Julian");
        workmates.add("Raphaël");
        workmates.add("Ephraim");

        //When
        String sb = convertWorkmatesList.convertListToString(workmates);


        String expected = "Julian\nRaphaël\nEphraim\n".replaceAll("\n", Objects.requireNonNull(System.getProperty("line.separator")));

        boolean bool = equalsIgnoreNewlineStyle(sb,expected);

        //Then
        Assert.assertTrue(bool);

    }

    private boolean equalsIgnoreNewlineStyle(String s1, String s2) {
        return s1 != null && s2 != null && normalizeLineEnds(s1).equals(normalizeLineEnds(s2));
    }

    private String normalizeLineEnds(String s) {
        return s.replace("\r\n", "\n").replace('\r', '\n');
    }

}
