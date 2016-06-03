package com.openclassrooms.fr.premierprojet;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.openclassrooms.fr.premierprojet.beans.ActivationProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.yla.misc.TimeConverter;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class BackupService extends IntentService {

    //TODO: comment the ActivationProperty usage...
    static final ActivationProperty activationProperty = new ActivationProperty();
    private final static String TAG = "BACKUP_SERVICE";

    public BackupService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String remoteFolderName = "backup_android";

        if (!activationProperty.isActivated()) {
            activationProperty.setActivated(true);
            try {
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(PremiereActivite.userpwd);

                SmbFile backupFolder = new SmbFile(PremiereActivite.path + remoteFolderName, auth);
                if (!backupFolder.exists())
                    backupFolder.mkdir();


                Log.i(TAG, "Backup folder opened/created : " + backupFolder.getCanonicalPath());

                File localRootFolder = Environment.getExternalStorageDirectory();

                Log.i(TAG, "Local folder opened : " + localRootFolder.getCanonicalPath());

                //TODO: check if there is enough storage capacity before doing anything...

                initBackupFolder(localRootFolder, backupFolder.getCanonicalPath(), auth);

                Log.i(TAG, "### START REMOTE COPY ###");
                long start = System.currentTimeMillis();
                remoteCopy(localRootFolder, backupFolder.getCanonicalPath(), auth);
                long laptime = System.currentTimeMillis() - start;
                Log.i(TAG, "### REMOTE COPY IS OVER ###");
                Log.i(TAG, "### Finished in : " + TimeConverter.convertMilliSecondsToString(laptime) + " ###");

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                activationProperty.setActivated(false);
            }
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

    private void remoteCopy(File src, String backupFolderCanonicalPath, NtlmPasswordAuthentication auth) {
        try {
            if (src.canRead()) {
                SmbFile file = new SmbFile(backupFolderCanonicalPath + src.getCanonicalPath(), auth);

                if (src.isDirectory()) {
                    if (!file.exists())
                        file.mkdir();
                    for (File f : src.listFiles())
                        remoteCopy(f, backupFolderCanonicalPath, auth);
                }

                if (src.isFile()) {
                    if (!file.exists()) {
                        file.createNewFile();
                        copy(src, file);
                    } else if (src.lastModified() > file.lastModified())
                        copy(src, file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            Log.e(TAG, "Copy " + (src.isDirectory() ? "DIR" : "FILE") + " " + src.getName() + " : [ FAILED ] ");
        }


    }

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

    private void initBackupFolder(File localRootFolder, String backupFolderCanonicalPath, NtlmPasswordAuthentication auth) throws IOException {
        List<File> parents = getLocalRootFolderParents(localRootFolder);

        int before = parents.size();


        for (File root : File.listRoots())
            parents.remove(root);

        int after = parents.size();


        for (File p : parents) {
            Log.i(TAG, "folder to create during init " + p.getCanonicalPath());
            SmbFile dir = new SmbFile(backupFolderCanonicalPath + p.getCanonicalPath(), auth);
            if (!dir.exists())
                dir.mkdir();
        }
    }
}
