package com.openclassrooms.fr.premierprojet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.net.MalformedURLException;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 * This activity is used to display the list of the different process running on the device
 *
 * @author jsie
 */
public class DeuxiemeActivite extends AppCompatActivity {

    private final StringBuilder sb = new StringBuilder();
    private final String path = "smb://ylalsrv01wlan0/jsie-home/";
    private final String filename = "android.txt";
    private final String userpwd = "jsie:qsec0fr";

    private int total = 0;

    private void exploreDirectory(SmbFile file) throws Exception {

        if (file.canRead()) {
            SmbFile[] files = file.listFiles();

            for (SmbFile f : files) {
                sb.append(f.getCanonicalPath() + "\n");
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

        final TextView textViewProcess = (TextView) findViewById(R.id.textViewProcess);
        assert textViewProcess != null;
        textViewProcess.setMovementMethod(new ScrollingMovementMethod());

        /*ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo process : pids)
            sb.append(process.pid).append(" -- ").append(process.processName).append("\n")*/

        //TODO: Use multi-threading to explore the folder and update the GUI

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(userpwd);
                SmbFile file = null;
                try {
                    file = new SmbFile(path, auth);
                    sb.append(file.getCanonicalPath() + "\n");
                    exploreDirectory(file);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewProcess.setText(sb.toString());

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

}
