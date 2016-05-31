package com.openclassrooms.fr.premierprojet;

import org.junit.Test;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    private final String path = "smb://ylalsrv01/jsie-home/";
    private final String filename = "android.txt";
    private final String userpwd = "jsie:qsec0fr";

    @Test
    public void testSizeFormat() throws Exception {
        long size = 1299973064;
        String s = SmbFileAdapter.formatSize(size);
        System.out.println(s);
    }

    @Test
    public void testSambaConnexion() throws Exception {

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(userpwd);
        SmbFile file = new SmbFile(path, auth);
        assert file.canRead();
        assert file.isDirectory();
        int attributs = file.getAttributes();

        assert (attributs & SmbFile.ATTR_READONLY) == 0;
        System.out.println("Attributes = " + attributs);
    }

    @Test
    public void testLocalFileExplorer() {

    }
}