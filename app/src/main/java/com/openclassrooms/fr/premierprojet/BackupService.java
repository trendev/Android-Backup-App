package com.openclassrooms.fr.premierprojet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.openclassrooms.fr.premierprojet.beans.BooleanProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.yla.misc.TimeConverter;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class BackupService extends IntentService {

    //The property which link the Backup button status and the status of this service
    static final BooleanProperty activationProperty = new BooleanProperty();
    private final static String TAG = "BACKUP_SERVICE";

    public BackupService() {
        super(TAG);
    }

    /**
     * <pre>
     * Will handle the backup actions.
     * Button is disabled when the service is running, so shouldn't handle more than one intent...
     * </pre>
     *
     * @param intent the Intent provided by the main activity. Doesn't contain a relevant Extra/Data.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String remoteFolderName = "backup_android_" + tm.getDeviceId();

        if (!activationProperty.isActivated()) {
            activationProperty.setActivated(true);
            try {
                //open the remote folder on samba/cifs server
                SmbFile backupFolder = new SmbFile(PremiereActivite.path + remoteFolderName, PremiereActivite.auth);
                if (!backupFolder.exists())
                    backupFolder.mkdir();


                Log.i(TAG, "Backup folder opened/created : " + backupFolder.getCanonicalPath());

                //get the root folder (usually from the sdcard device)
                File localRootFolder = Environment.getExternalStorageDirectory();
                if (localRootFolder == null)
                    throw new UnsupportedOperationException(TAG + ": no external storage (sdcard) to backup !");

                Log.i(TAG, "Local folder opened : " + localRootFolder.getCanonicalPath());

                //Check if there is enough storage capacity before doing anything...
                long localUsedSpace = localRootFolder.getTotalSpace() - localRootFolder.getFreeSpace();
                long remoteFreeSpace = backupFolder.getDiskFreeSpace();

                //Usually, the storage experts recommend to keep 20% of available space on a FileSystem...
                //Here, we will consider that this expectation is managed by the administrator of the samba/cifs server .
                if (remoteFreeSpace - localUsedSpace > 0) {

                    initBackupFolder(localRootFolder, backupFolder.getCanonicalPath());

                    Log.i(TAG, "### START REMOTE COPY ###");
                    long start = System.currentTimeMillis();
                    remoteCopy(localRootFolder, backupFolder.getCanonicalPath());
                    long laptime = System.currentTimeMillis() - start;
                    Log.i(TAG, "### REMOTE COPY IS OVER ###");
                    Log.i(TAG, "### Finished in : " + TimeConverter.convertMilliSecondsToString(laptime) + " ###");
                } else {
                    Log.e(TAG, "--- No available space on the remote server ---");
                    Log.e(TAG, localUsedSpace + " Bytes are required and there is only " + remoteFreeSpace + " Bytes available !");
                    Log.e(TAG, "Backup aborted !");
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                activationProperty.setActivated(false);
            }
        }
    }

    /**
     * <pre>
     *     Will list the parent folders (up to the roots) of the remote backup folder.
     *     For exemple, the sdcard is located in /mnt/sdcard. Should be good to create mnt on
     *     the remote server before creating the backup folder
     *     and copy the content of the local media.
     * </pre>
     *
     * @param rootFile the remote backup folder
     * @return the list of the parent folder to create for initializing the remote backup folder
     * @see #initBackupFolder(File, String)
     */
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

    /**
     * <pre>
     *     Explore the local folder content and initiate its remote copy.
     *     It's not an incremental copy (yet) but if a file already exists and the modified time is
     *     more recent than the target, a new copy is performed.
     *     Otherwise, it shifts to the next file/dir.
     * </pre>
     *
     * @param src                       the source file/dir to copy on the remote server
     * @param backupFolderCanonicalPath the path of the remote backup folder (after init of course).
     */
    private void remoteCopy(File src, String backupFolderCanonicalPath) {
        try {
            if (src.canRead()) {
                SmbFile file = new SmbFile(backupFolderCanonicalPath + src.getCanonicalPath(), PremiereActivite.auth);

                if (src.isDirectory()) {
                    if (!file.exists())
                        file.mkdir();
                    for (File f : src.listFiles())
                        remoteCopy(f, backupFolderCanonicalPath);//explore the directory
                }

                if (src.isFile()) {
                    if (!file.exists()) {
                        file.createNewFile();
                        copy(src, file);//first or new copy
                    } else if (src.lastModified() > file.lastModified()) //resynchronization
                        copy(src, file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            Log.e(TAG, "Copy " + (src.isDirectory() ? "DIR" : "FILE") + " " + src.getName() + " : [ FAILED ] ");
        }


    }

    /**
     * <pre>
     *     Performs the remote copy from a local source to a remote target.
     *     Buffer size is hardcoded and not automatically set.
     *     32768 Bytes (32KiB) is the most performant value.
     * </pre>
     *
     * @param src The source (a local file)
     * @param trg The target (a remote file)
     * @throws IOException sent if a major error occurs
     */
    private void copy(File src, SmbFile trg) throws IOException {
        Log.i(TAG, "Copy " + (src.isDirectory() ? "DIR" : "FILE") + " " + src.getCanonicalPath() + " ==> " + trg.getCanonicalPath());

        FileInputStream in = null;
        OutputStream out = null;

        try {
            if (src.canRead() && trg.canWrite()) {
                //Block size in Bytes:
                //4096, 8192, 16384, 32768, 65536
                byte[] buffer = new byte[32768];
                int size, total = 0;

                in = new FileInputStream(src);
                out = trg.getOutputStream();

                while ((size = in.read(buffer)) != -1) {
                    out.write(buffer, 0, size);
                    total += size;
                }
                Log.i(TAG, SmbFileAdapter.formatSize(total) + " copied");
                Log.i(TAG, "Copy " + (src.isDirectory() ? "DIR" : "FILE") + " " + src.getCanonicalPath() + " : [ OK ] ");
            }
        } catch (SmbException | FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }

    /**
     * Will create the required local root folders on the remote server.
     *
     * @param localRootFolder           the local root folder (sdcard)
     * @param backupFolderCanonicalPath the path to the remote backup folder (samba/cifs share).
     * @throws IOException sent if a major error occurs
     */
    private void initBackupFolder(File localRootFolder, String backupFolderCanonicalPath) throws IOException {
        List<File> parents = getLocalRootFolderParents(localRootFolder);

        int before = parents.size();


        for (File root : File.listRoots())
            parents.remove(root);

        int after = parents.size();


        for (File p : parents) {
            Log.i(TAG, "folder to create during init " + p.getCanonicalPath());
            SmbFile dir = new SmbFile(backupFolderCanonicalPath + p.getCanonicalPath(), PremiereActivite.auth);
            if (!dir.exists())
                dir.mkdir();
        }
    }
}
