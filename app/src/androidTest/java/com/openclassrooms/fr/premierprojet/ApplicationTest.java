package com.openclassrooms.fr.premierprojet;

import android.app.Application;
import android.content.ContentProviderClient;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {


    private final String TAG_EXPLORE_LOCAL_FILES = "EXPLORE_LOCAL_FILES";
    private final String TAG_REMOTE_COPY = "REMOTE_COPY";
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
    public void testRemoteWrites() throws Exception {
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
    public void testExploreRemoteFiles() throws Exception {
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(userpwd);
        SmbFile file = new SmbFile(path, auth);

        assertTrue(file.exists());
        assertTrue(file.isDirectory());
        assertFalse(file.isHidden());

        System.out.println(file.getCanonicalPath());

        exploreRemoteDirectory(file);
    }

    private void exploreRemoteDirectory(SmbFile file) throws Exception {
        assertTrue(file.isDirectory());

        if (file.canRead()) {
            SmbFile[] files = file.listFiles();

            for (SmbFile f : files) {
                System.out.println(f.getCanonicalPath());
                if (f.isDirectory() && !f.isHidden())
                    exploreRemoteDirectory(f);
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

    @Suppress
    public void testExploreLocalFiles() throws Exception {
        File rootFile = Environment.getExternalStorageDirectory();

        Log.i(TAG_EXPLORE_LOCAL_FILES, rootFile.getCanonicalPath());

        assertNotNull(rootFile);
        assertTrue(rootFile.exists());
        assertTrue(rootFile.isDirectory());
        assertTrue(rootFile.canRead());
        assertFalse(rootFile.isHidden());

        List<File> parents = getLocalRootFolderParents(rootFile);

        for (File p : parents)
            Log.i(TAG_EXPLORE_LOCAL_FILES, "Parent : " + p.getCanonicalPath());

        //exploreLocalDirectory(rootFile);
    }

    private void exploreLocalDirectory(File file) throws IOException {

        assertNotNull(file);
        assertTrue(file.exists());
        assertTrue(file.isDirectory());
        assertTrue(file.canRead());
        assertFalse(file.isHidden());

        for (File f : file.listFiles()) {
            Log.i(TAG_EXPLORE_LOCAL_FILES, f.getCanonicalPath());
            if (f.isDirectory() && !f.isHidden())
                exploreLocalDirectory(f);
        }

    }

    private List<File> getLocalRootFolderParents(File rootFile) {

        List<File> parents = new ArrayList<>();

        File parent = rootFile.getParentFile();

        while (parent != null) {
            parents.add(parent);
            parent = parent.getParentFile();
        }

        Collections.reverse(parents);

        return parents;
    }

    @Suppress
    public void testRemoteFolderCreation() throws Exception {
        String remoteFolderName = "backup_android/";

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(userpwd);
        SmbFile rootFolder = new SmbFile(path, auth);
        assertTrue(rootFolder.canRead());
        assertTrue(rootFolder.isDirectory());

        SmbFile remoteFolder = new SmbFile(path + remoteFolderName, auth);
        if (!remoteFolder.exists())
            remoteFolder.mkdir();

        assertTrue(remoteFolder.isDirectory());

        //TODO: check if there is enough storage capacity...
    }

    @LargeTest
    public void testRemoteCopy() throws Exception {

        String remoteFolderName = "backup_android";

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(userpwd);
        SmbFile sharedFolder = new SmbFile(path, auth);
        assertTrue(sharedFolder.canRead());
        assertTrue(sharedFolder.isDirectory());

        SmbFile backupFolder = new SmbFile(path + remoteFolderName, auth);
        if (!backupFolder.exists())
            backupFolder.mkdir();

        assertTrue(backupFolder.isDirectory());

        Log.i(TAG_REMOTE_COPY, "Backup folder opened/created : " + backupFolder.getCanonicalPath());

        File localRootFolder = Environment.getExternalStorageDirectory();

        assertTrue(localRootFolder.isDirectory());
        assertTrue(localRootFolder.canRead());

        Log.i(TAG_REMOTE_COPY, "Local folder opened : " + localRootFolder.getCanonicalPath());

        //TODO: check if there is enough storage capacity...

        SmbFile target = initBackupFolder(localRootFolder, backupFolder.getCanonicalPath(), auth);
        if (!target.exists())
            target.mkdir();

        assertTrue(target.canWrite());

        //TODO: copy the local files/dirs to the remove target
    }


    private boolean copy(File src, String canonicalPath, NtlmPasswordAuthentication auth) throws IOException {
        Log.i(TAG_REMOTE_COPY, "Copy " + src.getCanonicalPath() + " ==> " + canonicalPath);
        if (!src.canRead()) {
            return false;
        }

        if (src.isDirectory()) {
            SmbFile dir = new SmbFile(canonicalPath + src.getCanonicalPath(), auth);
            if (!dir.exists())
                dir.mkdir();
        }

        return true;
    }

    private SmbFile initBackupFolder(File localRootFolder, String backupFolderCanonicalPath, NtlmPasswordAuthentication auth) throws IOException {
        List<File> parents = getLocalRootFolderParents(localRootFolder);

        int before = parents.size();

        assertNotNull(parents);
        assertFalse(parents.isEmpty());

        for (File root : File.listRoots())
            parents.remove(root);

        int after = parents.size();

        assertFalse(parents.isEmpty());
        assertTrue(before > after);

        for (File p : parents) {
            Log.i(TAG_REMOTE_COPY, "folder to create during init " + p.getCanonicalPath());
            SmbFile dir = new SmbFile(backupFolderCanonicalPath + p.getCanonicalPath(), auth);
            if (!dir.exists())
                dir.mkdir();
        }

        return new SmbFile(backupFolderCanonicalPath + localRootFolder.getCanonicalPath(), auth);

    }
}
