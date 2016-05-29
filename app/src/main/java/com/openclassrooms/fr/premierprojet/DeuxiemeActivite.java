package com.openclassrooms.fr.premierprojet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 * This activity is used to display the list of the different process running on the device
 *
 * @author jsie
 */
public class DeuxiemeActivite extends AppCompatActivity {

    //TODO : define these fields as Application Preferences
    private static final String path = "smb://ylalsrv01wlan0/jsie-home/";
    private static final String userpwd = "jsie:qsec0fr";
    private static final Comparator<SmbFile> comparator = new SmbFileAdapter.SmbFileComparator();
    private static NtlmPasswordAuthentication auth;
    private static SmbFile rootFile = null;
    private static SmbFile currentSmbFile = null;
    private static ArrayAdapter<SmbFile> adapter;
    private static int depth = 0;
    private int total = 0;

    private void exploreDirectory(final SmbFile file) throws Exception {

        if (file.canRead()) {

            /**
             * Position the current SmbFile and increment the depth counter if the position is in the root.
             */
            currentSmbFile = file;
            if (!currentSmbFile.equals(rootFile))
                depth++;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setTitle(file.getName());
                }
            });

            SmbFile[] files = file.listFiles();

            List<SmbFile> list = new ArrayList<>(files.length);

            Collections.addAll(list, files);
            Collections.sort(list, comparator);

            /**
             * total must be reset during each exploration,
             * otherwise if a folder is empty a wrong value will be displayed
             */
            total = 0;
            for (SmbFile smbFile : list) {
                total++;
                final SmbFile f = smbFile;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.add(f);
                    }
                });
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DeuxiemeActivite.this, Integer.valueOf(total).toString() + " " + getResources().getString(R.string.files), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deuxieme_activite);

        final ListView listView = (ListView) findViewById(R.id.listView);
        final List<SmbFile> smbFileList = new LinkedList<>();

        adapter = new SmbFileAdapter(this, smbFileList);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SmbFile smbFile = adapter.getItem(i);
                try {
                    if (smbFile.isDirectory() && !smbFile.isHidden()) {
                        adapter.clear();
                        exploreDirectory(smbFile);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //TODO: insert the exploration result in a ListView and display a progress bar
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                auth = new NtlmPasswordAuthentication(userpwd);
                try {
                    rootFile = new SmbFile(path, auth);
                    /**
                     * If the remote folder has already been explored,
                     * we restart from the latest explored folder.
                     */
                    exploreDirectory((currentSmbFile == null) ? rootFile : currentSmbFile);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //GUI interactions
                            //TODO : find something better to send to the main activity...
                            Intent result = new Intent();

                            result.putExtra(PremiereActivite.TOTAL_FILES, Integer.toString(total));
                            setResult(RESULT_OK, result);
                        }
                    });
                }
            }
        });

        t.start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.out.println("KEYCODE_BACK detected");
            System.out.println(currentSmbFile.getCanonicalPath() + "  --- DEPTH = " + depth);
            /**
             * Should be 0 or less because depth counter won't be increment if root is the current folder
             */
            if (depth > 0) {
                /**
                 * Exploration will increment the depth counter, so it's necessary to go back higher
                 * in get the right position!
                 */
                depth -= 2;
                try {
                    adapter.clear();
                    exploreDirectory(new SmbFile(currentSmbFile.getParent(), auth));
                } catch (Exception e) {
                    finish();
                }
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
