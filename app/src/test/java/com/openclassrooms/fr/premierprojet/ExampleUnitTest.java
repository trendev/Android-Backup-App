package com.openclassrooms.fr.premierprojet;

import org.junit.Test;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    private final String path = "smb://ylalsrv01wlan0/jsie-home/";
    private final String filename = "android.txt";
    private final String userpwd = "jsie:qsec0fr";

    @Test
    public void testSamba() throws Exception {
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(userpwd);
        SmbFile file = new SmbFile(path, auth);

        assert file.exists() == true;
        assert file.isDirectory() == true;
        assert file.isHidden() == false;

        System.out.println(file.getCanonicalPath());

        exploreDirectory(file);
    }

    private void exploreDirectory(SmbFile file) throws Exception {
        assert file.isDirectory() == true;

        if (file.canRead()) {
            SmbFile[] files = file.listFiles();

            for (SmbFile f : files) {
                System.out.println(f.getCanonicalPath());
                if (f.isDirectory() && !f.isHidden())
                    exploreDirectory(f);
            }
        }
    }
}