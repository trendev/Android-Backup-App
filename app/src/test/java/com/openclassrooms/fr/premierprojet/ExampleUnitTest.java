package com.openclassrooms.fr.premierprojet;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {


    @Test
    public void testSizeFormat() throws Exception {
        long size = 1299973064;
        String s = SmbFileAdapter.formatSize(size);
        System.out.println(s);
    }
}