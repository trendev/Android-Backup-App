package com.openclassrooms.fr.premierprojet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

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

    private final String path = "smb://ylalsrv01wlan0/jsie-home/";
    private final String userpwd = "jsie:qsec0fr";
    private final List<SmbFile> smbFileList = new LinkedList<>();
    private int total = 0;

    private void exploreDirectory(SmbFile file) throws Exception {

        if (file.canRead()) {
            SmbFile[] files = file.listFiles();

            for (SmbFile f : files) {
                total++;
                if (f.isDirectory() && !f.isHidden())
                    exploreDirectory(f);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deuxieme_activite);

        final ListView listView = (ListView) findViewById(R.id.listView);

        final SmbFileAdapter adapter = new SmbFileAdapter(this, smbFileList);
        listView.setAdapter(adapter);
        //TODO: insert the exploration result in a ListView and display a progress bar
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(userpwd);
                try {
                    final SmbFile file = new SmbFile(path, auth);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.add(file);
                        }
                    });
                    exploreDirectory(file);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //GUI interactions
                            Intent result = new Intent();

                            result.putExtra(PremiereActivite.TOTAL_FILES, Integer.toString(total));
                            setResult(RESULT_OK, result);
                            Toast.makeText(DeuxiemeActivite.this, Integer.valueOf(total).toString() + " " + getResources().getString(R.string.files), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        t.start();

    }

}
