package com.openclassrooms.fr.premierprojet;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {


    private final String path = "smb://ylalsrv01/jsie-home/";
    private final String filename = "android.txt";
    private final String userpwd = "jsie:qsec0fr";

    public ApplicationTest() {
        super(Application.class);
    }

    @LargeTest
    public void testSambaConnexion() throws Exception {

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(userpwd);
        SmbFile file = new SmbFile(path, auth);
        assertTrue(file.canRead());
        assertTrue(file.isDirectory());
        assertNotNull(getContext());
    }

    //TODO: test the remote copy on samba-cifs server
    @LargeTest
    public void testRemoteCopy() throws Exception {
        //TODO: get a set of local file and copy them to the remove server
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(userpwd);
        SmbFile file = new SmbFile(path + filename, auth);
        if (!file.exists())
            file.createNewFile();

        assertTrue(file.canRead());
        assertTrue(file.isFile());
        assertFalse(file.isDirectory());
        assertTrue(file.canWrite());

        SmbFileOutputStream sfos = new SmbFileOutputStream(file);
        assertTrue(sfos.isOpen());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - kk:mm:ss");

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> e : System.getenv().entrySet())
            sb.append("[" + e.getKey() + " = " + e.getValue() + "]\n");

        for (Map.Entry<Thread, StackTraceElement[]> e : Thread.getAllStackTraces().entrySet())
            sb.append("{" + e.getKey() + " = " + e.getValue() + "}\n");

        String message = sb.toString() + "\n" + sdf.format(calendar.getTime()) + "\n";
        sfos.write(message.getBytes());
        sfos.close();

    }

    @LargeTest
    public void testExploreFiles() throws Exception {
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(userpwd);
        SmbFile file = new SmbFile(path, auth);

        assertTrue(file.exists());
        assertTrue(file.isDirectory());
        assertFalse(file.isHidden());

        System.out.println(file.getCanonicalPath());

        exploreDirectory(file);
    }

    private void exploreDirectory(SmbFile file) throws Exception {
        assertTrue(file.isDirectory());

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
