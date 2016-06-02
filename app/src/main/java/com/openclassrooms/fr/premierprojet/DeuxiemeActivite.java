package com.openclassrooms.fr.premierprojet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import jcifs.smb.SmbFile;

/**
 * This activity is used to display the list of the different process running on the device
 *
 * @author jsie
 */
public class DeuxiemeActivite extends AppCompatActivity {

    private static final Comparator<SmbFile> comparator = new SmbFileAdapter.SmbFileComparator();
    private static final Intent result = new Intent();
    private static SmbFile rootFile = null;
    private static SmbFile currentSmbFile = null;
    private static ArrayAdapter<SmbFile> adapter;
    private static int depth = 0;
    private static int localTotal = 0;
    private static int totalexp = 0;
    private static ProgressBar progressBar = null;

    /**
     * Will explore the content of a folder.
     * Should be executed in a Thread in order to improve the performances.
     *
     * @param file the folder to explore
     * @throws Exception
     */
    private void exploreRemoteDirectory(final SmbFile file) throws Exception {


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
                    progressBar.setVisibility(View.VISIBLE);
                    setTitle(file.getName());
                }
            });

            SmbFile[] files = file.listFiles();

            List<SmbFile> list = new ArrayList<>(files.length);

            Collections.addAll(list, files);
            Collections.sort(list, comparator);

            /**
             * localTotal must be reset during each exploration,
             * otherwise if a folder is empty a wrong value will be displayed
             */
            localTotal = 0;
            for (SmbFile smbFile : list) {
                localTotal++;
                totalexp++;
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
                    Toast.makeText(DeuxiemeActivite.this, Integer.valueOf(localTotal).toString() + " " + getResources().getString(R.string.files), Toast.LENGTH_SHORT).show();
                    result.putExtra(PremiereActivite.EXTRA_TOTAL_FILES, Integer.toString(totalexp));
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deuxieme_activite);

        if (progressBar == null)
            progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        final ListView listView = (ListView) findViewById(R.id.listView);
        final List<SmbFile> smbFileList = new LinkedList<>();

        adapter = new SmbFileAdapter(this, smbFileList);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final SmbFile smbFile = adapter.getItem(i);
                try {
                    if (smbFile.isDirectory() && !smbFile.isHidden()) {
                        adapter.clear();
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    exploreRemoteDirectory(smbFile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        t.setDaemon(true);
                        t.start();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    rootFile = new SmbFile(PremiereActivite.path, PremiereActivite.auth);
                    /**
                     * If the remote folder has already been explored,
                     * we restart from the latest explored folder.
                     */
                    exploreRemoteDirectory((currentSmbFile == null) ? rootFile : currentSmbFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();

        setResult(RESULT_OK, result);

    }

    /**
     * If the depth is greater than 0, will start to explore the parent folder
     *
     * @param keyCode the key code of the received event
     * @param event   the received event
     * @return true if no exit is performed
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /**
             * Should be 0 or less because depth counter won't be increment if root is the current folder
             */
            if (depth > 0) {
                /**
                 * Exploration will increment the depth counter, so it's necessary to go back higher
                 * in get the right position!
                 */
                depth -= 2;
                adapter.clear();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            exploreRemoteDirectory(new SmbFile(currentSmbFile.getParent(), PremiereActivite.auth));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.setDaemon(true);
                t.start();

                //will exit from the activity only if depth <= 0
                return true;
            }

        }
        /**
         * Exit from the activity, reset the current position to the root folder
         */
        resetCountersAndPositions();
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Reset the depth and total of explored files to zero and position the current pointer to null.
     */
    private void resetCountersAndPositions() {
        currentSmbFile = null;
        depth = 0;
        totalexp = 0;
    }



}