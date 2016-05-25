package com.openclassrooms.fr.premierprojet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void total_isCorrect() throws Exception {
        List<Integer> list = new ArrayList<>();
        assertTrue(list.isEmpty());
        int value = 0;
        for (int i = 0 ; i < 10 ; i++){
            value += i;
            list.add(value);
        }

        for(int  i : list)
            System.out.println(i);

        assertEquals(value,45);
        assertFalse(list.isEmpty());


    }
}