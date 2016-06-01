package com.openclassrooms.fr.premierprojet;

import android.app.Application;
import android.content.ContentProviderClient;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

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

    @Suppress
    public void testSambaConnexion() throws Exception {

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(userpwd);
        SmbFile file = new SmbFile(path, auth);
        assertTrue(file.canRead());
        assertTrue(file.isDirectory());
        assertNotNull(getContext());
    }

    @Suppress
    //TODO: test the remote copy on samba-cifs server
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

    @Suppress
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

    @Suppress
    public void testFetchContacts() throws Exception {
        /**
         *  This array Will be used in order to map the required entries from the Contacts.
         */
        String[] PROJECTION = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        /**
         * This structure will be used to store the different entries got from the queries.
         */
        Map<String, String> rep = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });

        String TAG = "FETCH_CONTACTS";

        Log.i(TAG, "FETCHING...");

        ContentProviderClient client = getContext().getContentResolver().acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);

        //        Cursor cursor = client.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        Cursor cursor = client.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                rep.put(id, name);

                Log.i(TAG, "Position (" + cursor.getPosition() + ") , ID = " + id + " , NAME = " + name);
            }
        }

        Log.i(TAG, "#Contacts = " + cursor.getCount() + " vs " + rep.size() + " in map");

        cursor.close();
    }


}
